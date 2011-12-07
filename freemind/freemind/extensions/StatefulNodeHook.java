package freemind.extensions;

/**
 * Used to implement undo on hook deletion. In fact, if a permanent hook is removed,
 * someone has to store its parameters to restore it later. Currently, I don't know,
 * why the load/save mechanism is not used.
 * @author polivaev
 * @date some time ago...
 */
public interface StatefulNodeHook extends PermanentNodeHook {
	void setContent(String key, String content);

	String getContent(String key);

	void setContent(String content);

	String getContent();

	void setContentUndoable(String key, String content);
}
