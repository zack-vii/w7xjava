package jScope;

/* $Id$ */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

class FontPanel extends JPanel{
    static final long serialVersionUID = 47623773223236L;
    Font              thisFont;

    public FontPanel(){
        this.thisFont = new Font("Arial", Font.PLAIN, 10);
    }

    public void changeFont(final Font font) {
        this.thisFont = font;
        this.repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 50);
    }

    // Check if the font is visible on
    // this platform
    public boolean isFontAvailable(final Font font) {
        int height;
        final Graphics g = this.getGraphics();
        final FontMetrics fm = g.getFontMetrics(font);
        height = fm.getHeight();
        return(height > 9 && height < 100);
    }

    @Override
    public void paint(final Graphics g) {
        final int w = this.getSize().width;// getWidth();
        final int h = this.getSize().height;// getHeight();
        g.setColor(Color.darkGray);
        g.setFont(this.thisFont);
        final String change = "Pick a font, size, and style to change me";
        final FontMetrics metrics = g.getFontMetrics();
        final int width = metrics.stringWidth(change);
        final int height = metrics.getHeight();
        // System.out.println(thisFont.toString());
        g.drawString(change, w / 2 - width / 2, h / 2 - height / 2);
    }
}

public class FontSelection extends JDialog implements ActionListener, ItemListener{
    static final long serialVersionUID = 4762373484632L;
    JRadioButton      application_i, waveform_i;
    String            envfonts[];
    Font              font;
    FontPanel         fontC;
    String            fontchoice       = "fontchoice";
    JLabel            fontLabel, sizeLabel, styleLabel, testLabel;
    JComboBox         fonts;
    int               index            = 0;
    jScopeFacade      main_scope;
    JButton           ok, cancel, apply;
    String            siChoice         = "10";
    String            size_l[];
    JComboBox         sizes;
    int               stChoice         = 0;
    String            style_l[];
    JComboBox         styles;

