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
import java.util.AbstractMap.SimpleImmutableEntry;

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

import freemind.main.Tools;

/**
 * @author Foltin
 * 
 */
public class NodeFoldingComponent extends JButton {
	private static final int TIMER_DELAY = 50;
	private static final int COLOR_COUNTER_MAX = 15;
	protected static java.util.logging.Logger logger = null;
	private boolean mIsEntered;
	private int mColorCounter = 0;
	private NodeView nodeView;
	/**
	 * 
	 */
	private static final float SIZE_FACTOR_ON_MOUSE_OVER = 4f;

	public NodeFoldingComponent(NodeView view) {
		super();
		if (logger == null) {
			logger = freemind.main.Resources.getInstance().getLogger(
					this.getClass().getName());
		}
		this.nodeView = view;
		setModel(new DefaultButtonModel());
		init(null, null);
		setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		setBackground(Color.BLACK);
		setContentAreaFilled(false);
		setFocusPainted(false);
		setFocusable(false);
		setAlignmentY(Component.TOP_ALIGNMENT);
		setUI(new RoundImageButtonUI());
		addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent pE) {
			}

			public void mousePressed(MouseEvent pE) {
			}

			public void mouseExited(MouseEvent pE) {
				mIsEntered = false;
				mColorCounter = COLOR_COUNTER_MAX;
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
		return getUI().getPreferredSize(this);
	}

	/**
	 * @return
	 */
	private int getZoomedCircleRadius() {
		return nodeView.getZoomedFoldingSymbolHalfWidth();
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
			Object oldRenderingHint = nodeView.getController()
					.setEdgesRenderingHint(g2);
			g2.setColor(c.getBackground());
			NodeFoldingComponent b = (NodeFoldingComponent) c;
			Rectangle bounds = shape.getBounds();
			Color col = getColorForCounter();
			if (b.mIsEntered) {
				Color oldColor = g2.getColor();
				g2.setColor(nodeView.getMap().getBackground());
				g2.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
				g2.setColor(col);
				int xmiddle = bounds.x + bounds.width / 2;
				int ymiddle = bounds.y + bounds.height / 2;
				g2.drawLine(bounds.x, ymiddle, bounds.x + bounds.width, ymiddle);
				if (nodeView.getModel().isFolded()) {
					g2.drawLine(xmiddle, bounds.y, xmiddle, bounds.y
							+ bounds.height);
				}
				g2.setColor(oldColor);
				g2.draw(shape);
			} else {
				if (mColorCounter != 0) {
					int xmiddle = bounds.x + bounds.width / 2;
					int ymiddle = bounds.y + bounds.height / 2;
					int diamter = bounds.width * mColorCounter
							/ COLOR_COUNTER_MAX;
					if(nodeView.getModel().isFolded()) {
						diamter = Math.max(diamter, foldingCircle.getBounds().width);
					}
					int radius = diamter / 2;
					Color oldColor = g2.getColor();
					g2.setColor(nodeView.getMap().getBackground());
					g2.fillOval(xmiddle - radius, ymiddle - radius, diamter,
							diamter);
					g2.setColor(col);
					if (nodeView.getModel().isFolded()) {
						g2.drawLine(xmiddle, ymiddle - radius, xmiddle, ymiddle
								+ radius);
					}
					g2.drawLine(xmiddle - radius, ymiddle, xmiddle + radius,
							ymiddle);
					g2.setColor(oldColor);
					g2.drawOval(xmiddle - radius, ymiddle - radius, diamter,
							diamter);
				} else {
					if (nodeView.getModel().isFolded()) {
						g2.draw(foldingCircle);
					}
				}
			}
			Tools.restoreAntialiasing(g2, oldRenderingHint);
		}

		/**
		 * @return
		 */
		private Color getColorForCounter() {
			Color color = nodeView.getModel().getEdge().getColor();

			int col = 16 * mColorCounter;
			return new Color((int) (color.getRed()), (int) (color.getGreen()),
					(int) (color.getBlue()), col);
		}

		public Dimension getPreferredSize(JComponent c) {
			JButton b = (JButton) c;
			Insets i = b.getInsets();
			int iw = (int) (getZoomedCircleRadius() * 2f * SIZE_FACTOR_ON_MOUSE_OVER);
			return new Dimension(iw + i.right + i.left, iw + i.top + i.bottom);
		}

		private void initShape(JComponent c) {
			if (!c.getBounds().equals(base)) {
				Dimension s = c.getPreferredSize();
				base = c.getBounds();
				float factor = 1f / SIZE_FACTOR_ON_MOUSE_OVER;
				shape = new Ellipse2D.Float(0, 0, s.width - 1, s.height - 1);
				float height = s.height * factor;
				float width = s.width * factor;
				foldingCircle = new Ellipse2D.Float(s.width / 2 - width / 2f,
						s.height / 2f - height / 2f, width, height);
			}
		}
	}

	public NodeView getNodeView() {
		return nodeView;
	}

	public void setCorrectedLocation(Point p) {
		int zoomedCircleRadius = getZoomedCircleRadius();
		boolean left = nodeView.getModel().isLeft();
		int xCorrection = (int) (zoomedCircleRadius * (SIZE_FACTOR_ON_MOUSE_OVER + ((left) ? +1f
				: -1f)));
		setLocation(p.x - xCorrection, (int) (p.y - zoomedCircleRadius
				* SIZE_FACTOR_ON_MOUSE_OVER));
	}

	private static int getCircleDiameter() {
		return NodeView.getFoldingSymbolWidth();
	}

}
