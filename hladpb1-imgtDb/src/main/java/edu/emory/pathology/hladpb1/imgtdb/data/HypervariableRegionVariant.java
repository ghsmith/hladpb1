package edu.emory.pathology.hladpb1.imgtdb.data;

import java.util.List;

/**
 * Data class for a hypervariable region variant.
 * 
 * @author ghsmith
 */
public class HypervariableRegionVariant {
    
    private String variantId;
    private List<String> proteinSequenceList;
    private List<String> beadAlleleNameList;

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public List<String> getProteinSequenceList() {
        return proteinSequenceList;
    }

    public void setProteinSequenceList(List<String> proteinSequenceList) {
        this.proteinSequenceList = proteinSequenceList;
    }

    public List<String> getBeadAlleleNameList() {
        return beadAlleleNameList;
    }

    public void setBeadAlleleNameList(List<String> beadAlleleNameList) {
        this.beadAlleleNameList = beadAlleleNameList;
    }
    
}
