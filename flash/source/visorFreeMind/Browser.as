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
/**
	Browser Class
	Author: Juan Pedro de Andres
	Will contain one mindmap in a time.
*/
class visorFreeMind.Browser {
	
	private var mcl:MovieClipLoader=new MovieClipLoader();

	public var mc_container:MovieClip; //Movieclip where we are created
	public var mc_floor:MovieClip; // dragable.
	public var historyManager:HistoryManager;
	public var searchDialog:SearchManager;
	public var panner:Panner;
	public var floor:Floor; //Class containing the mc where everything is draw
	private var first_node:Node=null;
	private var first_node_left:Node=null;
	private var list_right_clouds:Array=[];
	private var list_left_clouds:Array=[];
	private var list_arrows:Array=[];
	private var arrows_map:Object=null;
	public static var browser;
	private var listNodesR=[];
	private var listNodesL=[];
	private var list_edges=[];
	public  var links_mm:Array=[];
	private var aux_ta=null;
	private var initialization:Boolean=true;
	private var numWaitingImages:Number=0; //When we have images, we have to wait for then loaded
	private var imgsLoaded=false;
	//Buttons
	private var resizer;
	private var bBack;
	private var bForward;
	private var bGrow;
	private var bShrink;
	private var bReset;
	private var bShadow;
	private var bInfo;
	private var bColor;
	public  var withShadow=getStaticAtr("withShadow",false);
	public static var startCollapsedToLevel=-1;
	public static var defaultToolTipWordWrap=800;
	
	public static var offsetX=0;//right|left|Number
	public static var offsetY=0;//top|bottom|Number
	public static var unfoldAll:Boolean=false;
	public static var justMap:Boolean=false;
	public static var scaleTooltips:Boolean=false;
	public static var toolTipsBgColor:Number=0xFFDD88;
	public static var toolTipsFgColor:Number=0x664400;
	public static var CSSFile:String="flashfreemind.css";
	public static var flashVersion:Number=0;

	public var text_selectable=null;

	var my_fmt:TextFormat = new TextFormat();
	var myCSS;
	var ant_floor_y=0;
	var ant_pnode_y=0;
	var ant_floor_x=0;
	var ant_pnode_x=0;

	var keyControler; //Atends key events
	public var buttonsCreator;
	var loading:Loading;

	function Browser(file:String,_mc:MovieClip){
		trace("new Browser, shadow="+withShadow,0);
		browser=this;
		flashVersion=getFlashVersion();
		trace("flash version: "+flashVersion);
		mcl.addListener(this); // For waiting for the load of all images
		PrototypesCreator.init();
		mc_container=_mc;
		createFloor();
		if(Browser.justMap==false)
			buttonsCreator=new ButtonsCreator(this);
		recalcIfResize();
		createToolTip(); //node_txt en mc_container.
		loading=new Loading(this,mc_container);
		buttonsCreator.addToolTipsButtons();
		keyControler=new  KeyControler(this);
		//Añadimos un control de frames
		mc_container.browser=this;
		mc_container.onEnterFrame=function(){
			this.browser.checkImagesLoaded();
		}
		//Start loading first file
		historyManager=new HistoryManager(this);
		searchDialog=new SearchManager(this);
		panner=new Panner(this);
		historyManager.loadXML(file);
		
	}

	function checkImagesLoaded(){
		if(imgsLoaded==true){
			trace("imgsloaded");
			relocateMindMap();
			imgsLoaded=false;
			historyManager.genMMEnded();
		}
	}



	// In case STAGE size change, recalc positions
	function recalcIfResize(){
		Stage.addListener (this);
	}

	public function onResize(){
		buttonsCreator.relocateAllButtons();
		floor.fillFondo();
		relocateMindMap();
	}

	public function showLoading(){
		loading.show();
	}
	/////////////////////// FOR LOADING IMAGES //////////////////
	public function loadImage(fileName,mc_target){
		mcl.loadClip(fileName,mc_target);
		numWaitingImages++;
		trace("waiting: "+numWaitingImages+" add Loading :"+fileName);
		showLoading();
	}

