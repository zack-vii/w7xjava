package jtraverser.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import jtraverser.Node;
import jtraverser.TreeManager;
import jtraverser.jTraverserFacade;
import mds.data.descriptor_s.TREENODE.Flags;

@SuppressWarnings("serial")
public final class ModifyFlags extends JDialog{
    private final static boolean[] intToBool(final int iflags) {
        final boolean[] bflags = new boolean[32];
        for(byte i = 0; i < 32; i++)
            bflags[i] = (iflags & (1 << i)) != 0;
        return bflags;
    }
    private final JButton     close_b;
    private final JCheckBox[] flag;;
    private final boolean[]   settable_flag = new boolean[]{true, false, true, true, false, false, true, true, false, true, true, true, true, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false};
    private final TreeManager treeman;
    private final JButton     update_b;

    public ModifyFlags(final TreeManager treeman){
        this(treeman, treeman.getFrame());
    }

    public ModifyFlags(final TreeManager treeman, final Frame frame){
        super(frame);
        this.treeman = treeman;
        this.setFocusableWindowState(false);
        final JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        final JPanel jp1 = new JPanel();
        jp1.setLayout(new GridLayout(8, 4));
        this.flag = new JCheckBox[32];
        jp1.add(this.flag[13] = new JCheckBox("PathReference"));
        jp1.add(this.flag[14] = new JCheckBox("NidReference"));
        jp1.add(this.flag[5] = new JCheckBox("Segmented"));
        jp1.add(this.flag[8] = new JCheckBox("Compressible"));
        jp1.add(this.flag[1] = new JCheckBox("ParentOff"));
        jp1.add(this.flag[4] = new JCheckBox("Versions"));
        jp1.add(this.flag[16] = new JCheckBox("CompressSegments"));
        jp1.add(this.flag[9] = new JCheckBox("DoNotCompress"));
        jp1.add(this.flag[0] = new JCheckBox("Off"));
        jp1.add(this.flag[6] = new JCheckBox("Setup"));
        jp1.add(this.flag[2] = new JCheckBox("Essential"));
        jp1.add(this.flag[10] = new JCheckBox("CompressOnPut"));
        jp1.add(this.flag[11] = new JCheckBox("NoWriteModel"));
        jp1.add(this.flag[12] = new JCheckBox("NoWriteShot"));
        jp1.add(this.flag[7] = new JCheckBox("WriteOnce"));
        jp1.add(this.flag[15] = new JCheckBox("IncludeInPulse"));
        jp1.add(this.flag[3] = new JCheckBox("Cached"));
        for(byte i = 17; i < 31; i++)
            jp1.add(this.flag[i] = new JCheckBox("UndefinedFlag" + (i)));
        jp1.add(this.flag[31] = new JCheckBox("Error"));
        this.flag[0].addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Node currnode = ModifyFlags.this.treeman.getCurrentNode();
                if(currnode == null) return;
                if(ModifyFlags.this.flag[0].isSelected()) currnode.turnOff();
                else currnode.turnOn();
                ModifyFlags.this.treeman.reportChange();
            }
        });
        for(byte i = 1; i < 32; i++)
            if(this.flag[i] != null){
                final byte ii = i;
                this.flag[i].addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        ModifyFlags.this.editFlag(ii);
                        ModifyFlags.this.treeman.reportChange();
                    }
                });
            }
        jp.add(jp1);
        final JPanel jp3 = new JPanel();
        jp3.setLayout(new GridLayout(1, 2));
        jp3.add(this.close_b = new JButton("Close"));
        this.close_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ModifyFlags.this.close();
            }
        });
        jp3.add(this.update_b = new JButton("Refresh"));
        this.update_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ModifyFlags.this.update();
            }
        });
        jp.add(jp3, BorderLayout.SOUTH);
        this.getContentPane().add(jp);
        this.addKeyListener(new KeyAdapter(){
            @Override
            public void keyTyped(final KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE) ModifyFlags.this.setVisible(false);
            }
        });
        this.pack();
        this.setResizable(false);
    }

    public final void close() {
        this.setVisible(false);
    }

    private final void editFlag(final byte idx) {
        final Node currnode = ModifyFlags.this.treeman.getCurrentTree().getCurrentNode();
        if(currnode == null) return;
        if(this.flag[idx].isSelected()) try{
            currnode.setFlag(idx);
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, exc.getMessage(), "Error setting flag" + idx, JOptionPane.WARNING_MESSAGE);
        }
        else try{
            currnode.clearFlag(idx);
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, exc.getMessage(), "Error clearing flag " + idx, JOptionPane.WARNING_MESSAGE);
        }
        this.update();
    }

    public final void open() {
        if(this.treeman != null) this.setLocation(this.treeman.dialogLocation());
        this.setVisible(true);
        this.update();
    }

    private Flags readFlags() throws Exception {
        final Node currnode = ModifyFlags.this.treeman.getCurrentTree().getCurrentNode();
        if(currnode == null) return new Flags();
        final Flags flags = currnode.getFlags();
        if(flags.isError()) jTraverserFacade.stderr("Error getting Flags", null);
        return flags;
    }

    public final void update() {
        if(!this.isVisible()) return;
        int iflags;
        try{
            iflags = this.readFlags().flags;
        }catch(final Exception exc){
            jTraverserFacade.stderr("Error getting flags", exc);
            this.close();
            return;
        }
        final boolean[] bflags = ModifyFlags.intToBool(iflags);
        final Node currnode = ModifyFlags.this.treeman.getCurrentTree().getCurrentNode();
        final boolean is_ok = !(ModifyFlags.this.treeman.getCurrentTree().isReadOnly() || (currnode == null));
        for(int i = 0; i < 32; i++){
            this.flag[i].setSelected(bflags[i]);
            this.flag[i].setEnabled(is_ok && this.settable_flag[i]);
        }
        if(currnode == null) this.setTitle("Flags of <none selected>");
        else this.setTitle(new StringBuilder(128).append("Flags of ").append(currnode.getFullPath())//
                .append(" (0x").append(Integer.toHexString(iflags)).append(')').toString());
    }
}
