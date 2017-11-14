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
            JaxbImgtFinder imgtFinder = new JaxbImgtFinder();
            edu.emory.pathology.hladpb1.imgtdb.jaxb.imgt.Alleles imgtAlleles = imgtFinder.getAlleles();
            // Process each IMGT HLA-DPB1 allele that has a sequence element.
            imgtAlleles.getAllele().stream().filter(
                (imgtAllele) -> (
                    imgtAllele.getName().startsWith("HLA-DPB1"))
                    && imgtAllele.getSequence() != null
                ).forEach((imgtAllele) ->
            {
                Allele allele = new Allele();
                alleleList.add(allele);
                allele.setVersion(imgtAllele.getReleaseversions().getCurrentrelease());
                allele.setAlleleName(imgtAllele.getName());
                allele.setCodonMap(new TreeMap());
                final Iterator translationIterator = imgtAllele.getSequence().getFeature().stream().filter(
                    (feature) -> (
                        feature.getFeaturetype().equals("Protein"))
                    ).findFirst().get().getTranslation().chars().iterator();
                // Process each each IMGT exon. The codon position is deduced
                // from the CDNA coordinate and the reading frame. Note that
                // NULL alleles will not have protein sequence for all codons.
                imgtAllele.getSequence().getFeature().stream().filter(
                    (feature) -> (
                        feature.getFeaturetype().equals("Exon"))
                    ).forEach((feature) ->
                {
                    for(int cdnaCoordinate = feature.getCDNACoordinates().getStart().intValueExact(); cdnaCoordinate <= feature.getCDNACoordinates().getEnd().intValueExact(); cdnaCoordinate++) {
                        int codonNumber = ((cdnaCoordinate - 1) / 3) + 1; // codon number starts at one (not zero)
                        int positionInCodon = (cdnaCoordinate - 1) - (((cdnaCoordinate - 1) / 3) * 3) + 1; // position in codon starts at one (not zero)
                        // -HARD-CODED SIGNAL PEPTIDE LENGTH--------------------
                        codonNumber = codonNumber - 29; // signal peptide is 29 codons
                        // -----------------------------------------------------
                        if(allele.getCodonMap().get(codonNumber) == null) {
                            if(translationIterator.hasNext()) {
                                if(allele.getCodonMap().size() > 0 || positionInCodon == 1) { // IMGT translation starts with first whole codon
                                    Codon codon = new Codon();
                                    allele.getCodonMap().put(codonNumber, codon);
                                    codon.setAminoAcid(String.format("%c", translationIterator.next()));
                                }
                            }
                            else {
                                assert
                                    // It is a NULL allele...
                                    allele.getAlleleName().endsWith("N")
                                    // ...the exon doesn't end on a codon boundary (some alleles don't have all exons represented)
                                    || ((feature.getCDNACoordinates().getEnd().intValueExact() % 3) != 0)
                                    // ...or it is a stop codon.
                                    || (
                                        "TAG".equals(imgtAllele.getSequence().getNucsequence().substring(feature.getSequenceCoordinates().getStart().intValueExact() + (cdnaCoordinate - feature.getCDNACoordinates().getStart().intValueExact() - positionInCodon)).substring(0, 3))
                                        || "TAA".equals(imgtAllele.getSequence().getNucsequence().substring(feature.getSequenceCoordinates().getStart().intValueExact() + (cdnaCoordinate - feature.getCDNACoordinates().getStart().intValueExact() - positionInCodon)).substring(0, 3))
                                        || "TGA".equals(imgtAllele.getSequence().getNucsequence().substring(feature.getSequenceCoordinates().getStart().intValueExact() + (cdnaCoordinate - feature.getCDNACoordinates().getStart().intValueExact() - positionInCodon)).substring(0, 3))
                                    )    
                                    : allele.getAlleleName() + " translation too short" + codonNumber;
                            }
                        }
                    }
                });
                assert !translationIterator.hasNext() : allele.getAlleleName() + " translation too long"; // should not have any leftover translation
            });
            LOG.info(String.format("%d HLA-DPB1 alleles loaded", alleleList.size()));
            LOG.info(String.format("version is %s", alleleList.get(0).getVersion()));
        }
        return alleleList;
    }
    
}
