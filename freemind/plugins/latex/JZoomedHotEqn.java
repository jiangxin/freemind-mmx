package plugins.latex;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import atp.sHotEqn;

public class JZoomedHotEqn extends sHotEqn {
	static private double zoom = 1f;
	static String editorTitle = null;
	private LatexNodeHook model;

	JZoomedHotEqn(LatexNodeHook model) {
		setDebug(false);
		setEditable(false);
		setBorder(true);
		this.model = model;
		setEquation(model.getContent(null));
		if (editorTitle == null) {
			editorTitle = model.getMindMapController().getText(
					"plugins/latex/LatexNodeHook.editorTitle");
		}
	}

	public Dimension getPreferredSize() {
		Dimension dimension = isValid() ? super.getPreferredSize()
				: getSizeof(getEquation());
		dimension.height *= zoom;
		dimension.width *= zoom;
		return dimension;
	}

	public void paint(Graphics g) {
		if (zoom != 1F) {
			Graphics2D g2 = (Graphics2D) g;
			final AffineTransform transform = g2.getTransform();
			g2.scale(zoom, zoom);
			super.paint(g);
			g2.setTransform(transform);
		} else {
			super.paint(g);
		}
	}

	public void setBounds(int x, int y, int w, int h) {
		if (zoom < 1f) {
			super.setBounds(x, y, (int) (w / zoom), (int) (h / zoom));
		} else {
			super.setBounds(x, y, (int) (w), (int) (h));
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			edit();
			e.consume();
			return;
		}
		super.mouseClicked(e);
	}

	private void edit() {
		JTextArea textArea = new JTextArea(getEquation());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		final JScrollPane editorScrollPane = new JScrollPane(textArea);
		editorScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorScrollPane.setPreferredSize(new Dimension(500, 160));
		JDialog edit = new JDialog(JOptionPane.getFrameForComponent(this),
				editorTitle, true);
		edit.getContentPane().add(editorScrollPane);
		edit.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		edit.pack();
		edit.setLocationRelativeTo(this);
		edit.setVisible(true);
		String eq = textArea.getText();
		model.setContent(null, eq);
	}

	public void setModel(LatexNodeHook model) {
		this.model = model;
		setEquation(model.getContent(null));
		revalidate();
		repaint();
	}

}
