package edu.emory.pathology.hladpb1.imgtdb;

import edu.emory.pathology.hladpb1.imgtdb.jaxb.imgt.Alleles;
import java.io.File;
import java.util.logging.Logger;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

/**
 * This finder class loads the IMGT data classes generated from the IMGT XML
 * schema (https://github.com/ANHIG/IMGTHLA/blob/Latest/xml/hla.xsd). It
 * requires a reference to the IMGT XML database
 * (https://github.com/ANHIG/IMGTHLA/blob/Latest/xml/hla.xml).
 * 
 * @author ghsmith
 */
public class JaxbImgtFinder {

    private static final Logger LOG = Logger.getLogger(JaxbImgtFinder.class.getName());

    private String xmlFileName = "/tmp/hla.xml";
    private Alleles alleles;

    public JaxbImgtFinder() {
    }
    
    public JaxbImgtFinder(String xmlFileName) {
        if(xmlFileName != null) {
            this.xmlFileName = xmlFileName;
        }
    }
    
    public Alleles getAlleles() {
        if(alleles == null) {
            try {
                JAXBContext jc0 = JAXBContext.newInstance("edu.emory.pathology.hladpb1.imgtdb.jaxb.imgt");
                alleles = (Alleles)jc0.createUnmarshaller().unmarshal(new File(xmlFileName));            
            }
            catch(JAXBException e) {
                throw new RuntimeException(e);
            }
            LOG.info(String.format("%d alleles loaded from IMGT XML database", alleles.getAllele().size()));
            LOG.info(String.format("IMGT XML database version is %s", alleles.getAllele().get(0).getReleaseversions().getCurrentrelease()));
        }
        return alleles;
    }
    
}
