package edu.emory.pathology.hladpb1.webservices;

import edu.emory.pathology.hladpb1.imgtdb.AlleleFinder;
import edu.emory.pathology.hladpb1.imgtdb.HypervariableRegionFinder;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * This is the default NetBeans filter template. This filter ensures that the
 * appropriate finder classes are associated with the web session and also
 * inserts those 
 * 
 * @author ghsmith
 */
@WebFilter(filterName = "SessionFilter", urlPatterns = {"/resources/*"})
public class SessionFilter implements Filter {

    protected static ThreadLocal<String> sessionMutex = new ThreadLocal<>();
    protected static ThreadLocal<AlleleFinder> alleleFinder = new ThreadLocal<>();
    protected static ThreadLocal<HypervariableRegionFinder> hypervariableRegionFinder = new ThreadLocal<>();
    
    private static final boolean debug = false;

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;
    
    public SessionFilter() {
    }    
    
    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("SessionFilter:DoBeforeProcessing");
        }

        // The session mutex is used to synchronize acess to methods that update
        // attributes. Since it stores the session ID, it can also provide some
        // traceability (e.g., web service that returns the session ID to the
        // client).
        String sessionMutex;
        synchronized(this) {
            sessionMutex = (String)((HttpServletRequest)request).getAttribute("sessionMutex");
            if(sessionMutex == null) {
                sessionMutex = ((HttpServletRequest)request).getSession().getId();
                ((HttpServletRequest)request).setAttribute("sessionMutex", sessionMutex);
            }
        }
        
        synchronized(sessionMutex) {
            AlleleFinder alleleFinder = (AlleleFinder)((HttpServletRequest)request).getSession().getAttribute("alleleFinder");
            HypervariableRegionFinder hypervariableRegionFinder = (HypervariableRegionFinder)((HttpServletRequest)request).getSession().getAttribute("hypervariableRegionFinder");
            if(alleleFinder == null || hypervariableRegionFinder == null) {
                alleleFinder = new AlleleFinder(request.getServletContext().getInitParameter("imgtXmlFileName"));
                hypervariableRegionFinder = new HypervariableRegionFinder(request.getServletContext().getInitParameter("emoryXmlFileName"), request.getServletContext().getInitParameter("reagentLotNumber"));
                alleleFinder.assignHypervariableRegionVariantIds(hypervariableRegionFinder);
                alleleFinder.assignHypervariableRegionVariantMatches(alleleFinder.getAlleleList().get(0).getAlleleName());
                alleleFinder.computeCompatInterpretation(hypervariableRegionFinder);
                ((HttpServletRequest)request).getSession().setAttribute("alleleFinder", alleleFinder);
                ((HttpServletRequest)request).getSession().setAttribute("hypervariableRegionFinder", hypervariableRegionFinder);
            }
            SessionFilter.sessionMutex.set(sessionMutex);
            SessionFilter.alleleFinder.set(alleleFinder);
            SessionFilter.hypervariableRegionFinder.set(hypervariableRegionFinder);
        }
        
    }    
    
    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("SessionFilter:DoAfterProcessing");
        }
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        if (debug) {
            log("SessionFilter:doFilter()");
        }
        
        doBeforeProcessing(request, response);
        
        Throwable problem = null;
        try {
            chain.doFilter(request, response);
        } catch (Throwable t) {
            // If an exception is thrown somewhere down the filter chain,
            // we still want to execute our after processing, and then
            // rethrow the problem after that.
            problem = t;
            t.printStackTrace();
        }
        
        doAfterProcessing(request, response);

        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        if (problem != null) {
            if (problem instanceof ServletException) {
                throw (ServletException) problem;
            }
            if (problem instanceof IOException) {
                throw (IOException) problem;
            }
            sendProcessingError(problem, response);
        }
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {        
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {        
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {                
                log("SessionFilter:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("SessionFilter()");
        }
        StringBuffer sb = new StringBuffer("SessionFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }
    
    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);        
        
        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);                
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");                
                pw.print(stackTrace);                
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }
    
    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }
    
    public void log(String msg) {
        filterConfig.getServletContext().log(msg);        
    }
    
}
