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
import visorFreeMind.*;
/**
* Main Class
* Author : Juan Pedro de Andres
* This class is configured to be the called with MSTAC (Motion-Twin ActionScript 2 Compiler)
*/
class visorFreeMind.Main {
		
		static var browser:Browser;

		static var initialized=false;
		
		public static function main():Void{
				_root.onEnterFrame = function()
				{
					Main.run();
				};
		}
		
		static function redefineRightMenu(){
			var mycm=new ContextMenu();
			//mycm.hideBuiltInItems();
			mycm.onSelect = visorFreeMind.Main.copyInfoNodeOver;
			var copy=new ContextMenuItem("move back",visorFreeMind.Main.backward);
			mycm.customItems.push(copy);
			copy=new ContextMenuItem("move forward",visorFreeMind.Main.forward);
			mycm.customItems.push(copy);
			copy=new ContextMenuItem("copy to clipboard",visorFreeMind.Main.getNodeText);
			mycm.customItems.push(copy);
			copy=new ContextMenuItem("unfold all from Node",visorFreeMind.Main.unfoldAllFromNode);
			mycm.customItems.push(copy);
			copy=new ContextMenuItem("fold all from Node",visorFreeMind.Main.foldAllFromNode);
			mycm.customItems.push(copy);
			if(Browser.flashVersion>7){
				copy=new ContextMenuItem("gen shots for linked maps",visorFreeMind.Main.genShotsForLinkedMaps);
				mycm.customItems.push(copy);
			}
			copy=new ContextMenuItem("FREEMIND BROWSER v1.0b",visorFreeMind.Main.nada);
			mycm.customItems.push(copy);
			mycm.hideBuiltInItems();
			_root.menu = mycm;
		}

		static function nada(){
		}
		
		static function genShotsForLinkedMaps(){
			browser.historyManager.genShotsForLinkedMaps();
		}
		
		static function unfoldAllFromNode(){
			browser.unfoldAllFromNode();
		}
		
		static function foldAllFromNode(){
			browser.foldAllFromNode();
		}
		
		static function backward(){
			browser.historyManager.backward();
		}
		
		static function forward(){
			browser.historyManager.forward();
		}		
		
		static function copyInfoNodeOver(){
			Node.saveTxt();
			if(Node.lastOverTxt=="")
				_root.menu.customItems[2].enabled = false;
			else
				_root.menu.customItems[2].enabled = true;
		}
		
		static 	function getNodeText(){
				System.useCodepage = true;
				System.setClipboard(Node.lastOverTxt);
				System.useCodepage = false;
		}
		
		
		static public function run():Boolean
	   {
	   	   if(initialized==true)return true;
	   	   else initialized=true;
	   	   Flashout.init();
		   trace("Starting flash FreeMind Browser",2);

			// set the Flash movie to have a fixed anchor
		    // in the top left corner of the screen.
			Stage.align = "LT";

			// prevent the Flash movie from resizing when the browser window
		    // changes size.
			Stage.scaleMode = "noScale";

		    // tell the Macromedia Flash Player 6 to use the traditional code page
		    // of the operating system running the player
			System.useCodepage = false;

		   // If not defined init mindmap file, use default (index.mm)
		   if(_root.openUrl!=null)
		   		Node.openUrl=_root.openUrl;
		   if(_root.noElipseMode!=null)
		   		Edge.elipseMode=false;
		   if(_root.genAllShots!=null)
		   		HistoryManager.genAllShots=Boolean(_root.genAllShots.toLowerCase()=="true");
		   if(_root.unfoldAll!=null)
		   		Browser.unfoldAll=Boolean(_root.unfoldAll.toLowerCase()=="true");
		   if(_root.justMap!=null)
		   		Browser.justMap=Boolean(_root.justMap.toLowerCase()=="true");
		   if(_root.scaleTooltips!=null)
		   		Browser.scaleTooltips=Boolean(_root.scaleTooltips.toLowerCase()=="true");
		   if(_root.toolTipsBgColor!=null)
		   		Browser.toolTipsBgColor=Number(_root.toolTipsBgColor);
		   if(!isNaN(_root.defaultWordWrap))
		   		Node.defaultWordWrap=Number(_root.defaultWordWrap);
		   if(!isNaN(_root.defaultToolTipWordWrap))
		   		Browser.defaultToolTipWordWrap=Number(_root.defaultToolTipWordWrap);
		   if(_root.offsetX!=null && (_root.offsetX=="left" ||_root.offsetX=="left" || !isNaN(_root.offsetX)) )
		   		Browser.offsetX=_root.offsetX;
		   if(_root.offsetY!=null && (_root.offsetY=="top" ||_root.offsetY=="bottom" || !isNaN(_root.offsetY)) )
		   		Browser.offsetY=_root.offsetY;
		   if(_root.buttonsPos!=null && (_root.buttonsPos=="top" ||_root.buttonsPos=="bottom" ) )
		   		ButtonsCreator.buttonsPos=_root.buttonsPos;
		   if(!isNaN(_root.max_alpha_buttons))
		   		ButtonsCreator.max_alpha_buttons=Number(_root.max_alpha_buttons);
		   if(!isNaN(_root.min_alpha_buttons))
		   		ButtonsCreator.min_alpha_buttons=Number(_root.min_alpha_buttons);
		   if(!isNaN(_root.startCollapsedToLevel))
		   		Browser.startCollapsedToLevel=Number(_root.startCollapsedToLevel);
		   if(_root.mainNodeShape=="rectangle" || _root.mainNodeShape=="none")
		   		Node.mainNodeShape=_root.mainNodeShape;
		   if(!isNaN(_root.ShotsWidth))
		   		PictureTaker.ShotsWidth=Number(_root.ShotsWidth);
		   if(_root.baseImagePath!=null)
		   		Node.baseImagePath=_root.baseImagePath;
		   if(_root.CSSFile!=null)
		   		Browser.CSSFile=_root.CSSFile;
		   if(_root.initLoadFile!=null){
				browser=new Browser(_root.initLoadFile,_root);
		   }
			else{
				browser=new Browser("index.mm",_root);
			}

		    redefineRightMenu();

			return true;
		}

}
