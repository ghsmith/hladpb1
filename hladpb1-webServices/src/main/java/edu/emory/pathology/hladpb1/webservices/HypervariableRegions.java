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
import javax.xml.bind.JAXBException;

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
    public String getJsonReagentLotNumber() throws JAXBException {
        return hypervariableRegionFinder.get().getReagentLotNumber();
    }
    
    @GET
    @Produces("application/json")
    public List<HypervariableRegion> getJson() throws JAXBException {
        return hypervariableRegionFinder.get().getHypervariableRegionList();
    }

    @GET
    @Path("{hypervariableRegionName}")
    @Produces("application/json")
    public HypervariableRegion getJsonAllele(@PathParam("hypervariableRegionName") String hypervariableRegionName) throws JAXBException {
        return hypervariableRegionFinder.get().getHypervariableRegion(hypervariableRegionName);
    }

    @PUT
    @Path("{hypervariableRegionName}")
    @Consumes("application/json")
    public void putJsonAllele(@PathParam("hypervariableRegionName") String hypervariableRegionName, HypervariableRegion updatedHypervariableRegion) throws JAXBException {
        HypervariableRegion hypervariableRegion = hypervariableRegionFinder.get().getHypervariableRegion(hypervariableRegionName);
        boolean[] assignCompatibilityStatus = new boolean[] {false}; // wrapping for use in lambda
        hypervariableRegion.getVariantMap().keySet().forEach((variantId) -> {
            if(!updatedHypervariableRegion.getVariantMap().get(variantId).getKnownReactiveEpitopeForCompat().equals(hypervariableRegion.getVariantMap().get(variantId).getKnownReactiveEpitopeForCompat())) {
                hypervariableRegion.getVariantMap().get(variantId).setKnownReactiveEpitopeForCompat(updatedHypervariableRegion.getVariantMap().get(variantId).getKnownReactiveEpitopeForCompat());
                assignCompatibilityStatus[0] = true;
            }
        });
        if(assignCompatibilityStatus[0]) {
        }
    }
    
}
