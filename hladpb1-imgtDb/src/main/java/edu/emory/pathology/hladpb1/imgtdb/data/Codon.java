package edu.emory.pathology.hladpb1.imgtdb.data;

/**
 *
 * @author ghsmith
 */
public class Codon {

    private Integer position;
    private String hypervariableRegionName;
    private String aminoAcid;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getHypervariableRegionName() {
        return hypervariableRegionName;
    }

    public void setHypervariableRegionName(String hypervariableRegionName) {
        this.hypervariableRegionName = hypervariableRegionName;
    }

    public String getAminoAcid() {
        return aminoAcid;
    }

    public void setAminoAcid(String aminoAcid) {
        this.aminoAcid = aminoAcid;
    }
    
}