	public function onLoadInit(target){
		trace("init"); 
	}

	public function onLoadProgress(target){
		var progress = mcl.getProgress(target);
		trace("progress Loading :"+progress.bytesLoaded / progress.bytesTotal);
	}

	public function onLoadComplete(target){
		numWaitingImages--;
		trace("rest Loading :"+numWaitingImages);
		validLoaded();
	}

	public function onLoadError(target,error){
		numWaitingImages--;
		trace("rest Loading :"+numWaitingImages+" "+error);
		validLoaded();
	}

	public function validLoaded(){
		if(numWaitingImages==0){
			imgsLoaded=true;
			loading.hide();
		}
	}
	////////////////////////// END LOADING IMAGES ////////////////



	function relocateMindMap(){
		relocateNodes(listNodesL[0],listNodesL[0].childNodes,0,0,false);//false=left, true=right
		relocateNodes(listNodesR[0],listNodesR[0].childNodes,0,0,true);
		relocateShifts(listNodesL[0],listNodesL[0].childNodes,0,false);
		relocateShifts(listNodesR[0],listNodesR[0].childNodes,0,true);
		adjustLeftNodes();
		relocateFloor();
		floor.clear();
		drawClouds();
		drawEdges();
		ArrowDrawer.drawArrows(list_arrows,mc_floor,first_node.ref_mc._x);
		listNodesL[0].ref_mc._visible=false; //main node duplicated, only show one (right)
	}

	function adjustLeftNodes(){
		var difx=listNodesR[0].ref_mc._x-listNodesL[0].ref_mc._x;
		var dify=listNodesR[0].ref_mc._y-listNodesL[0].ref_mc._y;
		for(var i=0;i<listNodesL.length;i++){
			listNodesL[i].ref_mc._x+=difx;
			listNodesL[i].ref_mc._y+=dify;
		}
	}

	function createToolTip(){
		if(mc_container.tooltip==null){
			mc_container.tooltip=mc_container.createEmptyMovieClip("tooltip",110);
			mc_container.tooltip.createEmptyMovieClip("tex_container",10);
			mc_container.tooltip.tex_container.createTextField("textfield",7777,0,0,10,10);
			var txt=mc_container.tooltip.tex_container.textfield;
			txt.background=true;
			txt.backgroundColor=Browser.toolTipsBgColor;
			txt.autoSize=true;
			txt.selectable=false;
			txt.border=false;
			txt.html = true;
			my_fmt = new TextFormat();
			my_fmt.color=Browser.toolTipsFgColor;
			my_fmt.font="Arial";
			my_fmt.size=12;
			
			//CSS for the tooltiip
			myCSS = new TextField.StyleSheet();
			var cssURL = Browser.CSSFile;
			myCSS.load(cssURL);
			myCSS.onLoad = function(exito) {
			        if (exito) {
			    Browser.browser.mc_container.tooltip.tex_container.textfield.styleSheet = Browser.browser.myCSS;
			    }
			}
		}
		mc_container.tooltip.tex_container.textfield.text="";
		mc_container.tooltip.tex_container.textfield.setTextFormat(my_fmt);
		mc_container.tooltip._visible=false;
	}

	function showTooltip(texto,dx,dy){
		//Reset of width
		mc_container.tooltip.tex_container.textfield.wordWrap=false;
		mc_container.tooltip.tex_container.textfield.autoSize=true;
		// eliminate \r because of problems of double returns
		var newText=texto.replace("\r","");
		if(newText.indexOf("<body>")>=0)
			newText=newText.substr(newText.indexOf("<body>")+6);
		mc_container.tooltip.tex_container.textfield.htmlText=newText;
		var tt=mc_container.tooltip;
		
		//check of max tooltip width
		if(tt.tex_container.textfield._width>defaultToolTipWordWrap || tt.tex_container.textfield._width>Stage.width){
			var max=Stage.width<defaultToolTipWordWrap?Stage.width-10:defaultToolTipWordWrap;
			tt.tex_container.textfield._width=max;
			tt.tex_container.textfield.wordWrap=true;
		}
		
		var sombra=tt.createEmptyMovieClip("sombra",9);
		tt.tex_container.dropShadow(8,4,4,0x777799,sombra);
		
		tt._x=_root._xmouse+dx;
		tt._y=_root._ymouse+dy;
		tt.reposObjForViewing(dx,dy);
		//reposObjForViewing(tt,14,20);
		tt._visible=true;
	}

