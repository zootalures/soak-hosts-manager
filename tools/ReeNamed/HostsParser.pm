#!/usr/bin/perl
package ReeNamed::HostsParser;

# Geneirc Hosts parser for the bath hosts file, extracts some salient
# information about a given host (including RT references, a building
# and room name Comment dates, LIU dates and admin usernames
# Does not extract user names (as these are plain text)

use strict;
use IO::File;
use Cwd qw/realpath/;

use Carp qw(croak confess cluck);
use Data::Dumper;

sub new {
	my $class  = shift;
	my $origin = shift;

	#remove preceding and trailing dots  from origin
	#(we add the trailing one back later);
	$origin =~ s/\.$//;
	$origin =~ s/^\.//;

	return bless { -origin => $origin }, $class;

}

# returns a canonicalised fqdn with trailing dot from a partial host
# deals with inconsistnt fqdn dotting
# WARNING if the origin forms part of a host name, this will give
# inconsistent results.
sub canonHost {
	my ( $self, $hostname ) = @_;

	my $orig = $self->{-origin};

	# strip any preceding dots from host names (this is inconsistent anyway:)
	$hostname =~ s/^\.//;

	if ( $hostname =~ /$orig$/ || $hostname =~ /ac\.uk$/ ) {

		# fqdn missing trailing dot
		return $hostname . ".";
	}
	elsif ( $hostname =~ /$orig\.$/ || $hostname =~ /ac\.uk.$/ || $hostname =~/.com.$/ ) {

		#fqdn with trailing dot.
		return $hostname;
	}
	elsif ( $hostname =~ /\.$/ ) {

		#pqdn with trailnig dot ???
		return $hostname . $orig . ".";
	}
	else {
		return $hostname . "." . $orig . ".";
	}
}

my %mapping = (
	'W'         => -1,
	'WN'        => '1WN',
	'5W-Annexe' => '5WA',
	'MFC'       => -1,
	'HPLJ'      => -1,
	'CLP'       => -1,
	'Pod'       => -1,
	'No'        => -1,
	'Oak'       => 'OAK',
	'Lib'       => 'L',
	'Est'       => 'ES',
	'dv'        => -1,
	'slc'       => -1,
	'2s'        => '2S'
);

# These match before anything else
my %specials = (
	'Carpenter House' => { bd => 'CHS' },
	'John Wood'       => { bd => 'JHW' },
	'Oakfield'        => { bd => 'OAK' },
	'Pulteney'        => { bd => 'PUL' },

);

sub extractRTs {
	my $self    = shift;
	my $comment = shift;
	my $rec     = shift;

	while ( $comment =~ s/RT\#(\d+)// ) {

		#	print "Got ticket $1\n";
		push @{ $rec->{rts} }, $1;
	}
	return $comment;
}

sub extractPATs {
	my $self    = shift;
	my $comment = shift;
	my $rec     = shift;

	while ( $comment =~ s/PAT\#(\d+)// ) {
		#print "Got PAT $1\n";
		push @{ $rec->{pats} }, $1;
	}
	return $comment;
}

# extracts strings of chars which look like macs, and canonicalises them 
# to UC colon separated mac address which it puts in the host entry.
sub extractMAC {
	my $self    = shift;
	my $comment = shift;
	my $rec     = shift;

	if ( $comment =~ s/(([0-9A-F]{12})|(([0-9A-F]{2}:){5}[0-9A-F]{2}))//i ) {
		my $mac = $1;
		$mac =~ s/\://g;
		$mac = uc($mac);
		my @parts;
		while ( $mac =~ s/(..)// ) {
			push @parts, $1;
		}
		$mac = join( ":", @parts );
#		print "Got MAC $mac for add ",$rec->{address},"\n";
		$rec->{macAddress} = $mac;
	}
	return $comment;
}

sub extractLIU {    
	my $self    = shift;
	my $comment = shift;
	my $rec     = shift;
	if ( $comment =~ s/LIU (\d{8})// ) {
		$rec->{liu} = $1;
	}
	return $comment;
}
my %admins = (
	'RBP' => 'cssrbp',
	'MSA' => 'cssmsa',
	'OCC' => 'cspocc'
);

