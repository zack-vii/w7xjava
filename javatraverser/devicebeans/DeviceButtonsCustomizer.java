package devicebeans;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.Customizer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.StringTokenizer;
import java.util.Vector;

@SuppressWarnings("serial")
public class DeviceButtonsCustomizer extends Panel implements Customizer{
    Button                addButton, removeButton, doneButton;
    DeviceButtons         bean         = null;
    TextField             expression, message, methods;
    Vector<String>        expressionsV = new Vector<String>();
    java.awt.List         exprList;
    PropertyChangeSupport listeners    = new PropertyChangeSupport(this);
    Vector<String>        messagesV    = new Vector<String>();

    public DeviceButtonsCustomizer(){}

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener l) {
        this.listeners.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener l) {
        this.listeners.removePropertyChangeListener(l);
    }

    @Override
    public void setObject(final Object o) {
        this.bean = (DeviceButtons)o;
        this.setLayout(new BorderLayout());
        Panel jp = new Panel();
        jp.setLayout(new BorderLayout());
        jp.add(this.exprList = new java.awt.List(10, false), "Center");
        this.exprList.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                final int idx = DeviceButtonsCustomizer.this.exprList.getSelectedIndex();
                DeviceButtonsCustomizer.this.expression.setText((DeviceButtonsCustomizer.this.expressionsV.elementAt(idx)));
                DeviceButtonsCustomizer.this.message.setText((DeviceButtonsCustomizer.this.messagesV.elementAt(idx)));
            }
        });
        final String exprs[] = this.bean.getCheckExpressions();
        if(exprs != null){
            final String messgs[] = this.bean.getCheckMessages();
            if(messgs != null){
                for(int i = 0; i < exprs.length; i++){
                    if(i >= messgs.length) break;
                    this.messagesV.addElement(messgs[i]);
                    this.expressionsV.addElement(exprs[i]);
                    this.exprList.add(exprs[i] + " : " + messgs[i]);
                }
            }
        }
        Panel jp1 = new Panel();
        jp1.add(new Label("Check expr.: "));
        jp1.add(this.expression = new TextField(30));
        jp1.add(new Label("Error message: "));
        jp1.add(this.message = new TextField(30));
        jp.add(jp1, "South");
        jp1 = new Panel();
        jp1.setLayout(new GridLayout(2, 1));
        Panel jp2 = new Panel();
        jp2.add(this.addButton = new Button("Add"));
        jp1.add(jp2);
        jp2 = new Panel();
        jp2.add(this.removeButton = new Button("Remove"));
        jp1.add(jp2);
        jp.add(jp1, "East");
        this.addButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String currExpr = DeviceButtonsCustomizer.this.expression.getText();
                if(currExpr != null && currExpr.length() > 0){
                    final String currMess = DeviceButtonsCustomizer.this.message.getText();
                    if(currMess != null && currMess.length() > 0){
                        DeviceButtonsCustomizer.this.messagesV.addElement(currMess);
                        DeviceButtonsCustomizer.this.expressionsV.addElement(currExpr);
                        DeviceButtonsCustomizer.this.exprList.add(currExpr + " : " + currMess);
                    }
                }
            }
        });
        this.removeButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int idx = DeviceButtonsCustomizer.this.exprList.getSelectedIndex();
                // exprList.delItem(idx);
                DeviceButtonsCustomizer.this.exprList.remove(idx);
                DeviceButtonsCustomizer.this.messagesV.removeElementAt(idx);
                DeviceButtonsCustomizer.this.expressionsV.removeElementAt(idx);
            }
        });
        this.add(jp, "Center");
        jp = new Panel();
        jp.setLayout(new BorderLayout());
        jp1 = new Panel();
        jp1.add(new Label("Methods: "));
        jp1.add(this.methods = new TextField(40));
        final String[] methodList = this.bean.getMethods();
        if(methodList != null && methodList.length > 0){
            String method_txt = methodList[0];
            for(int i = 1; i < methodList.length; i++){
                method_txt += " " + methodList[i];
            }
            this.methods.setText(method_txt);
        }
        jp.add(jp1, "North");
        jp1 = new Panel();
        jp1.add(this.doneButton = new Button("Apply"));
        jp.add(jp1, "South");
        this.doneButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String[] messages = new String[DeviceButtonsCustomizer.this.messagesV.size()];
                final String[] expressions = new String[DeviceButtonsCustomizer.this.expressionsV.size()];
                for(int i1 = 0; i1 < DeviceButtonsCustomizer.this.messagesV.size(); i1++){
                    messages[i1] = (DeviceButtonsCustomizer.this.messagesV.elementAt(i1));
                    expressions[i1] = (DeviceButtonsCustomizer.this.expressionsV.elementAt(i1));
                }
                final String[] oldCheckMessages = DeviceButtonsCustomizer.this.bean.getCheckMessages();
                DeviceButtonsCustomizer.this.bean.setCheckMessages(messages);
                DeviceButtonsCustomizer.this.listeners.firePropertyChange("checkMessages", oldCheckMessages, DeviceButtonsCustomizer.this.bean.getCheckMessages());
                final String[] oldCheckExpressions = DeviceButtonsCustomizer.this.bean.getCheckExpressions();
                DeviceButtonsCustomizer.this.bean.setCheckExpressions(expressions);
                DeviceButtonsCustomizer.this.listeners.firePropertyChange("checkExpressions", oldCheckExpressions, DeviceButtonsCustomizer.this.bean.getCheckExpressions());
                final String method_list = DeviceButtonsCustomizer.this.methods.getText();
                // System.out.println(method_list);
                final StringTokenizer st = new StringTokenizer(method_list, " ,;");
                final int num_methods = st.countTokens();
                if(num_methods > 0){
                    final String[] methods = new String[num_methods];
                    int i2 = 0;
                    while(st.hasMoreTokens()){
                        methods[i2] = st.nextToken();
                        // System.out.println(methods[i]);
                        i2++;
                    }
                    final String[] oldMethods = DeviceButtonsCustomizer.this.bean.getMethods();
                    DeviceButtonsCustomizer.this.bean.setMethods(methods);
                    DeviceButtonsCustomizer.this.listeners.firePropertyChange("methods", oldMethods, DeviceButtonsCustomizer.this.bean.getMethods());
                }
            }
        });
        this.add(jp, "South");
    }
}
