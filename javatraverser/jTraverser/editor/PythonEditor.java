package jTraverser.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_a.CStringArray;
import mds.data.descriptor_r.Function;
import mds.data.descriptor_s.CString;

@SuppressWarnings("serial")
public class PythonEditor extends JPanel implements Editor{
    static final short OPC_FUN  = 162;
    boolean            default_scroll;
    boolean            editable = true;
    String             program;
    String             retVar;
    int                rows     = 7, columns = 20;
    JTextArea          text_area;
    JTextField         text_field;

    public PythonEditor(final Descriptor[] descriptors){
        JScrollPane scroll_pane;
        if(this.rows > 1) this.default_scroll = true;
        if(descriptors != null){
            this.getProgram(descriptors);
        }else{
            this.program = "";
            this.retVar = "";
        }
        this.text_area = new JTextArea(this.rows, this.columns);
        this.text_area.setText(this.program);
        this.text_field = new JTextField(10);
        this.text_field.setText(this.retVar);
        final Dimension d = this.text_area.getPreferredSize();
        d.height += 20;
        d.width += 20;
        final JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        final JPanel jp1 = new JPanel();
        jp1.setLayout(new BorderLayout());
        jp1.setBorder(BorderFactory.createTitledBorder("Return Variable"));
        jp1.add(this.text_field);
        jp.add(jp1, BorderLayout.NORTH);
        final JPanel jp2 = new JPanel();
        jp2.setLayout(new BorderLayout());
        jp2.setBorder(BorderFactory.createTitledBorder("Program"));
        scroll_pane = new JScrollPane(this.text_area);
        scroll_pane.setPreferredSize(d);
        jp2.add(scroll_pane);
        jp.add(jp2, BorderLayout.CENTER);
        this.setLayout(new BorderLayout());
        this.add(jp, BorderLayout.CENTER);
    }

    @Override
    public final Descriptor getData() {
        final String programTxt = this.text_area.getText();
        if(programTxt == null || programTxt.equals("")) return null;
        final StringTokenizer st = new StringTokenizer(programTxt, "\n");
        final String[] lines = new String[st.countTokens()];
        int idx = 0;
        int maxLen = 0;
        while(st.hasMoreTokens()){
            lines[idx] = st.nextToken();
            if(maxLen < lines[idx].length()) maxLen = lines[idx].length();
            idx++;
        }
        for(int i = 0; i < lines.length; i++){
            final int len = lines[i].length();
            for(int j = 0; j < maxLen - len; j++)
                lines[i] += " ";
        }
        final CStringArray stArr = new CStringArray(lines);
        final String retVarTxt = this.text_field.getText();
        Descriptor retArgs[];
        if(retVarTxt == null || retVarTxt.equals("")) retArgs = new Descriptor[]{null, new CString("Py"), stArr};
        else retArgs = new Descriptor[]{null, new CString("Py"), stArr, new CString(retVarTxt)};
        return new Function(PythonEditor.OPC_FUN, retArgs);
    }

    private final void getProgram(final Descriptor[] dataArgs) {
        if(dataArgs.length <= 3 || dataArgs[3] == null) this.retVar = "";
        else this.retVar = dataArgs[3].toString();
        if(dataArgs.length <= 2 || dataArgs[2] == null) this.program = "";
        else if(dataArgs[2] instanceof CStringArray) this.program = String.join("\n", ((CStringArray)dataArgs[2]).getValue());
        else this.program = dataArgs[2].toString();
    }

    @Override
    public final boolean isNull() {
        return false;
    }

    @Override
    public final void reset() {
        this.text_area.setText(this.program);
        this.text_field = new JTextField(this.retVar);
    }

    @Override
    public final void setEditable(final boolean editable) {
        this.editable = editable;
        if(this.text_area != null) this.text_area.setEditable(editable);
        if(this.text_field != null) this.text_field.setEditable(editable);
    }
}
