package edu.emory.pathology.hladpb1.webservices;

import edu.emory.pathology.hladpb1.imgtdb.data.Allele;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang.SerializationUtils;

/**
 * This class implements the Alleles RESTful web services.
 * 
 * @author ghsmith
 */
@Path("alleles")
public class Alleles {

    @GET
    @Produces("application/json")
    public List<Allele> getJson(@QueryParam("noCodons") String noCodons, @QueryParam("synonymous") String synonymous, @QueryParam("sab") String sab, @QueryParam("hvrMatchCount") int matchesHvrCount) {
        List<Allele> alleles = SessionFilter.alleleFinder.get().getAlleleList();
        // noCodons saves bandwidth
        if("true".equals(noCodons)) {
            alleles = (List)SerializationUtils.clone((Serializable)alleles);
            alleles.stream().forEach((allele) -> { allele.setCodonMap(null); });
        }
        // Implementing some rudimentary filtering.
        if("false".equals(synonymous)) {
            alleles = alleles.stream().filter((allele) -> (allele.getSynonymousAlleleName() == null)).collect(Collectors.toList());
        }
        if("true".equals(sab)) {
            alleles = alleles.stream().filter((allele) -> (allele.getSingleAntigenBead())).collect(Collectors.toList());
        }
        if(matchesHvrCount > 0) {
            alleles = alleles.stream().filter((allele) -> (allele.getMatchesHvrCount() >= matchesHvrCount)).collect(Collectors.toList());
        }
        return alleles;
    }

    @GET
    @Path("{alleleName}")
    @Produces("application/json")
    public Allele getJsonAllele(@PathParam("alleleName") String alleleName, @QueryParam("noCodons") String noCodons) {
        Allele allele = SessionFilter.alleleFinder.get().getAllele(alleleName);
        // noCodons saves bandwidth
        if("true".equals(noCodons)) {
            allele = (Allele)SerializationUtils.clone((Serializable)allele);
            allele.setCodonMap(null);
        }
        return allele;
    }

    @PUT
    @Path("{alleleName}")
    @Consumes("application/json")
    public void putJsonAllele(@PathParam("alleleName") String alleleName, Allele updateAllele) {
        synchronized(SessionFilter.sessionMutex.get()) {
            Allele allele = SessionFilter.alleleFinder.get().getAllele(alleleName);
            if(updateAllele.getDonorAllele1() != null && updateAllele.getDonorAllele1()) {
                SessionFilter.alleleFinder.get().getAlleleList().stream().forEach((loopAllele) -> {
                    if(loopAllele.getDonorAllele1() != null && loopAllele.getDonorAllele1()) {
                        loopAllele.setDonorAllele1(false);
                    }
                });
                allele.setDonorAllele1(true);
                allele.setDonorTypeForCompat(true);
            }
            if(updateAllele.getDonorAllele2() != null && updateAllele.getDonorAllele2()) {
                SessionFilter.alleleFinder.get().getAlleleList().stream().forEach((loopAllele) -> {
                    if(loopAllele.getDonorAllele2() != null && loopAllele.getDonorAllele2()) {
                        loopAllele.setDonorAllele2(false);
                    }
                });
                allele.setDonorAllele2(true);
                allele.setDonorTypeForCompat(true);
            }
            if(updateAllele.getRecipientAllele1() != null && updateAllele.getRecipientAllele1()) {
                SessionFilter.alleleFinder.get().getAlleleList().stream().forEach((loopAllele) -> {
                    if(loopAllele.getRecipientAllele1() != null && loopAllele.getRecipientAllele1()) {
                        loopAllele.setRecipientAllele1(false);
                    }
                });
                allele.setRecipientAllele1(true);
                allele.setRecipientTypeForCompat(true);
            }
            if(updateAllele.getRecipientAllele2() != null && updateAllele.getRecipientAllele2()) {
                SessionFilter.alleleFinder.get().getAlleleList().stream().forEach((loopAllele) -> {
                    if(loopAllele.getRecipientAllele2() != null && loopAllele.getRecipientAllele2()) {
                        loopAllele.setRecipientAllele2(false);
                    }
                });
                allele.setRecipientAllele2(true);
                allele.setRecipientTypeForCompat(true);
            }
            SessionFilter.alleleFinder.get().getAlleleList().stream().forEach((loopAllele) -> {
                if((loopAllele.getDonorAllele1() == null || !loopAllele.getDonorAllele1()) && (loopAllele.getDonorAllele2() == null || !loopAllele.getDonorAllele2())) {
                    loopAllele.setDonorTypeForCompat(false);
                }
                if((loopAllele.getRecipientAllele1() == null || !loopAllele.getRecipientAllele1()) && (loopAllele.getRecipientAllele2() == null || !loopAllele.getRecipientAllele2())) {
                    loopAllele.setRecipientTypeForCompat(false);
                }
            });
            boolean[] assignCompatibilityStatus = new boolean[] { false }; // wrapping for use in lambda
            if(!updateAllele.getDonorTypeForCompat().equals(allele.getDonorTypeForCompat())) {
                allele.setDonorTypeForCompat(updateAllele.getDonorTypeForCompat());
                assignCompatibilityStatus[0] = true;
            }
            if(!updateAllele.getRecipientTypeForCompat().equals(allele.getRecipientTypeForCompat())) {
                allele.setRecipientTypeForCompat(updateAllele.getRecipientTypeForCompat());
                assignCompatibilityStatus[0] = true;
            }
            if(!updateAllele.getRecipientAntibodyForCompat().equals(allele.getRecipientAntibodyForCompat())) {
                // Not allowing antibodies to specified for alleles that are not the
                // subject of a single antigen bead.
                if(allele.getSingleAntigenBead()) {
                    allele.setRecipientAntibodyForCompat(updateAllele.getRecipientAntibodyForCompat());
                    assignCompatibilityStatus[0] = true;
                }
            }
            if(assignCompatibilityStatus[0]) {
                SessionFilter.alleleFinder.get().computeCompatInterpretation(SessionFilter.hypervariableRegionFinder.get());
            }
            if(updateAllele.getReferenceForMatches()) {
                // This will concurrently un-assign the current reference allele.
                SessionFilter.alleleFinder.get().assignHypervariableRegionVariantMatches(alleleName);
            }
        }
    }
    
}
