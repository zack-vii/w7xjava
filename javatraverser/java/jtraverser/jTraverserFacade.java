package jtraverser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import jtraverser.TreeManager.Menu;
import mds.Database;

@SuppressWarnings("serial")
public final class jTraverserFacade extends JFrame{
    private class MenuChecker implements MenuListener{
        private final JMenu jmenu;
        private final Menu  menu;

        public MenuChecker(final JMenu jmenu, final Menu menu){
            this.jmenu = jmenu;
            this.menu = menu;
        }

        @Override
        public void menuCanceled(final MenuEvent e) {}

        @Override
        public void menuDeselected(final MenuEvent e) {}

        @Override
        public void menuSelected(final MenuEvent e) {
            if(this.jmenu.isEnabled()) this.menu.checkSupport();
        }
    }
    private static final JLabel status;
    private static final String TitleNoTree = "jTraverser - no tree open";
    static{
        String builddate = "unknown";
        try{
            final Class clazz = jTraverserFacade.class;
            final String className = clazz.getSimpleName() + ".class";
            final String classPath = clazz.getResource(className).toString();
            if(classPath.startsWith("jar")){
                final String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
                final Manifest manifest = new Manifest(new URL(manifestPath).openStream());
                final Attributes attr = manifest.getMainAttributes();
                builddate = attr.getValue("Built-Date").substring(0, 10);
            }
        }catch(final Exception e){
            e.printStackTrace();
        }
        status = new JLabel(new StringBuilder(64).append("jTaverser started (Build-Date ").append(builddate).append(")").toString());
    }

    public static void stderr(final String line, final Exception exc) {
        jTraverserFacade.status.setForeground(Color.RED);
        if(line == null){
            if(exc == null){
                jTraverserFacade.status.setText("");
                return;
            }
            final String msg = exc.toString();
            jTraverserFacade.status.setText(msg);
            System.err.println(msg);
        }else if(exc == null){
            jTraverserFacade.status.setText(String.format("ERROR: %s", line));
            System.err.println(String.format("%s", line));
        }else{
            final String msg = exc.getMessage();
            jTraverserFacade.status.setText(String.format("ERROR: %s (%s)", line, msg));
            System.err.println(String.format("%s\n%s", line, msg));
        }
    }

    public static void stdout(final String line) {
        jTraverserFacade.status.setForeground(Color.BLACK);
        jTraverserFacade.status.setText(line);
    }
    private final List<JMenu> jmenus = new ArrayList<JMenu>(4);
    private final TreeManager treeman;;

    public jTraverserFacade(final String server, final String exp_name, final String shot_name, String access){
        JOptionPane.setRootFrame(this);
        this.setTitle("jTraverser - no tree open");
        int mode = Database.NORMAL;
        if(access != null){
            access = access.toLowerCase();
            if("-edit".startsWith(access)) mode = Database.EDITABLE;
            else if("-readonly".startsWith(access)) mode = Database.READONLY;
        }
        this.treeman = new TreeManager(this);
        final JMenuBar menu_bar = new JMenuBar();
        this.setJMenuBar(menu_bar);
        JMenu jmenu;
        menu_bar.add(jmenu = new JMenu("File"));
        jmenu.addMenuListener(new MenuChecker(jmenu, new TreeManager.FileMenu(this.treeman, jmenu)));
        this.jmenus.add(jmenu);
        menu_bar.add(jmenu = new JMenu("Display"));
        jmenu.addMenuListener(new MenuChecker(jmenu, new TreeManager.DisplayMenu(this.treeman, jmenu)));
        this.jmenus.add(jmenu);
        jmenu.setEnabled(false);
        menu_bar.add(jmenu = new JMenu("Modify"));
        jmenu.addMenuListener(new MenuChecker(jmenu, new TreeManager.ModifyMenu(this.treeman, jmenu)));
        this.jmenus.add(jmenu);
        jmenu.setEnabled(false);
        menu_bar.add(jmenu = new JMenu("Edit"));
        jmenu.addMenuListener(new MenuChecker(jmenu, new TreeManager.EditMenu(this.treeman, jmenu)));
        this.jmenus.add(jmenu);
        jmenu.setEnabled(false);
        menu_bar.add(jmenu = new JMenu("Extras"));
        jmenu.addMenuListener(new MenuChecker(jmenu, new TreeManager.ExtrasMenu(this.treeman, jmenu)));
        this.jmenus.add(jmenu);
        jmenu.setEnabled(false);
        this.getContentPane().add(this.treeman);
        this.getContentPane().add(jTraverserFacade.status, BorderLayout.PAGE_END);
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });
        this.pack();
        this.setVisible(true);
        if(exp_name != null) try{
            this.treeman.openTree(server, exp_name.toUpperCase(), (shot_name == null) ? -1 : Integer.parseInt(shot_name), mode);
        }catch(final Exception e1){
            e1.printStackTrace();
        }
    }

    @Override
    public Component add(final Component component) {
        return this.getContentPane().add(component);
    }

    void reportChange(final Tree tree) {
        if(tree != null){
            this.setTitle(new StringBuilder(256).append("jTraverser - ").append(tree).toString());
            this.jmenus.get(1).setEnabled(true);
            this.jmenus.get(2).setEnabled(!tree.isReadOnly());
            this.jmenus.get(3).setEnabled(tree.isEditable());
            this.jmenus.get(4).setEnabled(true);
        }else{
            this.setTitle(jTraverserFacade.TitleNoTree);
            for(final JMenu jm : this.jmenus.subList(1, 5))
                jm.setEnabled(false);
        }
    }
}
