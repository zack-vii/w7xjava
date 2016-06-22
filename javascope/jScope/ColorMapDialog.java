package jscope;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jscope.ColorMap.ColorProfile;

@SuppressWarnings("serial")
final public class ColorMapDialog extends JDialog{
    public class ColorPalette extends JPanel{
        Color colors[];

        ColorPalette(final Color colors[]){
            this.setBorder(BorderFactory.createLoweredBevelBorder());
            this.setColormap(colors);
        }

        @Override
        public void paintComponent(final Graphics g) {
            final Dimension d = this.getSize();
            final float dx = (float)d.width / this.colors.length;
            int x;
            int width;
            for(int i = 0; i < this.colors.length; i++){
                x = (int)(i * dx + 0.5);
                width = (int)(x + dx + 0.5);
                g.setColor(this.colors[i]);
                g.fillRect(x, 0, width, d.height);
            }
            super.paintBorder(g);
        }

        public void setColormap(final Color colors[]) {
            this.colors = colors;
            this.repaint();
        }
    }

    private static Window getWindow(Component parent) {
        while(!(parent instanceof Window))
            parent = parent.getParent();
        return (Window)parent;
    }
    private final JCheckBox              useRGB, bitClip;
    private final JPanel                 bitOptionPanel, jpbo;
    private final JComboBox              cmComboBox;
    private final Vector<ActionListener> colorMapListener = new Vector<ActionListener>();
    private ColorProfile                 colorProfile;
    private byte                         colorTables[];
    private final ColorPalette           cp;
    private boolean                      isBitImage       = false, is32BitImage = false;
    private String                       nameColorTables[];
    private final JButton                ok, cancel;
    private final JSlider                shiftSlider;
    private Waveform                     wave             = null;

