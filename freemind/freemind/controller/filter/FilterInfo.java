/*
 * Created on 15.05.2005
 *
 */
package freemind.controller.filter;


/**
 * @author dimitri
 * 15.05.2005
 */
public class FilterInfo {
    private int info = Filter.FILTER_INITIAL_VALUE;

    /**
     * 
     */
    public FilterInfo() {
        super();
    }
    public void reset() {
        info = Filter.FILTER_INITIAL_VALUE;
    }
    
    public void setAncestor() {
        add(Filter.FILTER_SHOW_ANCESTOR); 
    }
    
    public void setDescendant() {
        add(Filter.FILTER_SHOW_DESCENDANT); 
    }
    
    public void setMatched() {
        add(Filter.FILTER_SHOW_MATCHED); 
    }
    
    void add(int flag){
        if ((flag & (Filter.FILTER_SHOW_MATCHED | Filter.FILTER_SHOW_HIDDEN)) != 0){
            info &= ~Filter.FILTER_INITIAL_VALUE;
        }
        info |= flag;        
    }

     int get() {
        return info;
    }
    /**
     * @return
     */
    public boolean isAncestor() {
        return (info & Filter.FILTER_SHOW_ANCESTOR) != 0;
    }
    /**
     * @return
     */
    public boolean isMatched() {
        return (info & Filter.FILTER_SHOW_MATCHED) != 0;
    }
}
