from freemind.extensions import PermanentNodeHookAdapter
from java.awt import Color
import time

class PyCreatedNodeHook(PermanentNodeHookAdapter):
    def __init__(self, map, controller):
        PermanentNodeHookAdapter.__init__(self, map, controller)
        print "__init__"+repr(self)
        self.created = time.ctime()
        self.lastModified = time.ctime()

    def invoke(self, node):
        self.setNode(node)
        print "invoke"+repr(self)
        PermanentNodeHookAdapter.invoke(self)
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
        PermanentNodeHookAdapter.onUpdateNodeHook(self)
        print "onUpdateNodeHook?"+repr(self)
        if(self.isSelfUpdateExpected()):
            return
        print "onUpdateNodeHook"+repr(self) + " for " + repr(self.getNode())
        self.lastModified = time.ctime()
        self.setStyle()

    def onAddChild(self, child):
        hook = PyCreatedNodeHook(self.getMap(), self.getController())
        hook.setNode(child)
        child.addHook(hook)




instance=PyCreatedNodeHook(map,controller)
