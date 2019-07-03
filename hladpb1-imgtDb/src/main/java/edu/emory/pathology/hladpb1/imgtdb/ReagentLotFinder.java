package edu.emory.pathology.hladpb1.imgtdb;

import edu.emory.pathology.hladpb1.imgtdb.data.ReagentLot;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This finder class loads our local data classes from the Emory data classes.
 * Our local data classes are optimized for the HLA-DPB1 classifier.
 *
 * @author ghsmith
 */
public class ReagentLotFinder {

    private static final Logger LOG = Logger.getLogger(ReagentLotFinder.class.getName());
    
    private String xmlFileName;

    public ReagentLotFinder() {
    }
    
    public ReagentLotFinder(String xmlFileName) {
        this.xmlFileName = xmlFileName;
        
    }

    public List<ReagentLot> getReagentLots() {
        List<ReagentLot> reagentLots = new ArrayList<>();
        JaxbEmoryFinder emoryFinder = new JaxbEmoryFinder(xmlFileName);
        edu.emory.pathology.hladpb1.imgtdb.jaxb.emory.ReagentLots emoryReagentLots = emoryFinder.getReagentLots();
        emoryReagentLots.getReagentLot().stream().forEach((rl) -> {
            ReagentLot reagentLot = new ReagentLot();
            reagentLot.setLotNumber(rl.getLotNumber());
            reagentLots.add(reagentLot);
        });
        return reagentLots;
    }

}
