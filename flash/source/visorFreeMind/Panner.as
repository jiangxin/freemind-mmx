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
 
import flash.display.BitmapData;
import flash.geom.Matrix;import flash.filters.DropShadowFilter;
import visorFreeMind.*;

class visorFreeMind.Panner{

	var drawer:MovieClip;
	var browser:Browser;
	var floor:Floor;
	var cont:MovieClip;
	var frame:MovieClip;
	var pressed:Boolean=false;
	var scale:Number;
	private var despx=3;
	private var despy=3;

	
	public static var  ShotsWidth:Number=180;

	function Panner(browser:Browser){
		this.browser=browser;
		this.floor=browser.floor;
		drawer=browser.floor.getCanvas();
		var father=browser.mc_container;
		cont = father.createEmptyMovieClip("contPan", father.getNextHighestDepth());
		frame = cont.createEmptyMovieClip("frame", 10);
		cont._visible=false;
		cont.controler=this;
	}


	function show(mc:MovieClip){
		cont._visible=true;
		takeShot();
		cont._x=mc._x+despx;
		cont._y=mc._y+despy;
		cont.reposObjForViewing(despx,despy);
		
		cont._visible=true;
		cont.mc=mc;
		pressed=false;
		browser.floor.notDraggable();
		cont.onEnterFrame=function(){
			if( this.mc.hitTest(_root._xmouse,_root._ymouse,false)||
				this.hitTest(_root._xmouse,_root._ymouse,false)){
				this.controler.checkPosition();
			}else{
				this.controler.hide();
			}
		}
		cont.onPress=function(){
			this.controler.pressed=true;
	    }
		cont.onRelease=function(){
			this.controler.pressed=false;
			this.clear();		
	    }
		cont.onReleaseOut=function(){
			this.controler.pressed=false;
			this.clear();		
	    }
	}
	
	function checkPosition(){
		if(pressed){
			frame.clear();
			var bo=drawer.getBounds(drawer);
			drawer._x=Stage.width/2-(_root._xmouse-cont._x)*(drawer._xscale/100)/scale-bo.xMin*(drawer._xscale/100);
			drawer._y=Stage.height/2-(_root._ymouse-cont._y)*(drawer._yscale/100)/scale-bo.yMin*(drawer._yscale/100);
			drawPannerRectangle();
		}else{
			frame.clear();
		}
	}
	
	function hide(){
		cont._visible=false;
		cont.onEnterFrame=undefined;
		cont.onPress=undefined;
		cont.onRelease=undefined;
		cont.onReleaseOut=undefined;
		browser.floor.makeDraggable();
	}
	
	function drawPannerRectangle(){
		frame.lineStyle(1,0x666666,90);
		var hsw=scale*(100.0/drawer._xscale)*Stage.width/2;
		var hsh=scale*(100.0/drawer._xscale)*Stage.height/2;
		frame.moveTo(_root._xmouse-hsw-cont._x,_root._ymouse-hsh-cont._y);
		frame.lineTo(_root._xmouse+hsw-cont._x,_root._ymouse-hsh-cont._y);
		frame.lineTo(_root._xmouse+hsw-cont._x,_root._ymouse+hsh-cont._y);
		frame.lineTo(_root._xmouse-hsw-cont._x,_root._ymouse+hsh-cont._y);
		frame.lineTo(_root._xmouse-hsw-cont._x,_root._ymouse-hsh-cont._y);
	}
	
	function genBitMap(){
		var auxMatrix:Matrix = new Matrix();
		var myMatrix:Matrix = new Matrix();
		var bo=drawer.getBounds(drawer);
		//bo=browser.getBounds();
		trace(drawer+" "+bo.xMax+" "+bo.xMin+" "+bo.yMax+" "+bo.yMin);
		scale=ShotsWidth/(bo.xMax-bo.xMin);
		var sy=ShotsWidth/(bo.yMax-bo.yMin);
		var auxBitmapData:BitmapData =new BitmapData(ShotsWidth, ShotsWidth*scale/sy, false, 0xffffffff);
		var myBitmapData:BitmapData =new BitmapData(ShotsWidth, ShotsWidth*scale/sy, false, 0xffffffff);
		sy=scale;
		auxMatrix.translate(-bo.xMin,-bo.yMin);
		auxMatrix.scale(scale,sy);
		auxBitmapData.draw(drawer,auxMatrix,null,null,null,true);
		//this second draw is very important for obtaining a smooth image
		myBitmapData.draw(auxBitmapData,myMatrix,null,null,null,true);
		auxBitmapData.dispose();
		return myBitmapData;
	}


	function takeShot(){
			browser.prepareBounds();
			cont.attachBitmap(genBitMap(),1,"auto", true);
			createDropShadowRectangle(cont);

	}
	

	
	function createDropShadowRectangle(art:MovieClip) {
		var filter:DropShadowFilter = new DropShadowFilter(4, 45, 0x000000, .6, 6, 6, 1, 3, false, false, false);
		var filterArray:Array = new Array();
		filterArray.push(filter);
		art.filters = filterArray;
		art.filter=filter;
	}
}