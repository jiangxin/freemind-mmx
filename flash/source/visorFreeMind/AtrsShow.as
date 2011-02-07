/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin, Juan Pedro and others.
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

class visorFreeMind.AtrsShow {
	
	private static var tf:TextFormat = new TextFormat();
	private static var initialized:Boolean=false;
	private static var mc:MovieClip=null;
	private static var defaultSize:Number=11;
	
	public static function showAtrs(node:Node,browser:Browser){
		if(mc==null)
		  mc=getAtrsShow(node,browser);
		  var sombra=mc.createEmptyMovieClip("sombra",1);
		  mc.dropShadow(8,4,4,0x777799,sombra);
		mc._x=_root._xmouse+14;
		mc._y=_root._ymouse+20;
		reposObjForViewing(mc);
	}
 
 	public static function hideAtrs(node:Node,browser:Browser){
 		if(mc)
 			mc.removeMovieClip();
 		mc=null;
 	}
 		public static function getAtrsShow(node:Node,browser:Browser){
		init(browser);
		var mc:MovieClip=browser.mc_container.createEmptyMovieClip("atrShow",16);
		var mcAtr:MovieClip=mc.createEmptyMovieClip("atrs",3);
		//Icons.get_gohome(mcAtr,20);
		var mcValues=mc.createEmptyMovieClip("values",4);
		var mcLineas=mc.createEmptyMovieClip("lineas",5);
		//Icons.get_gohome(mcValues,20);
		var latrs:Array=node.getAtributes();
		var despy=0;
		mcAtr.createTextField("name",0,0,despy,0,0);
		var atrName:TextField=mcAtr["name"];
		mcValues.createTextField("name",0,0,despy,0,0);
		var atrValue:TextField=mcValues["name"];
		atrName.autoSize=true;
		atrName.background=true;
		atrName.border=true;

		atrName.backgroundColor=0xFFDD88;
		atrValue.autoSize=true;
		atrValue.background=true;
		atrValue.border=true;
		atrValue.backgroundColor=0xFFe9a5;
		var ret="";
		var i=0;
		for(i=0;i<latrs.length;i++){
			var atr:XMLNode=latrs[i];
			atrName.text+=ret+atr.attributes.NAME;
			atrName.setTextFormat(tf);
			atrValue.text+=ret+atr.attributes.VALUE;
			atrValue.setTextFormat(tf);
			ret="\n";
		}
		mcValues._x=mcAtr._width;
		
		mcLineas.lineStyle(1,0x00000,20);
		for(var j=1;j<i;j++){
			mcLineas.moveTo(1,j*mc._height/i);
			mcLineas.lineTo(mc._width-1,j*mc._height/i);
		}
		return mc;
	}
	
	private static function init(browser:Browser){
		//if(initialized)return;
		tf.color=0x444444;
		tf.font="arial";
		tf.size=Math.floor(defaultSize*browser.mc_floor._xscale/100);
		tf.bold=false;
		tf.italic=false;
		initialized=true;
	}
	
		private static function reposObjForViewing(tt){
		var bbox=tt.getBounds(_root);
		//trace(tt._x+" "+bbox.xMax+" "+Stage.width);
		if(bbox.xMax>Stage.width){
			var newval=Stage.width-bbox.xMax;
			tt._x+=newval;
			//trace("new x:"+tt._x+" newval:"+newval);
		}
		if(bbox.yMax>Stage.height){
			var newval=Stage.height-bbox.yMax;
			tt._y+=newval;
			//trace("new x:"+tt._x+" newval:"+newval);
		}
	}
}