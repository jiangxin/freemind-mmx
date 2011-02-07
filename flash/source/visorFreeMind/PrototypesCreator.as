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
* Here we create all the prototypes used by the browser
*/
class visorFreeMind.PrototypesCreator{
	private static var initiated:Boolean=false;

	// Just can be initialized one time
	public static function init(){
		if(!initiated){
			createPrototypes();
		}
	}

	//Prototipes used in the applicatión are defined here.
	private static function createPrototypes(){

		/**
		* Draw a dashed line from starting point s_x,s_x a distance d_x,d_y
		* with a spaceLength inside the dashLength, and a thickness
		*/
		MovieClip.prototype.dashTo = function(s_x,s_y, d_x,d_y, dashLength, spaceLength,thickness) {
				if(thickness!=0){
					dashLength=dashLength*thickness;
					spaceLength=spaceLength*thickness;
				}
				var x = d_x - s_x;
				var y = d_y - s_y;
				var hyp = Math.sqrt((x)*(x) + (y)*(y));
				var units = hyp/(dashLength+spaceLength);
				var dashSpaceRatio = dashLength/(dashLength+spaceLength);
				var dashX = (x/units)*dashSpaceRatio;
				var spaceX = (x/units)-dashX;
				var dashY = (y/units)*dashSpaceRatio;
				var spaceY = (y/units)-dashY;

				this.moveTo(s_x, s_y);
				while (hyp > 0) {
						s_x += dashX;
						s_y += dashY;
						hyp -= dashLength;
						if (hyp < 0) {
								s_x = d_x;
								s_y = d_y;
						}
						this.lineTo(s_x, s_y);
						s_x += spaceX;
						s_y += spaceY;
						this.moveTo(s_x, s_y);
						hyp -= spaceLength;
				}
				this.moveTo(d_x, d_y);
		};

		// Not used yet, shadow has to be modified to work ok.
		MovieClip.prototype.roundRect = function(x1, y1, x2, y2, r) {
			r = Math.min(Math.abs(r), Math.min(Math.abs(x1-x2), Math.abs(y1-y2))/2);
			var f = 0.707106781186548*r;
			var a = 0.588186525863094*r;
			var b = 0.00579432557070009*r;
			var ux = Math.min(x1, x2);
			var uy = Math.min(y1, y2);
			var lx = Math.max(x1, x2);
			var ly = Math.max(y1, y2);
			this.moveTo(ux+r, uy);
			var cx = lx-r;
			var cy = uy+r;
			this.lineTo(cx, uy);
			this.curveTo(lx-a, uy+b, cx+f, cy-f);
			this.curveTo(lx-b, uy+a, lx, uy+r);
			cy = ly-r;
			this.lineTo(lx, cy);
			this.curveTo(lx-b, ly-a, cx+f, cy+f);
			this.curveTo(lx-a, ly-b, lx-r, ly);
			cx = ux+r;
			this.lineTo(cx, ly);
			this.curveTo(ux+a, ly-b, cx-f, cy+f);
			this.curveTo(ux-b, ly-a, ux, ly-r);
			cy = uy+r;
			this.lineTo(ux, cy);
			this.curveTo(ux+b, uy+a, cx-f, cy-f);
			this.curveTo(ux+a, uy+b, ux+r, uy);
		};

		MovieClip.prototype.blurredRect = function(x, y, width, height, blur, color, alpha) {
			this.lineStyle();
			var f = [];
			var sum = 0;
			var b=0;
			var factor;
			for (var i = 1; i<blur+1; i++) {
				f[i-1] = i*i;
				sum += f[i-1];
			}
			var newfactor= 2;
			var counter = 40;
			do {
				factor=newfactor
				b = 0;
				for (var i = 0; i<=blur; i++) {
					var ftemp = (f[i]*(factor*alpha)/sum)/100;
					b = b*(1-ftemp)+ftemp;
				}
				counter--;
				newfactor *= alpha/(100*b);
			} while ((counter>0) && (Math.abs(100*b-alpha)>.5));
			for (var i = 0; i<=blur; i++) {
				f[i] *= (factor*alpha)/sum;
			}
			for (var i = 0; i<=blur; i++) {
				this.beginFill(color, f[i]);
				this.roundRect(1+(x+i)-blur/2, 1+(y+i)-blur/2, x+width-i+blur/2-1, y+height-i+blur/2-1, blur-(i*2/3));
				this.endFill();
			}
		};
		/**
		* This prototype is based in the implementation of:  ( Search I don´t remember now)
		*	offsetX:
		*	offsetY:
		*	color: Color of the shadow
		*	container: where the shadow it is going to be drawn
		*/
		MovieClip.prototype.dropShadow=function(spread,offsetX,offsetY,color,container){
		this.spread=spread;
		this.col=color;
		this.sidesColors = [0xFFFFFF, this.col];
		this.cornerColors = [this.col,0xFFFFFF];
		this.ratios = [0, 255];
		var sh=container;
		this.sidesAlphas = [0, 100];
		this.cornerAlphas = [100, 0];
		//this.swapDepths(sh);
		this.bounds=this.getBounds(this);
		this.x=this.bounds.xMin;
		this.y=this.bounds.yMin;
		this.bounds=this.getBounds(_root);
		this.w=this.bounds.xMax-this.bounds.xMin
		this.h=this.bounds.yMax-this.bounds.yMin-5;
		var b=sh.createEmptyMovieClip("InnerBox_mc", 1);
		b.bounds=this.getBounds(b);
		b.x=b.bounds.xMin+offsetX;
		b.y=b.bounds.yMin+offsetY;
		b.w=(b.bounds.xMax-b.bounds.xMin)-spread/2;
		b.h=(b.bounds.yMax-b.bounds.yMin)-spread/2;
		b.lineStyle(1, this.col, 0);
		b.beginFill(this.col,0);
		b.drawRect(b.x,b.y,this.w,this.h);
		b.endFill();

		var sl=sh.createEmptyMovieClip(this._name+"SideL_mc", 2);
		this.matrix = {matrixType:"box", x:b.x-this.spread, y:b.y, w:this.spread, h:b.h, r:0};
		sl.lineStyle(0, this.col, 0);
		sl.beginGradientFill("linear", this.sidesColors, this.sidesAlphas, this.ratios, this.matrix);
		sl.drawRect(b.x-this.spread, b.y, this.spread, b.h);
		sl.endFill();
		var st=sh.createEmptyMovieClip(this._name+"SideT_mc", 3);
		this.matrix = {matrixType:"box", x:b.x, y:b.y-this.spread, w:b.w, h:this.spread, r:Math.PI/2};
		st.lineStyle(0, this.col, 0);
		st.beginGradientFill("linear", this.sidesColors, this.sidesAlphas, this.ratios, this.matrix);
		st.drawRect(b.x, b.y-this.spread, b.w, this.spread);
		st.endFill();

		var sr=sh.createEmptyMovieClip(this._name+"SideR_mc", 4);
		this.matrix = {matrixType:"box", x:b.x+b.w, y:b.y, w:this.spread, h:b.h, r:Math.PI};
		sr.lineStyle(0, this.col, 0);
		sr.beginGradientFill("linear", this.sidesColors, this.sidesAlphas, this.ratios, this.matrix);
		sr.drawRect(b.x+b.w, b.y, this.spread, b.h);
		sr.endFill();
		var sb=sh.createEmptyMovieClip(this._name+"SideB_mc", 5);
		this.matrix = {matrixType:"box", x:b.x, y:b.y+b.h, w:b.w, h:this.spread, r:-Math.PI/2};
		sb.lineStyle(0, this.col, 0);
		sb.beginGradientFill("linear", this.sidesColors, this.sidesAlphas, this.ratios, this.matrix);
		sb.drawRect(b.x, b.y+b.h, b.w, this.spread);
		sb.endFill();

		var clt=sh.createEmptyMovieClip(this._name+"CornerLT_mc", 6);
		this.matrix = {matrixType:"box", x:b.x-this.spread, y:b.y-this.spread, w:this.spread*2, h:this.spread*2, r:0};
		clt.lineStyle(0, this.col, 0);
		clt.beginGradientFill("radial", this.cornerColors, this.cornerAlphas, this.ratios, this.matrix);
		clt.moveTo(b.x-this.spread, b.y);
		clt.curveTo(b.x-this.spread, b.y-this.spread, b.x, b.y-this.spread);
		clt.moveTo(b.x-this.spread, b.y);
		clt.lineTo(b.x, b.y);
		clt.lineTo(b.x, b.y-this.spread);
		clt.endFill();

		var crt=sh.createEmptyMovieClip(this._name+"CornerRT_mc", 7);
		this.matrix = {matrixType:"box", x:b.x+b.w-this.spread, y:b.y-this.spread, w:this.spread*2, h:this.spread*2, r:0};
		crt.lineStyle(0, this.col, 0);
		crt.beginGradientFill("radial", this.cornerColors, this.cornerAlphas, this.ratios, this.matrix);
		crt.moveTo(b.x+b.w, b.y-this.spread);
		crt.curveTo(b.x+b.w+this.spread, b.y-this.spread, b.x+b.w+this.spread, b.y);
		crt.lineTo(b.x+b.w, b.y, b.y);
		crt.lineTo(b.x+b.w, b.y-this.spread);
		crt.endFill();
		var clb=sh.createEmptyMovieClip(this._name+"CornerLB_mc", 8);
		this.matrix = {matrixType:"box", x:b.x-this.spread, y:b.y+b.h-this.spread, w:this.spread*2, h:this.spread*2, r:0};
		clb.lineStyle(0, this.col, 0);
		clb.beginGradientFill("radial", this.cornerColors, this.cornerAlphas, this.ratios, this.matrix);
		clb.moveTo(b.x-this.spread, b.y+b.h);
		clb.curveTo(b.x-this.spread, b.y+b.h+this.spread, b.x, b.y+b.h+this.spread);
		clb.lineTo(b.x, b.y+b.h);
		clb.lineTo(b.x-this.spread, b.y+b.h);
		clb.endFill();
		var crb=sh.createEmptyMovieClip(this._name+"CornerRB_mc", 9);
		this.matrix = {matrixType:"box", x:b.x+b.w-this.spread, y:b.y+b.h-this.spread, w:this.spread*2, h:this.spread*2, r:0};
		crb.lineStyle(0, this.col,0);
		crb.beginGradientFill("radial", this.cornerColors, this.cornerAlphas, this.ratios, this.matrix);
		crb.moveTo(b.x+b.w+this.spread, b.y+b.h);
		crb.curveTo(b.x+b.w+this.spread, b.y+b.h+this.spread, b.x+b.w, b.y+b.h+this.spread);
		crb.lineTo(b.x+b.w, b.y, b.y);
		crb.lineTo(b.x+b.w, b.y+b.h);
		crb.endFill();
		return sh;
		}

		/**
		* Draw a rectangle, without filling it
		* x: position x
		* y: position y
		* w: width of the rectangle
		* h: height of the rectanble
		*/
		MovieClip.prototype.drawRect = function(x, y, w, h) {
		this.moveTo(x, y);
		this.lineTo(x+w, y);
		this.lineTo(x+w, y+h);
		this.lineTo(x, y+h);
		this.lineTo(x, y);
		};
		
		MovieClip.prototype.drawSquare = function(x, y, side) {
		this.moveTo(x, y);
		this.lineTo(x+side, y);
		this.lineTo(x+side, y+side);
		this.lineTo(x, y+side);
		this.lineTo(x, y);
		};
		
		MovieClip.prototype.fillCircle= function(r:Number,x:Number,y:Number,color){
			var styleMaker:Number = 22.5;
			this.moveTo(x+r,y);
			this.lineStyle();
			this.beginFill(color)
			var style:Number = Math.tan(styleMaker*Math.PI/180);
			for (var angle:Number=45;angle<=360;angle+=45){
			 var endX:Number = r * Math.cos(angle*Math.PI/180);
			 var endY:Number = r * Math.sin(angle*Math.PI/180);
			 var cX:Number   = endX + r* style * Math.cos((angle-90)*Math.PI/180);
			 var cY:Number   = endY + r* style * Math.sin((angle-90)*Math.PI/180);
			 this.curveTo(cX+x,cY+y,endX+x,endY+y);
			}
			this.endFill();
		}
		
		MovieClip.prototype.reposObjForViewing=	function(dx,dy){
			var bbox=this.getBounds(_root);
			if(bbox.xMax>Stage.width){
				var newval=Stage.width-bbox.xMax-dx;
				this._x+=newval;
			}
			if(bbox.yMax>Stage.height){
				var newval=Stage.height-bbox.yMax-dy;
				this._y+=newval;
			}
		}		
		
		String.prototype.replace = function() {
			var arg_search, arg_replace, position; 
			var endText, preText, newText; 
			arg_search = arguments[0];
			arg_replace = arguments[1];
			if(arg_search.length==1) return this.split(arg_search).join(arg_replace);
			position = this.indexOf(arg_search); 
			if(position == -1) return this; 
			endText = this; 
			do 
			{ 
			position = endText.indexOf(arg_search); 
			preText = endText.substring(0, position) 
			endText = endText.substring(position + arg_search.length) 
			newText += preText + arg_replace; 
			} while(endText.indexOf(arg_search) != -1) 
			newText += endText; 
			return newText; 
		} 
		
	}
}
