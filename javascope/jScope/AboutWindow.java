package jScope;

/* $Id$ */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class AboutWindow extends JLabel{
    static String     javaVersion      = System.getProperty("java.version");
    static final long serialVersionUID = 47643264578461L;
    ImageIcon         io               = null;

    public AboutWindow(){
        try{
            final String icon_file = jScopeFacade.findFileInClassPath("about_jscope.jpg");
            if(icon_file != null) this.io = new ImageIcon(icon_file);
            else this.io = new ImageIcon(this.getClass().getClassLoader().getResource("jScope/about_jscope.jpg"));
            if(jScopeFacade.is_debug) System.out.println("about_jscope image path " + icon_file + this.io);
            this.setIcon(this.io);
        }catch(final NullPointerException e){}
    }

    @Override
    public void paint(final Graphics gReal) {
        // Double buffering
        final Image imageBuffer = this.createImage(this.getWidth(), this.getHeight());
        final Graphics g = imageBuffer.getGraphics();
        if(this.io == null) return;
        final Image image = this.io.getImage();
        g.drawImage(image, 1, 1, null);
        Toolkit.getDefaultToolkit().sync();
        final int start = 32 + 2;
        // int top = 102 + 1;
        final int botton = 268 + 1;
        final int delta = 14;
        g.setColor(new Color(128, 128, 128));
        g.drawRect(-1, -1, this.getWidth(), this.getHeight()); // makes a bevel border
        // likeness
        g.drawString(jScopeFacade.VERSION, start, botton - 3 * delta);
        g.drawString("http://www.igi.cnr.it", start, botton - 2 * delta);
        g.drawString("JVM used :" + AboutWindow.javaVersion, start, botton - delta);
        gReal.drawImage(imageBuffer, 0, 0, this);
    }

    @Override
    public void update(final Graphics g) {
        this.paint(g);
    }
}
