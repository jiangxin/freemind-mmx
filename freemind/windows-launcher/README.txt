This windows launcher was compiled using Dev-C++ <http://www.bloodshed.net/devcpp.html>.
It is quite probable, that it can be compiled using Microsoft Visual C++.


This launcher requires no more development, for it does quite everything one would
expect from a java launcher:

1) Has an icon
2) When a file is dragged and dropped over the launcher, it is passed to java application
   as a parameter
3) When the launcher is run from a DOS console and given multiple arguments, all are passed to
   java application
4) Launcher has to reside in a directory relative to the application directory. However, it
   can be run from any other directory. Therefore, if you assign the launcher to a file type,
   the application is run correctly with the file passed as an argument.

