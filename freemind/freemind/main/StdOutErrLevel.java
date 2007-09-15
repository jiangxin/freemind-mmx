package freemind.main;

/*
 * Copyright 2006 Sun Microsystems, Inc.  All rights reserved.
 * Use is subject to license terms.
 *
 * ident        "$Revision: 1.1.2.1 $      SMI"
 */

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.util.logging.Level;

/**
 * Class defining 2 new Logging levels, one for STDOUT, one for STDERR,
 * used when multiplexing STDOUT and STDERR into the same rolling log file
 * via the Java Logging APIs.
 * @since 2.2
 */
public class StdOutErrLevel extends Level {
    
    /**
     * private constructor
     * @param name name used in toString
     * @param value integer value, should correspond to something reasonable in default Level class
     */
    private StdOutErrLevel(String name, int value) {
        super(name, value);
    }
    /**
     * Level for STDOUT activity.
     */
    public static Level STDOUT = new StdOutErrLevel("STDOUT", Level.INFO.intValue()+53);
    /**
     * Level for STDERR activity
     */
    public static Level STDERR = new StdOutErrLevel("STDERR", Level.SEVERE.intValue()+53);

    /**
     * Method to avoid creating duplicate instances when deserializing the
     * object.
     * @return the singleton instance of this <code>Level</code> value in this
     * classloader
     * @throws ObjectStreamException If unable to deserialize
     */
    protected Object readResolve()
	throws ObjectStreamException {
        if (this.intValue() == STDOUT.intValue())
            return STDOUT;
        if (this.intValue() == STDERR.intValue())
            return STDERR;
        throw new InvalidObjectException("Unknown instance :" + this);
    }        
    
}
