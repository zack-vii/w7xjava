package jtraverser.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import mds.MdsException;
import mds.data.descriptor.Descriptor;

@SuppressWarnings("serial")
public class ArgEditor extends JScrollPane{
    private final ExprEditor[] args;
    private final int          num_args;
    protected final Dimension  preferred;

    public ArgEditor(){
        this(null, 9, new Dimension(220, 89));
    }

    public ArgEditor(final Descriptor[] descriptors){
        this(descriptors, 9, new Dimension(220, 89));
    }

    public ArgEditor(Descriptor[] data, final int num_args, final Dimension preferred){
        if(data == null) data = new Descriptor[num_args];
        this.preferred = preferred;
        this.num_args = num_args;
        final JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(num_args, 1));
        this.args = new ExprEditor[num_args];
        for(int i = 0; i < num_args; i++){
            if(i < data.length) this.args[i] = new ExprEditor(data[i], false);
            else this.args[i] = new ExprEditor(null, false);
            jp.add(new LabeledExprEditor("Argument " + (i + 1), this.args[i]));
        }
        final JPanel jp2 = new JPanel();
        jp2.setLayout(new BorderLayout());
        jp2.add(jp, BorderLayout.NORTH);
        this.setViewportView(jp2);
        this.setPreferredSize(preferred);
        this.getVerticalScrollBar().setUnitIncrement(43);
    }

    public final Descriptor[] getData() throws MdsException {
        final Descriptor data[] = new Descriptor[this.num_args];
        for(int i = 0; i < this.num_args; i++)
            data[i] = this.args[i].getData();
        return data;
    }

    public final void reset() {
        for(int i = 0; i < this.num_args; i++)
            this.args[i].reset();
    }

    public final void setData(final Descriptor[] data) {
        int min_len = 0, i = 0;
        if(data != null){
            if(data.length < this.num_args) min_len = data.length;
            else min_len = this.num_args;
            for(; i < min_len; i++)
                this.args[i].setData(data[i]);
        }
        for(; i < this.num_args; i++)
            this.args[i].setData(null);
    }

    public final void setEditable(final boolean editable) {
        for(int i = 0; i < this.num_args; i++)
            this.args[i].setEditable(editable);
    }
}