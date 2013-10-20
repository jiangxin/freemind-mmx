/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2012 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
	if(removedNode.getIcons().size()>0)
		c.removeLastIcon(removedNode);
	c.addIcon(removedNode,freemind.modes.MindIcon.factory("button_cancel"));
	println "Removed from page T: " + removedNode;
}

return null;

def getUrl(HashMap urlMap, String urlT, String IMMO) {
	def found = null;
	try {
		URL url = new URL(urlT);
		def inp = new BufferedReader(new InputStreamReader(url.openStream(), "ISO-8859-1"));
		String str;
		def pattern = ~/.*<a href="(\/expo[^?";]*);([^?"]*)".*/;
		def noExposeLinkPattern = ~/.*title.*/;
		def nextPagePattern = ~/.*<a href="(\/[^"]*)" class="is24-next".*/;
		def additionalDataPattern = ~/.*(<dt .*)$/;
		def additionalDataPattern2 = ~/^<li>(.*)<\/li>$/;
		def additionalDataPattern3 = ~/.*<span class="address is24-hide">(.*)<\/span>.*/;
		def currentNode = null;
		def price = 0;
		def qmeters = 1;
		while ((str = inp.readLine()) != null) {
			// str is one line of text; readLine() strips the newline character(s)
			def matcher = pattern.matcher(str);
			if(matcher.matches() && ! noExposeLinkPattern.matcher(str).matches()) {
				currentNode = null;
				price = 0;
				qmeters = 1;
				def link = IMMO + matcher[0][1];
				def title = inp.readLine().trim();
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
					// remove all icons up to the date icon:
					def list = []
					while(cnode.getIcons().size()>0) {
						def currIcon = cnode.getIcons().lastElement()
						c.removeLastIcon(cnode);
						if(currIcon.getName().contains("full-")){
							// last, if icon found
							break;
						}
						list.add(0,currIcon)
						println "Removing " + currIcon
					}
					c.addIcon(cnode,freemind.modes.MindIcon.factory("full-" + (weeksOld as int)));
					for(otherIcon in list){
						c.addIcon(cnode, otherIcon);
						println "Readding " + otherIcon
					}
					urlMap.remove(link);
				} else {
					// new node
					println "New: L: " + link + ", T: " + title;
					def nn = c.addNewNode(node, node.getChildCount(), false);
					c.setNodeText(nn,title);
					c.setLink(nn, link);
					c.addIcon(nn,freemind.modes.MindIcon.factory("full-0"));
					currentNode = nn;
				}
			}
			// some additional data like size and price are added as subnodes to the node
			def data = addData(currentNode, additionalDataPattern.matcher(str));
			addData(currentNode, additionalDataPattern2.matcher(str));
			addData(currentNode, additionalDataPattern3.matcher(str));
			
			def matcherData = (~/.*Kaufpreis: ([0-9.]+) EUR.*/).matcher(data);
			if(matcherData.matches()){
				price = matcherData[0][1];
				println "Price " + price;
			}
			def matcherData2 = (~/.*Wohnfl.che: ([0-9.]+) m.*/).matcher(data);
			if(matcherData2.matches()){
				qmeters = matcherData2[0][1];
				println "qm " + qmeters;
				if(qmeters as double != 0.0) {
					def nn = c.addNewNode(currentNode, currentNode.getChildCount(), false);
					c.setNodeText(nn,"" + (((price as double) /(qmeters as double)) as int) + " EUR/qm");
				}				
			}
				
			
			def matcherNextPage = nextPagePattern.matcher(str);
			if(matcherNextPage.matches()) {
				found = IMMO + matcherNextPage[0][1];
			}
		}
		inp.close();
	} catch (Exception e) {
		println "error:"+e;
		found = "ERROR";
	}
	return found;
}


def addData(currentNode, matcherAdditionalData) {
	if(currentNode != null && matcherAdditionalData.matches()) {
		def data = matcherAdditionalData[0][1].replaceAll('<.*?>', '');
		def nn = c.addNewNode(currentNode, currentNode.getChildCount(), false);
		c.setNodeText(nn,"<html><body>"+data+"</body></html>");
		c.setFolded(currentNode, true);
		return data;
	}
	return "";
}