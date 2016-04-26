/*
 * A basic implementation of the DeviceSetup class.
 */
import java.awt.*;
import devicebeans.DeviceButtons;
import devicebeans.DeviceChannel;
import devicebeans.DeviceDispatch;
import devicebeans.DeviceField;
import devicebeans.DeviceSetup;
import javax.swing.*;

public class VMEWavesSetup extends DeviceSetup{
    /**
     * 
     */
    private static final long serialVersionUID = -5379063986731780125L;
    public VMEWavesSetup(JFrame parent){
        super(parent);
        // This code is automatically generated by Visual Cafe when you add
        // components to the visual environment. It instantiates and initializes
        // the components. To modify the code, only use code syntax that matches
        // what Visual Cafe can generate, or Visual Cafe may be unable to back
        // parse your Java file into its visual environment.
        // {{INIT_CONTROLS
        setDeviceTitle("VME waveform generator");
        getContentPane().setLayout(null);
        setSize(518, 373);
        comment.setNumCols(15);
        comment.setTextOnly(true);
        comment.setOffsetNid(1);
        comment.setLabelString("Comment: ");
        getContentPane().add(comment);
        comment.setBounds(24, 12, 276, 24);
        base_freq.setIdentifier("base_freq");
        base_freq.setOffsetNid(3);
        base_freq.setLabelString("Base Freq.: ");
        getContentPane().add(base_freq);
        base_freq.setBounds(12, 48, 204, 36);
        vme_ip.setNumCols(14);
        vme_ip.setIdentifier("vme_ip");
        vme_ip.setTextOnly(true);
        vme_ip.setOffsetNid(2);
        vme_ip.setLabelString("VME IP:");
        getContentPane().add(vme_ip);
        vme_ip.setBounds(228, 48, 264, 24);
        {
            String[] tempString = new String[2];
            tempString[0] = "VME IP address must be defined";
            tempString[1] = "Base frequency must be defined and positive";
            deviceButtons1.setCheckMessages(tempString);
        }
        {
            String[] tempString = new String[2];
            tempString[0] = "len(_vme_ip) > 0";
            tempString[1] = "_base_freq > 0";
            deviceButtons1.setCheckExpressions(tempString);
        }
        getContentPane().add(deviceButtons1);
        deviceButtons1.setBounds(108, 312, 324, 40);
        getContentPane().add(deviceDispatch1);
        deviceDispatch1.setBounds(312, 12, 131, 40);
        getContentPane().add(JTabbedPane1);
        JTabbedPane1.setBounds(36, 108, 468, 180);
        deviceChannel1.setInSameLine(true);
        deviceChannel1.setOffsetNid(4);
        deviceChannel1.setLabelString("ch1");
        deviceChannel1.setBorderVisible(true);
        deviceChannel1.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel1);
        deviceChannel1.setBounds(2, 42, 463, 135);
        JPanel2.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel1.add(JPanel2);
        JPanel3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel2.add(JPanel3);
        deviceField1.setOffsetNid(5);
        deviceField1.setLabelString("Trig. Time");
        JPanel3.add(deviceField1);
        deviceField2.setOffsetNid(6);
        deviceField2.setLabelString("Freq:");
        JPanel3.add(deviceField2);
        deviceField3.setNumCols(30);
        deviceField3.setOffsetNid(7);
        deviceField3.setLabelString("X:");
        JPanel2.add(deviceField3);
        deviceField4.setNumCols(30);
        deviceField4.setOffsetNid(8);
        deviceField4.setLabelString("Y:");
        JPanel2.add(deviceField4);
        deviceChannel2.setInSameLine(true);
        deviceChannel2.setOffsetNid(9);
        deviceChannel2.setLabelString("ch2");
        deviceChannel2.setBorderVisible(true);
        deviceChannel2.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel2);
        deviceChannel2.setBounds(2, 42, 463, 135);
        JPanel4.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel2.add(JPanel4);
        JPanel5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel4.add(JPanel5);
        deviceField5.setOffsetNid(10);
        deviceField5.setLabelString("Trig. Time");
        JPanel5.add(deviceField5);
        deviceField6.setOffsetNid(11);
        deviceField6.setLabelString("Freq:");
        JPanel5.add(deviceField6);
        deviceField7.setNumCols(30);
        deviceField7.setOffsetNid(12);
        deviceField7.setLabelString("X:");
        JPanel4.add(deviceField7);
        deviceField8.setNumCols(30);
        deviceField8.setOffsetNid(13);
        deviceField8.setLabelString("Y:");
        JPanel4.add(deviceField8);
        deviceChannel3.setInSameLine(true);
        deviceChannel3.setOffsetNid(14);
        deviceChannel3.setLabelString("ch3");
        deviceChannel3.setBorderVisible(true);
        deviceChannel3.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel3);
        deviceChannel3.setBounds(2, 42, 463, 135);
        JPanel6.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel3.add(JPanel6);
        JPanel7.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel6.add(JPanel7);
        deviceField9.setOffsetNid(15);
        deviceField9.setLabelString("Trig. Time");
        JPanel7.add(deviceField9);
        deviceField10.setOffsetNid(16);
        deviceField10.setLabelString("Freq:");
        JPanel7.add(deviceField10);
        deviceField11.setNumCols(30);
        deviceField11.setOffsetNid(17);
        deviceField11.setLabelString("X:");
        JPanel6.add(deviceField11);
        deviceField12.setNumCols(30);
        deviceField12.setOffsetNid(18);
        deviceField12.setLabelString("Y:");
        JPanel6.add(deviceField12);
        deviceChannel4.setInSameLine(true);
        deviceChannel4.setOffsetNid(19);
        deviceChannel4.setLabelString("ch4");
        deviceChannel4.setBorderVisible(true);
        deviceChannel4.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel4);
        deviceChannel4.setBounds(2, 42, 463, 135);
        JPanel8.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel4.add(JPanel8);
        JPanel9.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel8.add(JPanel9);
        deviceField13.setOffsetNid(20);
        deviceField13.setLabelString("Trig. Time");
        JPanel9.add(deviceField13);
        deviceField14.setOffsetNid(21);
        deviceField14.setLabelString("Freq:");
        JPanel9.add(deviceField14);
        deviceField15.setNumCols(30);
        deviceField15.setOffsetNid(22);
        deviceField15.setLabelString("X:");
        JPanel8.add(deviceField15);
        deviceField16.setNumCols(30);
        deviceField16.setOffsetNid(23);
        deviceField16.setLabelString("Y:");
        JPanel8.add(deviceField16);
        deviceChannel5.setInSameLine(true);
        deviceChannel5.setOffsetNid(24);
        deviceChannel5.setLabelString("ch5");
        deviceChannel5.setBorderVisible(true);
        deviceChannel5.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel5);
        deviceChannel5.setBounds(2, 42, 463, 135);
        JPanel10.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel5.add(JPanel10);
        JPanel11.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel10.add(JPanel11);
        deviceField17.setOffsetNid(25);
        deviceField17.setLabelString("Trig. Time");
        JPanel11.add(deviceField17);
        deviceField18.setOffsetNid(26);
        deviceField18.setLabelString("Freq:");
        JPanel11.add(deviceField18);
        deviceField19.setNumCols(30);
        deviceField19.setOffsetNid(27);
        deviceField19.setLabelString("X:");
        JPanel10.add(deviceField19);
        deviceField20.setNumCols(30);
        deviceField20.setOffsetNid(28);
        deviceField20.setLabelString("Y:");
        JPanel10.add(deviceField20);
        deviceChannel6.setInSameLine(true);
        deviceChannel6.setOffsetNid(29);
        deviceChannel6.setLabelString("ch6");
        deviceChannel6.setBorderVisible(true);
        deviceChannel6.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel6);
        deviceChannel6.setBounds(2, 42, 463, 135);
        JPanel12.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel6.add(JPanel12);
        JPanel13.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel12.add(JPanel13);
        deviceField21.setOffsetNid(30);
        deviceField21.setLabelString("Trig. Time");
        JPanel13.add(deviceField21);
        deviceField22.setOffsetNid(31);
        deviceField22.setLabelString("Freq:");
        JPanel13.add(deviceField22);
        deviceField23.setNumCols(30);
        deviceField23.setOffsetNid(32);
        deviceField23.setLabelString("X:");
        JPanel12.add(deviceField23);
        deviceField24.setNumCols(30);
        deviceField24.setOffsetNid(33);
        deviceField24.setLabelString("Y:");
        JPanel12.add(deviceField24);
        deviceChannel7.setInSameLine(true);
        deviceChannel7.setOffsetNid(34);
        deviceChannel7.setLabelString("ch7");
        deviceChannel7.setBorderVisible(true);
        deviceChannel7.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel7);
        deviceChannel7.setBounds(2, 42, 463, 135);
        JPanel14.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel7.add(JPanel14);
        JPanel15.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel14.add(JPanel15);
        deviceField25.setOffsetNid(35);
        deviceField25.setLabelString("Trig. Time");
        JPanel15.add(deviceField25);
        deviceField26.setOffsetNid(36);
        deviceField26.setLabelString("Freq:");
        JPanel15.add(deviceField26);
        deviceField27.setNumCols(30);
        deviceField27.setOffsetNid(37);
        deviceField27.setLabelString("X:");
        JPanel14.add(deviceField27);
        deviceField28.setNumCols(30);
        deviceField28.setOffsetNid(38);
        deviceField28.setLabelString("Y:");
        JPanel14.add(deviceField28);
        deviceChannel8.setInSameLine(true);
        deviceChannel8.setOffsetNid(39);
        deviceChannel8.setLabelString("ch8");
        deviceChannel8.setBorderVisible(true);
        deviceChannel8.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel8);
        deviceChannel8.setBounds(2, 42, 463, 135);
        JPanel18.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel8.add(JPanel18);
        JPanel19.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel18.add(JPanel19);
        deviceField33.setOffsetNid(40);
        deviceField33.setLabelString("Trig. Time");
        JPanel19.add(deviceField33);
        deviceField34.setOffsetNid(41);
        deviceField34.setLabelString("Freq:");
        JPanel19.add(deviceField34);
        deviceField35.setNumCols(30);
        deviceField35.setOffsetNid(42);
        deviceField35.setLabelString("X:");
        JPanel18.add(deviceField35);
        deviceField36.setNumCols(30);
        deviceField36.setOffsetNid(43);
        deviceField36.setLabelString("Y:");
        JPanel18.add(deviceField36);
        deviceChannel9.setInSameLine(true);
        deviceChannel9.setOffsetNid(44);
        deviceChannel9.setLabelString("ch9");
        deviceChannel9.setBorderVisible(true);
        deviceChannel9.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel9);
        deviceChannel9.setBounds(2, 42, 463, 135);
        JPanel16.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel9.add(JPanel16);
        JPanel17.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel16.add(JPanel17);
        deviceField29.setOffsetNid(45);
        deviceField29.setLabelString("Trig. Time");
        JPanel17.add(deviceField29);
        deviceField30.setOffsetNid(46);
        deviceField30.setLabelString("Freq:");
        JPanel17.add(deviceField30);
        deviceField31.setNumCols(30);
        deviceField31.setOffsetNid(47);
        deviceField31.setLabelString("X:");
        JPanel16.add(deviceField31);
        deviceField32.setNumCols(30);
        deviceField32.setOffsetNid(48);
        deviceField32.setLabelString("Y:");
        JPanel16.add(deviceField32);
        deviceChannel10.setInSameLine(true);
        deviceChannel10.setOffsetNid(49);
        deviceChannel10.setLabelString("ch10");
        deviceChannel10.setBorderVisible(true);
        deviceChannel10.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel10);
        deviceChannel10.setBounds(2, 42, 463, 135);
        JPanel20.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel10.add(JPanel20);
        JPanel21.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel20.add(JPanel21);
        deviceField37.setOffsetNid(50);
        deviceField37.setLabelString("Trig. Time");
        JPanel21.add(deviceField37);
        deviceField38.setOffsetNid(51);
        deviceField38.setLabelString("Freq:");
        JPanel21.add(deviceField38);
        deviceField39.setNumCols(30);
        deviceField39.setOffsetNid(52);
        deviceField39.setLabelString("X:");
        JPanel20.add(deviceField39);
        deviceField40.setNumCols(30);
        deviceField40.setOffsetNid(53);
        deviceField40.setLabelString("Y:");
        JPanel20.add(deviceField40);
        deviceChannel11.setInSameLine(true);
        deviceChannel11.setOffsetNid(54);
        deviceChannel11.setLabelString("ch11");
        deviceChannel11.setBorderVisible(true);
        deviceChannel11.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel11);
        deviceChannel11.setBounds(2, 42, 463, 135);
        JPanel22.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel11.add(JPanel22);
        JPanel23.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel22.add(JPanel23);
        deviceField41.setOffsetNid(55);
        deviceField41.setLabelString("Trig. Time");
        JPanel23.add(deviceField41);
        deviceField42.setOffsetNid(56);
        deviceField42.setLabelString("Freq:");
        JPanel23.add(deviceField42);
        deviceField43.setNumCols(30);
        deviceField43.setOffsetNid(57);
        deviceField43.setLabelString("X:");
        JPanel22.add(deviceField43);
        deviceField44.setNumCols(30);
        deviceField44.setOffsetNid(58);
        deviceField44.setLabelString("Y:");
        JPanel22.add(deviceField44);
        deviceChannel12.setInSameLine(true);
        deviceChannel12.setOffsetNid(59);
        deviceChannel12.setLabelString("ch12");
        deviceChannel12.setBorderVisible(true);
        deviceChannel12.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel12);
        deviceChannel12.setBounds(2, 42, 463, 135);
        JPanel24.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel12.add(JPanel24);
        JPanel25.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel24.add(JPanel25);
        deviceField45.setOffsetNid(60);
        deviceField45.setLabelString("Trig. Time");
        JPanel25.add(deviceField45);
        deviceField46.setOffsetNid(61);
        deviceField46.setLabelString("Freq:");
        JPanel25.add(deviceField46);
        deviceField47.setNumCols(30);
        deviceField47.setOffsetNid(62);
        deviceField47.setLabelString("X:");
        JPanel24.add(deviceField47);
        deviceField48.setNumCols(30);
        deviceField48.setOffsetNid(63);
        deviceField48.setLabelString("Y:");
        JPanel24.add(deviceField48);
        deviceChannel13.setInSameLine(true);
        deviceChannel13.setOffsetNid(64);
        deviceChannel13.setLabelString("ch13");
        deviceChannel13.setBorderVisible(true);
        deviceChannel13.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel13);
        deviceChannel13.setBounds(2, 42, 463, 135);
        JPanel26.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel13.add(JPanel26);
        JPanel27.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel26.add(JPanel27);
        deviceField49.setOffsetNid(65);
        deviceField49.setLabelString("Trig. Time");
        JPanel27.add(deviceField49);
        deviceField50.setOffsetNid(66);
        deviceField50.setLabelString("Freq:");
        JPanel27.add(deviceField50);
        deviceField51.setNumCols(30);
        deviceField51.setOffsetNid(67);
        deviceField51.setLabelString("X:");
        JPanel26.add(deviceField51);
        deviceField52.setNumCols(30);
        deviceField52.setOffsetNid(68);
        deviceField52.setLabelString("Y:");
        JPanel26.add(deviceField52);
        deviceChannel14.setInSameLine(true);
        deviceChannel14.setOffsetNid(69);
        deviceChannel14.setLabelString("ch14");
        deviceChannel14.setBorderVisible(true);
        deviceChannel14.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel14);
        deviceChannel14.setBounds(2, 42, 463, 135);
        JPanel28.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel14.add(JPanel28);
        JPanel29.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel28.add(JPanel29);
        deviceField53.setOffsetNid(70);
        deviceField53.setLabelString("Trig. Time");
        JPanel29.add(deviceField53);
        deviceField54.setOffsetNid(71);
        deviceField54.setLabelString("Freq:");
        JPanel29.add(deviceField54);
        deviceField55.setNumCols(30);
        deviceField55.setOffsetNid(72);
        deviceField55.setLabelString("X:");
        JPanel28.add(deviceField55);
        deviceField56.setNumCols(30);
        deviceField56.setOffsetNid(73);
        deviceField56.setLabelString("Y:");
        JPanel28.add(deviceField56);
        deviceChannel15.setInSameLine(true);
        deviceChannel15.setOffsetNid(74);
        deviceChannel15.setLabelString("ch15");
        deviceChannel15.setBorderVisible(true);
        deviceChannel15.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel15);
        deviceChannel15.setBounds(2, 42, 463, 135);
        JPanel30.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel15.add(JPanel30);
        JPanel31.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel30.add(JPanel31);
        deviceField57.setOffsetNid(75);
        deviceField57.setLabelString("Trig. Time");
        JPanel31.add(deviceField57);
        deviceField58.setOffsetNid(76);
        deviceField58.setLabelString("Freq:");
        JPanel31.add(deviceField58);
        deviceField59.setNumCols(30);
        deviceField59.setOffsetNid(77);
        deviceField59.setLabelString("X:");
        JPanel30.add(deviceField59);
        deviceField60.setNumCols(30);
        deviceField60.setOffsetNid(78);
        deviceField60.setLabelString("Y:");
        JPanel30.add(deviceField60);
        deviceChannel16.setInSameLine(true);
        deviceChannel16.setOffsetNid(79);
        deviceChannel16.setLabelString("ch16");
        deviceChannel16.setBorderVisible(true);
        deviceChannel16.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel16);
        deviceChannel16.setBounds(2, 42, 463, 135);
        JPanel32.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel16.add(JPanel32);
        JPanel33.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel32.add(JPanel33);
        deviceField61.setOffsetNid(80);
        deviceField61.setLabelString("Trig. Time");
        JPanel33.add(deviceField61);
        deviceField62.setOffsetNid(81);
        deviceField62.setLabelString("Freq:");
        JPanel33.add(deviceField62);
        deviceField63.setNumCols(30);
        deviceField63.setOffsetNid(82);
        deviceField63.setLabelString("X:");
        JPanel32.add(deviceField63);
        deviceField64.setNumCols(30);
        deviceField64.setOffsetNid(83);
        deviceField64.setLabelString("Y:");
        JPanel32.add(deviceField64);
        deviceChannel17.setInSameLine(true);
        deviceChannel17.setOffsetNid(84);
        deviceChannel17.setLabelString("ch17");
        deviceChannel17.setBorderVisible(true);
        deviceChannel17.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel17);
        deviceChannel17.setBounds(2, 42, 463, 135);
        JPanel1.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel17.add(JPanel1);
        JPanel34.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel1.add(JPanel34);
        deviceField65.setOffsetNid(85);
        deviceField65.setLabelString("Trig. Time");
        JPanel34.add(deviceField65);
        deviceField66.setOffsetNid(86);
        deviceField66.setLabelString("Freq:");
        JPanel34.add(deviceField66);
        deviceField67.setNumCols(30);
        deviceField67.setOffsetNid(87);
        deviceField67.setLabelString("X:");
        JPanel1.add(deviceField67);
        deviceField68.setNumCols(30);
        deviceField68.setOffsetNid(88);
        deviceField68.setLabelString("Y:");
        JPanel1.add(deviceField68);
        deviceChannel18.setInSameLine(true);
        deviceChannel18.setOffsetNid(89);
        deviceChannel18.setLabelString("ch18");
        deviceChannel18.setBorderVisible(true);
        deviceChannel18.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel18);
        deviceChannel18.setBounds(2, 42, 463, 135);
        JPanel35.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel18.add(JPanel35);
        JPanel36.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel35.add(JPanel36);
        deviceField69.setOffsetNid(90);
        deviceField69.setLabelString("Trig. Time");
        JPanel36.add(deviceField69);
        deviceField70.setOffsetNid(91);
        deviceField70.setLabelString("Freq:");
        JPanel36.add(deviceField70);
        deviceField71.setNumCols(30);
        deviceField71.setOffsetNid(92);
        deviceField71.setLabelString("X:");
        JPanel35.add(deviceField71);
        deviceField72.setNumCols(30);
        deviceField72.setOffsetNid(93);
        deviceField72.setLabelString("Y:");
        JPanel35.add(deviceField72);
        deviceChannel19.setInSameLine(true);
        deviceChannel19.setOffsetNid(94);
        deviceChannel19.setLabelString("ch19");
        deviceChannel19.setBorderVisible(true);
        deviceChannel19.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel19);
        deviceChannel19.setBounds(2, 42, 463, 135);
        JPanel37.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel19.add(JPanel37);
        JPanel38.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel37.add(JPanel38);
        deviceField73.setOffsetNid(95);
        deviceField73.setLabelString("Trig. Time");
        JPanel38.add(deviceField73);
        deviceField74.setOffsetNid(96);
        deviceField74.setLabelString("Freq:");
        JPanel38.add(deviceField74);
        deviceField75.setNumCols(30);
        deviceField75.setOffsetNid(97);
        deviceField75.setLabelString("X:");
        JPanel37.add(deviceField75);
        deviceField76.setNumCols(30);
        deviceField76.setOffsetNid(98);
        deviceField76.setLabelString("Y:");
        JPanel37.add(deviceField76);
        deviceChannel20.setInSameLine(true);
        deviceChannel20.setOffsetNid(99);
        deviceChannel20.setLabelString("ch20");
        deviceChannel20.setBorderVisible(true);
        deviceChannel20.setLayout(new BorderLayout(0, 0));
        JTabbedPane1.add(deviceChannel20);
        deviceChannel20.setBounds(2, 42, 463, 135);
        JPanel39.setLayout(new GridLayout(3, 1, 0, 0));
        deviceChannel20.add(JPanel39);
        JPanel40.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel39.add(JPanel40);
        deviceField77.setOffsetNid(100);
        deviceField77.setLabelString("Trig. Time");
        JPanel40.add(deviceField77);
        deviceField78.setOffsetNid(101);
        deviceField78.setLabelString("Freq:");
        JPanel40.add(deviceField78);
        deviceField79.setNumCols(30);
        deviceField79.setOffsetNid(102);
        deviceField79.setLabelString("X:");
        JPanel39.add(deviceField79);
        deviceField80.setNumCols(30);
        deviceField80.setOffsetNid(103);
        deviceField80.setLabelString("Y:");
        JPanel39.add(deviceField80);
        JTabbedPane1.setSelectedIndex(0);
        JTabbedPane1.setSelectedComponent(deviceChannel1);
        JTabbedPane1.setTitleAt(0, "1  ");
        JTabbedPane1.setTitleAt(1, "2  ");
        JTabbedPane1.setTitleAt(2, "3  ");
        JTabbedPane1.setTitleAt(3, "4  ");
        JTabbedPane1.setTitleAt(4, "5  ");
        JTabbedPane1.setTitleAt(5, "6  ");
        JTabbedPane1.setTitleAt(6, "7  ");
        JTabbedPane1.setTitleAt(7, "8  ");
        JTabbedPane1.setTitleAt(8, "9  ");
        JTabbedPane1.setTitleAt(9, "10  ");
        JTabbedPane1.setTitleAt(10, "11  ");
        JTabbedPane1.setTitleAt(11, "12  ");
        JTabbedPane1.setTitleAt(12, "13  ");
        JTabbedPane1.setTitleAt(13, "14  ");
        JTabbedPane1.setTitleAt(14, "15  ");
        JTabbedPane1.setTitleAt(15, "16  ");
        JTabbedPane1.setTitleAt(16, "17");
        JTabbedPane1.setTitleAt(17, "18");
        JTabbedPane1.setTitleAt(18, "19");
        JTabbedPane1.setTitleAt(19, "20");
        // }}
    }

    public VMEWavesSetup(){
        this((JFrame)null);
    }

    public VMEWavesSetup(String sTitle){
        this();
        setTitle(sTitle);
    }

    public void setVisible(boolean b) {
        if(b) setLocation(50, 50);
        super.setVisible(b);
    }

    static public void main(String args[]) {
        (new VMEWavesSetup()).setVisible(true);
    }

    public void addNotify() {
        // Record the size of the window prior to calling parents addNotify.
        Dimension size = getSize();
        super.addNotify();
        if(frameSizeAdjusted) return;
        frameSizeAdjusted = true;
        // Adjust size of frame according to the insets
        Insets insets = getInsets();
        setSize(insets.left + insets.right + size.width, insets.top + insets.bottom + size.height);
    }
    // Used by addNotify
    boolean                 frameSizeAdjusted = false;
    // {{DECLARE_CONTROLS
    DeviceField             comment           = new DeviceField();
    DeviceField             base_freq         = new DeviceField();
    DeviceField             vme_ip            = new DeviceField();
    DeviceButtons           deviceButtons1    = new DeviceButtons();
    DeviceDispatch          deviceDispatch1   = new DeviceDispatch();
    javax.swing.JTabbedPane JTabbedPane1      = new javax.swing.JTabbedPane();
    DeviceChannel           deviceChannel1    = new DeviceChannel();
    javax.swing.JPanel      JPanel2           = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel3           = new javax.swing.JPanel();
    DeviceField             deviceField1      = new DeviceField();
    DeviceField             deviceField2      = new DeviceField();
    DeviceField             deviceField3      = new DeviceField();
    DeviceField             deviceField4      = new DeviceField();
    DeviceChannel           deviceChannel2    = new DeviceChannel();
    javax.swing.JPanel      JPanel4           = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel5           = new javax.swing.JPanel();
    DeviceField             deviceField5      = new DeviceField();
    DeviceField             deviceField6      = new DeviceField();
    DeviceField             deviceField7      = new DeviceField();
    DeviceField             deviceField8      = new DeviceField();
    DeviceChannel           deviceChannel3    = new DeviceChannel();
    javax.swing.JPanel      JPanel6           = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel7           = new javax.swing.JPanel();
    DeviceField             deviceField9      = new DeviceField();
    DeviceField             deviceField10     = new DeviceField();
    DeviceField             deviceField11     = new DeviceField();
    DeviceField             deviceField12     = new DeviceField();
    DeviceChannel           deviceChannel4    = new DeviceChannel();
    javax.swing.JPanel      JPanel8           = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel9           = new javax.swing.JPanel();
    DeviceField             deviceField13     = new DeviceField();
    DeviceField             deviceField14     = new DeviceField();
    DeviceField             deviceField15     = new DeviceField();
    DeviceField             deviceField16     = new DeviceField();
    DeviceChannel           deviceChannel5    = new DeviceChannel();
    javax.swing.JPanel      JPanel10          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel11          = new javax.swing.JPanel();
    DeviceField             deviceField17     = new DeviceField();
    DeviceField             deviceField18     = new DeviceField();
    DeviceField             deviceField19     = new DeviceField();
    DeviceField             deviceField20     = new DeviceField();
    DeviceChannel           deviceChannel6    = new DeviceChannel();
    javax.swing.JPanel      JPanel12          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel13          = new javax.swing.JPanel();
    DeviceField             deviceField21     = new DeviceField();
    DeviceField             deviceField22     = new DeviceField();
    DeviceField             deviceField23     = new DeviceField();
    DeviceField             deviceField24     = new DeviceField();
    DeviceChannel           deviceChannel7    = new DeviceChannel();
    javax.swing.JPanel      JPanel14          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel15          = new javax.swing.JPanel();
    DeviceField             deviceField25     = new DeviceField();
    DeviceField             deviceField26     = new DeviceField();
    DeviceField             deviceField27     = new DeviceField();
    DeviceField             deviceField28     = new DeviceField();
    DeviceChannel           deviceChannel8    = new DeviceChannel();
    javax.swing.JPanel      JPanel18          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel19          = new javax.swing.JPanel();
    DeviceField             deviceField33     = new DeviceField();
    DeviceField             deviceField34     = new DeviceField();
    DeviceField             deviceField35     = new DeviceField();
    DeviceField             deviceField36     = new DeviceField();
    DeviceChannel           deviceChannel9    = new DeviceChannel();
    javax.swing.JPanel      JPanel16          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel17          = new javax.swing.JPanel();
    DeviceField             deviceField29     = new DeviceField();
    DeviceField             deviceField30     = new DeviceField();
    DeviceField             deviceField31     = new DeviceField();
    DeviceField             deviceField32     = new DeviceField();
    DeviceChannel           deviceChannel10   = new DeviceChannel();
    javax.swing.JPanel      JPanel20          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel21          = new javax.swing.JPanel();
    DeviceField             deviceField37     = new DeviceField();
    DeviceField             deviceField38     = new DeviceField();
    DeviceField             deviceField39     = new DeviceField();
    DeviceField             deviceField40     = new DeviceField();
    DeviceChannel           deviceChannel11   = new DeviceChannel();
    javax.swing.JPanel      JPanel22          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel23          = new javax.swing.JPanel();
    DeviceField             deviceField41     = new DeviceField();
    DeviceField             deviceField42     = new DeviceField();
    DeviceField             deviceField43     = new DeviceField();
    DeviceField             deviceField44     = new DeviceField();
    DeviceChannel           deviceChannel12   = new DeviceChannel();
    javax.swing.JPanel      JPanel24          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel25          = new javax.swing.JPanel();
    DeviceField             deviceField45     = new DeviceField();
    DeviceField             deviceField46     = new DeviceField();
    DeviceField             deviceField47     = new DeviceField();
    DeviceField             deviceField48     = new DeviceField();
    DeviceChannel           deviceChannel13   = new DeviceChannel();
    javax.swing.JPanel      JPanel26          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel27          = new javax.swing.JPanel();
    DeviceField             deviceField49     = new DeviceField();
    DeviceField             deviceField50     = new DeviceField();
    DeviceField             deviceField51     = new DeviceField();
    DeviceField             deviceField52     = new DeviceField();
    DeviceChannel           deviceChannel14   = new DeviceChannel();
    javax.swing.JPanel      JPanel28          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel29          = new javax.swing.JPanel();
    DeviceField             deviceField53     = new DeviceField();
    DeviceField             deviceField54     = new DeviceField();
    DeviceField             deviceField55     = new DeviceField();
    DeviceField             deviceField56     = new DeviceField();
    DeviceChannel           deviceChannel15   = new DeviceChannel();
    javax.swing.JPanel      JPanel30          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel31          = new javax.swing.JPanel();
    DeviceField             deviceField57     = new DeviceField();
    DeviceField             deviceField58     = new DeviceField();
    DeviceField             deviceField59     = new DeviceField();
    DeviceField             deviceField60     = new DeviceField();
    DeviceChannel           deviceChannel16   = new DeviceChannel();
    javax.swing.JPanel      JPanel32          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel33          = new javax.swing.JPanel();
    DeviceField             deviceField61     = new DeviceField();
    DeviceField             deviceField62     = new DeviceField();
    DeviceField             deviceField63     = new DeviceField();
    DeviceField             deviceField64     = new DeviceField();
    DeviceChannel           deviceChannel17   = new DeviceChannel();
    javax.swing.JPanel      JPanel1           = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel34          = new javax.swing.JPanel();
    DeviceField             deviceField65     = new DeviceField();
    DeviceField             deviceField66     = new DeviceField();
    DeviceField             deviceField67     = new DeviceField();
    DeviceField             deviceField68     = new DeviceField();
    DeviceChannel           deviceChannel18   = new DeviceChannel();
    javax.swing.JPanel      JPanel35          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel36          = new javax.swing.JPanel();
    DeviceField             deviceField69     = new DeviceField();
    DeviceField             deviceField70     = new DeviceField();
    DeviceField             deviceField71     = new DeviceField();
    DeviceField             deviceField72     = new DeviceField();
    DeviceChannel           deviceChannel19   = new DeviceChannel();
    javax.swing.JPanel      JPanel37          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel38          = new javax.swing.JPanel();
    DeviceField             deviceField73     = new DeviceField();
    DeviceField             deviceField74     = new DeviceField();
    DeviceField             deviceField75     = new DeviceField();
    DeviceField             deviceField76     = new DeviceField();
    DeviceChannel           deviceChannel20   = new DeviceChannel();
    javax.swing.JPanel      JPanel39          = new javax.swing.JPanel();
    javax.swing.JPanel      JPanel40          = new javax.swing.JPanel();
    DeviceField             deviceField77     = new DeviceField();
    DeviceField             deviceField78     = new DeviceField();
    DeviceField             deviceField79     = new DeviceField();
    DeviceField             deviceField80     = new DeviceField();
    // }}
}
