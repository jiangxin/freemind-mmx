/*
 * Created on 18.06.2005
 * Copyright (C) 2005 Dimitri Polivaev
 */
package freemind.controller.filter.util;

import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * @author Dimitri Polivaev
 * 18.06.2005
 */
public class SortedMapVector{
    private static class MapElement{
        private Comparable key;
        private Object value;
        public MapElement(Comparable key, Object value) {
            super();
            this.key = key;
            this.value = value;
        }
        Object getValue() {
            return value;
        }
        void setValue(Object value) {
            this.value = value;
        }
        Comparable getKey() {
            return key;
        }
    }
    
    private Vector elements;
    private static final int ELEMENT_NOT_FOUND_FLAG = 1 << 31;
    private static final int CAPACITY_INCREMENT = 10;

    public SortedMapVector() {
        elements =new Vector(0, CAPACITY_INCREMENT);
    }
    
    public int add(Comparable key, Object value) {
        int index = findElement(key);
        if ((index & ELEMENT_NOT_FOUND_FLAG) != 0)
        {
            index &= ~ELEMENT_NOT_FOUND_FLAG;
            elements.add(index, new MapElement(key, value));
        }
        return index;
    }
    
    public int capacity() {
        return elements.capacity();
    }
    public void clear() {
        elements.clear();
    }
    public Object getValue(int index) {
        return ((MapElement)elements.get(index)).getValue();
    }
    
    public Object getValue(Comparable key) {
        int index = findElement(key);
        if ((index & ELEMENT_NOT_FOUND_FLAG) == 0)
            return ((MapElement)elements.get(index)).getValue();
        throw new NoSuchElementException();
    }
    
    public Comparable getKey(int index) {
        return ((MapElement)elements.get(index)).getKey();
    }
    
    public boolean containsKey(Comparable key){
        int index = findElement(key);
        return (index & ELEMENT_NOT_FOUND_FLAG) == 0;         
    }
    
    public int indexOf(Comparable key){
        int index = findElement(key);
        if((index & ELEMENT_NOT_FOUND_FLAG) == 0) 
            return index;
        return -1;
     }
    private int findElement(Comparable key) {
        return findElement(key, 0, size());
    }

    private int findElement(Comparable key, int first, int size) {
        if (size == 0)
            return first | ELEMENT_NOT_FOUND_FLAG;
        int halfSize = size/2;
        int middle = first + halfSize;
        MapElement middleElement = (MapElement)elements.get(middle);
        int comparationResult = key.compareTo(middleElement.getKey());
        int last = first+size-1;
        if (comparationResult < 0){
            if (halfSize <= 1){
                if (middle != first)
                    comparationResult = key.compareTo(((MapElement)elements.get(first)).getKey());
                if (comparationResult < 0)
                    return first | ELEMENT_NOT_FOUND_FLAG;
                if (comparationResult == 0)
                    return first;
                return middle | ELEMENT_NOT_FOUND_FLAG;
            }
            return findElement(key, first, halfSize);
        }
        else if(comparationResult == 0)
        {
            return middle;
        }
        else {
            
            if (halfSize <= 1){
                if (middle != last)
                    comparationResult = key.compareTo(((MapElement)elements.get(last)).getKey());
                if (comparationResult < 0)
                    return last | ELEMENT_NOT_FOUND_FLAG;
                if (comparationResult == 0)
                    return last;
                return last+1 | ELEMENT_NOT_FOUND_FLAG;
            }
            return findElement(key, middle, size - halfSize);
        }
    }

    public boolean remove(Comparable key) {
        int index = findElement(key);
        if ((index & ELEMENT_NOT_FOUND_FLAG) == 0)
        {
            elements.remove(index);
            return true;
        }
        return false;
    }
    
    public void remove(int index) {
        elements.removeElementAt(index);
    }
    
    public int size() {
        return elements.size();
    }
}
