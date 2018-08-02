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
    private Boolean knownReactiveEpitopeForCompat;
    private Boolean compatIsRecipientEpitope;
    private Boolean compatPositiveSabCount;
    private Boolean compatAntibodyConsideredPresent;

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

    public Boolean getKnownReactiveEpitopeForCompat() {
        return knownReactiveEpitopeForCompat;
    }

    public void setKnownReactiveEpitopeForCompat(Boolean knownReactiveEpitopeForCompat) {
        this.knownReactiveEpitopeForCompat = knownReactiveEpitopeForCompat;
    }

    public Boolean getCompatIsRecipientEpitope() {
        return compatIsRecipientEpitope;
    }

    public void setCompatIsRecipientEpitope(Boolean compatIsRecipientEpitope) {
        this.compatIsRecipientEpitope = compatIsRecipientEpitope;
    }

    public Boolean getCompatPositiveSabCount() {
        return compatPositiveSabCount;
    }

    public void setCompatPositiveSabCount(Boolean compatPositiveSabCount) {
        this.compatPositiveSabCount = compatPositiveSabCount;
    }

    public Boolean getCompatAntibodyConsideredPresent() {
        return compatAntibodyConsideredPresent;
    }

    public void setCompatAntibodyConsideredPresent(Boolean compatAntibodyConsideredPresent) {
        this.compatAntibodyConsideredPresent = compatAntibodyConsideredPresent;
    }
    
}
