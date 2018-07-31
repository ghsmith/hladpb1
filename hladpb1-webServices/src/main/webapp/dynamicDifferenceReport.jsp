<!DOCTYPE html>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<html>

<head>

<link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css" />
<script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js" integrity="sha256-VazP97ZCwtekAsvgPBSUwPFKdrwD3unUfSGVYrahUqU=" crossorigin="anonymous"></script>

<style>
           
body {
    font-family: monospace;
}
th.index, td.index {
    border-left: 3px solid black;
}
th, td {
    text-align: center;
}
th.alleleName, td.alleleName {
    text-align: left;
}
th.codon, td.codon {
    text-align: right;
}
td {
    white-space: nowrap;
}
td.hypervariable {
    border: 1px solid black;
}
td.diff {
    color: yellow;
    background-color: gray;
}
td.diff.hypervariable {
    color: yellow;
    background-color: black;
}
            
</style>
        
</head>

<body>

    <h1>HLA-DPB1 Allele Difference Report (dynamic HTML version)</h1>

    <table id="reportTable" cellspacing="0">
        <thead>
            <tr>
                <th class="alleleName">[<a id="alleleNameSort" href="javascript:void(0);">sort</a>]</th>
                <th>[<a id="sabFilter" href="javascript:void(0);">filter</a>]</th>
                <th>[<a id="hvrMatchCountSort" href="javascript:void(0);">sort</a>]</th>
                <th colspan="6" style="border-bottom: 1px solid black;">hypervariable<br/>region ID</th>
                <th>&nbsp;</th>
                <th colspan="100" style="border-bottom: 1px solid black;">protein sequence (aligned to HLA-DPB1*01:01 codons 1-100)</th>
            </tr>
            <tr id="columnNames">
                <th data-name="allele.alleleName" class="alleleName">allele name</th>
                <th data-name="allele.singleAntigenBead ? 'Y' : 'N'">SAB</th>
                <th data-name="allele.hvrMatchCount">matches</th>
                <th data-name="'a' + allele.hvrVariantMap['a'].variantId">a</th>
                <th data-name="'b' + allele.hvrVariantMap['b'].variantId">b</th>
                <th data-name="'c' + allele.hvrVariantMap['c'].variantId">c</th>
                <th data-name="'d' + allele.hvrVariantMap['d'].variantId">d</th>
                <th data-name="'e' + allele.hvrVariantMap['e'].variantId">e</th>
                <th data-name="'f' + allele.hvrVariantMap['f'].variantId">f</th>
                <th>&nbsp;</th>
                <c:forEach var="codonNumber" begin="1" end="100">
                    <th data-name="allele.codonMap['${codonNumber}'] == undefined ? '' : allele.codonMap['${codonNumber}'].aminoAcid" class="codon ${codonNumber % 10 == 0 ? "index" : ""}">${codonNumber % 10 == 0 ? codonNumber : "&nbsp;"}</th>                
                </c:forEach>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>
        
</body>

</html>

<script>

var alleles // The array of alleles.

// Put an allele. With this report, this is only used to set the current
// reference allele.
function putAllele(allele) {
    $.ajax({
        url: "/hladpb1-webServices/resources/alleles/" + allele.alleleName,
        dataType: "json",
        type: "PUT",
        contentType: "application/json",
        data: JSON.stringify(allele)
    }).done(function(response) {
        getAlleles();
    });
}

// Get all alleles and refresh the hypervariable region matches.
function getAlleles() {
    $.ajax({
        url: "/hladpb1-webServices/resources/alleles?synonymous=null", // filter out synonymous alleles
        dataType: "json"
    }).done(function(response) {
        alleles = response;
        $("#reportTable tbody tr").each(function() {
            var alleleName = $(this).data("value");
            $(this).children("td").eq(2).html(alleles.find(function(allele) { return alleleName == allele.alleleName; }).hvrMatchCount);
        });
    });
}

// Document ready! Let's go...
$(document).ready(function() {

    // Initially populate the report table.
    $.ajax({
        url: "/hladpb1-webServices/resources/alleles?synonymous=null", // filter out synonymous alleles
        dataType: "json"
    }).done(function(response) {
        alleles = response;
        var rowHtml = [];
        alleles.forEach(function(allele) {
            rowHtml.push("<tr data-value='" + allele.alleleName + "' data-sequence='" + allele.sequenceNumber + "'>");
            $("#columnNames th").each(function(index) {
                var val = eval($(this).data("name"));
                var classs = $(this).attr("class");
                rowHtml.push("<td data-name=\"" + $(this).data("name") + "\" " + (classs == undefined ? "" : "class='" + classs + "'") + ">" + (val == undefined ? "" : val) + "</td>");
            });
            rowHtml.push("</tr>");
        });
        $("#reportTable tbody").append(rowHtml.join(""));
    });

    // Click on a row to set it as the reference allele.
    $("#reportTable tbody").on("click", "tr", function() {
        var alleleName = $(this).data("value");
        var allele = alleles.find(function(allele) { return alleleName == allele.alleleName; });
        allele.referenceAllele = true;
        putAllele(allele);
    });

    // Click on sort.
    $("#alleleNameSort").click(function() {
        var rows = $('#reportTable tbody tr').get();
        rows.sort(function(a, b) {
            var A = $(a).data("sequence");
            var B = $(b).data("sequence");
            if(A < B)      { return -1; }
            else if(A > B) { return  1; }
            else           { return  0; }
        });
        $.each(rows, function(index, row) {
          $('#reportTable').children('tbody').append(row);
        });
    });
    
    // Click on sort.
    $("#hvrMatchCountSort").click(function() {
        var rows = $('#reportTable tbody tr').get();
        rows.sort(function(a, b) {
            var A = $(a).children("td").eq(2).html();
            var B = $(b).children("td").eq(2).html();
            if(A < B)      { return  1; }
            else if(A > B) { return -1; }
            else           { return  0; }
        });
        $.each(rows, function(index, row) {
          $('#reportTable').children('tbody').append(row);
        });
    });
    
});

</script>
