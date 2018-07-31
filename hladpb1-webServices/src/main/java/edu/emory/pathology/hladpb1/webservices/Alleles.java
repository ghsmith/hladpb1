package edu.emory.pathology.hladpb1.webservices;

import edu.emory.pathology.hladpb1.imgtdb.AlleleFinder;
import edu.emory.pathology.hladpb1.imgtdb.HypervariableRegionFinder;
import edu.emory.pathology.hladpb1.imgtdb.data.Allele;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBException;

/**
 * This class implements the Alleles RESTful web services.
 * 
 * @author ghsmith
 */
@Path("alleles")
public class Alleles {

    // These are set by SessionFilter.
    protected static ThreadLocal<AlleleFinder> alleleFinder = new ThreadLocal<>();
    protected static ThreadLocal<HypervariableRegionFinder> hypervariableRegionFinder = new ThreadLocal<>();

    @GET
    @Produces("application/json")
    public List<Allele> getJson() throws JAXBException {
        // Currently filtering out synonymous alleles.
        return alleleFinder.get().getAlleleList().stream().filter((allele) -> (allele.getSynonymousAlleleName() == null)).collect(Collectors.toList());
    }

    @GET
    @Path("{alleleName}")
    @Produces("application/json")
    public Allele getJson(@PathParam("alleleName") String alleleName) throws JAXBException {
        return alleleFinder.get().getAllele(alleleName);
    }

    @PUT
    @Path("{alleleName}")
    @Consumes("application/json")
    public void putJson(@PathParam("alleleName") String alleleName, Allele allele) throws JAXBException {
        // Currently only allowing changes to the reference allele.
        if(allele.getReferenceAllele()) {
            alleleFinder.get().assignHypervariableRegionVariantMatches(alleleName);
        }
    }
    
}
