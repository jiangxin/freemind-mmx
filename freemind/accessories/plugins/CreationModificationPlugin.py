from freemind.extensions import NodeHookAdapter
from java.awt import Color
import time

class PyCreatedNodeHook(NodeHookAdapter):
    def __init__(self, node, map, controller):
        NodeHookAdapter.__init__(self, node, map, controller)
        print "__init__"+repr(self)
        self.created = time.ctime()
        self.lastModified = time.ctime()

    def invoke(self):
        print "invoke"+repr(self)
        NodeHookAdapter.invoke(self)
        self.setStyle()
        # propagate
        childIterator = self.getNode().childrenUnfolded()
        while childIterator.hasNext():
            child = childIterator.next()
            child.addHook(PyCreatedNodeHook(child, self.getMap(), self.getController()))

    def setStyle(self):
        self.setToolTip("<html>Node created on: " + self.created + "<br>Node changed on: " + self.lastModified+"</html>")
        self.nodeChanged(self.getNode())
        
    def onUpdateNodeHook(self):
        NodeHookAdapter.onUpdateNodeHook(self)
        print "onUpdateNodeHook?"+repr(self)
        if(self.isSelfUpdateExpected()):
            return
        print "onUpdateNodeHook"+repr(self) + " for " + repr(self.getNode())
        self.lastModified = time.ctime()
        self.setStyle()

    def onAddChild(self, child):
        child.addHook(PyCreatedNodeHook(child, self.getMap(), self.getController()))




instance=PyCreatedNodeHook(node,map,controller)
