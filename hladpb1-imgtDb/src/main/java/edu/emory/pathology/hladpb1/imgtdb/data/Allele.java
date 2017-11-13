package edu.emory.pathology.hladpb1.imgtdb.data;

import java.util.SortedMap;

/**
 *
 * @author ghsmith
 */
public class Allele {

    private String version;
    private String alleleName;
    private SortedMap<String, String> hypervariableRegionMap;
    private SortedMap<Integer, Codon> codonMap;
    private Boolean singleAntigenBead;
    private Boolean recipientAntibody;
    private Boolean recipientType;
    private Boolean donorType;

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

    public SortedMap<String, String> getHypervariableRegionMap() {
        return hypervariableRegionMap;
    }

    public void setHypervariableRegionMap(SortedMap<String, String> hypervariableRegionMap) {
        this.hypervariableRegionMap = hypervariableRegionMap;
    }

    public SortedMap<Integer, Codon> getCodonMap() {
        return codonMap;
    }

    public void setCodonMap(SortedMap<Integer, Codon> codonMap) {
        this.codonMap = codonMap;
    }

    public Boolean getSingleAntigenBead() {
        return singleAntigenBead;
    }

    public void setSingleAntigenBead(Boolean singleAntigenBead) {
        this.singleAntigenBead = singleAntigenBead;
    }

    public Boolean getRecipientAntibody() {
        return recipientAntibody;
    }

    public void setRecipientAntibody(Boolean recipientAntibody) {
        this.recipientAntibody = recipientAntibody;
    }

    public Boolean getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(Boolean recipientType) {
        this.recipientType = recipientType;
    }

    public Boolean getDonorType() {
        return donorType;
    }

    public void setDonorType(Boolean donorType) {
        this.donorType = donorType;
    }
    
}
