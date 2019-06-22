package edu.emory.pathology.epitopeFinder.client;

import edu.emory.pathology.epitopeFinder.imgtdb.data.Allele;
import edu.emory.pathology.epitopeFinder.imgtdb.data.EpRegEpitope;
import static edu.emory.pathology.hladpb1.client.Table1.getHypervariableRegions;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegionVariant;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
public class Table2 {

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
    static public List<Allele> getAlleles() {
        return client
            .target("https://rest.hlatools.org/epitopeFinder/resources/alleles")
            .queryParam("locusGroup", "DP")
            .request(MediaType.APPLICATION_JSON)
            .get(new GenericType<List<Allele>>(){});
    }
    static public void putAllele(Allele allele) {
        client
            .target("https://rest.hlatools.org/epitopeFinder/resources/alleles/" + allele.getAlleleName())
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity(allele, MediaType.APPLICATION_JSON));
    }
    static public List<EpRegEpitope> getEpitopes() {
        return client
            .target("https://rest.hlatools.org/epitopeFinder/resources/epRegEpitopes/DP")
            .request(MediaType.APPLICATION_JSON)
            .get(new GenericType<List<EpRegEpitope>>(){});
    }
    static public void reset() {
        client
            .target("https://rest.hlatools.org/epitopeFinder/resources/session/reset")
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity("", MediaType.APPLICATION_JSON));
    }

    // for sorting the epitope list (I'm surprised I didn't do this in the service)
    public static class EpitopeComparator implements Comparator<EpRegEpitope> {
        Pattern pat = Pattern.compile("^([0-9]*)(.*)");
        @Override
        public int compare(EpRegEpitope o1, EpRegEpitope o2) {
            Matcher mat1 = pat.matcher(o1.getEpitopeName()); mat1.find();
            Matcher mat2 = pat.matcher(o2.getEpitopeName()); mat2.find();
            String s1 = String.format("%05d%s", Integer.parseInt(mat1.group(1)), mat1.group(2));
            String s2 = String.format("%05d%s", Integer.parseInt(mat2.group(1)), mat2.group(2));
            return s1.compareTo(s2);
        }
    }
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        Class.forName("oracle.jdbc.driver.OracleDriver");
        conn = DriverManager.getConnection("jdbc:oracle:thin:@***:1521/***","***","***");
        conn.setAutoCommit(false);
        conn.createStatement().execute("set role all");

        // report header
        {
            List<EpRegEpitope> eps = getEpitopes();
            Collections.sort(eps, new EpitopeComparator());
            System.out.print("collectionDt, ptHash, abCount");
            for(EpRegEpitope ep : eps) {
                System.out.print(String.format(", %s",
                    ep.getEpitopeName().replace("<sup>", "_").replace("</sup>", "")
                ));
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
                    specificities.add(String.format("%02d:", Integer.parseInt(a))); // pad to 2 digits and add colon
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
            reset();
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
            List<EpRegEpitope> eps = getEpitopes();
            Collections.sort(eps, new EpitopeComparator());
            
            // report body
            System.out.print(String.format("%s, %s, %d",
                rs.getString("DAY_COLLECT_DT"),
                rs.getString("PT_HASH"),
                alleles.stream().filter((a) -> a.getRecipientAntibodyForCompat()).count()
            ));
            for(EpRegEpitope ep : eps) {
                if(ep.getCompatSabPanelPctPresent() > 0) {
                    System.out.print(String.format(", %d/%d/%d",
                        ep.getCompatSabPanelCountPresent() + ep.getCompatSabPanelCountAbsent(),
                        ep.getCompatSabPanelCountAbsent(),
                        alleles.stream().filter((a) -> a.getRecipientAntibodyForCompat()).count() - ep.getCompatSabPanelCountPresent()
                    ));
                }
                else {
                    System.out.print(String.format(", "));
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
