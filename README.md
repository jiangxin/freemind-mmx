# FreeMind Hacking

FreeMind is my favorite notebook. Everyday I write and search using FreeMind.

## What is FreeMind?

References:

* [FreeMind Offical site](http://freemind.sourceforge.net/)
* [Online PPT from ossxp.com (in Chinese)](http://www.ossxp.com/HelpCenter/00000_OSSXP/AboutUs_Slide/1010%20开源小礼物)

## Why Hacked FreeMind?

Because it scratch my personal itch.

* Chinese characters in output \*.mm file are *NOT* in UTF-8, but encoded
  as XML entities, such as &#xxx;. So the source code of documents can
  not be easily recognized;

* and it's hard to compare between different revisions either.

* In FreeMind output documents (\*.mm) , there are some mutable status
  infomations. They are not version control friendly.

  For example: status of node (folded or unfolded) is saved in FOLDED
  attribute. If user does not change the contents of the node, only
  change the folding status of nodes, the output file (.mm file) will
  be modified and will create an unecessary commit in version control
  system.

## VCS (Version Control System) I used for Freemind-MMX

Freemind was hosted in a CVS repository at the early age, and when I start
to hack Freemind-MMX, I used SVN, and all may hacks are in SVN feature
branches.

In the early 2009, I started to use Mercurial/Hg + MQ to maintain
FreeMind-MMX.

In 2010, FreeMind-MMX migrated to Git, and the patches are managed using
Topgit finally.

## Build FreeMind-MMX

Source code is hosted on GitHub:

* <http://github.com/jiangxin/freemind-mmx>

There are many branches in this repo:

* Branches start with 't/' are topic branches managed by Topgit.
* "tgmaster" is the base where all topic branches patched against.
* "master" branch are created from "tgmaster" branch and applied patches
  using git-quiltimport command.

Before build FreeMind-MMX from source code, you should install JDK.
Then get souce code of FreeMind-MMX by cloning this repo:

1. Clone freemind-mmx repo:

        $ git clone git://github.com/jiangxin/freemind-mmx.git
        $ cd freemind-mmx

2. Checkout the master branch, and build

        $ git checkout master
        $ cd freemind
        $ ant dist
        $ ant post
