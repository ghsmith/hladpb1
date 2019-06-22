package edu.emory.pathology.epitopeFinder.imgtdb.data;

import java.io.Serializable;
import java.util.List;

/**
 * Data class for a SAB panel.
 * 
 * @author ghsmith
 */
public class SabPanel implements Serializable {
   
    private String epRegLocusGroup;
    private List<String> epRegAlleleNameList;

    public String getEpRegLocusGroup() {
        return epRegLocusGroup;
    }

    public void setEpRegLocusGroup(String epRegLocusGroup) {
        this.epRegLocusGroup = epRegLocusGroup;
    }

    public List<String> getEpRegAlleleNameList() {
        return epRegAlleleNameList;
    }

    public void setEpRegAlleleNameList(List<String> epRegAlleleNameList) {
        this.epRegAlleleNameList = epRegAlleleNameList;
    }
    
}
