package edu.emory.pathology.hladpb1.imgtdb;

import edu.emory.pathology.hladpb1.imgtdb.jaxb.emory.ReagentLots;
import java.io.File;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * This finder class loads the Emory data classes generated from the Emory XML
 * schema (hladpb1HypervariableRegion.xsd). It requires a reference to the Emory
 * XML database (hladpb1HypervariableRegion.xml).
 * 
 * @author ghsmith
 */
public class JaxbEmoryFinder {

    private static final Logger LOG = Logger.getLogger(JaxbEmoryFinder.class.getName());

    private String xmlFileName = "/tmp/hladpb1HypervariableRegion.xml";
    private ReagentLots reagentLots;

    public JaxbEmoryFinder() {
    }

    public JaxbEmoryFinder(String xmlFileName) {
        if(xmlFileName != null) {
            this.xmlFileName = xmlFileName;
        }
    }
    
    public ReagentLots getReagentLots() {
        if(reagentLots == null) {
            try {
                JAXBContext jc0 = JAXBContext.newInstance("edu.emory.pathology.hladpb1.imgtdb.jaxb.emory");
                reagentLots = (ReagentLots)jc0.createUnmarshaller().unmarshal(new File(xmlFileName));
            }
            catch(JAXBException e) {
                throw new RuntimeException(e);
            }
            LOG.info(String.format("%d reagent lots loaded from Emory XML database", reagentLots.getReagentLot().size()));
        }
        return reagentLots;
    }
    
}
