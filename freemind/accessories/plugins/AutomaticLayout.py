from freemind.extensions import NodeHookAdapter
from java.awt import Color

class PyAutomaticLayoutNodeHook(NodeHookAdapter):
    def __init__(self, node, map, controller):
        NodeHookAdapter.__init__(self, node, map, controller)
        self.colors = [ Color(0x000000), Color(0x0033FF), Color(0x00b439), Color(0x990000), Color(0x111111) ]

    def invoke(self):
        NodeHookAdapter.invoke(self)
        self.setStyle()
        # propagate
        childIterator = self.getNode().childrenUnfolded()
        while childIterator.hasNext():
            child = childIterator.next()
            child.addHook(PyAutomaticLayoutNodeHook(child, self.getMap(), self.getController()))

    def setStyle(self):
        self.setColor()
        
    def onUpdateNodeHook(self):
        NodeHookAdapter.onUpdateNodeHook(self)
        print "onUpdateNodeHook?"+repr(self)
        if(self.isSelfUpdateExpected()):
            return
        print "onUpdateNodeHook"+repr(self) + " for " + repr(self.getNode())
        self.setStyle()

    def onAddChild(self, child):
        child.addHook(PyAutomaticLayoutNodeHook(child, self.getMap(), self.getController()))

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




instance=PyAutomaticLayoutNodeHook(node,map,controller)
