package edu.emory.pathology.hladpb1.imgtdb.data;

import java.io.Serializable;

/**
 * Data class for a reagent lot (currently "thin").
 * 
 * @author ghsmith
 */
public class ReagentLot implements Serializable {
   
    private String lotNumber;

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }
    
}
