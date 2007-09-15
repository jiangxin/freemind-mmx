package freemind.main;

import java.io.*;
import java.text.*;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;


public class StdFormatter extends SimpleFormatter {

    // Line separator string.  This is the value of the line.separator
    // property at the moment that the SimpleFormatter was created.
    private String lineSeparator = (String) java.security.AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction("line.separator"));

    /**
     * Format the given LogRecord.
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    public synchronized String format(LogRecord record) {
    	if(! record.getLoggerName().equals(StdOutErrLevel.STDERR.getName())
    			&& ! record.getLoggerName().equals(StdOutErrLevel.STDOUT.getName())){
    		return super.format(record);
    	}
    	StringBuffer sb = new StringBuffer();
    	sb.append(lineSeparator);
    	String message = formatMessage(record);
    	sb.append(record.getLevel().getLocalizedName());
    	sb.append(": ");
    	sb.append(message);
    	return sb.toString();
    }
}
