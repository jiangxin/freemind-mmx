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
* CloudDrawer, draw the clouds of the mindmap
* functions.
*/
class visorFreeMind.CloudDrawer {

	private static var _instance:CloudDrawer=null;

	private function CloudDrawer(){
	}

	public static function getInstance():CloudDrawer{
		if(CloudDrawer._instance==null){
			CloudDrawer._instance=new CloudDrawer();
		}
		return CloudDrawer._instance;
	}

	private function getCloudColor(xml_node:XMLNode):Number{
		if(xml_node.attributes.COLOR!=undefined)
			return new Number("0x"+xml_node.attributes.COLOR.substring(1));
		else
			return 0xEEEEEE;
	}

	function drawRectangle(xml_cloud:XMLNode,node:Node,container,supClouds,isRight){
		var cloudColor=getCloudColor(xml_cloud);
		var cloudBorderColor=genBorderColor(cloudColor); //Aprox
		var numSubClouds=0;
		//Obtain cloud points.
		var upPoints=[];
		var downPoints=[];
		var sidePoints=[];
		getUpPoints(node,upPoints,numSubClouds,isRight);
		getDownPoints(node,downPoints,numSubClouds,isRight);
		getSidePoints(node,sidePoints,numSubClouds,isRight);
		var allPoints=upPoints.concat(downPoints,sidePoints);
		var res=getMaxMinXY(allPoints); //[minX,minY,maxX,maxY]
		//Draw cloud
		container.lineStyle(0,cloudBorderColor,100);
		container.beginFill(cloudColor,100);
		container.moveTo(res[0],res[1]);
		container.lineTo(res[2],res[1]);
		container.lineTo(res[2],res[3]);
		container.lineTo(res[0],res[3]);
		container.lineTo(res[0],res[1]);
	}
	
	function genBorderColor(color){
		var nRed = (color >> 16)-0x22;
		nRed=nRed>=0?nRed:0;
		var nGreen= ((color >> 8) & 0xff)-0x22;
		nGreen=nGreen>=0?nGreen:0;
		var nBlue= (color & 0xff)-0x22;
		nBlue=nBlue>=0?nBlue:0;
		return (nRed<<16 | nGreen<<8 |nBlue);
	}
	
	function getMaxMinXY(allPoints){
		var desp=4;
		var maxX=allPoints[0][0];
		var maxY=allPoints[0][1];
		var minX=allPoints[0][0];
		var minY=allPoints[0][1];
		for(var point in allPoints){
			if(allPoints[point][0]>maxX)
				maxX=allPoints[point][0];
			if(allPoints[point][0]<minX)
				minX=allPoints[point][0];
			if(allPoints[point][1]>maxY)
				maxY=allPoints[point][1];
			if(allPoints[point][1]<minY)
				minY=allPoints[point][1];
		}
		return [minX-desp-4,minY-desp,maxX+desp,maxY+desp]
	}
	
	function drawCloud(xml_cloud:XMLNode,node:Node,container,supClouds,isRight){

		//var xml_node:XMLNode=node.getNode_xml();
		var cloudColor=getCloudColor(xml_cloud);
		var cloudBorderColor=genBorderColor(cloudColor); //Aprox

		var displacement=30/(1+supClouds/3);
		var maxDistance=30000/(1+supClouds);
		var numSubClouds=0;
		//Obtain cloud points.
		var upPoints=[];
		getUpPoints(node,upPoints,numSubClouds,isRight);
		var downPoints=[];
		getDownPoints(node,downPoints,numSubClouds,isRight);
		var sidePoints=[];
		getSidePoints(node,sidePoints,numSubClouds,isRight);

		//////////////// DRAW CLOUD
		container.lineStyle(3,cloudBorderColor,100);
		container.beginFill(cloudColor,100);

		var pOrig=upPoints[0];
		var pDest=upPoints[upPoints.length-1];
		container.moveTo(pOrig[0],pOrig[1]);
		var listSelected=[];
		calcGoodUpDownPoints(pOrig,pDest,1,upPoints.length,upPoints,listSelected,true,maxDistance);
		//trace(listSelected.length,1);
		for (var i=0;i<listSelected.length;i++){
			drawCurveUpDown(pOrig,listSelected[i],container,1,-1,displacement);
			pOrig=listSelected[i];
		}

		//SIDE
		pOrig=upPoints[upPoints.length-1];
		pDest=downPoints[downPoints.length-1];
		listSelected=[];
		calcGoodSidePoints(pOrig,pDest,1,sidePoints.length,sidePoints,listSelected,isRight,maxDistance);

		for(var i=0;i<listSelected.length;i++){
			drawCurveSide(pOrig,listSelected[i],container,isRight?1:-1,isRight?-1:1,displacement);
			pOrig=listSelected[i];
		}

		//DOWN
		drawCurveSide(pOrig,downPoints[downPoints.length-1],container,isRight?1:-1,isRight?-1:1,displacement);

		downPoints.reverse();
		pOrig=downPoints[0];
		pDest=downPoints[downPoints.length-1];
		listSelected=[];
		calcGoodUpDownPoints(pOrig,pDest,1,downPoints.length,downPoints,listSelected,false,maxDistance);
		for (var i=0;i<listSelected.length;i++){
			drawCurveUpDown(pOrig,listSelected[i],container,-1,1,displacement);
			pOrig=listSelected[i];
		}

		/*
		for (var  i=downPoints.length-2;i>=0;i--){
			drawCurveUpDown(pOrig,downPoints[i],container,-1,1);
			pOrig=downPoints[i];
		}
*/
		drawCurveUpDown(pOrig,pDest,container,-1,1,displacement);
		drawCurveSide(pDest,upPoints[0],container,isRight?-1:1,1,displacement)
		container.endFill();
	}

