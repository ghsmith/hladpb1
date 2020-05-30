package edu.emory.pathology.hladpb1.imgtdb;

import edu.emory.pathology.hladpb1.imgtdb.data.Allele;
import edu.emory.pathology.hladpb1.imgtdb.data.Codon;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegionVariant;
import edu.emory.pathology.hladpb1.imgtdb.jaxb.imgt.CDNAindel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang.SerializationUtils;

/**
 * This finder class loads our local data classes from the IMGT data classes.
 * Our local data classes are optimized for the HLA-DPB1 classifier.
 * 
 * @author ghsmith
 */
public class AlleleFinder {

    private static final Logger LOG = Logger.getLogger(AlleleFinder.class.getName());

    private String xmlFileName;
    private List<Allele> alleleList;

    public AlleleFinder() {
    }

    public AlleleFinder(String xmlFileName) {
        this.xmlFileName = xmlFileName;
    }
    
    public Allele getAllele(String alleleName) {
        return getAlleleList().stream().filter((allele) -> (alleleName.equals(allele.getAlleleName()))).findFirst().get();
    }
    
    public List<Allele> getAlleleList() {
        
        if(alleleList == null) {
            alleleList = new ArrayList();
            JaxbImgtFinder imgtFinder = new JaxbImgtFinder(xmlFileName);
            edu.emory.pathology.hladpb1.imgtdb.jaxb.imgt.Alleles imgtAlleles = imgtFinder.getAlleles();
            // Process each IMGT HLA-DPB1 allele that has a sequence element.
            imgtAlleles.getAllele().stream().filter((imgtAllele) -> (imgtAllele.getName().startsWith("HLA-DPB1")) && imgtAllele.getSequence() != null).forEach((imgtAllele) -> {
                Allele allele = new Allele();
                alleleList.add(allele);
                allele.setVersion(imgtAllele.getReleaseversions().getCurrentrelease());
                allele.setAlleleName(imgtAllele.getName());
                allele.setCodonMap(new TreeMap());
                final Iterator translationIterator = imgtAllele.getSequence().getFeature().stream().filter((feature) -> (feature.getFeaturetype().equals("Protein"))).findFirst().get().getTranslation().chars().iterator();
                final int[] codonsDiscarded = new int[] {0};
                // Process each each IMGT exon. The codon position is deduced
                // from the CDNA coordinate and the reading frame. Note that
                // NULL alleles will not have protein sequence for all codons.
                imgtAllele.getSequence().getFeature().stream().filter((feature) -> (feature.getFeaturetype().equals("Exon"))).forEach((feature) ->  {
                    for(int cdnaCoordinate = feature.getCDNACoordinates().getStart().intValueExact(); cdnaCoordinate <= feature.getCDNACoordinates().getEnd().intValueExact(); cdnaCoordinate++) {
                        int codonNumber = ((cdnaCoordinate - 1) / 3) + 1; // codon number starts at one (not zero)
                        int positionInCodon = (cdnaCoordinate - 1) - (((cdnaCoordinate - 1) / 3) * 3) + 1; // position in codon starts at one (not zero)

                        // -HARD-CODED SIGNAL PEPTIDE LENGTH--------------------
                        codonNumber = codonNumber - 29; // signal peptide is 29 codons
                        // -----------------------------------------------------

                        // -BRITTLE DELETION LOGIC -----------------------------
                        // (deletions are not represented in the IMGT translation string)
                        boolean inDeletion = false;
                        for(CDNAindel cDNAindel : feature.getCDNAindel()) {
                            if(cDNAindel.getType().equals("deletion")) {
                                if(cdnaCoordinate >= cDNAindel.getStart().intValueExact() && cdnaCoordinate <= cDNAindel.getEnd().intValueExact()) {
                                    LOG.info(String.format("%s deletion at cDNA %d codon %d position %d size %d", allele.getAlleleName(), cdnaCoordinate, codonNumber, positionInCodon, cDNAindel.getSize().intValueExact()));
                                    inDeletion = true;
                                    if(positionInCodon > 1) {
                                        codonNumber++;
                                    }
                                    for(int x = 1; x <= cDNAindel.getSize().intValueExact() / 3; x++) {
                                        LOG.info(String.format("%s inserting '.' at codon %d", allele.getAlleleName(), codonNumber));
                                        Codon codon = new Codon();
                                        codon.setCodonNumber(codonNumber - codonsDiscarded[0]);
                                        allele.getCodonMap().put(codon.getCodonNumber(), codon);
                                        codon.setAminoAcid(String.format("%c", '.'));
                                        codonNumber++;
                                    }
                                    cdnaCoordinate = cdnaCoordinate + cDNAindel.getSize().intValueExact() - 1;
                                    break;
                                }
                            }
                        }
                        if(inDeletion) {
                            continue;
                        }
                        // -----------------------------------------------------

                        // -BRITTLE INSERTION LOGIC ----------------------------
                        // (insertions are represented in the IMGT translation string)
                        boolean inInsertion = false;
                        for(CDNAindel cDNAindel : feature.getCDNAindel()) {
                            if(cDNAindel.getType().equals("insertion")) {
                                if(cdnaCoordinate == cDNAindel.getEnd().intValueExact()) {
                                    LOG.info(String.format("%s insertion at cDNA %d codon %d position %d size %d", allele.getAlleleName(), cdnaCoordinate, codonNumber, positionInCodon, cDNAindel.getSize().intValueExact()));
                                    inInsertion = true;
                                    for(int x = 0; x < cDNAindel.getSize().intValueExact() / 3; x++) {
                                        LOG.info(String.format("%s discarding amino acid %c", allele.getAlleleName(), translationIterator.next()));
                                        codonsDiscarded[0]++;
                                    }
                                    break;
                                }
                            }
                        }
                        // -----------------------------------------------------
                        
                        if(allele.getCodonMap().get(codonNumber - codonsDiscarded[0]) == null) {
                            if(translationIterator.hasNext()) {
                                if(allele.getCodonMap().size() > 0 || positionInCodon == 1) { // IMGT translation starts with first whole codon
                                    Codon codon = new Codon();
                                    codon.setCodonNumber(codonNumber - codonsDiscarded[0]);
                                    allele.getCodonMap().put(codon.getCodonNumber(), codon);
                                    codon.setAminoAcid(String.format("%c", translationIterator.next()));
                                }
                            }
                            else {
                                if(!(
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
                                )) {
                                    throw new RuntimeException(allele.getAlleleName() + " translation too short");
                                }
                            }
                        }
                    }
                });
                if(
                    // should not have any leftover translation
                    translationIterator.hasNext() 
                ) {
                    //throw new RuntimeException(allele.getAlleleName() + " translation too long");
                    while(translationIterator.hasNext()) {
                        LOG.info(String.format("%s unused translation %c", allele.getAlleleName(), translationIterator.next()));
                    }
                }
                allele.setNullAllele(allele.getAlleleName().endsWith("N"));
                {
                    String proteinSequence = allele.getCodonMap().values().stream().map(Codon::getAminoAcid).collect(Collectors.joining());
                    if(allele.getNullAllele()) {
                        if(proteinSequence.endsWith("X")) {
                            proteinSequence = proteinSequence.substring(0, proteinSequence.length() - 1);
                        }
                    }
                    allele.setProteinSequenceLength(proteinSequence.length());
                }
                // Synonymous logic requires alleles to be processed in order
                // (i.e., the "master" allele first).
                {
                    Pattern pattern = Pattern.compile("(HLA-DPB1\\*[0-9]*:[0-9]*)[:0-9N]*");
                    Matcher matcherThis = pattern.matcher(allele.getAlleleName());
                    matcherThis.find();
                    Allele synonymousAllele = null;
                    for(Allele candidateSynonymousAllele : alleleList) {
                        Matcher matcherSyn = pattern.matcher(candidateSynonymousAllele.getAlleleName());
                        matcherSyn.find();
                        if(allele != candidateSynonymousAllele && matcherThis.group(1).equals(matcherSyn.group(1))) {
                            synonymousAllele = candidateSynonymousAllele;
                            break;
                        }
                    }
                    if(synonymousAllele != null) {
                        String proteinSequenceThis = allele.getCodonMap().values().stream().map(Codon::getAminoAcid).collect(Collectors.joining());
                        String proteinSequenceSyn = synonymousAllele.getCodonMap().values().stream().map(Codon::getAminoAcid).collect(Collectors.joining());
                        if(allele.getNullAllele()) {
                            if(proteinSequenceThis.endsWith("X")) {
                                proteinSequenceThis = proteinSequenceThis.substring(0, proteinSequenceThis.length() - 1);
                            }
                        }
                        if(!(
                            proteinSequenceSyn.contains(proteinSequenceThis)
                            || proteinSequenceThis.contains(proteinSequenceSyn)
                        )) {
                            throw new RuntimeException(allele.getAlleleName() + " is not really synonymous with " + synonymousAllele.getAlleleName());
                        }
                        allele.setSynonymousAlleleName(synonymousAllele.getAlleleName());
                        allele.setSynonymousAlleleProteinShorter(!proteinSequenceSyn.contains(proteinSequenceThis));
                    }
                }
            });
            // Add sequence number for sorting. This isn't really used for
            // anything, but reflects the natural IMGT order, so I'll leave it
            // here just in case.
            int sequenceNumber = 0;
            for(Allele allele : alleleList) {
                allele.setSequenceNumber(sequenceNumber++);
            }
            // Strip out all but codons 1-100. We can always add them back
            // later.
            getAlleleList().stream().forEach((allele) -> { allele.setCodonMap(allele.getCodonMap().subMap(1, 100)); });
            LOG.info(String.format("%d HLA-DPB1 alleles loaded", alleleList.size()));
            LOG.info(String.format("version is %s", alleleList.get(0).getVersion()));
            
            // This is a little fix-up that will set the "master" allele for a
            // set of synonmous alleles to the allele with the LONGEST protein
            // sequence.
            {
                for(Allele allele : alleleList) {
                    if(allele.getSynonymousAlleleName() != null && allele.getSynonymousAlleleProteinShorter()) {
                        LOG.info(String.format("setting master synonymous allele from %s to %s", allele.getSynonymousAlleleName(), allele.getAlleleName()));
                        allele.setSynonymousAlleleName(null);
                        allele.setSynonymousAlleleProteinShorter(null);
                        Pattern pattern = Pattern.compile("(HLA-DPB1\\*[0-9]*:[0-9]*)[:0-9N]*");
                        Matcher matcherThis = pattern.matcher(allele.getAlleleName());
                        matcherThis.find();
                        for(Allele candidateSynonymousAllele : alleleList) {
                            Matcher matcherSyn = pattern.matcher(candidateSynonymousAllele.getAlleleName());
                            matcherSyn.find();
                            if(allele != candidateSynonymousAllele && matcherThis.group(1).equals(matcherSyn.group(1))) {
                                candidateSynonymousAllele.setSynonymousAlleleName(allele.getAlleleName());
                                candidateSynonymousAllele.setSynonymousAlleleProteinShorter(allele.getProteinSequenceLength() < candidateSynonymousAllele.getProteinSequenceLength());
                            }
                        }
                    }
                }
            }
            
        }
            
        return alleleList;
        
    }

