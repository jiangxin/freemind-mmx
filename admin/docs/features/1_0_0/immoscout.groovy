/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2013 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
 */
import java.io.*;
import java.net.*;

def IMMO = "http://www.immobilienscout24.de";

def urlMap = [ : ];
def nodeMap = [];
// add all childs of the current node (and not itself, as this is the base link)
nodeMap.addAll(node.getChildren())
while(nodeMap.size > 0) {
	def currentNode = nodeMap.pop();
	nodeMap.addAll(currentNode.getChildren());
	def link = currentNode.getLink();
	if(link != null  && link.startsWith(IMMO)){
		urlMap [  link ] = currentNode;
		//println "Found link " + link;
	}
}

def urlSc;
urlSc = node.getLink();

while(urlSc != null) {
	println "Next page to load: " + urlSc;
	urlSc = getUrl(urlMap, urlSc, IMMO);
	if("ERROR".equals(urlSc)) {
		return null;
	}
	//break;
}

// process remaining nodes, that are no longer in the web pages:

urlMap.keySet().each { link ->
	if(! link.startsWith(IMMO) )
		return;
	removedNode = urlMap[link];
	changeIcon(removedNode, freemind.modes.MindIcon.factory("button_cancel"));
	println "Removed from page T: " + removedNode;
}

return null;

def getUrl(HashMap urlMap, String urlT, String IMMO) {
	def found = null;
	try {
		URL url = new URL(urlT);
		def inp = new BufferedReader(new InputStreamReader(url.openStream(), "ISO-8859-1"));
		String str;
		def pattern = ~/.*<a href="(\/expo[^?";]*)".*title="([^"]*)".*/;
		def noExposeLinkPattern = ~/.*#is24-ex-floorplans.*/;
		def nextPagePattern = ~/.*<a class="pull-left" data-is24-qa="paging_bottom_next" href="(\/[^"]*)".*/;
		def additionalTitlePattern = ~/.*<dt class="title">(.*)<\/dt>.*$/;
		def additionalValuePattern = ~/.*<dd class="value">(.*)<\/dd>.*$/;
		def additionalPropertyPattern = ~/<li class="title">([^<]*)<\/li>/;
		def currentNode = null;
		def newNode = false;
		def price = 0;
		def qmeters = 1;
		def additionalTitle = "";
		while ((str = inp.readLine()) != null) {
			// str is one line of text; readLine() strips the newline character(s)
			def matcher = pattern.matcher(str);
			if(matcher.matches() && ! noExposeLinkPattern.matcher(str).matches()) {
				currentNode = null;
				newNode = false;
				price = 0;
				qmeters = 1;
				def link = IMMO + matcher[0][1];
				println "Looking at " + str + " with link " + link;
				def title = matcher[0][2];
				if(urlMap.containsKey(link) ) {
					// increase counter
					def cnode = urlMap[ link ] ;
					def created = cnode.getHistoryInformation().getCreatedAt().getTime();
					def now = (new java.util.Date()).getTime();
					def weeksOld = ( now - created ) / (1000 * 60 * 60 * 24 * 7);
					//println "L: " + link + " is " + weeksOld + " old.";
					if(weeksOld > 9) {
						weeksOld = 9;
					}
					def newIcon = freemind.modes.MindIcon.factory("full-" + (weeksOld as int));
					changeIcon(cnode, newIcon);
					urlMap.remove(link);
					currentNode = cnode;
					newNode = false;
				} else {
					// new node
					println "New: L: " + link + ", T: " + title;
					def nn = c.addNewNode(node, node.getChildCount(), false);
					c.setNodeText(nn,mkBody(title));
					c.setLink(nn, link);
					c.addIcon(nn,freemind.modes.MindIcon.factory("full-0"));
					currentNode = nn;
					newNode = true;
				}
			}
			// some additional data like size and price are added as subnodes to the node
			def mTitle = additionalTitlePattern.matcher(str);
			if(currentNode != null && mTitle.matches()) {
				additionalTitle = mTitle[0][1];
			}
			def mValue = additionalValuePattern.matcher(str);
			def data = "";
			if(currentNode != null && !additionalTitle.isEmpty() && mValue.matches()) {
				data = mkBody(additionalTitle + ": " + mValue[0][1]);
				changeNodeChildren(currentNode, data, newNode);
			}
			def mProp = additionalPropertyPattern.matcher(str);
			while(mProp.find()) {
				addData(currentNode, mProp, newNode);
			}
			
			if(newNode){
				def matcherData = (~/.*(Kaufpreis|Kaltmiete): ([0-9.,]+).*/).matcher(data);
				if(matcherData.matches()){
					price = matcherData[0][2].replace(".","").replace(",", ".");
					println "Price " + price;
				}
				def matcherData2 = (~/.*Wohnfl.*che: ([0-9.,]+).*/).matcher(data);
				if(matcherData2.matches()){
					qmeters = matcherData2[0][1].replace(".","").replace(",", ".");
					println "qm " + qmeters;
					if(qmeters as double != 0.0) {
						def qprice = (price as double) /(qmeters as double);
						if(qprice > 100.0){
							qprice = (qprice as int);
						} else {
							qprice = (qprice * 100.0 as int) / 100.0;
						}    
						def nn = c.addNewNode(currentNode, currentNode.getChildCount(), false);
						c.setNodeText(nn,"" + (((price as double) /(qmeters as double)) as int) + " EUR/qm");
					}				
				}
			}				
			
			def matcherNextPage = nextPagePattern.matcher(str);
			if(matcherNextPage.matches()) {
				found = IMMO + matcherNextPage[0][1];
				print "Next page: " + found;
			}
		}
		inp.close();
	} catch (Exception e) {
		println "error:"+e;
		found = "ERROR";
	}
	return found;
}


