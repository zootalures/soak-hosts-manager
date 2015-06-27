#!/usr/bin/perl
#use lib "../";
use ReeNamed::HostsParser;
use URI::file;
use IO::File;
use strict;
use Net::IP;
use Getopt::Long;
use Socket;
use Net::DNS::Resolver;
use XML::Writer;
my $defaultOU = "xx";

my $parser    = new ReeNamed::HostsParser('bath.ac.uk');
my $filename;
my $count;
my $stream;
my $classifyFile ;
my $dhcpFile;
my $outputFile = "hosts.xml";
my $output = new IO::File();
my %extraClassifiers;
my %dhcpReservations;
my %rDhcpReservations;
my @allhosts;
my $ouFile;
my %usedIps;
my %usedHostNames;
my %usedMacs;

sub canonicalizeMac{
    my $mac  = shift;
    $mac = s/\://g;

    $mac = uc($mac);
    my @parts;
    while ( $mac =~ s/(..)// ) {
	push @parts, $1;
    }
    $mac = join( ":", @parts );
  


    
    return $mac;
}

sub loadExtraClassifiers{
    my $filename = shift;
    open CFILE, "$filename";
    while(my $line = <CFILE>){
	chomp $line;
	my ($host,$class) = split(/\s+/,$line);
	$extraClassifiers{$host}  = $class;
    }
}
my %orgUnits;

sub loadOrgUnits{
    my $orgUnitFile = shift;
    open OUFILE,"<$orgUnitFile";
    while(my $line = <OUFILE>){
	chomp($line);
	$line =~/^(\w+)\s+(.*)$/;
	my $id = $1;
	my $name = $2;
	$orgUnits{$1} = $2;
	
	print STDERR "Read OU: $id:$name\n";
    }
    close OUFILE;
}
sub loadDHCPFile{
    my $filename = shift;
    open CFILE, "<$filename";
    my $count = 0;
    while(my $line = <CFILE>){
	chomp $line;
	my ($ip,$mac) = split /\s+/,$line;
	
	my $cmac = $mac;
	#print STDERR "GOT $ip : $cmac\n";
	$dhcpReservations{$ip} = $cmac;
	$rDhcpReservations{$cmac} = $ip;
	$count++;
    }
    close CFILE;
    print STDERR "Loaded $count entries from $filename\n";
    
}


my @hclassifymatches = (
    [ qr/^..pc\-.*\.campus\.bath\.ac\.uk/, { type => 'ADPC' } ],
    [ qr/^..pc\-.*\.bath\.ac\.uk/, { type => 'PC' } ],
    [ qr/^..mc\-/, { type => 'PC' } ],
    [ qr/^..mac\-/, { type =>  'PC' } ],
    [ qr/^..lp\-/, { type =>  'PC' } ],
    [ qr/^..sv\.campus\-/, { type => 'ADSERVER' } ],
    [ qr/^..vs\.campus\-/, { type => 'ADSERVER' } ],
    [ qr/^..sv\-/, { type => 'SERVER' } ],
    [ qr/^..pr\-/, { type => 'PRINTER' } ],

    [
     qr/^sg-/,
     {
	 type  => 'SWITCH'
     }
    ],
    [
     qr/^sg(ro|rc)\-/,
     {
	 type  => 'SWITCH'
	     
     }
    ],
    [
     qr/^sw(ro|rc)\-/,
     {
	 type  => 'SWITCH'
	     
     }
    ],
    [ qr/^\w+-rsc\./, { type => 'SERVICEPROCESSOR' } ],
    [ qr/^\w+-sp\./, { type => 'SERVICEPROCESSOR' } ],
    [ qr/^..rc\-/, { type => 'SERVICEPROCESSOR' } ],

    [
     qr/^swd\-/,
     {
	 type  => 'SWITCH'
     }
    ],
    [ qr/^sw\-/, { type => 'SWITCH' } ],
    [ qr/^sp\-/, { type => 'SWITCH'} ],
    [ qr/^ap\-/, { type => 'WIRELESSAP' } ],
    [ qr/^gw\-/, { type => 'GATEWAY' } ],
    [ qr/.*/,    { type => 'MISC' } ]
    );


sub name2HostName{
    my $name = shift;
    
    $name =~/^([^\.]+)(\..*)$/;
    
    return {name=>$1,ndSuffix=>$2};
    
}

sub getMappedHostAlias{
    my ($entry,$hostname, $alias ) = @_;
    
    my $res  = Net::DNS::Resolver->new;

    print STDERR "Looking alias type of $alias in entry \"",$entry->{address},"\" \n";
    my $query = $res->search($alias);
    if(defined($query)){
	foreach my $rr ($query->answer) {

	    
	    print STDERR "examining result ",$rr->string,"\n"; 
	    if($rr->type eq 'A' &&  $rr->name."." eq  $alias &&  $rr->address eq $entry->{address} ){
		
		
		print STDERR "Got A-name like alias for $alias\n";
		return {alias=>name2HostName($alias),type=>'AREC'};
		
	    }if($rr->type eq 'CNAME' &&$rr->name."." eq $alias && $rr->cname."." eq $hostname){
		print STDERR "Got CNAME-name like alias for $alias\n";
		return {alias=>name2HostName($alias),type=>'CNAME'};
		
		
	    }

	}
    }
    print STDERR "Presuming that this is a CNAME-like alias \n";
    
    return {alias=>name2HostName($alias),type=>'CNAME'};
    
}