    @SuppressWarnings("unchecked")
    public FontSelection(final Frame dw, final String title){
        super(dw, title, true);
        this.main_scope = (jScopeFacade)dw;
        this.getContentPane().setLayout(new BorderLayout(5, 5));
        final JPanel topPanel = new JPanel();
        final JPanel fontPanel = new JPanel();
        final JPanel sizePanel = new JPanel();
        final JPanel stylePanel = new JPanel();
        final JPanel sizeAndStylePanel = new JPanel();
        final JPanel buttonPanel = new JPanel();
        final JPanel fontSelectionPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(5, 5));
        fontPanel.setLayout(new GridLayout(2, 1));
        sizePanel.setLayout(new GridLayout(2, 1));
        stylePanel.setLayout(new GridLayout(2, 1));
        fontSelectionPanel.setLayout(new GridLayout(2, 1));
        sizeAndStylePanel.setLayout(new BorderLayout());
        buttonPanel.setLayout(new FlowLayout());
        topPanel.add(BorderLayout.WEST, fontPanel);
        sizeAndStylePanel.add(BorderLayout.WEST, sizePanel);
        sizeAndStylePanel.add(BorderLayout.CENTER, stylePanel);
        topPanel.add(BorderLayout.CENTER, sizeAndStylePanel);
        topPanel.add(BorderLayout.SOUTH, fontSelectionPanel);
        this.getContentPane().add(BorderLayout.NORTH, topPanel);
        this.fontLabel = new JLabel();
        this.fontLabel.setText("Fonts");
        final Font newFont = this.getFont();
        this.fontLabel.setFont(newFont);
        this.fontLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fontPanel.add(this.fontLabel);
        this.sizeLabel = new JLabel();
        this.sizeLabel.setText("Sizes");
        this.sizeLabel.setFont(newFont);
        this.sizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sizePanel.add(this.sizeLabel);
        this.styleLabel = new JLabel();
        this.styleLabel.setText("Styles");
        this.styleLabel.setFont(newFont);
        this.styleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        stylePanel.add(this.styleLabel);
        final GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.envfonts = gEnv.getAvailableFontFamilyNames();
        final ButtonGroup font_selection = new ButtonGroup();
        this.application_i = new JRadioButton("Application");
        fontSelectionPanel.add(this.application_i);
        this.waveform_i = new JRadioButton("Waveform", true);
        fontSelectionPanel.add(this.waveform_i);
        font_selection.add(this.application_i);
        font_selection.add(this.waveform_i);
        this.fonts = new JComboBox(this.envfonts);
        this.fonts.addItemListener(this);
        this.fontchoice = this.envfonts[0];
        fontPanel.add(this.fonts);
        this.size_l = new String[]{"10", "12", "14", "16", "18", "20", "22", "24", "26", "28"};
        this.sizes = new JComboBox(this.size_l);
        this.sizes.addItemListener(this);
        sizePanel.add(this.sizes);
        this.style_l = new String[]{"PLAIN", "BOLD", "ITALIC", "BOLD & ITALIC"};
        this.styles = new JComboBox(this.style_l);
        this.styles.addItemListener(this);
        stylePanel.add(this.styles);
        this.fontC = new FontPanel();
        this.fontC.setBackground(Color.white);
        this.getContentPane().add(BorderLayout.CENTER, this.fontC);
        this.ok = new JButton("Ok");
        this.ok.addActionListener(this);
        buttonPanel.add(this.ok);
        this.apply = new JButton("Apply");
        this.apply.addActionListener(this);
        buttonPanel.add(this.apply);
        this.cancel = new JButton("Cancel");
        this.cancel.addActionListener(this);
        buttonPanel.add(this.cancel);
        this.getContentPane().add(BorderLayout.SOUTH, buttonPanel);
        this.pack();
        this.GetPropertiesValue();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final Object ob = e.getSource();
        if(ob == this.ok || ob == this.apply){
            if(this.waveform_i.isSelected()){
                this.font = this.GetFont();
                this.main_scope.UpdateFont();
                this.main_scope.RepaintAllWaves();
                this.main_scope.setChange(true);
            }
            if(this.application_i.isSelected()){
                this.main_scope.SetApplicationFonts(this.GetFont());
                // SwingUtilities.updateComponentTreeUI(main_scope);
            }
            if(ob == this.ok) this.setVisible(false);
        }
        if(ob == this.cancel){
            this.setVisible(false);
        }
    }

    public void fromFile(final Properties pr, final String prompt) throws IOException {
        String prop;
        if((prop = pr.getProperty(prompt)) != null){
            this.font = this.StringToFont(prop);
        }
        if(this.font != null){
            this.setFontChoice();
            this.fontC.changeFont(this.font);
        }
    }

    public Font GetFont() {
        this.fontchoice = (String)this.fonts.getSelectedItem();
        this.stChoice = this.styles.getSelectedIndex();
        this.siChoice = (String)this.sizes.getSelectedItem();
        final Integer newSize = new Integer(this.siChoice);
        final int size = newSize.intValue();
        Font f = new Font(this.fontchoice, this.stChoice, size);
        f = this.StringToFont(f.toString());
        return f;
    }

    private void GetPropertiesValue() {
        final Properties js_prop = this.main_scope.js_prop;
        String prop;
        if(js_prop == null) return;
        if((prop = js_prop.getProperty("jScope.font.application")) != null){
            this.main_scope.SetApplicationFonts(this.StringToFont(prop));
        }
        prop = js_prop.getProperty("jScope.font");
        if(prop == null) this.font = this.StringToFont(this.getFont().toString());
        else this.font = this.StringToFont(prop);
        if(this.font != null){
            this.setFontChoice();
            this.fontC.changeFont(this.font);
        }
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        if(e.getStateChange() != ItemEvent.SELECTED){ return; }
        this.fontC.changeFont(this.GetFont());
    }

    private void setFontChoice() {
        this.fonts.removeItemListener(this);
        this.styles.removeItemListener(this);
        this.sizes.removeItemListener(this);
        this.fonts.setSelectedItem(this.fontchoice);
        this.styles.setSelectedIndex(this.stChoice);
        this.sizes.setSelectedItem(this.siChoice);
        this.fonts.addItemListener(this);
        this.styles.addItemListener(this);
        this.sizes.addItemListener(this);
    }

    public Font StringToFont(final String f) {
        String style;
        int pos, i;
        if(f.indexOf("java.awt.Font[") == -1) return null;
        if(jScopeFacade.is_debug) System.out.println("Font desiderato " + f);
        this.fontchoice = f.substring(f.indexOf("family=") + 7, pos = f.indexOf(",name="));
        pos++;// Index on the string after comma before style
        style = f.substring(f.indexOf("style=", pos) + 6, pos = f.indexOf(",size", pos));
        for(i = 0; i < this.style_l.length; i++)
            if(this.style_l[i].equals(style.toUpperCase())) break;
        if(i == this.style_l.length) this.stChoice = 0;
        else this.stChoice = i;
        this.siChoice = f.substring(f.indexOf("size=") + 5, f.indexOf("]", pos));
        Font font = new Font(this.fontchoice, this.stChoice, Integer.parseInt(this.siChoice));
        // Check if selected font can be displayed
        // on the current patform
        if(!this.fontC.isFontAvailable(font)){
            // Set font to default font
            this.fontchoice = "Default";
            this.stChoice = 0;
            this.siChoice = "12";
            font = new Font(this.fontchoice, this.stChoice, Integer.parseInt(this.siChoice));
        }
        if(jScopeFacade.is_debug) System.out.println("Font ottenuto " + font);
        return font;
    }

    public void toFile(final PrintWriter out, final String prompt) {
        if(this.font != null) out.println(prompt + ": " + this.font.toString());
        out.println("");
    }
}
