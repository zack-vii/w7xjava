package mds;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import com.mindbright.jca.security.SecureRandom;
import com.mindbright.ssh2.SSH2Channel;
import com.mindbright.ssh2.SSH2Connection;
import com.mindbright.ssh2.SSH2ConnectionEventAdapter;
import com.mindbright.ssh2.SSH2Listener;
import com.mindbright.ssh2.SSH2SimpleClient;
import com.mindbright.ssh2.SSH2Transport;
import com.mindbright.util.RandomSeed;
import com.mindbright.util.SecureRandomAndPad;
import jscope.DataProvider;

final public class SshTunneling extends Thread{
    private static SecureRandomAndPad createSecureRandom() {
        /*
         * NOTE, this is how it should be done if you want good randomness, however good randomness takes time so we settle with just some low-entropy garbage here. RandomSeed seed = new RandomSeed("/dev/random", "/dev/urandom"); byte[] s =
         * seed.getBytesBlocking(20); return new SecureRandomAndPad(new SecureRandom(s));
         */
        final byte[] seed = RandomSeed.getSystemStateHash();
        return new SecureRandomAndPad(new SecureRandom(seed));
    }
    private SSH2SimpleClient client;
    private String           error_string = null;
    private final Frame      frame;
    private JDialog          inquiry_dialog;
    private final String     localPort;
    private int              login_status;
    private String           passwd;
    private JPasswordField   passwd_text;
    private final String     remotePort;
    private final String     server;
    private SSH2Listener     sshListener;
    private SSH2Transport    transport;
    private JTextField       user_text;
    private String           username;

    public SshTunneling(final Frame frame, final String ip, final String remotePort, final String user, final String localPort) throws IOException{
        this.server = ip;
        this.remotePort = remotePort;
        this.localPort = localPort;
        this.frame = frame;
        final int status = this.credentialsDialog(frame, user);
        if(status != DataProvider.LOGIN_OK) throw(new IOException("Login not successful"));
    }

    private final boolean checkPasswd(final String server, final String username, final String passwd) {
        try{
            final Socket serverSocket = new Socket(server, 22);
            this.transport = new SSH2Transport(serverSocket, SshTunneling.createSecureRandom());
            this.client = new SSH2SimpleClient(this.transport, username, passwd);
            return true;
        }catch(final Exception exc){
            this.error_string = exc.getMessage();
        }
        return false;
    }

    private final int credentialsDialog(final Frame frame, final String user) {
        this.login_status = DataProvider.LOGIN_OK;
        this.inquiry_dialog = new JDialog(frame, "SSH login on node : " + this.server, true);
        this.inquiry_dialog.getContentPane().setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.add(new JLabel("Username: "));
        this.user_text = new JTextField(15);
        p.add(this.user_text);
        if(user != null) this.user_text.setText(user);
        this.inquiry_dialog.getContentPane().add(p, "North");
        p = new JPanel();
        p.add(new JLabel("Password: "));
        this.passwd_text = new JPasswordField(15);
        this.passwd_text.setEchoChar('*');
        p.add(this.passwd_text);
        this.inquiry_dialog.getContentPane().add(p, "Center");
        p = new JPanel();
        final JButton ok_b = new JButton("Ok");
        ok_b.setDefaultCapable(true);
        ok_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                SshTunneling.this.username = SshTunneling.this.user_text.getText();
                SshTunneling.this.passwd = new String(SshTunneling.this.passwd_text.getPassword());
                if(!SshTunneling.this.checkPasswd(SshTunneling.this.server, SshTunneling.this.username, SshTunneling.this.passwd)){
                    JOptionPane.showMessageDialog(SshTunneling.this.inquiry_dialog, "Login ERROR : " + ((SshTunneling.this.error_string != null) ? SshTunneling.this.error_string : "no further information"), "alert", JOptionPane.ERROR_MESSAGE);
                    SshTunneling.this.login_status = DataProvider.LOGIN_ERROR;
                }else{
                    SshTunneling.this.inquiry_dialog.setVisible(false);
                    SshTunneling.this.login_status = DataProvider.LOGIN_OK;
                }
            }
        });
        p.add(ok_b);
        final JButton clear_b = new JButton("Clear");
        clear_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                SshTunneling.this.user_text.setText("");
                SshTunneling.this.passwd_text.setText("");
            }
        });
        p.add(clear_b);
        final JButton cancel_b = new JButton("Cancel");
        cancel_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                SshTunneling.this.login_status = DataProvider.LOGIN_CANCEL;
                SshTunneling.this.inquiry_dialog.setVisible(false);
            }
        });
        p.add(cancel_b);
        this.inquiry_dialog.getContentPane().add(p, "South");
        this.inquiry_dialog.pack();
        if(frame != null){
            final Rectangle r = frame.getBounds();
            this.inquiry_dialog.setLocation(r.x + r.width / 2 - this.inquiry_dialog.getBounds().width / 2, r.y + r.height / 2 - this.inquiry_dialog.getBounds().height / 2);
        }else{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            this.inquiry_dialog.setLocation(screenSize.width / 2 - this.inquiry_dialog.getSize().width / 2, screenSize.height / 2 - this.inquiry_dialog.getSize().height / 2);
        }
        this.inquiry_dialog.setVisible(true);
        return this.login_status;
    }

    /**
     * Disconnect the transport layer gracefully
     **/
    public final void dispose() {
        this.transport.normalDisconnect("User disconnects");
        this.sshListener.stop();
    }

    @Override
    public final void run() {
        try{
            final SSH2Connection con = this.client.getConnection();
            this.sshListener = con.newLocalForward("127.0.0.1", Integer.parseInt(this.localPort), this.server, Integer.parseInt(this.remotePort));
            con.setEventHandler(new SSH2ConnectionEventAdapter(){
                @Override
                public void remoteSessionConnect(final SSH2Connection connection, final String remoteAddr, final int remotePort, final SSH2Channel channel) {
                    System.out.println("OK " + remoteAddr + " " + remotePort);
                }
            });
        }catch(final Exception e){
            JOptionPane.showMessageDialog(this.frame, "Exception starting ssh port forward process! " + e, "alert", JOptionPane.ERROR_MESSAGE);
        }
    }
}