    public void assignHypervariableRegionVariantIds(HypervariableRegionFinder hypervariableRegionFinder) {

        List<HypervariableRegion> hypervariableRegionList = hypervariableRegionFinder.getHypervariableRegionList();

        // 1. Ensure the source data is consistent with IMGT.
        hypervariableRegionList.stream().forEach((hypervariableRegion) -> { hypervariableRegion.getVariantMap().values().stream().forEach((variant) -> { variant.getBeadAlleleRefList().forEach((beadAlleleRef) -> {
            if(!(
                getAlleleList().stream().filter((allele) -> (allele.getAlleleName().startsWith(beadAlleleRef.getAlleleName()))).findFirst().isPresent()
            )) {
                throw new RuntimeException("Hypervariable region " + hypervariableRegion.getHypervariableRegionName() + " variant " + variant.getVariantId() + " bead allele " + beadAlleleRef.getAlleleName() + " does not exist in IMGT database");
            }
            getAlleleList().stream().filter((allele) -> (allele.getAlleleName().startsWith(beadAlleleRef.getAlleleName())) && allele.getSynonymousAlleleName() == null).findFirst().ifPresent((allele) -> {
                StringBuilder hvrProteinSequence = new StringBuilder();
                hypervariableRegion.getCodonNumberList().stream().forEach((codonNumber) -> {
                    Codon codon = allele.getCodonMap().get(codonNumber);
                    hvrProteinSequence.append(codon == null ? "*" : codon.getAminoAcid());
                });
                if(!(
                    variant.getProteinSequenceList().stream().filter((proteinSequence) -> (hvrProteinSequence.toString().equals(proteinSequence))).count() == 1
                )) {
                    throw new RuntimeException(
                        "Hypervariable region " + hypervariableRegion.getHypervariableRegionName() + " variant " + variant.getVariantId() + " bead allele " + beadAlleleRef.getAlleleName()
                        + " protein sequence is not consistent with IMGT database (hypervariable region: " + variant.getProteinSequenceList() + " / IMGT: " + hvrProteinSequence
                    );
                }
            });
        }); }); });

        // 2. Assign hypervariable region variants to alleles.
        
        // Note that I might add alleles to the list in the loop and that I want
        // the loop to iterate over those additional alleles. This is a form of
        // recursion.
        Map<String, Character> alleleSuffixes = new HashMap<>();
        for(int i = 0; i < getAlleleList().size(); i++) {
            
            Allele allele = getAlleleList().get(i);

            if(allele.getHvrVariantMap() == null) {
                allele.setHvrVariantMap(new TreeMap<>());
            }
            
            for(HypervariableRegion hypervariableRegion : hypervariableRegionList) {

                if(allele.getHvrVariantMap().get(hypervariableRegion.getHypervariableRegionName()) != null) {
                    continue;
                }
                
                StringBuilder hvrProteinSequence = new StringBuilder();
                hypervariableRegion.getCodonNumberList().stream().forEach((codonNumber) -> {
                    Codon codon = allele.getCodonMap().get(codonNumber);
                    if(codon != null) {
                        codon.setHypervariableRegionName(hypervariableRegion.getHypervariableRegionName());
                    }
                    hvrProteinSequence.append(codon == null ? "*" : codon.getAminoAcid());
                });
                Allele.HypervariableRegionVariantRef hvrvRef = new Allele.HypervariableRegionVariantRef();
                allele.getHvrVariantMap().put(hypervariableRegion.getHypervariableRegionName(), hvrvRef);
                hvrvRef.setHypervariableRegionName(hypervariableRegion.getHypervariableRegionName());
                hvrvRef.setVariantId(hvrProteinSequence.toString()); // default if the protein sequence does not match an established hypervariable region variant
                allele.setSingleAntigenBead(false);
                
                // If an allele matches multiple HVRVs, then new alleles with
                // "[a]", "[b]", "[c]", ... suffixes are created for all but
                // the first HVRV match. This preserves the cardinality of the
                // HVRV:allele as 1:many (avoids many:many) at the expense of
                // creating additional alleles. I'm going to call these
                // additional alleles "alternate alleles" to distinguish them
                // from the "primary allele" for the moment and hope they go
                // away.
                // THIS IS WAY TOO COMPLICATED. I HAVE EFFECTIVELY RETIRED THIS
                // BY NOT CREATING ANY REAGENT LOTS WHERE ONE ALLELE HAS
                // MULTIPLE HYPERVARIABLE REGION VARIANT ASSIGNMENTS FOR THE
                // SAME HYPERVARIABLE REGION.
                int matchIndex = 0;
                for(HypervariableRegionVariant variant : hypervariableRegion.getVariantMap().values()) {
                    for(String proteinSequence : variant.getProteinSequenceList()) {
                        if(hvrProteinSequence.toString().equals(proteinSequence)) {
                            Allele workingAllele;
                            if(matchIndex == 0) {
                                workingAllele = allele;
                            }
                            else {
                                workingAllele = (Allele)SerializationUtils.clone((Serializable)allele);
                                String baseAlleleName = workingAllele.getAlleleName().replaceAll("\\[.*\\]", "");
                                char alleleSuffix = alleleSuffixes.get(baseAlleleName) != null ? ((char)((int)alleleSuffixes.get(baseAlleleName) + 1)) : 'a';
                                alleleSuffixes.put(baseAlleleName, alleleSuffix);
                                workingAllele.setAlleleName(String.format("%s[%c]", baseAlleleName, alleleSuffix));
                                getAlleleList().add(workingAllele);
                            }
                            workingAllele.getHvrVariantMap().get(hypervariableRegion.getHypervariableRegionName()).setVariantId(variant.getVariantId());
                            variant.getBeadAlleleRefList().stream().filter((beadAlleleRef) -> (workingAllele.getAlleleName().startsWith(beadAlleleRef.getAlleleName()))).findFirst().ifPresent((beadAlleleRef) -> {
                                workingAllele.setSingleAntigenBead(true);
                            });
                            matchIndex++;
                        }
                    }
                }
                
            };
            
        };
        
        Collections.sort(getAlleleList());
        
    }

