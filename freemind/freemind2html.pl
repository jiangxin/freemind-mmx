#!/usr/bin/perl -w

# Copyright 2000 by Jared Rhine
# Released under the GPL

use strict;

use XML::Simple;
use Getopt::Long;

use vars qw($file);

GetOptions('file=s' => \$file);

die "Required parameter missing: '--file filename'\n" unless $file;
$file = "./$file" unless $file =~ m[/]; # XML::Simple doesn't look in current directory

my $map = XMLin($file);
my $root = $map->{node};

print "$root->{text}\n " . dumpnode($root,0);

sub dumpnode {
  my ($me, $level) = @_;

  # Single-child nodes are HASH refs, not ARRAY refs
  my $children = $me->{node};
  my @sn_refs = ref($children) eq 'ARRAY' ? @{$children} : $children;

  # Output children in indented list
  my $pad = ' ' x ($level*2);
  my $output = "$pad<ul>\n";
  foreach my $subnode (@sn_refs) { 
    my $newlevel = $level + 1;
    $output .= "$pad  <li>$subnode->{text}\n";
    $output .= dumpnode($subnode,$newlevel) if $subnode->{node};
  }
  $output .= "$pad</ul>\n";
}