	function hideTooltip(){
		mc_container.tooltip._visible=false;
	}
	

	function reposObjForViewing(tt,dx,dy){
		var bbox=tt.getBounds(_root);
		if(bbox.xMax>Stage.width){
			var newval=Stage.width-bbox.xMax-dx;
			tt._x+=newval;
		}
		if(bbox.yMax>Stage.height){
			var newval=Stage.height-bbox.yMax-dy;
			tt._y+=newval;
		}
	}
	
	/**
	 * Delete all the flash hidden elements of the current map, help
	 * for taking a shot	 */
	public function deleteHidden(){
		for(var j=0;j<list_edges.length;j++){
			if(list_edges[j].ref_mc._visible==false)
				list_edges[j].ref_mc.removeMovieClip();
		}
		for(var i=0;i<listNodesR.length;i++){
			if(listNodesR[i].ref_mc._visible==false)
				listNodesR[i].ref_mc.removeMovieClip();
		}

		for(var i=0;i<listNodesL.length;i++){
			if(listNodesL[i].ref_mc._visible==false)
				listNodesL[i].ref_mc.removeMovieClip();
		}
	}
	
	function resetData(){
		for(var j=0;j<list_edges.length;j++){
			list_edges[j].ref_mc.removeMovieClip();
		}
		for(var i=0;i<listNodesR.length;i++){
			listNodesR[i].ref_mc.removeMovieClip();
		}

		for(var i=0;i<listNodesL.length;i++){
			listNodesL[i].ref_mc.removeMovieClip();
		}

		floor.clear();
		list_edges=[];
		listNodesR=[];
		listNodesL=[];
		list_arrows=[];
		links_mm=[];
		arrows_map={};
		list_right_clouds=[];
		list_left_clouds=[];

		Node.currentOver=null;
		Node.num=2000;
		Edge.num=1000;
		searchDialog.reset();
	}

	function createFloor(){
		floor=new Floor(mc_container);
		mc_floor=floor.getCanvas();
	}



	function saveOldPosition(){
		ant_floor_y=mc_floor._y;
		ant_pnode_y=first_node.ref_mc._y;
		ant_floor_x=mc_floor._x;
		ant_pnode_x=first_node.ref_mc._x;
	}

	function genMindMap(jumpType){
		saveOldPosition();
		//Clean old Data.
		//trace("jumpType "+jumpType);
		if(jumpType!=2) {
			resetData();
			// clear floor.
			floor.clear();
			// generate Tree
			evalXML(jumpType);
		}else{ // 2=fold and unfold
			relocateMindMap();
		}
	}

	function relocateFloor(){
		if(initialization){
			trace("relocate con inicializacion");
			mc_floor._x=0;
			mc_floor._y=0;
			var bbox=mc_floor.getBounds(_root);
			//X
			if(offsetX=="left")	{		
				mc_floor._x=-bbox.xMin;
			}else if(offsetX=="right"){
				mc_floor._x=Stage.width-bbox.xMax;
			}else{
				var aux=Stage.width/2+offsetX;
				mc_floor._x=Stage.width/2+Number(offsetX)-first_node.ref_mc._x;
			}
			//Y
			if(offsetY=="top"){	
				mc_floor._y=-bbox.yMin;
			}else if(offsetY=="bottom"){
				mc_floor._y=Stage.height-bbox.yMax;
			}else{
				mc_floor._y=Stage.height/2+Number(offsetY)-first_node.ref_mc._y;
			}
			initialization=false;
		}else{
			trace("relocate sin inicializacion");
			mc_floor._y=ant_floor_y+ant_pnode_y-first_node.ref_mc._y;
			mc_floor._x=ant_floor_x+ant_pnode_x-first_node.ref_mc._x;
		}
	}

