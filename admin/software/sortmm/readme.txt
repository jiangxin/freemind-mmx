This program is released under GNU GPL v2. You can do with it whatever is
allowed with the above mentioned license. Due to the size of this small code I
have left any copyright note out of the source.

For further questions please contact me at 'aliakc@web.de'

usage: sortmm mindmap.mm > output.mm

After that please remove the first line of the new output.mm file containing
the below xml version statement. Be sure that you only delete that one line.

delete: <?xml version="1.0"?>

This program was written for my personal own usage together with freemind 0.9.0
The reason writing this was, that one of my mind maps has around 3500 nodes
filled with notes, reminders etc. Unfortunately using freemind 0.9.0 (and the
upcoming version 1.0.0) causes some out of memory issues when trying to sort
the nodes.

After dealing for a couple of days I concluded that this may be an issue of how
the Java virtual machine deals with threads, memory etc. Even lowering stack
and increasing heap didn't solve the problem. Even trying to analyze the
internals of the Java virtual machine using visualvm didn't make things better.

So I allowed myself to write this small code to sort my mind map with said
nodes within seconds. Please note that this works for me and not necessarily
for you. So please keep some backups of your mind maps and compare the results.

I use this small program in companion with iThoughts for iPhone. The script
called 'fixmap.sh' included within this archive is the usual way how I deal
with my mind map. The script 'sortmm.sh' is the makefile.

Ali Akcaagac
