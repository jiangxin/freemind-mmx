from freemind.extensions import ModeControllerHookAdapter
from java.awt.image import BufferedImage
from javax.swing import JFileChooser
from java.io import File
from java.io import FileOutputStream
from javax.swing.filechooser import FileFilter
import javax.imageio.ImageIO

class PyExportToImageModeControllerHook(ModeControllerHookAdapter):
    def __init__(self):
        ModeControllerHookAdapter.__init__(self)
       
    def startupMapHook(self):
        self.mc = self.getController();
        self.model = self.mc.getController().getModel()
        if(self.model == None):
            return # there may be no map open
        image = self.createBufferedImage()
        if(image != None):
            self.exportToImage(image, "jpeg")

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
    
instance=PyExportToImageModeControllerHook()
#print repr(methodsOf(PyExportToImageModeControllerHook)).replace(",", "\n")
#print repr(methodsOf(ModeControllerHookAdapter)).replace(",", "\n")
