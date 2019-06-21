package edu.emory.pathology.hladpb1.imgtdb.data;

import java.io.Serializable;
import java.util.List;
import java.util.SortedMap;

/**
 * Data class for a hypervariable region.
 * 
 * @author ghsmith
 */
public class HypervariableRegion implements Serializable {
   
    private String hypervariableRegionName;
    private List<Integer> codonNumberList;
    private SortedMap<String, HypervariableRegionVariant> variantMap;

    public String getHypervariableRegionName() {
        return hypervariableRegionName;
    }

    public void setHypervariableRegionName(String hypervariableRegionName) {
        this.hypervariableRegionName = hypervariableRegionName;
    }

    public List<Integer> getCodonNumberList() {
        return codonNumberList;
    }

    public void setCodonNumberList(List<Integer> codonNumberList) {
        this.codonNumberList = codonNumberList;
    }

    public SortedMap<String, HypervariableRegionVariant> getVariantMap() {
        return variantMap;
    }

    public void setVariantMap(SortedMap<String, HypervariableRegionVariant> variantMap) {
        this.variantMap = variantMap;
    }
    
}