    public void assignHypervariableRegionVariantMatches(String referenceAlleleName) {
        
        Allele referenceAllele = getAllele(referenceAlleleName);
        getAlleleList().stream().forEach((allele) -> {
            allele.setReferenceForMatches(referenceAllele.equals(allele));
            allele.setMatchesHvrCount(
                (int)referenceAllele.getHvrVariantMap().values().stream().filter((hvrVariant) -> (
                    !hvrVariant.getVariantId().contains("*")
                    && hvrVariant.getVariantId().equals(allele.getHvrVariantMap().get(hvrVariant.getHypervariableRegionName()).getVariantId()))
                ).count()
            );
            allele.getHvrVariantMap().values().stream().forEach((hvrVariant) -> {
                hvrVariant.setMatchesReference(
                    !hvrVariant.getVariantId().contains("*")
                    && referenceAllele.getHvrVariantMap().get(hvrVariant.getHypervariableRegionName()).getVariantId().equals(hvrVariant.getVariantId())
                );
            });
            allele.getCodonMap().values().stream().forEach((codon) -> {
                codon.setMatchesReference(false);
                if(referenceAllele.getCodonMap().get(codon.getCodonNumber()) != null) {
                    codon.setMatchesReference(referenceAllele.getCodonMap().get(codon.getCodonNumber()).getAminoAcid().equals(codon.getAminoAcid()));
                }
            });
        });
        
    }

