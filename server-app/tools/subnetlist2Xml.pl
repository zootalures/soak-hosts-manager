#!/usr/bin/perl
use Text::CSV;
use XML::Writer;
use IO::Handle;
use strict;


sub trimws{
    my $val = shift;

    $val=~s/^\s+.//;
    $val=~s/\s+$//;
    return $val;
    
}
my $io = new IO::Handle();
my $writer = new XML::Writer(OUTPUT=>$io->fdopen(fileno(STDOUT),"w"),DATA_MODE=>1,DATA_INDENT=>3);

$writer->startTag("SoakData");

my $skip = <STDIN>;
while(my $line = <STDIN>){
    chomp($line);
#    print STDERR "parsing $line\n";
    my $csv = Text::CSV->new();
    $csv->parse($line);
    my ($vlan,$noscan,$netc,$name,$desc,$iprange,$netaddr,$gw,$snmask)= $csv->fields();
    
    
    if(($iprange=~/\s*(\d+\.\d+\.\d+\.\d+)\s*\W\s*(\d+\.\d+\.\d+\.\d+)\s*/)){


	my ($minIP,$maxIP) = ($1,$2);
	$writer->startTag("subnets");
	$writer->dataElement("minIP",trimws $minIP);
	$writer->dataElement("maxIP",trimws $maxIP);
	$writer->dataElement("noScan",lc($noscan));
	$writer->dataElement("networkClass",$netc);
	$vlan = trimws($vlan);
	if($vlan){
	    $writer->dataElement("vlan",trimws $vlan);
	}
	$gw = trimws ($gw);
	
	if($gw){
	    $writer->dataElement("gateway",trimws $gw);
	}
	$writer->dataElement("name",$name);
	$writer->dataElement("description",$desc);
	$writer->endTag("subnets");
    }else{
	print STDERR "Range did not parse for line $line\n";
    }
    
}
$writer->endTag("SoakData");
$writer->end();