	function evalXML(jumpType){
		// Initial values.
		var styleNode=1; // 0= elipse, 1=fork , 2=bubble
		var styleLine=0; //0=bezier, 1=linear,2=sharp_bezier,3=sharp_linear
		var lineWidth=0;
		var color_LineaIni=0x888888;
		var xmlObj=historyManager.getXML();
		var nodeXMLIni=getFirstNodeType("node",xmlObj.firstChild);
		
		//asociate right left to nodes without it
		asociatePosition(nodeXMLIni);
		//if first time that loaded
		if(jumpType==0 && startCollapsedToLevel >=0)
			collapseToLevel(nodeXMLIni,startCollapsedToLevel);
		var supClouds=0;
		//Right nodes
		first_node=genNodes(true,nodeXMLIni,0,0,color_LineaIni,lineWidth,styleNode,styleLine,true,mc_floor,supClouds);
		//Left nodes
		first_node_left=genNodes(false,nodeXMLIni,0,0,color_LineaIni,lineWidth,styleNode,styleLine,true,mc_floor,supClouds);
		//IF there is no image to load we can show all.
		if(numWaitingImages==0){
			relocateMindMap();
			this.historyManager.genMMEnded();
		}
	}

	function relocateShifts(node,childNodes,incLevel,isRight){
		var m_decy=0;
		var m_incy=0;
		if(node.shift_y>0){
			m_incy=node.shift_y;
			incLevel+=m_incy;
		}else{
			m_decy=-node.shift_y;
		}

		node.ref_mc._y+=incLevel;
		var negvals=0;
		if(node.folded==false){
			for(var i=0;i<childNodes.length;i++){
				var mv=relocateShifts(childNodes[i],childNodes[i].childNodes,incLevel,isRight);
				incLevel=mv[0];
				negvals+=mv[1];
				
			}
		}
		node.ref_mc._y+=negvals;
		return [incLevel+m_decy,m_decy+negvals];
	}
	
	function relocateNodes(node,childNodes,x,y,isRight){
		var incy:Number=y;
		var numE:Number=0;
		var y1:Node=node;
		var yn:Node=node;
		var resize:Number=0;

		if(node.withCloud) {
			incy+=18;
		}

		if(node.withImage){
			node.withImage=false;
			node.posElements();
			node.drawAroundNode(node.cbg,100,false);
		}
		
		//hgap added
		node.ref_mc._x=x-(isRight?-node.hgap:node.ref_mc._width+node.hgap);
		var incx:Number=getIncX(isRight,node,node.ref_mc._x);

		if(node.folded==false){
			for(var i=0;i<childNodes.length;i++){
				var mv=relocateNodes(childNodes[i],childNodes[i].childNodes,incx,incy,isRight);
				numE++;
				incy=mv[0];
				if(i==0) {
					y1=mv[1];//take first
				}
				yn=mv[2]; // take last
			}
		} else {
			hideSubNodes(node);
		}

		if(numE>=1)
			node.ref_mc._y=y1.ref_mc._y+((yn.ref_mc._y-y1.ref_mc._y)/2);
		else
			node.ref_mc._y=incy;
			
		//for solving diferent fonts sizes
		incy=Math.max(incy,node.ref_mc._y+node.ref_mc._height); 

		node.ref_mc._visible=true;

		if(node.withCloud==true )
			return [incy+18,y1,yn];
		else
			return [incy+2,y1,yn];
	}

	function hideSubNodes(node){
		node.ref_mc._visible=false;
		for(var i=0;i<node.childNodes.length;i++){
			hideSubNodes(node.childNodes[i]);
		}
	}

	function drawEdges(){
		for(var i=0;i<list_edges.length;i++){
			if(list_edges[i]._dest.ref_mc._visible==true)
				list_edges[i].draw();
			else
				list_edges[i].ref_mc.clear();
		}
	}

	function drawClouds(){
		drawCloudsSide(list_left_clouds,false);
		drawCloudsSide(list_right_clouds,true);
	}


