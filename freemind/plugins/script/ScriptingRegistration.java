/**
 * 
 */
package plugins.script;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import plugins.script.ScriptEditorPanel.ScriptHolder;
import plugins.script.ScriptEditorPanel.ScriptModel;
import plugins.script.ScriptingEngine.ErrorHandler;
import freemind.common.BooleanProperty;
import freemind.common.ScriptEditorProperty;
import freemind.common.SeparatorProperty;
import freemind.common.StringProperty;
import freemind.common.TextTranslator;
import freemind.controller.actions.generated.instance.Pattern;
import freemind.controller.actions.generated.instance.ScriptEditorWindowConfigurationStorage;
import freemind.extensions.HookRegistration;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain.StartupDoneListener;
import freemind.main.HtmlTools;
import freemind.main.Tools;
import freemind.main.Tools.BooleanHolder;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.ApplyPatternAction.ExternalPatternAction;
import freemind.preferences.FreemindPropertyContributor;
import freemind.preferences.layout.OptionPanel;

public class ScriptingRegistration implements HookRegistration,
		ExternalPatternAction, StartupDoneListener {

	private static final class ScriptingPluginPropertyContributor implements
			FreemindPropertyContributor {

		private final MindMapController modeController;

		public ScriptingPluginPropertyContributor(
				MindMapController modeController) {
			this.modeController = modeController;
		}

		public List getControls(TextTranslator pTextTranslator) {
			Vector controls = new Vector();
			controls.add(new OptionPanel.NewTabProperty(
					"plugins/scripting/tab_name"));
			controls.add(new SeparatorProperty(
					"plugins/scripting/separatorPropertyName"));
			controls.add(new BooleanProperty(
					FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_FILE_RESTRICTION
							+ ".tooltip",
					FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_FILE_RESTRICTION));
			controls.add(new BooleanProperty(
					FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_NETWORK_RESTRICTION
							+ ".tooltip",
					FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_NETWORK_RESTRICTION));
			controls.add(new BooleanProperty(
					FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_EXEC_RESTRICTION
							+ ".tooltip",
					FreeMind.RESOURCES_EXECUTE_SCRIPTS_WITHOUT_EXEC_RESTRICTION));
			controls.add(new BooleanProperty(
					FreeMind.RESOURCES_SIGNED_SCRIPT_ARE_TRUSTED + ".tooltip",
					FreeMind.RESOURCES_SIGNED_SCRIPT_ARE_TRUSTED));
			controls.add(new StringProperty(
					FreeMind.RESOURCES_SCRIPT_USER_KEY_NAME_FOR_SIGNING
							+ ".tooltip",
					FreeMind.RESOURCES_SCRIPT_USER_KEY_NAME_FOR_SIGNING));
			return controls;
		}
	}

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
					.decorateDialog(pPanel, pWindow_preference_storage_property);
		}

		public void endDialog(boolean pIsCanceled) {
			if (pIsCanceled) {
				mScript = mOriginalScript;
			}
		}

		public boolean executeScript(int pIndex, PrintStream pOutStream,
				ErrorHandler pErrorHandler) {
			return ScriptingEngine.executeScript(controller.getSelected(),
					new BooleanHolder(true), mScript, controller,
					pErrorHandler, pOutStream, getScriptCookies());
		}

		public int getAmountOfScripts() {
			return 1;
		}

		public ScriptHolder getScript(int pIndex) {
			return new ScriptHolder("Script", mScript);
		}

		public boolean isDirty() {
			return !Tools.safeEquals(mScript, mOriginalScript);
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

		public int addNewScript() {
			return 0;
		}
	}

	private final MindMapController controller;
	private final MindMap mMap;
	private ScriptEditorProperty.ScriptEditorStarter mScriptEditorStarter;
	private HashMap mScriptCookies = new HashMap();
	private ScriptingPluginPropertyContributor mScriptingPluginPropertyContributor;

	public ScriptingRegistration(ModeController controller, MindMap map) {
		this.controller = (MindMapController) controller;
		mMap = map;
		ScriptingEngine.logger = controller.getFrame().getLogger(
				this.getClass().getName());
	}

	public void register() {
		controller.registerPlugin(this);
		mScriptEditorStarter = new ScriptEditorProperty.ScriptEditorStarter() {

			public String startEditor(String pScriptInput) {
				ScriptingEngine.logger.info("Start to edit script..."
						+ pScriptInput);
				PatternScriptModel patternScriptModel = new PatternScriptModel(
						pScriptInput);
				ScriptEditorPanel scriptEditorPanel = new ScriptEditorPanel(
						patternScriptModel, controller.getFrame(), false);
				scriptEditorPanel.setVisible(true);
				return patternScriptModel.getScript();
			}
		};
		controller.registerPlugin(mScriptEditorStarter);
		mScriptingPluginPropertyContributor = new ScriptingPluginPropertyContributor(
				controller);
		OptionPanel.addContributor(mScriptingPluginPropertyContributor);
		controller.getFrame().registerStartupDoneListener(this);
	}

	public void deRegister() {
		controller.deregisterPlugin(this);
		controller.deregisterPlugin(mScriptEditorStarter);
		OptionPanel.removeContributor(mScriptingPluginPropertyContributor);
	}

	public void act(MindMapNode node, Pattern pattern) {
		if (pattern.getPatternScript() != null
				&& pattern.getPatternScript().getValue() != null) {
			String scriptString = HtmlTools.toXMLUnescapedText(HtmlTools.unescapeHTMLUnicodeEntity(pattern
					.getPatternScript().getValue()));
			ScriptingEngine.logger.info("Executing script: " +scriptString);
			executeScript(node, scriptString);
		}
	}

	private void executeScript(MindMapNode node, String scriptString) {
		ScriptingEngine.executeScript(node, new BooleanHolder(false),
				scriptString, controller, new ErrorHandler() {
					public void gotoLine(int pLineNumber) {
					}
				}, System.out, getScriptCookies());
	}

	public HashMap getScriptCookies() {
		return mScriptCookies;
	}

	public void startupDone() {
		/* Is there a startup groovy script? */
		String startupScriptFile = System.getProperty("startup_groovy_script");
		if (startupScriptFile != null && !startupScriptFile.isEmpty()) {
			String expandFileName = Tools.expandFileName(startupScriptFile);
			ScriptingEngine.logger.info("Starting script at " + expandFileName);
			String scriptString = Tools.getFile(new File(expandFileName));
			if (scriptString != null && !scriptString.isEmpty()) {
				ScriptingEngine.logger.info("Starting script " + scriptString);
				try {
					executeScript(controller.getRootNode(), scriptString);
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
				}
			} else {
				ScriptingEngine.logger.warning("Starting script not found!");
			}
		}
	}

}