/*
 * Created on 08.05.2005
 *
 */
package freemind.controller.filter.util;

import freemind.main.Resources;

/**
 * @author dimitri
 * 08.05.2005
 */
public class TranslatedString{
    private String foreignString;
    private String key;
    private TranslatedString(){
    }
    public TranslatedString(String key) {
        this.key = key;
        foreignString =Resources.getInstance().getResourceString(key);
    }
    static public TranslatedString literal(String literal){
        TranslatedString result = new TranslatedString();
        result.key = literal;
        result.foreignString = literal;
        return result;
    }
    public boolean equals(Object o){
        if (o instanceof TranslatedString){
            TranslatedString ts =  (TranslatedString)o;
            return key.equals(ts.key);
        }
        return key.equals(o);
    }

    public String toString(){
        return foreignString;
    }
}
