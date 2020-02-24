package edu.emory.pathology.epitopeFinder.client;

import edu.emory.pathology.epitopeFinder.imgtdb.data.EpRegEpitope;
import edu.emory.pathology.hladpb1.imgtdb.data.Allele;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class Table3 {

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
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException, FileNotFoundException {

        putReagentLot("LABScreen Class II Standard w/combo HVRVs (various lots)");
        Set<String> perfectMatches = new HashSet<>();
        Set<String> perfectMatchesNoG = new HashSet<>();
        Set<String> imperfectMatchesNoG_5 = new HashSet<>();
        Set<String> imperfectMatchesNoG_4 = new HashSet<>();
        Set<String> imperfectMatchesNoG_3 = new HashSet<>();
        Set<String> imperfectMatchesNoG_2 = new HashSet<>();
        Set<String> imperfectMatchesNoG_1 = new HashSet<>();
        Set<String> imperfectMatchesNoG_0 = new HashSet<>();
        List<Allele> as = getAlleles();
        as.stream().filter(a -> a.getSingleAntigenBead()).forEach(a -> {
            a.setReferenceForMatches(true);
            putAllele(a);
            List<Allele> bs = getAlleles();
            bs.stream().filter(b -> !b.getSingleAntigenBead() && !b.getNullAllele()).forEach(b -> {
                if(b.getMatchesHvrCount() == 7) {
                    perfectMatches.add(b.getAlleleName());
                }
                if((b.getMatchesHvrCount() == 6 && !b.getHvrVariantMap().get("g").getMatchesReference())
                   || (b.getMatchesHvrCount() == 7 && b.getHvrVariantMap().get("g").getMatchesReference())) {
                    perfectMatchesNoG.add(b.getAlleleName());
                }
                if((b.getMatchesHvrCount() == 5 && !b.getHvrVariantMap().get("g").getMatchesReference())
                   || (b.getMatchesHvrCount() == 6 && b.getHvrVariantMap().get("g").getMatchesReference())) {
                    imperfectMatchesNoG_5.add(b.getAlleleName());
                }
                if((b.getMatchesHvrCount() == 4 && !b.getHvrVariantMap().get("g").getMatchesReference())
                   || (b.getMatchesHvrCount() == 5 && b.getHvrVariantMap().get("g").getMatchesReference())) {
                    imperfectMatchesNoG_4.add(b.getAlleleName());
                }
                if((b.getMatchesHvrCount() == 3 && !b.getHvrVariantMap().get("g").getMatchesReference())
                   || (b.getMatchesHvrCount() == 4 && b.getHvrVariantMap().get("g").getMatchesReference())) {
                    imperfectMatchesNoG_3.add(b.getAlleleName());
                }
                if((b.getMatchesHvrCount() == 2 && !b.getHvrVariantMap().get("g").getMatchesReference())
                   || (b.getMatchesHvrCount() == 3 && b.getHvrVariantMap().get("g").getMatchesReference())) {
                    imperfectMatchesNoG_2.add(b.getAlleleName());
                }
                if((b.getMatchesHvrCount() == 1 && !b.getHvrVariantMap().get("g").getMatchesReference())
                   || (b.getMatchesHvrCount() == 2 && b.getHvrVariantMap().get("g").getMatchesReference())) {
                    imperfectMatchesNoG_1.add(b.getAlleleName());
                }
                if((b.getMatchesHvrCount() == 0 && !b.getHvrVariantMap().get("g").getMatchesReference())
                   || (b.getMatchesHvrCount() == 1 && b.getHvrVariantMap().get("g").getMatchesReference())) {
                    imperfectMatchesNoG_0.add(b.getAlleleName());
                }
            });
        });
        System.out.println(as.size());
        System.out.println(as.stream().filter(a -> !a.getNullAllele()).count());
        System.out.println(perfectMatches.size());
        System.out.println(perfectMatchesNoG.size());
        System.out.println(imperfectMatchesNoG_5.size());
        System.out.println(imperfectMatchesNoG_4.size());
        System.out.println(imperfectMatchesNoG_3.size());
        System.out.println(imperfectMatchesNoG_2.size());
        System.out.println(imperfectMatchesNoG_1.size());
        System.out.println(imperfectMatchesNoG_0.size());
    }
    
}
