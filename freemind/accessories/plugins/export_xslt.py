from freemind.extensions import ModeControllerHookAdapter
from accessories.plugins.util.xslt import ExportDialog

class PyExportXsltModeControllerHook(ModeControllerHookAdapter):
    def __init__(self):
        ModeControllerHookAdapter.__init__(self)

    def startupMapHook(self):
        self.mc = self.getController()
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

    
instance=PyExportXsltModeControllerHook()
