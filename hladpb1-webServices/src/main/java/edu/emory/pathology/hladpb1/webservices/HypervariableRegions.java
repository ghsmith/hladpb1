package edu.emory.pathology.hladpb1.webservices;

import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * This class implements the HypervariableRegions RESTful web services.
 * 
 * @author ghsmith
 */
@Path("hypervariableRegions")
public class HypervariableRegions {

    @GET
    @Path("reagentLotNumber")
    @Produces("application/json")
    public String getJsonReagentLotNumber() {
        return SessionFilter.hypervariableRegionFinder.get().getReagentLotNumber();
    }
    
    @GET
    @Produces("application/json")
    public List<HypervariableRegion> getJson() {
        return SessionFilter.hypervariableRegionFinder.get().getHypervariableRegionList();
    }

    @GET
    @Path("{hypervariableRegionName}")
    @Produces("application/json")
    public HypervariableRegion getJsonAllele(@PathParam("hypervariableRegionName") String hypervariableRegionName) {
        return SessionFilter.hypervariableRegionFinder.get().getHypervariableRegion(hypervariableRegionName);
    }

    @PUT
    @Path("{hypervariableRegionName}")
    @Consumes("application/json")
    public void putJsonAllele(@PathParam("hypervariableRegionName") String hypervariableRegionName, HypervariableRegion updateHypervariableRegion) {

        synchronized(SessionFilter.sessionMutex.get()) {

            HypervariableRegion hypervariableRegion = SessionFilter.hypervariableRegionFinder.get().getHypervariableRegion(hypervariableRegionName);
            hypervariableRegion.getVariantMap().values().forEach((hvrVariant) -> {
                hvrVariant.setKnownReactiveEpitopeForCompat(updateHypervariableRegion.getVariantMap().get(hvrVariant.getVariantId()).getKnownReactiveEpitopeForCompat());
            });

            SessionFilter.alleleFinder.get().computeCompatInterpretation(SessionFilter.hypervariableRegionFinder.get());

        }

    }
    
}
