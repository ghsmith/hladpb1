package edu.emory.pathology.hladpb1.imgtdb;

import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegionVariant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
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
    
    private String xmlFileName;
    private String reagentLotNumber;
    private List<HypervariableRegion> hypervariableRegionList;

    public HypervariableRegionFinder(String reagentLotNumber) {
        this.reagentLotNumber = reagentLotNumber;
    }
    
    public HypervariableRegionFinder(String xmlFileName, String reagentLotNumber) {
        this.xmlFileName = xmlFileName;
        this.reagentLotNumber = reagentLotNumber;
    }
    
    public List<HypervariableRegion> getHypervariableRegionList() throws JAXBException {
        if(hypervariableRegionList == null) {
            hypervariableRegionList = new ArrayList();
            JaxbEmoryFinder emoryFinder = new JaxbEmoryFinder(xmlFileName);
            edu.emory.pathology.hladpb1.imgtdb.jaxb.emory.ReagentLots emoryReagentLots = emoryFinder.getReagentLots();
            emoryReagentLots.getReagentLot().stream().filter((emoryReagentLot) -> (emoryReagentLot.getLotNumber().equals(reagentLotNumber)))
            .findFirst().get().getHypervariableRegions().getHypervariableRegion().forEach((emoryHypervariableRegion) -> {
                HypervariableRegion hypervariableRegion = new HypervariableRegion();
                hypervariableRegionList.add(hypervariableRegion);
                hypervariableRegion.setHypervariableRegionName(emoryHypervariableRegion.getHvrName());
                hypervariableRegion.setCodonNumberList(new ArrayList<>());
                hypervariableRegion.setVariantMap(new TreeMap<>());
                Arrays.stream(emoryHypervariableRegion.getCodonNumbers().split(",")).forEach((codonNumber) -> { hypervariableRegion.getCodonNumberList().add(Integer.valueOf(codonNumber)); });
                emoryHypervariableRegion.getHvrVariants().getHvrVariant().stream().forEach((emoryHvrVariant) -> {
                    HypervariableRegionVariant hvrVariant = new HypervariableRegionVariant();
                    hvrVariant.setVariantId(emoryHvrVariant.getVariantId());
                    hypervariableRegion.getVariantMap().put(hvrVariant.getVariantId(), hvrVariant);
                    hvrVariant.setProteinSequenceList(new ArrayList<>());
                    Arrays.stream(emoryHvrVariant.getProteinSequences().split(",")).forEach((proteinSequence) -> { hvrVariant.getProteinSequenceList().add(proteinSequence); });
                    hvrVariant.setBeadAlleleNameList(new ArrayList<>());
                    emoryHvrVariant.getBeads().getBead().stream().forEach((emoryBead) -> { hvrVariant.getBeadAlleleNameList().add(emoryBead.getAlleleName()); });
                });
            });
        }
        LOG.info(String.format("%d hypervariable regions loaded", hypervariableRegionList.size()));
        LOG.info(String.format("lot number %s", reagentLotNumber));
        return hypervariableRegionList;
    }

    public String getReagentLotNumber() {
        return reagentLotNumber;
    }

}
