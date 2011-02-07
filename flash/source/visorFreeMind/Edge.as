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
* Edge are required for drawing the "edges" between Nodes.
* They have their own movieclip
*/
class visorFreeMind.Edge {
	public static var num:Number=1000; // counter of edges
	public static var elipseMode:Boolean=true;
	private var nombre:String; // Not used
	private var ref_mc:MovieClip;
	private var gap:Number=0; //linked with size of Nodes
	private var _orig:Node;
	private var _dest:Node;

	function Edge(orig:Node,dest:Node,nom:String,mc:MovieClip){
		nombre=nom;
		_orig=orig;
		_dest=dest;
		num++;
		ref_mc=mc.createEmptyMovieClip("link"+num,num);
		// add to nodes origin and dest
		_orig.addEdge(this);
		_dest.addEdge(this);
	}


	private function drawEdge(or_x,or_y,ddx,ddy,color,alpha,colorOrig){
		var thickness=_dest.lineWidth;
		var h_thickness=_dest.lineWidth*0.5;
		//0=bezier, 1=linear,2=sharp_bezier,3=sharp_linear;4=rectangular
		switch(_dest.styleLine){
		case 0:
			ref_mc.lineStyle(thickness,color,alpha);
			ref_mc.moveTo(or_x,or_y);
			ref_mc.curveTo(ddx*0.3,0,ddx*0.5,ddy*0.5);
			ref_mc.curveTo(ddx*0.7,ddy,ddx,ddy);
			break;
		case 1:
			ref_mc.lineStyle(thickness,color,alpha);
			ref_mc.moveTo(or_x,or_y);
			ref_mc.lineTo(ddx,ddy);
			//ref_mc.dashTo(or_x,or_y, ddx,ddy, 1, 2,thickness);
			break;
		case 2:
			ref_mc.beginFill(_dest.cf,alpha);
			ref_mc.lineStyle(0,color,alpha);
			ref_mc.moveTo(or_x,or_y+h_thickness);
			ref_mc.curveTo(ddx*0.3,h_thickness,ddx*0.5,(ddy+h_thickness)*0.5);
			ref_mc.curveTo(ddx*0.7,ddy,ddx,ddy);
			ref_mc.curveTo(ddx*0.7,ddy,ddx*0.5,(ddy-h_thickness)*0.5);
			ref_mc.curveTo(ddx*0.3,-h_thickness,0,-h_thickness);
			ref_mc.lineTo(or_x,or_y+h_thickness);
			ref_mc.endFill();
			break;
		case 3:
			ref_mc.beginFill(color,alpha);
			ref_mc.lineStyle(0,color,alpha);
			
			ref_mc.moveTo(or_x,or_y+h_thickness);
			ref_mc.lineTo(ddx,ddy);
			ref_mc.lineTo(or_x,or_y-h_thickness);
			ref_mc.lineTo(or_x,or_y+h_thickness);
			ref_mc.endFill();
			break;
		case 4:
			//ref_mc.lineStyle(_orig.lineWidth,colorOrig,alpha);
			ref_mc.lineStyle(thickness,color,alpha);
			ref_mc.moveTo(or_x,or_y);
			ref_mc.lineTo(or_x+10*Math.abs(ddx)/ddx,or_y);
			ref_mc.lineTo(or_x+10*Math.abs(ddx)/ddx,ddy);
			ref_mc.lineTo(ddx,ddy);
			break;
		}
	}

