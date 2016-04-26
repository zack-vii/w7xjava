package jScope;

/* $Id$ */
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import javax.swing.JButton;

public abstract class jScopeBrowseSignals extends jScopeBrowseUrl{
    static final long   serialVersionUID = 32443264357561L;
    JButton             add_sig;
    JButton             add_sig_shot;
    String              prev_type        = new String("text");
    jScopeWaveContainer wave_panel;

    public jScopeBrowseSignals(){
        super(null);
        this.add_sig = new JButton("Add signal");
        this.add_sig.setSelected(true);
        this.p.add(this.add_sig);
        this.add_sig.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                jScopeBrowseSignals.this.addSignal(false);
            }
        });
        this.add_sig_shot = new JButton("Add signal & shot");
        this.add_sig_shot.setSelected(true);
        this.p.add(this.add_sig_shot);
        this.add_sig_shot.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                jScopeBrowseSignals.this.addSignal(true);
            }
        });
        this.pack();
        this.setSize(this.p.getPreferredSize().width + 50, Toolkit.getDefaultToolkit().getScreenSize().height - 80);
    }

    public void addSignal(final boolean with_shot) {
        if(this.wave_panel != null){
            final URL u = this.url_list.elementAt(this.curr_url);
            final String url_name = (u.toString());
            final String sig_path = this.getSignal(url_name);
            final String shot = (with_shot) ? this.getShot() : null;
            if(sig_path != null){
                final boolean is_image = (this.mime_type != null && this.mime_type.indexOf("image") != -1);
                this.wave_panel.AddSignal(this.getTree(), shot, "", sig_path, true, is_image);
            }
        }
    }

    abstract protected String getServerAddr();

    abstract protected String getShot();

    abstract protected String getSignal(String url);

    abstract protected String getTree();

    @Override
    protected void setPage(final URL url) throws IOException {
        super.setPage(url);
        final boolean equal = (this.prev_type == null) ? (this.mime_type == null) : (this.mime_type != null && this.prev_type.equals(this.mime_type));
        if(equal) return;
        this.prev_type = this.mime_type;
        // Assume (like browsers) that missing mime-type indicates text/html.
        if(this.mime_type == null || this.mime_type.indexOf("text") != -1){
            this.add_sig.setText("Add signal");
            this.add_sig.setEnabled(true);
            this.add_sig_shot.setText("Add signal & shot");
        }else{
            this.add_sig.setText("Add frames");
            this.add_sig.setEnabled(false);
            this.add_sig_shot.setText("Add frames & shot");
        }
    }

    public void setWaveContainer(final jScopeWaveContainer wave_panel) {
        this.wave_panel = wave_panel;
    }
}
