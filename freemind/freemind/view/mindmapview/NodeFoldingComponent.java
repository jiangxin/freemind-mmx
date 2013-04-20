/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: NodeMotionListenerView.java,v 1.1.4.4.4.9 2009/03/29 19:37:23 christianfoltin Exp $*/
package freemind.view.mindmapview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;

import freemind.main.Resources;

/**
 * @author Foltin
 * 
 */
public class NodeFoldingComponent extends JButton {
	protected static java.util.logging.Logger logger = null;

	public NodeFoldingComponent(NodeView view) {
		super();
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		this.nodeView = view;
		setModel(new DefaultButtonModel());
		init(null, getIcon("accessories/plus.png"));
		setPressedIcon(getIcon("accessories/show.png"));
		setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		setBackground(Color.BLACK);
		setContentAreaFilled(false);
		setFocusPainted(false);
		// setVerticalAlignment(SwingConstants.TOP);
		setAlignmentY(Component.TOP_ALIGNMENT);
		initShape();
		setUI(new RoundImageButtonUI());
	}

	protected ImageIcon getIcon(String resource) {
		return new ImageIcon(Resources.getInstance().getResource(resource));
	}

	public Dimension getPreferredSize() {
		Icon icon = getIcon();
		Insets i = getInsets();
		int iw = Math.max(icon.getIconWidth(), icon.getIconHeight());
		return new Dimension(iw + i.right + i.left, iw + i.top + i.bottom);
	}

	protected Shape shape, base;

	protected void initShape() {
		if (!getBounds().equals(base)) {
			Dimension s = getPreferredSize();
			base = getBounds();
			shape = new Ellipse2D.Float(0, 0, s.width - 1, s.height - 1);
		}
	}

	protected void paintBorder(Graphics g) {
		initShape();
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(getBackground());
		// g2.setStroke(new BasicStroke(1.0f));
		g2.draw(shape);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	public boolean contains(int x, int y) {
		initShape();
		return shape.contains(x, y);
	}

	private NodeView nodeView;

	class RoundImageButtonUI extends BasicButtonUI {
		protected Shape shape, base;

		protected void installDefaults(AbstractButton b) {
			super.installDefaults(b);
			clearTextShiftOffset();
			defaultTextShiftOffset = 0;
			Icon icon = b.getIcon();
			if (icon == null)
				return;
			b.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
			b.setContentAreaFilled(false);
			b.setFocusPainted(false);
			b.setOpaque(false);
			b.setBackground(Color.BLACK);
			// b.setVerticalAlignment(SwingConstants.TOP);
			b.setAlignmentY(Component.TOP_ALIGNMENT);
			initShape(b);
		}

		protected void installListeners(AbstractButton b) {
			BasicButtonListener listener = new BasicButtonListener(b) {

				public void mousePressed(MouseEvent e) {
					AbstractButton b = (AbstractButton) e.getSource();
					initShape(b);
					if (shape.contains(e.getX(), e.getY())) {
						super.mousePressed(e);
					}
				}

				public void mouseEntered(MouseEvent e) {
					if (shape.contains(e.getX(), e.getY())) {
						super.mouseEntered(e);
					}
				}

				public void mouseMoved(MouseEvent e) {
					if (shape.contains(e.getX(), e.getY())) {
						super.mouseEntered(e);
					} else {
						super.mouseExited(e);
					}
				}
			};
			if (listener != null) {
				b.addMouseListener(listener);
				b.addMouseMotionListener(listener);
				b.addFocusListener(listener);
				b.addPropertyChangeListener(listener);
				b.addChangeListener(listener);
			}
		}

		public void paint(Graphics g, JComponent c) {
			super.paint(g, c);
			Graphics2D g2 = (Graphics2D) g;
			initShape(c);
			// Border
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(c.getBackground());
			// g2.setStroke(new BasicStroke(1.0f));
			g2.draw(shape);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		public Dimension getPreferredSize(JComponent c) {
			JButton b = (JButton) c;
			Icon icon = b.getIcon();
			Insets i = b.getInsets();
			int iw = Math.max(icon.getIconWidth(), icon.getIconHeight());
			return new Dimension(iw + i.right + i.left, iw + i.top + i.bottom);
		}

		private void initShape(JComponent c) {
			if (!c.getBounds().equals(base)) {
				Dimension s = c.getPreferredSize();
				base = c.getBounds();
				shape = new Ellipse2D.Float(0, 0, s.width - 1, s.height - 1);
			}
		}
	}

	public NodeView getNodeView() {
		return nodeView;
	}

	public static void main(String[] args) {
        JFrame frame = new JFrame("RoundImageButton");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new NodeFoldingComponent(null));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setSize(400, 800);

	}
	
}
