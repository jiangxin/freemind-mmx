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
*	Create al the buttons used in the Browser
*/
class visorFreeMind.ButtonsCreator{
	//Buttons
	private var resizer:MovieClip;
	private var bBack:MovieClip;
	private var bForward:MovieClip;
	private var bGrow:MovieClip;
	private var bShrink:MovieClip;
	private var bFit:MovieClip;
	private var bReset:MovieClip;
	private var bShadow:MovieClip;
	private var bInfo:MovieClip;
	private var bColor:MovieClip;
	private var bHistory:MovieClip;
	private var bSearch:MovieClip;
	private var bPan:MovieClip;
	private var hidden:MovieClip;
	private var mc_Color;
	private var mc_Color_rollout;
	private var mainColor;
	public static var alfa=100;
	public static var min_alpha_buttons=60;
	public static var max_alpha_buttons=100;
	private var browser:Browser;
	public static var mc_now_over:MovieClip=null;
	public static var listFade={};
	public static var buttonsPos="top";//top|bottom
	public  static var colors=[0xFFFFFF,0xEEEEEE,0xDDDDDD,
							   0xEEFFFF,0xFFEEFF,0xFFFFEE,
								0xFFEEEE,0xEEFFEE,0xEEEEFF];

	public function ButtonsCreator(browser:Browser){
		AttributeChanger.init(browser.mc_container);
		this.browser=browser;
		resetMainColor();
		createFading(browser);
		trace("ButtonsCreator created");
	}

	private function createFading(browser:Browser){
		this.hidden=browser.mc_container.createEmptyMovieClip("hidden",7800);
		this.hidden.onEnterFrame=function(){
			//disminuimos los que esten en la tabla
			for(var objeto in ButtonsCreator.listFade){
				var mc_aux=ButtonsCreator.listFade[objeto];
				if(ButtonsCreator.mc_now_over!=mc_aux){
					if(mc_aux._alpha>ButtonsCreator.min_alpha_buttons){
						mc_aux._alpha=mc_aux._alpha-4;
					}else{
						delete ButtonsCreator.listFade[objeto];
					}
				}
			}
			
			if(ButtonsCreator.mc_now_over!=null ){
				if( ButtonsCreator.mc_now_over._alpha<ButtonsCreator.max_alpha_buttons){
				ButtonsCreator.mc_now_over._alpha=ButtonsCreator.mc_now_over._alpha+40;
				}
				ButtonsCreator.listFade[ButtonsCreator.mc_now_over._name]=ButtonsCreator.mc_now_over;
			}
		}
	}
	
	function resetAlpha(){
		resizer._alpha=min_alpha_buttons;
		bBack._alpha=min_alpha_buttons;
		bForward._alpha=min_alpha_buttons;
		bGrow._alpha=min_alpha_buttons;
		bShrink._alpha=min_alpha_buttons;
		bFit._alpha=min_alpha_buttons;
		bReset._alpha=min_alpha_buttons;
		bShadow._alpha=min_alpha_buttons;
		bInfo._alpha=min_alpha_buttons;
		bColor._alpha=min_alpha_buttons;
		bHistory._alpha=min_alpha_buttons;
		bPan._alpha=min_alpha_buttons;
		bSearch._alpha=min_alpha_buttons;
	}
	
	function resetMainColor(){
		var color=browser.floor.getBackgroundColor();
		var nRed = (color >> 16)-0x33;
		nRed=nRed>=0?nRed:0;
		var nGreen= ((color >> 8) & 0xff)-0x33;
		nGreen=nGreen>=0?nGreen:0;
		var nBlue= (color & 0xff)-0x33;
		nBlue=nBlue>=0?nBlue:0;
		this.mainColor=(nRed<<16 | nGreen<<8 |nBlue);
		//trace(color+"("+nRed+","+nGreen+","+nBlue+")->"+this.mainColor+"\n");
		createSizeButtons(browser.mc_container);
		createNavigationButtons(browser.mc_container);
		createHistoryButton(browser.mc_container);
		createSearchButton(browser.mc_container);
		if(Browser.flashVersion>=8)
			createPanButton(browser.mc_container);
		relocateAllButtons();
		addToolTipsButtons();
		resetAlpha();
	}
	
