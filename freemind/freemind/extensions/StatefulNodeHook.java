package freemind.extensions;

public interface StatefulNodeHook extends PermanentNodeHook {
	void setContent(String key, String content);
	String getContent(String key);
	void setContent(String content);
	String getContent();
	void setContentUndoable(String key, String content);
}
