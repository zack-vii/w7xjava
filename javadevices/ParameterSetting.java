import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.TimerTask;
import java.util.Vector;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import devicebeans.Database;
import devicebeans.DeviceCloseListener;
import devicebeans.DeviceSetup;
import devicebeans.tools.LoadPulse;
import mds.data.descriptor.Descriptor;
import mds.data.descriptor_s.CString;
import mds.data.descriptor_s.Int32;
import mds.data.descriptor_s.Nid;
import mds.data.descriptor_s.Path;

/*
 * Le tabelle di configurazione rfxConfigHash e rfxConfigOnHash sono aggiornate in:
 * - init()
 * - modifica singola form config
 * - uscita dal PAS
 * Il check della configurazione e' eseguito in:
 * - uscita dal PAS
 * - Caricamento di un impulso
 * Quando viene caricata una configurazione salvata si esegue il confronto tra la configurazione salvata
 * e l'experiment model attuale.
 * La configurazione viene salvata solamente alla partenza e quando viene modificato un device di config
 */
public class ParameterSetting extends JFrame implements Printable{
    class AlarmHandler extends Thread{
        DataInputStream  dis;
        DataOutputStream dos;

        @Override
        public void run() {
            try{
                ServerSocket serverSock;
                if(ParameterSetting.this.isRt) serverSock = new ServerSocket(4003);
                else serverSock = new ServerSocket(4004);
                while(true){
                    final Socket sock = serverSock.accept();
                    this.dis = new DataInputStream(sock.getInputStream());
                    try{
                        while(true){
                            final String message = this.dis.readUTF();
                            JOptionPane.showMessageDialog(ParameterSetting.this, message, "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                    }catch(final Exception exc){
                        System.out.println("Client exited");
                    }
                }
            }catch(final Exception exc){
                System.err.println("Error accepting socket connections");
            }
        }
    }
    class DecouplingDialog extends JDialog{
        /**
         *
         */
        private static final long serialVersionUID = 7113472628301669529L;
        JComboBox                 decouplingC;

        DecouplingDialog(){
            super(ParameterSetting.this, "Select MHD decoupling");
            JPanel jp = new JPanel();
            final String[] matrixNames = ParameterSetting.this.getMatrixFiles();
            final String[] allMatrixNames = new String[matrixNames.length + 1];
            for(int i = 0; i < matrixNames.length; i++)
                allMatrixNames[i] = matrixNames[i].substring(0, matrixNames[i].length() - 4);
            allMatrixNames[matrixNames.length] = "From Shot...";
            this.decouplingC = new JComboBox(allMatrixNames);
            this.decouplingC.setSelectedIndex(0);
            jp.add(new JLabel("Decoupling: "));
            jp.add(this.decouplingC);
            this.getContentPane().add(jp, "Center");
            jp = new JPanel();
            final JButton okB = new JButton("Ok");
            okB.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    ParameterSetting.this.log("Set Decoupling");
                    ParameterSetting.this.setDecoupling((String)DecouplingDialog.this.decouplingC.getSelectedItem());
                    DecouplingDialog.this.setVisible(false);
                }
            });
            jp.add(okB);
            final JButton cancelB = new JButton("Cancel");
            cancelB.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    ParameterSetting.this.log("Set decoupling aborted");
                    DecouplingDialog.this.setVisible(false);
                }
            });
            jp.add(cancelB);
            this.getContentPane().add(jp, "South");
            this.pack();
        }
    } // End class DecouplingDialog
    class DiffButton extends JButton{
        /**
         *
         */
        private static final long serialVersionUID = 6712067604761140071L;
        int                       actShot;
        boolean                   highlightDiff    = false;
        int                       idx;

        DiffButton(final int actShot, final int idx){
            this(actShot, idx, true);
        }

        DiffButton(final int actShot, final int idx, final boolean highlightDiff){
            super("" + actShot);
            this.highlightDiff = highlightDiff;
            this.actShot = actShot;
            this.idx = idx;
            this.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    try{
                        ParameterSetting.this.rfx.close();
                        ParameterSetting.this.rfx = new Database("RFX", DiffButton.this.actShot);
                        ParameterSetting.this.rfx.open();
                    }catch(final Exception exc){
                        JOptionPane.showMessageDialog(ParameterSetting.this, "Cannot open pulse " + DiffButton.this.actShot, "Error opening tree", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    ParameterSetting.this.updateDeviceNids();
                    try{
                        ParameterSetting.this.log("Show difference in device " + DiffButton.this.idx);
                        final DeviceSetup currDevice = ParameterSetting.this.createDevice(DiffButton.this.idx);
                        currDevice.configure(ParameterSetting.this.rfx, ParameterSetting.this.nids[DiffButton.this.idx].getValue());
                        currDevice.setReadOnly(true);
                        currDevice.setTitle(currDevice.getTitle() + "  Shot: " + DiffButton.this.actShot);
                        if(DiffButton.this.highlightDiff) currDevice.setHighlight(true, ParameterSetting.this.modifiedNids);
                        currDevice.setVisible(true);
                    }catch(final Exception exc){
                        System.err.println("Error creating device: " + exc);
                    }
                    try{
                        ParameterSetting.this.rfx.close();
                        ParameterSetting.this.rfx = new Database("RFX", ParameterSetting.this.shot);
                        ParameterSetting.this.rfx.open();
                    }catch(final Exception exc){
                        JOptionPane.showMessageDialog(ParameterSetting.this, "Cannot open pulse " + ParameterSetting.this.shot, "Error opening tree", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    ParameterSetting.this.updateDeviceNids();
                }
            });
        }
    } // End inner class DiffButton
    class PrintButton extends JButton implements ActionListener{
        /**
         *
         */
        private static final long serialVersionUID = 3177529663194018071L;
        int                       idx;

        PrintButton(final int idx){
            super("Print");
            this.idx = idx;
            this.addActionListener(this);
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            try{
                ParameterSetting.this.log("Print device " + this.idx);
                ParameterSetting.this.print(this.idx);
            }catch(final Exception exc){
                System.err.println("Cannot print device: " + exc);
            }
        }
    }
    static class RfxFileFilter extends javax.swing.filechooser.FileFilter{
        @Override
        public boolean accept(final File f) {
            if(f.isDirectory()) return true;
            return f.getName().endsWith(".rfx");
        }

        @Override
        public String getDescription() {
            return "Saved configurations for RFX";
        }
    }
    class RtHandler extends Thread{
        int currIdx;

        @Override
        public void run() {
            try{
                final ServerSocket serverSock = new ServerSocket(4000);
                while(true){
                    final Socket sock = serverSock.accept();
                    final DataInputStream dis = new DataInputStream(sock.getInputStream());
                    try{
                        while(true){
                            // The index of the changed forms are passed
                            this.currIdx = dis.readInt();
                            if(this.currIdx >= 0){
                                System.out.println("RECEIVED UNCHECK " + this.currIdx);
                                ParameterSetting.this.states[this.currIdx] = ParameterSetting.UNCHECKED;
                                SwingUtilities.invokeAndWait(new Runnable(){
                                    @Override
                                    public void run() {
                                        System.out.println("ADESSO METTO ROSSO");
                                        ParameterSetting.this.buttons[RtHandler.this.currIdx].setForeground(Color.red);
                                    }
                                });
                            }else if(this.currIdx == -1)// Going to receive the list of modified nids
                            {
                                final int numModifiedNids = dis.readInt();
                                ParameterSetting.this.modifiedNids = new int[numModifiedNids];
                                for(int i = 0; i < numModifiedNids; i++)
                                    ParameterSetting.this.modifiedNids[i] = dis.readInt();
                                ParameterSetting.this.getCurrFFState();
                            }else // currIdx == -2. Notified and of applyToModel
                                ParameterSetting.this.getCurrFFState();
                        }
                    }catch(final Exception exc){
                        System.out.println("Client exited");
                    }
                }
            }catch(final Exception exc){
                System.err.println("Error accepting socket connections");
            }
        }
    } // End class RtHandler
    class SchedulerHandler extends Thread{
        DataInputStream  dis;
        DataOutputStream dos;

        private String executePulseCheck() {
            try{
                final Descriptor msgData = ParameterSetting.this.rfx.tdiExecute("ParameterSettingCheck()");
                String s = msgData.toString();
                s = s.substring(1, s.length() - 1);
                final StringTokenizer st = new StringTokenizer(s, "#");
                String out = "";
                while(st.hasMoreTokens()){
                    out += st.nextToken() + "\n";
                }
                return out;
            }catch(final Exception exc){
                System.err.println("Error evalutaing ParameterSettingCheck function : " + exc);
                return "";
            }
        }

        void proceedeConfirm() {
            String msg = this.executePulseCheck();
            System.out.println("Messaggio = " + msg);
            if(msg != null && msg.length() > 0) msg = "Transitare dal PAS (Corrente Magnetizzante: " + ParameterSetting.this.getMagnetizingCurrent() + " A)?\n\n\tATTENZIONE\n\n" + msg;
            else msg = "Transitare dal PAS (Corrente Magnetizzante: " + ParameterSetting.this.getMagnetizingCurrent() + " A)?\n\n";
            JOptionPane.showConfirmDialog(ParameterSetting.this, msg, "Acknowledgement request", JOptionPane.YES_OPTION);
            /* Taliercio 6 - 2 - 2009
            JOptionPane.showConfirmDialog(
                ParameterSetting.this,
                "Transitare dal PAS (Corrente Magnetizzante: " +
                getMagnetizingCurrent() + " A)?",
                "Acknowledgement request",
                JOptionPane.YES_OPTION);
            */
            try{
                this.dos.writeInt(1);
                this.dos.flush();
            }catch(final Exception exc){
                System.err.println("Error sending scheduler answer: " + exc);
            }
        }

        void proceedeLimits() {
            final String limitsMsg = ParameterSetting.this.checkLimits();
            if(limitsMsg != null){
                ParameterSetting.this.limitsWd = new WarningDialog(ParameterSetting.this, limitsMsg);
                ParameterSetting.this.limitsWd.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final String newMsg = ParameterSetting.this.checkLimits();
                        if(newMsg != null) ParameterSetting.this.limitsWd.setText(newMsg);
                        else{
                            ParameterSetting.this.limitsWd.dispose();
                            SchedulerHandler.this.proceedeConfirm();
                        }
                    }
                });
                ParameterSetting.this.limitsWd.pack();
                ParameterSetting.this.limitsWd.setVisible(true);
            }else this.proceedeVersions();
            // proceedeConfirm();
        }

        void proceedeVersions() {
            final String versionMsg = ParameterSetting.this.checkVersionsForPas();
            if(versionMsg != null){
                ParameterSetting.this.versionWd = new WarningDialog(ParameterSetting.this, versionMsg);
                ParameterSetting.this.versionWd.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final String newMsg = ParameterSetting.this.checkVersionsForPas();
                        if(newMsg != null) ParameterSetting.this.versionWd.setText(newMsg);
                        else{
                            ParameterSetting.this.versionWd.dispose();
                            SchedulerHandler.this.proceedeConfirm();
                        }
                    }
                });
                ParameterSetting.this.versionWd.pack();
                ParameterSetting.this.versionWd.setVisible(true);
            }else this.proceedeConfirm();
        }

        @Override
        public void run() {
            try{
                ServerSocket serverSock;
                if(ParameterSetting.this.isRt) serverSock = new ServerSocket(4001);
                else serverSock = new ServerSocket(4002);
                while(true){
                    final Socket sock = serverSock.accept();
                    this.dis = new DataInputStream(sock.getInputStream());
                    this.dos = new DataOutputStream(sock.getOutputStream());
                    try{
                        while(true){
                            // The index of the changed forms are passed
                            final int phaseIdx = this.dis.readInt();
                            switch(phaseIdx){
                                case LEAVE_PAS:{
                                    if(ParameterSetting.this.isRt){
                                        if(!ParameterSetting.this.allChecked()){
                                            ParameterSetting.this.checkedWd = new WarningDialog(ParameterSetting.this, "Una o piu' form di impostazione non sono state verificate");
                                            ParameterSetting.this.checkedWd.addActionListener(new ActionListener(){
                                                @Override
                                                public void actionPerformed(final ActionEvent e) {
                                                    if(ParameterSetting.this.allChecked()){
                                                        ParameterSetting.this.checkedWd.dispose();
                                                        // proceedeConfig();
                                                        SchedulerHandler.this.proceedeLimits();
                                                    }
                                                }
                                            });
                                            ParameterSetting.this.checkedWd.pack();
                                            ParameterSetting.this.checkedWd.setVisible(true);
                                        }else{
                                            // proceedeConfig();
                                            this.proceedeLimits();
                                        }
                                    }else{
                                        ParameterSetting.this.applyToModelItem.setEnabled(false);
                                        if(ParameterSetting.this.isOnline) ParameterSetting.this.applyToModelB.setEnabled(false);
                                        ParameterSetting.this.revertModelItem.setEnabled(false);
                                    }
                                    break;
                                }
                                case ENTER_PRE:
                                    // doingShot = true;
                                case ENTER_PAS:
                                    if(!ParameterSetting.this.isRt){
                                        ParameterSetting.this.applyToModelItem.setEnabled(true);
                                        if(ParameterSetting.this.isOnline) ParameterSetting.this.applyToModelB.setEnabled(true);
                                        ParameterSetting.this.revertModelItem.setEnabled(true);
                                        ParameterSetting.this.setTitle("RFX Parameters     shot: " + ParameterSetting.this.getShot());
                                    }else ParameterSetting.this.setTitle("RFX Parameters -- RT --  shot: " + ParameterSetting.this.getShot());
                                    break;
                                case LEAVE_SECONDARY:
                                    ParameterSetting.this.doingShot = false;
                                    // i2tEvaluateResidualPostPulse();
                                    if(!ParameterSetting.this.isRt) ParameterSetting.this.setTitle("RFX Parameters     shot: " + ParameterSetting.this.getShot());
                                    else ParameterSetting.this.setTitle("RFX Parameters -- RT --  shot: " + ParameterSetting.this.getShot());
                            }
                        }
                    }catch(final Exception exc){
                        System.out.println("Client exited");
                    }
                }
            }catch(final Exception exc){
                System.err.println("Error accepting socket connections");
            }
        }
    } // End SchedulerHandler
      // Class SelectSetup defines the check boxes for selective load and save
    class SelectSetup extends JDialog{
        /**
         *
         */
        private static final long serialVersionUID = 6083214427754633522L;
        JCheckBox[]               checkBoxes       = new JCheckBox[ParameterSetting.NUM_SETUP - 1];
        JCheckBox                 currFFCB;
        JCheckBox                 mhdDecoupingCheckBox;
        JCheckBox                 poloidalCB, axiCB, pcCB, pmCB, toroidalCB, chopperCB, feedForwardCB, inverterCB, tfCB, bfCB, mhdCB, viCB, pelletCB, diagTimesCB, ipCB, timesPmCB, timesPcCB, timesPvCB, timesPpCB, timesPrCB, timesPtsoCB, timesPtcbCB,
                timesPtctCB, timesGasCB, timesTfCB, timesIsCB, timesChopperCB, timesInverterCB;
        JCheckBox[]               timeCheckBoxes   = new JCheckBox[13];

        SelectSetup(final ActionListener actionListener){
            super(ParameterSetting.this, "Select components");
            final JPanel jp1 = new JPanel();
            jp1.setLayout(new GridLayout(1, 2));
            JPanel jp = new JPanel();
            jp.setLayout(new GridLayout(ParameterSetting.NUM_SETUP, 1));
            jp.add(this.checkBoxes[0] = this.poloidalCB = new JCheckBox("EDA1", true));
            jp.add(this.checkBoxes[1] = this.axiCB = new JCheckBox("Axisymmetric contr.", true));
            jp.add(this.checkBoxes[2] = this.pcCB = new JCheckBox("PC", true));
            jp.add(this.checkBoxes[3] = this.pmCB = new JCheckBox("PM", true));
            jp.add(this.checkBoxes[4] = this.toroidalCB = new JCheckBox("EDA3", true));
            jp.add(this.checkBoxes[5] = this.chopperCB = new JCheckBox("Chopper", true));
            jp.add(this.checkBoxes[6] = this.feedForwardCB = new JCheckBox("Feed Forward", true));
            jp.add(this.checkBoxes[7] = this.inverterCB = new JCheckBox("Inverter", true));
            jp.add(this.checkBoxes[8] = this.tfCB = new JCheckBox("TF", true));
            jp.add(this.checkBoxes[9] = this.bfCB = new JCheckBox("B & F", true));
            jp.add(this.checkBoxes[10] = this.mhdCB = new JCheckBox("MHD", true));
            jp.add(this.checkBoxes[11] = this.viCB = new JCheckBox("VI", true));
            jp.add(this.checkBoxes[12] = this.pelletCB = new JCheckBox("Pellet", true));
            jp.add(this.checkBoxes[13] = this.diagTimesCB = new JCheckBox("Diag. times", true));
            jp.add(this.checkBoxes[14] = this.ipCB = new JCheckBox("IP Control", true));
            jp.add(this.currFFCB = new JCheckBox("MHD FF", true));
            jp1.add(jp);
            jp = new JPanel();
            jp.setLayout(new GridLayout(13, 1));
            jp.add(this.timeCheckBoxes[0] = this.timesPmCB = new JCheckBox("Times: PM", true));
            jp.add(this.timeCheckBoxes[1] = this.timesPcCB = new JCheckBox("Times: PC", true));
            jp.add(this.timeCheckBoxes[2] = this.timesPvCB = new JCheckBox("Times: PV", true));
            jp.add(this.timeCheckBoxes[3] = this.timesPpCB = new JCheckBox("Times: PP", true));
            jp.add(this.timeCheckBoxes[4] = this.timesPrCB = new JCheckBox("Times: PR", true));
            jp.add(this.timeCheckBoxes[5] = this.timesPtsoCB = new JCheckBox("Times: PTSO", true));
            jp.add(this.timeCheckBoxes[6] = this.timesPtcbCB = new JCheckBox("Times: PTCB", true));
            jp.add(this.timeCheckBoxes[7] = this.timesPtctCB = new JCheckBox("Times: PTCT", true));
            jp.add(this.timeCheckBoxes[8] = this.timesGasCB = new JCheckBox("Times: Gas", true));
            jp.add(this.timeCheckBoxes[9] = this.timesTfCB = new JCheckBox("Times: TF", true));
            jp.add(this.timeCheckBoxes[10] = this.timesIsCB = new JCheckBox("Times: IS", true));
            jp.add(this.timeCheckBoxes[11] = this.timesChopperCB = new JCheckBox("Times: Chopper", true));
            jp.add(this.timeCheckBoxes[12] = this.timesInverterCB = new JCheckBox("Times: Inverter", true));
            jp1.add(jp);
            this.getContentPane().add(jp1, "Center");
            jp = new JPanel();
            final JButton deselectAllB = new JButton("Deselect All");
            deselectAllB.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    // for(int i = 0; i < 14; i++)
                    for(int i = 0; i < 15; i++)
                        SelectSetup.this.checkBoxes[i].setSelected(false);
                    for(int i = 0; i < 13; i++)
                        SelectSetup.this.timeCheckBoxes[i].setSelected(false);
                    SelectSetup.this.currFFCB.setSelected(false);
                }
            });
            jp.add(deselectAllB);
            final JButton saveB = new JButton("Save");
            saveB.addActionListener(actionListener);
            jp.add(saveB);
            final JButton cancelB = new JButton("Cancel");
            cancelB.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    SelectSetup.this.setVisible(false);
                }
            });
            jp.add(cancelB);
            this.getContentPane().add(jp, "South");
            this.pack();
        }

        boolean currFFSelected() {
            return this.currFFCB.isSelected();
        }

        void enable(final int[] nids, final int[] mask, final int idx) {
            final int baseNid = ParameterSetting.this.nids[0].getValue();
            for(final int element : mask){
                for(final int nid : nids){
                    if(nid - baseNid == element){
                        this.timeCheckBoxes[idx].setEnabled(true);
                        this.timeCheckBoxes[idx].setSelected(true);
                    }
                }
            }
        }

        boolean[] getSelectedDevices() {
            return new boolean[]{this.poloidalCB.isSelected(), this.axiCB.isSelected(), this.pcCB.isSelected(), this.pmCB.isSelected(), this.toroidalCB.isSelected(), this.chopperCB.isSelected(), this.feedForwardCB.isSelected(), this.inverterCB.isSelected(), this.tfCB.isSelected(), this.bfCB.isSelected(), this.mhdCB.isSelected(), this.viCB.isSelected(), this.pelletCB.isSelected(), this.diagTimesCB.isSelected(), this.ipCB.isSelected()};
        }

        boolean[] getSelectedTimes() {
            return new boolean[]{this.timesPmCB.isSelected(), this.timesPcCB.isSelected(), this.timesPvCB.isSelected(), this.timesPpCB.isSelected(), this.timesPrCB.isSelected(), this.timesPtsoCB.isSelected(), this.timesPtcbCB.isSelected(), this.timesPtctCB.isSelected(), this.timesGasCB.isSelected(), this.timesTfCB.isSelected(), this.timesIsCB.isSelected(), this.timesChopperCB.isSelected(), this.timesInverterCB.isSelected()};
        }

        void setEnabledDevices(final Hashtable setupHash) {
            for(int i = 0; i < ParameterSetting.NUM_SETUP - 1; i++){
                this.checkBoxes[i].setSelected(false);
                this.checkBoxes[i].setEnabled(false);
            }
            /*            Enumeration mapNames = mapSetupHash.keys();
            System.out.println("\n\n\nMAP CONTENT");
             while (mapNames.hasMoreElements())
                System.out.println((String)mapNames.nextElement());
            
            
            */
            final Enumeration pathNames = setupHash.keys();
            while(pathNames.hasMoreElements()){
                final String currPathName = (String)pathNames.nextElement();
                // System.out.println("Setup Hash: " + currPathName);
                // Integer currInt = (Integer) mapSetupHash.get(pathNames.
                // nextElement());
                final Integer currInt = (Integer)ParameterSetting.this.mapSetupHash.get(currPathName);
                if(currInt == null) System.out.println("MISSING IDX for " + currPathName);
                if(currInt != null){
                    final int idx = currInt.intValue();
                    if(idx > 0){
                        this.checkBoxes[idx - 1].setEnabled(true);
                        this.checkBoxes[idx - 1].setSelected(true);
                    }
                }
            }
            this.currFFCB.setSelected(true);
        }

        void setEnabledDevicesForSavingConfiguration() {
            for(int i = 0; i < ParameterSetting.NUM_SETUP - 1; i++){
                this.checkBoxes[i].setSelected(true);
                this.checkBoxes[i].setEnabled(true);
            }
            for(int i = 0; i < 13; i++){
                this.timeCheckBoxes[i].setSelected(true);
                this.timeCheckBoxes[i].setEnabled(true);
            }
            this.currFFCB.setSelected(true);
            this.checkBoxes[0].setSelected(false);
            this.checkBoxes[0].setEnabled(false);
            this.timeCheckBoxes[5].setSelected(true);
            this.timeCheckBoxes[5].setEnabled(false);
        }

        void setEnabledTimes(final Hashtable setupHash) {
            for(int i = 0; i < 13; i++){
                this.timeCheckBoxes[i].setSelected(false);
                this.timeCheckBoxes[i].setEnabled(false);
            }
            final int nids[] = new int[setupHash.size()];
            final Enumeration pathNames = setupHash.keys();
            int idx = 0;
            while(pathNames.hasMoreElements()){
                final String currPath = (String)pathNames.nextElement();
                try{
                    final Nid currNid = new Path(currPath).toNid();
                    nids[idx] = currNid.getValue();
                    idx++;
                }catch(final Exception exc){
                    System.err.println("Internal error in setEnabledTimes for " + currPath + ": " + exc);
                }
            }
            this.enable(nids, ParameterSetting.this.pm_mask, 0);
            this.enable(nids, ParameterSetting.this.pc_mask, 1);
            this.enable(nids, ParameterSetting.this.pv_mask, 2);
            this.enable(nids, ParameterSetting.this.pp_mask, 3);
            this.enable(nids, ParameterSetting.this.pr_mask, 4);
            this.enable(nids, ParameterSetting.this.ptso_mask, 5);
            // MAY 2009 Force PTSO NOT TO BE ENABLED
            this.timeCheckBoxes[5].setEnabled(false);
            this.enable(nids, ParameterSetting.this.ptcb_mask, 6);
            this.enable(nids, ParameterSetting.this.ptct_mask, 7);
            this.enable(nids, ParameterSetting.this.gas_mask, 8);
            this.enable(nids, ParameterSetting.this.tf_mask, 9);
            this.enable(nids, ParameterSetting.this.is_mask, 10);
            this.enable(nids, ParameterSetting.this.chopper_mask, 11);
            this.enable(nids, ParameterSetting.this.inverter_mask, 12);
        }
    } // End class SelectSetup
    class WarningDialog extends JDialog{
        /**
         *
         */
        private static final long serialVersionUID = 944336110202627958L;
        JLabel                    label;
        JButton                   retryB;

        WarningDialog(final JFrame frame, final String message){
            super(frame, "Warning");
            this.getContentPane().add(this.label = new JLabel(message), "Center");
            final JPanel jp = new JPanel();
            this.retryB = new JButton("Retry");
            jp.add(this.retryB);
            this.getContentPane().add(jp, "South");
            this.setLocation(new Point(frame.getWidth() / 2, frame.getHeight() / 2));
            this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        }

        void addActionListener(final ActionListener al) {
            this.retryB.addActionListener(al);
        }

        void setText(final String text) {
            this.label.setText(text);
        }
    } // End class WarningDialog
    static final int          CHECKED             = 0, CHECKING = 1, UNCHECKED = 2;
    static final String       DECOUPLING_BASE_DIR = "/usr/local/rfx/data_acquisition/real_time/decoupling/";
    static final int          ENTER_PAS           = 1, LEAVE_PAS = 2, ENTER_PRE = 3, LEAVE_SECONDARY = 4;
    // static final int NUM_DEVICES = 22;
    static final int          NUM_DEVICES         = 23;
    // static final int NUM_SETUP = 15;
    static final int          NUM_SETUP           = 16;
    static String             refShotLabelText    = "Ref. Shot: ";
    /**
     *
     */
    private static final long serialVersionUID    = 3173238522773974932L;

    /*
    void i2tEvaluateResidualPrePulse()
    {
    try {
    	if( rfx == null )
    	{
    		Descriptor msgData = rfx.evaluateData( Database.tdiCompile("I2t_PM('PRE_PULSE')"), 0 );
    		float out = msgData.toFloat()[0];
    		residualI2tPMLabel.setText( "Residual I2t PM = "+out+" A2s" );
    		if( out < 0 )
    			JOptionPane.showMessageDialog(ParameterSetting.this, "ATTENZIONE : con questa impostazione di PM superato il valore di i2t (23e9) giornaliero ammesso",
                "Error", JOptionPane.ERROR_MESSAGE);
    	}
    }catch(Exception exc)
    {
        JOptionPane.showMessageDialog(ParameterSetting.this, "Error Evalating Residual I2T Magnetizing",
                "Error", JOptionPane.ERROR_MESSAGE);
    
    }
    }
    
    void i2tEvaluateResidualPostPulse()
    
    {
    try {
    	if( rfx != null )
    	{
    		Descriptor msgData = rfx.evaluateData( Database.tdiCompile("I2t_PM('POST_PULSE')"), 0 );
    		float out = msgData.toFloat()[0];
    		residualI2tPMLabel.setText( "Residual I2t PM = "+out+" A2s" );
    		if( out < 0 )
    			JOptionPane.showMessageDialog(ParameterSetting.this, "ATTENZIONE : Superato il valore di i2t (23e9) giornaliero ammesso sessione da SOSPENDERE",
                "Error", JOptionPane.ERROR_MESSAGE);
    	}
    }catch (Exception exc)
    {
        JOptionPane.showMessageDialog(ParameterSetting.this, "Error Evalating Residual I2T Magnetizing",
                "Error", JOptionPane.ERROR_MESSAGE);
    
    }
    }
    */
    public static void main(final String args[]) {
        ParameterSetting parameterS;
        if(args.length > 1) parameterS = new ParameterSetting(args[0], args[1]);
        else if(args.length > 0) parameterS = new ParameterSetting(args[0]);
        else parameterS = new ParameterSetting();
        parameterS.init();
        parameterS.pack();
        parameterS.setVisible(true);
    }
    JButton          applyToModelB;
    JMenuItem        applyToModelItem;
    JButton          buttons[]        = new JButton[ParameterSetting.NUM_DEVICES];
    JDialog          changedD;
    /*
    	JLabel residualI2tPMLabel;
    */
    WarningDialog    checkedWd, configWd, limitsWd, versionWd;
    JFileChooser     chooser          = new JFileChooser();
    JMenuItem        compareItem;
    JComboBox        currFFC;
    int              currLoadShot;
    int              currPrintDeviceIdx, currPrintLoadPulse;
    Hashtable        currSetupHash    = new Hashtable();
    Hashtable        currSetupOnHash  = new Hashtable();
    DecouplingDialog decouplingD;
    int              decouplingKeys[];
    String           decouplingNames[];
    // Nid mhdBcNid;
    DeviceSetup      devices[]        = new DeviceSetup[ParameterSetting.NUM_DEVICES];
    boolean          doingShot        = false;
    boolean          isOnline;
    boolean          isRt             = false;
    FileWriter       logFile          = null;
    /*   Hashtable rfxConfigHash = new Hashtable();
       Hashtable rfxConfigOnHash = new Hashtable();
       Hashtable currConfigHash = new Hashtable();
       Hashtable currConfigOnHash = new Hashtable();
     */
    Hashtable        mapSetupHash     = new Hashtable();
    int              maxPMAT, maxPCATParallel, maxPCATSeries, maxTFAT, maxTCCH, maxTCAC, maxPMVoltage,
            // maxFillVoltage, maxPuffVoltage,
            maxTempRoom, maxTempSaddle, maxTempMagnetizing, maxPOhm, maxPrTime, maxTempTor, maxI2T, maxCurrSellaV, maxCurrSellaVI0, maxCurrSellaP, maxFillPuffVoltage;
    JTextField       maxPMATF, maxPCATParallelF, maxPCATSeriesF, maxTFATF, maxTCCHF, maxTCACF, maxPMVoltageF,
            // maxFillVoltageF, maxPuffVoltageF,
            maxTempRoomF, maxTempSaddleF, maxTempMagnetizingF, maxPOhmF, maxPrTimeF, maxTempTorF, maxI2TF, maxCurrSellaVF, maxCurrSellaVI0F, maxCurrSellaPF, maxFillPuffVoltageF;
    JTextArea        messageArea;
    JComboBox        modeC;
    Hashtable        modelSetupHash   = new Hashtable();
    Hashtable        modelSetupOnHash = new Hashtable();
    int[]            modifiedNids;
    Nid              nids[]           = new Nid[ParameterSetting.NUM_DEVICES];
    int[]            pm_mask          = new int[]{25, 26, 1}, pc_mask = new int[]{29, 30, 14}, pv_mask = new int[]{27, 28}, pp_mask = new int[]{2, 3, 4, 5}, pr_mask = new int[]{45, 46}, ptso_mask = new int[]{19, 20, 21, 22},
            ptcb_mask = new int[]{15, 16, 17, 18}, ptct_mask = new int[]{6, 7, 8, 9, 10, 11, 12, 13}, gas_mask = new int[]{47, 48}, tf_mask = new int[]{23, 24}, is_mask = new int[]{38, 39}, chopper_mask = new int[]{32, 33},
            inverter_mask = new int[]{34, 35, 36, 37, 40, 41, 42, 43, 44};
    Cursor           prevCursor       = this.getCursor();
    PrintService     printService     = PrintServiceLookup.lookupDefaultPrintService();
    boolean          readOnly         = false;
    JLabel           refShotLabel;
    JMenuItem        revertModelItem;
    Database         rfx;
    DataOutputStream rtDos;
    String           rtIp;
    Socket           rtSock;
    SelectSetup      saveSelected     = null, loadSelected = null, applyModelSelected = null;
    int              shot             = 100;
    int              states[]         = new int[]{ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED, ParameterSetting.CHECKED};
    JTabbedPane      tabbedP;
    JButton          timesB, poloidalControlB, axiSetupB, pcSetupB, pmSetupB, ipSetupB, toroidalControlB, chopperSetupB, ffSetupB, inverterSetupB, tfSetupB, bfControlB, mhdControlB, viSetupB, mopB, ansaldoConfigB, unitsConfigB, poloidalConfigB,
            toroidalConfigB, mhdConfigB, viConfigB;
    Nid              timesRoot, poloidalControlRoot, axiSetupRoot, pcSetupRoot, pmSetupRoot, ipSetupRoot, toroidalControlRoot, chopperSetupRoot, ffSetupRoot, inverterSetupRoot, tfSetupRoot, bfControlRoot, mhdControlRoot, viSetupRoot, mopRoot,
            ansaldoConfigRoot, unitsConfigRoot, poloidalConfigRoot, toroidalConfigRoot, mhdConfigRoot, viConfigRoot, pvSetupRoot,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              // Usato
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               // solo
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               // per
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               // la
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               // configurazione
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               // PV
            pelletSetupRoot, diagTimesSetupRoot;
    String[]         titles           = {"TIMES SETUP", "POLOIDAL CONTROL", "AXISYMMETRIC SETUP", "PC SETUP", "PM SETUP", "TOROIDAL CONTROL", "CHOPPER SETUP", "FEEDFORWARD SETUP", "INVERTER SETUP", "TF SETUP", "B&F CONTROL", "MHD CONTROL", "VI SETUP", "MOP", "UNITS SETUP", "IP SETUP", "UNITS CONFIG", "POLOIDAL CONFIG", "TOROIDAL CONFIG", "MHD CONFIG", "VI CONFIG"};

    ParameterSetting(){
        this(true, false, null);
    }

    ParameterSetting(final boolean isRt, final boolean isOnline, final String rtIp){
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });
        this.isRt = isRt;
        if(isRt) this.readOnly = true;
        this.isOnline = isOnline;
        if(isRt){
            this.setTitle("RFX Parameters -- RT    ");
            this.handleRt();
        }else{
            this.setTitle("RFX Parameters");
            this.rtIp = rtIp;
            if(isOnline) this.handleNotRt();
        }
        if(isRt || isOnline){
            this.handleScheduler();
            this.handleAlarms();
        }
        if(!isRt) this.prepareDecouplingInfo();
        final JMenuBar menuBar = new JMenuBar();
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem printItem = new JMenuItem("Print Setup");
        printItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("Print Setup");
                ParameterSetting.this.printSetup();
            }
        });
        fileMenu.add(printItem);
        if(!isRt){
            JMenuItem loadItem = new JMenuItem("Load pulse...");
            loadItem.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    ParameterSetting.this.loadPulse();
                }
            });
            fileMenu.add(loadItem);
            final JMenuItem saveItem = new JMenuItem("Save Configuration...");
            saveItem.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    ParameterSetting.this.saveSelectedSetup();
                }
            });
            fileMenu.add(saveItem);
            loadItem = new JMenuItem("Load Configuration...");
            loadItem.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    ParameterSetting.this.loadSetup();
                }
            });
            fileMenu.add(loadItem);
            this.applyToModelItem = new JMenuItem("Apply To Model");
            this.applyToModelItem.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if(JOptionPane.showConfirmDialog(ParameterSetting.this, "Transfer current configuration to the experiment model?", "Confirmation request", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                        ParameterSetting.this.log("Apply to Model");
                        ParameterSetting.this.applyToModel();
                    }else ParameterSetting.this.log("Apply to Model aborted");
                }
            });
            fileMenu.add(this.applyToModelItem);
            this.applyToModelItem.setEnabled(isOnline);
            this.revertModelItem = new JMenuItem("Revert To Previous Model");
            this.revertModelItem.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if(JOptionPane.showConfirmDialog(ParameterSetting.this, "Revert to the last experiment model?", "Confirmation request", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                        ParameterSetting.this.log("Revert to last experiment model");
                        ParameterSetting.this.revertModel();
                    }else ParameterSetting.this.log("Revert to last experiment model aborted");
                }
            });
            this.revertModelItem.setEnabled(false);
            fileMenu.add(this.revertModelItem);
            this.compareItem = new JMenuItem("Compare to Shot ...");
            this.compareItem.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    ParameterSetting.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    SwingUtilities.invokeLater(new Runnable(){
                        @Override
                        public void run() {
                            ParameterSetting.this.compareShots();
                        }
                    });
                }
            });
            fileMenu.add(this.compareItem);
            final JMenuItem decouplingItem = new JMenuItem("Set MHD Decoupling...");
            decouplingItem.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    ParameterSetting.this.setDecoupling();
                }
            });
            fileMenu.add(decouplingItem);
            final JMenuItem decouplingInfoItem = new JMenuItem("Get MHD Decoupling Info...");
            decouplingInfoItem.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    ParameterSetting.this.log("Show decoupling info");
                    ParameterSetting.this.showDecouplingInfo();
                }
            });
            fileMenu.add(decouplingInfoItem);
        }
        menuBar.add(fileMenu);
        final JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                try{
                    ParameterSetting.this.logFile.close();
                }catch(final Exception exc){}
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);
        this.setJMenuBar(menuBar);
        final JPanel setupJp = new JPanel();
        setupJp.setLayout(new BorderLayout());
        JPanel jp = new JPanel();
        if(!isRt){
            jp.add(new JLabel("Working shot: "));
            if(isOnline){
                this.modeC = new JComboBox(new String[]{"100"});
                this.shot = 100;
            }else{
                this.modeC = new JComboBox(new String[]{"101", "102", "103", "104", "105", "106", "107", "108", "109"});
                this.shot = 101;
            }
            this.modeC.setSelectedIndex(0);
            this.modeC.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final int idx = ParameterSetting.this.modeC.getSelectedIndex();
                    final int baseShot = ((ParameterSetting.this.isOnline) ? 100 : 101);
                    try{
                        ParameterSetting.this.rfx.close();
                        ParameterSetting.this.rfx = new Database("rfx", -1);
                        ParameterSetting.this.rfx.open();
                        ParameterSetting.this.rfx.create(baseShot + idx);
                        ParameterSetting.this.rfx.close();
                        ParameterSetting.this.rfx = new Database("rfx", baseShot + idx);
                        ParameterSetting.this.rfx.open();
                        ParameterSetting.this.shot = baseShot + idx;
                    }catch(final Exception exc){
                        System.err.println("Error opening working RFX pulse: " + exc);
                        System.exit(0);
                    }
                }
            });
            jp.add(this.modeC);
            if(isOnline) jp.add(this.refShotLabel = new JLabel(""));
        }
        this.buttons[0] = this.timesB = new JButton("Times Setup");
        this.timesB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("Times Setup open");
                final int nid = ParameterSetting.this.timesRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[0] == null)
                {
                    ParameterSetting.this.devices[0] = device = new RFXTimesSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(0);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(0, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[0] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[0] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        jp.add(this.timesB);
        this.buttons[14] = this.viSetupB = new JButton("Diag Times Setup");
        this.viSetupB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("Pellet Setup open");
                final int nid = ParameterSetting.this.diagTimesSetupRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[12] == null)
                {
                    ParameterSetting.this.devices[14] = device = new RFXDiagTimesV1Setup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(14);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(14, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[14] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[14] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        jp.add(this.buttons[14]);
        /*
        JButton forTestB = new JButton("Test Button");
        forTestB.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
        		String limitsMsg = checkLimits();
        		System.out.println("Limint : " + limitsMsg);
        	    }
        });
        	jp.add(forTestB);
        */
        if(!isRt && isOnline){
            this.applyToModelB = new JButton("Apply To Model");
            this.applyToModelB.setForeground(Color.red);
            this.applyToModelB.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if(JOptionPane.showConfirmDialog(ParameterSetting.this, "Transfer current configuration to the experiment model?", "Confirmation request", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) ParameterSetting.this.applyToModel();
                }
            });
            jp.add(this.applyToModelB);
        }
        /*
        	jp.add(residualI2tPMLabel = new JLabel());
        */
        setupJp.add(jp, "North");
        jp = new JPanel();
        jp.setLayout(new GridLayout(1, 4));
        JPanel jp1 = new JPanel();
        jp1.setBorder(new TitledBorder("Poloidal"));
        jp1.setLayout(new GridLayout(6, 1));
        this.buttons[1] = this.poloidalControlB = new JButton("EDA1");
        this.poloidalControlB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("EDA1 Setup open");
                final int nid = ParameterSetting.this.poloidalControlRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[1] == null)
                {
                    ParameterSetting.this.devices[1] = device = new RFXPolControlSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    // if (ParameterSetting.this.readOnly)
                    device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(1);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(1, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[1] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[1] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        jp1.add(this.poloidalControlB);
        this.buttons[2] = this.axiSetupB = new JButton("Axi Setup");
        this.axiSetupB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("Axi Setup open");
                final int nid = ParameterSetting.this.axiSetupRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[2] == null)
                {
                    ParameterSetting.this.devices[2] = device = new RFXAxiControlSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(2);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(2, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[2] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[2] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        jp1.add(this.axiSetupB);
        this.buttons[3] = this.pcSetupB = new JButton("PC Setup");
        this.pcSetupB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("PC Setup open");
                final int nid = ParameterSetting.this.pcSetupRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[3] == null)
                {
                    // devices[3] = device = new RFXPCSetupSetup();
                    ParameterSetting.this.devices[3] = device = new RFXPC4SetupSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    final PrintButton printB = new PrintButton(3);
                    device.addButton(printB);
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(3, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[3] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[3] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        jp1.add(this.pcSetupB);
        this.buttons[4] = this.pmSetupB = new JButton("PM Setup");
        jp1.add(this.pmSetupB);
        this.pmSetupB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("PM Setup open");
                final int nid = ParameterSetting.this.pmSetupRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[4] == null)
                {
                    ParameterSetting.this.devices[4] = device = new RFXPMSetupSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(4);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(4, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[4] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[4] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        //////// New IP Control 2010
        this.buttons[15] = this.ipSetupB = new JButton("IP Setup");
        jp1.add(this.ipSetupB);
        this.ipSetupB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("IP Setup open");
                final int nid = ParameterSetting.this.ipSetupRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[4] == null)
                {
                    ParameterSetting.this.devices[15] = device = new IPControlSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(15);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(15, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[15] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[15] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        ////////////////////////
        jp.add(jp1);
        jp1 = new JPanel();
        jp1.setBorder(new TitledBorder("Toroidal"));
        jp1.setLayout(new GridLayout(6, 1));
        this.buttons[5] = this.toroidalControlB = new JButton("EDA3");
        this.toroidalControlB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("EDA3 Setup open");
                final int nid = ParameterSetting.this.toroidalControlRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[5] == null)
                {
                    ParameterSetting.this.devices[5] = device = new RFXTorControlSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(5);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(5, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[5] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[5] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        jp1.add(this.toroidalControlB);
        this.buttons[6] = this.chopperSetupB = new JButton("Chopper Setup");
        this.chopperSetupB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("Chopper Setup open");
                final int nid = ParameterSetting.this.chopperSetupRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[6] == null)
                {
                    ParameterSetting.this.devices[6] = device = new RFXChopperSetupSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(6);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(6, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[6] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[6] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        jp1.add(this.chopperSetupB);
        this.buttons[7] = this.ffSetupB = new JButton("FeedForward Setup");
        this.ffSetupB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("Feedforward Setup open");
                final int nid = ParameterSetting.this.ffSetupRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[7] == null)
                {
                    ParameterSetting.this.devices[7] = device = new RFXFeedforwardSetupSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(7);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(7, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[7] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[7] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        jp1.add(this.ffSetupB);
        this.buttons[8] = this.inverterSetupB = new JButton("Inverter Setup");
        this.inverterSetupB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("Inverter Setup open");
                final int nid = ParameterSetting.this.inverterSetupRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[8] == null)
                {
                    ParameterSetting.this.devices[8] = device = new RFXInverterSetupSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(8);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(8, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[8] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[8] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        jp1.add(this.inverterSetupB);
        this.buttons[9] = this.tfSetupB = new JButton("TF Setup");
        this.tfSetupB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("TF Setup open");
                final int nid = ParameterSetting.this.tfSetupRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[9] == null)
                {
                    ParameterSetting.this.devices[9] = device = new RFXTFSetupSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(9);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(9, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[9] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[9] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        jp1.add(this.tfSetupB);
        this.buttons[10] = this.bfControlB = new JButton("B&F Control");
        this.bfControlB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("B&F Setup open");
                final int nid = ParameterSetting.this.bfControlRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[10] == null)
                {
                    ParameterSetting.this.devices[10] = device = new RFXAxiToroidalControlSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(10);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(10, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[10] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[10] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        jp1.add(this.bfControlB);
        jp.add(jp1);
        jp1 = new JPanel();
        jp1.setBorder(new TitledBorder("MHD"));
        jp1.setLayout(new GridLayout(6, 1));
        this.buttons[11] = this.mhdControlB = new JButton("MHD Control");
        this.mhdControlB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("MHD Control Setup open");
                final int nid = ParameterSetting.this.mhdControlRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[11] == null)
                {
                    // devices[11] = device = new RFXMHDSetup();
                    ParameterSetting.this.devices[11] = device = new MARTE_MHD_CTRLSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(11);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(11, updated);
                            /*                            //Copy the same configuration to MHD BC
                            //devices[11].apply( mhdControlRoot.getValue());
                            
                            //GABRIELE OTTOBRE 2008
                            //Faccio Apply solo se isChanged true
                            if(justApplied)
                                devices[11].apply(mhdBcNid.getValue());
                            else
                            {
                                //Il Reset e' necessario nel caso siano state cambiati dei campi senza apply
                                devices[11].reset();
                                devices[11].apply(mhdBcNid.getValue());
                            }
                            
                            /////////////////////////////////////////
                            
                            
                            
                            //Copy PAR303_VAL (measure radius), PAR304_VAL (MoNo sine excluded)
                            //and PAR305_VAL (N limits for sideband correction) into MHD_BR
                            copyData("\\MHD_AC::CONTROL.PARAMETERS:PAR303_VAL", "\\MHD_BR::CONTROL.PARAMETERS:PAR303_VAL");
                            copyData("\\MHD_AC::CONTROL.PARAMETERS:PAR304_VAL", "\\MHD_BR::CONTROL.PARAMETERS:PAR304_VAL");
                            copyData("\\MHD_AC::CONTROL.PARAMETERS:PAR305_VAL", "\\MHD_BR::CONTROL.PARAMETERS:PAR305_VAL");
                             */
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[11] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[11] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        jp1.add(this.mhdControlB);
        final JPanel jp2 = new JPanel();
        jp2.add(new JLabel("MHD FF:"));
        jp2.add(this.currFFC = new JComboBox(new String[]{"DISABLED", "ENABLED"}));
        jp1.add(jp2);
        if(isRt) this.currFFC.setEnabled(false);
        else{
            this.currFFC.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    ParameterSetting.this.setCurrFFState();
                }
            });
        }
        jp.add(jp1);
        jp1 = new JPanel();
        jp1.setBorder(new TitledBorder("Vessel"));
        jp1.setLayout(new GridLayout(6, 1));
        this.buttons[12] = this.viSetupB = new JButton("VI Setup");
        this.viSetupB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("VI Setup open");
                final int nid = ParameterSetting.this.viSetupRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[12] == null)
                {
                    // devices[12] = device = new RFXVISetupSetup();
                    ParameterSetting.this.devices[12] = device = new RFXVICONTROLSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(12);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(12, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[12] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[12] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        jp1.add(this.viSetupB);
        this.buttons[13] = this.viSetupB = new JButton("Pellet Setup");
        this.viSetupB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("Pellet Setup open");
                final int nid = ParameterSetting.this.pelletSetupRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[12] == null)
                {
                    ParameterSetting.this.devices[13] = device = new PELLETSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(13);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(13, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[13] == ParameterSetting.UNCHECKED){
                    ParameterSetting.this.states[13] = ParameterSetting.CHECKING;
                    if(ParameterSetting.this.modifiedNids != null && ParameterSetting.this.modifiedNids.length > 0) device.setHighlight(true, ParameterSetting.this.modifiedNids);
                }
            }
        });
        jp1.add(this.viSetupB);
        jp.add(jp1);
        setupJp.add(jp, "Center");
        if(!isRt){
            this.getContentPane().add(setupJp, "Center");
            this.chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            this.chooser.setFileFilter(new RfxFileFilter());
            return;
        }
        // Other Stuff only for RT
        final JPanel configJp = new JPanel();
        configJp.setLayout(new GridLayout(1, 2));
        jp = new JPanel();
        jp.setLayout(new GridLayout(4, 1));
        this.buttons[16] = this.mopB = new JButton("MOP");
        this.mopB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("MOP Setup open");
                final int nid = ParameterSetting.this.mopRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[13] == null)
                {
                    ParameterSetting.this.devices[16] = device = new RFXMOPSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(16);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(16, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[16] == ParameterSetting.UNCHECKED) ParameterSetting.this.states[16] = ParameterSetting.CHECKING;
            }
        });
        jp.add(this.mopB);
        this.buttons[17] = this.ansaldoConfigB = new JButton("Ansaldo Config");
        this.ansaldoConfigB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("Ansaldo Setup open");
                final int nid = ParameterSetting.this.ansaldoConfigRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[14] == null)
                {
                    ParameterSetting.this.devices[17] = device = new RFXANSALDOSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(17);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(17, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[17] == ParameterSetting.UNCHECKED) ParameterSetting.this.states[17] = ParameterSetting.CHECKING;
            }
        });
        jp.add(this.ansaldoConfigB);
        this.buttons[18] = this.unitsConfigB = new JButton("Units Config");
        this.unitsConfigB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("Units Setup open");
                final int nid = ParameterSetting.this.unitsConfigRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[15] == null)
                {
                    ParameterSetting.this.devices[18] = device = new RFXABUnitsSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(18);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(18, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[18] == ParameterSetting.UNCHECKED) ParameterSetting.this.states[18] = ParameterSetting.CHECKING;
            }
        });
        jp.add(this.unitsConfigB);
        configJp.add(jp);
        jp = new JPanel();
        jp.setLayout(new GridLayout(4, 1));
        this.buttons[19] = this.poloidalConfigB = new JButton("Poloidal Config");
        this.poloidalConfigB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("Poloidal Config Setup open");
                final int nid = ParameterSetting.this.poloidalConfigRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[16] == null)
                {
                    ParameterSetting.this.devices[19] = device = new RFXPoloidalSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(19);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(19, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[19] == ParameterSetting.UNCHECKED) ParameterSetting.this.states[19] = ParameterSetting.CHECKING;
            }
        });
        jp.add(this.poloidalConfigB);
        this.buttons[20] = this.toroidalConfigB = new JButton("Toroidal Config");
        this.toroidalConfigB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("Toroidal config Setup open");
                final int nid = ParameterSetting.this.toroidalConfigRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[17] == null)
                {
                    ParameterSetting.this.devices[20] = device = new RFXToroidalSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(20);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(20, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[20] == ParameterSetting.UNCHECKED) ParameterSetting.this.states[20] = ParameterSetting.CHECKING;
            }
        });
        jp.add(this.toroidalConfigB);
        this.buttons[21] = this.mhdConfigB = new JButton("MHD Config");
        this.mhdConfigB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("MHD Config Setup open");
                final int nid = ParameterSetting.this.mhdConfigRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[18] == null)
                {
                    ParameterSetting.this.devices[21] = device = new RFXPRConfigSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(21);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(21, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[21] == ParameterSetting.UNCHECKED) ParameterSetting.this.states[21] = ParameterSetting.CHECKING;
            }
        });
        jp.add(this.mhdConfigB);
        // buttons[22] = viConfigB = new JButton("Vi Config"); Taliercio 10 - 01 - 2011
        this.buttons[22] = this.viConfigB = new JButton("V Config");
        this.viConfigB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.log("VI Config Setup open");
                final int nid = ParameterSetting.this.viConfigRoot.getValue();
                DeviceSetup device = DeviceSetup.getDevice(nid);
                if(device == null)
                // if (devices[19] == null)
                {
                    /*
                    devices[22] = device = new RFXVIConfigSetup(); Taliercio 10 - 1 - 2011
                    */
                    ParameterSetting.this.devices[22] = device = new RFXVConfigSetup();
                    device.configure(ParameterSetting.this.rfx, nid);
                    if(ParameterSetting.this.readOnly) device.setReadOnly(true);
                    final PrintButton printB = new PrintButton(22);
                    device.addButton(printB);
                    if(ParameterSetting.this.isRt) device.setCancelText("Acknowledge");
                    device.pack();
                    // device.setLocation(getMousePosition());
                    device.setVisible(true);
                    device.addDeviceCloseListener(new DeviceCloseListener(){
                        @Override
                        public void deviceClosed(final boolean updated, final boolean justApplied) {
                            ParameterSetting.this.handleDeviceClosed(22, updated);
                        }
                    });
                }else device.setVisible(true);
                if(ParameterSetting.this.states[22] == ParameterSetting.UNCHECKED) ParameterSetting.this.states[22] = ParameterSetting.CHECKING;
            }
        });
        jp.add(this.viConfigB);
        configJp.add(jp);
        final JPanel limitsJp = new JPanel();
        limitsJp.setLayout(new BorderLayout());
        final JPanel limitsListJp = new JPanel();
        limitsListJp.setLayout(new GridLayout(18, 1));
        jp = new JPanel();
        jp.add(new JLabel("Corrente Max. PMAT per unita' (A): "));
        jp.add(this.maxPMATF = new JTextField("" + this.maxPMAT, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Tensione Max. PCAT in serie: "));
        jp.add(this.maxPCATSeriesF = new JTextField("" + this.maxPCATSeries, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Tensione Max. PCAT in Parallelo: "));
        jp.add(this.maxPCATParallelF = new JTextField("" + this.maxPCATParallel, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Corrente Max. TFAT per unita' (A): "));
        jp.add(this.maxTFATF = new JTextField("" + this.maxTFAT, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Tensione Max. Chopper toroidale TCCH (V): "));
        jp.add(this.maxTCCHF = new JTextField("" + this.maxTCCH, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Corrente Max. Inverter toroidale TCAC (A): "));
        jp.add(this.maxTCACF = new JTextField("" + this.maxTCAC, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Tensione di picco avvolgimento magnetizzante: "));
        jp.add(this.maxPMVoltageF = new JTextField("" + this.maxPMVoltage, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Temperatura Max. avvolgimento magnetizzante (C): "));
        jp.add(this.maxTempMagnetizingF = new JTextField("" + this.maxTempMagnetizing, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Limite del tempo diaccensione di PR (ms): "));
        jp.add(this.maxPrTimeF = new JTextField("" + this.maxPrTime, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Limite corrente bobine a sella a vuoto (A): "));
        jp.add(this.maxCurrSellaVF = new JTextField("" + this.maxCurrSellaV, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Limite corrente bobine a sella a vuoto, altri avvolgimenti I=0 (A): "));
        jp.add(this.maxCurrSellaVI0F = new JTextField("" + this.maxCurrSellaVI0, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Limite corrente bobine a sella con plasma (A): "));
        jp.add(this.maxCurrSellaPF = new JTextField("" + this.maxCurrSellaP, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Temperatura Max. Bobine a sella (C): "));
        jp.add(this.maxTempSaddleF = new JTextField("" + this.maxTempSaddle, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Temperatura Max. Camera (C): "));
        jp.add(this.maxTempRoomF = new JTextField("" + this.maxTempRoom, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("POhm. Max (MW): "));
        jp.add(this.maxPOhmF = new JTextField("" + this.maxPOhm, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Temperatura Max. bobine toroidali (C): "));
        jp.add(this.maxTempTorF = new JTextField("" + this.maxTempTor, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Limite I2T avvolgimento Toroidale (MA2s): "));
        jp.add(this.maxI2TF = new JTextField("" + this.maxI2T, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Tensione Max. Valvole Filling e Puffing (V): "));
        jp.add(this.maxFillPuffVoltageF = new JTextField("" + this.maxFillPuffVoltage, 10));
        limitsListJp.add(jp);
        /*
        jp = new JPanel();
        jp.add(new JLabel("Tensione Max. Valvole Filling (V): "));
        jp.add(maxFillVoltageF = new JTextField("" + maxFillVoltage, 10));
        limitsListJp.add(jp);
        jp = new JPanel();
        jp.add(new JLabel("Tensione Max. Valvole Puffing (V): "));
        jp.add(maxPuffVoltageF = new JTextField("" + maxPuffVoltage, 10));
        limitsListJp.add(jp);
        */
        this.getLimits();
        limitsJp.add(limitsListJp, "Center");
        jp = new JPanel();
        final JButton enterB = new JButton("Enter");
        enterB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.saveLimits();
            }
        });
        jp.add(enterB);
        limitsJp.add(jp, "South");
        final JPanel messageJp = new JPanel();
        messageJp.setLayout(new BorderLayout());
        jp = new JPanel();
        final JLabel messagesTitle = new JLabel("Messaggi per il Responsabile di Turno");
        messagesTitle.setFont(new Font("Serif", Font.BOLD, 30));
        jp.add(messagesTitle);
        messageJp.add(jp, "North");
        this.messageArea = new JTextArea(10, 40);
        this.getRtMessages();
        messageJp.add(this.messageArea, "Center");
        jp = new JPanel();
        final JButton saveB = new JButton("Save");
        saveB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ParameterSetting.this.saveRtMessages();
            }
        });
        jp.add(saveB);
        messageJp.add(jp, "South");
        this.tabbedP = new JTabbedPane();
        this.tabbedP.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(final ChangeEvent e) {
                if(ParameterSetting.this.tabbedP.getSelectedIndex() == 2) // If showing linits
                    ParameterSetting.this.getLimits();
            }
        });
        this.tabbedP.add(setupJp, "Setup");
        this.tabbedP.add(configJp, "Config");
        this.tabbedP.add(limitsJp, "Limits");
        this.tabbedP.add(messageJp, "Messages");
        this.getContentPane().add(this.tabbedP, "Center");
    }

    ParameterSetting(final String rtIp){
        this(false, false, rtIp);
    }

    ParameterSetting(final String rtIp, final String online){
        this(false, online.equals("online"), rtIp);
    }

    boolean allChecked() {
        for(final int state2 : this.states)
            if(state2 != ParameterSetting.CHECKED) return false;
        return true;
    }

    void applyCurrFFToModel(final boolean ffOn) {
        try{
            this.rfx.close();
            this.rfx = new Database("rfx", -1);
            this.rfx.open();
            final Nid ffNid1 = this.rfx.resolve(new Path("\\MHD_AC::CURR_FF"));
            final Nid ffNid2 = this.rfx.resolve(new Path("\\MHD_BC::CURR_FF"));
            this.rfx.setOn(ffNid1, ffOn);
            this.rfx.setOn(ffNid2, ffOn);
            this.rfx.close();
            this.rfx = new Database("rfx", this.shot);
            this.rfx.open();
        }catch(final Exception exc){
            System.out.println("Cannot set state of currFF in model");
            return;
        }
    }

    // Apply only state: used for PTSO
    void applyOnSetup(final int idx, final int nidOffsets[], final Hashtable setupOnHash) {
        try{
            for(final int nidOffset : nidOffsets){
                final Nid currNid = new Nid(this.nids[idx].getValue() + nidOffset);
                final String fullPath = this.rfx.getInfo(currNid).getFullPath();
                final Boolean isOn = (Boolean)setupOnHash.get(fullPath);
                if(isOn != null){
                    this.rfx.setOn(currNid, isOn.booleanValue());
                }
            }
        }catch(final Exception exc1){
            System.err.println("Error applying setup for nid array: " + exc1);
        }
    }

    void applySetup(final Hashtable setupHash, final Hashtable setupOnHash) {
        Enumeration pathNames = setupHash.keys();
        String currPath = "", currDecompiled = "", savedDecompiled = "";
        while(pathNames.hasMoreElements()){
            try{
                currPath = (String)pathNames.nextElement();
                System.out.println("APPLY SETUP: " + currPath);
                final Nid currNid = new Path(currPath).toNid();
                currDecompiled = (String)setupHash.get(currPath);
                try{
                    savedDecompiled = (this.rfx.getData(currNid)).toString();
                }catch(final Exception exc){
                    savedDecompiled = "";
                }
                if(!currDecompiled.equals(savedDecompiled)){
                    final Descriptor currData = this.rfx.tdiCompile(currDecompiled);
                    this.rfx.putData(currNid, currData);
                }
            }catch(final Exception exc){
                System.err.println("Error applying configuration: " + exc + currPath + "  " + currDecompiled + "  " + savedDecompiled);
            }
        }
        pathNames = setupOnHash.keys();
        while(pathNames.hasMoreElements()){
            try{
                currPath = (String)pathNames.nextElement();
                final Nid currNid = this.rfx.resolve(new Path(currPath));
                final Boolean currBool = (Boolean)setupOnHash.get(currPath);
                this.rfx.setOn(currNid, currBool.booleanValue());
            }catch(final Exception exc){
                System.err.println("Error applying configuration: " + exc + currPath + "  " + currDecompiled + "  " + savedDecompiled);
            }
        }
    }

    void applySetup(final Hashtable setupHash, final Hashtable setupOnHash, final boolean[] deviceSelect, final boolean[] timeSelect) {
        Enumeration pathNames = setupHash.keys();
        String currPath = "", currDecompiled = "", savedDecompiled = "";
        while(pathNames.hasMoreElements()){
            try{
                currPath = (String)pathNames.nextElement();
                final Integer idxObj = (Integer)this.mapSetupHash.get(currPath);
                if(idxObj == null) continue;
                final int idx = idxObj.intValue();
                if(idx == 0 || idx >= ParameterSetting.NUM_SETUP || !deviceSelect[idx - 1]) continue;
                final Nid currNid = this.rfx.resolve(new Path(currPath));
                currDecompiled = (String)setupHash.get(currPath);
                try{
                    savedDecompiled = (this.rfx.getData(currNid)).toString();
                }catch(final Exception exc){
                    savedDecompiled = "";
                }
                if(!currDecompiled.equals(savedDecompiled)){
                    final Descriptor currData = Database.tdiCompile(currDecompiled);
                    this.rfx.putData(currNid, currData);
                }
            }catch(final Exception exc){
                System.err.println("Error applying configuration: " + exc + currPath + "  " + currDecompiled + "  " + savedDecompiled);
            }
        }
        pathNames = setupOnHash.keys();
        while(pathNames.hasMoreElements()){
            try{
                currPath = (String)pathNames.nextElement();
                final Integer idxObj = (Integer)this.mapSetupHash.get(currPath);
                if(idxObj == null) continue;
                final int idx = idxObj.intValue();
                if(idx == 0 || idx >= ParameterSetting.NUM_SETUP || !deviceSelect[idx - 1]) continue;
                final Nid currNid = this.rfx.resolve(new Path(currPath));
                final Boolean currBool = (Boolean)setupOnHash.get(currPath);
                this.rfx.setOn(currNid, currBool.booleanValue());
            }catch(final Exception exc){
                System.err.println("Error applying configuration: " + exc + currPath + "  " + currDecompiled + "  " + savedDecompiled);
            }
        }
        // Timing components
        if(timeSelect[0]) this.applySetup(0, this.pm_mask, setupHash, setupOnHash);
        if(timeSelect[1]) this.applySetup(0, this.pc_mask, setupHash, setupOnHash);
        if(timeSelect[2]) this.applySetup(0, this.pv_mask, setupHash, setupOnHash);
        if(timeSelect[3]) this.applySetup(0, this.pp_mask, setupHash, setupOnHash);
        if(timeSelect[4]) this.applySetup(0, this.pr_mask, setupHash, setupOnHash);
        // if (timeSelect[5]) applySetup(0, ptso_mask, setupHash, setupOnHash);
        if(timeSelect[5]) this.applyOnSetup(0, this.ptso_mask, setupOnHash);
        if(timeSelect[6]) this.applySetup(0, this.ptcb_mask, setupHash, setupOnHash);
        if(timeSelect[7]) this.applySetup(0, this.ptct_mask, setupHash, setupOnHash);
        if(timeSelect[8]) this.applySetup(0, this.gas_mask, setupHash, setupOnHash);
        if(timeSelect[9]) this.applySetup(0, this.tf_mask, setupHash, setupOnHash);
        if(timeSelect[10]) this.applySetup(0, this.is_mask, setupHash, setupOnHash);
        if(timeSelect[11]) this.applySetup(0, this.chopper_mask, setupHash, setupOnHash);
        if(timeSelect[12]) this.applySetup(0, this.inverter_mask, setupHash, setupOnHash);
    }

    void applySetup(final int idx, final int nidOffsets[], final Hashtable setupHash, final Hashtable setupOnHash) {
        try{
            for(final int nidOffset : nidOffsets){
                final Nid currNid = new Nid(this.nids[idx].getValue() + nidOffset);
                final String fullPath = this.rfx.getInfo(currNid).getFullPath();
                final String currDecompiled = (String)setupHash.get(fullPath);
                if(currDecompiled != null){
                    final Descriptor currData = Database.tdiCompile(currDecompiled);
                    this.rfx.putData(currNid, currData);
                }
                final Boolean isOn = (Boolean)setupOnHash.get(fullPath);
                if(isOn != null){
                    this.rfx.setOn(currNid, isOn.booleanValue());
                }
            }
        }catch(final Exception exc1){
            System.err.println("Error applying setup for nid array: " + exc1);
        }
    }

    void applyTimes() {
        final int nid = this.timesRoot.getValue();
        final DeviceSetup device = new RFXTimesSetup();
        device.configure(this.rfx, nid);
        device.check();
        if(ParameterSetting.this.readOnly) device.setReadOnly(true);
        final PrintButton printB = new PrintButton(0);
        device.addButton(printB);
        device.resetNidHash();
    }

    void applyToModel() {
        final Hashtable applyHash = new Hashtable();
        final Hashtable applyOnHash = new Hashtable();
        final boolean allSelectedDevices[] = {false, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        final boolean allSelectedTimes[] = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        this.getSetupForModel(applyHash, applyOnHash, allSelectedDevices, allSelectedTimes);
        this.applyToModel(applyHash, applyOnHash);
    }

    void applyToModel(final Hashtable applyHash, final Hashtable applyOnHash) {
        try{
            this.rfx.close();
            this.rfx = new Database("RFX", -1);
            this.rfx.open();
        }catch(final Exception exc){
            System.err.println("Cannot open model");
            return;
        }
        this.saveSetup(this.modelSetupHash, this.modelSetupOnHash);
        this.revertModelItem.setEnabled(true);
        final Hashtable modifiedSetupHash = new Hashtable();
        final Hashtable modifiedSetupOnHash = new Hashtable();
        final boolean[] changed = this.compareSetup(applyHash, applyOnHash, this.modelSetupHash, this.modelSetupOnHash, modifiedSetupHash, modifiedSetupOnHash);
        if(this.isOnline){
            for(int i = 0; i < ParameterSetting.NUM_SETUP; i++)
                if(changed[i]) this.setUncheckedRt(i);
        }
        this.notifyChangedRt(modifiedSetupHash, modifiedSetupOnHash);
        this.applySetup(modifiedSetupHash, modifiedSetupOnHash);
        this.applyTimes();
        this.applyCurrFFToModel(this.currFFC.getSelectedIndex() == 1);
        this.notifyApplySetupFinishedRt();
        // Write Ref. Shot
        try{
            final String refShotStr = this.refShotLabel.getText().substring(ParameterSetting.refShotLabelText.length());
            final Nid refShotNid = this.rfx.resolve(new Path("\\INFO:REF_SHOT"));
            final int refShot = Integer.parseInt(refShotStr);
            this.rfx.putData(refShotNid, new Int32(refShot));
        }catch(final Exception exc){
            System.out.println("Cannot write Ref. Shot");
        }
        try{
            this.rfx.close();
            this.rfx = new Database("RFX", this.shot);
            this.rfx.open();
        }catch(final Exception exc){
            System.err.println("Cannot open working shot");
            return;
        }
        this.copyDecoupling(100, -1);
        // copyMhdBr(100, -1);
    }

    Nid checkDeviceConfig(final Nid deviceRoot, final Hashtable configHash, final Hashtable configOnHash) {
        try{
            final Nid baseNid = this.rfx.getDefault();
            this.rfx.setDefault(deviceRoot);
            Nid[] deviceNids = this.rfx.getWild(NodeInfo.USAGE_NUMERIC);
            this.rfx.setDefault(baseNid);
            if(deviceNids != null){
                for(final Nid deviceNid : deviceNids){
                    String currDec, savedDec;
                    try{
                        currDec = (this.rfx.getData(deviceNid)).toString();
                    }catch(final Exception exc){
                        currDec = null;
                    }
                    savedDec = (String)configHash.get(this.rfx.getInfo(deviceNid).getFullPath());
                    if((savedDec == null && currDec != null) || (savedDec != null && currDec == null) || ((savedDec != null && currDec != null) && !savedDec.equals(currDec))) return deviceNid;
                    final boolean on = this.rfx.isOn(deviceNid);
                    final boolean savedOn = ((Boolean)configOnHash.get(this.rfx.getInfo(deviceNid).getFullPath())).booleanValue();
                    if(on != savedOn) return deviceNid;
                }
            }
            this.rfx.setDefault(deviceRoot);
            deviceNids = this.rfx.getWild(NodeInfo.USAGE_TEXT);
            this.rfx.setDefault(baseNid);
            if(deviceNids != null){
                for(final Nid deviceNid : deviceNids){
                    String currDec, savedDec;
                    try{
                        currDec = (this.rfx.getData(deviceNid)).toString();
                    }catch(final Exception exc){
                        currDec = null;
                    }
                    savedDec = (String)configHash.get(this.rfx.getInfo(deviceNid).getFullPath());
                    if((savedDec == null && currDec != null) || (savedDec != null && currDec == null) || ((savedDec != null && currDec != null) && !savedDec.equals(currDec))) return deviceNid;
                    final boolean on = this.rfx.isOn(deviceNid);
                    final boolean savedOn = ((Boolean)configOnHash.get(this.rfx.getInfo(deviceNid).getFullPath())).booleanValue();
                    if(on != savedOn) return deviceNid;
                }
            }
            return null;
        }catch(final Exception exc){
            System.err.println("Error comparing configurations: " + exc);
            return null;
        }
    }

    String checkLimits() {
        try{
            final float[] pmWave = (this.rfx.tdiEvaluate(new Path("\\PM_SETUP:WAVE"))).toFloat();
            float maxCurr = 0;
            for(final float element : pmWave){
                if(maxCurr < element) maxCurr = element;
            }
            if(maxCurr > this.maxPMAT){ return "Corrente Magnetizzante sopra i limiti"; }
            final int numPMUnits = this.countPMUnits();
            final float rTransfer = (this.rfx.tdiEvaluate(new Path("\\P_CONFIG:LOAD_RESIST"))).toFloat()[0];
            if(maxCurr * numPMUnits * rTransfer > this.maxPMVoltage) return "Tensione di picco avvolgimento magnetizzante sopra i limiti";
        }catch(final Exception exc){
            System.err.println("Cannot read max PMAT: " + exc);
        }
        try{
            final float[] pmWave = (this.rfx.tdiEvaluate(new Path("\\TF_SETUP:WAVE"))).toFloat();
            float maxCurr = 0;
            for(final float element : pmWave){
                if(maxCurr < element) maxCurr = element;
            }
            if(maxCurr > this.maxTFAT){ return "Corrente TFAT sopra i limiti"; }
        }catch(final Exception exc){
            System.err.println("Cannot read max TFAT: " + exc);
        }
        try{
            final float[] pmWave = (this.rfx.tdiEvaluate(new Path("\\CHOPPER_SETUP:WAVE"))).toFloat();
            float maxCurr = 0;
            for(final float element : pmWave){
                if(maxCurr < element) maxCurr = element;
            }
            if(maxCurr > this.maxTCCH){ return "Tensione Chopper sopra i limiti"; }
        }catch(final Exception exc){
            System.err.println("Cannot read max Chopper voltage: " + exc);
        }
        try{
            for(int waveIdx = 1; waveIdx <= 12; waveIdx++){
                final float[] pmWave = (this.rfx.tdiEvaluate(new Path("\\INVERTER_SETUP.CHANNEL_" + waveIdx + ":WAVE"))).toFloat();
                float maxCurr = 0;
                for(final float element : pmWave){
                    if(maxCurr > element) maxCurr = element;
                }
                if(Math.abs(maxCurr) > Math.abs(this.maxTCAC)){ return "Corrente Inverter toroidale sopra i limiti"; }
            }
        }catch(final Exception exc){
            System.err.println("Cannot read max Inverter current: " + exc);
        }
        try{
            final String pcConfig = ((CString)this.rfx.tdiEvaluate(new Path("\\PC_SETUP:CONFIG"))).getValue();
            float maxVolt = 0;
            // float[] pcWave = (rfx.evaluateData(new Path("\\PC_SETUP:WAVE"))).toFloat();
            final float[] pcWave = (this.rfx.tdiEvaluate(new Path("\\RFX::PC_SETUP.WAVE_1:WAVE"))).toFloat();
            for(final float element : pcWave){
                if(maxVolt < element) maxVolt = element;
            }
            if(pcConfig.trim().toUpperCase().equals("PARALLEL")){
                if(Math.abs(maxVolt) > Math.abs(this.maxPCATParallel)){ return "Tensione PCAT in configurazione parallela sopra i limiti"; }
            }else{
                if(Math.abs(maxVolt) > Math.abs(this.maxPCATSeries)){ return "Tensione PCAT in configurazione serie sopra i limiti"; }
            }
        }catch(final Exception exc){
            System.err.println("Cannot read max PCAT voltage: " + exc);
        }
        try{
            final float[] currWave = (this.rfx.tdiEvaluate(new Path("\\VI_SETUP:FILL_WAVE"))).toFloat();
            float maxVolt = 0;
            for(final float element : currWave){
                if(maxVolt < element) maxVolt = element;
            }
            // if (maxVolt > maxFillVoltage)
            if(maxVolt > this.maxFillPuffVoltage){ return "Tensione Valvole Filling (He/impurities injection) sopra i limiti"; }
        }catch(final Exception exc){
            System.err.println("Cannot read max Filling (He/impurities injection) voltage: " + exc);
        }
        try{
            final float[] currWave = (this.rfx.tdiEvaluate(new Path("\\VI_SETUP:PUFF_WAVE"))).toFloat();
            float maxVolt = 0;
            for(final float element : currWave){
                if(maxVolt < element) maxVolt = element;
            }
            // if (maxVolt > maxPuffVoltage)
            if(maxVolt > this.maxFillPuffVoltage){ return "Tensione Valvole Puffing (H2 injection) sopra i limiti"; }
        }catch(final Exception exc){
            System.err.println("Cannot read max Puffing (H2 injection) voltage: " + exc);
        }
        try{
            final float startPR = (this.rfx.tdiEvaluate(new Path("\\RFX::T_START_PR"))).toFloat()[0];
            final float stopPR = (this.rfx.tdiEvaluate(new Path("\\RFX::T_STOP_PR"))).toFloat()[0];
            if((stopPR - startPR) > this.maxPrTime / 1000.){ return "Durata di accensione di PR oltre i limiti"; }
        }catch(final Exception exc){
            System.err.println("Cannot read PR timing : " + exc);
        }
        try{
            final int controlType = (this.rfx.tdiEvaluate(new Path("\\RFX::IP_CONTROL:TYPE"))).toInt()[0];
            if(controlType > 1){
                final float phomMax = (this.rfx.tdiEvaluate(new Path("\\RFX::IP_CONTROL.RFP:POHMMAX"))).toFloat()[0];
                if(phomMax > this.maxPOhm * 1e6){ return "Valore di Pohm Max definita in IP_CONTROL oltre i limiti"; }
            }
        }catch(final Exception exc){
            System.err.println("Cannot read Pohm Max : " + exc);
        }
        try{
            final float i2tTF = (this.rfx.tdiEvaluate("computeTF_i2t()")).toFloat()[0];
            if(i2tTF > this.maxI2T * 1e6){ return "Valore di I2T su avvolgimento toroidale oltre i limiti"; }
        }catch(final Exception exc){
            System.err.println("Cannot compute I2T for toroidal coils : " + exc);
        }
        return null;
    }

    boolean checkVersions() {
        /*            if(!checkVersionVme("\\MHD_AC::CONTROL:VERSION", "\\VERSIONS:VME_MHD_AC", "MHD_AC", true))
                return false;
            if(!checkVersionVme("\\MHD_BC::CONTROL:VERSION", "\\VERSIONS:VME_MHD_BC", "MHD_BC", true))
                return false;
            if(!checkVersionVme("\\MHD_BR::CONTROL:VERSION", "\\VERSIONS:VME_MHD_BR", "MHD_BR", true))
                return false;
            if(!checkVersionVme("\\EDA1::CONTROL:VERSION", "\\VERSIONS:VME_EDA1", "EDA1", true))
                return false;
            if(!checkVersionVme("\\EDA3::CONTROL:VERSION", "\\VERSIONS:VME_EDA3", "EDA3", true))
                return false;
            if(!checkVersionVme("\\DEQU_RAW::CONTROL:VERSION", "\\VERSIONS:VME_DEQU", "DEQU", true))
                return false;
            if(!checkVersionVme("\\DFLU_RAW::CONTROL:VERSION", "\\VERSIONS:VME_DFLU", "DFLU", true))
                return false;
        */ return true;
    }

    String checkVersionsForPas() {
        /*            if(!checkVersionVme("\\MHD_AC::CONTROL:VERSION", "\\VERSIONS:VME_MHD_AC", "MHD_AC", false))
                return "Incompatible major version number for MHD_AC";
            if(!checkVersionVme("\\MHD_BC::CONTROL:VERSION", "\\VERSIONS:VME_MHD_BC", "MHD_BC", false))
                return "Incompatible major version number for MHD_BC";
            if(!checkVersionVme("\\MHD_BR::CONTROL:VERSION", "\\VERSIONS:VME_MHD_BR", "MHD_BR", false))
                 return "Incompatible major version number for MHD_BR";
           if(!checkVersionVme("\\EDA1::CONTROL:VERSION", "\\VERSIONS:VME_EDA1", "EDA1", false))
                 return "Incompatible major version number for EDA1";
           if(!checkVersionVme("\\EDA3::CONTROL:VERSION", "\\VERSIONS:VME_EDA3", "EDA3", false))
                return "Incompatible major version number for EDA3";
            if(!checkVersionVme("\\DEQU_RAW::CONTROL:VERSION", "\\VERSIONS:VME_DEQU", "DEQU", false))
                return "Incompatible major version number for DEQU";
            if(!checkVersionVme("\\DFLU_RAW::CONTROL:VERSION", "\\VERSIONS:VME_DFLU", "DFLU", false))
                return "Incompatible major version number for DFLU";
        */ return null;
    }

    boolean checkVersionVme(final String currPath, final String configPath, final String name, final boolean displayWarning) {
        try{
            CString data;
            String currVersion, version;
            data = (CString)this.rfx.tdiEvaluate(new Path(currPath));
            currVersion = data.getValue();
            data = (CString)this.rfx.tdiEvaluate(new Path(configPath));
            version = data.getValue();
            final StringTokenizer st1 = new StringTokenizer(currVersion, ".");
            final StringTokenizer st2 = new StringTokenizer(version, ".");
            final String major1 = st1.nextToken();
            final String major2 = st2.nextToken();
            final String minor1 = st1.nextToken();
            final String minor2 = st2.nextToken();
            if(!major1.equals(major2)){
                if(displayWarning) JOptionPane.showMessageDialog(ParameterSetting.this, "Major version of " + currVersion + " is not compatible with major version of " + version + " for " + name + ": the loaded configuration cannot be used!!", "Error comparing versions", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if(!minor1.equals(minor2) && displayWarning){
                JOptionPane.showMessageDialog(ParameterSetting.this, "Minor version of " + currVersion + " is not compatible with minor version of " + version + " for " + name + ": the loaded configuration might be not fully compatible", "Error comparing versionse", JOptionPane.WARNING_MESSAGE);
            }
            return true;
        }catch(final Exception exc){
            /*  JOptionPane.showMessageDialog(ParameterSetting.this,
                      "Error reading version numbers of" + name,
                      "Error comparing versions",
                      JOptionPane.WARNING_MESSAGE);
              //return false;*/
            return true;
        }
    }

    boolean[] compareSetup(final Hashtable currSetupHash, final Hashtable currSetupOnHash, final Hashtable modelSetupHash, final Hashtable modelSetupOnHash, final Hashtable modifiedSetupHash, final Hashtable modifiedSetupOnHash) {
        final boolean changed[] = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
        final Enumeration pathNames = currSetupHash.keys();
        while(pathNames.hasMoreElements()){
            final String currPath = (String)pathNames.nextElement();
            String currDecompiled = (String)currSetupHash.get(currPath);
            String modelDecompiled = (String)modelSetupHash.get(currPath);
            if(currDecompiled == null) currDecompiled = "";
            if(modelDecompiled == null) modelDecompiled = "";
            if(!currDecompiled.equals(modelDecompiled)){
                try{
                    final int idx = ((Integer)(this.mapSetupHash.get(currPath))).intValue();
                    changed[idx] = true;
                    modifiedSetupHash.put(currPath, currDecompiled);
                    /*//////////////////////////////////GAB 2011 TACON DI URGENZA
                    			    if(currPath.startsWith("\\RFX::TOP.RFX.MHD.MHD_AC"))
                    			    {
                    				String newCurrPath = "\\RFX::TOP.RFX.MHD.MHD_BC"+currPath.substring(24);
                    				System.out.println("ZONTATO " + newCurrPath);
                            	modifiedSetupHash.put(newCurrPath, currDecompiled);
                     			    }
                    /////////////////////////////////////////////////*/
                }catch(final Exception exc){
                    System.err.println("Warning : mapping not found in compareSetup for " + currPath);
                    // System.exit(0);
                }
            }
            try{
                final boolean currOn = ((Boolean)currSetupOnHash.get(currPath)).booleanValue();
                final boolean modelOn = ((Boolean)modelSetupOnHash.get(currPath)).booleanValue();
                if(currOn != modelOn){
                    final int idx = ((Integer)(this.mapSetupHash.get(currPath))).intValue();
                    changed[idx] = true;
                    modifiedSetupOnHash.put(currPath, new Boolean(currOn));
                }
            }catch(final Exception exc){
                System.err.println("Warning : mapping not found in compareSetup for " + currPath);
                // System.exit(0);
            }
        }
        return changed;
    }

    void compareShots() {
        final String shotsStr = JOptionPane.showInputDialog(this, "Shot", "");
        try{
            final StringTokenizer st = new StringTokenizer(shotsStr, " ,");
            final int shot1 = Integer.parseInt(st.nextToken());
            this.log("Compare to shot " + shot1);
            this.compareShots(shot1);
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, "Incorrect shot numbers", "", JOptionPane.WARNING_MESSAGE);
        }
    }

    void compareShots(final int shot1) {
        final Hashtable setup1Hash = new Hashtable(), setup1OnHash = new Hashtable(), setup2Hash = new Hashtable(), setup2OnHash = new Hashtable(), diffSetupHash = new Hashtable(), diffSetupOnHash = new Hashtable();
        this.updateDeviceNids();
        this.saveSetupAndConfig(setup1Hash, setup1OnHash);
        try{
            this.rfx.close();
            this.rfx = new Database("RFX", shot1);
            this.rfx.open();
        }catch(final Exception exc){
            this.setCursor(this.prevCursor);
            JOptionPane.showMessageDialog(this, "Cannot open pulse " + shot1, "Error opening tree", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.updateDeviceNids();
        this.saveSetupAndConfig(setup2Hash, setup2OnHash);
        try{
            this.rfx.close();
            this.rfx = new Database("RFX", this.shot);
            this.rfx.open();
        }catch(final Exception exc){
            this.setCursor(this.prevCursor);
            System.err.println("Cannot open working shot");
            return;
        }
        this.updateDeviceNids();
        final boolean changed[] = this.compareSetup(setup1Hash, setup1OnHash, setup2Hash, setup2OnHash, diffSetupHash, diffSetupOnHash);
        int numChanged = 0;
        for(final boolean element : changed)
            if(element) numChanged++;
        if(numChanged > 0){
            this.reportDiffToModifiedNids(diffSetupHash, diffSetupOnHash);
            this.changedD = new JDialog(this, "Changed Devices");
            JPanel jp = new JPanel();
            jp.setLayout(new GridLayout(numChanged, 1));
            for(int i = 0; i < changed.length; i++){
                if(changed[i]){
                    final JPanel jp1 = new JPanel();
                    jp1.add(new JLabel(this.titles[i]));
                    jp1.add(new DiffButton(this.shot, i));
                    jp1.add(new DiffButton(shot1, i, false));
                    jp.add(jp1);
                }
            }
            this.changedD.getContentPane().add(jp, "Center");
            jp = new JPanel();
            final JButton cancelB = new JButton("Close");
            cancelB.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    ParameterSetting.this.changedD.dispose();
                }
            });
            jp.add(cancelB);
            this.changedD.getContentPane().add(jp, "South");
            this.changedD.pack();
            this.changedD.setVisible(true);
        }else // No changes
        {
            JOptionPane.showMessageDialog(this, "No differences found", "", JOptionPane.INFORMATION_MESSAGE);
        }
        this.setCursor(this.prevCursor);
    }

    void copyData(final String pathStr1, final String pathStr2) {
        try{
            final Path path1 = new Path(pathStr1);
            final Path path2 = new Path(pathStr2);
            final Nid nid1 = this.rfx.resolve(path1);
            final Nid nid2 = this.rfx.resolve(path2);
            final Descriptor data = this.rfx.getData(nid1);
            this.rfx.putData(nid2, data);
        }catch(final Exception exc){
            System.err.println("Error copying" + pathStr1 + " into " + pathStr2 + ": " + exc);
        }
    }

    /*
    void copyMhdBr(int fromShot, int toShot)
    {
        Descriptor data303, data304, data305;
        Nid nid303, nid304, nid305;
        Descriptor decouplingData;
        if(fromShot == toShot)
            return;
    
        try {
            rfx.close();
            rfx = new Database("rfx", fromShot);
            rfx.open();
        }
        catch(Exception exc)
        {
            JOptionPane.showMessageDialog(this, "Cannot open shot " + fromShot, "Error opening shot",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            nid303 = rfx.resolve(new Path("\\MHD_AC::CONTROL.PARAMETERS:PAR303_VAL"));
            data303 = rfx.getData(nid303);
            nid304 = rfx.resolve(new Path("\\MHD_AC::CONTROL.PARAMETERS:PAR304_VAL"));
            data304 = rfx.getData(nid304);
            nid305 = rfx.resolve(new Path("\\MHD_AC::CONTROL.PARAMETERS:PAR305_VAL"));
            data305 = rfx.getData(nid305);
        }
        catch(Exception exc)
        {
            JOptionPane.showMessageDialog(this, "Cannot read MHD_BR for " + fromShot, "Error reading data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            rfx.close();
            rfx = new Database("rfx", toShot);
            rfx.open();
        }
        catch(Exception exc)
        {
            JOptionPane.showMessageDialog(this, "Cannot open shot " + toShot, "Error opening shot",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            nid303 = rfx.resolve(new Path("\\MHD_BR::CONTROL.PARAMETERS:PAR303_VAL"));
            rfx.putData(nid303, data303);
            nid304 = rfx.resolve(new Path("\\MHD_BR::CONTROL.PARAMETERS:PAR304_VAL"));
            rfx.putData(nid304, data304);
            nid305 = rfx.resolve(new Path("\\MHD_BR::CONTROL.PARAMETERS:PAR305_VAL"));
            rfx.putData(nid305, data305);
         }
        catch(Exception exc)
        {
            JOptionPane.showMessageDialog(this, "Cannot write MHD_BR for " + toShot, "Error reading data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(toShot != shot)
        {
            try {
                rfx.close();
                rfx = new Database("rfx", shot);
                rfx.open();
            }
            catch(Exception exc)
            {
                JOptionPane.showMessageDialog(this, "Cannot open shot " + shot, "Error opening shot",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
    
    }
    */
    void copyDecoupling(final int fromShot, final int toShot) {
        Descriptor decouplingData;
        if(fromShot == toShot) return;
        try{
            this.rfx.close();
            this.rfx = new Database("rfx", fromShot);
            this.rfx.open();
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, "Cannot open shot " + fromShot, "Error opening shot", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Nid decNid;
        /*        try {
             decNid = rfx.resolve(new Path(
                    "\\MHD_AC::CONTROL.PARAMETERS:PAR236_VAL"));
        }catch(Exception exc) {decNid = null; }
        if(decNid == null)
        {
         */ try{
            decNid = this.rfx.resolve(new Path("\\MHD_AC::MARTE.PARAMS:PAR_312:DATA"));
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, "Cannot find Decoupling for " + fromShot, "Error reading data", JOptionPane.WARNING_MESSAGE);
            try{
                this.rfx.close();
                this.rfx = new Database("rfx", toShot);
                this.rfx.open();
            }catch(final Exception exc1){
                JOptionPane.showMessageDialog(this, "Cannot open shot " + toShot, "Error opening shot", JOptionPane.WARNING_MESSAGE);
                return;
            }
            return;
        }
        // }
        try{
            decouplingData = this.rfx.getData(decNid);
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, "Cannot read Decoupling for " + fromShot, "Error reading data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try{
            this.rfx.close();
            this.rfx = new Database("rfx", toShot);
            this.rfx.open();
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, "Cannot open shot " + toShot, "Error opening shot", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try{
            final Nid decNid1 = this.rfx.resolve(new Path(
                    // "\\MHD_AC::CONTROL.PARAMETERS:PAR236_VAL"));
                    "\\MHD_AC::MARTE.PARAMS:PAR_312:DATA"));
            // Nid decNid2 = rfx.resolve(new Path(
            // "\\MHD_BC::CONTROL.PARAMETERS:PAR236_VAL"));
            this.rfx.putData(decNid1, decouplingData);
            // rfx.putData(decNid2, decouplingData);
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, "Cannot read Decoupling for " + toShot, "Error reading data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(toShot != this.shot){
            try{
                this.rfx.close();
                this.rfx = new Database("rfx", this.shot);
                this.rfx.open();
            }catch(final Exception exc){
                JOptionPane.showMessageDialog(this, "Cannot open shot " + this.shot, "Error opening shot", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
    }

    int countPMUnits() {
        final Nid unitsNid = new Nid(this.nids[4].getValue() + 5);
        try{
            final Descriptor unitsData = this.rfx.tdiEvaluate(unitsNid);
            final String units = unitsData.toString();
            final StringTokenizer st = new StringTokenizer(units, " ,\"");
            return st.countTokens();
        }catch(final Exception exc){
            System.err.println("Error getting num enabled PM: " + exc);
            return 0;
        }
    }

    DeviceSetup createDevice(final int idx) {
        switch(idx){
            case 0:
                return new RFXTimesSetup();
            case 1:
                return new RFXPolControlSetup();
            case 2:
                return new RFXAxiControlSetup();
            case 3:
                // return new RFXPCSetupSetup();
                return new RFXPC4SetupSetup();
            case 4:
                return new RFXPMSetupSetup();
            case 5:
                return new RFXTorControlSetup();
            case 6:
                return new RFXChopperSetupSetup();
            case 7:
                return new RFXFeedforwardSetupSetup();
            case 8:
                return new RFXInverterSetupSetup();
            case 9:
                return new RFXTFSetupSetup();
            case 10:
                return new RFXAxiToroidalControlSetup();
            case 11:
                return new RFXMHDSetup();
            case 12:
                // return new RFXVISetupSetup();
                return new RFXVICONTROLSetup();
            case 13:
                return new PELLETSetup();
            case 14:
                return new RFXDiagTimesV1Setup();
            case 15:
                return new IPControlSetup();
            case 16:
                return new RFXMOPSetup();
            case 17:
                return new RFXANSALDOSetup();
            case 18:
                return new RFXABUnitsSetup();
            case 19:
                return new RFXPoloidalSetup();
            case 20:
                return new RFXToroidalSetup();
            case 21:
                return new RFXPRConfigSetup();
            case 22:
                // return new RFXVIConfigSetup(); Taliercio 10 - 01 - 2011
                return new RFXVConfigSetup();
        }
        return null;
    }

    private void getCurrFFState() {
        boolean on1, on2;
        try{
            final Nid nid1D = this.rfx.resolve(new Path("\\MHD_AC::CURR_FF"));
            final Nid nid2D = this.rfx.resolve(new Path("\\MHD_BC::CURR_FF"));
            on1 = this.rfx.isOn(nid1D);
            on2 = this.rfx.isOn(nid2D);
        }catch(final Exception exc){
            System.out.println("Cannot get state of currFF");
            return;
        }
        if(on1 != on2) JOptionPane.showMessageDialog(this, "Different FF setting for AC and BC");
        this.currFFC.setSelectedIndex(on1 ? 1 : 0);
    }

    String getDecouplingName(final int inShot) {
        String outName = null;
        try{
            // if(inShot != shot)
            {
                // rfx.close();
                this.rfx = new Database("rfx", inShot);
                this.rfx.open();
            }
            Nid decNid;
            try{
                decNid = this.rfx.resolve(new Path("\\MHD_AC::CONTROL.PARAMETERS:PAR236_VAL"));
            }catch(final Exception exc){
                decNid = null;
            }
            if(decNid == null) decNid = this.rfx.resolve(new Path("\\MHD_AC::MARTE.PARAMS:PAR_312:DATA"));
            final Descriptor decouplingData = this.rfx.getData(decNid);
            final Descriptor evaluatedDecouplingData = this.rfx.tdiEvaluate(decouplingData);
            final float[] decouplingValues = (evaluatedDecouplingData).toFloat();
            final int key = Convert.getKey(decouplingValues);
            for(int i = 0; i < this.decouplingKeys.length; i++){
                if(this.decouplingKeys[i] == key){
                    outName = this.decouplingNames[i];
                    break;
                }
            }
            // if (inShot != shot) {
            this.rfx.close();
            this.rfx = new Database("rfx", this.shot);
            this.rfx.open();
            // }
        }catch(final Exception exc){
            System.err.println(exc);
            outName = null;
        }
        return outName;
    }

    void getLimits() {
        try{
            final BufferedReader br = new BufferedReader(new FileReader("rt_limits"));
            this.maxPMAT = Integer.parseInt(br.readLine());
            this.maxPCATParallel = Integer.parseInt(br.readLine());
            this.maxPCATSeries = Integer.parseInt(br.readLine());
            this.maxTFAT = Integer.parseInt(br.readLine());
            this.maxTCCH = Integer.parseInt(br.readLine());
            this.maxTCAC = Integer.parseInt(br.readLine());
            this.maxPMVoltage = Integer.parseInt(br.readLine());
            // maxFillVoltage = Integer.parseInt(br.readLine());
            // maxPuffVoltage = Integer.parseInt(br.readLine());
            this.maxTempRoom = Integer.parseInt(br.readLine());
            this.maxTempSaddle = Integer.parseInt(br.readLine());
            this.maxTempMagnetizing = Integer.parseInt(br.readLine());
            this.maxPOhm = Integer.parseInt(br.readLine());
            this.maxPrTime = Integer.parseInt(br.readLine());
            this.maxTempTor = Integer.parseInt(br.readLine());
            this.maxI2T = Integer.parseInt(br.readLine());
            this.maxCurrSellaV = Integer.parseInt(br.readLine());
            this.maxCurrSellaVI0 = Integer.parseInt(br.readLine());
            this.maxCurrSellaP = Integer.parseInt(br.readLine());
            this.maxFillPuffVoltage = Integer.parseInt(br.readLine());
            br.close();
        }catch(final Exception exc){
            System.out.println(exc);
            this.maxPMAT = 12500;
            this.maxPCATParallel = 1500;
            this.maxPCATSeries = 3000;
            this.maxTFAT = 6000;
            this.maxTCCH = 2000;
            this.maxTCAC = 2500;
            this.maxPMVoltage = 35000;
            // maxFillVoltage = 120;
            // maxPuffVoltage = 120;
            this.maxTempRoom = 100;
            this.maxTempSaddle = 60;
            this.maxTempMagnetizing = 35;
            this.maxPOhm = 80;
            this.maxPrTime = 650;
            this.maxTempTor = 50;
            this.maxI2T = 300;
            this.maxCurrSellaV = 200;
            this.maxCurrSellaVI0 = 400;
            this.maxCurrSellaP = 400;
            this.maxFillPuffVoltage = 120;
        }
        this.maxPMATF.setText("" + this.maxPMAT);
        this.maxPCATParallelF.setText("" + this.maxPCATParallel);
        this.maxPCATSeriesF.setText("" + this.maxPCATSeries);
        this.maxTFATF.setText("" + this.maxTFAT);
        this.maxTCCHF.setText("" + this.maxTCCH);
        this.maxTCACF.setText("" + this.maxTCAC);
        this.maxPMVoltageF.setText("" + this.maxPMVoltage);
        // maxFillVoltageF.setText("" + maxFillVoltage);
        // maxPuffVoltageF.setText("" + maxPuffVoltage);
        this.maxTempRoomF.setText("" + this.maxTempRoom);
        this.maxTempSaddleF.setText("" + this.maxTempSaddle);
        this.maxTempMagnetizingF.setText("" + this.maxTempMagnetizing);
        this.maxPOhmF.setText("" + this.maxPOhm);
        this.maxPrTimeF.setText("" + this.maxPrTime);
        this.maxTempTorF.setText("" + this.maxTempTor);
        this.maxI2TF.setText("" + this.maxI2T);
        this.maxCurrSellaVF.setText("" + this.maxCurrSellaV);
        this.maxCurrSellaVI0F.setText("" + this.maxCurrSellaVI0);
        this.maxCurrSellaPF.setText("" + this.maxCurrSellaP);
        this.maxFillPuffVoltageF.setText("" + this.maxFillPuffVoltage);
    }

    float getMagnetizingCurrent() {
        final Nid waveNid = new Nid(this.nids[4].getValue() + 8);
        try{
            final Descriptor waveData = this.rfx.tdiEvaluate(waveNid);
            final float[] wave = waveData.toFloat();
            float maxCurr = wave[0];
            for(final float element : wave)
                if(maxCurr < element) maxCurr = element;
            return maxCurr * this.countPMUnits();
        }catch(final Exception exc){
            System.err.println("Error getting Magnetizing current: " + exc);
            return 0;
        }
    }

    String[] getMatrixFiles() {
        try{
            final File currDir = new File("/usr/local/rfx/data_acquisition/real_time/decoupling");
            return currDir.list(new FilenameFilter(){
                @Override
                public boolean accept(final File dir, final String name) {
                    return name.endsWith(".dat");
                }
            });
        }catch(final Exception exc){
            return new String[0];
        }
    }

    Vector getModifiedNidsV(final Hashtable modifiedSetupHash, final Hashtable modifiedSetupOnHash) {
        final Vector nidsV = new Vector();
        Enumeration changedPaths = modifiedSetupHash.keys();
        String currName = "";
        while(changedPaths.hasMoreElements()){
            try{
                currName = (String)changedPaths.nextElement();
                final Nid currNidData = this.rfx.resolve(new Path(currName));
                nidsV.addElement(new Integer(currNidData.getValue()));
            }catch(final Exception exc){
                System.err.println("Cannot resolve " + currName + ": " + exc);
            }
        }
        changedPaths = modifiedSetupOnHash.keys();
        while(changedPaths.hasMoreElements()){
            try{
                currName = (String)changedPaths.nextElement();
                final Nid currNidData = this.rfx.resolve(new Path(currName));
                nidsV.addElement(new Integer(currNidData.getValue()));
            }catch(final Exception exc){
                System.err.println("Cannot resolve " + currName + ": " + exc);
            }
        }
        return nidsV;
    }

    String getPCConnection() {
        final Nid configNid = new Nid(this.nids[3].getValue() + 2);
        try{
            final CString configData = (CString)this.rfx.tdiEvaluate(configNid);
            return configData.getValue();
        }catch(final Exception exc){
            System.err.println("Error getting PC connection: " + exc);
            return "";
        }
    }

    String getPVConnection() {
        final Nid configNid = new Nid(this.pvSetupRoot.getValue() + 2);
        try{
            final CString configData = (CString)this.rfx.tdiEvaluate(configNid);
            return configData.getValue();
        }catch(final Exception exc){
            System.err.println("Error getting PV connection: " + exc);
            return "";
        }
    }

    void getRtMessages() {
        try{
            final BufferedReader br = new BufferedReader(new FileReader("rt_messages"));
            String currLine = br.readLine();
            String messages = "";
            while(currLine != null){
                messages += (currLine + "\n");
                currLine = br.readLine();
            }
            this.messageArea.setText(messages);
            br.close();
        }catch(final Exception exc){
            System.err.println("Cannot get rt messages: " + exc);
        }
    }

    float getRTransfer() {
        final Nid unitsNid = new Nid(this.nids[19].getValue() + 20);
        try{
            final Descriptor configData = this.rfx.tdiEvaluate(unitsNid);
            System.out.println(configData.toFloat()[0]);
            return configData.toFloat()[0];
        }catch(final Exception exc){
            System.err.println("Error getting R transfer: " + exc);
            return 0;
        }
    }

    void getSetupForModel(final Hashtable setupHash, final Hashtable setupOnHash, final boolean select[], final boolean timeSelect[]) {
        for(int i = 1; i < ParameterSetting.NUM_SETUP; i++)
            if(select[i - 1]) this.saveSetup(i, setupHash, setupOnHash);
        // Timing components
        if(timeSelect[0]) this.saveSetup(0, this.pm_mask, setupHash, setupOnHash);
        if(timeSelect[1]) this.saveSetup(0, this.pc_mask, setupHash, setupOnHash);
        if(timeSelect[2]) this.saveSetup(0, this.pv_mask, setupHash, setupOnHash);
        if(timeSelect[3]) this.saveSetup(0, this.pp_mask, setupHash, setupOnHash);
        if(timeSelect[4]) this.saveSetup(0, this.pr_mask, setupHash, setupOnHash);
        if(timeSelect[5]) this.saveSetup(0, this.ptso_mask, setupHash, setupOnHash);
        if(timeSelect[6]) this.saveSetup(0, this.ptcb_mask, setupHash, setupOnHash);
        if(timeSelect[7]) this.saveSetup(0, this.ptct_mask, setupHash, setupOnHash);
        if(timeSelect[8]) this.saveSetup(0, this.gas_mask, setupHash, setupOnHash);
        if(timeSelect[9]) this.saveSetup(0, this.tf_mask, setupHash, setupOnHash);
        if(timeSelect[10]) this.saveSetup(0, this.is_mask, setupHash, setupOnHash);
        if(timeSelect[11]) this.saveSetup(0, this.chopper_mask, setupHash, setupOnHash);
        if(timeSelect[12]) this.saveSetup(0, this.inverter_mask, setupHash, setupOnHash);
    }

    int getShot() {
        try{
            final int currShot = this.rfx.getCurrentShot("rfx");
            if(this.doingShot) return currShot;
            return currShot + 1;
        }catch(final Exception exc){
            System.err.println("Error getting current shot: " + exc);
            return -1;
        }
    }

    void handleAlarms() {
        (new AlarmHandler()).start();
    }

    void handleDeviceClosed(final int idx, final boolean isChanged) {
        this.log("Device " + idx + " Closed");
        if(this.isRt){
            if(idx < ParameterSetting.NUM_SETUP) // Setup devices
            {
                if(this.states[idx] == ParameterSetting.CHECKING){
                    this.states[idx] = ParameterSetting.CHECKED;
                    this.buttons[idx].setForeground(Color.black);
                }
                if(this.modifiedNids != null && this.modifiedNids.length > 0) this.devices[idx].setHighlight(false, this.modifiedNids);
            }
        }
    }

    void handleNotRt() {
        try{
            this.rtSock = new Socket(this.rtIp, 4000);
            this.rtDos = new DataOutputStream(this.rtSock.getOutputStream());
        }catch(final Exception exc){
            this.rtDos = null;
            final java.util.Timer timer = new java.util.Timer();
            timer.schedule(new TimerTask(){
                @Override
                public void run() {
                    ParameterSetting.this.handleNotRt();
                }
            }, 5000);
        }
        System.out.println("handleNotRt Finito");
    }

    void handleRt() {
        (new RtHandler()).start();
    }

    void handleScheduler() {
        (new SchedulerHandler()).start();
    }

    void init() {
        try{
            if(this.isRt){
                this.rfx = new Database("RFX", -1);
                this.rfx.open();
            }else{
                if(this.isOnline){
                    this.rfx = new Database("RFX", -1);
                    this.rfx.open();
                    this.rfx.create(100);
                    this.rfx.close();
                    this.rfx = new Database("RFX", 100);
                    this.rfx.open();
                    try{
                        final Nid nidD = this.rfx.resolve(new Path("\\INFO:REF_SHOT"));
                        final int refShot = this.rfx.getData(nidD).toInt()[0];
                        this.refShotLabel.setText(ParameterSetting.refShotLabelText + refShot);
                    }catch(final Exception exc){}
                }else{
                    this.rfx = new Database("RFX", -1);
                    this.rfx.open();
                    this.rfx.create(101);
                    this.rfx.close();
                    this.rfx = new Database("RFX", 101);
                    this.rfx.open();
                }
            }
            this.rfx.open();
        }catch(final Exception exc){
            System.err.println("Cannot open RFX");
            System.exit(0);
        }
        this.updateDeviceNids();
        this.saveSetup(this.currSetupHash, this.currSetupOnHash);
        this.getCurrFFState();
        if(this.isRt) this.setTitle("RFX Parameters -- RT --    shot: " + this.getShot());
        else this.setTitle("RFX Parameters  shot: " + this.shot);
    }

    void loadCurrFF(final int ffShot) {
        try{
            this.rfx.close();
            this.rfx = new Database("rfx", ffShot);
            this.rfx.open();
            Nid ffNid1 = this.rfx.resolve(new Path("\\MHD_AC::CURR_FF"));
            Nid ffNid2 = this.rfx.resolve(new Path("\\MHD_BC::CURR_FF"));
            final boolean currFFOn = this.rfx.isOn(ffNid1);
            if(currFFOn){
                final Descriptor ffCurr[] = new Descriptor[192];
                int idx = 0;
                for(int i = 1; i <= 4; i++){
                    for(int j = 1; j <= 9; j++){
                        final Nid nid = this.rfx.resolve(new Path("\\MHD_AC::CURR_FF:I0" + j + i));
                        ffCurr[idx++] = this.rfx.getData(nid);
                    }
                    for(int j = 10; j <= 48; j++){
                        final Nid nid = this.rfx.resolve(new Path("\\MHD_AC::CURR_FF:I" + j + i));
                        ffCurr[idx++] = this.rfx.getData(nid);
                    }
                }
                this.rfx.close();
                this.rfx = new Database("MHD_FF", 100);
                this.rfx.open();
                idx = 0;
                for(int i = 1; i <= 4; i++){
                    for(int j = 1; j <= 9; j++){
                        final Nid nid = this.rfx.resolve(new Path("\\CURR_FF:I0" + j + i));
                        this.rfx.putData(nid, ffCurr[idx++]);
                    }
                    for(int j = 10; j <= 48; j++){
                        final Nid nid = this.rfx.resolve(new Path("\\CURR_FF:I" + j + i));
                        this.rfx.putData(nid, ffCurr[idx++]);
                    }
                }
            }
            this.rfx.close();
            final Descriptor cleanExpr = Database.tdiCompile("tcl(\"clean mhd_ff/shot=100/override\")");
            this.rfx.tdiEvaluate(cleanExpr);
            this.rfx = new Database("rfx", this.shot);
            this.rfx.open();
            ffNid1 = this.rfx.resolve(new Path("\\MHD_AC::CURR_FF"));
            ffNid2 = this.rfx.resolve(new Path("\\MHD_BC::CURR_FF"));
            this.rfx.setOn(ffNid1, currFFOn);
            this.rfx.setOn(ffNid2, currFFOn);
            this.getCurrFFState();
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(ParameterSetting.this, "Error loadingh MHD FF configuration: " + exc, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void loadPulse() {
        int currShot = 0;
        int prevPMUnits;
        float prevRTransfer;
        String prevPCConnection;
        String prevPVConnection;
        final String shotStr = JOptionPane.showInputDialog(this, "Shot number: ", "Enter shot", JOptionPane.INFORMATION_MESSAGE);
        if(shotStr == null || shotStr.trim().equals("")) return;
        try{
            this.currLoadShot = currShot = Integer.parseInt(shotStr);
            this.log("Load Pulse " + shotStr);
            final LoadPulse loadP = new LoadPulse();
            this.currSetupHash = new Hashtable();
            this.currSetupOnHash = new Hashtable();
            // loadP.getSetup("rfx", currShot, currSetupHash, currSetupOnHash);
            loadP.getSetupWithAbsPath("rfx", currShot, -1, this.currSetupHash, this.currSetupOnHash);
            prevPMUnits = loadP.getPMUnits();
            prevRTransfer = loadP.getRTransfer();
            prevPCConnection = loadP.getPCConnection();
            prevPVConnection = loadP.getPVConnection();
            this.loadSelectedSetup();
            // Checks to be performed after loading pulse
            final int currPMUnits = this.countPMUnits();
            if(currPMUnits != prevPMUnits) JOptionPane.showMessageDialog(ParameterSetting.this, "The number of enabled PM units in loaded shot " + currShot + " is " + prevPMUnits + " which is different from the previous number (" + currPMUnits + ")  of enabled PM units in the working shot", "Configuration discrepance", JOptionPane.WARNING_MESSAGE);
            final float rTransfer = this.getRTransfer();
            if(rTransfer != prevRTransfer) JOptionPane.showMessageDialog(ParameterSetting.this, "Transfer Resistance in loaded shot " + currShot + " is " + prevRTransfer + " which is different from the previous value (" + rTransfer + ")  in the working shot", "Configuration discrepance", JOptionPane.WARNING_MESSAGE);
            final String pcConnection = this.getPCConnection();
            if(!pcConnection.equals(prevPCConnection)) JOptionPane.showMessageDialog(ParameterSetting.this, "PCAT connection in loaded shot " + currShot + " is " + prevPCConnection + " which is different from the previous value (" + pcConnection + ")  in the working shot", "Configuration discrepance", JOptionPane.WARNING_MESSAGE);
            final String pvConnection = this.getPVConnection();
            if(!pvConnection.equals(prevPVConnection)) JOptionPane.showMessageDialog(ParameterSetting.this, "PVAT connection in loaded shot " + currShot + " is " + prevPVConnection + " which is different from the previous value (" + pvConnection + ")  in the working shot", "Configuration discrepance", JOptionPane.WARNING_MESSAGE);
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(ParameterSetting.this, "Error loading pulse " + currShot + ": " + exc, "Error loading pulse", JOptionPane.WARNING_MESSAGE);
        }
    }

    void loadSelectedSetup() {
        if(this.loadSelected == null){
            this.loadSelected = new SelectSetup(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    ParameterSetting.this.log("Load Pulse");
                    final boolean[] selectedDevices = ParameterSetting.this.loadSelected.getSelectedDevices();
                    final boolean[] selectedTimes = ParameterSetting.this.loadSelected.getSelectedTimes();
                    ParameterSetting.this.applySetup(ParameterSetting.this.currSetupHash, ParameterSetting.this.currSetupOnHash, selectedDevices, selectedTimes);
                    if(ParameterSetting.this.loadSelected.currFFSelected()){
                        ParameterSetting.this.loadCurrFF(ParameterSetting.this.currLoadShot);
                    }
                    ParameterSetting.this.checkVersions();
                    ParameterSetting.this.loadSelected.setVisible(false);
                    if(ParameterSetting.this.isOnline){
                        String decouplingName = ParameterSetting.this.getDecouplingName(ParameterSetting.this.currLoadShot);
                        if(decouplingName == null) decouplingName = "Unknown";
                        if(JOptionPane.showConfirmDialog(ParameterSetting.this, "Caricare MHD Decoupling da shot " + ParameterSetting.this.currLoadShot + " (" + decouplingName + ")?", "Decoupling", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                            ParameterSetting.this.copyDecoupling(ParameterSetting.this.currLoadShot, ParameterSetting.this.shot);
                        }
                        /*if (JOptionPane.showConfirmDialog(ParameterSetting.this,
                                                          "Stampare la scheda di caricamento impulso?",
                                                          "Caricamento impulso",
                                                          JOptionPane.YES_NO_OPTION) ==
                            JOptionPane.YES_OPTION) {
                            try {
                                printLoadPulse(currLoadShot);
                            } catch (Exception exc) {
                                System.err.println("Error printing form: " + exc);
                            }
                        }*/
                        // Report saved shot
                        ParameterSetting.this.refShotLabel.setText(ParameterSetting.refShotLabelText + ParameterSetting.this.currLoadShot);
                        // i2tEvaluateResidualPrePulse();
                    }
                }
            });
        }
        this.loadSelected.setEnabledDevices(this.currSetupHash);
        // loadSelected.setEnabledTimes(currSetupHash); //May 2009 Use currSetupOnHash for includiong PTSO
        this.loadSelected.setEnabledTimes(this.currSetupOnHash);
        this.loadSelected.setVisible(true);
    }

    void loadSetup() {
        this.chooser.rescanCurrentDirectory();
        this.chooser.setApproveButtonText("Load Configuration");
        final int returnVal = this.chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            String filePath = this.chooser.getSelectedFile().getPath();
            if(!filePath.toLowerCase().endsWith(".rfx")) filePath += ".rfx";
            this.readSetupFromFile(filePath);
            this.loadSelectedSetup();
            this.loadSelected.setEnabledDevicesForSavingConfiguration();
        }
    }

    void log(final String message) {
        if(this.isRt || !this.isOnline) return;
        if(this.logFile == null){
            try{
                this.logFile = new FileWriter("ParameterSetting.log");
            }catch(final Exception exc){
                System.out.println("Cannot open Log file: " + exc);
                return;
            }
        }
        try{
            this.logFile.write("" + new Date() + "\t" + message + "\n");
        }catch(final Exception exc){
            System.err.println("Error writing to log file: " + exc);
        }
    }

    void notifyApplySetupFinishedRt() {
        if(this.rtDos != null){
            try{
                this.rtDos.writeInt(-2);
                this.rtDos.flush();
            }catch(final Exception exc){
                this.rtDos = null;
                this.handleNotRt();
            }
        }
    }

    void notifyChangedRt(final Hashtable modifiedSetupHash, final Hashtable modifiedSetupOnHash) {
        final Vector nidsV = this.getModifiedNidsV(modifiedSetupHash, modifiedSetupOnHash);
        // Protocol: -1, followed by the number of chenged nids and the nids
        if(this.rtDos != null && nidsV.size() > 0){
            try{
                this.rtDos.writeInt(-1);
                this.rtDos.writeInt(nidsV.size());
                for(int i = 0; i < nidsV.size(); i++)
                    this.rtDos.writeInt(((Integer)nidsV.elementAt(i)).intValue());
                this.rtDos.flush();
            }catch(final Exception exc){
                this.rtDos = null;
                this.handleNotRt();
            }
        }
    }

    void prepareDecouplingInfo() {
        String[] fileNames;
        try{
            final File currDir = new File("/usr/local/rfx/data_acquisition/real_time/decoupling");
            fileNames = currDir.list(new FilenameFilter(){
                @Override
                public boolean accept(final File dir, final String name) {
                    return name.endsWith(".key");
                }
            });
        }catch(final Exception exc){
            return;
        }
        if(fileNames == null) fileNames = new String[0];
        this.decouplingKeys = new int[fileNames.length];
        this.decouplingNames = new String[fileNames.length];
        for(int i = 0; i < fileNames.length; i++){
            try{
                final BufferedReader br = new BufferedReader(new FileReader(ParameterSetting.DECOUPLING_BASE_DIR + fileNames[i]));
                final String keyStr = br.readLine();
                this.decouplingKeys[i] = Integer.parseInt(keyStr);
                this.decouplingNames[i] = fileNames[i].substring(0, fileNames[i].length() - 4);
            }catch(final Exception exc){
                System.err.println(exc);
            }
        }
    }

    @Override
    public int print(final Graphics g, final PageFormat pf, final int pageIndex) throws PrinterException {
        // if(currPrintDeviceIdx == 7 || currPrintDeviceIdx == 14)
        // pf.setOrientation(PageFormat.LANDSCAPE);
        final double height = pf.getImageableHeight();
        final double width = pf.getImageableWidth();
        final Graphics2D g2 = (Graphics2D)g;
        if(pageIndex == 0){
            g2.translate(pf.getImageableX(), pf.getImageableY());
            final Font prevFont = g2.getFont();
            final Font titleFont = new Font("Serif", Font.BOLD, 30);
            g2.setFont(titleFont);
            final FontMetrics titleFontMetrics = g2.getFontMetrics();
            int titleWidth, titleHeight;
            System.out.println("Printing title...");
            if(this.currPrintDeviceIdx >= 0){
                titleWidth = titleFontMetrics.stringWidth(this.titles[this.currPrintDeviceIdx]);
                titleHeight = titleFontMetrics.getHeight();
                g2.drawString(this.titles[this.currPrintDeviceIdx], (int)width / 2 - titleWidth / 2, titleHeight);
            }else // Scheda ripetizione impulso
            {
                titleWidth = titleFontMetrics.stringWidth("Ripetizione Impulso");
                titleHeight = titleFontMetrics.getHeight();
                g2.drawString("Ripetizione Impulso", (int)width / 2 - titleWidth / 2, titleHeight);
            }
            final Font infoFont = new Font("Serif", Font.BOLD, 20);
            g2.setFont(infoFont);
            final FontMetrics infoFontMetrics = g2.getFontMetrics();
            System.out.println("Printing Date..." + this.currPrintDeviceIdx);
            final int infoHeight = infoFontMetrics.getHeight();
            g2.drawString("Data: " + new SimpleDateFormat("dd/MM/yyy").format(new Date()) + "          Impulso: " + this.getShot(), 0, 2 * titleHeight + 10);
            /*            if (currPrintDeviceIdx < 13)
            {
                g2.drawString("RT: ",
                              0, 2 * titleHeight + 20 + infoHeight);
                int rtWidth = infoFontMetrics.stringWidth("RT: ");
                g2.drawLine(rtWidth, 2 * titleHeight + 20 + infoHeight,
                            rtWidth + 150, 2 * titleHeight + 20 + infoHeight);
            }
            */
            if(this.currPrintDeviceIdx >= 0){
                g2.setFont(prevFont);
                g2.translate(0, 2 * titleHeight + 30 + infoHeight);
                g2.scale((width) / this.devices[this.currPrintDeviceIdx].getWidth(), ((height - (2 * titleHeight + 30 + infoHeight)) / this.devices[this.currPrintDeviceIdx].getHeight()));
                System.out.println("Printing Device...");
                this.devices[this.currPrintDeviceIdx].printAll(g2);
            }else // Scheda ripetizione impulso
            {
                g2.drawString("Configurazione caricata dall'impulso " + this.currPrintLoadPulse, 0, 3 * titleHeight + 10);
            }
            System.out.println("Print Done");
            return Printable.PAGE_EXISTS;
        }else return Printable.NO_SUCH_PAGE;
    }

    void print(final int idx) throws PrinterException, PrintException {
        this.currPrintDeviceIdx = idx;
        final DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        final Doc doc = new SimpleDoc(this, flavor, null);
        final DocPrintJob prnJob = this.printService.createPrintJob();
        prnJob.print(doc, null);
    }

    void printLoadPulse(final int shot) throws PrinterException, PrintException {
        final int prevPrintDeviceIdx = this.currPrintDeviceIdx;
        this.currPrintDeviceIdx = -1;
        this.currPrintLoadPulse = shot;
        final DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        final Doc doc = new SimpleDoc(this, flavor, null);
        final DocPrintJob prnJob = this.printService.createPrintJob();
        prnJob.print(doc, null);
        this.currPrintDeviceIdx = prevPrintDeviceIdx;
    }

    void printSetup() {
        final PrintRequestAttributeSet dialogAttributes = new HashPrintRequestAttributeSet(), printAttributes = new HashPrintRequestAttributeSet();
        this.printService = ServiceUI.printDialog(null, 50, 50, PrintServiceLookup.lookupPrintServices(null, printAttributes), this.printService, null, dialogAttributes);
    }

    void readSetupFromFile(final String fileName) {
        try{
            final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
            this.currSetupHash = (Hashtable)ois.readObject();
            this.currSetupOnHash = (Hashtable)ois.readObject();
            ois.close();
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, "Cannot load configuration: " + exc, "Read error", JOptionPane.WARNING_MESSAGE);
        }
    }

    void reportDiffToModifiedNids(final Hashtable modifiedSetupHash, final Hashtable modifiedSetupOnHash) {
        final Vector nidsV = this.getModifiedNidsV(modifiedSetupHash, modifiedSetupOnHash);
        this.modifiedNids = new int[nidsV.size()];
        for(int i = 0; i < this.modifiedNids.length; i++)
            this.modifiedNids[i] = ((Integer)nidsV.elementAt(i)).intValue();
    }

    void revertModel() {
        try{
            this.rfx.close();
            this.rfx = new Database("RFX", -1);
            this.rfx.open();
        }catch(final Exception exc){
            System.err.println("Cannot open model");
            return;
        }
        this.applySetup(this.modelSetupHash, this.modelSetupOnHash);
        try{
            this.rfx.close();
            this.rfx = new Database("RFX", this.shot);
            this.rfx.open();
        }catch(final Exception exc){
            System.err.println("Cannot open working shot");
            return;
        }
    }

    void saveLimits() {
        try{
            this.maxPMAT = Integer.parseInt(this.maxPMATF.getText());
            this.maxPCATParallel = Integer.parseInt(this.maxPCATParallelF.getText());
            this.maxPCATSeries = Integer.parseInt(this.maxPCATSeriesF.getText());
            this.maxTFAT = Integer.parseInt(this.maxTFATF.getText());
            this.maxTCCH = Integer.parseInt(this.maxTCCHF.getText());
            this.maxTCAC = Integer.parseInt(this.maxTCACF.getText());
            this.maxPMVoltage = Integer.parseInt(this.maxPMVoltageF.getText());
            // maxFillVoltage = Integer.parseInt(maxFillVoltageF.getText());
            // maxPuffVoltage = Integer.parseInt(maxPuffVoltageF.getText());
            this.maxTempRoom = Integer.parseInt(this.maxTempRoomF.getText());
            this.maxTempSaddle = Integer.parseInt(this.maxTempSaddleF.getText());
            this.maxTempMagnetizing = Integer.parseInt(this.maxTempMagnetizingF.getText());
            this.maxPOhm = Integer.parseInt(this.maxPOhmF.getText());
            this.maxPrTime = Integer.parseInt(this.maxPrTimeF.getText());
            this.maxTempTor = Integer.parseInt(this.maxTempTorF.getText());
            this.maxI2T = Integer.parseInt(this.maxI2TF.getText());
            this.maxCurrSellaV = Integer.parseInt(this.maxCurrSellaVF.getText());
            this.maxCurrSellaVI0 = Integer.parseInt(this.maxCurrSellaVI0F.getText());
            this.maxCurrSellaP = Integer.parseInt(this.maxCurrSellaPF.getText());
            this.maxFillPuffVoltage = Integer.parseInt(this.maxFillPuffVoltageF.getText());
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, "Valori errati nei limiti", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try{
            final BufferedWriter bw = new BufferedWriter(new FileWriter("rt_limits"));
            bw.write("" + this.maxPMAT);
            bw.newLine();
            bw.write("" + this.maxPCATParallel);
            bw.newLine();
            bw.write("" + this.maxPCATSeries);
            bw.newLine();
            bw.write("" + this.maxTFAT);
            bw.newLine();
            bw.write("" + this.maxTCCH);
            bw.newLine();
            bw.write("" + this.maxTCAC);
            bw.newLine();
            bw.write("" + this.maxPMVoltage);
            bw.newLine();
            // bw.write("" + maxFillVoltage);
            // bw.newLine();
            // bw.write("" + maxPuffVoltage);
            // bw.newLine();
            bw.write("" + this.maxTempRoom);
            bw.newLine();
            bw.write("" + this.maxTempSaddle);
            bw.newLine();
            bw.write("" + this.maxTempMagnetizing);
            bw.newLine();
            bw.write("" + this.maxPOhm);
            bw.newLine();
            bw.write("" + this.maxPrTime);
            bw.newLine();
            bw.write("" + this.maxTempTor);
            bw.newLine();
            bw.write("" + this.maxI2T);
            bw.newLine();
            bw.write("" + this.maxCurrSellaV);
            bw.newLine();
            bw.write("" + this.maxCurrSellaVI0);
            bw.newLine();
            bw.write("" + this.maxCurrSellaP);
            bw.newLine();
            bw.write("" + this.maxFillPuffVoltage);
            bw.newLine();
            bw.close();
        }catch(final Exception exc){
            System.err.println("Error saving limits: " + exc);
        }
    }

    void saveRtMessages() {
        try{
            final BufferedWriter bw = new BufferedWriter(new FileWriter("rt_messages"));
            final String messages = this.messageArea.getText();
            final StringTokenizer st = new StringTokenizer(messages, "\n");
            while(st.hasMoreTokens()){
                bw.write(st.nextToken());
                bw.newLine();
            }
            bw.close();
        }catch(final Exception exc){
            System.err.println("Error saving rt messages: " + exc);
        }
    }

    void saveSelectedSetup() {
        if(this.saveSelected == null){
            this.saveSelected = new SelectSetup(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    ParameterSetting.this.log("Save Configuration");
                    final boolean[] selectedDevices = ParameterSetting.this.saveSelected.getSelectedDevices();
                    final boolean[] selectedTimes = ParameterSetting.this.saveSelected.getSelectedTimes();
                    ParameterSetting.this.saveSetup(selectedDevices, selectedTimes);
                    ParameterSetting.this.saveSelected.setVisible(false);
                }
            });
            this.saveSelected.setEnabledDevicesForSavingConfiguration();
        }
        this.saveSelected.setVisible(true);
    }

    void saveSetup(final boolean select[], final boolean timeSelect[]) {
        this.chooser.rescanCurrentDirectory();
        this.chooser.setApproveButtonText("Save");
        final int returnVal = this.chooser.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            this.currSetupHash = new Hashtable();
            this.currSetupOnHash = new Hashtable();
            for(int i = 1; i < ParameterSetting.NUM_SETUP; i++)
                if(select[i - 1]) this.saveSetup(i, this.currSetupHash, this.currSetupOnHash);
            // Timing components
            if(timeSelect[0]) this.saveSetup(0, this.pm_mask, this.currSetupHash, this.currSetupOnHash);
            if(timeSelect[1]) this.saveSetup(0, this.pc_mask, this.currSetupHash, this.currSetupOnHash);
            if(timeSelect[2]) this.saveSetup(0, this.pv_mask, this.currSetupHash, this.currSetupOnHash);
            if(timeSelect[3]) this.saveSetup(0, this.pp_mask, this.currSetupHash, this.currSetupOnHash);
            if(timeSelect[4]) this.saveSetup(0, this.pr_mask, this.currSetupHash, this.currSetupOnHash);
            if(timeSelect[5]) this.saveSetup(0, this.ptso_mask, this.currSetupHash, this.currSetupOnHash);
            if(timeSelect[6]) this.saveSetup(0, this.ptcb_mask, this.currSetupHash, this.currSetupOnHash);
            if(timeSelect[7]) this.saveSetup(0, this.ptct_mask, this.currSetupHash, this.currSetupOnHash);
            if(timeSelect[8]) this.saveSetup(0, this.gas_mask, this.currSetupHash, this.currSetupOnHash);
            if(timeSelect[9]) this.saveSetup(0, this.tf_mask, this.currSetupHash, this.currSetupOnHash);
            if(timeSelect[10]) this.saveSetup(0, this.is_mask, this.currSetupHash, this.currSetupOnHash);
            if(timeSelect[11]) this.saveSetup(0, this.chopper_mask, this.currSetupHash, this.currSetupOnHash);
            if(timeSelect[12]) this.saveSetup(0, this.inverter_mask, this.currSetupHash, this.currSetupOnHash);
            String filePath = this.chooser.getSelectedFile().getPath();
            if(!filePath.toLowerCase().endsWith(".rfx")) filePath += ".rfx";
            this.writeSetupToFile(filePath);
        }
    }

    void saveSetup(final Hashtable setupHash, final Hashtable setupOnHash) {
        for(int i = 0; i < ParameterSetting.NUM_SETUP; i++)
            this.saveSetup(i, setupHash, setupOnHash);
    }

    void saveSetup(final int idx, final Hashtable configHash, final Hashtable configOnHash) {
        try{
            final Nid baseNid = this.rfx.getDefault();
            this.rfx.setDefault(this.nids[idx]);
            Nid[] deviceNids = this.rfx.getWild(NodeInfo.USAGE_NUMERIC);
            this.rfx.setDefault(baseNid);
            if(deviceNids != null){
                for(final Nid deviceNid : deviceNids){
                    String currDec;
                    try{
                        final String fullPath = this.rfx.getInfo(deviceNid).getFullPath();
                        System.out.println("SAVE SETUP: " + fullPath);
                        if(fullPath.endsWith(".TRIANGLE:REGULATION")) System.out.println("CUEA");
                        if(!fullPath.endsWith("PARAMS:PAR_312:DATA") && // Escludo le matrici di disaccopiamento!!!!!!!!!!!11
                                (fullPath.indexOf("SIGNALS:") == -1)) // E i segnali in RfxControl
                        {
                            this.mapSetupHash.put(fullPath, new Integer(idx));
                            currDec = (this.rfx.getData(deviceNid)).toString();
                            configHash.put(fullPath, currDec);
                        }
                    }catch(final Exception exc){}
                    configOnHash.put(this.rfx.getInfo(deviceNid).getFullPath(), new Boolean(this.rfx.isOn(deviceNid)));
                }
            }
            this.rfx.setDefault(this.nids[idx]);
            deviceNids = this.rfx.getWild(NodeInfo.USAGE_TEXT);
            this.rfx.setDefault(baseNid);
            if(deviceNids != null){
                for(final Nid deviceNid : deviceNids){
                    String currDec;
                    try{
                        final String fullPath = this.rfx.getInfo(deviceNid).getFullPath();
                        if(fullPath.endsWith(".TRIANGLE:REGULATION")) System.out.println("CUEA");
                        if(!fullPath.endsWith(":PAR236_VAL") && // Escludo le matrici di disaccopiamento!!!!!!!!!!!11
                                (fullPath.indexOf("SIGNALS:") == -1)) // E i segnali in RfxControl
                        {
                            this.mapSetupHash.put(fullPath, new Integer(idx));
                            currDec = (this.rfx.getData(deviceNid)).toString();
                            configHash.put(fullPath, currDec);
                        }
                    }catch(final Exception exc){}
                    configOnHash.put(this.rfx.getInfo(deviceNid).getFullPath(), new Boolean(this.rfx.isOn(deviceNid)));
                }
            }
            this.rfx.setDefault(this.nids[idx]);
            deviceNids = this.rfx.getWild(NodeInfo.USAGE_STRUCTURE);
            this.rfx.setDefault(baseNid);
            if(deviceNids != null){
                for(final Nid deviceNid : deviceNids){
                    final String fullPath = this.rfx.getInfo(deviceNid).getFullPath();
                    this.mapSetupHash.put(fullPath, new Integer(idx));
                    configOnHash.put(fullPath, new Boolean(this.rfx.isOn(deviceNid)));
                    // Dummy value
                    configHash.put(fullPath, "");
                }
            }
            this.rfx.setDefault(this.nids[idx]);
            deviceNids = this.rfx.getWild(NodeInfo.USAGE_SIGNAL);
            this.rfx.setDefault(baseNid);
            if(deviceNids != null){
                for(final Nid deviceNid : deviceNids){
                    String currDec;
                    try{
                        final String fullPath = this.rfx.getInfo(deviceNid).getFullPath();
                        if(!fullPath.endsWith(":PAR236_VAL") && // Escludo le matrici di disaccopiamento!!!!!!!!!!!11
                                (fullPath.indexOf("SIGNALS:") == -1)) // E i segnali in RfxControl
                        {
                            this.mapSetupHash.put(fullPath, new Integer(idx));
                            currDec = (this.rfx.getData(deviceNid)).toString();
                            configHash.put(fullPath, currDec);
                        }
                    }catch(final Exception exc){}
                    configOnHash.put(this.rfx.getInfo(deviceNid).getFullPath(), new Boolean(this.rfx.isOn(deviceNid)));
                }
            }
        }catch(final Exception exc1){
            System.err.println("Error getting device nids: " + exc1);
        }
    }

    void saveSetup(final int idx, final int nidOffsets[], final Hashtable configHash, final Hashtable configOnHash) {
        try{
            for(final int nidOffset : nidOffsets){
                final Nid currNid = new Nid(this.nids[idx].getValue() + nidOffset);
                String currDec;
                final String fullPath = this.rfx.getInfo(currNid).getFullPath();
                try{
                    currDec = (this.rfx.getData(currNid)).toString();
                    configHash.put(fullPath, currDec);
                }catch(final Exception exc){}
                configOnHash.put(fullPath, new Boolean(this.rfx.isOn(currNid)));
            }
        }catch(final Exception exc1){
            System.err.println("Error getting device nids: " + exc1);
        }
    }

    void saveSetupAndConfig(final Hashtable setupHash, final Hashtable setupOnHash) {
        for(int i = 0; i < ParameterSetting.NUM_DEVICES; i++)
            this.saveSetup(i, setupHash, setupOnHash);
    }

    private void setCurrFFState() {
        final int idx = this.currFFC.getSelectedIndex();
        try{
            final Nid nid1D = this.rfx.resolve(new Path("\\MHD_AC::CURR_FF"));
            final Nid nid2D = this.rfx.resolve(new Path("\\MHD_BC::CURR_FF"));
            this.rfx.setOn(nid1D, idx != 0);
            this.rfx.setOn(nid2D, idx != 0);
        }catch(final Exception exc){
            System.out.println("Cannot set state of currFF");
            return;
        }
    }

    void setDecoupling() {
        if(this.decouplingD == null) this.decouplingD = new DecouplingDialog();
        this.decouplingD.setVisible(true);
    }

    void setDecoupling(final String decouplingName) {
        Convert conv;
        if(decouplingName.equals("diagonal")){
            conv = new Convert(
                    // "\\mhd_ac::control.parameters:par236_val", "diagonal", shot);
                    "\\\\MHD_AC::MARTE.PARAMS:PAR_312:DATA", "diagonal", this.shot);
            conv.convertMatrix();
            /*            conv = new Convert(
            //                "\\mhd_bc::control.parameters:par236_val", "diagonal", -1);
                  "\\mhd_bc::control.parameters:par236_val", "diagonal", shot);
            conv.convertMatrix();
            */ }else if(decouplingName.equals("From Shot...")){
            final String shotStr = JOptionPane.showInputDialog(ParameterSetting.this, "Shot number: ");
            try{
                final int shotNum = Integer.parseInt(shotStr);
                this.copyDecoupling(shotNum, this.shot);
            }catch(final Exception exc){
                JOptionPane.showMessageDialog(ParameterSetting.this, "Error loading Decoupling Matrix", "Error", JOptionPane.WARNING_MESSAGE);
            }
        }else{
            conv = new Convert(
                    // "\\mhd_ac::control.parameters:par236_val", DECOUPLING_BASE_DIR + decouplingName + ".dat", shot);
                    "\\MHD_AC::MARTE.PARAMS:PAR_312:DATA", ParameterSetting.DECOUPLING_BASE_DIR + decouplingName + ".dat", this.shot);
            try{
                this.rfx.close();
                conv.convertMatrix();
                this.rfx = new Database("RFX", this.shot);
                this.rfx.open();
            }catch(final Exception exc){
                System.out.println("CANNOT OPEN RFX AFTER SETTING DECOUPLING");
            }
            /*           conv = new Convert(
            //                "\\mhd_bc::control.parameters:par236_val", DECOUPLING_BASE_DIR + decouplingName + ".dat", -1);
                "\\mhd_bc::control.parameters:par236_val", DECOUPLING_BASE_DIR + decouplingName + ".dat", shot);
            conv.convertMatrix();
            */ }
        this.decouplingD.setVisible(false);
    }

    void setReadOnly(final boolean readOnly) {
        System.out.println("SET READ ONLY ");
        this.readOnly = readOnly;
        for(final Nid nid : this.nids){
            final DeviceSetup device = DeviceSetup.getDevice(nid.getValue());
            if(device != null){
                System.out.println("SET READ ONLY " + readOnly);
                device.setReadOnly(readOnly);
            }
        }
    }

    void setUncheckedRt(final int idx) {
        if(this.rtDos != null){
            try{
                this.rtDos.writeInt(idx);
            }catch(final Exception exc){
                this.rtDos = null;
                this.handleNotRt();
                // Trye to resend message
                try{
                    this.rtDos.writeInt(idx);
                }catch(final Exception exc1){}
            }
        }
    }

    void showDecouplingInfo() {
        final String shotStr = JOptionPane.showInputDialog(ParameterSetting.this, "Shot: ");
        try{
            final int inShot = Integer.parseInt(shotStr);
            final String decouplingName = this.getDecouplingName(inShot);
            if(decouplingName != null) JOptionPane.showMessageDialog(ParameterSetting.this, "Decoupling for shot " + inShot + ": " + decouplingName, "Decoupling", JOptionPane.INFORMATION_MESSAGE);
            else JOptionPane.showMessageDialog(ParameterSetting.this, "Unknown decoupling matrix", "Decoupling", JOptionPane.INFORMATION_MESSAGE);
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(ParameterSetting.this, "Unknown decoupling matrix", "Decoupling", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    void updateDeviceNids() {
        try{
            this.nids[0] = this.timesRoot = this.rfx.resolve(new Path("\\RFX_TIMES"));
            // nids[1] = poloidalControlRoot = rfx.resolve(new Path(
            // "\\EDA1::CONTROL"));
            this.nids[1] = this.poloidalControlRoot = this.rfx.resolve(new Path("\\EDA1::MARTE"));
            this.nids[2] = this.axiSetupRoot = this.rfx.resolve(new Path("\\AXI_CONTROL"));
            this.nids[3] = this.pcSetupRoot = this.rfx.resolve(new Path("\\PC_SETUP"));
            this.nids[4] = this.pmSetupRoot = this.rfx.resolve(new Path("\\PM_SETUP"));
            this.nids[5] = this.toroidalControlRoot = this.rfx.resolve(new Path("\\EDA3::CONTROL"));
            this.nids[6] = this.chopperSetupRoot = this.rfx.resolve(new Path("\\CHOPPER_SETUP"));
            this.nids[7] = this.ffSetupRoot = this.rfx.resolve(new Path("\\FEEDFORWARD_SETUP"));
            this.nids[8] = this.inverterSetupRoot = this.rfx.resolve(new Path("\\INVERTER_SETUP"));
            this.nids[9] = this.tfSetupRoot = this.rfx.resolve(new Path("\\TF_SETUP"));
            this.nids[10] = this.bfControlRoot = this.rfx.resolve(new Path("\\AXI_TOROIDAL_CONTROL"));
            // nids[11] = mhdControlRoot = rfx.resolve(new Path("\\MHD_AC::CONTROL"));
            this.nids[11] = this.mhdControlRoot = this.rfx.resolve(new Path("\\MHD_AC::MARTE"));
            // mhdBcNid = rfx.resolve(new Path("\\MHD_BC::CONTROL"));
            this.nids[12] = this.viSetupRoot = this.rfx.resolve(new Path("\\VI_SETUP"));
            this.nids[13] = this.pelletSetupRoot = this.rfx.resolve(new Path("\\PELLET_SETUP"));
            this.nids[14] = this.diagTimesSetupRoot = this.rfx.resolve(new Path("\\DIAG_TIMES_SETUP"));
            this.nids[15] = this.ipSetupRoot = this.rfx.resolve(new Path("\\IP_CONTROL"));
            this.nids[16] = this.mopRoot = this.rfx.resolve(new Path("\\MOP"));
            this.nids[17] = this.ansaldoConfigRoot = this.rfx.resolve(new Path("\\ANSALDO"));
            this.nids[18] = this.unitsConfigRoot = this.rfx.resolve(new Path("\\ABUNITS"));
            this.nids[19] = this.poloidalConfigRoot = this.rfx.resolve(new Path("\\P_CONFIG"));
            this.nids[20] = this.toroidalConfigRoot = this.rfx.resolve(new Path("\\T_CONFIG"));
            this.nids[21] = this.mhdConfigRoot = this.rfx.resolve(new Path("\\PR_CONFIG"));
            this.nids[22] = this.viConfigRoot = this.rfx.resolve(new Path("\\VI_CONFIG"));
            this.pvSetupRoot = this.rfx.resolve(new Path("\\PV_SETUP"));
        }catch(final Exception exc){
            exc.printStackTrace();
            System.err.println("Error opening device");
            System.exit(0);
        }
    }

    void writeSetupToFile(final String fileName) {
        try{
            final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
            oos.writeObject(this.currSetupHash);
            oos.writeObject(this.currSetupOnHash);
            oos.close();
        }catch(final Exception exc){
            JOptionPane.showMessageDialog(this, "Cannot save configuration: " + exc, "Write error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
