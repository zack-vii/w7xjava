package devicebeans;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import jTraverser.Database;
import mds.MdsException;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.CString;

public class DeviceLabel extends DeviceComponent{
    private static final long serialVersionUID = 2377066253876685403L;
    Descriptor                data;
    // public boolean showState = false;
    public boolean            displayEvaluated = false;
    @SuppressWarnings("unused")
    private boolean           initial_state;
    // protected boolean isGridBag = false;
    protected String          initialField     = "<empty>";
    protected boolean         initializing     = false;
    JPanel                    jp;
    // protected JCheckBox checkB;
    protected JLabel          label;
    // protected JTextField textF;
    public String             labelString      = "<empty>";
    public int                numCols          = 10;
    // GridBagLayout gridbag;
    protected int             preferredWidth   = -1;
    private final boolean     reportingChange  = false;
    public boolean            textOnly         = false;

    public DeviceLabel(){
        this.initializing = true;
        // jp = new JPanel();
        // jp.add(checkB = new JCheckBox());
        // checkB.setVisible(false);
        this.add(this.label = new JLabel("<empty>"));
        // add(jp);
        // add(textF = new JTextField(10));
        // textF.setEnabled(editable);
        // textF.setEditable(editable);
        // setLayout(gridbag = new GridBagLayout());
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
        // this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        this.initializing = false;
    }

