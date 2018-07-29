package edu.emory.pathology.hladpb1.imgtdb.data;

import java.util.SortedMap;

/**
 * Data class for an allele.
 * @author ghsmith
 */
public class Allele {

    private String version;
    private String alleleName;
    private SortedMap<String, String> hvrVariantMap;
    private SortedMap<Integer, Codon> codonMap;
    private Boolean nullAllele;
    private String synonymousAlleleName;
    private Boolean synonymousAlleleProteinShorter;
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

    public SortedMap<String, String> getHvrVariantMap() {
        return hvrVariantMap;
    }

    public void setHvrVariantMap(SortedMap<String, String> hvrVariantMap) {
        this.hvrVariantMap = hvrVariantMap;
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
