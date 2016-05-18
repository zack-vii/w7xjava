package jScope;

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
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jScope.ColorMap.ColorProfile;

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
    JCheckBox                            bitClip;
    JPanel                               bitOptionPanel, colorMapPanel;
    JComboBox                            cmComboBox;
    private final Vector<ActionListener> colorMapListener = new Vector<ActionListener>();
    ColorProfile                         colorProfile;
    byte                                 colorTables[];
    ColorPalette                         cp;
    boolean                              is16BitImage     = false;
    JTextField                           minVal, maxVal;
    String                               nameColorTables[];
    JButton                              ok, apply, cancel;
    JSlider                              shiftSlider;
    // WaveformEditor weR, weG, weB;
    Waveform                             wave             = null;

    @SuppressWarnings("unchecked")
    ColorMapDialog(final Component parent, String colorPaletteFile){
        super(ColorMapDialog.getWindow(parent), "Color Palette");
        if(colorPaletteFile == null) colorPaletteFile = System.getProperty("user.home") + File.separator + "jScope" + File.separator + "colors1.tbl";
        this.readColorPalette(colorPaletteFile);
        this.getContentPane().setLayout(new GridLayout(3, 1));
        final JPanel pan1 = new JPanel();
        // pan1.setLayout(new GridLayout(2, 1));
        final JPanel pan2 = new JPanel();
        /*
            pan2.add(new JLabel("MIN : "));
            pan2.add(minVal = new JTextField(6));
            pan2.add(new JLabel("MAX : "));
            pan2.add(maxVal = new JTextField(6));
         */
        pan2.add(this.cmComboBox = new JComboBox());
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
        this.colorProfile = new ColorProfile((ColorMap)this.cmComboBox.getSelectedItem());
        this.cmComboBox.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent ev) {
                final ColorMap cm = (ColorMap)ev.getItem();
                ColorMapDialog.this.cp.setColormap(cm.colors);
                ColorMapDialog.this.wave.applyColorMap(cm);
            }
        });
        if(this.colorProfile == null) this.colorProfile = new ColorProfile(new ColorMap());
        this.cp = new ColorPalette(this.colorProfile.colorMap.colors);
        this.getContentPane().add(this.cp);
        pan1.add(pan2);
        this.bitOptionPanel = new JPanel();
        this.bitOptionPanel.setBorder(BorderFactory.createTitledBorder("16 bit  Option"));
        this.bitOptionPanel.add(this.shiftSlider = new JSlider(-8, 8, 0));
        this.shiftSlider.setName("Bit Offset");
        this.shiftSlider.setMajorTickSpacing(1);
        this.shiftSlider.setPaintTicks(true);
        this.shiftSlider.setPaintLabels(true);
        this.shiftSlider.setSnapToTicks(true);
        this.shiftSlider.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(final ChangeEvent e) {
                final JSlider source = (JSlider)e.getSource();
                if(!source.getValueIsAdjusting()){
                    ColorMapDialog.this.wave.setFrameBitShift(ColorMapDialog.this.shiftSlider.getValue(), ColorMapDialog.this.bitClip.isSelected());
                }
            }
        });
        this.bitOptionPanel.add(this.bitClip = new JCheckBox("Bit Clip"));
        this.bitClip.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(final ItemEvent e) {
                ColorMapDialog.this.wave.setFrameBitShift(ColorMapDialog.this.shiftSlider.getValue(), ColorMapDialog.this.bitClip.isSelected());
            }
        });
        final JPanel pan4 = new JPanel();
        pan4.add(this.ok = new JButton("Ok"));
        this.ok.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                // if (ColorMapDialog.this.wave.IsImage())
                {
                    final ColorProfile cp;
                    if(ColorMapDialog.this.is16BitImage) cp = new ColorProfile((ColorMap)ColorMapDialog.this.cmComboBox.getSelectedItem(), ColorMapDialog.this.shiftSlider.getValue(), ColorMapDialog.this.bitClip.isSelected());
                    else cp = new ColorProfile((ColorMap)ColorMapDialog.this.cmComboBox.getSelectedItem());
                    ColorMapDialog.this.wave.setColorProfile(cp);
                    ColorMapDialog.this.setVisible(false);
                }
            }
        });
        pan4.add(this.cancel = new JButton("Cancel"));
        this.cancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                {
                    ColorMapDialog.this.wave.setColorProfile(ColorMapDialog.this.colorProfile);
                    ColorMapDialog.this.setVisible(false);
                    if(ColorMapDialog.this.is16BitImage){
                        ColorMapDialog.this.bitClip.setSelected(ColorMapDialog.this.colorProfile.bitClip);
                        ColorMapDialog.this.shiftSlider.setValue(ColorMapDialog.this.colorProfile.bitShift);
                    }
                }
            }
        });
        this.getContentPane().add(pan1);
        this.getContentPane().add(pan4);
        this.pack();
        this.setSize(330, 350);
    }

    public void addColorMapListener(final ActionListener l) {
        this.colorMapListener.addElement(l);
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
                final InputStream pis = this.getClass().getClassLoader().getResourceAsStream("colors1.tbl");
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

    public void setWave(final Waveform wave) {
        this.wave = wave;
        this.colorProfile = new ColorProfile(wave.getColorProfile());
        this.cmComboBox.setSelectedItem(this.colorProfile.colorMap);
        if(wave.frames != null && wave.frames.getFrameType() == FrameData.BITMAP_IMAGE_16){
            if(!this.is16BitImage){
                this.getContentPane().setLayout(new GridLayout(4, 1));
                this.getContentPane().add(this.bitOptionPanel, 2);
                this.setSize(330, 350);
            }
            this.is16BitImage = true;
            this.shiftSlider.setValue(this.colorProfile.bitShift);
            this.bitClip.setSelected(this.colorProfile.bitClip);
        }else{
            this.is16BitImage = false;
            this.getContentPane().remove(this.bitOptionPanel);
            this.getContentPane().setLayout(new GridLayout(3, 1));
            this.setSize(330, 250);
        }
    }
}