    @SuppressWarnings("unchecked")
    ColorMapDialog(final Component parent, String colorPaletteFile){
        super(ColorMapDialog.getWindow(parent), "Color Palette");
        if(colorPaletteFile == null) colorPaletteFile = System.getProperty("user.home") + "/jScope/colors.tbl";
        this.readColorPalette(colorPaletteFile);
        this.getContentPane().setLayout(new BorderLayout());
        JPanel jp = new JPanel(new BorderLayout());
        this.add(jp, BorderLayout.NORTH);
        jp.add(this.useRGB = new JCheckBox("RGB color"), BorderLayout.CENTER);
        jp.add(jp = new JPanel(new GridLayout(2, 1)), BorderLayout.SOUTH);
        jp.add(this.cmComboBox = new JComboBox());
        final int r[] = new int[256];
        final int g[] = new int[256];
        final int b[] = new int[256];
        for(int i = 0; i < this.nameColorTables.length; i++){
            for(int j = 0; j < 256; j++){
                r[j] = 0xFF & this.colorTables[i * (256 * 3) + j];
                g[j] = 0xFF & this.colorTables[i * (256 * 3) + 256 + j];
                b[j] = 0xFF & this.colorTables[i * (256 * 3) + 256 * 2 + j];
            }
            this.cmComboBox.addItem(new ColorMap(this.nameColorTables[i], r, g, b));
        }
        this.cmComboBox.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent ev) {
                final ColorMap cm = (ColorMap)ev.getItem();
                ColorMapDialog.this.cp.setColormap(cm.colors);
                ColorMapDialog.this.wave.applyColorMap(cm);
            }
        });
        this.colorProfile = new ColorProfile((ColorMap)this.cmComboBox.getSelectedItem());
        if(this.colorProfile == null) this.colorProfile = new ColorProfile(new ColorMap());
        jp.add(this.cp = new ColorPalette(this.colorProfile.colorMap.colors), 0);
        this.add(this.bitOptionPanel = new JPanel(new BorderLayout()), BorderLayout.CENTER);
        this.jpbo = new JPanel(new BorderLayout());
        this.bitOptionPanel.add(this.jpbo, BorderLayout.CENTER);
        this.useRGB.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                ColorMapDialog.this.updateMode();
                ColorMapDialog.this.repaint();
                ColorMapDialog.this.wave.setFrameRGB(ColorMapDialog.this.useRGB.isSelected());
            }
        });
        this.jpbo.setBorder(BorderFactory.createTitledBorder("Bit Option"));
        this.jpbo.add(this.shiftSlider = new JSlider(0, 0, 0), BorderLayout.CENTER);
        this.shiftSlider.setToolTipText("Number of significant bits");
        this.shiftSlider.setMajorTickSpacing(8);
        this.shiftSlider.setMinorTickSpacing(1);
        this.shiftSlider.setPaintTicks(true);
        this.shiftSlider.setPaintLabels(true);
        this.shiftSlider.setSnapToTicks(true);
        this.shiftSlider.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(final ChangeEvent e) {
                final JSlider source = (JSlider)e.getSource();
                if(!source.getValueIsAdjusting()){
                    ColorMapDialog.this.setBitShift(ColorMapDialog.this.getBitShift());
                    ColorMapDialog.this.wave.setFrameBitShift(ColorMapDialog.this.getBitShift(), ColorMapDialog.this.bitClip.isSelected());
                }
            }
        });
        this.jpbo.add(this.bitClip = new JCheckBox("Bit Clip"), BorderLayout.EAST);
        this.bitClip.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                ColorMapDialog.this.wave.setFrameBitShift(ColorMapDialog.this.getBitShift(), ColorMapDialog.this.bitClip.isSelected());
            }
        });
        jp = new JPanel(new GridLayout(1, 0));
        jp.add(this.ok = new JButton("Ok"));
        this.ok.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                ColorMapDialog.this.setVisible(false);
            }
        });
        jp.add(this.cancel = new JButton("Cancel"));
        this.cancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                {
                    ColorMapDialog.this.setVisible(false);
                    // revert changes
                    ColorMapDialog.this.wave.setColorProfile(ColorMapDialog.this.colorProfile);
                    if(ColorMapDialog.this.isBitImage){
                        ColorMapDialog.this.useRGB.setSelected(ColorMapDialog.this.colorProfile.useRGB);
                        ColorMapDialog.this.bitClip.setSelected(ColorMapDialog.this.colorProfile.bitClip);
                        ColorMapDialog.this.setBitShift(ColorMapDialog.this.colorProfile.bitShift);
                    }
                }
            }
        });
        this.getContentPane().add(jp, BorderLayout.PAGE_END);
        this.pack();
    }

    public void addColorMapListener(final ActionListener l) {
        this.colorMapListener.addElement(l);
    }

    private final int getBitShift() {
        return ColorMapDialog.this.shiftSlider.getValue() > 0 ? ColorMapDialog.this.shiftSlider.getValue() : 1;
    }

    public ColorMap getColorMap(final String name) {
        for(int i = 0; i < this.cmComboBox.getItemCount(); i++){
            final ColorMap cm = (ColorMap)this.cmComboBox.getItemAt(i);
            if(cm.name.equals(name)) return cm;
        }
        return new ColorMap();
    }

    public void processActionEvents(final ActionEvent avtionEvent) {
        for(int i = 0; i < this.colorMapListener.size(); i++)
            this.colorMapListener.elementAt(i).actionPerformed(avtionEvent);
    }

    public void readColorPalette(final String cmap) {
        DataInputStream dis;
        try{
            try{
                final FileInputStream bin = new FileInputStream(new File(cmap));
                dis = new DataInputStream(bin);
            }catch(final IOException exc){
                final InputStream pis = this.getClass().getClassLoader().getResourceAsStream("jscope/colors.tbl");
                dis = new DataInputStream(pis);
            }
            final byte nColorTables = dis.readByte();
            this.nameColorTables = new String[nColorTables];
            final byte name[] = new byte[32];
            this.colorTables = new byte[nColorTables * 3 * 256];
            dis.readFully(this.colorTables);
            for(int i = 0; i < nColorTables; i++){
                dis.readFully(name);
                this.nameColorTables[i] = (new String(name)).trim();
            }
            dis.close();
        }catch(final Exception exc){
            System.out.println("Color map exception : " + exc);
            this.nameColorTables = new String[0];
            this.colorTables = new byte[0];
        }
    }

    public void removeMapListener(final ActionListener l) {
        this.colorMapListener.remove(l);
    }

    private final void setBitShift(final int bitShift) {
        ColorMapDialog.this.shiftSlider.setMaximum(this.wave.frames.getFrameType() * 8);
        ColorMapDialog.this.shiftSlider.setValue(bitShift > 0 ? bitShift : ColorMapDialog.this.shiftSlider.getMaximum() + bitShift);
    }

    public void setWave(final Waveform wave) {
        this.wave = wave;
        this.colorProfile = new ColorProfile(wave.getColorProfile());
        this.cmComboBox.setSelectedItem(this.colorProfile.colorMap);
        if(wave.frames != null && wave.frames.getFrameType() < 5){
            this.bitOptionPanel.setVisible(true);
            this.isBitImage = true;
            this.is32BitImage = wave.frames.getFrameType() == FrameData.BITMAP_IMAGE_32;
            this.useRGB.setVisible(this.is32BitImage);
            this.useRGB.setSelected(this.colorProfile.useRGB);
            this.bitClip.setSelected(this.colorProfile.bitClip);
            this.setBitShift(this.colorProfile.bitShift);
            final Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
            for(int i = 0; i < 9; i++){
                final Integer val = i * ColorMapDialog.this.wave.frames.getFrameType();
                labelTable.put(new Integer(i * ColorMapDialog.this.wave.frames.getFrameType()), new JLabel(val.toString()));
            }
            this.shiftSlider.setLabelTable(labelTable);
        }else{
            this.bitOptionPanel.setVisible(false);
            this.useRGB.setVisible(false);
            this.isBitImage = this.is32BitImage = false;
        }
        this.updateMode();
        this.pack();
    }

    private final void updateMode() {
        final boolean state = !(ColorMapDialog.this.is32BitImage && ColorMapDialog.this.useRGB.isSelected());
        this.jpbo.setEnabled(state);
        this.cmComboBox.setEnabled(state);
        this.shiftSlider.setEnabled(state);
        this.bitClip.setEnabled(state);
    }
}
