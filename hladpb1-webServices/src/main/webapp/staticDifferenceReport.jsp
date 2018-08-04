<!DOCTYPE html>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.util.Comparator"%>
<%@page import="edu.emory.pathology.hladpb1.imgtdb.data.Allele"%>
<%@page import="edu.emory.pathology.hladpb1.imgtdb.AlleleFinder"%>
<%@page import="edu.emory.pathology.hladpb1.imgtdb.data.HypervariableRegion"%>
<%@page import="edu.emory.pathology.hladpb1.imgtdb.HypervariableRegionFinder"%>
<%@page import="java.util.List"%>

<%

    // Get finders from session.
    HypervariableRegionFinder hypervariableRegionFinder = (HypervariableRegionFinder)request.getSession().getAttribute("hypervariableRegionFinder");
    AlleleFinder alleleFinder = (AlleleFinder)request.getSession().getAttribute("alleleFinder");

    // Get hypervariable regions and non-synonymous alleles.
    List<HypervariableRegion> hypervariableRegions = hypervariableRegionFinder.getHypervariableRegionList();
    List<Allele> alleles = alleleFinder.getAlleleList().stream().filter((allele) -> (allele.getSynonymousAlleleName() == null)).collect(Collectors.toList());

    // Process URL parameters.
    String referenceAlleleName = request.getParameter("referenceAlleleName") == null ? alleles.get(0).getAlleleName() : request.getParameter("referenceAlleleName");
    String sortColumn = request.getParameter("sortColumn") == null ? "alleleName" : request.getParameter("sortColumn");
    Boolean sabOnly = request.getParameter("sabOnly") == null ? false : request.getParameter("sabOnly").equals("true");
    Boolean matchesHvrCountGe4Only = request.getParameter("matchesHvrCountGe4Only") == null ? true : request.getParameter("matchesHvrCountGe4Only").equals("true");

    // Assign the reference allele so all the matches properties are recomputed.
    alleleFinder.assignHypervariableRegionVariantMatches(referenceAlleleName);
    
    // Sort the alleles.
    List<Allele> sortedAndFilteredAlleles = new ArrayList(alleles);
    if("alleleName".equals(sortColumn)) { // alleleName
        sortedAndFilteredAlleles.sort(new Comparator<Allele>() {
            public int compare(Allele a, Allele b) {
                return a.getSequenceNumber().compareTo(b.getSequenceNumber());
            }
        });
    }
    else if("matchesHvrCount".equals(sortColumn)) { // matchesHvrCount (reverse) then alleleName
        sortedAndFilteredAlleles.sort(new Comparator<Allele>() {
            public int compare(Allele a, Allele b) {
                return a.getMatchesHvrCount().compareTo(b.getMatchesHvrCount()) == 0 ? a.getSequenceNumber().compareTo(b.getSequenceNumber()) : -1 * a.getMatchesHvrCount().compareTo(b.getMatchesHvrCount());
            }
        });
    }

    // Filter the alleles.
    if(sabOnly) {
        sortedAndFilteredAlleles = sortedAndFilteredAlleles.stream().filter((allele) -> (allele.getSingleAntigenBead())).collect(Collectors.toList());
    }
    if(matchesHvrCountGe4Only) {
        sortedAndFilteredAlleles = sortedAndFilteredAlleles.stream().filter((allele) -> (allele.getMatchesHvrCount() >= 4)).collect(Collectors.toList());
    }
    
    // Expose Java variables for JSTL tags
    request.setAttribute("hypervariableRegions", hypervariableRegions);
    request.setAttribute("alleles", alleles);
    request.setAttribute("sortedAndFilteredAlleles", sortedAndFilteredAlleles);
    request.setAttribute("reagentLotNumber", hypervariableRegionFinder.getReagentLotNumber());
    request.setAttribute("referenceAlleleName", alleles.stream().filter((allele) -> { return referenceAlleleName.equals(allele.getAlleleName()); }).findFirst().get().getAlleleName());
    request.setAttribute("sortColumn", sortColumn);
    request.setAttribute("sabOnly", sabOnly);
    request.setAttribute("matchesHvrCountGe4Only", matchesHvrCountGe4Only);

%>    

<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<html>

<head>

<meta name="viewport" content="width=device-width, initial-scale=0.86, maximum-scale=3.0, minimum-scale=0.86">

<style>
           
body {
    font-family: monospace;
    font-size: medium;
}
table {
    font-family: monospace;
    font-size: medium;
    border-spacing: 0px;
    border-collapse: collapse;
}
tr.referenceAllele {
    background-color: yellow;
}
th, td {
    text-align: center;
    white-space: nowrap;
}
th.index, td.index {
    border-left: 3px solid black;
}
th.alleleName, td.alleleName {
    text-align: left;
}
th.hvrId, td.hvrId {
    text-align: center;
}
th.codon, td.codon {
    text-align: right;
}
td.codon.hypervariableRegion {
    font-weight: bold;
    border: 1px solid black;
}
td.mismatch {
    background-color: lightgray;
}
a.filterEnabled {
    background-color: yellow;
}

</style>

<title>HLA-DPB1 Allele Difference Report</title>
        
</head>

