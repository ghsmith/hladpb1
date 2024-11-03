package edu.emory.pathology.hladpb1.webservices;

import edu.emory.pathology.hladpb1.imgtdb.AlleleFinder;
import edu.emory.pathology.hladpb1.imgtdb.HypervariableRegionFinder;
import edu.emory.pathology.hladpb1.imgtdb.ReagentLotFinder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;

/**
 * This class returns session information. All updates to to attributes are
 * persisted in the session and are disposed of when the session ends.
 * 
 * @author ghsmith
 */
@Path("session")
public class Session {

    @GET
    @Produces("application/json")
    public String getJson() {
        return SessionFilter.sessionMutex.get();
    }

    @PUT
    @Path("reset")
    @Produces("application/json")
    public void putReset(@Context HttpServletRequest request) {
        request.getSession().invalidate();
    }

    @PUT
    @Path("reagentLot")
    @Produces("application/json")
    public void putReagentLot(@Context HttpServletRequest request, String reagentLotNumber) {
        AlleleFinder alleleFinder = new AlleleFinder(request.getServletContext().getInitParameter("imgtXmlFileName"));
        HypervariableRegionFinder hypervariableRegionFinder = new HypervariableRegionFinder(request.getServletContext().getInitParameter("emoryXmlFileName"), reagentLotNumber);
        ReagentLotFinder reagentLotFinder = new ReagentLotFinder(request.getServletContext().getInitParameter("emoryXmlFileName"));
        alleleFinder.assignHypervariableRegionVariantIds(hypervariableRegionFinder);
        alleleFinder.assignHypervariableRegionVariantMatches(alleleFinder.getAlleleList().get(0).getAlleleName());
        alleleFinder.computeCompatInterpretation(hypervariableRegionFinder);
        ((HttpServletRequest)request).getSession().setAttribute("alleleFinder", alleleFinder);
        ((HttpServletRequest)request).getSession().setAttribute("hypervariableRegionFinder", hypervariableRegionFinder);
        ((HttpServletRequest)request).getSession().setAttribute("reagentLotFinder", reagentLotFinder);
        SessionFilter.alleleFinder.set(alleleFinder);
        SessionFilter.hypervariableRegionFinder.set(hypervariableRegionFinder);
        SessionFilter.reagentLotFinder.set(reagentLotFinder);
    }
    
}