	function addToolTipsButtons(){
		var over=function(){
			this.browser.showTooltip("<p>"+this.tooltip+"</p>",14,20);
			AttributeChanger.deleteElem(this);
			this._alpha=ButtonsCreator.max_alpha_buttons;
			//AttributeChanger.addChange(this,"_rotation",120,4);
		}

		var out=function(){
			this.browser.hideTooltip();
			AttributeChanger.deleteElem(this);
			AttributeChanger.addChange(this,"_alpha",ButtonsCreator.min_alpha_buttons,30);
			//AttributeChanger.addChange(this,"_rotation",0,30);		
		}

		
		bBack.onRollOver=over;
		bBack.onRollOut=out;
		bForward.onRollOver=over;
		bForward.onRollOut=out;
		bGrow.onRollOver=over;
		bGrow.onRollOut=out;
		bShrink.onRollOver=over;
		bShrink.onRollOut=out;
		bShrink.onRollOver=over;
		bFit.onRollOut=out;		
		bFit.onRollOver=over;
		bReset.onRollOut=out;
		bReset.onRollOver=over;
		bShadow.onRollOver=over;
		bShadow.onRollOut=out;
		bInfo.onRollOver=over;
		bInfo.onRollOut=out;
		bColor.onRollOut=out;
		bHistory.onRollOut=out;
		bPan.onRollOut=out;
		bSearch.onRollOut=out;
	}

	// For resize of Stage
	function relocateAllButtons(){
		var yPos=buttonsPos=="top"?10:Stage.height-10;
		var newCenter=Stage.width/2;
		bBack._x=newCenter-80;
		bBack._y=yPos;
		bForward._x=newCenter-60;
		bForward._y=yPos;
		bGrow._x=newCenter-40;
		bGrow._y=yPos;
		bShrink._x=newCenter-20;
		bShrink._y=yPos;
		bFit._x=newCenter;
		bFit._y=yPos;
		bReset._x=newCenter+20;
		bReset._y=yPos;
		bShadow._x=newCenter+40;
		bShadow._y=yPos;
		bInfo._x=newCenter+60;
		bInfo._y=yPos;
		bColor._x=newCenter+80;
		bColor._y=yPos;
		bHistory._y=yPos;
		bHistory._x=10;
		bSearch._y=yPos;
		bSearch._x=30;
		bPan._y=yPos;
		bPan._x=50;
		createColorSelector();
	}

	function createNavigationButtons(mc_container){
		bBack=mc_container.createEmptyMovieClip("navBack",7778);
		bBack.browser=browser;
		bBack.tooltip="BACK";

		bForward=mc_container.createEmptyMovieClip("navForward",7779);
		bForward.browser=browser;
		bForward.tooltip="FORWARD";

		bBack.lineStyle(16,mainColor,alfa);
		bBack.moveTo(0,0);
		bBack.lineTo(1,0);
		bBack.lineStyle(3,0xFFFFFF,90);
		bBack.moveTo(3,-4);
		bBack.lineTo(-4,0);
		bBack.lineTo(3,4);
		bForward.lineStyle(16,mainColor,alfa);
		bForward.moveTo(0,0);
		bForward.lineTo(1,0);
		bForward.lineStyle(3,0xFFFFFF,90);
		bForward.moveTo(-3,-4);
		bForward.lineTo(4,0);
		bForward.lineTo(-3,4);
		bBack.onPress=function(){
			this.browser.historyManager.backward();
		}
		bForward.onPress=function(){
			this.browser.historyManager.forward();
		}
	}

	function createColorSelector(){
		mc_Color=browser.mc_container.createEmptyMovieClip("colSel",8000);
		var mc_ColorCanvas=createCube(mc_Color,0,0,0xFFFFFF,12,38);
		mc_Color_rollout=createCube(browser.mc_container,0,0,0xEEEEEE,7793,80);
		for(var i=0;i<9;i++){
			createCube(mc_ColorCanvas,1+(i%3)*12,1+Math.floor(i/3)*12,colors[i],i,12);
		}
		mc_Color._x=bColor._x+0;
		mc_Color._y=bColor._y+0;
		if(buttonsPos=="bottom")
			mc_Color._y=bColor._y-mc_Color._height;
		mc_Color._visible=false;
		mc_Color_rollout._x=bColor._x-15;
		mc_Color_rollout._y=bColor._y-15;
		mc_Color_rollout._visible=false;
		mc_Color_rollout._alpha=0;

		mc_Color_rollout.mc_Color=mc_Color;
		bColor.mc_Color=mc_Color;
		bColor.mc_Color_rollout=mc_Color_rollout;
		mc_Color.browser=browser;
		mc_Color.bc=this;
		bColor.onRollOver=function(){
			this.mc_Color._visible=true;
			this.mc_Color_rollout._visible=true;
			ButtonsCreator.mc_now_over=this;
			this._alpha=ButtonsCreator.max_alpha_buttons;
			ButtonsCreator.listFade[this._name]=this;
		}
		mc_Color_rollout.onRollOver=function(){
			this.mc_Color._visible=false;
			this._visible=false;
		}

		mc_Color.onPress=function(){
			//Calculamos el punto de hit
			var x=_root._xmouse-this._x-1;
			var y=_root._ymouse-this._y-1;
			var i=Math.floor(x/12)+Math.floor(y/12)*3;
			//trace("x:"+x+" y:"+y +" i: "+i);
			if(i>=0 && i<9){
				this.browser.floor.changeBgColor(ButtonsCreator.colors[i]);
				this.bc.resetMainColor();
			}
			this._visible=false;
		}

		
		mc_ColorCanvas.dropShadow(8,4,4,0x777799,mc_Color);
	}