	function drawCloudsSide(lista,side){
		for (var i=lista.length-1;i>=0;i--){
			if(lista[i][1].ref_mc._visible==true){
				if(lista[i][0].attributes.type)
					CloudDrawer.getInstance().drawRectangle(lista[i][0],lista[i][1],lista[i][2],lista[i][3],side);
				else 
					CloudDrawer.getInstance().drawCloud(lista[i][0],lista[i][1],lista[i][2],lista[i][3],side);
			}
		}
	}
	
	function getLineStyle(edge_xml,styleLine){
		if(edge_xml!=null){
			if(edge_xml.attributes.STYLE!=null){
				if(edge_xml.attributes.STYLE=="sharp_bezier") styleLine=2;
				else if(edge_xml.attributes.STYLE=="bezier") styleLine=0;
				else if(edge_xml.attributes.STYLE=="linear") styleLine=1;
				else if(edge_xml.attributes.STYLE=="sharp_linear") styleLine=3;
				else if(edge_xml.attributes.STYLE=="rectangular") styleLine=4;
			}
		}
		return styleLine;
	}

	function getLineWidth(edge_xml,lineWidth){
		if(edge_xml!=null){
			if(edge_xml.attributes.WIDTH!=null){
				if(edge_xml.attributes.WIDTH=="thin")
					lineWidth=0;
				else
				lineWidth=new Number(edge_xml.attributes.WIDTH);
			}
		}
		return lineWidth;
	}

	function getLineColor(edge_xml,colorFloor){
		if(edge_xml!=null){
			if(edge_xml.attributes.COLOR!=undefined){
				var cn:String=edge_xml.attributes.COLOR;
				colorFloor=new Number("0x"+cn.substring(1));
			}
		}
		return colorFloor;
	}

	private function getNodeColor(xml_node:XMLNode):Number{
		if(xml_node!=null && xml_node.attributes.COLOR!=undefined)
			return new Number("0x"+xml_node.attributes.COLOR.substring(1));
		else
			return 0xEEEEEE;
	}


	function getNodeStyle(node_xml,styleNode){
		if(node_xml.attributes.STYLE=="bubble")
		  styleNode=2;
		if(node_xml.attributes.STYLE=="fork")
		  styleNode=1;
		if(node_xml.attributes.STYLE=="elipse")
		  styleNode=0;
		return styleNode;
	}

	function getIncX(isRight,node,x){
		if(isRight)
		   return  x+22+node.ref_mc._width;
		else
			return node.ref_mc._x-22;
	}

	function getTextSize(font){
		if(font.attributes.SIZE!=null)
			return new Number(font.attributes.SIZE);
		return 12;
	}

	function getItalic(font){
		if(font.attributes.ITALIC!=null && font.attributes.ITALIC=="true")
			return true;
		return false;
	}

	function getBold(font){
		if(font.attributes.BOLD!=null && font.attributes.BOLD=="true")
			return true;
		return false;
	}

	function getFontType(font){
		if(font.attributes.NAME!=null && font.attributes.NAME!="SansSerif")
			return font.attributes.NAME;
		return "_sans";
	}

	function asociatePosition(node_xml){
		var cont=0;
		for(var i=0;i<node_xml.childNodes.length;i++){
			var n=node_xml.childNodes[i];
			 if(n.nodeName=="node" ){
			 	if(n.attributes.POSITION===undefined)
			 		n.attributes.POSITION=(cont % 2==1?"left":"right");
					cont++;
			}
		}
	}
	
	function collapseToLevel(node_xml,level){
		for(var i=0;i<node_xml.childNodes.length;i++){
			var n=node_xml.childNodes[i];
			if(n.nodeName=="node" ){
			 	collapseToLevel(n,level-1);
				if(level<=0) 
					node_xml.attributes.FOLDED="true";
			}
		}
	}
	
	function takeLinks_mm(node_xml){
		if(node_xml.attributes.LINK != undefined && node_xml.attributes.LINK.indexOf(":")==-1 && node_xml.attributes.LINK.indexOf(".mm")!=-1)
		this.links_mm.push(node_xml.attributes.LINK);
	}
	
