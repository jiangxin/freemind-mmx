/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.modes.attributes;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
public class Attribute {
    private String name;
    private String value;
    /**
     * @param string
     */
    public Attribute(String name) {
        this.name = name;
        this.value = "";
    }
    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
