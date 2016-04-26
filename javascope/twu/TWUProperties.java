package twu;

/* ------------------------------------------------------------------------ */
/*
 * TWUProperties: Handling the Web-Umbrella Signal Properties. Copyright (c) 2002...2003, J.G.krom, Forschungszentrum Juelich GmbH. All rights reserved. This class handles the properties 'file' as used in the TEC Web-Umbrella. NOTE: The TWU* classes (with
 * TWU in three capitals) are written so that they can also be used in other java based applications and not only by jScope. Please keep the code of these separate of jScope specific elements. This class does not throw any exceptions; this should be left
 * so. This is because it is also used in contexts where the handling of such exceptions is somewhat troublesome. (E.g. JVMs running implicitly by matlab.) Author: Jon Krom, Forschungszentrum Juelich, Institut fuer Plasmaphysik. $Id$
 * ------------------------------------------------------------------------ Derived from (matlab-) WUProperties.java Revision 1.6 2002/02/26 hrk/jgk I removed the automatic CVS log from this file; it can be read directly from the CVS repository with
 * "cvs log".
 */
/* ------------------------------------------------------------------------ */
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class TWUProperties{
    // Allow, like many browsers do, URLs without protocol name (the
    // 'http:' bit). Assume in these cases that we want 'http:'
    // This allows. (at least in principle) other protocols.
    private static String CompleteURL(final String in) {
        if(in.indexOf("://") >= 0) return in;
        return "http:" + in;
    }

    public static void main(final String a[]) {
        TWUProperties twup;
        System.out.println();
        System.out.println(TWUProperties.revision());
        System.out.println();
        System.out.println("Web-Umbrella data from a webserver.");
        twup = new TWUProperties(
                // "//localhost/textor/all/91100/vuv/spred-a/H-I_@_102,550"); // Existing
                // "//localhost/textor/all/91100/vuv/spred-a/H-I_@_102,551"); // Non-existing
                // ""); // Non-existing
                null);
        System.out.println(twup);
        System.out.println(twup.summary());
        System.out.println();
        twup = new TWUProperties("//localhost/textor/all/91100/vuv/spred-a/H-I_@_102,550"); // Existing
        System.out.println(twup);
        System.out.println(twup.summary());
        System.out.println();
    }

    public static String revision() {
        return "$Id$";
    }
    protected Properties signalProps = new Properties();
    private URL          signalURL   = null;
    private boolean      textRead    = false;

    /* -------------------------------------------------------------------- */
    // Constructors
    public TWUProperties(final String SigURL){
        this(SigURL, null);
    }

    public TWUProperties(final String SigURL, final String user_agent){
        final String actual_user_agent = (user_agent != null) ? user_agent : "TWUProperties.java for jScope ($Revision$)";
        this.signalProps = new Properties();
        if(SigURL == null) return;
        final String fullURL = TWUProperties.CompleteURL(SigURL);
        try{
            this.signalURL = new URL(fullURL);
            final URLConnection con = this.signalURL.openConnection();
            con.setRequestProperty("User-Agent", actual_user_agent);
            final String mime_type = con.getContentType();
            // Assume (like browsers) that missing mime-type indicates text/html.
            if(mime_type == null || mime_type.indexOf("text") >= 0){
                this.signalProps.load(con.getInputStream());
                this.textRead = true;
            }
        }catch(final Exception e){
            this.signalURL = null;
            /* No need for further action; textRead will be left false. */
        }
    }

    public double averageStep() {
        final double span = this.Maximum() - this.Minimum();
        int segments = this.LengthTotal() - 1;
        if(this.Decrementing()) segments *= -1;
        return(segments == 0 ? Double.NaN : span / segments);
    }

    public boolean Decrementing() {
        final String equidistant = this.signalProps.getProperty("Equidistant");
        return (equidistant != null) && equidistant.equalsIgnoreCase("decrementing");
    }

    public int Dim0Length() {
        final String ln0str = this.signalProps.getProperty("Length.dimension.0");
        final Integer d0l = Integer.valueOf(ln0str == null ? "0" : ln0str);
        return d0l.intValue();
    }

    public int Dim1Length() {
        final String ln1str = this.signalProps.getProperty("Length.dimension.1");
        final Integer d1l = Integer.valueOf(ln1str == null ? "0" : ln1str);
        return d1l.intValue();
    }

    public int Dimensions() {
        final String dimstr = this.signalProps.getProperty("Dimensions");
        final Integer dim = Integer.valueOf(dimstr == null ? "0" : dimstr);
        return dim.intValue();
    }

    public boolean equals(final String other_url) {
        try{
            return TWUProperties.CompleteURL(other_url).equals(this.signalURL.toString());
        }catch(final Exception e){
            return false;
        }
    }

    public boolean Equidistant() {
        return (this.Incrementing() || this.Decrementing()) && this.signalProps.getProperty("Signal.Minimum") != null && this.signalProps.getProperty("Signal.Maximum") != null;
    }

    public String FQAbscissa0Name() {
        final String abs = this.signalProps.getProperty("Abscissa.URL.0");
        return((abs == null || abs.equalsIgnoreCase("None")) ? null : abs);
    }

    public String FQAbscissa1Name() {
        final String abs = this.signalProps.getProperty("Abscissa.URL.1");
        return((abs == null || abs.equalsIgnoreCase("None")) ? null : abs);
    }

    public String FQAbscissaName() {
        final String abs = this.signalProps.getProperty("Abscissa.URL.0");
        return((abs == null || abs.equalsIgnoreCase("None")) ? null : abs);
    }

    public String FQBulkName() {
        return this.signalProps.getProperty("Bulkfile.URL");
    }

    public String FQSignalName() {
        return this.signalProps.getProperty("SignalURL");
    }

    /* -------------------------------------------------------------------- */
    // A generic getproperties method.
    public String getProperty(final String keyword) {
        return this.signalProps.getProperty(keyword);
    }

    public boolean hasAbscissa() {
        return this.hasAbscissa0();
    }

    public boolean hasAbscissa0() {
        final String abscissa = this.signalProps.getProperty("Abscissa.URL.0");
        return((abscissa == null) ? false : (!abscissa.equalsIgnoreCase("None")));
    }

    public boolean hasAbscissa1() {
        final String abscissa = this.signalProps.getProperty("Abscissa.URL.1");
        return((abscissa == null) ? false : (!abscissa.equalsIgnoreCase("None")));
    }

    public boolean Incrementing() {
        final String equidistant = this.signalProps.getProperty("Equidistant");
        return (equidistant != null) && equidistant.equalsIgnoreCase("incrementing");
    }

    public int LengthTotal() {
        final String ltstr = this.signalProps.getProperty("Length.total");
        final Integer lt = Integer.valueOf(ltstr == null ? "0" : ltstr);
        return lt.intValue();
    }

    public double Maximum() {
        final String maxi = this.signalProps.getProperty("Signal.Maximum");
        Double max = new Double(Double.NaN);
        if(maxi != null) max = Double.valueOf(maxi);
        return max.doubleValue();
    }

    public double Minimum() {
        final String mini = this.signalProps.getProperty("Signal.Minimum");
        Double min = new Double(Double.NaN);
        if(mini != null) min = Double.valueOf(mini);
        return min.doubleValue();
    }

    public boolean notEquals(final String other_url) {
        return !this.equals(other_url);
    }

    public String SignalName() {
        return this.signalProps.getProperty("FullSignalName");
    }

    public String summary() {
        return "Valid                  : " + this.valid() + "\n" + "Title                  : " + this.Title() + "\n" + "Signal Name            : " + this.SignalName() + "\n" + "Full Signal Name       : " + this.FQSignalName() + "\n" + "Bulk File Name         : " + this.FQBulkName() + "\n" + "Dimensions             : " + this.Dimensions() + "\n" + "Total Length           : " + this.LengthTotal() + "\n" + "Equidistant            : " + this.Equidistant() + "\n" + "Signal.Minimum         : " + this.Minimum() + "\n" + "Signal.Maximum         : " + this.Maximum() + "\n" + "averageStep            : " + this.averageStep() + "\n" + "Dimension [0] length   : " + this.Dim0Length() + "\n" + "Abscissa [0] File Name : " + (this.hasAbscissa0() ? this.FQAbscissa0Name() : "No abscissa_0 for this signal") + "\n" + "Dimension [1] length   : " + this.Dim1Length() + "\n" + "Abscissa [1] File Name : " + (this.hasAbscissa1() ? this.FQAbscissa1Name() : "No abscissa_1 for this signal") + "\n";
    }

    public String Title() {
        String title = this.signalProps.getProperty("Title");
        if(title == null) title = this.signalProps.getProperty("SignalName");
        return title;
    }

    /* -------------------------------------------------------------------- */
    @Override
    public String toString() {
        return this.signalURL == null ? "" : this.signalURL.toString();
    }

    public String Units() {
        final String unitstr = this.signalProps.getProperty("Unit");
        return((unitstr == null || unitstr.equalsIgnoreCase("None")) ? "" : unitstr);
    }

    /* -------------------------------------------------------------------- */
    // Accessors:
    public boolean valid() {
        // Should handle this pseudo-numerically
        final String version = this.signalProps.getProperty("TWU.properties.version");
        return this.textRead && version != null && (version.equals("0.7") || version.equals("0.8"));
    }
}
/* ------------------------------------------------------------------------ */
// End of $Id$
/* ------------------------------------------------------------------------ */
