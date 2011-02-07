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
import flash.filters.DropShadowFilter;
import flash.net;

/**
* Nodes, represent the mindmaps nodes
*/

class visorFreeMind.Node {
	public static var num:Number=2000; // counter of nodes
	public static var colorSel:Number=0xBBBBBB; // select color
	public static var colorNoSel:Number=0xFFDD44; // unselect color
	public static var defaultWordWrap:Number=600;
	public static var currentOver:Node=null;
	public static var overBeforeMenu:Node=undefined;
	public static var lastOverTxt=null;
	public static var openUrl="_blank";
	public static var mainNodeShape=null;
	public static var baseImagePath:String="";
	
	public var childNodes:Array;
	private var lastClicks:Number=0;
	public var text:String; // Text of the node
	private var coment:String;
	private var _x:Number; // xpos of the node
	private var _y:Number; // ypos of the node
	private var node_xml:XMLNode;
	public var ref_mc:MovieClip;
	private var over:Boolean;
	private var id:String;
	private var lastclick:Number=0;
	private var edges_ar:Array=null;
	private var open_intv:Number=0;
	private var close_intv:Number=0;
	public var cf:Number=0xCC0000;
	public var cfont:Number=0x444444;
	public var cbg:Number=0;
	public var style:Number=1; // 0= elipse, 1=fork , 2=bubble
	public var lineWidth:Number=0;
	public var styleLine:Number=0; //0=llave, 1=Linea,2=sharp_llave,3=sharp_linea
	public var folded:Boolean;
	public var isRight:Boolean;
	public var haveEdge:Boolean;
	public var withCloud:Boolean;
	public var textSize:Number;
	public var italic:Boolean;
	public var bold:Boolean;
	public var font:String;
	public var hgap:Number=0;
	public var shift_y:Number=0;
	
	private var link:MovieClip;
	private var noteIcon:MovieClip;
	private var atrsIcon:MovieClip;
	private var withImage:Boolean=false;
	private var listElements:Array=null;
	private var counter=0;
	private var browser:Browser;
	private var box_txt;
	private var sombra=null;
	private var eventControler;
	private var note=null;
	private var atributes=null;
	private var richText=false;

	function getAtributes():Array{
		return atributes;
	}
	
	function getNode_xml():XMLNode{
		return node_xml;
	}
	
	function getID(){
		return id;
	}

	function Node(x:Number,y:Number,node_xml:XMLNode,coment:String,mc:MovieClip,
							yy,cf:Number,lineWidth:Number,style:Number,styleLine:Number,
							folded:Boolean,isRight:Boolean,withCloud:Boolean,
							textSize:Number,italic:Boolean,bold:Boolean,font:String,browser:Browser){
		//trace("creando nodo:"+node_xml.attributes.TEXT);
		this.cf=cf;
		this.lineWidth=lineWidth;
		this.style=style;
		if(style==0 && Node.mainNodeShape=="rectangle")
			this.style=2;
		if(style==0 && Node.mainNodeShape=="none")
			this.style=-1;
		this.styleLine=styleLine;
		this.folded=folded;
		this.isRight=isRight;
		this.withCloud=withCloud;
		this.textSize=textSize;
		this.italic=italic;
		this.bold=bold;
		this.font=font;
		this.browser=browser;
		this.node_xml=node_xml;
		this.haveEdge=node_xml.attributes.LINK!=undefined?true:false;
		//richtext not suported, quick fix for images.
		text=getText(node_xml);
		note=findNote(node_xml);
		atributes=findAtributes(node_xml);
		coment=coment;
		listElements=[];
		num+=4;
		//creation of asociated movieClip
		id=node_xml.attributes.ID?node_xml.attributes.ID:"node_"+num;
		ref_mc=mc.createEmptyMovieClip(id,num);
		//ref_mc.trackAsMenu=true;
		box_txt=ref_mc.createEmptyMovieClip("box_txt",11);
		ref_mc.node_txt=ref_mc.createEmptyMovieClip("node_txt",12);
		//ref_mc.node_txt.trackAsMenu=true;
		ref_mc._x=x;
		ref_mc._y=y;
		ref_mc._visible=false;

		ref_mc.inst=this; // add a reference to the Node object that create it
		ref_mc.box_txt.inst=this;

		//Font color
		if(node_xml.attributes.COLOR!=undefined){
			var cn:String=node_xml.attributes.COLOR;
			cfont=new Number("0x"+cn.substring(1));
		}
		
		//Background color
		if(node_xml.attributes.BACKGROUND_COLOR!=undefined){
			var cn:String=node_xml.attributes.BACKGROUND_COLOR;
			cbg=new Number("0x"+cn.substring(1));
		}
		
		//HGAP
		if(node_xml.attributes.HGAP!=undefined){
			hgap=new Number(node_xml.attributes.HGAP);
			if(hgap>0) hgap-=20;
			//trace("hgap:"+hgap);
		}
		
		//SHIFT_Y
		if(node_xml.attributes.SHIFT_Y!=undefined){
			shift_y=new Number(node_xml.attributes.SHIFT_Y);
		}
		
		//VSHIFT
		if(node_xml.attributes.VSHIFT!=undefined){
			shift_y=new Number(node_xml.attributes.VSHIFT);
		}
		
		// -----------EVENTS OF NODE -----------
		eventControler=ref_mc;
		//For eliminating blinking of the node, if with shadow
		if(style!=1 && cbg!=0)
			eventControler=ref_mc.box_txt;
		eventControler.useHandCursor = false;
		activateEvents();
	}