	private function drawEdgeCenter(or_x,or_y,ang,or_x2,or_y2,ddx,ddy,color,alpha,colorOrig){
		var thickness=_dest.lineWidth;
		var h_thickness=_dest.lineWidth*0.5;
		//0=bezier, 1=linear,2=sharp_bezier,3=sharp_linear;4=rectangular
		switch(_dest.styleLine){
		case 0:
			ref_mc.lineStyle(thickness,color,alpha);
			var r=Math.sqrt((ddx-or_x)*(ddx-or_x)+(ddy-or_y)*(ddy-or_y))*0.3;
			if(r>=Math.abs(ddx))r=Math.abs(ddx); //For not crossing
			var cx=or_x+r*Math.cos(ang);
			var cy=or_y+r*Math.sin(ang);
			var cx2=ddx-r*ddx/Math.abs(ddx);
			var cy2=ddy;
			ref_mc.moveTo(or_x,or_y); 
			ref_mc.curveTo(cx,cy,(cx+cx2)/2,(cy+cy2)/2);
			ref_mc.curveTo(cx2,ddy,ddx,ddy);
			break;
		case 1:
			ref_mc.lineStyle(thickness,color,alpha);
			ref_mc.moveTo(or_x,or_y);
			ref_mc.lineTo(ddx,ddy);
			//ref_mc.dashTo(or_x,or_y, ddx,ddy, 1, 2,thickness);
			break;
		case 2:
			ref_mc.beginFill(_dest.cf,alpha);
			ref_mc.lineStyle(0,color,alpha);
			var r=Math.sqrt((ddx-or_x)*(ddx-or_x)+(ddy-or_y)*(ddy-or_y))*0.3;
			if(r>=Math.abs(ddx))r=Math.abs(ddx); //For not crossing
			var cx=or_x+r*Math.cos(ang);
			var cy=or_y+r*Math.sin(ang);
			var cxx=or_x2+r*Math.cos(ang);
			var cyy=or_y2+r*Math.sin(ang);
			var cx2=ddx-r*ddx/Math.abs(ddx);
			var cy2=ddy;
			ref_mc.moveTo(or_x,or_y);
			ref_mc.curveTo(cx,cy,(cx+cx2)/2,(cy+cy2)/2);
			ref_mc.curveTo(cx2,ddy,ddx,ddy);
			ref_mc.curveTo(cx2,cy2,(cxx+cx2)/2,(cyy+cy2)/2);
			ref_mc.curveTo(cxx,cyy,or_x2,or_y2);
			ref_mc.lineTo(or_x,or_y);
			ref_mc.endFill();
			break;
		case 3:
			ref_mc.beginFill(color,alpha);
			ref_mc.lineStyle(0,color,alpha);
			ref_mc.moveTo(or_x,or_y);
			ref_mc.lineTo(ddx,ddy);
			ref_mc.lineTo(or_x2,or_y2);
			ref_mc.lineTo(or_x,or_y);
			ref_mc.endFill();
			break;
		case 4:
			//ref_mc.lineStyle(_orig.lineWidth,colorOrig,alpha);
			ref_mc.lineStyle(thickness,color,alpha);
			ref_mc.moveTo(or_x,or_y);
			ref_mc.lineTo(or_x+10*Math.abs(ddx)/ddx,or_y);
			ref_mc.lineTo(or_x+10*Math.abs(ddx)/ddx,ddy);
			ref_mc.lineTo(ddx,ddy);
			break;
		}
	}

	private function intersec(x,y,orig_w,orig_h){
			var angle_d=Math.atan2(y,x);
			var radian = Math.atan2(orig_w*Math.tan(angle_d),orig_h);
			var angle_d_360=((angle_d*180/Math.PI)+360)%360;
			if(angle_d_360>90 && angle_d_360<270)
				radian+=Math.PI;
			var w=(orig_w)*Math.cos(radian)/2;
			var h=(orig_h)*Math.sin(radian)/2;
			var pos=[w,h,radian];
			return pos;
	}
	
