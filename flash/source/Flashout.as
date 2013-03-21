/**
 * @author Eugene Potapenko (www.potapenko.com)
 *  date created: 01.03.2005 8:27:35
 */
class Flashout extends Object
{
	private var object:Object;
	
	private static var messages:Array = new Array();
	private static var socket:XMLSocket;
	private static var isConnected:Boolean = false;
	
	public static var DEBUG:String = "DEBUG: ";
	public static var INFO:String = "INFO: ";
	public static var WARN:String = "WARN: ";
	public static var ERROR:String = "ERROR: ";
	public static var FATAL:String = "FATAL: ";
	public static var SERVER_IN:String = "SERVER_IN: ";
	public static var SERVER_OUT:String = "SERVER_OUT: ";
	
	//-------------------------------------------------------------------------
	//	constructor
	//-------------------------------------------------------------------------
	
	public function Flashout(object:Object)
	{
		this.object = object;
	}
	
	//-------------------------------------------------------------------------
	//	init
	//-------------------------------------------------------------------------
	
	public static function init()
	{
		if(_root.FLASHOUT_ENABLE == "TRUE" && socket == undefined)
		{
			createSocket();
			
		}else if(isConnected){
			
			for (var i : Number = 0; i < messages.length; i++) {
				socket.send(messages[i]);
			}
			messages = new Array();
		}
	}
	
	/**
     * @deprecated use TRACE(Flashut.DEBUG + message);
     */
	
	public static function log(text:Object):Void
	{
		trace(DEBUG + text);		
	}
	
	/**
     * @deprecated use trace(Flashut.INFO + message);
     */
     
	public static function info(text:Object):Void
	{
		trace(INFO + text);
	}
	
	/**
     * @deprecated use trace(Flashut.WARN + message);
     */
	
	public static function warning(text:Object):Void
	{
		trace(WARN + text);
	}
	
	/**
     * @deprecated use trace(Flashut.DEBUG + message);
     */	
     
	public static function debug(text:Object):Void
	{
		trace(DEBUG + text);
	}
	
	/**
     * @deprecated use trace(Flashut.ERROR + message);
     */	
     
	public static function error(text:Object):Void
	{
		trace(ERROR + text);
	}
	
	/**
     * @deprecated use trace(Flashut.FATAL + message);
     */	
     
	public static function fatal(text:Object):Void
	{
		trace(FATAL + text);
	}
	
	/**
     * @deprecated use trace(Flashut.SERVER_IN + message);
     */
	
	public static function serverIn(text:Object):Void
	{
		trace(SERVER_IN + text);
	}
	
	/**
     * @deprecated use trace(Flashut.SERVER_OUT + message);
     */
	
	public static function serverOut(text:Object):Void
	{
		trace(SERVER_OUT + text);
	}
		
	//-------------------------------------------------------------------------
	//	generateToString
	//-------------------------------------------------------------------------
	
	public function generateToString(className:String, ignoreFields:Array):String
	{
		if(className == undefined) className = "ClassName";
	
		if(!ignoreFields)ignoreFields = new Array();
		
		ignoreFields.push("toString");
		
		var p:Object = this.object.__proto__;
		this.object.__proto__ = undefined;
		
		var result:Array = new Array();
		result.push(className + "{");
		for(var v:String in this.object)
		{
			if(v != "\r" && v != "\n" && v != "\r\n" &&indexOf(ignoreFields, v) == -1)
			{
				var quote:String = "";
				var param:Object = this.object[v];
				
				if(typeof(param) == "string") quote = "\"";
				
				result.push(v + "=" + quote + param + quote);
				result.push(", "); 
			}
		}
		
		if(result.length != 1)
		{
			result.pop();
			result.push("");
		}
		
		result.push("};");
		
		this.object.__proto__ = p;
		
		return result.join("");
	}
	
	//-------------------------------------------------------------------------
	//	indexOf
	//-------------------------------------------------------------------------
	
	private static function indexOf(array:Array, searched:Object):Number
	{
		if(array == undefined)return -1;
		
		for(var i:Number=0;i<array.length;i++)
		{
			if(searched == array[i])
			{
				return i;
			}
		}	
		return -1;
	}
	
	//-------------------------------------------------------------------------
	//	createSocket
	//-------------------------------------------------------------------------
	
	private static function createSocket()
	{
		socket = new XMLSocket();
		socket.onConnect = function(s)
		{
			if(!s)
			{
				return;
			}
			
			var o = Flashout;
			o.isConnected = true;
			
			for (var i : Number = 0; i < o.messages.length; i++) {
				o.socket.send(o.messages[i]);
			}
			o.messages = new Array();
		}
		socket.connect("localhost", parseInt(_root.FLASHOUT_PORT))
	}	
	
	//-------------------------------------------------------------------------
	//	traceReplacer
	//-------------------------------------------------------------------------
	
	public static function traceReplacer(message:Object, classMethodString:String, file:String ,line:Number)
	{
		var d:Date = new Date();
		var timestamp:String = "[" + 
						formatDate(d.getMinutes().toString(), 2)+ 
						":" + 
						formatDate(d.getSeconds().toString(), 2)+ 
						":" + 
						formatDate(d.getMilliseconds().toString(), 3) + 
						"]";
		
		arguments.shift();						
		
		message = _root.FLASHOUT_FILE_ID + "###" + 
					"|" + timestamp + arguments.join("#") + "|" + 
					message;
		messages.push(message);
		
		test(message);		
		init();
	}
	
	//-------------------------------------------------------------------------
	//	formatDate
	//-------------------------------------------------------------------------
	
	private static function formatDate(dateString:String, len:Number):Object
	{
		dateString = "000000" + dateString;
		dateString = dateString.substr(dateString.length - len, len);
		return dateString;
	}
	
	//-------------------------------------------------------------------------
	//	toString
	//-------------------------------------------------------------------------
		
	public function toString():String 
	{
		return (new Flashout(this.object))
			.generateToString("Object", ["object", "task"]);
	}
	
	//-------------------------------------------------------------------------
	//	log
	//-------------------------------------------------------------------------
	
	public static function test(message:Object)
	{
		_root.test_txt.text += message + "\r";
	}
}