	function getText(node_xml:XMLNode){
		trace(node_xml.attributes.TEXT);
		if(node_xml.attributes.TEXT!=undefined){//ff
			if(node_xml.attributes.TEXT.indexOf("<html>")>=0)
				richText=true;
			return node_xml.attributes.TEXT;
		}else{//richcontent
			richText=true;
			for(var i=0;i<node_xml.childNodes.length;i++){
				if(node_xml.childNodes[i].nodeName=="richcontent" &&
					node_xml.childNodes[i].attributes.TYPE=="NODE"){
					//trace(node_xml.childNodes[i].firstChild.toString());
					return node_xml.childNodes[i].firstChild.toString();
				}
			}
		}
	}
	
	function findNote(node_xml:XMLNode){
		var lista=getNodesType("hook",node_xml);
		for(var i=0;i<lista.length;i++){
			var hook=lista[i];
			if(hook.attributes.NAME.indexOf("NodeNote")!=-1)
				return hook.firstChild.firstChild.toString();
		}
		//version 9
		for(var i=0;i<node_xml.childNodes.length;i++){
				if(node_xml.childNodes[i].nodeName=="richcontent" &&
					node_xml.childNodes[i].attributes.TYPE=="NOTE"){
					//trace(node_xml.childNodes[i].firstChild.toString());
					return node_xml.childNodes[i].firstChild.toString();
				}
			}
		
		return null;
	}

