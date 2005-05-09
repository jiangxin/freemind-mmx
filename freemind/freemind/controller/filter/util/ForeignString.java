/*
 * Created on 08.05.2005
 *
 */
package freemind.controller.filter.util;

import freemind.controller.Controller;

/**
 * @author dimitri
 * 08.05.2005
 */
public class ForeignString{
    private String foreignString;
    private String key;
    private ForeignString(){        
    }
    public ForeignString(String key) {        
        this.key = key;
        foreignString =Controller.getInstance().getResourceString(key);
    }
    static public ForeignString literal(String literal){
        ForeignString result = new ForeignString();
        result.key = literal;
        result.foreignString = literal;
        return result;
    }
    public boolean equals(Object o){
        return key.equals(o);
    }
    public String toString(){
        return foreignString;
    }
}