    public void computeCompatInterpretation(HypervariableRegionFinder hypervariableRegionFinder) {

        // 1. Reset all compatibility attributes.
        getAlleleList().stream().forEach((allele) -> {
            allele.setCompatInterpretation(null);
        });
        hypervariableRegionFinder.getHypervariableRegionList().stream().forEach((hypervariableRegion) -> { hypervariableRegion.getVariantMap().values().stream().forEach((variant) -> {
            variant.setCompatIsRecipientEpitope(false);
            variant.setCompatPositiveSabCount(0);
            variant.setCompatPositiveSabPct(0);
            variant.setCompatAntibodyConsideredPresent(false);
            variant.getBeadAlleleRefList().stream().forEach((beadAlleleRef) -> { beadAlleleRef.setCompatBeadPositive(false); });
        }); });
        
        // 2. Determine which hypervariable region variants correspond to
        //    recipient epitopes.
        getAlleleList().stream().filter((allele) -> (allele.getRecipientTypeForCompat())).forEach((allele) -> {
            allele.getHvrVariantMap().values().stream().forEach((hvrVariant) -> {
                if(hvrVariant.getVariantId().matches("[0-9]*")) {
                    hypervariableRegionFinder.getHypervariableRegion(hvrVariant.getHypervariableRegionName()).getVariantMap().get(hvrVariant.getVariantId()).setCompatIsRecipientEpitope(true);
                }
            });
        });

        // 3. Determine the percentage of beads that are positive for each
        //    hypervariable region variant.
        getAlleleList().stream().filter((allele) -> (allele.getSingleAntigenBead() && allele.getRecipientAntibodyForCompat())).forEach((allele) -> {
            allele.getHvrVariantMap().values().stream().forEach((hvrVariant) -> {
                hypervariableRegionFinder.getHypervariableRegion(hvrVariant.getHypervariableRegionName()).getVariantMap().get(hvrVariant.getVariantId()).getBeadAlleleRefList().stream().filter((beadAlleleRef) -> (
                    allele.getAlleleName().startsWith(beadAlleleRef.getAlleleName())
                )).forEach((beadAlleleRef) -> {
                    beadAlleleRef.setCompatBeadPositive(true);
                });
            });
        });
        hypervariableRegionFinder.getHypervariableRegionList().stream().forEach((hypervariableRegion) -> { hypervariableRegion.getVariantMap().values().stream().forEach((variant) -> {
            variant.setCompatPositiveSabCount(new Long(variant.getBeadAlleleRefList().stream().filter((beadAlleleRef) -> (beadAlleleRef.getCompatBeadPositive())).count()).intValue());
            variant.setCompatPositiveSabPct(
                (100 * new Long(variant.getBeadAlleleRefList().stream().filter((beadAlleleRef) -> (beadAlleleRef.getCompatBeadPositive())).count()).intValue())
                / new Long(variant.getBeadAlleleRefList().stream().count()).intValue()
            );
        }); });

        // 4. Determine is an antibody for each hypervariable region variant
        //    epitope is considered to be present.
        hypervariableRegionFinder.getHypervariableRegionList().stream().forEach((hypervariableRegion) -> { hypervariableRegion.getVariantMap().values().stream().forEach((variant) -> {
            variant.setCompatAntibodyConsideredPresent(
                (variant.getCompatPositiveSabPct().equals(100) || variant.getKnownReactiveEpitopeForCompat())
                && !variant.getCompatIsRecipientEpitope()
            );
        }); });
        
        // 5. Deduce a compatibility status for each allele.
        getAlleleList().stream().forEach((allele) -> {
            if(allele.getRecipientAntibodyForCompat()) {
                if(allele.getRecipientTypeForCompat()) {
                    allele.setCompatInterpretation("AA");
                }
                else {
                    allele.setCompatInterpretation("I");
                }
            }
            else {
                int[] epitopeAntibodyCount = new int[] { 0 }; // wrapping for use in lambda
                allele.getHvrVariantMap().values().stream().forEach((hvrVariant) -> {
                    if(
                        hypervariableRegionFinder.getHypervariableRegion(hvrVariant.getHypervariableRegionName()).getVariantMap().values().stream().filter((variant) -> (
                            variant.getCompatAntibodyConsideredPresent() && hvrVariant.getVariantId().equals(variant.getVariantId())
                        )).count() == 1
                    ) {
                        epitopeAntibodyCount[0]++;
                    }
                });
                if(epitopeAntibodyCount[0] > 0) {
                    allele.setCompatInterpretation("EI");
                }
                else {
                    allele.setCompatInterpretation("NEI");
                }
            }
        });
        
    }
    
}
