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
a.filterEnabled {
    background-color: lightgreen;
}

</style>

<title>HLA-DPB1 Allele Difference Report</title>
        
</head>

<body>

    <div id="working" style="position: fixed; top: 0px; width: 150px; left: 50%; margin-left: -75px; text-align: center; background-color: red; color: white;">working...</div>

    <h1>HLA-DPB1 Allele Difference Report</h1>
    
    <p>
        The reference allele for hypervariable region matching is highlighted in
        yellow. This report only shows the lowest allele number for synonymous
        alleles. Click on a row to set that allele as the reference allele. By
        default, this report only shows alleles where there are at least 4
        hypervariable region matches to the reference allele, but you may toggle
        this filter on and off.
    </p>   
    <p>
        The IMGT allele database version is <span id="imgtDbVersion"></span> and
        the single antigen bead (SAB) reagent lot number is
        <span id="reagentLotNumber"></span>. This report is for research use
        only.
    </p>
    
    <table id="reportTable" cellspacing="0">
        <thead>
            <tr>
                <th class="alleleName">[<a id="alleleNameSort" href="javascript:void(0);">sort</a>]</th>
                <th>[<a id="sabFilter" href="javascript:void(0);">filter=Y</a>]</th>
                <th>[<a id="hvrMatchCountGe4Filter" href="javascript:void(0);">filter&ge;4)</a>]<br/>[<a id="hvrMatchCountSort" href="javascript:void(0);">sort</a>]</th>
                <th colspan="6" style="border-bottom: 1px solid black;">hypervariable<br/>region ID</th>
                <th>&nbsp;</th>
                <th colspan="100" style="border-bottom: 1px solid black;">protein sequence (aligned to HLA-DPB1*01:01 codons 1-100)</th>
            </tr>
            <tr id="columnNames">
                <th data-name="allele.alleleName" class="alleleName">allele name</th>
                <th data-name="allele.singleAntigenBead ? 'Y' : '&nbsp;'">SAB</th>
                <th data-name="allele.hvrMatchCount">matches</th>
                <th data-name="'a' + allele.hvrVariantMap['a'].variantId" data-container="allele.hvrVariantMap['a']" class="hvrId">a</th>
                <th data-name="'b' + allele.hvrVariantMap['b'].variantId" data-container="allele.hvrVariantMap['b']" class="hvrId">b</th>
                <th data-name="'c' + allele.hvrVariantMap['c'].variantId" data-container="allele.hvrVariantMap['c']" class="hvrId">c</th>
                <th data-name="'d' + allele.hvrVariantMap['d'].variantId" data-container="allele.hvrVariantMap['d']" class="hvrId">d</th>
                <th data-name="'e' + allele.hvrVariantMap['e'].variantId" data-container="allele.hvrVariantMap['e']" class="hvrId">e</th>
                <th data-name="'f' + allele.hvrVariantMap['f'].variantId" data-container="allele.hvrVariantMap['f']" class="hvrId">f</th>
                <th>&nbsp;</th>
                <c:forEach var="codonNumber" begin="1" end="100"><th data-name="allele.codonMap['${codonNumber}'] == undefined ? '&nbsp;' : allele.codonMap['${codonNumber}'].aminoAcid" data-container="allele.codonMap['${codonNumber}'] == undefined ? '' : allele.codonMap['${codonNumber}']" class="codon ${codonNumber % 10 == 0 ? "index" : ""}">${codonNumber % 10 == 0 ? codonNumber : "&nbsp;"}</th></c:forEach>
            </tr>
            <tr id="hypervariableRegionNames">
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
                <c:forEach var="codonNumber" begin="1" end="100"><th data-name="allele.codonMap['${codonNumber}'] == undefined ? '&nbsp;' : allele.codonMap['${codonNumber}'].hypervariableRegionName" class="codon ${codonNumber % 10 == 0 ? "index" : ""}">&nbsp;</th></c:forEach>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>
        
</body>

</html>

<script>

var reagentLotNumber; // the single antigen bead reagent lot number
var alleles = []; // the array of alleles
var sabOnly = false; // only show single antigen bead alleles
var hvrMatchCountGe4Only = true; // only show alleles where the match count is ge 4

