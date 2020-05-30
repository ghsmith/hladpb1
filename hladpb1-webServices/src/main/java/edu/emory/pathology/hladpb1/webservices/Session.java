package edu.emory.pathology.hladpb1.webservices;

import edu.emory.pathology.hladpb1.imgtdb.AlleleFinder;
import edu.emory.pathology.hladpb1.imgtdb.HypervariableRegionFinder;
import edu.emory.pathology.hladpb1.imgtdb.ReagentLotFinder;
import edu.emory.pathology.hladpb1.imgtdb.data.Allele;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegionVariant;
import edu.emory.pathology.hladpb1.imgtdb.data.ReagentLot;
import edu.emory.pathology.hladpb1.webservices.jaxb.haml.Haml;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.lang.SerializationUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

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
    
    @PUT
    @Path("/uploadHaml")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void uploadHaml(
        @Context HttpServletRequest request,
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataContentDisposition fileDetail
    ) throws JAXBException {

        JAXBContext jc = JAXBContext.newInstance(new Class[] { Haml.class });
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Haml haml = (Haml)unmarshaller.unmarshal(uploadedInputStream);

        AlleleFinder alleleFinder = new AlleleFinder(request.getServletContext().getInitParameter("imgtXmlFileName"));
        HypervariableRegionFinder hypervariableRegionFinder = new HypervariableRegionFinder(request.getServletContext().getInitParameter("emoryXmlFileName"), request.getServletContext().getInitParameter("reagentLotNumber"));
        ReagentLotFinder reagentLotFinder = new ReagentLotFinder(request.getServletContext().getInitParameter("emoryXmlFileName"));
        alleleFinder.assignHypervariableRegionVariantIds(hypervariableRegionFinder);

        List<HypervariableRegion> customHypervariableRegionList = (List<HypervariableRegion>)SerializationUtils.clone((Serializable)hypervariableRegionFinder.getHypervariableRegionList());
        for(Allele allele : alleleFinder.getAlleleList()) {
            allele.setSingleAntigenBead(false);
        }
        for(HypervariableRegion hvr : customHypervariableRegionList) {
            for(HypervariableRegionVariant hvrv : hvr.getVariantMap().values()) {
                hvrv.setBeadAlleleRefList(new ArrayList<>());
                for(Haml.PatientAntibodyAssessment.SolidPhasePanel spp : haml.getPatientAntibodyAssessment().get(0).getSolidPhasePanel()) {
                    for(Haml.PatientAntibodyAssessment.SolidPhasePanel.Bead bead : spp.getBead()) {
                        for(String alleleName : bead.getHLAAlleleSpecificity().split(",")) {
                            for(Allele allele : alleleFinder.getAlleleList()) {
                                if(allele.getAlleleName().contains(alleleName)) {
                                    allele.setSingleAntigenBead(true);
                                    if(allele.getHvrVariantMap().get(hvr.getHypervariableRegionName()).getVariantId().equals(hvrv.getVariantId())) {
                                        HypervariableRegionVariant.BeadAlleleRef beadAlleleRef = new HypervariableRegionVariant.BeadAlleleRef();
                                        beadAlleleRef.setAlleleName(alleleName);
                                        hvrv.getBeadAlleleRefList().add(beadAlleleRef);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // What if the custom panel doesn't have an HVRV represented?
        for(HypervariableRegion hvr : customHypervariableRegionList) {
            List<HypervariableRegionVariant> deleteList = new ArrayList<>();
            for(HypervariableRegionVariant hvrv : hvr.getVariantMap().values()) {
                if(hvrv.getBeadAlleleRefList().size() == 0) {
                    deleteList.add(hvrv);
                }
            }
            for(HypervariableRegionVariant delete : deleteList) {
                hvr.getVariantMap().remove(delete.getVariantId());
            }
        }
        
        reagentLotFinder.getReagentLots().add(new ReagentLot("custom HAML panel: " + fileDetail.getFileName()));
        hypervariableRegionFinder.setCustomPanel(customHypervariableRegionList, "custom HAML panel: " + fileDetail.getFileName());

        alleleFinder.assignHypervariableRegionVariantMatches(alleleFinder.getAlleleList().get(0).getAlleleName());
        alleleFinder.computeCompatInterpretation(hypervariableRegionFinder);
        ((HttpServletRequest)request).getSession().setAttribute("alleleFinder", alleleFinder);
        ((HttpServletRequest)request).getSession().setAttribute("hypervariableRegionFinder", hypervariableRegionFinder);
        ((HttpServletRequest)request).getSession().setAttribute("reagentLotFinder", reagentLotFinder);
        SessionFilter.alleleFinder.set(alleleFinder);
        SessionFilter.hypervariableRegionFinder.set(hypervariableRegionFinder);
        SessionFilter.reagentLotFinder.set(reagentLotFinder);
        
        for(Allele allele : SessionFilter.alleleFinder.get().getAlleleList()) {
            allele.setMfi(null);
        }
        for(Haml.PatientAntibodyAssessment.SolidPhasePanel spp : haml.getPatientAntibodyAssessment().get(0).getSolidPhasePanel()) {
            for(Haml.PatientAntibodyAssessment.SolidPhasePanel.Bead bead : spp.getBead()) {
                for(String alleleName : bead.getHLAAlleleSpecificity().split(",")) {
                    for(Allele allele : SessionFilter.alleleFinder.get().getAlleleList()) {
                        if(allele.getAlleleName().contains(alleleName)) {
                            allele.setMfi(bead.getRawMFI());
                        }
                    }
                }
            }
        }

    }
    
}
