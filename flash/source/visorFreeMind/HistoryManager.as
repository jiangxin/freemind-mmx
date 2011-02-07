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

class visorFreeMind.HistoryManager {
	
	private var xmlData; // load mm files
	private var xmlCurrent; // The actual xml viewed
	public var visitedMM:Array=[]; // for navigating among visited mm.
	public var fileName;
	private var dictVisitedMM={}; // Dictionary of visited mm.
	private var dictVisitedMMLevel={}; // Dictionary of visited mm level.
	public var posXmls:Number=-1;	
	private var browser:Browser;
	public var pt:PictureTaker;
	public var startingXmlForGenAll;
	
	private static var hm:HistoryManager;
	public static var genAllShots:Boolean=false;
	private var listForAllShots:Array=[];
	
	function HistoryManager(browser:Browser){
		this.browser=browser;
		pt=new PictureTaker(browser);
		HistoryManager.hm=this;
	}
	
	function contTimes(str,strBuscado){
		var cont=0;
		while(str.lastIndexOf(strBuscado)!=-1){
			cont++;
			str=str.substr(0,str.lastIndexOf(strBuscado));	
		}
		return cont;
	}
	
	
	public function getXML(){
		return dictVisitedMM[fileName];
	}
	
	function historyJump(fn:String){
			pt.takeShot(fileName);
			fileName=fn;
			trace("llamando a takeShot con "+fn);
			genMindMap(1);		
	}
	
	function genGoodFileName(fn:String){
		if(fileName!=undefined && fileName.lastIndexOf("/")!=-1){
			var actualDepth=fileName.substr(0,fileName.lastIndexOf("/"));
			var numUp=contTimes(fn,"..");
			if(numUp!=0){
				while(numUp!=0){
					numUp--;
					var lio=actualDepth.lastIndexOf("/");
					if(lio==-1)
						actualDepth="";
					else
						actualDepth=actualDepth.substr(0,lio);
				}
				fn=fn.substr(fn.lastIndexOf("..")+3,fn.length);
			}
			if(actualDepth!="")
				fn=actualDepth+"/"+fn;
			//trace(Flashout.INFO+actualDepth+" "+fn+" "+numUp);
		}
		return fn;
	}
	
	function loadXML(fn:String){
		//first validate if we have it,modify all relative to main mm.
		trace("file input:"+fn);
		fn=genGoodFileName(fn);
		simpleLoadXML(fn);		
	}

	function simpleLoadXML(fn:String){
		if(dictVisitedMM[fn]==undefined){
			xmlData=new XML();
			fileName=fn;
			xmlData.ignoreWhite=true;
			xmlData.onLoad=this.loadedXML;
			xmlData.load(fn);
		} else{
			takeShotIfNew();
			fileName=fn;
			trace("llamando a takeShot con "+fn);
			genMindMap(1);
		}
	}
	
	function genShotsForLinkedMaps(){
		genAllShots=true;
		Browser.unfoldAll=true;
		startingXmlForGenAll=fileName;
		genMMEnded();
	}
	
    function genMMEnded(){
    	trace("MindMap fully generated");
    	if(genAllShots) {
    		trace("generating all mm: "+browser.links_mm.length);
    		takeShotIfNew();
    		for(var i=0;i<browser.links_mm.length;i++){
    			trace(browser.links_mm[i]);
    			listForAllShots.push(genGoodFileName(browser.links_mm[i]));
    		}
    		
    		for(var i=0;i<listForAllShots.length;i++){
    			if(dictVisitedMM[listForAllShots[i]]==undefined){
    				simpleLoadXML(listForAllShots[i]);
    				return;
    			}
    		}
    		//if reached no more to lookfor
    		genAllShots=false;
    		Browser.unfoldAll=false;
    		if(startingXmlForGenAll!=undefined)
    			simpleLoadXML(startingXmlForGenAll);
    		else
    			simpleLoadXML(visitedMM[0]);
    	}
    }
    
	function takeShotIfNew(){
				pt.takeShot(fileName);
	}
	
	function loadedXML(loaded) {
		trace("en loaded: "+loaded);
		if (loaded) {
		  HistoryManager.hm.genMindMap(0);
		} else {
			trace("file not loaded!");
		}
	}
	
	function genMindMap(jumpType){
		  trace("en genMindMap");
		  gestHistory(jumpType);
		  browser.genMindMap(jumpType);
	}
	
	function deleteForwardHistory(){
		while(visitedMM.length!=(posXmls+1)){
			visitedMM.pop(); //clean olds
		}
	}


	public function backward(){
		if(posXmls>0){
			takeShotIfNew();
			posXmls--;
			fileName=visitedMM[posXmls];
			//pt.removeShot();
			genMindMap(3);
		}
	}
	
	public function forward(){
		if(posXmls<(visitedMM.length-1)){
			posXmls++;
			fileName=visitedMM[posXmls];
			//pt.takeShot(fileName);
			genMindMap(3);
		}
	}
	
	function gestHistory(jumpType){
		if(jumpType==0){ // 0=new
			dictVisitedMM[fileName]=xmlData;
			deleteForwardHistory();
			posXmls++;
			if(visitedMM.length>0)
				pt.takeShot(visitedMM[visitedMM.length-1]);
			visitedMM.push(fileName);
		}
		if(jumpType==1){ // 1=visited
			deleteForwardHistory();
			posXmls++;
			visitedMM.push(fileName);
		}
	}
}