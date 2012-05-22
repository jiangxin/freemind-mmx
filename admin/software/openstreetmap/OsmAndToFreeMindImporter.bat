# This file will start the OsmAndToFreeMind converter with custom memory settings for
# the JVM. With the below settings the heap size (Available memory for the application)
# will range from 64 megabyte up to 720 megabyte.

java.exe -Djava.util.logging.config.file=logging.properties -Xms64M -Xmx512M -cp "./OsmAndToFreeMindImporter.jar;./lib/*.jar" com.sourceforge.freemind.PoiToFreeMind %1 %2 %3 %4 %5 %6 %7 %8 %9
