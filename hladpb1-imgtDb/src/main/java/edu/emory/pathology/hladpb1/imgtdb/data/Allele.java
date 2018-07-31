package edu.emory.pathology.hladpb1.imgtdb.data;

import java.util.SortedMap;

/**
 * Data class for an allele.
 * @author ghsmith
 */
public class Allele {

    // Inner class to make it convenient to reference hypervariable region with
    // the matchesReference attribute.
    public static class HypervariableRegionVariantRef {
        public String variantId;
        public Boolean matchesReference;
    }
    
    private Integer sequenceNumber;
    private String version;
    private String alleleName;
    private SortedMap<String, HypervariableRegionVariantRef> hvrVariantMap;
    private Integer hvrMatchCount;
    private SortedMap<Integer, Codon> codonMap;
    private Boolean nullAllele;
    private String synonymousAlleleName;
    private Boolean synonymousAlleleProteinShorter;
    private Boolean singleAntigenBead;
    private Boolean referenceAllele;
    private Boolean recipientAntibody;
    private Boolean recipientType;
    private Boolean donorType;

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

    public SortedMap<String, HypervariableRegionVariantRef> getHvrVariantMap() {
        return hvrVariantMap;
    }

    public void setHvrVariantMap(SortedMap<String, HypervariableRegionVariantRef> hvrVariantMap) {
        this.hvrVariantMap = hvrVariantMap;
    }

    public Integer getHvrMatchCount() {
        return hvrMatchCount;
    }

    public void setHvrMatchCount(Integer hvrMatchCount) {
        this.hvrMatchCount = hvrMatchCount;
    }

    public SortedMap<Integer, Codon> getCodonMap() {
        return codonMap;
    }

    public void setCodonMap(SortedMap<Integer, Codon> codonMap) {
        this.codonMap = codonMap;
    }

    public Boolean getNullAllele() {
        return nullAllele;
    }

    public void setNullAllele(Boolean nullAllele) {
        this.nullAllele = nullAllele;
    }

    public String getSynonymousAlleleName() {
        return synonymousAlleleName;
    }

    public void setSynonymousAlleleName(String synonymousAlleleName) {
        this.synonymousAlleleName = synonymousAlleleName;
    }

    public Boolean getSynonymousAlleleProteinShorter() {
        return synonymousAlleleProteinShorter;
    }

    public void setSynonymousAlleleProteinShorter(Boolean synonymousAlleleProteinShorter) {
        this.synonymousAlleleProteinShorter = synonymousAlleleProteinShorter;
    }

    public Boolean getSingleAntigenBead() {
        return singleAntigenBead;
    }

    public void setSingleAntigenBead(Boolean singleAntigenBead) {
        this.singleAntigenBead = singleAntigenBead;
    }

    public Boolean getReferenceAllele() {
        return referenceAllele;
    }

    public void setReferenceAllele(Boolean referenceAllele) {
        this.referenceAllele = referenceAllele;
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
