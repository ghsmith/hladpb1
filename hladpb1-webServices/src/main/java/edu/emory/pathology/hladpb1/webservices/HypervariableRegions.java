package edu.emory.pathology.hladpb1.webservices;

import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion;
import java.util.List;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

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
                hvrVariant.setExcludeFromCompat(updateHypervariableRegion.getVariantMap().get(hvrVariant.getVariantId()).getExcludeFromCompat());
                if(hvrVariant.getExcludeFromCompat()) {
                    hvrVariant.setKnownReactiveEpitopeForCompat(false);
                }
            });

            SessionFilter.alleleFinder.get().computeCompatInterpretation(SessionFilter.hypervariableRegionFinder.get());

        }

    }
    
}