	function findAtributes(node_xml:XMLNode){
		var lista=getNodesType("attribute",node_xml);
		if(lista.length>0){
			return lista;
		}else
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

	
	public function deactivateEvents(){
		eventControler.enabled=false;
	}

  // changes from https://sourceforge.net/projects/freemind/forums/forum/22101/topic/1423817
       // public method for url encoding
       public function encodeUTF8(string:String):String 
       {
         var utftext = "";

         for (var n = 0; n < string.length; n++) {

                    var c = string.charCodeAt(n);

                    if (c < 128) {
                        utftext += String.fromCharCode(c);
                    }
                    else if((c > 127) && (c < 2048)) {
                        utftext += String.fromCharCode((c >> 6) | 192);
                        utftext += String.fromCharCode((c & 63) | 128);
                    }
                    else {
                        utftext += String.fromCharCode((c >> 12) | 224);
                        utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                        utftext += String.fromCharCode((c & 63) | 128);
                    }
                }

                return utftext;
        }



	public function activateEvents(){
		if(eventControler.enabled==false){
			eventControler.enabled=true;
			return;
		}

		eventControler.onPress=function(){
			if(this.inst.node_xml.attributes.LINK != undefined && this.inst.link.hitTest(_root._xmouse,_root._ymouse,false)){
				var url:String=this.inst.node_xml.attributes.LINK;
				this.inst.browser.hideTooltip();
				if(url.indexOf("#")==0){ //jump to the local node
					this.inst.browser.unfoldLocalLink(url.substr(1,url.length-1));
					trace("local jump");
					return;
				}
				if(url.indexOf("http://") > -1 || url.indexOf(".mm")==-1){
					//trace("versionss: "+Browser.flashVersion);
					if(Browser.flashVersion>=9){
						//sendToURL(new URLRequest(url),Node.openUrl);
						//getURL(url,Node.openUrl);
						try {
                			getURL(url,Node.openUrl);
//proposal from https://sourceforge.net/projects/freemind/forums/forum/22101/topic/1423817:
//                			getURL(this.inst.encodeUTF8(url),Node.openUrl); 
            			}
            			catch (e:Error) {
                				this.inst.browser.showTooltip("error:  "+e,14,20);
            			}
					}else{
                				getURL(url,Node.openUrl);
//proposal from https://sourceforge.net/projects/freemind/forums/forum/22101/topic/1423817:
//						getURL(this.inst.encodeUTF8(url),Node.openUrl); 
					}
				}else{
					//have in mind directory change, it works diferent in Freemind
					this.inst.browser.historyManager.loadXML(url);
				}
				return;
			}

		    if(Key.isDown(Key.CONTROL)){
		    	this.inst.browser.unfoldLinks(this.inst);
		    	return;
		    }
			if(this.inst.hasSubnodes() && this.inst.style>0){ // we don´t want main node to fold
				if(this.inst.folded){
					this.inst.node_xml.attributes.FOLDED="false";
					this.inst.folded=false;
					this.inst.colorSelect();
					if(this.inst==this.inst.browser.first_node){
						//trace("folding first"+this.inst.browser.first_node_left);
						this.inst.browser.first_node_left.folded=false;
						this.inst.browser.first_node_left.node_xml.attributes.FOLDED="false";
					}
				}else{
					this.inst.node_xml.attributes.FOLDED="true";
					this.inst.folded=true;
					this.inst.colorSelect();
					if(this.inst==this.inst.browser.first_node){
						this.inst.browser.first_node_left.folded=true;
						this.inst.browser.first_node_left.node_xml.attributes.FOLDED="true";
					}
				}
				this.inst.lastClick=0;
				
				this.inst.browser.genMindMap(2);
			}
			return;
		}
		
		eventControler.onRollOver=function(){
			this.inst.globalColorSelect();
			this.inst.browser.floor.showNode(this.inst.ref_mc);
			
			if((this.inst.noteIcon!=null) || (this.inst.atrsIcon!=null) ||
				(this.inst.node_xml.attributes.LINK != undefined) ) {
				this.onMouseMove=function(){
					if(this.inst.noteIcon.hitTest(_root._xmouse,_root._ymouse,false)){
						this.inst.browser.showTooltip(this.inst.note,14,20);
						this.useHandCursor = true;
					}
					else if(this.inst.link.hitTest(_root._xmouse,_root._ymouse,false)){
						this.inst.browser.showTooltip(this.inst.node_xml.attributes.LINK,14,20);
						this.useHandCursor = true;
					}
					else if(this.inst.atrsIcon.hitTest(_root._xmouse,_root._ymouse,false)){
						AtrsShow.showAtrs(this.inst,this.inst.browser);
						this.useHandCursor = true;
					}
					else{
						Node.currentOver=this.inst;
						this.inst.browser.hideTooltip();
						AtrsShow.hideAtrs();
						this.useHandCursor = false;
					}
				}
			}
			
		}

		eventControler.onRollOut=function(){
			this.inst.browser.hideTooltip();
			this.inst.colorNoSelect();
			//trace("onRollOut");
			Node.currentOver=null;
			if(this.inst.noteIcon!=null  || (this.inst.atrsIcon!=null) ||
				(this.inst.node_xml.attributes.LINK != undefined)) {
				this.onMouseMove=null;
			}
		}
		
	}

	public function globalColorSelect(){
		//trace("globalColorSelect");
		if (Node.currentOver instanceof Node ){
			Node.currentOver.colorNoSelect();
			trace("globalColorSelect: currentOver  exist");
		}
		Node.currentOver=this;	
		colorSelect();
	}
	
	static function saveTxt(){
		Node.overBeforeMenu=undefined;
		if (Node.currentOver instanceof Node ){
			var node=Node.currentOver;
			Node.overBeforeMenu=Node.currentOver;
			if(node.noteIcon!=null and 	node.noteIcon.hitTest(_root._xmouse,_root._ymouse,false)){
					Node.lastOverTxt=node.note.replace("\n","\r\n");
			}else if(node.link!=null and 	node.link.hitTest(_root._xmouse,_root._ymouse,false)){
					Node.lastOverTxt=node.node_xml.attributes.LINK;
			}else	if(node.atrsIcon!=null and 	node.atrsIcon.hitTest(_root._xmouse,_root._ymouse,false)){
				    var result="";
			   		for(var i=0;i<node.atributes.length;i++){
						var atr:XMLNode=node.atributes[i];
						if(i>0) 
						  result+="\r\n";
						result+=atr.attributes.NAME +"\t"+atr.attributes.VALUE;
					}
					Node.lastOverTxt=result;
			}else{
				Node.lastOverTxt=node.text.replace("\n","\r\n");
			}
		}else{
			Node.lastOverTxt="";
		}
	}
	
	function hasSubnodes(){
		if(node_xml.childNodes.length==0) return false;
		for(var i=0;i<node_xml.childNodes.length;i++){
			if(node_xml.childNodes[i].nodeName=="node") return true;
		}
		return false;
	}


	public function addEdge(e:Edge){
		edges_ar.push(e);
	}

	public function colorSelect(){
		drawAroundNode(colorNoSel,100,true);
	}

	public function colorNoSelect(){
		drawAroundNode(cbg,100,false);
	}

	// draw ovals/circles
	private function circle2(x,y,width,height,color:Number){
		var a=width;
		var b=height;
		var j=a*0.70711;
		var n=b*0.70711;
		var i=j-(b-n)*a/b;
		var m=n-(a-j)*b/a;
		ref_mc.lineStyle(lineWidth,color,100);
		ref_mc.beginFill(color,0);
		ref_mc.moveTo(x+a,y);
		ref_mc.curveTo(x+a,y-m,x+j,y-n);
		ref_mc.curveTo(x+i,y-b,x,y-b);
		ref_mc.curveTo(x-i,y-b,x-j,y-n);
		ref_mc.curveTo(x-a,y-m,x-a,y);
		ref_mc.curveTo(x-a,y+m,x-j,y+n);
		ref_mc.curveTo(x-i,y+b,x,y+b);
		ref_mc.curveTo(x+i,y+b,x+j,y+n);
		ref_mc.curveTo(x+a,y+m,x+a,y);
		ref_mc.endFill();
	}

	private function circle(width,height,color:Number,alpha:Number,colorLine:Number){
		box_txt.clear();
		var x=(width-4)*0.5;
		var y=(height-4)*0.5;
		var a=width*0.5;
		var b=height*0.80;
		var j=a*0.70711;
		var n=b*0.70711;
		var i=j-(b-n)*a/b;
		var m=n-(a-j)*b/a;
		box_txt.lineStyle(1,colorLine,100);
		box_txt.beginFill(color,alpha);
		box_txt.moveTo(x+a,y);
		box_txt.curveTo(x+a,y-m,x+j,y-n);
		box_txt.curveTo(x+i,y-b,x,y-b);
		box_txt.curveTo(x-i,y-b,x-j,y-n);
		box_txt.curveTo(x-a,y-m,x-a,y);
		box_txt.curveTo(x-a,y+m,x-j,y+n);
		box_txt.curveTo(x-i,y+b,x,y+b);
		box_txt.curveTo(x+i,y+b,x+j,y+n);
		box_txt.curveTo(x+a,y+m,x+a,y);
		box_txt.endFill();
	}
	// draw rounded rectangle
	private function round_rectangle(w:Number,h:Number,color:Number,alpha:Number,colorLine:Number){
		box_txt.clear();

		var incx=2;
		var d=2;
		var a=2;
		box_txt.lineStyle(1,colorLine,100);
		box_txt.beginFill(color,alpha);
		/*
		// Temporal
		box_txt.roundRect(0,0,w,h,4);
		box_txt.endFill();
		return;
		//--------------- test ---------
		*/
		box_txt.moveTo(a-incx,a-d);
		box_txt.lineTo(w-a+incx,a-d);
		//box_txt.curveTo(w-a-0.5+d+incx,a+0.5-d,w-a+d+incx,a);
		box_txt.lineTo(w-a+d+incx,a);
		box_txt.lineTo(w-a+d+incx,h-a);
		box_txt.lineTo(w-a+d+incx,h-a+d);
		//box_txt.curveTo(w-a-0.5+d+incx,h-0.5-a+d,w-a+incx,h-a+d);
		box_txt.lineTo(w-a+incx,h-a+d);
		box_txt.lineTo(a-incx,h-a+d);
		//box_txt.curveTo(a-d-incx,h-a+d,a-d-incx,h-a);
		box_txt.lineTo(a-d-incx,h-a);
		box_txt.lineTo(a-d-incx,a);
		//box_txt.curveTo(a-d-incx,a-d,a-incx,a-d);
		box_txt.lineTo(a-incx,a-d);
		box_txt.endFill();
	}

	private function sel_subline(w:Number,h:Number,color:Number,alpha:Number,colorLine:Number){
		box_txt.clear();

		var incx=2;
		var d=2;
		var a=4;
		box_txt.lineStyle(1,colorLine,100);
		box_txt.beginFill(color,alpha);
		box_txt.moveTo(a-incx,a-d);
		box_txt.lineTo(w-a+incx,a-d);
		box_txt.curveTo(w-a+d+incx,a-d,w-a+d+incx,a);
		box_txt.lineTo(w-a+d+incx,h-a);
		box_txt.curveTo(w-a+d+incx,h-a+d,w-a+incx,h-a+d);
		box_txt.lineTo(a-incx,h-a+d);
		box_txt.curveTo(a-d-incx,h-a+d,a-d-incx,h-a);
		box_txt.lineTo(a-d-incx,a);
		box_txt.curveTo(a-d-incx,a-d,a-incx,a-d);
		box_txt.endFill();
		a=0;
		ref_mc.lineStyle(lineWidth,cf,100);
		ref_mc.moveTo(0,h-a);
		ref_mc.lineTo(w,h-a);
	}

	private function subline(w:Number,h:Number,color:Number,alpha){
		var a=0;
		ref_mc.clear();
		box_txt.clear();
		ref_mc.lineStyle(lineWidth,cf,100);
		ref_mc.moveTo(0,h-a);
		ref_mc.lineTo(w,h-a);
		//ref_mc.dashTo(0,h-a, w,h-a, 1, 2,lineWidth);
	}

	public function crearTextField(name_txt:String){
		//trace (Flashout.DEBUG +"create TextField "+name_txt+" "+text);
		if(richText && text.indexOf("img src=")&&(text.indexOf(".jpg")>=0 || text.indexOf(".jpeg")>=0  || text.indexOf(".png")>=0 || text.indexOf(".gif")>=0 || text.indexOf(".JPG")>=0 || text.indexOf(".JPEG")>=0 || text.indexOf(".PNG")>=0 || text.indexOf(".GIF")>=0)){
			var start=text.indexOf("img src=")+9;
			var length;
			if (text.indexOf(".jpg")>=0) length=text.indexOf(".jpg")+4-start;
			else if (text.indexOf(".jpeg")>=0) length=text.indexOf(".jpeg")+4-start;
			else if (text.indexOf(".png")>=0) length=text.indexOf(".png")+4-start;
			else if (text.indexOf(".gif")>=0) length=text.indexOf(".gif")+4-start;
			else if (text.indexOf(".JPG")>=0) length=text.indexOf(".JPG")+4-start;
			else if (text.indexOf(".JPEG")>=0) length=text.indexOf(".JPEG")+4-start;
			else if (text.indexOf(".PNG")>=0) length=text.indexOf(".PNG")+4-start;
			else if (text.indexOf(".GIF")>=0) length=text.indexOf(".GIF")+4-start;
			// have to wait for the image load.
			var cont_image=ref_mc.node_txt.createEmptyMovieClip("node_image",2);
			//Have to use the Flash Loader
			var imgtext=text.substr(start,length);
			trace("cargamos:"+baseImagePath+imgtext);
			trace("baseImagePath:"+baseImagePath);
			browser.loadImage(baseImagePath+imgtext,cont_image);
			withImage=true;
			var aux=text.substr(start);
			var auxi=aux.indexOf(">");
			text=text.substr(0,start-10)+text.substr(start+auxi+1);
			//trace("text:"+text);
			if(text=="<html>") return;
		}


		ref_mc.node_txt.createTextField(name_txt,3,0,0,0,0);
		var my_fmt:TextFormat = new TextFormat();
		if(richText){
			ref_mc.node_txt.node_txt.html=true;
			ref_mc.node_txt.node_txt.multiline=true;
			ref_mc.node_txt.node_txt.htmlText=text;
			//trace (" textfield html text="+text+"--");			
		}else{
			ref_mc.node_txt.node_txt.text=text;
			//trace ("textfield text="+text+"--");
		}
		var txt=ref_mc.node_txt.node_txt;
		txt.background=false;
		txt.backgroundColor=0xFFAAAA;
		txt.autoSize=true;
		txt.selectable=false;
		txt.border=false;
		txt.multiline = true;
		//txt.wordWrap = true;
		my_fmt.color=cfont;
		my_fmt.font=font;
		my_fmt.size=textSize;
		my_fmt.bold=bold;
		my_fmt.italic=italic;
		txt.setTextFormat(my_fmt);
		//WordWrap
		if(txt._width>defaultWordWrap){
			txt._width=defaultWordWrap;			
			txt.wordWrap=true;
		}

	}

	public function drawAroundNode(colorNoSel,alpha,isSelected){
		ref_mc.clear();
		if (colorNoSel==0)
			alpha=0;
		// 0= ellipse, 1=fork , 2=bubble
		
		//n NODE_TXT
		var n=ref_mc.node_txt;
		
		// Style==2 Bubble
		if  (style==2){
			round_rectangle(n._width, n._height,colorNoSel,alpha,cf);
			if( browser.withShadow)
				addSpaceForShadow();
			if  ( alpha==100 && cbg!=0 && browser.withShadow && false){
				if(isSelected){
					round_rectangle(n._width, n._height,cbg,alpha,cf);
					sombra._visible=false;
				}else{
					sombra._visible=true;
				}
			}
		}
		else if(style==0){// ELLIPSE
			circle(n._width+4, n._height+4,colorNoSel,alpha,cf);
			if(Browser.flashVersion>7 && browser.withShadow){
				createDropShadowRectangleF8(ref_mc);
			}
		}
		else if(style==-1){// NONE
			//circle(n._width+4, n._height+4,colorNoSel,alpha,cf);
			if(Browser.flashVersion>7 && browser.withShadow){
				createDropShadowRectangleF8(ref_mc);
			}
		}
		else{// FORK
			if(alpha==100)//selected
				sel_subline(n._width, n._height,colorNoSel,alpha,colorSel);
			else
				subline(n._width,n._height,colorNoSel,alpha);
		}
		
		//IF folded, draw a circle meaning, can be unfolded
		if (folded==true){
		    drawCircle(n._width,n._height,cf);
		}
	}



	private function drawCircle(w,h,color){
		if(style==1){
			if(!isRight) w=-4;
		    circle2(w+2,h,2,2,color);
		}else{
			if(!isRight) w=-9;
			circle2(w+5,h/2,2,2,color);
		}
	}

	public function draw(){
		counter++;
		crearTextField("node_txt");

		var iconsList=getIcons(node_xml);
		var numIcons=0;
		for(var i=0;i<iconsList.length;i++){
			var name=iconsList[i].replace("-","_");
			//trace ("icon "+name);
			if(Icons["get_"+name]!=null){
				addIcon(Icons["get_"+name](ref_mc.node_txt,numIcons++));
				//trace ("add icon "+name);
			}
		}
		
		addSpecialIcons(numIcons);

		if(withImage==false) 
		posElements();

		drawAroundNode(cbg,100,false);
	}

	function calcInitIconY(){
		var initY=0;
		if(ref_mc.node_txt.node_txt!=undefined)
			initY=(Math.max(ref_mc.node_txt.node_txt._height,16)-16)/2;
		if (ref_mc.node_txt.node_image!=undefined){
				var aux_y=(Math.max(ref_mc.node_txt.node_image._height,16)-16)/2;
				if(initY<aux_y){
					initY=(initY<aux_y?aux_y:initY);
				}		
		}
		if(initY>600) initY=16;
		return initY;
	}

	function posIcons(initX,initY){
			for(var i=0;i<listElements.length;i++){
				listElements[i]._x=initX;
				listElements[i]._y=initY;
				initX+=listElements[i]._width;
			}
		return initX;
	}
	
	function posTextImag(initX,initY){
		if (ref_mc.node_txt.node_image!=undefined){
				ref_mc.node_txt.node_image._x=initX;
				initX+=ref_mc.node_txt.node_image._width;
			}

		if(ref_mc.node_txt.node_txt!=undefined){
			ref_mc.node_txt.node_txt._x=initX;
			initX+=ref_mc.node_txt.node_txt._width;
			//trace ("long text="+ref_mc.node_txt.node_txt._width);
		}
		return initX;
	}
	
	function posSpecialIcons(initX,initY){
		if(atrsIcon){
			atrsIcon._x=initX;
			atrsIcon._y=initY;
			initX+=atrsIcon._width+2;
		}
		if(noteIcon){
			noteIcon._x=initX;
			noteIcon._y=initY;
			initX+=noteIcon._width+2;
		}
		if(link){
			link._x=initX;
			link._y=initY;
			initX+=link._width+2;
		}
		return initX;
	}
	
	function posElements(){
		//First the maxY
		var initY=calcInitIconY();
		var initX=0;
		//Now X
		if(isRight){
			initX=posIcons(0,initY);
			initX=posTextImag(initX,initY);
			posSpecialIcons(initX,initY);
			
		}else{
			initX=posSpecialIcons(initX,initY);
			initX=posTextImag(initX,initY);
			initX=posIcons(initX,initY);
		}

		genShadow();
	}

	public function delShadow(){
		if(sombra!=null){
			sombra.removeMovieClip();
			sombra=null;
		}
	}
	public function genShadow(){
		delShadow();
		if(style==2 && cbg!=0 && browser.withShadow && ref_mc.node_txt._width>0){
			if(Browser.flashVersion>7){
				createDropShadowRectangleF8(ref_mc);
			}else{
				sombra=ref_mc.createEmptyMovieClip("sombra",10);
				ref_mc.node_txt.dropShadow(8,6,4,0x555555,sombra);
			}
		}
	}
	
	function addSpaceForShadow(){
		box_txt.lineStyle(1,"red",0);
		box_txt.moveTo(0,ref_mc.node_txt._height);
		box_txt.lineTo(0,ref_mc.node_txt._height+6);
	}
	
	function createDropShadowRectangleF8(art:MovieClip) {
		var filter:DropShadowFilter = new DropShadowFilter(4, 45, 0x000000, .6, 4, 4, 1, 3, false, false, false);
		var filterArray:Array = new Array();
		filterArray.push(filter);
		art.filters = filterArray;
		art.filter=filter;
	}
	
	public function addIcon(icon){
		listElements.push(icon);
		return;
	}

	public function addSpecialIcons(numIcons){
		if(haveEdge){
			var direction=node_xml.attributes.LINK;
			if(direction.indexOf(".mm")>-1 &&
			   direction.indexOf(".mm")==direction.length-3)
				link=Icons.get_mm_link(ref_mc.node_txt,numIcons++);
			else if(direction.indexOf("#")==0){
				link=Icons.get_inner_link(ref_mc.node_txt,numIcons++);
			}else
				link=Icons.get_link(ref_mc.node_txt,numIcons++);
		}		
		if(note!=null){
			noteIcon=Icons.get_Note(ref_mc.node_txt,numIcons++);
		}
		if(atributes!=null){
			atrsIcon=Icons.get_Atrs(ref_mc.node_txt,numIcons++);
		}

	}

	private function getIcons(node_xml){
		var iconsList=[];
		for(var i=0;i<node_xml.childNodes.length;i++){
			var n=node_xml.childNodes[i];
			if (n.nodeName=="icon" && n.attributes.BUILTIN!=null)
				iconsList.push(n.attributes.BUILTIN);
		}
		return iconsList;
	}

}
