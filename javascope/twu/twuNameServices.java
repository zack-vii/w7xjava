package twu;

// -----------------------------------------------------------------------------
// twuNameServices
//
// Signalnames, and some other names, within the TEC Web-Umbrella (TWU) scheme
// are based on URLs. This leads sometimes to names that are overly long and
// verbose. This class provides functions to translate to and from a full
// SignalURL and some more user orientated variants.
//
// $Id$
//
// -----------------------------------------------------------------------------
// import java.io.*;
// import java.net.*;
import java.util.StringTokenizer;
// import java.awt.*;
// import javax.swing.*;
// import java.awt.event.*;
// import java.lang.InterruptedException;
import jScope.DataProvider;
import jScope.WaveInterface;

// -----------------------------------------------------------------------------
class twuNameServices{
    private static final String default_experiment   = "textor";
    // -------------------------------------------------------------------------
    // Some default names... In practice seldomly used.
    // Might require adaptation at other sites. JGK 2003-10-23
    // -------------------------------------------------------------------------
    private static final String default_provider_url = "ipptwu.ipp.kfa-juelich.de";

    // -------------------------------------------------------------------------
    // Some feature tests.
    // -------------------------------------------------------------------------
    public static boolean catersFor(final DataProvider dp) {
        return(dp instanceof twuDataProvider);
    }

    // -------------------------------------------------------------------------
    // Take a jScope internal TWU-signal name and return its URL (its Path).
    static protected String GetSignalPath(String internalSignalURL, final long shot) {
        if(twuNameServices.IsFullURL(internalSignalURL)) return internalSignalURL;
        // Hashed_URLs
        // Check if signal path is in the format
        // //url_server_address/experiment/shotGroup/#####/signal_path
        if(twuNameServices.isHashedURL(internalSignalURL)) return twuNameServices.hashed2shot(internalSignalURL, shot);
        // If not, then it is of the old jScope internal format
        // url_server_address//group/signal_path
        // (Continue handling them; they could come out of .jscp files)
        String p_url = twuNameServices.GetURLserver(internalSignalURL);
        if(p_url == null) p_url = twuNameServices.default_provider_url;
        else internalSignalURL = internalSignalURL.substring(internalSignalURL.indexOf("//") + 2, internalSignalURL.length());
        final StringTokenizer st = new StringTokenizer(internalSignalURL, "/");
        String full_url = "http://" + p_url + "/" + twuNameServices.probableExperiment(null) + "/" + st.nextToken() + "/" + shot;
        while(st.hasMoreTokens())
            full_url += "/" + st.nextToken();
        return full_url;
    }

    static private String GetURLserver(final String in) {
        // Find the servername, if it follows the (early) jScope internal
        // convention that it is encoded before the double slash.
        int idx;
        String out = null;
        if((idx = in.indexOf("//")) != -1) out = in.substring(0, idx);
        return out;
    }

    // -------------------------------------------------------------------------
    // Take a (pseudo-)SignalURL, replace any hash-fields with the shotnumber
    public static String hashed2shot(final String hashedURL, final long shot) {
        if(hashedURL == null) return hashedURL;
        final int hashfield = hashedURL.indexOf("#");
        if(hashfield == -1) return hashedURL;
        final String full_url = hashedURL.substring(0, hashfield) + shot + hashedURL.substring(hashedURL.lastIndexOf("#") + 1);
        return full_url;
    }

    static private boolean IsFullURL(String in) {
        in = in.toLowerCase();
        return (in.startsWith("http://") || in.startsWith("//")) && in.indexOf("#") == -1;
    }

    public static boolean isHashedURL(String in) {
        in = in.toLowerCase();
        return in.startsWith("//") && in.indexOf("#") != -1;
    }

    // -------------------------------------------------------------------------
    // Some (re-)formatters.
    // -------------------------------------------------------------------------
    // Make a URL string for the "Legend" display.
    public static String legendString(final WaveInterface wi, final String signalURL, final long shot) {
        final String startOfURL = "/" + twuNameServices.probableExperiment(wi) + "/all";
        final int ix = signalURL.indexOf(startOfURL);
        if(ix > 0) return twuNameServices.hashed2shot(signalURL.substring(ix), shot);
        return twuNameServices.hashed2shot(signalURL, shot);
    }

    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------
    // Some local support functions.
    private static String probableExperiment(final WaveInterface wi) {
        if(wi != null){
            if(wi.experiment != null) return wi.experiment;
            if(wi.dp != null){
                if(twuNameServices.catersFor(wi.dp)){
                    final twuDataProvider twudp = (twuDataProvider)wi.dp;
                    final String twudp_exp = twudp.getExperiment();
                    if(twudp_exp != null) return twudp_exp;
                }
            }
        }
        // Why has "WaveInterface" sometimes a null experiment name ?
        // TODO : Try to understand what's happening here !
        // For the moment: assume a likely name for the experiment;
        // this might require adaptation at other sites. JGK 2003-07-22
        return twuNameServices.default_experiment;
    }
}
// -----------------------------------------------------------------------------
// End of: $Id$
// -----------------------------------------------------------------------------
