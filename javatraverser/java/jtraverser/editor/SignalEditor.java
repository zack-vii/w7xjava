package jtraverser.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jtraverser.dialogs.TreeDialog;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_r.Signal;
import mds.data.descriptor_s.Missing;

@SuppressWarnings("serial")
public class SignalEditor extends JPanel implements ActionListener, Editor{
    private final TreeDialog  dialog;
    private final JComboBox   combo;
    private int               mode_idx = 0, curr_mode_idx = 0;
    private Descriptor        data, raw, dim, signal;
    private LabeledExprEditor expr_edit;
    private JPanel            signal_panel;
    private ArrayEditor       data_edit, raw_edit, dim_edit;

    public SignalEditor(final Descriptor data, final TreeDialog dialog){
        this.data = data == null ? Missing.NEW : data;
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
        this.setData(data);
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
                this.signal_panel = new JPanel();
                this.signal_panel.setLayout(new GridLayout(1, 3));
                if(this.signal instanceof Signal){
                    this.signal_panel.add(this.data_edit = new ArrayEditor(this.data = ((Signal)this.signal).getData(), this.dialog, "Data"));
                    this.signal_panel.add(this.raw_edit = new ArrayEditor(this.raw = ((Signal)this.signal).getRaw(), this.dialog, "Raw"));
                    this.signal_panel.add(this.dim_edit = new ArrayEditor(this.dim = ((Signal)this.signal).getDimension(), this.dialog, "Dimension"));
                }else{
                    this.signal_panel.add(this.data_edit = new ArrayEditor(this.dialog, "Data"));
                    this.signal_panel.add(this.raw_edit = new ArrayEditor(this.dialog, "Raw"));
                    this.signal_panel.add(this.dim_edit = new ArrayEditor(this.dialog, "Dimension"));
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
                Descriptor data = this.data_edit.getData();
                if(data == null) data = this.data;
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

    public final void setData(final Descriptor signal) {
        if(signal == null) this.mode_idx = 0;
        else if(signal instanceof Signal) this.mode_idx = 1;
        else this.mode_idx = 2;
        this.signal = signal == null ? new Signal(Missing.NEW, Missing.NEW, Missing.NEW) : signal;
        this.reset();
    }

    @Override
    public final void setEditable(final boolean editable) {
        this.combo.setEnabled(editable);
        if(this.data_edit != null) this.data_edit.setEditable(editable);
        if(this.raw_edit != null) this.raw_edit.setEditable(editable);
        if(this.dim_edit != null) this.dim_edit.setEditable(editable);
        if(this.expr_edit != null) this.expr_edit.setEditable(editable);
    }

    private final void setMode(final int idx) {
        switch(this.curr_mode_idx){
            case 1:
                this.remove(this.signal_panel);
                this.data_edit.interrupt();
                this.dim_edit.interrupt();
                this.raw_edit.interrupt();
                this.signal_panel = this.data_edit = this.dim_edit = this.raw_edit = null;
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
}