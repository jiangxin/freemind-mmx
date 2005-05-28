/*
 * Created on 14.05.2005
 *
 */
package freemind.controller.filter.util;

import javax.swing.*;
import java.util.*;

public class SortedListModel extends AbstractListModel {
  SortedSet model;

  public SortedListModel() {
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
    model.clear();
    fireContentsChanged(this, 0, getSize());
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

  public boolean removeElement(Object element) {
    boolean removed = model.remove(element);
    if (removed) {
      fireContentsChanged(this, 0, getSize());
    }
    return removed;   
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
}