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
 
class visorFreeMind.SearchManager {
	
	public var container:MovieClip;
	public var button:MovieClip;
	public var browser:Browser;
	
	public var width:Number=100;
	public var input:TextField;
	public var pressedOnce=undefined;
	private var despx=8;
	private var despy=8;
	private var searchModified:Boolean=true;
	private var sList:Array=new Array();
	private var sPos:Number=0;
	
	function SearchManager(browser:Browser){
		this.browser=browser;
		var father=browser.mc_container;
		container = father.createEmptyMovieClip("contBitmaps", father.getNextHighestDepth());
		container._visible=false;
		button = container.createEmptyMovieClip("button",container.getNextHighestDepth());
		button.inst=this;
		container._x=20;
		container._y=20;
		container .createTextField( "input" , container.getNextHighestDepth() , 6+despx , 5+despy , width , 20 );
		input=container.input;
		input.inst=this;
		input.border=true;
		input.borderColor=0x888888;
		input.backgroundColor=0xffffff;
		input.background=true;
		input.type="input";
		input.textHeight=30;
		var format= new TextFormat();
		format.font = "Arial";
		format.color = 0x555555;
		format.size = 16;
		format.underline = false;
		
		input.setNewTextFormat(format);
		input.onSetFocus=function (){
			this.focoused=true;
		};
		input.onKillFocus=function (){
			this.focoused=undefined;
		};
		
		input.onChanged=function(){
			this.inst.searchModified=true;
		}
		
		Key.addListener(this);
		
		//round_rectangle2(width+14+18,input._height+12,0xaaaaaa,80,0x556677);
		drawButton();
		button._x=width+18+despx;
		button._y=15+despy;
		
		button.onPress=function(){
			this.inst.search();
		}
		
	}
	
	public function reset(){
		sList=new Array();
		searchModified=true;
	}
	
	public function drawHidePixel(){
		container.lineStyle(1,0xFF0000,0);
		container.moveTo(0,0);
		container.lineTo(1,1);
	}
	
	public function drawButton(){
		button.lineStyle(17,0x77cc77,100);
		button.moveTo(0,0);
		button.lineTo(1,0);
		button.lineStyle(16,0x99ee99,80);
		button.moveTo(0,0);
		button.lineTo(1,0);
		
		button.lineStyle(3,0xFFFFFF,90);
		button.moveTo(0,-3);
		button.lineTo(-4,0);
		button.lineTo(0,3);
		button.moveTo(-4,0);
		button.lineTo(5,0);
	}
	
    public function onKeyDown() {
		  if (Key.getCode() == Key.ENTER
		      && pressedOnce == undefined
		      && input.focoused) {
		    pressedOnce = true;
		    search();
		  }
	}
		
 	public function onKeyUp() {
		  if (Key.getCode() == Key.ENTER) {
		    pressedOnce = undefined;
		  }
	}
		
	public function search(){
		if(searchModified){
			sList=this.browser.search(input.text,false);
			sPos=0;
			searchModified=false;
		}
		if(sList.length>0){
			if(sPos>=sList.length) sPos=0;
			this.browser.unfoldLocalLink(sList[sPos]);
			this.browser.selectNode(sList[sPos]);
			
			sPos++;
		}
	}
	
	function show(mc:MovieClip,mainColor){
		container._x=mc._x-despx;
		container._y=mc._y-despy;
		//not very fine reposition
		container.reposObjForViewing(despx,despy);
		container._visible=true;
		container.clear();
		drawHidePixel();
		round_rectangle2(width+14+18,input._height+12,mainColor,80,0x556677);
		container.onEnterFrame=function(){
			if(this.hitTest(_root._xmouse,_root._ymouse,false)){
	
			}else{
				this._visible=false;
				this.onEnterFrame=undefined;
			}
		}
	}
	
	function hide(){
		this.container._visible=false;
		this.container.onEnterFrame=undefined;
	}
	
	private function round_rectangle2(w:Number,h:Number,color:Number,alpha:Number,colorLine:Number){
		container.lineStyle(1,colorLine,10);
		container.moveTo(despx,despy);
		container.beginFill(color,alpha);
		container.roundRect(despx,despy,w+despx,h+despy,3);
		container.endFill();
		return;		
	}

}