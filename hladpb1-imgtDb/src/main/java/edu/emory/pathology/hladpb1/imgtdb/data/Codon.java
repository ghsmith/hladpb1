package edu.emory.pathology.hladpb1.imgtdb.data;

/**
 * Data class for a codon.
 * @author ghsmith
 */
public class Codon {

    private Integer codonNumber;
    private String aminoAcid;
    private String hypervariableRegionName;
    private Boolean matchesReference;

    public Integer getCodonNumber() {
        return codonNumber;
    }

    public void setCodonNumber(Integer codonNumber) {
        this.codonNumber = codonNumber;
    }

    public String getAminoAcid() {
        return aminoAcid;
    }

    public void setAminoAcid(String aminoAcid) {
        this.aminoAcid = aminoAcid;
    }

    public String getHypervariableRegionName() {
        return hypervariableRegionName;
    }

    public void setHypervariableRegionName(String hypervariableRegionName) {
        this.hypervariableRegionName = hypervariableRegionName;
    }

    public Boolean getMatchesReference() {
        return matchesReference;
    }

    public void setMatchesReference(Boolean matchesReference) {
        this.matchesReference = matchesReference;
    }
    
}
