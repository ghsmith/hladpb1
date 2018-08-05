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
        
        private String variantId;
        private Boolean matchesReference;

        public String getVariantId() {
            return variantId;
        }

        public void setVariantId(String variantId) {
            this.variantId = variantId;
        }

        public Boolean getMatchesReference() {
            return matchesReference;
        }

        public void setMatchesReference(Boolean matchesReference) {
            this.matchesReference = matchesReference;
        }
        
    }
    
    private Integer sequenceNumber;
    private String version;
    private String alleleName;
    private SortedMap<String, HypervariableRegionVariantRef> hvrVariantMap;
    private SortedMap<Integer, Codon> codonMap;
    private Boolean nullAllele;
    private String synonymousAlleleName;
    private Boolean synonymousAlleleProteinShorter;
    private Boolean singleAntigenBead;
    private Boolean referenceForMatches;
    private Integer matchesHvrCount;
    private Boolean recipientAntibodyForCompat = false;
    private Boolean recipientTypeForCompat = false;
    private Boolean donorTypeForCompat = false;
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

    public SortedMap<String, HypervariableRegionVariantRef> getHvrVariantMap() {
        return hvrVariantMap;
    }

    public void setHvrVariantMap(SortedMap<String, HypervariableRegionVariantRef> hvrVariantMap) {
        this.hvrVariantMap = hvrVariantMap;
    }

    public Integer getMatchesHvrCount() {
        return matchesHvrCount;
    }

    public void setMatchesHvrCount(Integer matchesHvrCount) {
        this.matchesHvrCount = matchesHvrCount;
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

    public Boolean getReferenceForMatches() {
        return referenceForMatches;
    }

    public void setReferenceForMatches(Boolean referenceForMatches) {
        this.referenceForMatches = referenceForMatches;
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

    public String getCompatInterpretation() {
        return compatInterpretation;
    }

    public void setCompatInterpretation(String compatInterpretation) {
        this.compatInterpretation = compatInterpretation;
    }

}
