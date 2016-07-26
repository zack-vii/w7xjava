package jtraverser.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jtraverser.dialogs.TreeDialog;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Signal;
import mds.data.descriptor_s.Missing;
import mds.data.descriptor_s.TREENODE;

@SuppressWarnings("serial")
public class SignalEditor extends JPanel implements ActionListener, Editor{
    private final TreeDialog  dialog;
    private final JComboBox   combo;
    private int               mode_idx      = 0;
    private int               curr_mode_idx = 0;
    private Descriptor        dat, raw, dim, signal;
    private LabeledExprEditor expr_edit;
    private JPanel            signal_panel;
    private ArrayEditor       dat_edit, raw_edit, dim_edit;
    private TREENODE          node;
    private int               segment, numsegments;

    public SignalEditor(final TreeDialog dialog){
        this.dialog = dialog;
        this.setLayout(new BorderLayout());
        final JPanel jp = new JPanel();
        jp.add(new JLabel("Signal: "));
        this.combo = new JComboBox<String>(new String[]{"Undefined", "Signal", "Expression"});
        this.combo.setEditable(false);
        this.combo.setSelectedIndex(this.mode_idx);
        this.combo.addActionListener(this);
        jp.add(this.combo);
        this.add(jp, BorderLayout.NORTH);
        this.addEditor();
    }

    public SignalEditor(final TREENODE node, final TreeDialog dialog){
        this(dialog);
        this.setNode(node);
    }

    @Override
    public final void actionPerformed(final ActionEvent e) {
        final int idx = this.combo.getSelectedIndex();
        if(idx != this.curr_mode_idx) this.setMode(idx);
    }

    private final void addEditor() {
        switch(this.curr_mode_idx){
            case 0:
                return;
            case 1:
                this.signal_panel = new JPanel(new BorderLayout());
                final JPanel contentpanel = new JPanel(new GridLayout(1, 3));
                this.signal_panel.add(contentpanel, BorderLayout.CENTER);
                if(this.signal instanceof Signal){
                    contentpanel.add(this.dat_edit = new ArrayEditor(this.dat, this.dialog, "Data"));
                    contentpanel.add(this.raw_edit = new ArrayEditor(this.raw, this.dialog, "Raw"));
                    contentpanel.add(this.dim_edit = new ArrayEditor(this.dim, this.dialog, "Dimension"));
                }else{
                    contentpanel.add(this.dat_edit = new ArrayEditor(this.dialog, "Data"));
                    contentpanel.add(this.raw_edit = new ArrayEditor(this.dialog, "Raw"));
                    contentpanel.add(this.dim_edit = new ArrayEditor(this.dialog, "Dimension"));
                }
                if(this.numsegments > 0){
                    final JPanel segpane = new JPanel(new BorderLayout());
                    final JSlider segments = new JSlider(0, this.numsegments - 1, this.segment);
                    segpane.add(segments, BorderLayout.CENTER);
                    final JTextField text = new JTextField(9);
                    text.setHorizontalAlignment(JTextField.CENTER);
                    text.setText(Integer.toString(SignalEditor.this.segment));
                    text.setEditable(false);
                    segpane.add(text, BorderLayout.WEST);
                    final JLabel label = new JLabel(String.format("Segments: %d", this.numsegments), JLabel.CENTER);
                    label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                    segpane.add(label, BorderLayout.EAST);
                    segments.addMouseListener(new MouseAdapter(){
                        @Override
                        public void mouseReleased(final MouseEvent ce) {
                            final int newsegment = ((JSlider)ce.getSource()).getValue();
                            if(SignalEditor.this.segment == newsegment) return;
                            try{
                                SignalEditor.this.setSignal(SignalEditor.this.node.getSegment(SignalEditor.this.segment = newsegment));
                                SignalEditor.this.dat_edit.setData(SignalEditor.this.dat);
                                SignalEditor.this.raw_edit.setData(SignalEditor.this.raw);
                                SignalEditor.this.dim_edit.setData(SignalEditor.this.dim);
                            }catch(final MdsException e){
                                e.printStackTrace();
                            }
                        }
                    });
                    segments.addChangeListener(new ChangeListener(){
                        @Override
                        public void stateChanged(final ChangeEvent ce) {
                            text.setText(Integer.toString(((JSlider)ce.getSource()).getValue()));
                        }
                    });
                    this.signal_panel.add(segpane, BorderLayout.NORTH);
                }
                this.add(this.signal_panel, BorderLayout.CENTER);
                break;
            case 2:
                this.expr_edit = new LabeledExprEditor(this.signal);
                this.add(this.expr_edit, BorderLayout.CENTER);
                break;
        }
    }

    @Override
    public final Descriptor getData() throws MdsException {
        switch(this.curr_mode_idx){
            case 0:
                return null;
            case 1:
                Descriptor data = this.dat_edit.getData();
                if(data == null) data = this.dat;
                Descriptor raw = this.raw_edit.getData();
                if(raw == null) raw = this.raw;
                Descriptor dim = this.dim_edit.getData();
                if(dim == null) dim = this.dim;
                return new Signal(data, raw, dim);
            case 2:
                return this.expr_edit.getData();
        }
        return null;
    }

    @Override
    public final boolean isNull() {
        return this.curr_mode_idx == 0;
    }

    @Override
    public final void reset() {
        this.combo.setSelectedIndex(this.mode_idx);
        this.setMode(this.mode_idx);
    }

    @Override
    public final void setEditable(final boolean editable) {
        this.combo.setEnabled(editable);
        if(this.dat_edit != null) this.dat_edit.setEditable(editable);
        if(this.raw_edit != null) this.raw_edit.setEditable(editable);
        if(this.dim_edit != null) this.dim_edit.setEditable(editable);
        if(this.expr_edit != null) this.expr_edit.setEditable(editable);
    }

    private final void setMode(final int idx) {
        switch(this.curr_mode_idx){
            case 1:
                this.remove(this.signal_panel);
                this.dat_edit.interrupt();
                this.dim_edit.interrupt();
                this.raw_edit.interrupt();
                this.signal_panel = this.dat_edit = this.dim_edit = this.raw_edit = null;
                break;
            case 2:
                this.remove(this.expr_edit);
                this.expr_edit = null;
                break;
        }
        this.curr_mode_idx = idx;
        this.addEditor();
        this.validate();
        this.dialog.repack();
        this.repaint();
    }

    public final void setNode(final TREENODE node) {
        this.node = node;
        try{
            this.numsegments = this.node.getNumSegments();
            if(this.numsegments == 0) this.setSignal(this.node.getRecord());
            else this.setSignal(this.node.getSegment(this.segment = 0));
        }catch(final MdsException e){
            this.setSignal(Missing.NEW);
        }
        this.reset();
    }

    public final void setSignal(final Descriptor signal) {
        this.dat = this.raw = this.dim = Missing.NEW;
        if(signal == null) this.mode_idx = 0;
        else if(signal instanceof Signal){
            this.mode_idx = 1;
            this.dat = ((Signal)signal).getData();
            this.raw = ((Signal)signal).getRaw();
            this.dim = ((Signal)signal).getDimension();
        }else this.mode_idx = 2;
        this.signal = signal == null ? new Signal(Missing.NEW, Missing.NEW, Missing.NEW) : signal;
    }
}