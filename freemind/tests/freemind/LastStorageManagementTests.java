package tests.freemind;

import java.util.List;

import freemind.controller.LastStateStorageManagement;
import freemind.controller.actions.generated.instance.MindmapLastStateStorage;

public class LastStorageManagementTests extends FreeMindTestBase {

	private static final String INITIAL_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><mindmap_last_state_map_storage/>";
	private LastStateStorageManagement mMgm;

	protected void setUp() throws Exception {
		super.setUp();
		mMgm = new LastStateStorageManagement(INITIAL_XML);
	}

	public void testGetXml() {
		assertEquals(INITIAL_XML, mMgm.getXml());
	}

	public void testChangeOrAdd() {
		for (int i = 0; i < LastStateStorageManagement.LIST_AMOUNT_LIMIT + 1; ++i) {
			MindmapLastStateStorage test = new MindmapLastStateStorage();
			test.setRestorableName("" + i);
			mMgm.changeOrAdd(test);
			assertEquals(test, mMgm.getStorage("" + i));
			waitOneMilli();
		}
		// the element at zero is the oldest and must have been removed.
		assertNull(mMgm.getStorage("" + 0));
	}

	public void testGetList() {
		for (int i = 0; i < LastStateStorageManagement.LIST_AMOUNT_LIMIT; ++i) {
			MindmapLastStateStorage test = new MindmapLastStateStorage();
			test.setRestorableName("" + i);
			if (i <= 5) {
				test.setTabIndex(5 - i);
			} else {
				test.setTabIndex(-1);
			}
			mMgm.changeOrAdd(test);
			assertEquals(test, mMgm.getStorage("" + i));
			waitOneMilli();
		}
		List list = mMgm.getLastOpenList();
		assertEquals(6, list.size());
		assertEquals("5",
				((MindmapLastStateStorage) list.get(0)).getRestorableName());
		assertEquals("4",
				((MindmapLastStateStorage) list.get(1)).getRestorableName());
	}

	public void testChangeOrAdd2() {
		for (int i = 0; i < LastStateStorageManagement.LIST_AMOUNT_LIMIT; ++i) {
			MindmapLastStateStorage test = new MindmapLastStateStorage();
			test.setRestorableName("" + i);
			mMgm.changeOrAdd(test);
			assertEquals(test, mMgm.getStorage("" + i));
			waitOneMilli();
		}
		// change the first:
		MindmapLastStateStorage storageFirstElement = mMgm.getStorage("" + 0);
		storageFirstElement.setY(2);
		mMgm.changeOrAdd(storageFirstElement);
		waitOneMilli();
		MindmapLastStateStorage test = new MindmapLastStateStorage();
		test.setRestorableName(""
				+ LastStateStorageManagement.LIST_AMOUNT_LIMIT + 1);
		mMgm.changeOrAdd(test);
		waitOneMilli();
		// the element at one is the oldest and must have been removed.
		assertNotNull(mMgm.getStorage("" + 0));
		assertNull(mMgm.getStorage("" + 1));
	}

	private void waitOneMilli() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			freemind.main.Resources.getInstance().logException(e);

		}
	}

}
