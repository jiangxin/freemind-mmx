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

## Build FreeMind-MMX

Source code is hosted on GitHub:

* <http://github.com/jiangxin/freemind-mmx>

Before your build FreeMind-MMX from source code, you should
install JDK and Quilt. Quilt is a patch management tool.

Then get souce code of FreeMind-MMX by cloning this repo:

1. Clone freemind-mmx repo:

        $ git clone git://github.com/jiangxin/freemind-mmx.git
        $ cd freemind-mmx

2. Update FreeMind upstream source code in submodule 'upstream'

        $ git submodule init
        $ git submodule update

## Build for Debian/Ubuntu

Debian is my favorite Linus distribution. Build a FreeMind-MMX debian
package is simple:

    $ dpkg-buildpackage -b -rfakeroot

## Build for other platform

1. Apply patches on FreeMind upstream source code.

        $ make -f debian/rules patch
        $ cd upstream/freemind

2. Build FreeMind-MMX

        $ ant dist
        $ ant post
