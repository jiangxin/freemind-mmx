cd C:\share\Installation-Files\freemind\dist
@rem java -Xrunhprof -jar lib/freemind.jar
@rem java -Xrunhprof:cpu=samples,depth=30 -verbose:gc -jar lib/freemind.jar
@rem java -verbose:gc -jar lib/freemind.jar
@rem java -Xrunhprof:file=fm.hprof,format=b -jar lib/freemind.jar
@rem java -XrunjinsightPA -jar lib/freemind.jar
java -jar lib/freemind.jar

pause

