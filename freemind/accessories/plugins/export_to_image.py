from freemind.extensions import ModeControllerHookAdapter
from javax.swing import AbstractAction

class PyExportToImageModeControllerHook(ModeControllerHookAdapter):
    def __init__(self, controller):
        ModeControllerHookAdapter.__init__(self, controller)
        self.actionJPEG=None
        self.actionPNG =None

    def fileMenuHook(self, fmenu):
        if(self.actionJPEG == None):
            self.actionJPEG = ExportImageAction(self.getController(), "Export to jpeg", "jpeg", self)
        if(self.actionPNG  == None):
            self.actionPNG  = ExportImageAction(self.getController(), "Export to png" , "png" , self)
        fmenu.add(self.actionJPEG)
        fmenu.add(self.actionPNG )

    def enableActions(self, enabled):
        print "enableActions"+repr(enabled)
        if(self.actionJPEG != None):
            self.actionJPEG.setEnabled(enabled)
        if(self.actionPNG  != None):
            self.actionPNG.setEnabled(enabled)

        
from java.awt.image import BufferedImage
from javax.swing import JFileChooser
from java.io import File
from java.io import FileOutputStream
from javax.swing.filechooser import FileFilter
import javax.imageio.ImageIO

class ExportImageAction(AbstractAction):
    def __init__(self, mapController, label, type, hook):
        AbstractAction.__init__(self, label)
        self.mc = mapController
        self.type = type
        self.model = self.mc.getController().getModel()
        self.hook = hook

    def actionPerformed(self, e):
        self.model = self.mc.getController().getModel()
        if(self.model == None):
            return # there may be no map open
        image = self.createBufferedImage()
        if(image != None):
            self.exportToImage(image, self.type)

    def createBufferedImage(self):
        view = self.mc.getView()
        if(view == None):
            return None
        rect = view.getInnerBounds(self.model.getRoot().getViewer())
        image = BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB)
        g = image.createGraphics()
        g.translate(-rect.getMinX(), -rect.getMinY())
        view.update(g)
        return image

    def exportToImage(self, image, type):
        chooser = JFileChooser()
        imageName = self.model.getFile().toString() + "." + type
        chooser.setSelectedFile(File(imageName))
        chooser.addChoosableFileFilter(ImageFilter(type))
        # set title
        returnVal = chooser.showSaveDialog(self.mc.getView())
        if (returnVal != JFileChooser.APPROVE_OPTION):
            return
        f = chooser.getSelectedFile()
        # exists, ...
        out = FileOutputStream(f)
        javax.imageio.ImageIO.write(image, type, out)


class ImageFilter(FileFilter):
    def __init__(self, type):
        self.type = type

    def accept(self,f):
        if(f.isDirectory()):
            return 1
        file = f.toString()
        if(file.endswith("."+self.type)):
            return 1
        else:
            return 0
        
    def getDescription(self):
        return self.type



#def methodsOf(aClass):
#    classMembers = vars(aClass).values()
#    methods = [eachMember for eachMember in classMembers 
#            if callable(eachMember)]
#    for eachBase in aClass.__bases__:
#        methods.extend(methodsOf(eachBase))
#    return methods
    
instance=PyExportToImageModeControllerHook(controller)
#print repr(methodsOf(PyExportToImageModeControllerHook)).replace(",", "\n")
#print repr(methodsOf(ModeControllerHookAdapter)).replace(",", "\n")
