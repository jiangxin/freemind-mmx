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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * @author Foltin
 * 
 */
public class NodeFoldingComponent extends JButton {
	private static final int TIMER_DELAY = 25;
	private static final int CIRCLE_DIAMETER = 10;
	private static final int COLOR_COUNTER_MAX = 15;
	protected static java.util.logging.Logger logger = null;
	private boolean mIsEntered;
	private int mColorCounter = 0;
	private NodeView nodeView;

	public NodeFoldingComponent(NodeView view) {
		super();
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		this.nodeView = view;
		setModel(new DefaultButtonModel());
		init(null, null);
		// setPressedIcon(getIcon("accessories/show.png"));
		setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		setBackground(Color.BLACK);
		setContentAreaFilled(false);
		setFocusPainted(false);
		setFocusable(false);
		// setVerticalAlignment(SwingConstants.TOP);
		setAlignmentY(Component.TOP_ALIGNMENT);
		// initShape();
		setUI(new RoundImageButtonUI());
		addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent pE) {
			}

			public void mousePressed(MouseEvent pE) {
			}

			public void mouseExited(MouseEvent pE) {
				mIsEntered = false;
				repaint();
			}

			public void mouseEntered(MouseEvent pE) {
				mIsEntered = true;
				repaint();
			}

			public void mouseClicked(MouseEvent pE) {
			}
		});
		int delay = TIMER_DELAY;
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (mIsEntered && mColorCounter < COLOR_COUNTER_MAX) {
					mColorCounter++;
					repaint();
				}
				if (!mIsEntered && mColorCounter > 0) {
					mColorCounter--;
					repaint();
				}

			}
		};
		new Timer(delay, taskPerformer).start();
	}

	public Dimension getPreferredSize() {
		Insets i = getInsets();
		int iw = CIRCLE_DIAMETER;
		return new Dimension(iw + i.right + i.left, iw + i.top + i.bottom);
	}


	class RoundImageButtonUI extends BasicButtonUI {
		protected Shape shape, foldingCircle, base;

		protected void installDefaults(AbstractButton b) {
			super.installDefaults(b);
			clearTextShiftOffset();
			defaultTextShiftOffset = 0;
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
			NodeFoldingComponent b = (NodeFoldingComponent) c;
			Rectangle bounds = shape.getBounds();
			Color col = getColorForCounter();
			if (b.mIsEntered) {
				// g2.setStroke(new BasicStroke(1.0f));
				Color oldColor = g2.getColor();
				g2.setColor(nodeView.getMap().getBackground());
				g2.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
				g2.setColor(col);
				int xmiddle = bounds.x + bounds.width / 2;
				int ymiddle = bounds.y + bounds.height / 2;
				g2.drawLine(bounds.x, ymiddle, bounds.x + bounds.width, ymiddle);
				if(nodeView.getModel().isFolded()) {
					g2.drawLine(xmiddle, bounds.y, xmiddle, bounds.y
							+ bounds.height);
				}
				g2.setColor(oldColor);
				g2.draw(shape);
			} else {
				if (mColorCounter != 0) {
					// bounds = foldingCircle.getBounds();
					int xmiddle = bounds.x + bounds.width / 2;
					int ymiddle = bounds.y + bounds.height / 2;
					Color oldColor = g2.getColor();
					g2.setColor(nodeView.getMap().getBackground());
					g2.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
					g2.setColor(col);
					if(nodeView.getModel().isFolded()) {
						g2.drawLine(xmiddle, bounds.y, xmiddle, bounds.y
								+ bounds.height);
					}
					g2.drawLine(bounds.x, ymiddle, bounds.x + bounds.width,
							ymiddle);
					g2.setColor(oldColor);
					g2.draw(shape);
				} else {
					if(nodeView.getModel().isFolded()) {
						g2.translate(-bounds.width / 4, -bounds.height / 4);
						g2.draw(foldingCircle);
					}
				}
			}
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		/**
		 * @return
		 */
		private Color getColorForCounter() {
			Color color = nodeView.getModel().getEdge().getColor();

			double col = 16 * (16 - mColorCounter - 1) / 256.0;
			return new Color((int) (color.getRed() * col),
					(int) (color.getGreen() * col),
					(int) (color.getBlue() * col));
		}

		public Dimension getPreferredSize(JComponent c) {
			JButton b = (JButton) c;
			Insets i = b.getInsets();
			int iw = CIRCLE_DIAMETER;
			return new Dimension(iw + i.right + i.left, iw + i.top + i.bottom);
		}

		private void initShape(JComponent c) {
			if (!c.getBounds().equals(base)) {
				Dimension s = c.getPreferredSize();
				base = c.getBounds();
				shape = new Ellipse2D.Float(0, 0, s.width - 1, s.height - 1);
				foldingCircle = new Ellipse2D.Float(s.width / 4, s.height / 4,
						s.width * 3 / 4 - 1, s.height * 3 / 4 - 1);
			}
		}
	}

	public NodeView getNodeView() {
		return nodeView;
	}

	public void setCorrectedLocation(Point p) {
		setLocation(p.x - CIRCLE_DIAMETER / 4, p.y - CIRCLE_DIAMETER / 4);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("RoundImageButton");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		NodeFoldingComponent comp = new NodeFoldingComponent(null);
		comp.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent pE) {
				System.out.println("Pressed!");
			}
		});
		frame.getContentPane().add(comp);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setSize(400, 800);
		frame.setVisible(true);
	}

}
