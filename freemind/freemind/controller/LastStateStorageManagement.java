/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2011 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package freemind.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import freemind.controller.actions.generated.instance.MindmapLastStateMapStorage;
import freemind.controller.actions.generated.instance.MindmapLastStateStorage;
import freemind.controller.actions.generated.instance.NodeListMember;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;

/**
 * @author foltin
 * 
 */
public class LastStateStorageManagement {
	public static final int LIST_AMOUNT_LIMIT = 50;
	private MindmapLastStateMapStorage mLastStatesMap = null;
	protected static java.util.logging.Logger logger = null;

	public LastStateStorageManagement(String pXml) {
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		try {
			XmlAction action = Tools.unMarshall(pXml);
			if (action != null) {
				if (action instanceof MindmapLastStateMapStorage) {
					mLastStatesMap = (MindmapLastStateMapStorage) action;

				}
			}
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
		if (mLastStatesMap == null) {
			logger.warning("Creating a new last state map storage as there was no old one or it was corrupt.");
			mLastStatesMap = new MindmapLastStateMapStorage();
		}
	}

	public String getXml() {
		return Tools.marshall(mLastStatesMap);
	}
	
	public void changeOrAdd(MindmapLastStateStorage pStore) {
		boolean found = false;
		for (Iterator it = mLastStatesMap.getListMindmapLastStateStorageList()
				.iterator(); it.hasNext();) {
			MindmapLastStateStorage store = (MindmapLastStateStorage) it.next();
			if (Tools.safeEquals(pStore.getRestorableName(), store.getRestorableName())) {
				// deep copy
				store.setLastZoom(pStore.getLastZoom());
				store.setX(pStore.getX());
				store.setY(pStore.getY());
				store.clearNodeListMemberList();
				for (Iterator it2 = pStore.getListNodeListMemberList()
						.iterator(); it2.hasNext();) {
					NodeListMember member = (NodeListMember) it2.next();
					store.addNodeListMember(member);
				}
				found = true;
				store.setLastChanged(System.currentTimeMillis());
				break;
			}
		}
		if (!found) {
			pStore.setLastChanged(System.currentTimeMillis());
			mLastStatesMap.addMindmapLastStateStorage(pStore);
		}
		// size limit
		if (mLastStatesMap.sizeMindmapLastStateStorageList() > LIST_AMOUNT_LIMIT) {
			// make map from date to object:
			TreeMap dateToStoreMap = new TreeMap();
			for (Iterator it = mLastStatesMap
					.getListMindmapLastStateStorageList().iterator(); it
					.hasNext();) {
				MindmapLastStateStorage store = (MindmapLastStateStorage) it
						.next();
				dateToStoreMap
						.put(Long.valueOf(-store.getLastChanged()), store);
			}
			// clear list
			mLastStatesMap.clearMindmapLastStateStorageList();
			// rebuild
			int counter = 0;
			for (Iterator it = dateToStoreMap.entrySet().iterator(); it
					.hasNext();) {
				Entry entry = (Entry) it.next();
				mLastStatesMap
						.addMindmapLastStateStorage((MindmapLastStateStorage) entry
								.getValue());
				counter++;
				if (counter >= LIST_AMOUNT_LIMIT) {
					// drop the rest of the elements.
					break;
				}
			}
		}
	}

	public MindmapLastStateStorage getStorage(String pFileName) {
		for (Iterator it = mLastStatesMap.getListMindmapLastStateStorageList()
				.iterator(); it.hasNext();) {
			MindmapLastStateStorage store = (MindmapLastStateStorage) it.next();
			if (Tools.safeEquals(pFileName, store.getRestorableName())) {
				return store;
			}
		}
		return null;
	}
	
	public List getList() {
		return mLastStatesMap.getListMindmapLastStateStorageList();
	}
	
	public int getLastFocussedTab() {
		return mLastStatesMap.getLastFocusedTab();
	}
	
	public void setLastFocussedTab(int pIndex) {
		mLastStatesMap.setLastFocusedTab(pIndex);
	}
	
}
