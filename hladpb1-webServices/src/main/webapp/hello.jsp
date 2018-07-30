<%-- 
    Document   : hello
    Created on : Jul 29, 2018, 2:34:43 PM
    Author     : priam
--%>

<%@page import="edu.emory.pathology.hladpb1.imgtdb.data.Allele"%>
<%@page import="edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion"%>
<%@page import="edu.emory.pathology.hladpb1.imgtdb.HypervariableRegionFinder"%>
<%@page import="edu.emory.pathology.hladpb1.imgtdb.AlleleFinder"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        
<pre>
<%

    AlleleFinder alleleFinder = new AlleleFinder(request.getServletContext().getInitParameter("imgtXmlFileName"));
    List<Allele> alleleList = alleleFinder.getAlleleList();
    HypervariableRegionFinder hypervariableRegionFinder = new HypervariableRegionFinder(request.getServletContext().getInitParameter("emoryXmlFileName"), "10");
    List<HypervariableRegion> hypervariableRegionList = hypervariableRegionFinder.getHypervariableRegionList();
    alleleFinder.assignHypervariableRegionVariantIds(hypervariableRegionList);
    alleleFinder.assignHypervariableRegionVariantMatches("HLA-DPB1*01:01:01:01");
    for(Allele allele : alleleList) {
        out.print(String.format("%-25s %-30s %1s %-25s %1s %1s %1d: ", allele.getAlleleName(), allele.getHvrVariantMap().values(), allele.getNullAllele() ? "Y" : "N", allele.getSynonymousAlleleName(), allele.getSynonymousAlleleProteinShorter() != null && allele.getSynonymousAlleleProteinShorter() ? "Y": "N", allele.getSingleAntigenBead() ? "Y" : "N", allele.getHvrMatchCount()));
        for(int i = 1; i <= 100; i++) {
            out.print(allele.getCodonMap().get(i) != null ? allele.getCodonMap().get(i).getAminoAcid() : "*");
        }
        out.println();
    }

%>
</pre>        
        
    </body>
</html>