my @namepresuffixes = ("pc-","sv-","mc-","mac-","pr-");

sub setHostOwnership{
    my $host = shift; 
    my $hostname = $host->{hostnameTxt};
    my $hostOU = $defaultOU;
  OU:foreach my $ou (keys(%orgUnits))
  {
      foreach my $nsfx (@namepresuffixes){
	  my $prefix = $ou.$nsfx;
#	  print "checking $hostname against $prefix\n";
	  if($hostname =~ /^$prefix/){
	      $hostOU = $ou;
	      last OU;
	  }
      }
  }
    
    print STDERR "$hostname: $hostOU\n";

    
    $host->{orgUnit} = $hostOU;
    
}
sub classifyHost {
    my $host     = shift;
    my $hostname = $host->{hostnameTxt};
    my @hostclasses;
    
    #print "classifying $hostname \n";
    
    #print "checking ",$host->hostname(),"\n"; 	 
    if (my $expClass = $extraClassifiers{$hostname}){    
	$host->{hostClass} =$expClass;
	return;   
    }
    
    
    foreach my $match (@hclassifymatches) {
	
	#   print "trying  ",$match->[0],"\n";
	if ( $hostname =~ $match->[0] ) {
	    push @hostclasses, $match->[1]->{type};
	    
	    print $hostname, ": ", $match->[1]->{type}, ", \n";
	    last;
	}
    }
    
    
    if ( @hostclasses == 1 ) {
	#
#		print STDERR "classifying ", $host->hostname, " as ", $hostclasses[0],  "\n";
	$host->{hostClass} =  $hostclasses[0] ;
    }else{
	$host->{hostClass} = "MISC";
    }
    

}

sub handler {
    my ( $entry, $lineno ) = @_;
    my $nhosts = @{ $entry->{hosts} };
    my $hostname;
    
    my @aliases;
    
    #hand to skip localhost :)
    if ( $entry->{address} =~ /^127/ ) {
	return;
    }
    
    $count++;

    
    my @hostnames =  @{$entry->{hosts}};
    my $nhostnames = @hostnames;
    if(0==$nhostnames){
	print STDERR "Skipping ",$entry->{address}, " no hostname\n";
	return ;
    }
    
    my $ip = $entry->{address};
    
#### Try and figure out host's real name and aliases (and which are which)
# in the case that a PTR record exists for the host with this IP, the target of that address
# is treated as the Canonical domain name and others are the  aliases.
    if ( @{ $entry->{hosts} } > 1 ) {
	my %namehash;
	
	print STDERR "looking for hostname of $ip with names: ",join(",", @{ $entry->{hosts} }),"\n";
	my $revhostname = gethostbyaddr(inet_aton($ip),AF_INET);
	$revhostname.=".";
	
	
	if(defined($revhostname)){

	    foreach my $hn (@{$entry->{hosts}}){
		if($hn eq $revhostname){
		    $hostname = $hn;
		    @aliases = ();
		    foreach my $alias (@{$entry->{hosts}}){
			if($alias ne $hn){
			    push @aliases,$alias;
			}
		    }

		    print STDERR "found hostname  using PTR record: $hn alaiases are ",join(",", @aliases), "\n";
		    
		}
	    }
	}		
	
    }
    
    # if we couldn't figure out stuff from DNS Just pick the first one
    if ( !$hostname ) {
	if ( @{ $entry->{hosts} } > 1 ) {
	    print STDERR "Couldn't choose a canonical DNS-based host name for ",
	    $ip, " guessing first address:",
	    $entry->{hosts}->[0], "\n";
	}
	
	$hostname = shift @{ $entry->{hosts} };
	@aliases  = @{ $entry->{hosts} };
    }
    
    my $macAddress;
    #FIXME: use DHCP  for AD hosts where possible here

    if(defined($dhcpReservations{$ip})){
	my $gotMac = $dhcpReservations{$ip};
	my $entryMac = canonicalizeMac($entry->{macAddress});
	if(defined($entryMac) && $entryMac ne $gotMac){
	    print STDERR "Using reserved MAC  $gotMac address for host rather than specified  $entryMac\n";
	}
	$macAddress = $gotMac;
    }elsif($entry->{macAddress}){
	my $rrev = $rDhcpReservations{$entry->{macAddress}};
	if(!defined($rrev) || $rrev eq $ip){
	    $macAddress = $entry->{macAddress};
	}else{
	    print STDERR "Skipping existing MAC for $ip as it seems to be used elsewhere \n";
	}
    }
    
    
    ## Create the host object
    
    my $partname =name2HostName($hostname);

    
    my $host = {
	description => $entry->{comment},
	hostname    => $partname,
	hostnameTxt    => $hostname,
	ipAddress   => $entry->{address},
	macAddress =>$macAddress,
	location=>$entry->{room}
	
    };
    
    
    if($usedIps{$entry->{address}}){
	die("duplicate IP for $hostname :".$entry->{address});
	
	
    }
    $usedIps{$entry->{address}} = $host;
    
    if($usedHostNames{$hostname}){
	die("duplicate Host Name  :".$hostname);    
    }
    $usedHostNames{$hostname}  = $host;

    if(defined($macAddress) && $usedMacs{$macAddress}){
	die("duplicate MAC Address for $hostname  :".$macAddress);    
    }
    $usedMacs{$macAddress}  = $host;
    

    
    my @mappedAlias ;
    
    foreach my $alias (@aliases) {
	my $alias = getMappedHostAlias($entry,$hostname,$alias);
	$usedHostNames{$alias} = $host;
	push @mappedAlias,$alias;
	
    }
    
    $host->{aliases} = \@mappedAlias;
    
    # classify the host based on its name
    classifyHost($host);
    setHostOwnership($host);

    push @allhosts ,$host;

}

