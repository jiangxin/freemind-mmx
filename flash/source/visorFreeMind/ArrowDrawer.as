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
* ArrowDrawer, draw the arrow enden lines in a mindmap, and it is composed of all, static
* functions.
*/
class visorFreeMind.ArrowDrawer {

	public static function drawArrows(list_arrows,mc_floor,xcenter){
		for(var i=0;i<list_arrows.length;i++){
			var arrow=list_arrows[i];
			var mc_orig=mc_floor[arrow[0]];
			var mc_dest=mc_floor[arrow[1]];
			//trace("dibujando arrow orig:"+mc_orig+" dest:"+mc_dest);
			//trace(arrow[0]+" x:"+mc_floor[arrow[0]]._x+" "+arrow[1]+" x:"+mc_floor[arrow[1]]._x,2);
			if(mc_orig._visible && mc_dest._visible){
				drawArrow(mc_orig,mc_dest,arrow[4],mc_floor,xcenter);
				drawStartEndArrowFigure(mc_orig,arrow[2],arrow[4],mc_floor,xcenter);
				drawStartEndArrowFigure(mc_dest,arrow[3],arrow[4],mc_floor,xcenter);
			}else{
				if(mc_orig._visible){
					drawDashedLine(mc_orig,arrow[4],mc_floor,xcenter);
					drawStartEndArrowFigure(mc_orig,arrow[2],arrow[4],mc_floor,xcenter);
				}
				if(mc_dest._visible){
					drawDashedLine(mc_dest,arrow[4],mc_floor,xcenter);
					drawStartEndArrowFigure(mc_dest,arrow[3],arrow[4],mc_floor,xcenter);
				}
			}

		}
	}

	private static function drawDashedLine(mc_mc,color,mc_floor,xcenter){
		mc_floor.lineStyle(1,color,100)
		if(mc_mc._x>xcenter){
			mc_floor.dashTo(mc_mc._x+mc_mc._width,mc_mc._y+mc_mc._height/2,
							mc_mc._x+mc_mc._width+50,mc_mc._y+mc_mc._height/2,
							3,2,1);
		}else{
			mc_floor.dashTo(mc_mc._x,mc_mc._y+mc_mc._height/2,
							mc_mc._x-50,mc_mc._y+mc_mc._height/2,
							3,2,1);
		}
	}

	private static function drawStartEndArrowFigure(mc_mc,type,color,mc_floor,xcenter){
			if(type=="Default"){
				if(mc_mc._x>xcenter){
					drawTriangle(mc_mc._x+mc_mc._width,mc_mc._y+mc_mc._height/2,color,1,mc_floor);
				}else{
					drawTriangle(mc_mc._x,mc_mc._y+mc_mc._height/2,color,-1,mc_floor);
				}
			}
	}

	private static function drawTriangle(posx,posy,color,signo,mc_floor){
		mc_floor.lineStyle(1,color,100);

		mc_floor.moveTo(posx,posy);
		mc_floor.beginFill(color,100);
		mc_floor.lineTo(posx+signo*10,posy-4);
		mc_floor.lineTo(posx+signo*10,posy+4);
		mc_floor.lineTo(posx,posy);
		mc_floor.endFill();
	}

	private static function drawArrow(mc_orig,mc_dest,color,mc_floor,xcenter){
		var incr=0;
		if((mc_orig._x>xcenter && mc_dest._x>xcenter) || (mc_orig._x<xcenter && mc_dest._x<xcenter))
			incr=0.5*Math.abs(mc_orig._y-mc_dest._y);
		incr=0.5*Math.abs(mc_orig._y-mc_dest._y);
		var base=40+incr;
		mc_floor.lineStyle(1,color,100);

		mc_floor.moveTo(mc_orig._x+(mc_orig._x>xcenter?mc_orig._width:0),
						mc_orig._y+mc_orig._height/2);
		mc_floor.curveTo(mc_orig._x+(mc_orig._x>xcenter?mc_orig._width+base:-base),
						mc_orig._y+mc_orig._height/2,
						((mc_dest._x+(mc_dest._x>xcenter?mc_dest._width+base:-base))+(mc_orig._x+(mc_orig._x>xcenter?mc_orig._width+base:-base)))/2,
						(mc_orig._y+mc_orig._height/2+mc_dest._y+mc_dest._height/2)/2);
		mc_floor.curveTo(mc_dest._x+(mc_dest._x>xcenter?mc_dest._width+base:-base),
						mc_dest._y+mc_dest._height/2,
						mc_dest._x+(mc_dest._x>xcenter?mc_dest._width:0),
						mc_dest._y+mc_dest._height/2);
	}




}
