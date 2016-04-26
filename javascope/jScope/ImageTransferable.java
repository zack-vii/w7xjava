package jScope;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

class ImageTransferable implements Transferable, ClipboardOwner{
    BufferedImage ri;

    ImageTransferable(final BufferedImage img){
        this.ri = img;
    }

    @Override
    public Object getTransferData(final DataFlavor flavor) throws IOException, UnsupportedFlavorException {
        if(this.ri == null) return null;
        if(!this.isDataFlavorSupported(flavor)){ throw new UnsupportedFlavorException(flavor); }
        return this.ri;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.imageFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return DataFlavor.imageFlavor.equals(flavor);
    }

    @Override
    public void lostOwnership(final Clipboard clipboard, final Transferable contents) {
        /*
        System.out.println ("ImageTransferable lost ownership of "  +clipboard.getName());
        System.out.println ("data: " + contents);
         */
    }
}