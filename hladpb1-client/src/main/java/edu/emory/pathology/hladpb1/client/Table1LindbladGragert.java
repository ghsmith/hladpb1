package edu.emory.pathology.hladpb1.client;

import edu.emory.pathology.hladpb1.imgtdb.data.Allele;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegionVariant;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAttribute;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.RequestEntityProcessing;

/**
 *
 * @author Geoffrey H. Smith
 */
public class Table1LindbladGragert {

    static Client client;
    
    static {
        ClientConfig cc = new ClientConfig().connectorProvider(new ApacheConnectorProvider());        
        cc.property(ClientProperties.REQUEST_ENTITY_PROCESSING, RequestEntityProcessing.BUFFERED);
        client = ClientBuilder.newClient(cc);
    }
    
    // HLA-DPB1 web service endpoints (Jersey REST client)
    static public List<Allele> getAlleles() {
        return client
            .target("https://rest.hlatools.org/hladpb1/resources/alleles")
            .queryParam("synonymous", "false")
            .queryParam("noCodons", "true")
            .request(MediaType.APPLICATION_JSON)
            .get(new GenericType<List<Allele>>(){});
    }
    static public void putAllele(Allele allele) {
        client
            .target("https://rest.hlatools.org/hladpb1/resources/alleles/" + allele.getAlleleName())
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity(allele, MediaType.APPLICATION_JSON));
    }
    static public List<HypervariableRegion> getHypervariableRegions() {
        return client
            .target("https://rest.hlatools.org/hladpb1/resources/hypervariableRegions")
            .request(MediaType.APPLICATION_JSON)
            .get(new GenericType<List<HypervariableRegion>>(){});
    }
    static public void reset() {
        client
            .target("https://rest.hlatools.org/hladpb1/resources/session/reset")
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity("", MediaType.APPLICATION_JSON));
    }
    static public String getReagentLot() {
        return client
            .target("https://rest.hlatools.org/hladpb1/resources/hypervariableRegions/reagentLotNumber")
            .request(MediaType.APPLICATION_JSON)
            .get(new GenericType<String>(){});
    }
    static public void putReagentLot(String reagentLot) {
        client
            .target("https://rest.hlatools.org/hladpb1/resources/session/reagentLot")
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity(reagentLot, MediaType.TEXT_PLAIN));
    }

    // Kelsi (local static files)
    static public Map<String, Float> indexKelsi(String fileName, String columnName) throws IOException {
        Map<String, Float> alleleMap = new HashMap<>();
        CSVParser csvParser = CSVParser.parse(new File(fileName), Charset.defaultCharset(), CSVFormat.TDF.withFirstRecordAsHeader());
        for(CSVRecord csvRecord : csvParser) {
            alleleMap.put(csvRecord.get(0), Float.parseFloat(csvRecord.get(columnName)));
        }
        return alleleMap;
    }

    // Loren (Jersey REST client)
    static public class LorenCPRA { @XmlAttribute(name = "CPRA") public float cpra; };
    static public float getLorenCPRA(String nameList) {
        return client.target("http://transplanttoolbox.org/nmdp_cpra/cpra_rest/")
            .request(MediaType.APPLICATION_JSON)
            .post(
                Entity.entity("antigen_list=" + nameList, MediaType.APPLICATION_FORM_URLENCODED),
                LorenCPRA.class
            ).cpra;
    }
    
    public static void main(String[] args) throws IOException {
        
        Map<String, Float> kelsiOverall = indexKelsi("/home/priam/Downloads/lindblad/all_freq_tables/overall.txt", "Frequency");
        Map<String, Float> kelsiAsian = indexKelsi("/home/priam/Downloads/lindblad/all_freq_tables/asian.txt", "Frequency");
        Map<String, Float> kelsiBlack = indexKelsi("/home/priam/Downloads/lindblad/all_freq_tables/black.txt", "Frequency");
        Map<String, Float> kelsiHPI = indexKelsi("/home/priam/Downloads/lindblad/all_freq_tables/hawaii_pacific_islander.txt", "Frequency");
        Map<String, Float> kelsiHispanic = indexKelsi("/home/priam/Downloads/lindblad/all_freq_tables/hispanic.txt", "Frequency");
        Map<String, Float> kelsiMultiracial = indexKelsi("/home/priam/Downloads/lindblad/all_freq_tables/multiracial.txt", "Frequency");
        Map<String, Float> kelsiNativeAmerican = indexKelsi("/home/priam/Downloads/lindblad/all_freq_tables/native_american.txt", "Frequency");
        Map<String, Float> kelsiWhite = indexKelsi("/home/priam/Downloads/lindblad/all_freq_tables/white.txt", "Frequency");
        Map<String, Float> kelsiUA_n = indexKelsi("/home/priam/Downloads/lindblad/all_freq_tables/uas.txt", "n");
        Map<String, Float> kelsiUA_freq = indexKelsi("/home/priam/Downloads/lindblad/all_freq_tables/uas.txt", "Frequency");
        
        putReagentLot("LABScreen Class II Standard (various lots)");
        //putReagentLot("LABScreen Class II Standard w/combo HVRVs (various lots)");
        List<HypervariableRegion> hvrs = getHypervariableRegions();
        List<Allele> alleles = getAlleles();

        System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
            "HVR",
            "AA pos",
            "HVRV",
            "AA seq",
            "allele count",
            "overall[1]",
            "asian[1]",
            "black[1]",
            "HPI[1]",
            "hispanic[1]",
            "multiracial[1]",
            "native american[1]",
            "white[1]",
            "CPRA[2]",
            "name list"
        ));
        
        for(HypervariableRegion hvr : hvrs) {

            for(HypervariableRegionVariant hvrv : hvr.getVariantMap().values()) {

                int alleleCount = 0;
                for(Allele allele : alleles) {
                    if(allele.getHvrVariantMap().get(hvr.getHypervariableRegionName()).getVariantId().equals(hvrv.getVariantId())) {
                        if(allele.getAlleleName().matches("^.*[A-Z]$")) { // reject [N]ull and [Q]uestionable alleles
                            continue;
                        }
                        alleleCount++;
                    }
                }

                float kelsiOverallFreq = 0f;
                float kelsiAsianFreq = 0f;
                float kelsiBlackFreq = 0f;
                float kelsiHPIFreq = 0f;
                float kelsiHispanicFreq = 0f;
                float kelsiMultiracialFreq = 0f;
                float kelsiNativeAmericanFreq = 0f;
                float kelsiWhiteFreq = 0f;
                Set<String> usedKelsiAlleleNameSet = new HashSet<>();
                for(Allele allele : alleles) {
                    if(allele.getHvrVariantMap().get(hvr.getHypervariableRegionName()).getVariantId().equals(hvrv.getVariantId())) {
                        if(allele.getAlleleName().matches("^.*[A-Z]$")) {
                            continue;
                        }
                        String kelsiAlleleName = allele.getAlleleName().replaceAll("HLA-DPB1\\*([0-9][0-9][0-9]?:[0-9][0-9]).*", "$1");
                        if(usedKelsiAlleleNameSet.contains(kelsiAlleleName)) {
                            continue;
                        }
                        usedKelsiAlleleNameSet.add(kelsiAlleleName);
                        kelsiOverallFreq += kelsiOverall.get(kelsiAlleleName) != null ? kelsiOverall.get(kelsiAlleleName) : 0f;
                        kelsiAsianFreq += kelsiAsian.get(kelsiAlleleName) != null ? kelsiAsian.get(kelsiAlleleName) : 0f;
                        kelsiBlackFreq += kelsiBlack.get(kelsiAlleleName) != null ? kelsiBlack.get(kelsiAlleleName) : 0f;
                        kelsiHPIFreq += kelsiHPI.get(kelsiAlleleName) != null ? kelsiHPI.get(kelsiAlleleName) : 0f;
                        kelsiHispanicFreq += kelsiHispanic.get(kelsiAlleleName) != null ? kelsiHispanic.get(kelsiAlleleName) : 0f;
                        kelsiMultiracialFreq += kelsiMultiracial.get(kelsiAlleleName) != null ? kelsiMultiracial.get(kelsiAlleleName) : 0f;
                        kelsiNativeAmericanFreq += kelsiNativeAmerican.get(kelsiAlleleName) != null ? kelsiNativeAmerican.get(kelsiAlleleName) : 0f;
                        kelsiWhiteFreq += kelsiWhite.get(kelsiAlleleName) != null ? kelsiWhite.get(kelsiAlleleName) : 0f;
                    }
                }

                Float lorenCPRA = 0f;
                List<String> lorenUAList = new ArrayList<>();
                for(String proteinSequence : hvrv.getProteinSequenceList()) {
                    StringBuffer lorenUA = new StringBuffer();
                    lorenUA.append("DPB1_HVR_" + hvr.getHypervariableRegionName().toUpperCase());
                    for(int i = 0; i < proteinSequence.length(); i++) {
                        lorenUA.append("_" + hvr.getCodonNumberList().get(i) + proteinSequence.charAt(i));
                    }
                    lorenUAList.add(lorenUA.toString());
                }
                try {
                    lorenCPRA = getLorenCPRA(String.join(",", lorenUAList));
                }
                catch(RuntimeException e) {
                    lorenCPRA = null;
                }

                System.out.println(String.format("%s\t%s\t%s\t%s\t%d\t%5.4f\t%5.4f\t%5.4f\t%5.4f\t%5.4f\t%5.4f\t%5.4f\t%5.4f\t%s\t%s",
                    hvr.getHypervariableRegionName(),
                    hvr.getCodonNumberList(),
                    hvr.getHypervariableRegionName() + hvrv.getVariantId(),
                    hvrv.getProteinSequenceList(),
                    alleleCount,
                    kelsiOverallFreq,
                    kelsiAsianFreq,
                    kelsiBlackFreq,
                    kelsiHPIFreq,
                    kelsiHispanicFreq,
                    kelsiMultiracialFreq,
                    kelsiNativeAmericanFreq,
                    kelsiWhiteFreq,
                    lorenCPRA != null ? String.format("%5.4f", lorenCPRA) : "ERROR",
                    String.join(",", lorenUAList)
                ));

            }

        }
        
        System.out.println("IMGT allele database version: 3.36.0");
        System.out.println("Using reagent lot #: " + getReagentLot());
        System.out.println("[1] Kelsi Lindblad");
        System.out.println("[2] Loren Gragert");

        {
            int alleleCount = 0;
            int alleleCountWithAThroughF = 0;
            int alleleCountWithAThroughG = 0;
            for(Allele allele : alleles) {
                if(allele.getAlleleName().matches("^.*[A-Z]$")) { // reject [N]ull and [Q]uestionable alleles
                    continue;
                }
                alleleCount++;
                if(
                    allele.getHvrVariantMap().get("a").getVariantId().matches("^[0-9]")
                    && allele.getHvrVariantMap().get("b").getVariantId().matches("^[0-9]")
                    && allele.getHvrVariantMap().get("c").getVariantId().matches("^[0-9]")
                    && allele.getHvrVariantMap().get("d").getVariantId().matches("^[0-9]")
                    && allele.getHvrVariantMap().get("e").getVariantId().matches("^[0-9]")
                    & allele.getHvrVariantMap().get("f").getVariantId().matches("^[0-9]")
                ) {
                    alleleCountWithAThroughF++;
                    if(allele.getHvrVariantMap().get("g").getVariantId().matches("^[0-9]")) {
                        alleleCountWithAThroughG++;
                    }
                }
            }
            System.out.println(String.format("not-null allele count: %d", alleleCount));
            System.out.println(String.format("not-null allele count with HVR a-f defined: %d (%4.2f)", alleleCountWithAThroughF, (float)alleleCountWithAThroughF / alleleCount));
            System.out.println(String.format("not-null allele count with HVR a-g defined: %d (%4.2f)", alleleCountWithAThroughG, (float)alleleCountWithAThroughG / alleleCount));
        }
        
    }
    
}
