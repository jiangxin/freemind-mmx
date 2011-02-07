/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
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
 * Created on 25.04.2005
 */
/**
* 	NOT USED YET
*/

class Menu {
	private _visible:Boolean=true;

	function generateMenu( container,name, x, y, depth, node_xml) {
	// variable declarations
		var curr_node;
		var curr_item;
		var curr_menu = container.createEmptyMovieClip(name, depth);

		// for all items or XML nodes (items and menus)
		// within this node_xml passed for this menu
		for (var i=0; i<node_xml.childNodes.length; i++) {
			// movieclip for each menu item
			curr_item = curr_menu.attachMovie("menuitem","item"+i+"_mc", i);
			curr_item._x = x;
			curr_item._y = y + i*curr_item._height;
			curr_item.trackAsMenu = true;

			// item properties assigned from XML
			curr_node = node_xml.childNodes[i];
			curr_item.action = curr_node.attributes.action;
			curr_item.variables = curr_node.attributes.variables;
			curr_item.name.text = curr_node.attributes.name;

			// item submenu behavior for rollover event
			if (node_xml.childNodes[i].nodeName == "menu"){
				// open a submenu
				curr_item.node_xml = curr_node;
				curr_item.onRollOver = curr_item.onDragOver = function(){
					var x = this._x + this._width - 5;
					var y = this._y + 5;
					generateMenu(curr_menu, "submenu_mc", x, y, 1000, this.node_xml);
					// show a hover color
					var col = new Color(this.background);
					col.setRGB(0xf4faff);
				};
			}else{ // nodeName == "item"
				curr_item.arrow._visible = false;
				// close existing submenu
				curr_item.onRollOver = curr_item.onDragOver = function(){
					curr_menu.submenu_mc.removeMovieClip();
					// show a hover color
					var col = new Color(this.background);
					col.setRGB(0xf4faff);
				};
			}

			curr_item.onRollOut = curr_item.onDragOut = function(){
				// restore color
				var col = new Color(this.background);
				col.setTransform({ra:100,rb:0,ga:100,gb:0,ba:100,bb:0});
			};

			// any item, menu opening or not can have actions
			curr_item.onRelease = function(){
				Actions[this.action](this.variables);
				CloseSubmenus();
			};
		} // end for loop
	}

	// create the main menu
	function Menu(x, y, depth, menu_xml,visible){
		// generate a menu list
		_visible=visible
		GenerateMenu(this,"mainmenu_mc", x, y, depth, menu_xml.firstChild);
		// close only submenus if visible durring a mouseup
		// this main menu (mainmenu_mc) will remain
		mainmenu_mc.onMouseUp = function(){
			if (mainmenu_mc.submenu_mc &&
				!mainmenu_mc.hitTest(_root._xmouse, _root._ymouse, true)){
				CloseSubmenus();
			}
	}
}

// closes all submenus by removing the submenu_mc
// in the main menu (if it exists)
CloseSubmenus = function(){
	mainmenu_mc.submenu_mc.removeMovieClip();
};

// This actions object handles methods for actions
// defined by the XML called when a menu item is pressed
Actions = Object();
Actions.gotoURL = function(urlVar){
	getURL(urlVar, "_blank");
};
Actions.message = function(msg){
	message_txt.text = msg;
};
Actions.newMenu = function(menuxml){
	menu_xml.load(menuxml);
};





// load XML, when done, run CreateMainMenu to interpret it
menu_xml = new XML();
menu_xml.ignoreWhite = true;
menu_xml.onLoad = function(ok){
	// create main menu after successful loading of XML
	if (ok){
		CreateMainMenu(10, 10, 0, this);
		message_txt.text = "message area";
	}else{
		message_txt.text = "error:  XML not successfully loaded";
	}
};
// load first XML menu
menu_xml.load("menu1.xml");
