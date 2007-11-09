/**
 * 
 */
package plugins.script;

import java.io.PrintStream;

import plugins.script.ScriptEditorPanel.ScriptHolder;
import plugins.script.ScriptEditorPanel.ScriptModel;
import plugins.script.ScriptingEngine.ErrorHandler;
import freemind.common.ScriptEditorProperty;
import freemind.controller.actions.generated.instance.Pattern;
import freemind.controller.actions.generated.instance.ScriptEditorWindowConfigurationStorage;
import freemind.extensions.HookRegistration;
import freemind.main.Tools.BooleanHolder;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.ApplyPatternAction.ExternalPatternAction;

public class ScriptingRegistration implements HookRegistration,
		ExternalPatternAction {

	private final class PatternScriptModel implements ScriptModel {

		private String mScript;
		private String mOriginalScript;

		public PatternScriptModel(String pScript) {
			mScript = pScript;
			mOriginalScript = pScript;
		}

		public ScriptEditorWindowConfigurationStorage decorateDialog(
				ScriptEditorPanel pPanel,
				String pWindow_preference_storage_property) {
			return (ScriptEditorWindowConfigurationStorage) controller
					.decorateDialog(pPanel,
							pWindow_preference_storage_property);
		}

		public void endDialog(boolean pIsCanceled) {
			if (pIsCanceled) {
				mScript = mOriginalScript;
			}
		}

		public boolean executeScript(int pIndex, PrintStream pOutStream, ErrorHandler pErrorHandler) {
			return ScriptingEngine.executeScript(controller.getSelected(),
					new BooleanHolder(true), mScript, controller, pErrorHandler, pOutStream);
		}

		public int getAmountOfScripts() {
			return 1;
		}

		public ScriptHolder getScript(int pIndex) {
			return new ScriptHolder("Script", mScript);
		}

		public boolean isDirty() {
			return false;
		}

		public void setScript(int pIndex, ScriptHolder pScript) {
			mScript = pScript.getScript();
		}

		public void storeDialogPositions(ScriptEditorPanel pPanel,
				ScriptEditorWindowConfigurationStorage pStorage,
				String pWindow_preference_storage_property) {
			controller.storeDialogPositions(pPanel, pStorage,
					pWindow_preference_storage_property);
		}

		public String getScript() {
			return mScript;
		}
	}

	private final MindMapController controller;
	private final MindMap mMap;
	private ScriptEditorProperty.ScriptEditorStarter mScriptEditorStarter;

	public ScriptingRegistration(ModeController controller, MindMap map) {
		this.controller = (MindMapController) controller;
		mMap = map;
		ScriptingEngine.logger = controller.getFrame().getLogger(this.getClass().getName());
	}

	public void register() {
		controller.registerPlugin(this);
		mScriptEditorStarter = new ScriptEditorProperty.ScriptEditorStarter() {

			public String startEditor(String pScriptInput) {
				ScriptingEngine.logger.info("Start to edit script...");
				PatternScriptModel patternScriptModel = new PatternScriptModel(
						pScriptInput);
				ScriptEditorPanel scriptEditorPanel = new ScriptEditorPanel(
						patternScriptModel, controller.getFrame());
				scriptEditorPanel.setVisible(true);
				return patternScriptModel.getScript();
			}
		};
		controller.registerPlugin(mScriptEditorStarter);
	}

	public void deRegister() {
		controller.deregisterPlugin(this);
		controller.deregisterPlugin(mScriptEditorStarter);
	}

	public void act(MindMapNode node, Pattern pattern) {
		if (pattern.getPatternScript() != null
				&& pattern.getPatternScript().getValue() != null) {
			ScriptingEngine.executeScript(node, new BooleanHolder(false), pattern
					.getPatternScript().getValue(), controller, new ErrorHandler(){

						public void gotoLine(int pLineNumber) {
						}}, System.out);
		}
	}

}