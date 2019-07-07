package edu.emory.pathology.hladpb1.client;

import edu.emory.pathology.hladpb1.imgtdb.data.Allele;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegionVariant;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;

/**
 *
 * @author Geoffrey H. Smith
 */
public class Table1 {

    // data warehouse (JDBC)
    static Connection conn;
    static String resultSelect =
          "with antibody_results as "
        + "( "
        + "  select "
        + "    lp.patient_id patient_id, "
        + "    ldc.day_collect_dt day_collect_dt, "
        + "    ( "
        + "      select "
        + "        count(*) "
        + "      from "
        + "        ehcvw.fact_event_document "
        + "      where "
        + "        order_key = frl.order_key "
        + "    ) "
        + "    count_fact_event_document, "
        + "    ( "
        + "      select "
        + "        listagg "
        + "        ( "
        + "          dbms_lob.substr "
        + "          ( "
        + "            event_clob_doc, "
        + "            dbms_lob.instr(event_clob_doc, 'Electronically Signed By', dbms_lob.instr(event_clob_doc, 'Single Antigen Bead II')) - dbms_lob.instr(event_clob_doc, 'Single Antigen Bead II'), "
        + "            dbms_lob.instr(event_clob_doc, 'Single Antigen Bead II') "
        + "          ), "
        + "          '@' "
        + "        ) within group(order by event_document_key) "
        + "      from "
        + "        ehcvw.fact_event_document "
        + "      where "
        + "        order_key = frl.order_key "
        + "    ) "
        + "    doc "
        + "  from "
        + "    ehcvw.fact_result_lab frl "
        + "    join ehcvw.lkp_order_lab lol on (lol.order_key = frl.order_key) "
        + "      join  ehcvw.lkp_day_collect ldc on (ldc.day_collect_key = lol.day_collect_key) "
        + "    join ehcvw.lkp_patient lp on (lp.patient_key = frl.patient_key) "
        + "  where    "
        + "    frl.structured_result_type_key = 1982 /* HLA class II antibodies */ "
        + "  order by "
        + "    ldc.day_collect_dt "
        + "), "
        + "type_results as "
        + "( "
        + "  select "
        + "    lp.patient_id, "
        + "    ldc.day_collect_dt day_collect_dt, "
        + "    ( "
        + "      select "
        + "        count(*) "
        + "      from "
        + "        ehcvw.fact_event_document "
        + "      where "
        + "        order_key = frl.order_key "
        + "    ) "
        + "    count_fact_event_document, "
        + "    ( "
        + "      select "
        + "        decode(structured_result_type_key, 2345, '[LOW RES]', 2344, '[HIGH RES]', '[? RES]') || "
        + "        listagg "
        + "        ( "
        + "          dbms_lob.substr "
        + "          ( "
        + "            event_clob_doc, "
        + "            dbms_lob.instr(event_clob_doc, '==', dbms_lob.instr(event_clob_doc, chr(10) || 'DR')) - dbms_lob.instr(event_clob_doc, chr(10) || 'DR'), "
        + "            dbms_lob.instr(event_clob_doc, chr(10) || 'DR') "
        + "         ), "
        + "          '@' "
        + "        ) within group(order by event_document_key) "
        + "      from "
        + "        ehcvw.fact_event_document "
        + "      where "
        + "        order_key = frl.order_key "
        + "    ) "
        + "    doc, "
        + "    row_number() over (partition by patient_id order by structured_result_type_key desc, day_collect_dt desc) rn /* sort low res first */ "
        + "  from "
        + "    ehcvw.fact_result_lab frl "
        + "    join ehcvw.lkp_order_lab lol on (lol.order_key = frl.order_key) "
        + "      join  ehcvw.lkp_day_collect ldc on (ldc.day_collect_key = lol.day_collect_key) "
        + "    join ehcvw.lkp_patient lp on (lp.patient_key = frl.patient_key) "
        + "  where    "
        + "    frl.structured_result_type_key in (2345, 2344) /* HLA class II low res type, high res type */ "
        + "  order by "
        + "    ldc.day_collect_dt "
        + ") "
        + "select "
        + "  to_char(ar.day_collect_dt, 'YYYY-MM-DD') day_collect_dt, "
        + "  substr(lower(rawtohex(utl_raw.cast_to_raw(sys.dbms_obfuscation_toolkit.md5(input_string => ar.patient_id || '***')))), 1, 8) pt_hash, "
        + "  trim(replace(regexp_replace(ar.doc, '.*Specificity:(.*)Comments:(.*)Antibody.MFI.*', '\\1', 1, 1, 'n'), chr(10), ' ')) specificity, "
        + "  trim(replace(regexp_replace(ar.doc, '.*Specificity:(.*)Comments:(.*)Antibody.MFI.*', '\\2', 1, 1, 'n'), chr(10), ' ')) comments, "
        + "  trim(replace(tr.doc, chr(10), ' ')) hla_type "
        + "from "
        + "  antibody_results ar, "
        + "  type_results tr "
        + "where "
        + "  ar.patient_id = tr.patient_id "
        + "  and tr.rn = 1 "
        + "  and ar.day_collect_dt >= '01-DEC-2018' "
        + "  and ar.day_collect_dt < '01-JUN-2019' "
        + "  and regexp_like(ar.doc, '.*Specificity:(.*(DP[0-9]|DP:).*)Comments:(.*)Antibody.MFI.*', 'n') /* watch out for DPA */ "
        + "  and tr.doc like '[LOW RES]%' "
        + "order by "
        + "  ar.day_collect_dt desc, "
        + "  2 ";

