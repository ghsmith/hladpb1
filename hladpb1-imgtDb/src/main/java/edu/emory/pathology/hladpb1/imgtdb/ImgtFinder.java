package edu.emory.pathology.hladpb1.imgtdb;

import edu.emory.pathology.hladpb1.imgtdb.jaxb.imgt.Alleles;
import java.io.File;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * This finder class loads the IMGT data classes generated from the IMGT XML
 * schema (https://github.com/ANHIG/IMGTHLA/blob/Latest/xml/hla.xsd). It
 * requires a reference to the IMGT XML database
 * (https://github.com/ANHIG/IMGTHLA/blob/Latest/xml/hla.xml.zip).
 * 
 * @author ghsmith
 */
public class ImgtFinder {

    private static final Logger LOG = Logger.getLogger(ImgtFinder.class.getName());

    private Alleles alleles;
    
    public Alleles getAlleles() throws JAXBException {
        if(alleles == null) {
            JAXBContext jc0 = JAXBContext.newInstance("edu.emory.pathology.hladpb1.imgtdb.jaxb.imgt");
            alleles = (Alleles)jc0.createUnmarshaller().unmarshal(new File("data/hla.xml"));            
            LOG.info(String.format("%d alleles loaded from IMGT XML database", alleles.getAllele().size()));
            LOG.info(String.format("IMGT XML database version is %s", alleles.getAllele().get(0).getReleaseversions().getCurrentrelease()));
        }
        return alleles;
    }
    
}
