package freemind.extensions;

import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;

abstract public class StatefulMindMapNodeHookAdapter extends PermanentMindMapNodeHookAdapter
		implements StatefulNodeHook {

	public String getContent() {
		return getContent(null);
	}

	public void setContent(String content) {
		setContent(null, content);
	}

	public void setContentUndoable(String content) {
		setContentUndoable(null, content);
	}

	public void setContentUndoable(String key, String content) {
		getMindMapController().undoableHookContentActor.performAction(getNode(), getName(), key, content);
	}

}