    // HLA-DPB1 web service endpoints (Jersey REST client)
    static ClientConfig cc = new ClientConfig().connectorProvider(new ApacheConnectorProvider());        
    static Client client = ClientBuilder.newClient(cc);
    static public void putReagentLot(String reagentLotNumber) {
        client
            .target("https://rest.hlatools.org/hladpb1/resources/session/reagentLot")
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity(reagentLotNumber, MediaType.APPLICATION_JSON));
    }
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
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        Class.forName("oracle.jdbc.driver.OracleDriver");
        conn = DriverManager.getConnection("jdbc:oracle:thin:@***:1521/***","***","***");
        conn.setAutoCommit(false);
        conn.createStatement().execute("set role all");

        putReagentLot("LABScreen Class II Standard w/combinatorial HVRVs (various lots)");
        
        // report header
        {
            List<HypervariableRegion> hvrs = getHypervariableRegions();
            System.out.print("collectionDt, ptHash, abCount");
            for(HypervariableRegion hvr : hvrs) {
                for(HypervariableRegionVariant hvrv : hvr.getVariantMap().values()) {
                    System.out.print(String.format(", %s%s",
                        hvr.getHypervariableRegionName(),
                        hvrv.getVariantId()
                    ));
                }
            }
            System.out.print(", hlaTypes, abSpecs");
            System.out.println();
        }
        
        ResultSet rs = conn.createStatement().executeQuery(resultSelect);
        
        while(rs.next()) {

            List<String> specificities = new ArrayList<>();
            List<String> hlaTypes = new ArrayList<>();

            // parse the DP specificities
            String specificityUnparsed = rs.getString("SPECIFICITY").replace(":", ""); // some people using "DP:" and some people don't
            {
                Pattern pat = Pattern.compile("DP([0-9 ]*)");
                Matcher mat = pat.matcher(specificityUnparsed);
                mat.find();
                for(String a : mat.group(1).split(" ")) {
                    specificities.add(String.format("%02d:01", Integer.parseInt(a))); // pad to 2 digits
                    // if "DP4," also add "04:02"
                    if(a.equals("4")) {
                        specificities.add("04:02");
                    }
                }
            }

            // parse the "allele-specific" specificities from the comments
            String commentsUnparsed = rs.getString("COMMENTS");
            if(commentsUnparsed != null) {
                Pattern pat = Pattern.compile("DPB1\\*([0-9:]*)");
                Matcher mat = pat.matcher(commentsUnparsed);
                while(mat.find()) {
                    specificities.add(mat.group(1));
                }
            }

            // parse the HLA type
            String hlaTypeUnparsed = rs.getString("HLA_TYPE");
            {
                Pattern pat = Pattern.compile("DPB1\\*.([0-9:]*)[A-Z]?.(([0-9:]*)[A-Z]?|XX)"); // some of the things that look like spaces aren't (vertical tabs?)
                Matcher mat = pat.matcher(hlaTypeUnparsed);
                if(!mat.find()) {
                    continue;
                }
                hlaTypes.add(mat.group(1) + (mat.group(1).contains(":") ? "" : ":")); // add colon if one is not present
                if(mat.group(3) != null && mat.group(3).length() > 0) {
                    hlaTypes.add(mat.group(3) + (mat.group(3).contains(":") ? "" : ":")); // add colon if one is not present
                }
            }
            
            // use the web service
            //
            // since this is using a starts-with allele name match, it will set
            // the antibody flag on the primary and all alternate alleles
            reset();
            putReagentLot("LABScreen Class II Standard w/combinatorial HVRVs (various lots)");
            List<Allele> alleles = getAlleles();
            for(String specificity : specificities) {
                alleles.stream().filter((a) -> a.getAlleleName().startsWith("HLA-DPB1*" + specificity)).forEach(
                    (a) -> {
                        a.setRecipientAntibodyForCompat(true);
                        putAllele(a);
                    }
                );
            }
            for(String hlaType : hlaTypes) {
                alleles.stream().filter((a) -> a.getAlleleName().startsWith("HLA-DPB1*" + hlaType)).forEach(
                    (a) -> {
                        a.setRecipientTypeForCompat(true);
                        putAllele(a);
                    }
                );
            }
            alleles = getAlleles();
            List<HypervariableRegion> hvrs = getHypervariableRegions();

            // report body
            //
            // note that the total antibody count only considers the primary and
            // not alterate alleles; both primary and alternate alleles are
            // specified as antibodies (alterate alleles names end with a
            // closing square bracket)
            System.out.print(String.format("%s, %s, %d",
                rs.getString("DAY_COLLECT_DT"),
                rs.getString("PT_HASH"),
                alleles.stream().filter((a) -> a.getRecipientAntibodyForCompat() && !a.getAlleleName().endsWith("]")).count()
            ));
            for(HypervariableRegion hvr : hvrs) {
                for(HypervariableRegionVariant hvrv : hvr.getVariantMap().values()) {
                    System.out.print(String.format(", %d/%d/%d%s",
                        hvrv.getBeadAlleleRefList().size(),
                        hvrv.getBeadAlleleRefList().size() - hvrv.getCompatPositiveSabCount(),
                        alleles.stream().filter((a) -> a.getRecipientAntibodyForCompat() && !a.getAlleleName().endsWith("]")).count() - hvrv.getCompatPositiveSabCount(),
                        hvrv.getCompatIsRecipientEpitope() ? "*" : ""
                    ));
                }
            }
            System.out.print(String.format(", \"%s\"", hlaTypes));
            System.out.print(String.format(", \"%s\"", specificities));
            System.out.println();
            
        }

        rs.close();
        conn.close();
        
    }
    
}
