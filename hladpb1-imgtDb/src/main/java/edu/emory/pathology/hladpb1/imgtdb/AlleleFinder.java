package edu.emory.pathology.hladpb1.imgtdb;

import edu.emory.pathology.hladpb1.imgtdb.data.Allele;
import edu.emory.pathology.hladpb1.imgtdb.data.Codon;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

/**
 * This finder class loads our local data classes from the IMGT data classes.
 * Our local data classes are optimized for the HLA-DPB1 classifier.
 * 
 * @author ghsmith
 */
public class AlleleFinder {

    private static final Logger LOG = Logger.getLogger(AlleleFinder.class.getName());

    private List<Allele> alleleList;
    
    public List<Allele> getAlleleList() throws JAXBException {
        if(alleleList == null) {
            alleleList = new ArrayList();
            ImgtFinder imgtFinder = new ImgtFinder();
            edu.emory.pathology.hladpb1.imgtdb.jaxb.Alleles imgtAlleles = imgtFinder.getAlleles();
            imgtAlleles.getAllele().stream().filter((imgtAllele) -> (imgtAllele.getName().startsWith("HLA-DPB1")) && imgtAllele.getSequence() != null).forEach((imgtAllele) -> {
                Allele allele = new Allele();
                alleleList.add(allele);
                allele.setVersion(imgtAllele.getReleaseversions().getCurrentrelease());
                allele.setAlleleName(imgtAllele.getName());
                allele.setCodonMap(new TreeMap());
                final Iterator translationIterator = imgtAllele.getSequence().getFeature().stream().filter((feature) -> (feature.getFeaturetype().equals("Protein"))).findFirst().get().getTranslation().chars().iterator();
                imgtAllele.getSequence().getFeature().stream().filter((feature) -> (feature.getFeaturetype().equals("Exon"))).forEach((feature) -> {
                    for(int cdnaCoordinate = feature.getCDNACoordinates().getStart().intValueExact(); cdnaCoordinate <= feature.getCDNACoordinates().getEnd().intValueExact(); cdnaCoordinate++) {
                        int codonPosition = ((cdnaCoordinate - 1 + feature.getCDNACoordinates().getReadingframe().intValueExact()) / 3) + 1;
                        codonPosition = codonPosition - 29; // signal peptide is 29 codons
                        if((allele.getCodonMap().get(codonPosition) == null) && translationIterator.hasNext()) {
                            Codon codon = new Codon();
                            allele.getCodonMap().put(codonPosition, codon);
                            codon.setAminoAcid(String.format("%c", translationIterator.next()));
                        }
                    }
                });
            });
            LOG.info(String.format("%d HLA-DPB1 alleles loaded", alleleList.size()));
            LOG.info(String.format("version is %s", alleleList.get(0).getVersion()));
        }
        return alleleList;
    }
    
}