	function genNodes(isRight,node_xml,x,y,lineColor,lineWidth,styleNode,styleLine,first,container,supClouds){
		var n:XMLNode=null;

		//get edge style.
		takeLinks_mm(node_xml);
		var edge=getFirstNodeType("edge",node_xml);
		var font=getFirstNodeType("font",node_xml);
		var newSupClouds=supClouds;
		styleLine=getLineStyle(edge,styleLine);
		lineWidth=getLineWidth(edge,lineWidth);
		lineColor=getLineColor(edge,lineColor);
		styleNode=getNodeStyle(node_xml,styleNode);
		var cloudNode=getFirstNodeType("cloud",node_xml);
		var arrows=getNodesType("arrowlink",node_xml);
		var cloudColor:Number=getNodeColor(cloudNode);
		var textSize=getTextSize(font);
		var italic=getItalic(font);
		var bold=getBold(font);
		var type=getFontType(font);
		var withCloud:Boolean=cloudNode!=null?true:false;

		var folded=!unfoldAll;
		if(node_xml.attributes.FOLDED!="true")
		  folded=false;
	
		var node:Node;

		node=new Node(0,0,node_xml,"",container,
							   3,lineColor,lineWidth,(first?0:styleNode),styleLine,folded,isRight,withCloud,
							   textSize,italic,bold,type,this);
			
		if(!(first&&(!isRight))){
			node.draw();
		}

		if(isRight)
			listNodesR.push(node);
		else
			listNodesL.push(node);

		var childNodes:Array=[];

		if(withCloud) newSupClouds++;
		//creation of all nodes, folded or not.
		for(var i=0;i<node_xml.childNodes.length;i++){

			n=node_xml.childNodes[i];
			//subnodes
			 if(n.nodeName=="node" && n.attributes.POSITION!=(isRight?"left":"right")){
				var subnode=genNodes(isRight,n,0,0,lineColor,lineWidth,styleNode,styleLine,false,container,newSupClouds);
				childNodes.push(subnode);
			}
		}

		node.childNodes=childNodes;
				
		for (var i=0; i<childNodes.length;i++){
			var enl:Edge=new Edge(((first&&(!isRight))?this.first_node:node),childNodes[i],"",container);
			list_edges.push(enl);
		}

		if(cloudNode!=undefined ){
			if(isRight)
				list_right_clouds.push([cloudNode,node,container,supClouds]);
			else
				list_left_clouds.push([cloudNode,node,container,supClouds]);
			supClouds++;
		}


		for(var i=0;i<arrows.length;i++){
			list_arrows.push([node.getID(),
							arrows[i].attributes.DESTINATION,
							arrows[i].attributes.STARTARROW,
							arrows[i].attributes.ENDARROW,
							getNodeColor(arrows[i])]);
			arrows_map[node.getID()]="";
			arrows_map[arrows[i].attributes.DESTINATION]="";
		}

		return node;
	}


	function getFirstNodeType(type,node_xml){
		for(var i=0;i<node_xml.childNodes.length;i++){
			var n=node_xml.childNodes[i];
			if (n.nodeName==type )
			   return n;
		}
		return null;
	}

	function getNodesType(type,node_xml){
		var aux=[];
		for(var i=0;i<node_xml.childNodes.length;i++){
			var n=node_xml.childNodes[i];
			if (n.nodeName==type )
			   aux.push(n);
		}
		return aux;
	}




	static	function setStaticAtr(nameAtr,value){
		var freeMindVars = SharedObject.getLocal("freeMindBrowser");
		freeMindVars.data[nameAtr]=value;
		freeMindVars.flush();
	}


	static function getStaticAtr(nameAtr,defaultVal){
		var freeMindVars = SharedObject.getLocal("freeMindBrowser");
		if(freeMindVars.data[nameAtr]==null){
			return defaultVal;
		}
		return freeMindVars.data[nameAtr];
	}

	public function upscale(){
		mc_floor._xscale+=20;
		mc_floor._yscale+=20;
		if(scaleTooltips){
			mc_container.tooltip._xscale+=20;
			mc_container.tooltip._yscale+=20;
		}
		regenShadows();
	}
	
