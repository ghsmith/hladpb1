package edu.emory.pathology.hladpb1.webservices;

import edu.emory.pathology.hladpb1.imgtdb.data.Allele;
import edu.emory.pathology.hladpb1.webservices.jaxb.haml.Haml;
import java.io.InputStream;
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
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.lang.SerializationUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

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
    public void putJsonAllele(@PathParam("alleleName") String alleleName, @QueryParam("enumeratedAlleleMode") String enumeratedAlleleMode, Allele updateAllele) {

        synchronized(SessionFilter.sessionMutex.get()) {

            Allele allele = SessionFilter.alleleFinder.get().getAllele(alleleName);
            Allele virginAllele = (Allele)SerializationUtils.clone((Serializable)allele);

            // 1. Set the reference allele for the difference report.
            if(updateAllele.getReferenceForMatches()) {
                // This will concurrently un-assign the current reference allele.
                SessionFilter.alleleFinder.get().assignHypervariableRegionVariantMatches(alleleName);
            }
            
            // 2. Set the donor, recipient, and recipient antibody types for a
            //    full compatibility evaluation.
            allele.setDonorTypeForCompat(updateAllele.getDonorTypeForCompat());
            allele.setRecipientTypeForCompat(updateAllele.getRecipientTypeForCompat());
            // Not allowing antibodies to specified for alleles that are not the
            // subject of a single antigen bead.
            if(allele.getSingleAntigenBead()) {
                allele.setRecipientAntibodyForCompat(updateAllele.getRecipientAntibodyForCompat());
            }

            // 3. If the donor/recipient allele #1/#2 fields are being used by
            //    a client that cares to keep track of what is allele #1 and
            //    what is allele #2, then process those properties.
            if("true".equals(enumeratedAlleleMode)) {
                allele.setDonorAllele1(updateAllele.getDonorAllele1());
                if(allele.getDonorAllele1() != null && allele.getDonorAllele1()) {
                    SessionFilter.alleleFinder.get().getAlleleList().stream().filter((loopAllele) -> (allele != loopAllele)).forEach((loopAllele) -> { loopAllele.setDonorAllele1(false); });
                }
                allele.setDonorAllele2(updateAllele.getDonorAllele2());
                if(allele.getDonorAllele2() != null && allele.getDonorAllele2()) {
                    SessionFilter.alleleFinder.get().getAlleleList().stream().filter((loopAllele) -> (allele != loopAllele)).forEach((loopAllele) -> { loopAllele.setDonorAllele2(false); });
                }
                allele.setRecipientAllele1(updateAllele.getRecipientAllele1());
                if(allele.getRecipientAllele1() != null && allele.getRecipientAllele1()) {
                    SessionFilter.alleleFinder.get().getAlleleList().stream().filter((loopAllele) -> (allele != loopAllele)).forEach((loopAllele) -> { loopAllele.setRecipientAllele1(false); });
                }
                allele.setRecipientAllele2(updateAllele.getRecipientAllele2());
                if(allele.getRecipientAllele2() != null && allele.getRecipientAllele2()) {
                    SessionFilter.alleleFinder.get().getAlleleList().stream().filter((loopAllele) -> (allele != loopAllele)).forEach((loopAllele) -> { loopAllele.setRecipientAllele2(false); });
                }
                SessionFilter.alleleFinder.get().getAlleleList().stream().forEach((loopAllele) -> {
                    loopAllele.setDonorTypeForCompat(false);
                    if((loopAllele.getDonorAllele1() != null && loopAllele.getDonorAllele1()) || (loopAllele.getDonorAllele2() != null && loopAllele.getDonorAllele2())) {
                        loopAllele.setDonorTypeForCompat(true);
                    }
                    loopAllele.setRecipientTypeForCompat(false);
                    if((loopAllele.getRecipientAllele1() != null && loopAllele.getRecipientAllele1()) || (loopAllele.getRecipientAllele2() != null && loopAllele.getRecipientAllele2())) {
                        loopAllele.setRecipientTypeForCompat(true);
                    }
                });
            }

            // 4. When a recipient or donor type is specified, make sure all of the
            //    alleles (primary and alternate) are also specified. This seems
            //    like the prudent thing to do to me. Antibodies may be specified
            //    as a particular allele (primary or alternate), but not recipient
            //    and donor types (e.g., if you select 04:01 as a recipient or
            //    donor type, then you are also selecting 04:01[a], 04:01[b],
            //    and 04:01[c]. In practice, having alleles assigned to multiple
            //    HVRV in the same HVR is probably too complicated, so all this
            //    stuff is probably not worth thinking too hard about.
            {
                String baseAlleleName = allele.getAlleleName().replaceAll("\\[.*\\]", "");
                if(
                    (allele.getRecipientTypeForCompat() != null && allele.getRecipientTypeForCompat())
                    && (virginAllele.getRecipientTypeForCompat() == null || !virginAllele.getRecipientTypeForCompat())
                ) {
                    SessionFilter.alleleFinder.get().getAlleleList().stream().filter((loopAllele) -> (loopAllele.getAlleleName().startsWith(baseAlleleName))).forEach((loopAllele) -> {
                        loopAllele.setRecipientTypeForCompat(true);
                    });
                }
                if(
                    (allele.getRecipientTypeForCompat() == null || !allele.getRecipientTypeForCompat())
                    && (virginAllele.getRecipientTypeForCompat() != null && virginAllele.getRecipientTypeForCompat())
                ) {
                    SessionFilter.alleleFinder.get().getAlleleList().stream().filter((loopAllele) -> (loopAllele.getAlleleName().startsWith(baseAlleleName))).forEach((loopAllele) -> {
                        loopAllele.setRecipientTypeForCompat(false);
                    });
                }
                if(
                    (allele.getDonorTypeForCompat() != null && allele.getDonorTypeForCompat())
                    && (virginAllele.getDonorTypeForCompat() == null || !virginAllele.getDonorTypeForCompat())
                ) {
                    SessionFilter.alleleFinder.get().getAlleleList().stream().filter((loopAllele) -> (loopAllele.getAlleleName().startsWith(baseAlleleName))).forEach((loopAllele) -> {
                        loopAllele.setDonorTypeForCompat(true);
                    });
                }
                if(
                    (allele.getDonorTypeForCompat() == null || !allele.getDonorTypeForCompat())
                    && (virginAllele.getDonorTypeForCompat() != null && virginAllele.getDonorTypeForCompat())
                ) {
                    SessionFilter.alleleFinder.get().getAlleleList().stream().filter((loopAllele) -> (loopAllele.getAlleleName().startsWith(baseAlleleName))).forEach((loopAllele) -> {
                        loopAllele.setDonorTypeForCompat(false);
                    });
                }
            }
            
            // 5. Do the compatibility evaluation.
            SessionFilter.alleleFinder.get().computeCompatInterpretation(SessionFilter.hypervariableRegionFinder.get());

            // 6. Set the selection attributes.
            allele.setSelection1(updateAllele.getSelection1());
            allele.setSelection2(updateAllele.getSelection2());
            
        }
        
    }
    
    @PUT
    @Path("/uploadHaml")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void uploadHaml(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("mfiThreshold") String mfiThreshold) throws JAXBException {
        Integer parsedMfiThreshold = null;
        try {
            parsedMfiThreshold = Integer.valueOf(mfiThreshold);
        }
        catch(Exception e) {}
        System.out.println(parsedMfiThreshold);
        JAXBContext jc = JAXBContext.newInstance(new Class[] { Haml.class });
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Haml haml = (Haml)unmarshaller.unmarshal(uploadedInputStream);
        for(Allele allele : SessionFilter.alleleFinder.get().getAlleleList()) {
            allele.setMfi(null);
            if(parsedMfiThreshold != null) {
                allele.setSelection1(false);
            }
        }
        for(Haml.PatientAntibodyAssessment.SolidPhasePanel spp : haml.getPatientAntibodyAssessment().get(0).getSolidPhasePanel()) {
            for(Haml.PatientAntibodyAssessment.SolidPhasePanel.Bead bead : spp.getBead()) {
                for(String alleleName : bead.getHLAAlleleSpecificity().split(",")) {
                    for(Allele allele : SessionFilter.alleleFinder.get().getAlleleList()) {
                        if(allele.getAlleleName().contains(alleleName)) {
                            allele.setMfi(bead.getRawMFI());
                            if(parsedMfiThreshold != null) {
                                allele.setSelection1(allele.getMfi() >= parsedMfiThreshold);
                            }
                        }
                    }
                }
            }
        }
    }
    
}
