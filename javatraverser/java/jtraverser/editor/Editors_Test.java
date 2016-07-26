package jtraverser.editor;

import java.awt.Dimension;
import javax.swing.JFrame;
import org.junit.Test;
import jtraverser.dialogs.TreeDialog;
import mds.data.descriptor_r.Range;

@SuppressWarnings("static-method")
public class Editors_Test{
    @Test
    public void testSignalEditor() {
        final JFrame f = new JFrame();
        f.setPreferredSize(new Dimension(640, 480));
        f.add(new ArrayEditor(new Range(1, 10000, 0.01).getData(), new TreeDialog(new NodeEditor()), "test"));
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
