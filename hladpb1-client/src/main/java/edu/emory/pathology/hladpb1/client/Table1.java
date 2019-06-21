package edu.emory.pathology.hladpb1.client;

import edu.emory.pathology.hladpb1.imgtdb.data.Allele;
import edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
        + "  ar.patient_id patient_id, "
        + "  ar.day_collect_dt day_collect_dt, "
        + "  trim(replace(regexp_replace(ar.doc, '.*Specificity:(.*)Comments:(.*)Antibody.MFI.*', '\\1', 1, 1, 'n'), chr(10), ' ')) specificity, "
        + "  trim(replace(regexp_replace(ar.doc, '.*Specificity:(.*)Comments:(.*)Antibody.MFI.*', '\\2', 1, 1, 'n'), chr(10), ' ')) comments, "
        + "  trim(replace(tr.doc, chr(10), ' ')) hla_type "
        + "from "
        + "  antibody_results ar, "
        + "  type_results tr "
        + "where "
        + "  ar.patient_id = tr.patient_id "
        + "  and tr.rn = 1 "
        + "  and ar.day_collect_dt >= '01-MAY-2019' "
        + "  and ar.day_collect_dt < '01-JUN-2019' "
        + "  and regexp_like(ar.doc, '.*Specificity:(.*(DP[0-9]|DP:).*)Comments:(.*)Antibody.MFI.*', 'n') /* watch out for DPA */ "
        + "  and tr.doc like '[LOW RES]%' "
        + "  and 1 = 2 "
        + "order by "
        + "  1, "
        + "  2 ";

    // HLA-DPB1 web service endpoints (Jersey REST client)
    static ClientConfig cc = new ClientConfig().connectorProvider(new ApacheConnectorProvider());        
    static Client client = ClientBuilder.newClient(cc);
    static public List<Allele> getAlleles() {
        return client
            .target("https://rest.hlatools.org/hladpb1/resources/alleles")
            .queryParam("synonymous", "false")
            .queryParam("sab", "true")
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
    static public HypervariableRegion getHypervariableRegion(String hvrName) {
        return client
            .target("https://rest.hlatools.org/hladpb1/resources/hypervariableRegions/" + hvrName)
            .request(MediaType.APPLICATION_JSON)
            .get(HypervariableRegion.class);
    }
    static public void reset() {
        client
            .target("https://rest.hlatools.org/hladpb1/resources/session/reset")
            .request(MediaType.APPLICATION_JSON)
            .put(Entity.entity("", MediaType.APPLICATION_JSON));
    }
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        /*Class.forName("oracle.jdbc.driver.OracleDriver");
        conn = DriverManager.getConnection("jdbc:oracle:thin:@***:1521/***","***","***");
        conn.setAutoCommit(false);
        conn.createStatement().execute("set role all");

        ResultSet rs = conn.createStatement().executeQuery(resultSelect);*/
        
        List<Allele> alleles;
        Allele allele1;
        HypervariableRegion hvr;
        alleles = getAlleles();
        allele1 = alleles.stream().filter((allele) -> (allele.getAlleleName().startsWith("HLA-DPB1*01:01:01:01"))).findFirst().get();
        allele1.setRecipientAntibodyForCompat(true);
        putAllele(allele1);
        allele1 = alleles.stream().filter((allele) -> (allele.getAlleleName().startsWith("HLA-DPB1*03:01"))).findFirst().get();
        allele1.setRecipientAntibodyForCompat(true);
        putAllele(allele1);
        alleles = getAlleles();
        hvr = getHypervariableRegion("f");
        System.out.println(hvr.getVariantMap().get("1").getCompatPositiveSabCount());
        
        /*while(rs.next()) {

            System.out.println(rs.getString("SPECIFICITY"));
            //System.out.println(String.format("%s %s %s", rs.getString(1), rs.getString(2), rs.getString(3)));
            
        }

        rs.close();
        conn.close();*/
        
    }
    
}
