package edu.emory.pathology.hladpb1.imgtdb;

import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

/**
 * This finder class loads our local data classes from the Emory data classes.
 * Our local data classes are optimized for the HLA-DPB1 classifier.
 *
 * @author ghsmith
 */
public class HypervariableRegionFinder {

    private static final Logger LOG = Logger.getLogger(HypervariableRegionFinder.class.getName());
    
    private String reagentLotNumber = "10";
    private List<HypervariableRegion> hypervariableRegionList;

    public HypervariableRegionFinder() {
    }
    
    public HypervariableRegionFinder(String reagentLotNumber) {
        this.reagentLotNumber = reagentLotNumber;
    }

    public List<HypervariableRegion> getHypervariableRegionList() throws JAXBException {
        if(hypervariableRegionList == null) {
            hypervariableRegionList = new ArrayList();
            JaxbEmoryFinder emoryFinder = new JaxbEmoryFinder();
            edu.emory.pathology.hladpb1.imgtdb.jaxb.emory.ReagentLots emoryReagentLots = emoryFinder.getReagentLots();
            emoryReagentLots.getReagentLot().stream().filter(
                (emoryReagentLot) -> (
                    emoryReagentLot.getLotNumber().equals(reagentLotNumber))
                ).findFirst().get().getHypervariableRegions().getHypervariableRegion().forEach((imgtHypervariableRegion) ->
            {
                HypervariableRegion hypervariableRegion = new HypervariableRegion();
                hypervariableRegionList.add(hypervariableRegion);
                hypervariableRegion.setHypervariableRegionName(imgtHypervariableRegion.getHvrName());
                hypervariableRegion.setCodonNumberList(Arrays.asList(imgtHypervariableRegion.getCodonNumbers().split(","))
            });
        }
        return hypervariableRegionList;
    }
    
}