def changeNodeChildren(currentNode, text, newNode){
	if(newNode){
		def nn = c.addNewNode(currentNode, currentNode.getChildCount(), false);
		c.setNodeText(nn,text);
		c.setFolded(currentNode, true);
		return;
	}
	return;
	// the following must be fixed.
	def ptext = freemind.main.HtmlTools.htmlToPlain(text);
	def index = ptext.indexOf(":");
	if(index < 0){
		// no comparison possible.
		return;
	}
	def searchText = ptext.substring(0, index);
	//println "Searching for " + searchText;
	def nodeMap = [];
	nodeMap.addAll(currentNode.getChildren())
	while(nodeMap.size > 0) {
		def node = nodeMap.pop();
		def ntext = node.getPlainTextContent();
		def index2 = ntext.indexOf(":");
		if(index2<0) {
			continue;
		}
		//println "Comparing " + searchText + " with " + ntext;
		if(ntext.startsWith(searchText)){
			//println "Detailed comparing " + ptext + " with " + ntext;
			// this is the comparison:
			if(ntext.equals(ptext)) {
				// but equal. ok.
				return;
			} else {
				// TEXT HAS CHANGED!
				c.setNodeText(node, text);
				c.addIcon(node,freemind.modes.MindIcon.factory("messagebox_warning"));				
				c.addIcon(currentNode,freemind.modes.MindIcon.factory("messagebox_warning"));				
				c.setFolded(currentNode, false);
				return;
			}
		}
	}
	// not found, strange.
}

def addData(currentNode, matcherAdditionalData, newNode) {
	if(currentNode != null) {
		def data = matcherAdditionalData.group(1).replaceAll('<.*?>', '');
		def text = mkBody(data);
		changeNodeChildren(currentNode, text, newNode);
		return data;
	}
	return "";
}


def mkBody(str) {
	return "<html><body>"+str+"</body></html>";
}

def changeIcon(cnode, newIcon){
	// remove all icons up to the date icon:
	def list = []
	while(cnode.getIcons().size()>0) {
		def currIcon = cnode.getIcons().lastElement()
		c.removeLastIcon(cnode);
		if(currIcon.getName().contains("full-") || currIcon.getName().contains("button_cancel")){
			// last, if icon found
			break;
		}
		list.add(0,currIcon)
		//println "Removing " + currIcon
	}
	c.addIcon(cnode,newIcon);
	for(otherIcon in list){
		c.addIcon(cnode, otherIcon);
		//println "Readding " + otherIcon
	}
}
