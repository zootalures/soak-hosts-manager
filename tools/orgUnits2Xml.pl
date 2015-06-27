#!/usr/bin/perl
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



while(my $line = <STDIN>){
    chomp($line);
    $line =~/^(\w+)\s+(.*)$/;
    my $id = $1;
    my $name = $2;
    
    
    	# <organisationalUnits>
	# 	<id>CC</id>
	# 	<name>Computing Services</name>
	# </organisationalUnits>

    
    $writer->startTag("organisationalUnits");
    $writer->dataElement("id",trimws $id);
    $writer->dataElement("name",$name);
    $writer->endTag("organisationalUnits");
    
    
}
$writer->endTag("SoakData");
$writer->end();

