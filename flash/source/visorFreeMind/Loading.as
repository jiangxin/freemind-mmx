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

class visorFreeMind.Loading {
	
	var cont_load:MovieClip;
	var browser:Browser;
	var mainColor;
	
	function show(){
		cont_load._x=(Stage.width-40)/2;
		cont_load._y=(Stage.height-cont_load._height)/2;
		getMainColor();
		cont_load._visible=true;
		Mouse.hide();
	}
	
	function hide(){
		cont_load._visible=false;
		Mouse.show();
	}
	
	function Loading(browser,father){
		createLoadingWindow(browser,father);
	}
	
	function createLoadingWindow(browser,father){
		this.browser=browser;
		cont_load=father.createEmptyMovieClip("loading",12);
		cont_load._visible=false;
		cont_load.loading=this;
		cont_load.pos=0;
		cont_load.onEnterFrame=function(){
			if(this._visible==true){
				this.clear();
				var x=0;
				for(var i=0;i<5;i++){
					if(i==this.pos)
						this.loading.drawCircle(x,100);
					else
						this.loading.drawCircle(x,50);
					x+=22;
				}
				this.pos=(this.pos+1)%5;
			}
		}
	}

	function drawCircle(x,alfa){
		cont_load.lineStyle(16,mainColor,alfa);
		cont_load.moveTo(x+0,0);
		cont_load.lineTo(x+1,0);
	}
	
	function getMainColor(){
		var color=browser.floor.getBackgroundColor();
		var nRed = (color >> 16)-0x33;
		nRed=nRed>=0?nRed:0;
		var nGreen= ((color >> 8) & 0xff)-0x33;
		nGreen=nGreen>=0?nGreen:0;
		var nBlue= (color & 0xff)-0x33;
		nBlue=nBlue>=0?nBlue:0;
		this.mainColor=(nRed<<16 | nGreen<<8 |nBlue);

	}
}