package edu.emory.pathology.hladpb1.imgtdb.data;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Data class for an allele.
 * @author ghsmith
 */
public class Allele implements Serializable, Comparable<Allele> {

    Pattern pat = Pattern.compile("HLA-DPB1\\*([0-9]*):(.*)");
    
    @Override
    public int compareTo(Allele o) {
        Matcher matThis = pat.matcher(this.getAlleleName());
        matThis.find();
        Matcher matOther = pat.matcher(o.getAlleleName());
        matOther.find();
        return(String.format("%04d:%s", Integer.parseInt(matThis.group(1)), matThis.group(2))
            .compareTo(String.format("%04d:%s", Integer.parseInt(matOther.group(1)), matOther.group(2))));
    }

    // Inner class to make it convenient to reference hypervariable region with
    // the matchesReference attribute.
    public static class HypervariableRegionVariantRef implements Serializable {
        
        private String hypervariableRegionName;
        private String variantId;
        private Boolean matchesReference;

        public String getHypervariableRegionName() {
            return hypervariableRegionName;
        }

        public void setHypervariableRegionName(String hypervariableRegionName) {
            this.hypervariableRegionName = hypervariableRegionName;
        }

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
    private Integer proteinSequenceLength;
    private Boolean referenceForMatches;
    private Integer matchesHvrCount;
    private Boolean recipientAntibodyForCompat = false;
    private Boolean recipientTypeForCompat = false;
    private Boolean donorTypeForCompat = false;
    private String compatInterpretation;

    // These fields are not used by any algorithms and are only provided as
    // a convenience for clients that keep track of which allele is allele #1
    // and which allele is allele #2.
    private Boolean recipientAllele1;
    private Boolean recipientAllele2;
    private Boolean donorAllele1;
    private Boolean donorAllele2;

    // These fields are not used by any algorithms and are only provided as
    // a convenience for clients.
    private Boolean selection1;
    private Boolean selection2;
    
    private Integer mfi;
    
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

    public Boolean getRecipientAllele1() {
        return recipientAllele1;
    }

    public void setRecipientAllele1(Boolean recipientAllele1) {
        this.recipientAllele1 = recipientAllele1;
    }

    public Boolean getRecipientAllele2() {
        return recipientAllele2;
    }

    public void setRecipientAllele2(Boolean recipientAllele2) {
        this.recipientAllele2 = recipientAllele2;
    }

    public Boolean getDonorAllele1() {
        return donorAllele1;
    }

    public void setDonorAllele1(Boolean donorAllele1) {
        this.donorAllele1 = donorAllele1;
    }

    public Boolean getDonorAllele2() {
        return donorAllele2;
    }

    public void setDonorAllele2(Boolean donorAllele2) {
        this.donorAllele2 = donorAllele2;
    }

    public Integer getProteinSequenceLength() {
        return proteinSequenceLength;
    }

    public void setProteinSequenceLength(Integer proteinSequenceLength) {
        this.proteinSequenceLength = proteinSequenceLength;
    }

    public Boolean getSelection1() {
        return selection1;
    }

    public void setSelection1(Boolean selection1) {
        this.selection1 = selection1;
    }

    public Boolean getSelection2() {
        return selection2;
    }

    public void setSelection2(Boolean selection2) {
        this.selection2 = selection2;
    }

    public Integer getMfi() {
        return mfi;
    }

    public void setMfi(Integer mfi) {
        this.mfi = mfi;
    }
    
}
