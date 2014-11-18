# FreeMind Hacking

FreeMind is an open source [mind-mapping](http://en.wikipedia.org/wiki/Mind_map)
software, and it's my favorite notebook. ;-)

## What is FreeMind?

References:

* [FreeMind Offical site](http://freemind.sourceforge.net/)
* [Online PPT from ossxp.com (in Chinese)](http://www.ossxp.com/HelpCenter/00000_OSSXP/AboutUs_Slide/1010%20开源小礼物)

## Why comes FreeMind-MMX?

Because it scratch my personal itch.

* FreeMind saves mind map in a XML file with extension ".mm".  Chinese
  characters in it are encoded as XML entities, such as "\&#xxx;", but
  *NOT* in UTF-8, which makes FreeMind document larger and hard to see
  differences between two revisions.

* All my documents are stored in a version control system (CVS and SVN
  before 2008, and Hg and Git latter).  But I find FreeMind's XML files
  are not version control friendly.  When I expand or collapse a node of
  mind map, FreeMind shows file has been modified and needs to be saved.
  This is because FreeMind records too many attributes for node including
  folded status, created time, modified time in the result XML file.
  Thus may make many unecessary commits to version control system.

I named my hacking of FreeMind as FreeMind-MMX, because when it saving
mind map, there are two files instead of one.  One output file (with extension
".mm") is a XML file, which store text contents and all necessary node
attributes, and another one is a hidden ".mmx" file, which is also a XML
file, but only stores auxiliary node attributes (such as folded, created
time and modified time).

## Download

FreeMind was hosted in a CVS repository at the early age.  when I started
to hack, I used SVN (using vendor branch model) to manage my
customizations, and all may hacks are in SVN feature branches.  At that
time, to share my contributions, there are not many choices like today.
So I host it on sourceforge.net.  Now I only use the download services
of Source Forge, and I will upload the binaries if I still remember the
password of source forge.

* Download binaries from: [Download from SourceForge](https://sourceforge.net/projects/freemind-mmx/files/FreeMind-MMX/)

## Source Code

In the early 2009, I started to use Mercurial/Hg + MQ to maintain
FreeMind-MMX.  And since 2010, I became a fun of Git, I wrote a book for
Git in Chinese and became contributors for Git. So FreeMind-MMX
migrated again, and my customizations are managed using Topgit finally.
That's why you should download FreeMind-MMX here:

* [FreeMind-MMX on GitHub](https://github.com/jiangxin/freemind-mmx>)

There are many branches in this repo:

* Branches start with 't/' are topic branches managed by Topgit.
* "tgmaster" branch is the base where all topic branches patched against.
* "master" branch are created from "tgmaster" branch and applied patches
  using git-quiltimport command.  This branch will be rewind when patches
  are changed or upstream is updated.

## Build FreeMind-MMX from source

Before build FreeMind-MMX from source code, you should install JDK.
Be sure to export a valid `JAVA_HOME`.

Then clone FreeMind-MMX from GitHub, and build.

1. Clone freemind-mmx repo:

        $ git clone git://github.com/jiangxin/freemind-mmx.git
        $ cd freemind-mmx

2. Checkout the master branch, and build

        $ git checkout master
        $ cd freemind
        $ ant dist
        $ ant post

3. Find build results in ../post directory, and click to install.
