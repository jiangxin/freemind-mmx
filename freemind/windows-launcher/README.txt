This windows launcher was compiled using Dev-C++ <http://www.bloodshed.net/devcpp.html>.
To download Dev-C++ go to project page at SourceForge <http://sourceforge.net/projects/dev-cpp/>.
To recompile it just open the project Freemind.dev with Dev-C++ and compile the project.

It is quite probable that it can be compiled using Microsoft Visual C++.



This launcher requires no more development for it does quite everything one would
expect from a java launcher:

1) Has an icon
2) When a file is dragged and dropped over the launcher, it is passed to java application
   as a parameter
3) When the launcher is run from a DOS console and given multiple arguments, all are passed to
   java application
4) Launcher has to reside in a directory relative to the application directory. However, it
   can be run from any other directory. Therefore, if you assign the launcher to a file type,
   the application is run correctly with the file passed as an argument.


Things to watch

1) FreeMind.ico must be a real icon, not an icon created by changing the extension of a file from
   bmp to ico.
2) Icon1.rc is the file telling where the icons is stored. It is a resource file (I don't pretend
   to know what it means). This resource file is compiled into Icon1.o (object file), which is then
   linked with freemind.o to create Freemind.exe
