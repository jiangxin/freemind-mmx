/*
 * Created on 16.03.2004
 *
 */
package accessories.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;

import accessories.plugins.dialogs.ChooseFormatPopupDialog;

import com.jgoodies.forms.builder.DefaultFormBuilder;

import freemind.common.PropertyBean;
import freemind.common.PropertyControl;
import freemind.common.SeparatorProperty;
import freemind.common.XmlBindingTools;
import freemind.common.PropertyControl.TextTranslator;
import freemind.controller.Controller;
import freemind.controller.actions.generated.instance.Pattern;
import freemind.extensions.HookRegistration;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.StylePatternFactory;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;
import freemind.preferences.FreemindPropertyContributor;
import freemind.preferences.FreemindPropertyListener;
import freemind.preferences.layout.OptionPanel;

/**
 * @author foltin
 * 
 */
public class AutomaticLayout extends PermanentMindMapNodeHookAdapter {

	private static final String AUTOMATIC_FORMAT_LEVEL = "automaticFormat_level";

	/**
	 * Registers the property pages.
	 * 
	 * @author foltin
	 * 
	 */
	public static class Registration implements HookRegistration {
		private AutomaticLayoutPropertyContributor mAutomaticLayoutPropertyContributor;

		private final MindMapController modeController;

		private static FreemindPropertyListener listener = null;

		public Registration(ModeController controller, MindMap map) {
			modeController = (MindMapController) controller;
		}

		public void register() {
			// add listener:
			if (listener == null) {
				FreemindPropertyListener listener = new FreemindPropertyListener() {

					public void propertyChanged(String propertyName,
							String newValue, String oldValue) {
						if (propertyName.startsWith(AUTOMATIC_FORMAT_LEVEL)) {
							patterns = null;
						}
					}
				};
				Controller.addPropertyChangeListener(listener);
			}

			mAutomaticLayoutPropertyContributor = new AutomaticLayoutPropertyContributor(
					modeController);
			OptionPanel.addContributor(mAutomaticLayoutPropertyContributor);
		}

		public void deRegister() {
			OptionPanel.removeContributor(mAutomaticLayoutPropertyContributor);
		}

	}

	public static class StylePatternProperty extends PropertyBean implements
			PropertyControl, ActionListener {

		String description;

		String label;

		String pattern;

		JButton mButton;

		private final TextTranslator mTranslator;

		private final MindMapController mindMapController;

		public StylePatternProperty(String description, String label,
				TextTranslator pTranslator, MindMapController pController) {
			super();
			this.description = description;
			this.label = label;
			mTranslator = pTranslator;
			mindMapController = pController;
			mButton = new JButton();
			mButton.addActionListener(this);
			pattern = null;
		}

		public String getDescription() {
			return description;
		}

		public String getLabel() {
			return label;
		}

		public void setValue(String value) {
			pattern = value;
			Pattern resultPattern = getPatternFromString();
			String patternString = StylePatternFactory.toString(resultPattern,
					new TextTranslator() {
						public String getText(String pKey) {
							return mindMapController.getText(pKey);
						}
					});
			mButton.setText(patternString);
			mButton.setToolTipText(patternString);
		}

		public String getValue() {
			return pattern;
		}

		public void layout(DefaultFormBuilder builder,
				TextTranslator pTranslator) {
			JLabel label = builder.append(pTranslator.getText(getLabel()),
					mButton);
			label.setToolTipText(pTranslator.getText(getDescription()));
			// add "reset to standard" popup:

		}

		public void actionPerformed(ActionEvent arg0) {
			// construct pattern:
			Pattern pat = getPatternFromString();
			ChooseFormatPopupDialog formatDialog = new ChooseFormatPopupDialog(
					mindMapController.getFrame().getJFrame(),
					mindMapController,
					"accessories/plugins/AutomaticLayout.properties_StyleDialogTitle",
					pat);
			formatDialog.setModal(true);
			formatDialog.pack();
			formatDialog.setVisible(true);
			// process result:
			if (formatDialog.getResult() == ChooseFormatPopupDialog.OK) {
				Pattern resultPattern = formatDialog.getPattern();
				resultPattern.setName("dummy");
				pattern = XmlBindingTools.getInstance().marshall(resultPattern);
				setValue(pattern);
				firePropertyChangeEvent();
			}
		}

		private Pattern getPatternFromString() {
			return StylePatternFactory.getPatternFromString(pattern);
		}

		public void setEnabled(boolean pEnabled) {
			mButton.setEnabled(pEnabled);
		}

	}

