from freemind.extensions import ModeControllerHookAdapter
from javax.swing import AbstractAction
from accessories.plugins.util.xslt import ExportDialog

class PyExportXsltModeControllerHook(ModeControllerHookAdapter):
    def __init__(self, controller):
        ModeControllerHookAdapter.__init__(self, controller)
        self.action=None

    def fileMenuHook(self, fmenu):
        if(self.action == None):
            self.action = ExportAction(self.getController())
        fmenu.add(self.action)

    def enableActions(self, enabled):
        print "enableActions"+repr(enabled)
        if(self.action != None):
            self.action.setEnabled(enabled)

        
        

def methodsOf(aClass):
    classMembers = vars(aClass).values()
    methods = [eachMember for eachMember in classMembers 
            if callable(eachMember)]
    for eachBase in aClass.__bases__:
        methods.extend(methodsOf(eachBase))
    return methods
    
class ExportAction(AbstractAction):
    def __init__(self, mapController):
        AbstractAction.__init__(self, "Export to XSLT")
        self.mc = mapController

    def actionPerformed(self, e):
        model = self.mc.getController().getModel()
        if(model == None):
            return # there may be no map open
        if((model.getFile() == None) or model.isReadOnly()):
            if(self.mc.save()):
                self.export(model.getFile())
                return
            else:
                return
        else:
            self.export(model.getFile())

    def export(self, file):
        exp = ExportDialog(file)
        exp.setVisible(1)

    
instance=PyExportXsltModeControllerHook(controller)
#print repr(methodsOf(PyExportXsltModeControllerHook)).replace(",", "\n")
#print repr(methodsOf(ModeControllerHookAdapter)).replace(",", "\n")