# <hosts>
# −
# 	<changeInfo>
# <createdAt>2007-12-10T16:51:24.009+00:00</createdAt>
# </changeInfo>
# <description/>
# −
# 	<hostAliases>
# −
# 	<alias>
# <domain>.campus.bath.ac.uk.</domain>
# <name>testalas</name>
# </alias>
# <type>CNAME</type>
# </hostAliases>
# −
# 	<hostAliases>
# −
# 	<alias>
# <domain>.campus.bath.ac.uk.</domain>
# <name>testalias2</name>
# </alias>
# <type>AREC</type>
# </hostAliases>
# <hostClass>ADPC</hostClass>
# −
# 	<hostName>
# <domain>.campus.bath.ac.uk.</domain>
# <name>testpc</name>
# </hostName>
# <id>1</id>
# <ipAddress>138.38.0.17</ipAddress>
# −
# 	<location>
# <building/>
# <room/>
# </location>
# <macAddress>00:11:22:33:44:55</macAddress>
# </hosts>
sub writeNameToXML{
    my $writer = $_[0];
    my $outerTag = $_[1];
    my $name = $_[2];
    $writer->startTag($outerTag);
    $writer->dataElement("name",$name->{name});
    $writer->dataElement("domain",$name->{ndSuffix});
    
    $writer->endTag($outerTag);
    
}
sub writeHostsToXML{
    my $w = new XML::Writer(OUTPUT=>$output,DATA_MODE=>1,DATA_INDENT=>3);
    $w->startTag("SoakData");
    foreach my $host  ( @allhosts){
	$w->startTag("hosts");
	writeNameToXML($w,"hostName",$host->{hostname});
	$w->dataElement("hostClass",$host->{hostClass});
	$w->dataElement("ipAddress",$host->{ipAddress});
	$w->dataElement("description",$host->{description});
	$w->dataElement("macAddress",$host->{macAddress});
	
	$w->startTag("location");
	if(my $loc = $host->{location}){
	    $w->dataElement("building",$loc->{building});
	    $w->dataElement("room",$loc->{room});
	}

	$w->endTag("location");
	
	$w->startTag("ownership");
	$w->dataElement("orgUnit",$host->{orgUnit});
	$w->endTag("ownership");
	foreach my $alias (@{$host->{aliases}}){
	    $w->startTag("hostAliases");
	    writeNameToXML($w,"alias",$alias->{alias});
	    $w->dataElement("type",$alias->{type});
	    $w->endTag("hostAliases");
	}
	$w->endTag("hosts");
    }
    $w->endTag("SoakData");
    
    
}


############### MAIN

GetOptions("cfile=s"=>\$classifyFile,"dfile=s"=>\$dhcpFile,"oufile=s"=>\$ouFile,"output=s"=>\$outputFile);
$filename = shift;

if(!defined($filename)){
    print "usage:\n\t hosts2xml.pl --cfile=hostClassOverrides --dfile=dhcpFile --oufile=oufile --output=file.xml hostsFile\n";
    exit(0);
}

if(!defined($outputFile)){
    $outputFile = "hosts.xml";
}

$output->open("> $outputFile") or die ("cant' open output file $outputFile");
$stream  = IO::File->new("<$filename") or die "can't open file $filename";
if(defined($classifyFile)){
    loadExtraClassifiers($classifyFile);
}
if(defined($ouFile)){
    loadOrgUnits($ouFile);
}

if(defined($dhcpFile)){
    loadDHCPFile($dhcpFile);
}
$parser->parseFile( $stream, $filename, \&handler );
writeHostsToXML();
print STDERR "Added $count hosts\n";