	private static final class AutomaticLayoutPropertyContributor implements
			FreemindPropertyContributor {

		private final MindMapController modeController;

		public AutomaticLayoutPropertyContributor(
				MindMapController modeController) {
			this.modeController = modeController;
		}

		public List getControls(TextTranslator pTextTranslator) {
			Vector controls = new Vector();
			controls
					.add(new OptionPanel.NewTabProperty(
							"accessories/plugins/AutomaticLayout.properties_PatternTabName"));
			controls
					.add(new SeparatorProperty(
							"accessories/plugins/AutomaticLayout.properties_PatternSeparatorName"));
			controls.add(new StylePatternProperty("level1",
					AUTOMATIC_FORMAT_LEVEL + "1", pTextTranslator,
					modeController));
			controls.add(new StylePatternProperty("level2",
					AUTOMATIC_FORMAT_LEVEL + "2", pTextTranslator,
					modeController));
			controls.add(new StylePatternProperty("level3",
					AUTOMATIC_FORMAT_LEVEL + "3", pTextTranslator,
					modeController));
			controls.add(new StylePatternProperty("level4",
					AUTOMATIC_FORMAT_LEVEL + "4", pTextTranslator,
					modeController));
			controls.add(new StylePatternProperty("level5",
					AUTOMATIC_FORMAT_LEVEL + "5", pTextTranslator,
					modeController));
			return controls;
		}
	}

	private static Pattern[] patterns = null;

	/**
	 * 
	 */
	public AutomaticLayout() {
		super();

	}

	private void setStyle(MindMapNode node) {
		logger.finest("updating node id="
				+ node.getObjectId(getMindMapController()) + " and text:"
				+ node);
		int depth = depth(node);
		logger.finest("COLOR, depth=" + (depth));
		reloadPatterns();
		int myIndex = patterns.length - 1;
		if (depth < patterns.length)
			myIndex = depth;
		Pattern p = patterns[myIndex];
		getMindMapController().applyPattern(node, p);
	}

	private int depth(MindMapNode node) {
		if (node.isRoot())
			return 0;
		return depth(node.getParentNode()) + 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHook#onAddChild(freemind.modes.MindMapNode)
	 */
	public void onAddChildren(MindMapNode newChildNode) {
		logger.finest("onAddChildren " + newChildNode);
		super.onAddChild(newChildNode);
		setStyleRecursive(newChildNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHook#onUpdateChildrenHook(freemind.modes.MindMapNode)
	 */
	public void onUpdateChildrenHook(MindMapNode updatedNode) {
		super.onUpdateChildrenHook(updatedNode);
		setStyleRecursive(updatedNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHook#onUpdateNodeHook()
	 */
	public void onUpdateNodeHook() {
		super.onUpdateNodeHook();
		setStyle(getNode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode node) {
		super.invoke(node);
		setStyleRecursive(node);
	}

	/** get styles from preferences: */
	private void reloadPatterns() {
		if (patterns == null) {
			patterns = new Pattern[5];
			for (int i = 0; i < 5; ++i) {
				String property = getMindMapController().getFrame()
						.getProperty(AUTOMATIC_FORMAT_LEVEL + (i+1));
				patterns[i] = StylePatternFactory
						.getPatternFromString(property);
			}
		}
	}

	/**
	 * @param node
	 */
	private void setStyleRecursive(MindMapNode node) {
		logger.finest("setStyle " + node);
		setStyle(node);
		// recurse:
		for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
			MindMapNode child = (MindMapNode) i.next();
			invoke(child);
		}
	}

}