	public function adjustToolTips(){
		if(scaleTooltips){
			mc_container.tooltip._xscale=mc_floor._xscale;
			mc_container.tooltip._yscale=mc_floor._yscale;
		}
	}
	
	public function downscale(){
		mc_floor._xscale-=20;
		mc_floor._yscale-=20;
		if(scaleTooltips){
			mc_container.tooltip._xscale-=20;
			mc_container.tooltip._yscale-=20;
		}
		//regenShadows();
	}
	
	public function regenShadows(){
		for(var i=0;i<listNodesL.length;i++){
			listNodesL.genShadow();
		}
		for(var i=0;i<listNodesR.length;i++){
			listNodesR.genShadow();
		}
	}
	
	public function selectNode(nodeId:String){
		mc_floor[nodeId].inst.globalColorSelect();
	}
	
	public function unfoldLocalLink(nodeId:String){
		var nodoXml:XMLNode=mc_floor[nodeId].inst.node_xml;
		trace(nodeId+ " "+nodoXml);
		if(mc_floor[nodeId]._visible==false){
			unfold(nodoXml);
		}else{
			//fold(nodoXml);
		}
		genMindMap(2);
		floor.centerNode(mc_floor[nodeId]);
	}
	
	public function unfoldLinks(node:Node){
		trace("unfolding links for: "+node.getID());
		var str=node.getID();
		var lista:Array=[];
		for( var i=0;i<list_arrows.length;i++){
			if(list_arrows[i][0]==str) lista.push(list_arrows[i][1]);
			if(list_arrows[i][1]==str) lista.push(list_arrows[i][0]);
		}
		
		for(var i=0;i<lista.length;i++){
			trace("encontrado:"+lista[i]);
			var nodoXml:XMLNode=mc_floor[lista[i]].inst.node_xml;
			if(mc_floor[lista[i]]._visible==false){
				unfold(nodoXml);
			}else{
				//fold(nodoXml);
			}
			
		}
		if(lista.length>0) genMindMap(2);
		floor.centerNode(mc_floor[lista[0]]);
	}
	
	private function unfold(elNodoXml:XMLNode){
		var nodoXml=elNodoXml;	 
		while(nodoXml.parentNode!=null){
			nodoXml=nodoXml.parentNode;
			mc_floor[nodoXml.attributes.ID].inst.folded=false;
			mc_floor[nodoXml.attributes.ID].inst.colorNoSelect();
		}
	}
	
	private function fold(elNodoXml:XMLNode){
		var nodoXml=elNodoXml;	 
		while(nodoXml.parentNode!=null){
			nodoXml=nodoXml.parentNode;
			if(nodoXml.attributes.FOLDED=="true")
				mc_floor[nodoXml.attributes.ID].inst.folded=true;
			if(nodoXml.attributes.FOLDED=="false")
				mc_floor[nodoXml.attributes.ID].inst.folded=false;
		}
	}
	

	
	private function getFlashVersion():Number{ 
	 var thisVer = System.capabilities.version.split(","); 
	 var thisVerSpaceNum = thisVer[0].indexOf(" "); 
	 return Number(thisVer[0].substr(thisVerSpaceNum)); 
	}
	
	public function changeFoldedAllNodes(value:Boolean){
		for(var i=0;i<listNodesR.length;i++){
			if(listNodesR[i].childNodes.length>0 && listNodesR[i]!=this.first_node  && listNodesR[i]!=this.first_node_left){
				listNodesR[i].folded=value;
				listNodesR[i].colorNoSelect();
			}
		}
		for(var i=0;i<listNodesL.length;i++){
			if(listNodesL[i].childNodes.length>0 && listNodesL[i]!=this.first_node_left  && listNodesR[i]!=this.first_node){
				listNodesL[i].folded=value;
				listNodesL[i].colorNoSelect();
			}
		}
		genMindMap(2);
	}
	
	public function foldAllFromNode(){
		changeFoldedFromActualNode(true);
	}
	public function unfoldAllFromNode(){
		changeFoldedFromActualNode(false);
	}
	
