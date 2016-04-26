package mds;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
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
import jScope.DataProvider;

final public class sshTunneling extends Thread{
    public static SecureRandomAndPad createSecureRandom() {
        /*
         * NOTE, this is how it should be done if you want good randomness, however good randomness takes time so we settle with just some low-entropy garbage here. RandomSeed seed = new RandomSeed("/dev/random", "/dev/urandom"); byte[] s =
         * seed.getBytesBlocking(20); return new SecureRandomAndPad(new SecureRandom(s));
         */
        final byte[] seed = RandomSeed.getSystemStateHash();
        return new SecureRandomAndPad(new SecureRandom(seed));
    }
    SSH2SimpleClient client;
    DataProvider     da;
    String           error_string = null;
    JFrame           f;
    JDialog          inquiry_dialog;
    String           localPort;
    int              login_status;
    String           passwd;
    JPasswordField   passwd_text;
    String           remotePort;
    String           server;
    SSH2Listener     sshListener;
    SSH2Transport    transport;
    JTextField       user_text;
    String           username;

    public sshTunneling(final JFrame f, final DataProvider da, final String ip, final String remotePort, final String user, final String localPort) throws IOException{
        this.da = da;
        this.server = ip;
        this.remotePort = remotePort;
        this.localPort = localPort;
        this.f = f;
        final int status = this.credentialsDialog(f, user);
        if(status != DataProvider.LOGIN_OK) throw(new IOException("Login not successful"));
    }

    boolean CheckPasswd(final String server, final String username, final String passwd) {
        try{
            final Socket serverSocket = new Socket(server, 22);
            this.transport = new SSH2Transport(serverSocket, sshTunneling.createSecureRandom());
            this.client = new SSH2SimpleClient(this.transport, username, passwd);
            return true;
        }catch(final Exception exc){
            this.error_string = exc.getMessage();
        }
        return false;
    }

    private int credentialsDialog(final JFrame f, final String user) {
        this.login_status = DataProvider.LOGIN_OK;
        this.inquiry_dialog = new JDialog(f, "SSH login on node : " + this.server, true);
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
                sshTunneling.this.username = sshTunneling.this.user_text.getText();
                sshTunneling.this.passwd = new String(sshTunneling.this.passwd_text.getPassword());
                if(!sshTunneling.this.CheckPasswd(sshTunneling.this.server, sshTunneling.this.username, sshTunneling.this.passwd)){
                    JOptionPane.showMessageDialog(sshTunneling.this.inquiry_dialog, "Login ERROR : " + ((sshTunneling.this.error_string != null) ? sshTunneling.this.error_string : "no further information"), "alert", JOptionPane.ERROR_MESSAGE);
                    sshTunneling.this.login_status = DataProvider.LOGIN_ERROR;
                }else{
                    sshTunneling.this.inquiry_dialog.setVisible(false);
                    sshTunneling.this.login_status = DataProvider.LOGIN_OK;
                }
            }
        });
        p.add(ok_b);
        final JButton clear_b = new JButton("Clear");
        clear_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                sshTunneling.this.user_text.setText("");
                sshTunneling.this.passwd_text.setText("");
            }
        });
        p.add(clear_b);
        final JButton cancel_b = new JButton("Cancel");
        cancel_b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                sshTunneling.this.login_status = DataProvider.LOGIN_CANCEL;
                sshTunneling.this.inquiry_dialog.setVisible(false);
            }
        });
        p.add(cancel_b);
        this.inquiry_dialog.getContentPane().add(p, "South");
        this.inquiry_dialog.pack();
        if(f != null){
            final Rectangle r = f.getBounds();
            this.inquiry_dialog.setLocation(r.x + r.width / 2 - this.inquiry_dialog.getBounds().width / 2, r.y + r.height / 2 - this.inquiry_dialog.getBounds().height / 2);
        }else{
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            this.inquiry_dialog.setLocation(screenSize.width / 2 - this.inquiry_dialog.getSize().width / 2, screenSize.height / 2 - this.inquiry_dialog.getSize().height / 2);
        }
        this.inquiry_dialog.setVisible(true);
        return this.login_status;
    }

    public void Dispose() {
        /*
         * Disconnect the transport layer gracefully
         */
        this.transport.normalDisconnect("User disconnects");
        this.sshListener.stop();
    }

    @Override
    public void finalize() {}

    @Override
    public void run() {
        try{
            final SSH2Connection con = this.client.getConnection();
            /*
             * System.out.println("127.0.0.1:" + Integer.parseInt(localPort)+ " "+server+":"+ Integer.parseInt(remotePort));
             */
            this.sshListener = con.newLocalForward("127.0.0.1", Integer.parseInt(this.localPort), this.server, Integer.parseInt(this.remotePort));
            con.setEventHandler(new SSH2ConnectionEventAdapter(){
                @Override
                public void remoteSessionConnect(final SSH2Connection connection, final String remoteAddr, final int remotePort, final SSH2Channel channel) {
                    System.out.println("OK " + remoteAddr + " " + remotePort);
                }
            });
        }catch(final Exception e){
            JOptionPane.showMessageDialog(this.f, "Exception starting ssh port forward process! " + e, "alert", JOptionPane.ERROR_MESSAGE);
        }
    }
}