	function drawCurveSide(pOrig,pDest,container,sign_x,sign_y,displacement){
		var slope=(pDest[0]+0.25-pOrig[0])/(pOrig[1]-0.25-pDest[1]);
		var middlePoint=[(pDest[0]+pOrig[0])/2,(pDest[1]+pOrig[1])/2];
		var mod=Math.abs(displacement/Math.sqrt(1+slope*slope)); //30 pixels =  absolute displacement
		container.curveTo(middlePoint[0]+sign_x*mod,middlePoint[1]-sign_y*mod*slope,pDest[0],pDest[1]);
	}

	function drawCurveUpDown(pOrig,pDest,container,sign_x,sign_y,displacement){
		var slope=(pDest[1]+0.25-pOrig[1])/(pOrig[0]-0.25-pDest[0]);
		var middlePoint=[(pDest[0]+pOrig[0])/2,(pDest[1]+pOrig[1])/2];
		var mod=Math.abs(displacement/Math.sqrt(1+slope*slope)); //30 pixels =  absolute displacement
		container.curveTo(middlePoint[0]-sign_x*mod*slope,middlePoint[1]+sign_y*mod,pDest[0],pDest[1]);
	}

	function calcGoodUpDownPoints(maxsup,maxinf,ini,end,sidePoints,listSelected,isRight,maxDistance){
		var slope=(maxsup[1]-maxinf[1]-0.0005)/(maxinf[0]-maxsup[0]);
		//trace("slope("+maxsup[0]+","+maxsup[1]+")"+"("+
		//	maxinf[0]+","+maxinf[1]+")"+":"+slope);
		var k1=slope*maxsup[0]+maxsup[1];
		//trace("	k1:"+k1);
		var newmax=-1;
		for(var i=ini;i<end;i++){
			var p=sidePoints[i];
			var k2=slope*p[0]+p[1];
			//trace("	k2:"+k2+" ("+p[0]+","+p[1]+")");
			if(( isRight && k1>k2) || (!isRight && k1<k2)){
				k1=k2;
				//trace("	new k1:"+k2);
				newmax=i;
			}
		}
		if(newmax>=0){
			calcGoodUpDownPoints(maxsup,sidePoints[newmax],ini,newmax,sidePoints,listSelected,isRight,maxDistance);
			listSelected.push(sidePoints[newmax]);
			//trace("added:"+"("+sidePoints[newmax][0]+","+sidePoints[newmax][1]+")");
			calcGoodUpDownPoints(sidePoints[newmax],maxinf,newmax+1,end,sidePoints,listSelected,isRight,maxDistance);
		} else  if(((maxinf[0]-maxsup[0])*(maxinf[0]-maxsup[0]) + (maxinf[1]-maxsup[1])*(maxinf[1]-maxsup[1]))>maxDistance){

			var middlePoint=[maxsup[0]+(maxinf[0]-maxsup[0])*0.5,maxsup[1]+(maxinf[1]-maxsup[1])*0.5];
			calcGoodUpDownPoints(maxsup,middlePoint,ini,ini,sidePoints,listSelected,isRight,maxDistance);
			listSelected.push(middlePoint);
			calcGoodUpDownPoints(middlePoint,maxinf,ini,ini,sidePoints,listSelected,isRight,maxDistance);

		}
	}