// Get reagent lot number.
function getReagentLotNumber() {
    $("#working").show();
    return $.ajax({
        url: "/hladpb1-webServices/resources/hypervariableRegions/reagentLotNumber",
        dataType: "json"
    }).then(function(response) {
        reagentLotNumber = response;
    });
}

// Get all alleles.
function getAlleles() {
    $("#working").show();
    return $.ajax({
        url: "/hladpb1-webServices/resources/alleles?synonymous=false&sab=" + sabOnly + "&hvrMatchCount=" + (hvrMatchCountGe4Only ? 4 : 0), // always filter out synonymous alleles
        dataType: "json"
    }).then(function(response) {
        alleles = response;
    });
}

// Put an allele. With this report, this is only used to set the current
// reference allele.
function putAllele(allele) {
    $("#working").show();
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
    $("#working").show();
    $("#reportTable tbody tr").remove();
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
    var dfr = $.Deferred();
    $("#working").show();
    $("#imgtDbVersion").html(alleles[0].version);
    $("#reagentLotNumber").html(reagentLotNumber);
    $("#sabFilter").removeClass("filterEnabled");
    if(sabOnly) { $("#sabFilter").addClass("filterEnabled"); }
    $("#hvrMatchCountGe4Filter").removeClass("filterEnabled");
    if(hvrMatchCountGe4Only) { $("#hvrMatchCountGe4Filter").addClass("filterEnabled"); }
    $("#hypervariableRegionNames").children("th").each(function() {
        // This assumes that the first allele has all hypervariable regions
        // represented.
        var allele = alleles[0];
        $(this).html(eval($(this).data("name")));
    });
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
            if($(this).hasClass("codon") && $(this).html() != "&nbsp;") {
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
    dfr.resolve();
    return dfr.promise();
}

// Sort function.
function alleleNameSort(a, b) {
    var A = $(a).data("sequence");
    var B = $(b).data("sequence");
    if(A < B)      { return -1; }
    else if(A > B) { return  1; }
    else           { return  0; }
};

// Sort function.
function hvrMatchCountSort(a, b) {
    var A = $(a).children("td").eq(2).html();
    var B = $(b).children("td").eq(2).html();
    if(A < B)      { return  1; }
    else if(A > B) { return -1; }
    else           { return alleleNameSort(a, b); }
}

// Document ready! Let's go...
$(document).ready(function() {

    // Initial population of report table.
    getReagentLotNumber().then(getAlleles).then(populateTableRows).then(setUiState).done(function() { $("#working").hide(); });

    // Click on a row to set it as the reference allele. Handled by web service.
    $("#reportTable tbody").on("click", "tr", function() {
        var alleleName = $(this).data("value");
        var allele = alleles.find(function(allele) { return alleleName == allele.alleleName; });
        allele.referenceAllele = true;
        putAllele(allele).then(getAlleles).then(setUiState).done(function() { $("#working").hide(); } );
    });

    // Single antigen bead filter toggle on/off. Handled by web service.
    $("#sabFilter").click(function() {
        sabOnly = !sabOnly;
        getAlleles().then(populateTableRows).then(setUiState).done(function() { $("#working").hide(); });
    });

    // Match count filter toggle on/off. Handled by web service.
    $("#hvrMatchCountGe4Filter").click(function() {
        hvrMatchCountGe4Only = !hvrMatchCountGe4Only;
        getAlleles().then(populateTableRows).then(setUiState).done(function() { $("#working").hide(); });
    });

    // Click on sort. This is handled locally.
    $("#alleleNameSort").click(function() {
        $("#working").show();
        setTimeout(function() {
            var rows = $('#reportTable tbody tr').get();
            rows.sort(alleleNameSort);
            $.each(rows, function(index, row) {
              $('#reportTable').children('tbody').append(row);
            });
            $("#working").hide();
        }, 1);
    });
    
    // Click on sort. This is handled locally.
    $("#hvrMatchCountSort").click(function() {
        $("#working").show();
        setTimeout(function() {
            var rows = $('#reportTable tbody tr').get();
            rows.sort(hvrMatchCountSort);
            $.each(rows, function(index, row) {
              $('#reportTable').children('tbody').append(row);
            });
            $("#working").hide();
        }, 1);
    });
    
});

</script>