	public function changeFoldedFromActualNode(value:Boolean){
		var node:Node=Node.overBeforeMenu;
		if(node==undefined || node==first_node || node==first_node_left){
			changeFoldedAllNodes(value);
		}else{
			changeFoldedFromNode(node,value);
		}
		
		genMindMap(2);
	}
	
	public function changeFoldedFromNode(node:Node,value:Boolean){ 
		if(node.childNodes.length>0){
			node.folded=value;
			node.colorNoSelect();
			for(var i=0;i<node.childNodes.length;i++){
				changeFoldedFromNode(node.childNodes[i],value);
			}
		}
	}
	
	public function search(str:String,caseSensitive:Boolean){
		var lista=new Array();
		for(var i=0;i<listNodesR.length;i++){
			if(caseSensitive==false){
				if(listNodesR[i].text.toLowerCase().indexOf(str.toLowerCase(),0)>-1){
					lista.push(listNodesR[i].getID());
				}
			}else{
				if(listNodesR[i].text.indexOf(str,0)>-1){
					lista.push(listNodesR[i].getID());
				}
			}
		}

		for(var i=0;i<listNodesL.length;i++){
			if(caseSensitive==false){
				if(listNodesL[i].text.toLowerCase().indexOf(str.toLowerCase(),0)>-1){
					lista.push(listNodesL[i].getID());
				}
			}else{
				if(listNodesL[i].text.indexOf(str,0)>-1){
					lista.push(listNodesL[i].getID());
				}
			}
		}
		return lista;
	}
	
	function getBounds(){
		var comparator=floor.getCanvas();
		var b=listNodesR[0].ref_mc.getBounds(comparator);
		trace("b:"+b+" comp"+comparator);
		var aux;
		for(var i=0;i<listNodesL.length;i++){
			if(listNodesL[i].ref_mc._visible==true){
				aux=listNodesL[i].ref_mc.getBounds(comparator);
				if(aux.xMin<b.xMin) b.xMin=aux.xMin;
				if(aux.xMax>b.xMax) b.xMax=aux.xMax;
				if(aux.yMin<b.yMin) b.yMin=aux.yMin;
				if(aux.yMax>b.yMax) b.yMax=aux.yMax;
			}
		}
		for(var i=0;i<listNodesR.length;i++){
			if(listNodesR[i].ref_mc._visible==true){
				aux=listNodesR[i].ref_mc.getBounds(comparator);
				if(aux.xMin<b.xMin) b.xMin=aux.xMin;
				if(aux.xMax>b.xMax) b.xMax=aux.xMax;
				if(aux.yMin<b.yMin) b.yMin=aux.yMin;
				if(aux.yMax>b.yMax) b.yMax=aux.yMax;
			}
		}
		return b;
	}
	
	function prepareBounds(){

		for(var i=0;i<listNodesL.length;i++){
			if(listNodesL[i].ref_mc._visible==false){
				listNodesL[i].ref_mc._x=0;
				listNodesL[i].ref_mc._y=0;
			}
		}
		for(var i=0;i<listNodesR.length;i++){
			if(listNodesR[i].ref_mc._visible==false){
				listNodesR[i].ref_mc._x=0;
				listNodesR[i].ref_mc._y=0;
			}
		}
	}
	
	function fitMindMap(){
		var bo=getBounds(); 
		var sx=Stage.width/(bo.xMax-bo.xMin);
		var sy=Stage.height/(bo.yMax-bo.yMin);
		//trace(bo.xMax+" "+bo.xMin+" y:"+bo.yMax+" "+bo.yMin);
		var res=sx>sy?sy:sx;
		mc_floor._xscale=res*100;
		mc_floor._yscale=res*100;
		
		if(scaleTooltips){
			mc_container.tooltip._xscale=res*100;
			mc_container.tooltip._yscale=res*100;
		}
		//bo=getBounds(); 
		mc_floor._x=-bo.xMin*res+(Stage.width-(bo.xMax-bo.xMin)*res)/2;
		mc_floor._y=-bo.yMin*res+(Stage.height-(bo.yMax-bo.yMin)*res)/2;		
		trace(res);
	}
}
