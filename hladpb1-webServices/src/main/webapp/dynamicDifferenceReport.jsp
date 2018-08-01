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
                <th data-name="'a' + allele.hvrVariantMap['a'].variantId" data-container="allele.hvrVariantMap['a']" class="hvrId">a</th>
                <th data-name="'b' + allele.hvrVariantMap['b'].variantId" data-container="allele.hvrVariantMap['b']" class="hvrId">b</th>
                <th data-name="'c' + allele.hvrVariantMap['c'].variantId" data-container="allele.hvrVariantMap['c']" class="hvrId">c</th>
                <th data-name="'d' + allele.hvrVariantMap['d'].variantId" data-container="allele.hvrVariantMap['d']" class="hvrId">d</th>
                <th data-name="'e' + allele.hvrVariantMap['e'].variantId" data-container="allele.hvrVariantMap['e']" class="hvrId">e</th>
                <th data-name="'f' + allele.hvrVariantMap['f'].variantId" data-container="allele.hvrVariantMap['f']" class="hvrId">f</th>
                <th>&nbsp;</th>
                <c:forEach var="codonNumber" begin="1" end="100">
                    <th data-name="allele.codonMap['${codonNumber}'] == undefined ? '' : allele.codonMap['${codonNumber}'].aminoAcid" data-container="allele.codonMap['${codonNumber}'] == undefined ? '' : allele.codonMap['${codonNumber}']" class="codon ${codonNumber % 10 == 0 ? "index" : ""}">${codonNumber % 10 == 0 ? codonNumber : "&nbsp;"}</th>
                </c:forEach>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>
        
</body>

</html>

<script>

var alleles = []; // The array of alleles.

// Get all alleles.
function getAlleles() {
    return $.ajax({
        url: "/hladpb1-webServices/resources/alleles?synonymous=null", // filter out synonymous alleles
        dataType: "json"
    }).then(function(response) {
        alleles = response;
    });
}

// Put an allele. With this report, this is only used to set the current
// reference allele.
function putAllele(allele) {
    return $.ajax({
        url: "/hladpb1-webServices/resources/alleles/" + allele.alleleName,
        dataType: "json",
        type: "PUT",
        contentType: "application/json",
        data: JSON.stringify(allele)
    });
}

// Populate rows in the report table from the alleles array.
function populateTableRows() {
    var rowHtml = [];
    alleles.forEach(function(allele) {
        rowHtml.push("<tr data-value='" + allele.alleleName + "' data-sequence='" + allele.sequenceNumber + "'>");
        $("#columnNames th").each(function(index) {
            var name = $(this).data("name");
            if(name == undefined) {
                rowHtml.push("<td></td>");
            }
            else {
                var val = eval(name);
                var tagAttributes = $(this)[0].outerHTML;
                tagAttributes = tagAttributes.substring(4, tagAttributes.indexOf(">"));
                rowHtml.push("<td " + tagAttributes + ">" + (val == undefined ? "" : val) + "</td>");
            }
        });
        rowHtml.push("</tr>");
    });
    $("#reportTable tbody").append(rowHtml.join(""));
}

// Set the UI state based on the hypervariable region matches.
function setUiState() {
    $("#reportTable tbody tr").each(function() {
        var alleleName = $(this).data("value");
        var allele = alleles.find(function(allele) { return alleleName == allele.alleleName; });
        $(this).removeClass("referenceAllele");
        if(allele.referenceAllele) {
            $(this).addClass("referenceAllele");
        }
        $(this).children("td").each(function() {
            if($(this).data("name") == "allele.hvrMatchCount") {
                $(this).html(allele.hvrMatchCount);
            }
            if($(this).hasClass("hvrId")) {
                $(this).removeClass("mismatch");
                if(!eval($(this).data("container") + ".matchesReference")) {
                    $(this).addClass("mismatch");
                }
            }
            if($(this).hasClass("codon") && $(this).html() != "") {
                $(this).removeClass("mismatch");
                if(!eval($(this).data("container") + ".matchesReference")) {
                    $(this).addClass("mismatch");
                }
                if(eval($(this).data("container") + ".hypervariableRegionName") != null) {
                    $(this).addClass("hypervariableRegion");
                }
            }
        });
    });
}

// Document ready! Let's go...
$(document).ready(function() {

    getAlleles().then(populateTableRows).then(setUiState);

    // Click on a row to set it as the reference allele.
    $("#reportTable tbody").on("click", "tr", function() {
        var alleleName = $(this).data("value");
        var allele = alleles.find(function(allele) { return alleleName == allele.alleleName; });
        allele.referenceAllele = true;
        putAllele(allele).then(getAlleles).then(setUiState);
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
