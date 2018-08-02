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
    public void putJsonAllele(@PathParam("hypervariableRegionName") String hypervariableRegionName, HypervariableRegion hypervariableRegion) throws JAXBException {
        // Currently only allowing changes to the forCompatibility properties.
        HypervariableRegion currentHypervariableRegion = hypervariableRegionFinder.get().getHypervariableRegion(hypervariableRegionName);
        hypervariableRegion.getVariantMap().keySet().forEach((hyperVariableRegionName) -> {
            currentHypervariableRegion.getVariantMap().get(hyperVariableRegionName).setKnownReactiveEpitopeForCompat(hypervariableRegion.getVariantMap().get(hyperVariableRegionName).getKnownReactiveEpitopeForCompat());
        });
    }
    
}
