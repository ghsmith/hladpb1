package edu.emory.pathology.epitopeFinder.imgtdb.data;

import java.io.Serializable;
import java.util.SortedMap;

/**
 * Data class for an Epitope Registry Epitope.
 * @author ghsmith
 */
public class EpRegEpitope implements Serializable {

    public static class EpRegEpitopeAlleleRef implements Serializable {
        
        private String epRegAlleleName;
        private Boolean inCurrentSabPanel;
        private Boolean inEpRegSabPanel;
        private String compatStatus;

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

        public String getCompatStatus() {
            return compatStatus;
        }

        public void setCompatStatus(String compatStatus) {
            this.compatStatus = compatStatus;
        }
        
    }

    public static class EpRegEpitopeAlleleFilterRef implements Serializable {
        
        private String sourceUrl;
        private String reactiveEpRegAlleleName;

        public String getSourceUrl() {
            return sourceUrl;
        }

        public void setSourceUrl(String sourceUrl) {
            this.sourceUrl = sourceUrl;
        }

        public String getReactiveEpRegAlleleName() {
            return reactiveEpRegAlleleName;
        }

        public void setReactiveEpRegAlleleName(String reactiveEpRegAlleleName) {
            this.reactiveEpRegAlleleName = reactiveEpRegAlleleName;
        }

    }
    
    private Integer sequenceNumber;
    private String sourceUrl;
    private String locusGroup;
    private String epitopeName;
    private SortedMap<String, EpRegEpitopeAlleleRef> alleleMap;
    private SortedMap<String, EpRegEpitopeAlleleFilterRef> compatAlleleFilterMap;
    private Integer compatSabPanelCountPresent;
    private Integer compatSabPanelCountAbsent;
    private Integer compatSabPanelCountUnknown;
    private Integer compatSabPanelPctPresent;

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getLocusGroup() {
        return locusGroup;
    }

    public void setLocusGroup(String locusGroup) {
        this.locusGroup = locusGroup;
    }

    public String getEpitopeName() {
        return epitopeName;
    }

    public void setEpitopeName(String epitopeName) {
        this.epitopeName = epitopeName;
    }

    public SortedMap<String, EpRegEpitopeAlleleRef> getAlleleMap() {
        return alleleMap;
    }

    public void setAlleleMap(SortedMap<String, EpRegEpitopeAlleleRef> alleleMap) {
        this.alleleMap = alleleMap;
    }

    public SortedMap<String, EpRegEpitopeAlleleFilterRef> getCompatAlleleFilterMap() {
        return compatAlleleFilterMap;
    }

    public void setCompatAlleleFilterMap(SortedMap<String, EpRegEpitopeAlleleFilterRef> compatAlleleFilterMap) {
        this.compatAlleleFilterMap = compatAlleleFilterMap;
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