	private function drawCenter(){
			var thickness=_dest.lineWidth;
			var destThickness=0;
			var h_thickness=_dest.lineWidth*0.5;
		
			var bo=_orig.ref_mc.getBounds(ref_mc._parent); 

			ref_mc._x=bo.xMin+(bo.xMax-bo.xMin)/2;
			ref_mc._y=bo.yMin+(bo.yMax-bo.yMin)/2;
			var ow=bo.xMax-bo.xMin;
			var oh=bo.yMax-bo.yMin;
			
			var ddx;
			var ddy;
			if(_orig.ref_mc._x < _dest.ref_mc._x){ // RIGHT
				ddx=_dest.ref_mc._x-((_dest.style==1)?0:(_dest.ref_mc.box_txt._width-_dest.ref_mc.node_txt._width)/2)- ref_mc._x;
				 ddy=_dest.ref_mc._y -  ref_mc._y +((_dest.style==1)? _dest.ref_mc.node_txt._height -destThickness: _dest.ref_mc.node_txt._height/2);
			}else { // LEFT
				ddx=_dest.ref_mc._x+_dest.ref_mc.node_txt._width+((_dest.style==1)?0:(_dest.ref_mc.box_txt._width-_dest.ref_mc.node_txt._width)/2)- ref_mc._x;
				ddy=_dest.ref_mc._y -  ref_mc._y +((_dest.style==1)? _dest.ref_mc.node_txt._height -destThickness: _dest.ref_mc.node_txt._height/2);
			}			
			
			var pos=intersec(ddx,ddy,ow,oh);
			//trace(pos[0]+" -- "+pos[1]+" "+ddx+" "+ddy+" "+_orig.ref_mc._width+" "+_orig.ref_mc._height+" "+ow+" : "+oh);
			var pos2=pos;//Case aditional points
			if(_dest.styleLine==3 || _dest.styleLine==2){//Calc aditional points in the elipse
				var despx=Math.cos(pos[2]+Math.PI/2);
				var despy=Math.sin(pos[2]+Math.PI/2);
				pos2=intersec(pos[0]-h_thickness*despx,pos[1]-h_thickness*despy,ow,oh);
				pos=intersec(pos[0]+h_thickness*despx,pos[1]+h_thickness*despy,ow,oh);
			}
			drawEdgeCenter(pos[0],pos[1],pos[2],pos2[0],pos2[1],ddx,ddy,_dest.cf,100,_orig.cf);
	}
	
	public function draw(){
		ref_mc.clear();
		if(_orig.style==0 && elipseMode)//=Central Node
			drawCenter();
		else
			drawSimple();
	}
	
	private function drawSimple(){
		var ddx,ddy;
		var destThickness=0;
		var origThickness=0;
		if(_orig.ref_mc._x < _dest.ref_mc._x){ // RIGHT
			ref_mc._x=_orig.ref_mc._x+_orig.ref_mc.node_txt._width+((_orig.style==1)?0:(_orig.ref_mc.box_txt._width-_orig.ref_mc.node_txt._width)/2);
			ref_mc._y=_orig.ref_mc._y+((_orig.style==1)? _orig.ref_mc.node_txt._height -origThickness: _orig.ref_mc.node_txt._height/2);
			ddx=_dest.ref_mc._x-((_dest.style==1)?0:(_dest.ref_mc.box_txt._width-_dest.ref_mc.node_txt._width)/2)- ref_mc._x;
			 ddy=_dest.ref_mc._y -  ref_mc._y +((_dest.style==1)? _dest.ref_mc.node_txt._height -destThickness: _dest.ref_mc.node_txt._height/2);

			drawEdge(0,0,ddx,ddy,_dest.cf,100,_orig.cf);
		}else { // LEFT
			ref_mc._x=_orig.ref_mc._x-((_orig.style==1)?0:(_orig.ref_mc.box_txt._width-_orig.ref_mc.node_txt._width)/2);
			ref_mc._y=_orig.ref_mc._y+((_orig.style==1)? _orig.ref_mc.node_txt._height -origThickness: _orig.ref_mc.node_txt._height/2);
			ddx=_dest.ref_mc._x+_dest.ref_mc.node_txt._width+((_dest.style==1)?0:(_dest.ref_mc.box_txt._width-_dest.ref_mc.node_txt._width)/2)- ref_mc._x;
			ddy=_dest.ref_mc._y -  ref_mc._y +((_dest.style==1)? _dest.ref_mc.node_txt._height -destThickness: _dest.ref_mc.node_txt._height/2);

			drawEdge(0,0,ddx,ddy,_dest.cf,100,_orig.cf);
		}

	}
}
