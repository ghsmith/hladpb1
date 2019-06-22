package edu.emory.pathology.epitopeFinder.imgtdb.data;

import java.io.Serializable;
import java.util.SortedMap;

/**
 * Data class for an allele.
 * @author ghsmith
 */
public class Allele implements Serializable {

    public static class AlleleEpRegEpitopeRef implements Serializable {
        
        private String epitopeName;
        private Integer compatSabPanelCountPresent;
        private Integer compatSabPanelCountAbsent;
        private Integer compatSabPanelCountUnknown;
        private Integer compatSabPanelPctPresent;

        public String getEpitopeName() {
            return epitopeName;
        }

        public void setEpitopeName(String epitopeName) {
            this.epitopeName = epitopeName;
        }

        public Integer getCompatSabPanelCountPresent() {
            return compatSabPanelCountPresent;
        }

        public void setCompatSabPanelCountPresent(Integer compatSabPanelCountPresent) {
            this.compatSabPanelCountPresent = compatSabPanelCountPresent;
        }

        public Integer getCompatSabPanelCountAbsent() {
            return compatSabPanelCountAbsent;
        }

        public void setCompatSabPanelCountAbsent(Integer compatSabPanelCountAbsent) {
            this.compatSabPanelCountAbsent = compatSabPanelCountAbsent;
        }

        public Integer getCompatSabPanelCountUnknown() {
            return compatSabPanelCountUnknown;
        }

        public void setCompatSabPanelCountUnknown(Integer compatSabPanelCountUnknown) {
            this.compatSabPanelCountUnknown = compatSabPanelCountUnknown;
        }

        public Integer getCompatSabPanelPctPresent() {
            return compatSabPanelPctPresent;
        }

        public void setCompatSabPanelPctPresent(Integer compatSabPanelPctPresent) {
            this.compatSabPanelPctPresent = compatSabPanelPctPresent;
        }
       
    }
        
    private Integer sequenceNumber;
    private String version;
    private String alleleName;
    private String epRegLocusGroup;
    private String epRegAlleleName;
    private Boolean inCurrentSabPanel;
    private Boolean inEpRegSabPanel;
    private Boolean recipientAntibodyForCompat = false;
    private Boolean recipientTypeForCompat = false;
    private Boolean donorTypeForCompat = false;
    private SortedMap<String, AlleleEpRegEpitopeRef> compatEpRegEpitopeMap;
    private String compatInterpretation;

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAlleleName() {
        return alleleName;
    }

    public void setAlleleName(String alleleName) {
        this.alleleName = alleleName;
    }

    public String getEpRegLocusGroup() {
        return epRegLocusGroup;
    }

    public void setEpRegLocusGroup(String epRegLocusGroup) {
        this.epRegLocusGroup = epRegLocusGroup;
    }

    public String getEpRegAlleleName() {
        return epRegAlleleName;
    }

    public void setEpRegAlleleName(String epRegAlleleName) {
        this.epRegAlleleName = epRegAlleleName;
    }

    public Boolean getInCurrentSabPanel() {
        return inCurrentSabPanel;
    }

    public void setInCurrentSabPanel(Boolean inCurrentSabPanel) {
        this.inCurrentSabPanel = inCurrentSabPanel;
    }

    public Boolean getInEpRegSabPanel() {
        return inEpRegSabPanel;
    }

    public void setInEpRegSabPanel(Boolean inEpRegSabPanel) {
        this.inEpRegSabPanel = inEpRegSabPanel;
    }

    public Boolean getRecipientAntibodyForCompat() {
        return recipientAntibodyForCompat;
    }

    public void setRecipientAntibodyForCompat(Boolean recipientAntibodyForCompat) {
        this.recipientAntibodyForCompat = recipientAntibodyForCompat;
    }

    public Boolean getRecipientTypeForCompat() {
        return recipientTypeForCompat;
    }

    public void setRecipientTypeForCompat(Boolean recipientTypeForCompat) {
        this.recipientTypeForCompat = recipientTypeForCompat;
    }

    public Boolean getDonorTypeForCompat() {
        return donorTypeForCompat;
    }

    public void setDonorTypeForCompat(Boolean donorTypeForCompat) {
        this.donorTypeForCompat = donorTypeForCompat;
    }

    public SortedMap<String, AlleleEpRegEpitopeRef> getCompatEpRegEpitopeMap() {
        return compatEpRegEpitopeMap;
    }

    public void setCompatEpRegEpitopeMap(SortedMap<String, AlleleEpRegEpitopeRef> compatEpRegEpitopeMap) {
        this.compatEpRegEpitopeMap = compatEpRegEpitopeMap;
    }

    public String getCompatInterpretation() {
        return compatInterpretation;
    }

    public void setCompatInterpretation(String compatInterpretation) {
        this.compatInterpretation = compatInterpretation;
    }
    
}