	function createCube(mc_container,posx,posy,color,deep,side){
		var cubo=mc_container.createEmptyMovieClip("color_"+deep,deep);
		cubo.lineStyle(1,color,100);
		cubo.beginFill(color,100);
		cubo.moveTo(0,0);
		cubo.lineTo(side,0);
		cubo.lineTo(side,side);
		cubo.lineTo(0,side);
		cubo.lineTo(0,0);
		cubo.endFill();
		cubo._x=posx;
		cubo._y=posy;
		return cubo;
	}

	function drawRectangleB(mc_container:MovieClip,mainColor,alfa,alfaLine,s){
		mc_container.lineStyle(1,mainColor,alfaLine);
		mc_container.beginFill(mainColor,alfa);
		mc_container.moveTo(-s,-s);
		mc_container.lineTo(s,-s);
		mc_container.lineTo(s,s);
		mc_container.lineTo(-s,s);
		mc_container.lineTo(-s,-s);
		mc_container.endFill();
	}
	
	function createHistoryButton(mc_container:MovieClip){
		bHistory=mc_container.createEmptyMovieClip("bHistory",7796);
		bHistory.browser=browser;
		bHistory.tooltip="show history";
		drawRectangleB(bHistory,mainColor,alfa,0,8);
		bHistory.lineStyle(3,0xFFFFFF,90);
		bHistory.moveTo(-3,-4);
		bHistory.lineTo(0,4);
		bHistory.moveTo(3,-4);
		bHistory.lineTo(0,4);
		bHistory.onRollOver=function(){
			var bbox=this.getBounds(_root);
			this.browser.historyManager.pt.show(bbox.xMax,bbox.yMax);
			ButtonsCreator.mc_now_over=this;
		}
		bHistory.onPress=function(){
			this.browser.historyManager.pt.changeFold();;
		}
	}
	
	function createSearchButton(mc_container:MovieClip){
		bSearch=mc_container.createEmptyMovieClip("bSearch",7797);
		bSearch.browser=browser;
		bSearch.bCreator=this;
		bSearch.tooltip="show search dialog";
		drawRectangleB(bSearch,mainColor,alfa,0,8);
		drawRectangleB(bSearch,0xFFFFFF,90,0,4);
		bSearch.onRollOver=function(){
			this.browser.searchDialog.show(this,this.bCreator.mainColor);
			this.browser.historyManager.pt.hide();
			ButtonsCreator.mc_now_over=this;
		}
	}
	
	function createPanButton(mc_container:MovieClip){
		bPan=mc_container.createEmptyMovieClip("bPan",7799);
		bPan.browser=browser;
		bPan.bCreator=this;
		bPan.tooltip="show search dialog";
		drawRectangleB(bPan,mainColor,alfa,0,8);
		drawRectangleB(bPan,0xFFFFFF,0,90,4);
		bPan.onRollOver=function(){
			this.browser.panner.show(this);
			this.browser.historyManager.pt.hide();
			this.browser.searchDialog.hide();
			ButtonsCreator.mc_now_over=this;
		}
	}
	
