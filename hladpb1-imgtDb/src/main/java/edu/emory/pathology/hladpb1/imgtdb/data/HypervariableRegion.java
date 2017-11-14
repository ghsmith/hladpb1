package edu.emory.pathology.hladpb1.imgtdb.data;

import java.util.List;
import java.util.Map;

/**
 * Data class for a hypervariable region.
 * 
 * @author ghsmith
 */
public class HypervariableRegion {
   
    private String hypervariableRegionName;
    private List codonNumberList;
    private Map<String, HypervariableRegionVariant> variantMap;

    public String getHypervariableRegionName() {
        return hypervariableRegionName;
    }

    public void setHypervariableRegionName(String hypervariableRegionName) {
        this.hypervariableRegionName = hypervariableRegionName;
    }

    public List getCodonNumberList() {
        return codonNumberList;
    }

    public void setCodonNumberList(List codonNumberList) {
        this.codonNumberList = codonNumberList;
    }

    public Map<String, HypervariableRegionVariant> getVariantMap() {
        return variantMap;
    }

    public void setVariantMap(Map<String, HypervariableRegionVariant> variantMap) {
        this.variantMap = variantMap;
    }
    
}