<body>

    <form style="display: none;">
        <input name="referenceAlleleName" type="hidden" value="${referenceAlleleName}"/>
        <input name="sortColumn" type="hidden" value="${sortColumn}"/>
        <input name="sabOnly" type="hidden" value="${sabOnly}"/>
        <input name="matchesHvrCountGe4Only" type="hidden" value="${matchesHvrCountGe4Only}"/>
    </form>

    <h1>HLA-DPB1 Allele Difference Report (<a href="https://github.com/ghsmith/hladpb1/blob/master/hladpb1-webServices/src/main/webapp/staticDifferenceReport.html">static version</a>)</h1>
    
    <p>
        The reference allele for hypervariable region matching is highlighted in
        yellow. This report only shows the lowest allele number for synonymous
        alleles. Select a reference allele from the list or click on a row to
        set that allele as the reference allele. By default, this report only
        shows alleles where there are at least 4 hypervariable region matches to
        the reference allele, but you may toggle this filter on and off.
    </p>   
    <p>
        The IMGT allele database version is ${reagentLotNumber} and the single
        antigen bead (SAB) reagent lot number is ${alleles[0].version}.
    </p>
    <p>
        This report is for research use only.
    </p>
    <p>
        reference allele:
        <select onchange="document.forms[0].referenceAlleleName.value = this.value; document.forms[0].submit();">
            <c:forEach items="${alleles}" var="allele">
                <option value="${allele.alleleName}" ${referenceAlleleName == allele.alleleName ? "selected='true'" : ""}>${allele.alleleName}</option>
            </c:forEach>
        </select>
    </p>
    
    <table>
        <thead>
            <tr>
                <th class="alleleName">[<a id="alleleNameSort" href="javascript:{ document.forms[0].sortColumn.value = 'alleleName'; document.forms[0].submit(); };">sort</a>]</th>
                <th>[<a href="javascript:{ document.forms[0].sabOnly.value = '${sabOnly ? "false" : "true"}'; document.forms[0].submit(); };" ${sabOnly ? "class='filterEnabled'" : ""}>filter=Y</a>]</th>
                <th>[<a href="javascript:{ document.forms[0].matchesHvrCountGe4Only.value = '${matchesHvrCountGe4Only ? "false" : "true"}'; document.forms[0].submit(); };" ${matchesHvrCountGe4Only ? "class='filterEnabled'" : ""}>filter&ge;4)</a>]<br/>[<a href="javascript:{ document.forms[0].sortColumn.value = 'matchesHvrCount'; document.forms[0].submit(); };">sort</a>]</th>
                <th colspan="6" style="border-bottom: 1px solid black;">hypervariable<br/>region ID</th>
                <th>&nbsp;</th>
                <th colspan="100" style="border-bottom: 1px solid black;">protein sequence (aligned to HLA-DPB1*01:01 codons 1-100)</th>
            </tr>
            <tr>
                <th class="alleleName">allele name</th>
                <th>SAB</th>
                <th>matches</th>
                <th>a</th>
                <th>b</th>
                <th>c</th>
                <th>d</th>
                <th>e</th>
                <th>f</th>
                <th>&nbsp;</th>
                <c:forEach var="codonNumber" begin="1" end="100"><th class="codon ${codonNumber % 10 == 0 ? "index" : ""}">${codonNumber % 10 == 0 ? codonNumber : "&nbsp;"}</th></c:forEach>
            </tr>
            <tr>
                <th>&nbsp;</th>
                <th>&nbsp;</th>
                <th>&nbsp;</th>
                <th>&nbsp;</th>
                <th>&nbsp;</th>
                <th>&nbsp;</th>
                <th>&nbsp;</th>
                <th>&nbsp;</th>
                <th>&nbsp;</th>
                <th>&nbsp;</th>
                <c:forEach var="codonNumber" begin="1" end="100"><th class="codon ${codonNumber % 10 == 0 ? "index" : ""}">${alles[0].codonMap[codonNumber].hypervariableRegionName}</th></c:forEach>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${sortedAndFilteredAlleles}" var="allele">
                <tr ${referenceAlleleName == allele.alleleName ? "class='referenceAllele'" : ""} onclick="document.forms[0].referenceAlleleName.value = '${allele.alleleName}'; document.forms[0].submit();">
                    <td class="alleleName">${allele.alleleName}</td>
                    <td>${allele.singleAntigenBead ? "Y" : "&nbsp;"}</td>
                    <td>${allele.matchesHvrCount}</td>
                    <td ${allele.hvrVariantMap["a"].matchesReference ? "" : "class='mismatch'"}>a${allele.hvrVariantMap["a"].variantId}</td>
                    <td ${allele.hvrVariantMap["b"].matchesReference ? "" : "class='mismatch'"}>b${allele.hvrVariantMap["b"].variantId}</td>
                    <td ${allele.hvrVariantMap["c"].matchesReference ? "" : "class='mismatch'"}>c${allele.hvrVariantMap["c"].variantId}</td>
                    <td ${allele.hvrVariantMap["d"].matchesReference ? "" : "class='mismatch'"}>d${allele.hvrVariantMap["d"].variantId}</td>
                    <td ${allele.hvrVariantMap["e"].matchesReference ? "" : "class='mismatch'"}>e${allele.hvrVariantMap["e"].variantId}</td>
                    <td ${allele.hvrVariantMap["f"].matchesReference ? "" : "class='mismatch'"}>f${allele.hvrVariantMap["f"].variantId}</td>
                    <td>&nbsp;</td>
                    <c:forEach var="codonNumber" begin="1" end="100"><td class="codon ${codonNumber % 10 == 0 ? "index" : ""} ${allele.codonMap[codonNumber] != null && allele.codonMap[codonNumber].hypervariableRegionName != null ? "hypervariableRegion" : ""}  ${allele.codonMap[codonNumber] != null && !allele.codonMap[codonNumber].matchesReference ? "mismatch" : ""}">${allele.codonMap[codonNumber] == null ? "&nbsp;" : allele.codonMap[codonNumber].aminoAcid}</td></c:forEach>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</body>

</html>
