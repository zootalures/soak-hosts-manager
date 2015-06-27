#!/usr/bin/perl

print "<SoakData>\n"; 
while(my $line = <STDIN>){
    chomp $line;
    if($line =~/(\d+)\s+([^\s]+)/){
	my ($number,$name) = ($1,$2);
	$name =~s/\&/&amp;/;
	print "<vlans>\n",
	"\t<name>$name</name>\n",
	"\t<number>$number</number>\n",
	"</vlans>\n";
    }
}
print "</SoakData>\n";