	function createSizeButtons(mc_container){
		bGrow=mc_container.createEmptyMovieClip("increase",7788);
		bGrow.browser=browser;
		bGrow.tooltip="INCREASE";
		bShrink=mc_container.createEmptyMovieClip("shrink",7789);
		bShrink.browser=browser;
		bShrink.tooltip="SHRINK";
		bFit=mc_container.createEmptyMovieClip("fit",7786);
		bFit.browser=browser;
		bFit.tooltip="FIT";
		bReset=mc_container.createEmptyMovieClip("reset",7790);
		bReset.browser=browser;
		bReset.tooltip="RESET";
		bShadow=mc_container.createEmptyMovieClip("shadow",7791);
		bShadow.bc=this;
		bShadow.browser=browser;
		bShadow.tooltip="SHADOW ON";
		bInfo=mc_container.createEmptyMovieClip("bInfo",7792);
		bInfo.browser=browser;
		bInfo.tooltip="<p><b>This is a free</b> FREEMIND BROWSER v1.0b\n<b>shortcuts</b>\n"+
			"LEFT : move left\n"+
			"RIGHT : move right\n"+
			"UP : move up\n"+
			"DOWN : move down\n"+
			"CTRL LEFT : back history\n"+
			"CTRL RIGHT : forward history\n"+
			"CTRL '+' : increase\n"+
			"CTRL '-' : shrink\n"+
			"CTRL 'c' : node to clipboard\n"+
			"CTRL + Lmouse : unfold linked</p>";
		bColor=mc_container.createEmptyMovieClip("bColor",7794);
		bColor.browser=browser;
		bColor.tooltip="change background color";

		bGrow.lineStyle(16,mainColor,alfa);
		bGrow.moveTo(0,0);
		bGrow.lineTo(1,0);
		bGrow.lineStyle(3,0xFFFFFF,90);
		bGrow.moveTo(0,-4);
		bGrow.lineTo(0,4);
		bGrow.moveTo(-4,0);
		bGrow.lineTo(4,0);
		bShrink.lineStyle(16,mainColor,alfa);
		bShrink.moveTo(0,0);
		bShrink.lineTo(1,0);
		bShrink.lineStyle(3,0xFFFFFF,90);
		bShrink.moveTo(-4,0);
		bShrink.lineTo(4,0);

		bFit.lineStyle(16,mainColor,alfa);
		bFit.moveTo(0,0);
		bFit.lineTo(1,0);
		drawRectangleB(bFit,0xFFFFFF,90,0,4);


		bReset.lineStyle(16,mainColor,alfa);
		bReset.moveTo(0,0);
		bReset.lineTo(1,0);
		bReset.lineStyle(8,0xFFFFFF,90);
		bReset.moveTo(0,0);
		bReset.lineTo(1,0);

		bShadow.lineStyle(16,mainColor,alfa);
		bShadow.moveTo(0,0);
		bShadow.lineTo(1,0);
		bShadow.lineStyle(14,0xFFFFFF,90);
		bShadow.moveTo(0,0);
		bShadow.lineTo(1,0);
		if(Browser.getStaticAtr("withShadow",false)){
			bShadow.lineStyle(10,mainColor,alfa);
			bShadow.moveTo(0,0);
			bShadow.lineTo(1,0);
			bShadow.tooltip="SHADOW OFF";
		}
		
		bInfo.lineStyle(16,mainColor,alfa);
		bInfo.moveTo(0,0);
		bInfo.lineTo(1,0);
		bInfo.lineStyle(3,0xFFFFFF,90);
		bInfo.moveTo(0,-4);
		bInfo.lineTo(0,-3.8);
		bInfo.moveTo(0,0);
		bInfo.lineTo(0,5);

		bColor.lineStyle(16,mainColor,alfa);
		bColor.moveTo(0,0);
		bColor.lineTo(1,0);


		bGrow.onPress=function(){
			/*
			for(var i=0;i<this.browser.listNodesR.length;i++){
			this.browser.listNodesR[i].delShadow();
			}
			*/
			this.browser.upscale();
			//this.browser.genMindMap(3);
			/*
			for(var i=0;i<this.browser.listNodesR.length;i++){
			this.browser.listNodesR[i].genShadow();
			}
			*/
		}

		bShrink.onPress=function(){
			this.browser.downscale();
		}

		bFit.onPress=function(){
			this.browser.fitMindMap();
		}
		
		bReset.onPress=function(){
			this.browser.mc_floor._xscale=100;
			this.browser.mc_floor._yscale=100;
			this.browser.mc_container.tooltip._xscale=100;
			this.browser.mc_container.tooltip._yscale=100;
			//Center View
			this.browser.initialization=true;
			//this.browser.relocateFloor();
			this.browser.relocateMindMap();
		}

		bShadow.onPress=function(){
			this.clear();
			this.lineStyle(16,this.bc.mainColor,100);
			this.moveTo(0,0);
			this.lineTo(1,0);
			this.lineStyle(14,0xFFFFFF,90);
			this.moveTo(0,0);
			this.lineTo(1,0);
			this.browser.withShadow= this.browser.withShadow?false:true;
			Browser.setStaticAtr("withShadow",this.browser.withShadow);
			trace("withShadow:"+Browser.getStaticAtr("withShadow","hi"));
			if(this.browser.withShadow==true){
				this.lineStyle(10,this.bc.mainColor,100);
				this.moveTo(0,0);
				this.lineTo(1,0);
				this.tooltip="SHADOW OFF";
			}else{
				this.tooltip="SHADOW ON";
			}
			this.browser.genMindMap(3);;
		}
	}
}
