/*
 * Created on 26.05.2005
 *
 */
package freemind.modes;

import java.util.Vector;

import freemind.controller.filter.util.SortedListModel;

/**
 * @author dimitri
 * 26.05.2005
 */
public class MapStatistics {
    private SortedListModel mapIcons;

    public MapStatistics() {
        super();
        mapIcons = new SortedListModel();
        Vector iconNames = MindIcon.getAllIconNames();
    }

    void addIcon(MindIcon icon) {
        mapIcons.add(icon);
    }

    /**
     * @return
     */
    public SortedListModel getIcons() {
        return mapIcons;
    }

}
