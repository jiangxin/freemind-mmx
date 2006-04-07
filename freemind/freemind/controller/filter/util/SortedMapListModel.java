/*
 * Created on 14.05.2005
 *
 */
package freemind.controller.filter.util;

import javax.swing.*;
import java.util.*;

public class SortedMapListModel extends AbstractListModel implements SortedListModel {
  SortedSet model;

  public SortedMapListModel() {
    model = new TreeSet();
  }

  public int getSize() {
    return model.size();
  }

  public Object getElementAt(int index) {
    return model.toArray()[index];
  }

  public void add(Object element) {
    if (model.add(element)) {
      fireContentsChanged(this, 0, getSize());
    }
  }
  
  public void addAll(Object elements[]) {
      Collection c = Arrays.asList(elements);
      model.addAll(c);
      fireContentsChanged(this, 0, getSize());
  }
  
  public void clear() {
      int oldSize = getSize();
      if(oldSize > 0){
          model.clear();
          fireIntervalRemoved(this, 0, oldSize-1);
      }
  }

  public boolean contains(Object element) {
    return model.contains(element);
  }

  public Object firstElement() {
    return model.first();
  }

  public Iterator iterator() {
    return model.iterator();
  }

  public Object lastElement() {
    return model.last();
  }

/**
 * @param o
 * @return
 */
public int getIndexOf(Object o) {
    Iterator i = iterator();
    int count = -1;
    while(i.hasNext()){
        count++;
        if (i.next().equals(o))
            return count;
    }
    return -1;
}

/* (non-Javadoc)
 * @see freemind.controller.filter.util.SortedListModel#replace(java.lang.Object, java.lang.Object)
 */
public void replace(Object oldO, Object newO) {
    if(oldO.equals(newO))
        return;
    boolean removed = model.remove(oldO);
    boolean added = model.add(newO);
    if(removed || added) {
        fireContentsChanged(this, 0, getSize());
    }
}

/* (non-Javadoc)
 * @see freemind.controller.filter.util.SortedListModel#delete(java.lang.Object)
 */
public void remove(Object element) {
    if (model.remove(element)) {
        fireContentsChanged(this, 0, getSize());
      }
    
}
}