    @Override
    public Component add(final Component c) {
        if(!this.initializing){
            JOptionPane.showMessageDialog(null, "You cannot add a component to a Device Label. Please remove the component.", "Error adding Device field", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return super.add(c);
    }

    @Override
    public Component add(final Component c, final int intex) {
        if(!this.initializing){
            JOptionPane.showMessageDialog(null, "You cannot add a component to a Device Label. Please remove the component.", "Error adding Device field", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return super.add(c);
    }

    @Override
    public Component add(final String name, final Component c) {
        if(!this.initializing){
            JOptionPane.showMessageDialog(null, "You cannot add a component to a Device Label. Please remove the component.", "Error adding Device field", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return super.add(c);
    }

    @Override
    public void apply() {
        return;
    }
    /*
      void postApply()
      {
    if (editable || !displayEvaluated || data == null)
      return;
    //Nothing to do if the field is not editable and displays evaulated data
    String textString;
    try
    {
      textString = Tree.dataToString(subtree.evaluateData(data, 0));
    }
    catch (Exception exc)
    {
      textString = data.toString();
    }
    if (textString != null)
    {
      if (textOnly && textString.charAt(0) == '"')
        textF.setText(textString.substring(1, textString.length() - 1));
      else
        textF.setText(textString);
    }
      }
    */

    @Override
    public void apply(final int currBaseNid) {
        return;
    }

    @Override
    protected void dataChanged(final int offsetNid, final Object data) {
        if(this.reportingChange || this.offsetNid != offsetNid) return;
        try{
            final String textData = ((Descriptor)data).toString();
            this.label.setText(textData);
            /*
                  textF.setText(textData);
            */
        }catch(final Exception exc){}
    }

    @Override
    protected void displayData(final Descriptor data, final boolean is_on) {
        this.data = data;
        this.initial_state = is_on;
        /*
        if (showState)
          checkB.setSelected(is_on);
        */
        if(data != null){
            String textString;
            if(this.displayEvaluated){
                try{
                    this.initialField = textString = this.subtree.evaluate(data).toString();
                }catch(final Exception exc){
                    textString = data.toString();
                }
            }else textString = data.toString();
            if(textString != null){
                if(this.textOnly && textString.charAt(0) == '"') this.label.setText(textString.substring(1, textString.length() - 1));
                else this.label.setText(textString);
            }
        }else this.label.setText("<empty>");
        /*
            label.setEnabled(is_on);
            textF.setEnabled(is_on & editable);
            textF.setEditable(is_on & editable);
        */
    }
    /*
      protected Descriptor getData()
      {
    String dataString = textF.getText();
    if (dataString == null)
      return null;
    if (textOnly)
      return Tree.dataFromExpr("\"" + dataString + "\"");
    else
      return Tree.dataFromExpr(dataString);
      }


      protected boolean getState()
      {
    if (!showState)
      return initial_state;
    else
      return checkB.isSelected();
      }

      public void setEnabled(boolean state)
      {
    if (!editable && state)
      return; //Do not set enabled if not editable
    //if(checkB != null) checkB.setEnabled(state);
    if (textF != null)
    {
      textF.setEnabled(state);
      textF.setEditable(state);
    }
    if (label != null)
      label.setEnabled(state);
      //if(checkB != null) checkB.setSelected(state);
      //initial_state = state;
      }
    */

    @Override
    protected Descriptor getData() {
        final String dataString = this.label.getText();
        try{
            if(dataString == null) return null;
            if(this.textOnly) return new CString(dataString);
            return Database.tdiCompile(dataString);
        }catch(final MdsException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /*
      public void setEditable(boolean editable)
      {
    this.editable = editable;
      }

      public boolean getEditable()
      {
    return editable;
      }
    */
    public boolean getDisplayEvaluated() {
        return this.displayEvaluated;
    }

    public String getLabelString() {
        return this.labelString;
    }

    public int getNumCols() {
        return this.numCols;
    }

    public int getPreferredWidth() {
        return this.preferredWidth;
    }

    @Override
    protected boolean getState() {
        return true;
    }

    public boolean getTextOnly() {
        return this.textOnly;
    }

    @Override
    protected void initializeData(final Descriptor data, final boolean is_on) {
        this.initializing = true;
        this.initial_state = is_on;
        // initialField = data.toString();
        /*
           Container parent = getParent();

           if (parent.getLayout() == null)
           {
             isGridBag = false;
           }
           else
             isGridBag = true;

           GridBagConstraints gc = null;
           if (isGridBag)
           {
             setLayout(gridbag = new GridBagLayout());
             gc = new GridBagConstraints();
             gc.anchor = GridBagConstraints.WEST;
             gc.gridx = gc.gridy = 0;
             gc.gridwidth = gc.gridheight = 1;
             gc.weightx = gc.weighty = 1.;
             gc.fill = GridBagConstraints.NONE;
             gridbag.setConstraints(jp, gc);
           }
        */
        /*
            if (showState)
            {
              //add(checkB = new JCheckBox());
              checkB.setVisible(true);
              checkB.setSelected(is_on);
              checkB.addChangeListener(new ChangeListener()
              {
        public void stateChanged(ChangeEvent e)
        {
          boolean state = checkB.isSelected();
          if (label != null)
            label.setEnabled(state);
          if (textF != null && editable)
          {
            textF.setEnabled(state);
            textF.setEditable(state);
          }
        }
              });
            }
            if (textF != null && isGridBag)
            {
              gc.gridx++;
              gc.anchor = GridBagConstraints.EAST;
              gridbag.setConstraints(textF, gc);
            }
        */
        this.displayData(data, is_on);
        /*
            setEnabled(is_on);
            textF.addKeyListener(new KeyAdapter()
            {
              public void keyTyped(KeyEvent e)
              {
        reportingChange = true;
        reportDataChanged(textF.getText());
        reportingChange = false;
              }
            });

            textF.setEnabled(editable);
            textF.setEditable(editable);
            if (preferredWidth > 0)
            {
              setPreferredSize(new Dimension(preferredWidth, getPreferredSize().height));
              setSize(new Dimension(preferredWidth, getPreferredSize().height));
            }
        */
        this.redisplay();
        this.initializing = false;
    }

    @Override
    protected boolean isDataChanged() {
        if(this.displayEvaluated && this.initialField != null) return !(this.label.getSize().equals(this.initialField));
        return true;
    }

    /*
      public boolean supportsState()
      {
    return showState;
      }
    */
    @Override
    public void setBounds(final int x, final int y, final int width, final int height) {
        super.setBounds(x, y, width, height);
        this.setPreferredSize(new Dimension(width, height));
    }

    public void setDisplayEvaluated(final boolean displayEvaluated) {
        this.displayEvaluated = displayEvaluated;
    }

    public void setLabelString(final String labelString) {
        this.labelString = labelString;
        this.label.setText(labelString);
        // redisplay();
    }

    public void setNumCols(final int numCols) {
        this.numCols = numCols;
        final FontMetrics fm = this.getFontMetrics(this.getFont());
        this.setSize(numCols * fm.charWidth('A'), fm.getHeight() + 4);
        // redisplay();
    }

    public void setPreferredWidth(final int preferredWidth) {
        this.preferredWidth = preferredWidth;
    }

    /*
      public void setShowState(boolean showState)
      {
    this.showState = showState;
    if (showState)
      checkB.setVisible(true);
    else
      checkB.setVisible(false);
      //redisplay();
      }
    */
    /*
      public boolean getShowState()
      {
    return showState;
      }
    */
    public void setTextOnly(final boolean textOnly) {
        this.textOnly = textOnly;
    }
}
