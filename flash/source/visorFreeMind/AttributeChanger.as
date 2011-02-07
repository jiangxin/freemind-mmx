
class visorFreeMind.AttributeChanger
{
	//Where will have the list of changing mclips.
	private static var listTween={};
	private static var mc_advisor;
	private static var inited:Boolean=false;
	
	//
	// The AttributeChanger, will be initialized for creating a mc .
	// Will work base on frames and on time
	// Will change start-end attributes of an mc
	// will allow a group of objects
	// Will return an Id, allowing to stop an effect.
	// And much more. :-)
	//
	static function init()
	{	
		if(!inited){
			inited=true;
			mc_advisor=_root.createEmptyMovieClip("mc_AttributeChanger",_root.getNextHighestDepth());
			mc_advisor.onEnterFrame=function(){
				AttributeChanger.onEnterFrame();
			}
		}
	}
	
	static function getMap(att:String){
		if(listTween[att])
			return listTween[att];
		else{
			listTween[att]={};
			return listTween[att];
		}
	}
	
	static function addWithStart(mc:MovieClip,att:String,start_value:Number,end_value:Number,numFrames){
		if(!inited) init();
		var map=getMap(att);
		map[mc]=[mc,att,start_value,end_value,numFrames,(end_value-start_value)/numFrames];
	}
	
	static function addChange(mc:MovieClip,att:String,end_value:Number,numFrames){
		if(!inited) init();
		var map=getMap(att);
		var start_value=mc[att];
		//trace(start_value);
		map[mc]=[mc,att,start_value,end_value,numFrames,(end_value-start_value)/numFrames];
	}
	
	static function deleteElem(mc:MovieClip){
		for(var map in listTween){
			delete listTween[map][mc];
		}
	}
	
	static function onEnterFrame(){
		//See whats on the list, and take acctions acord to them.
		for(var map in listTween){
			var nmap=listTween[map];
			for(var obj in nmap){
				var elems=nmap[obj];
				//trace(elems);
				elems[4]=elems[4]-1;
				if(elems[4]>=0){
					elems[0][elems[1]]=elems[2];
					elems[2]+=elems[5];
				}

				if(elems[4]==-1){
					elems[0][elems[1]]=elems[3];
					delete nmap[obj];
				}
			}
		}
	}
	
	
	
}
