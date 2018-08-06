package edu.emory.pathology.hladpb1.webservices;

import edu.emory.pathology.hladpb1.imgtdb.AlleleFinder;
import edu.emory.pathology.hladpb1.imgtdb.HypervariableRegionFinder;
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

    // These are set by SessionFilter.
    protected static ThreadLocal<AlleleFinder> alleleFinder = new ThreadLocal<>();
    protected static ThreadLocal<HypervariableRegionFinder> hypervariableRegionFinder = new ThreadLocal<>();

    @GET
    @Path("reagentLotNumber")
    @Produces("application/json")
    public String getJsonReagentLotNumber() {
        return hypervariableRegionFinder.get().getReagentLotNumber();
    }
    
    @GET
    @Produces("application/json")
    public List<HypervariableRegion> getJson() {
        return hypervariableRegionFinder.get().getHypervariableRegionList();
    }

    @GET
    @Path("{hypervariableRegionName}")
    @Produces("application/json")
    public HypervariableRegion getJsonAllele(@PathParam("hypervariableRegionName") String hypervariableRegionName) {
        return hypervariableRegionFinder.get().getHypervariableRegion(hypervariableRegionName);
    }

    @PUT
    @Path("{hypervariableRegionName}")
    @Consumes("application/json")
    public void putJsonAllele(@PathParam("hypervariableRegionName") String hypervariableRegionName, HypervariableRegion updateHypervariableRegion) {
        HypervariableRegion hypervariableRegion = hypervariableRegionFinder.get().getHypervariableRegion(hypervariableRegionName);
        boolean[] assignCompatibilityStatus = new boolean[] { false }; // wrapping for use in lambda
        hypervariableRegion.getVariantMap().values().forEach((hvrVariant) -> {
            if(!updateHypervariableRegion.getVariantMap().get(hvrVariant.getVariantId()).getKnownReactiveEpitopeForCompat().equals(hvrVariant.getKnownReactiveEpitopeForCompat())) {
                hvrVariant.setKnownReactiveEpitopeForCompat(updateHypervariableRegion.getVariantMap().get(hvrVariant.getVariantId()).getKnownReactiveEpitopeForCompat());
                assignCompatibilityStatus[0] = true;
            }
        });
        if(assignCompatibilityStatus[0]) {
            alleleFinder.get().computeCompatInterpretation(hypervariableRegionFinder.get());
        }
    }
    
}