sub extractAdmin {
	my $self    = shift;
	my $comment = shift;
	my $rec     = shift;

	foreach my $ad ( keys %admins ) {
		if ( $comment =~ s/$ad// ) {
			$rec->{admin} = $admins{$ad};
			last;
		}
	}
	return $comment;
}

sub extractCOM {
	my $self    = shift;
	my $comment = shift;
	my $rec     = shift;
	if ( $comment =~ s/COM (\d{8})// ) {
		$rec->{COM} = $1;
	}
	return $comment;
}

# Somewhat complicated because of inconsistencies.  Picks out standard
# room names and also adds buildings if some well known things are
# there.

sub extractBuilding {
	my $self    = shift;
	my $comment = shift;
	my $rec     = shift;
	my $room    = undef;

	$comment =~ s/Oakfield (\d\.\d+)/OAK$1/;

	# Big RE pics out things which generally look like locations
	if ( $comment =~
s/(?:\s*-\s+)?\s?(\(?(\d?[A-z]{1,3}(?:-Annexe)?)-?([\?\d]*)([\.\-])([\d\?]+[A-z]?)\)?)(?:\s+-\s*)?//
	  )
	{

		# filter out ?? entries and make them undef
		my ( $bd, $lvl, $sep, $rm, $orig ) =
		  map {
			if ( $_ =~ /^\?+$/ ) { undef; }
			else {
				$_;
			}
		  } ( $2, $3, $4, $5, $1 );
		my $map = $mapping{$bd};
		next if -1 == $map;
		$bd = $map if $map;

		if (   $sep eq '-'
			&& undef == $lvl
			&& int($rm)
			&& $rm < 7 )
		{
			$lvl = $rm;
			$rm  = undef;
		}
		if ( $lvl
			&& !( $lvl =~ /^\d+$/ ) )
		{
			print STDERR "Skipping room in \"$orig\" probably not a room\n";
			next;
		}

		#	print "Got location $bd $lvl $rm\n";
		
		$room = { building => $bd, room => "$lvl.$rm", orig => $orig };

	}
	else {
	    foreach my $special ( keys %specials ) {
		if ( $comment =~ /$special/ ) {
		    my $bd = $specials{$special}->{bd};
		    $room = { building => $bd };
		}
	    }
	}
	
	if (defined($room)) {
	    $rec->{room} = $room;
	}
	return ($comment);
}

sub parseFile {
	my $self     = shift;
	my $stream   = shift;
	my $filename = shift;
	my $handler  = shift;
	my $lineno;

	$filename = realpath($filename);
	while ( my $line = $stream->getline ) {
		$lineno++;
		my $lc = $line;
		my $comline =0;
		if(!$lc=~/^\s*\#\s*\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\s+/){
			$comline = 1;
			$lc =~s/^\s*\#\s*//;
		}
		$lc =~ s/\#.*$//;
		if ( $lc =~ /^\s*$/ ) {

			#	    print "Skipping line $lineno \n";
			next;
		}
		my $rc =
		  ( $line =~ /^\s*(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})\s+([^\#]*)\s*(?:[\#]\s*(.*))?/ );
		if ( !$rc ) {
			print "Skipping $filename:$lineno\n";
			next;
		}
		my $comment = $3;
		my %ht;
 
		#eliminate duplicate hosts (i.e. with pqdn and fqdn);
		map { $ht{ $self->canonHost($_) } = 1 } ( split( /\s+/, $2 ) );
		my @hosts = sort( keys(%ht) );
		my $entry = {
			address => $1,
			hosts   => \@hosts,
			commented=>$comline
		};
		$comment = $self->extractBuilding( $comment, $entry );
		#$comment = $self->extractRTs( $comment,      $entry );
		#$comment = $self->extractPATs( $comment,     $entry );
		#$comment = $self->extractCOM( $comment,      $entry );
		#$comment = $self->extractLIU( $comment,      $entry );
		#$comment = $self->extractAdmin( $comment,    $entry );
		$comment = $self->extractMAC( $comment,      $entry );
		
		if ( !$comment =~ /^\s*$/ ) {
			$entry->{comment} =
			  $comment;    # store whats left of the comment if any
		}
		eval {
			&$handler( $entry, { filename => $filename, lineno => $lineno } )
			  if $handler;
		};
		if ($@) {
			print Dumper($entry);
			print "$line\n";
			die "parsing failed at $filename:$lineno $@";

		}

	}
}
1;
