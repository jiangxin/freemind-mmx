from freemind.extensions import PermanentNodeHookAdapter
from java.awt import Color

class PyAutomaticLayoutNodeHook(PermanentNodeHookAdapter):
    def __init__(self, map, controller):
        PermanentNodeHookAdapter.__init__(self, map, controller)
        self.colors = [ Color(0x000000), Color(0x0033FF), Color(0x00b439), Color(0x990000), Color(0x111111) ]

    def invoke(self, node):
        self.setNode(node)
        PermanentNodeHookAdapter.invoke(self)
        self.setStyle()
        # propagate
        childIterator = self.getNode().childrenUnfolded()
        while childIterator.hasNext():
            child = childIterator.next()
            child.addHook(PyAutomaticLayoutNodeHook(child, self.getMap(), self.getController()))

    def setStyle(self):
        self.setColor()
        
    def onUpdateNodeHook(self):
        PermanentNodeHookAdapter.onUpdateNodeHook(self)
        print "onUpdateNodeHook?"+repr(self)
        if(self.isSelfUpdateExpected()):
            return
        print "onUpdateNodeHook"+repr(self) + " for " + repr(self.getNode())
        self.setStyle()

    def onAddChild(self, child):
        hook = PyAutomaticLayoutNodeHook(self.getMap(), self.getController())
        hook.setNode(child)
        child.addHook(hook)

    def setColor(self):
        depth = self.depth(self.getNode())
        print "COLOR, depth="+repr(depth)
        if(depth==0):
            print "ROOT NODE"
        mycolor = self.colors[-1] # last color
        if(depth < len(self.colors)):
            mycolor = self.colors[depth]
        self.getNode().setColor(mycolor)
        self.nodeChanged(self.getNode())

    def depth(self, node):
        if(node.isRoot()):
            return 0
        return self.depth(node.getParent()) + 1




instance=PyAutomaticLayoutNodeHook(map,controller)
