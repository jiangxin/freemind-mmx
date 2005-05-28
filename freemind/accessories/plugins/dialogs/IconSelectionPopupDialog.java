/**
 * Created on 22.02.2004
 *FreeMind - A Program for creating and viewing Mindmaps
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
 *
 * @author <a href="mailto:labe@users.sourceforge.net">Lars Berning</a>
 */
package accessories.plugins.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import freemind.main.FreeMindMain;

public class IconSelectionPopupDialog extends JDialog implements KeyListener, MouseListener{
  private Vector icons;
	private int result;
	private JPanel iconPanel = new JPanel();
  private JLabel[] iconLabels;
  private JLabel descriptionLabel;
  private int numOfIcons;
  private int xDimension;
  private int yDimension;
  private Position selected = new Position(0,0);
  private static Position lastPosition = new Position(0,0);
  private Vector descriptions;
  private FreeMindMain freeMindMain;
 
  public IconSelectionPopupDialog(JFrame caller, Vector icons, Vector descriptions, FreeMindMain freeMindMain){

	super(caller, "select icon");
	getContentPane().setLayout(new BorderLayout());
		this.descriptions = descriptions;
	this.freeMindMain = freeMindMain;
	this.icons = icons;

	setDefaultCloseOperation(
		JDialog.DO_NOTHING_ON_CLOSE);
	addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent we) {
			close();
		}
	});

	//we will build a button-matrix which is closest to quadratical
	numOfIcons = icons.size();
	xDimension = new Double(Math.ceil(Math.sqrt(numOfIcons))).intValue();
	if (numOfIcons  <= xDimension * (xDimension-1))
		yDimension = xDimension - 1;
	else yDimension = xDimension;
 
	GridLayout gridlayout = new GridLayout(0, xDimension);
	gridlayout.setHgap(3);
	gridlayout.setVgap(3);
	iconPanel.setLayout(gridlayout);

	  iconLabels = new JLabel[numOfIcons];
	  for (int i=0; i<numOfIcons; ++i) {          
		iconPanel.add(iconLabels[i] = new JLabel((Icon)icons.get(i)));
		iconLabels[i].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		iconLabels[i].addMouseListener(this);
	  }
      
	  int perIconSize = 27;
	  iconPanel.setPreferredSize(new Dimension(xDimension * perIconSize, yDimension * perIconSize));
	  iconPanel.setMinimumSize(new Dimension(xDimension * perIconSize, yDimension * perIconSize));
	  iconPanel.setMaximumSize(new Dimension(xDimension * perIconSize, yDimension * perIconSize));
	  iconPanel.setSize(new Dimension(xDimension * perIconSize, yDimension * perIconSize));

	getContentPane().add(iconPanel, BorderLayout.CENTER);
	descriptionLabel = new JLabel(" ");
	//descriptionLabel.setEnabled(false);
	getContentPane().add(descriptionLabel, BorderLayout.SOUTH);
	setSelectedPosition(lastPosition);
	select(getSelectedPosition());
	addKeyListener(this);
	  pack();
  }

  private boolean canSelect(Position position){
	return ((position.getX() >= 0) && (position.getX() < xDimension) && (position.getY() >= 0) && (position.getY() < yDimension) && (calculateIndex(position) < numOfIcons));
  }

  private int calculateIndex(Position position){
	return position.getY()*xDimension + position.getX();
  }
  
  private Position getPosition (JLabel caller){
	int index = 0;
	for (index = 0; index < iconLabels.length; index++){
		if (caller == iconLabels[index]) break;
	}
	return new Position(index%xDimension, index/xDimension);
  }
	
  private void setSelectedPosition(Position position){
	selected = position;
	lastPosition = position;
  }
  
  private Position getSelectedPosition(){
	return selected;
  }
  

  private void select(Position position){
	unhighlight(getSelectedPosition());
	setSelectedPosition(position);
	highlight(position);
	descriptionLabel.setText((String)descriptions.get(calculateIndex(position)));
  }
  
  private void unhighlight(Position position){
	iconLabels[calculateIndex(position)].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
  }
  
  private void highlight(Position position){
	iconLabels[calculateIndex(position)].setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
  }

  private void cursorLeft(){
	Position newPosition = new Position(getSelectedPosition().getX()-1, getSelectedPosition().getY());
	if(canSelect(newPosition))
		select(newPosition);
  }

  private void cursorRight(){
	Position newPosition = new Position(getSelectedPosition().getX()+1, getSelectedPosition().getY());
	if(canSelect(newPosition))
			select(newPosition);
  }
  private void cursorUp(){
	Position newPosition = new Position(getSelectedPosition().getX(), getSelectedPosition().getY()-1);
	if(canSelect(newPosition))
			select(newPosition);
  }

  private void cursorDown(){
	Position newPosition = new Position(getSelectedPosition().getX(), getSelectedPosition().getY()+1);
	if(canSelect(newPosition))
			select(newPosition);
  }


  private void addIcon(){
	result = calculateIndex(getSelectedPosition());
	this.dispose();
  }

	public int getResult() {
		return result;
	}

  /* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent arg0) {
		switch (arg0.getKeyCode()){
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_KP_RIGHT:
				cursorRight();
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_KP_LEFT:
				cursorLeft();
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_KP_DOWN:
				cursorDown();
				break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_KP_UP:
				cursorUp();
				break;
			case KeyEvent.VK_ESCAPE:
				arg0.consume();
                close();
				break;
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_SPACE:
				arg0.consume();
				addIcon();
				break;
		}
	}

    private void close() {
        result = -1;
        this.dispose();
    }

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		addIcon();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		select(getPosition((JLabel)arg0.getSource()));
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
	}

	static class Position{
		private int x, y;
		public Position (int x, int y){
			this.x = x;
			this.y = y;
		}
		/**
		 * @return Returns the x.
		 */
		public int getX() {
			return x;
		}
		/**
		 * @return Returns the y.
		 */
		public int getY() {
			return y;
		}
		
		public String toString(){
			return ("("+getX()+","+getY()+")");
		}
	}
}