	function calcGoodSidePoints(maxsup,maxinf,ini,end,sidePoints,listSelected,isRight,maxDistance){
		var slope=(maxinf[0]-maxsup[0])/(maxsup[1]-maxinf[1]);
		var k1=maxsup[0]+slope*maxsup[1];
		var newmax=-1;
		for(var i=ini;i<end;i++){
			var p=sidePoints[i];
			var k2=p[0]+slope*p[1];
			if(( isRight && k1<k2) || (!isRight && k1>k2)){
				k1=k2;
				newmax=i;
			}
		}
		if(newmax>=0){
			calcGoodSidePoints(maxsup,sidePoints[newmax],ini,newmax,sidePoints,listSelected,isRight,maxDistance);
			listSelected.push(sidePoints[newmax]);
			calcGoodSidePoints(sidePoints[newmax],maxinf,newmax+1,end,sidePoints,listSelected,isRight,maxDistance);
		} else  if(((maxinf[0]-maxsup[0])*(maxinf[0]-maxsup[0]) + (maxinf[1]-maxsup[1])*(maxinf[1]-maxsup[1]))>maxDistance){

			var middlePoint=[maxsup[0]+(maxinf[0]-maxsup[0])*0.5,maxsup[1]+(maxinf[1]-maxsup[1])*0.5];
			calcGoodSidePoints(maxsup,middlePoint,ini,ini,sidePoints,listSelected,isRight,maxDistance);
			listSelected.push(middlePoint);
			calcGoodSidePoints(middlePoint,maxinf,ini,ini,sidePoints,listSelected,isRight,maxDistance);

		}
	}

	function getSidePoints(node,sidePoints,numSubClouds,isRight){
		if(node.childNodes.length>0 && node.childNodes[0].ref_mc._visible){
			for(var i=0;i<node.childNodes.length;i++){
				var incNumSubClouds=node.childNodes[i].withCloud==true?numSubClouds+1:numSubClouds;
				getSidePoints(node.childNodes[i],sidePoints,incNumSubClouds,isRight);
			}
		}else{
			var aux=(isRight?node.ref_mc._width+8*numSubClouds:-numSubClouds*8);
			sidePoints.push([node.ref_mc._x+aux,node.ref_mc._y]);
			sidePoints.push([node.ref_mc._x+aux,node.ref_mc._y+node.ref_mc._height]);
		}
	}

	/**
	 * 	 */
	function getUpPoints(node,upPoints,numSubClouds,isRight){
		upPoints.push([node.ref_mc._x+(isRight?0:node.ref_mc._width),node.ref_mc._y-8*numSubClouds]);
		upPoints.push([node.ref_mc._x+(isRight?node.ref_mc._width:0),node.ref_mc._y-8*numSubClouds]);
		if(node.childNodes.length>0 && node.childNodes[0].ref_mc._visible){
			var incNumSubClouds=node.childNodes[0].withCloud==true?numSubClouds+1:numSubClouds;
			getUpPoints(node.childNodes[0],upPoints,incNumSubClouds,isRight);
		}
	}

	function getUpPoints2(node,upPoints,numSubClouds,isRight){
		upPoints.push([node.ref_mc._x+(isRight?0:node.ref_mc._width),node.ref_mc._y-8*numSubClouds]);
		if(node.childNodes.length>0 && node.childNodes[0].ref_mc._visible){
			var incNumSubClouds=node.childNodes[0].withCloud==true?numSubClouds+1:numSubClouds;
			getUpPoints(node.childNodes[0],upPoints,incNumSubClouds,isRight);
		}else{
			upPoints.push([node.ref_mc._x+(isRight?node.ref_mc._width:0),node.ref_mc._y-8*numSubClouds]);
		}
	}

	function getDownPoints(node,downPoints,numSubClouds,isRight){
		downPoints.push([node.ref_mc._x+(isRight?0:node.ref_mc._width),node.ref_mc._y+node.ref_mc._height+8*numSubClouds]);
		downPoints.push([node.ref_mc._x+(isRight?node.ref_mc._width:0),node.ref_mc._y+node.ref_mc._height+8*numSubClouds]);
		if(node.childNodes.length>0 && node.childNodes[node.childNodes.length-1].ref_mc._visible){
			var incNumSubClouds=node.childNodes[node.childNodes.length-1].withCloud==true?numSubClouds+1:numSubClouds;
			getDownPoints(node.childNodes[node.childNodes.length-1],downPoints,incNumSubClouds,isRight);
		}
	}


	function getDownPoints2(node,downPoints,numSubClouds,isRight){
		downPoints.push([node.ref_mc._x+(isRight?0:node.ref_mc._width),node.ref_mc._y+node.ref_mc._height+8*numSubClouds]);
		if(node.childNodes.length>0 && node.childNodes[node.childNodes.length-1].ref_mc._visible){
			var incNumSubClouds=node.childNodes[node.childNodes.length-1].withCloud==true?numSubClouds+1:numSubClouds;
			getDownPoints(node.childNodes[node.childNodes.length-1],downPoints,incNumSubClouds,isRight);
		}else{
			downPoints.push([node.ref_mc._x+(isRight?node.ref_mc._width:0),node.ref_mc._y+node.ref_mc._height+8*numSubClouds]);
		}
	}



}
