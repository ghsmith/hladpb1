<!DOCTYPE html>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<html>

<head>

<meta name="viewport" content="width=device-width, initial-scale=0.86, maximum-scale=3.0, minimum-scale=0.86">
<link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css"/>
<script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js" integrity="sha256-VazP97ZCwtekAsvgPBSUwPFKdrwD3unUfSGVYrahUqU=" crossorigin="anonymous"></script>

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
th, td {
    text-align: center;
    white-space: nowrap;
    padding: 3px;
}
th.alleleName, td.alleleName {
    text-align: left;
}
th.hvrId, td.hvrId {
    text-align: center;
}
th.variantId, th.variantSeq {
    text-align: center;
    border: 1px solid black;
}
th.index, td.index {
    border-left: 3px solid black;
}
a.filterEnabled {
    background-color: yellow;
}
table.statusDescriptionTable td {
    text-align: left;
}
#reportTable tr:hover {
    background-color: yellow;
}

</style>

<title>HLA-DPB1 Compatibility Report</title>
        
</head>

<body>

    <div id="working" style="position: fixed; top: 0px; width: 150px; left: 50%; margin-left: -75px; text-align: center; background-color: red; color: white; font-weight: bold;">working...</div>

    <h1>HLA-DPB1 Compatability Report (<a href="https://github.com/ghsmith/hladpb1/blob/master/hladpb1-webServices/src/main/webapp/ajaxCompatibilityReport.html">AJAX version</a>)</h1>
    
    <p>
        Select recipient antibodies and donor & recipient alleles below. An
        antibody for a hypervariable region epitope is inferred when (a) all
        single antigen beads with that epitope are positive and (b) the epitope
        is not present on any of the recipient's alleles. Each donor allele will
        be evaluated for compatibility with the recipient and assigned a
        compatibility status:
    </p>
    <p>
        <table class="statusDescriptionTable">
            <tr><td><i>likely&nbsp;compatible&nbsp;(LC)</i></td><td>-</td><td>the single antigen bead corresponding to the donor allele is NOT positive and antibody specificity for an epitope of the donor allele is NOT inferred</td></tr>
            <tr><td><i>incompatible&nbsp;(I)</i></td><td>-</td><td>the single antigen bead corresponding to the donor allele is positive</td></tr>
            <tr><td><i>likely&nbsp;incompatible&nbsp;(LI)</i></td><td>-</td><td>antibody specificity for an epitope of the donor allele is inferred</td></tr>
            <tr><td><i>auto-antibody&nbsp;(AA)</i></td><td>-</td><td>the single antigen bead corresponding to the donor allele is positive, but is also a recipient allele</td></tr>
        </table>
    </p>

    <p>
        The IMGT allele database version is <span id="imgtDbVersion">...</span>
        and the single antigen bead (SAB) reagent lot number is
        <span id="reagentLotNumber">...</span>.
    </p>
    <p>
        This report is for research use only.
    </p>
    
    <table id="reportTable">
        <thead>
            <tr>
                <th class="alleleName">[<a id="alleleNameSort" href="javascript:void(0);">sort</a>]</th>
                <th>[<a id="sabFilter" href="javascript:void(0);">filter=Y</a>]</th>
                <th colspan="42" style="border-bottom: 1px solid black;">hypervariable region name</th>
                <th>[sort]</th>
            </tr>
            <tr id="columnNames">
                <th data-name="allele.alleleName" class="alleleName">allele name</th>
                <th data-name="allele.singleAntigenBead ? 'Y' : '&nbsp;'">SAB</th>
                <th colspan="7" data-hvr-id="a" class="hvrId index">a</th>
                <th colspan="7" data-hvr-id="b" class="hvrId index">b</th>
                <th colspan="7" data-hvr-id="c" class="hvrId index">c</th>
                <th colspan="7" data-hvr-id="d" class="hvrId index">d</th>
                <th colspan="7" data-hvr-id="e" class="hvrId index">e</th>
                <th colspan="7" data-hvr-id="f" class="hvrId index">f</th>
                <th data-name="allele.compatInterpretation" class="index">status</th>
            </tr>
            <!-- Set up for a maximum of 6 hypervariable region variants per
                 hypervariable region. This is reasonable to start with. -->
            <tr id="hvrVariantIds" style="border: 1px solid black;">
                <th colspan="2" style="text-align: right;">hypervariable region variant ID &Longrightarrow;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="allele.hvrVariantMap['a'] != undefined && ${variantId} == allele.hvrVariantMap['a'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="a" class="variantId ${variantId == 1 ? "index" : ""}">${variantId}</th></c:forEach>
                    <th data-name="allele.hvrVariantMap['a'] != undefined && allele.hvrVariantMap['a'].variantId.match(/[A-Z]+/) ? allele.hvrVariantMap['a'].variantId : '&nbsp;'" data-hvr-id="a" class="variantId ${variantId == 1 ? "index" : ""}">X</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="allele.hvrVariantMap['b'] != undefined && ${variantId} == allele.hvrVariantMap['b'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="b" class="variantId ${variantId == 1 ? "index" : ""}">${variantId}</th></c:forEach>
                    <th data-name="allele.hvrVariantMap['b'] != undefined && allele.hvrVariantMap['b'].variantId.match(/[A-Z]+/) ? allele.hvrVariantMap['b'].variantId : '&nbsp;'" data-hvr-id="b" class="variantId ${variantId == 1 ? "index" : ""}">X</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="allele.hvrVariantMap['c'] != undefined && ${variantId} == allele.hvrVariantMap['c'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="c" class="variantId ${variantId == 1 ? "index" : ""}">${variantId}</th></c:forEach>
                    <th data-name="allele.hvrVariantMap['c'] != undefined && allele.hvrVariantMap['c'].variantId.match(/[A-Z]+/) ? allele.hvrVariantMap['c'].variantId : '&nbsp;'" data-hvr-id="c" class="variantId ${variantId == 1 ? "index" : ""}">X</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="allele.hvrVariantMap['d'] != undefined && ${variantId} == allele.hvrVariantMap['d'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="d" class="variantId ${variantId == 1 ? "index" : ""}">${variantId}</th></c:forEach>
                    <th data-name="allele.hvrVariantMap['d'] != undefined && allele.hvrVariantMap['d'].variantId.match(/[A-Z]+/) ? allele.hvrVariantMap['d'].variantId : '&nbsp;'" data-hvr-id="d" class="variantId ${variantId == 1 ? "index" : ""}">X</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="allele.hvrVariantMap['e'] != undefined && ${variantId} == allele.hvrVariantMap['e'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="e" class="variantId ${variantId == 1 ? "index" : ""}">${variantId}</th></c:forEach>
                    <th data-name="allele.hvrVariantMap['e'] != undefined && allele.hvrVariantMap['e'].variantId.match(/[A-Z]+/) ? allele.hvrVariantMap['e'].variantId : '&nbsp;'" data-hvr-id="e" class="variantId ${variantId == 1 ? "index" : ""}">X</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="allele.hvrVariantMap['f'] != undefined && ${variantId} == allele.hvrVariantMap['f'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="f" class="variantId ${variantId == 1 ? "index" : ""}">${variantId}</th></c:forEach>
                    <th data-name="allele.hvrVariantMap['f'] != undefined && allele.hvrVariantMap['f'].variantId.match(/[A-Z]+/) ? allele.hvrVariantMap['f'].variantId : '&nbsp;'" data-hvr-id="f" class="variantId ${variantId == 1 ? "index" : ""}">X</th>
                <th class="index">&nbsp;</th>
            </tr>
            <tr id="hvrVariantSeqs" style="border: 1px solid black;">
                <th colspan="2" style="text-align: right;">protein sequence(s) &Longrightarrow;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['a'].variantMap['${variantId}'] == undefined ? '&nbsp;' : hvrMap['a'].variantMap['${variantId}'].proteinSequenceList.join('<br/>')" data-hvr-id="a" class="variantSeq ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['b'].variantMap['${variantId}'] == undefined ? '&nbsp;' : hvrMap['b'].variantMap['${variantId}'].proteinSequenceList.join('<br/>')" data-hvr-id="b" class="variantSeq ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['c'].variantMap['${variantId}'] == undefined ? '&nbsp;' : hvrMap['c'].variantMap['${variantId}'].proteinSequenceList.join('<br/>')" data-hvr-id="c" class="variantSeq ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['d'].variantMap['${variantId}'] == undefined ? '&nbsp;' : hvrMap['d'].variantMap['${variantId}'].proteinSequenceList.join('<br/>')" data-hvr-id="d" class="variantSeq ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['e'].variantMap['${variantId}'] == undefined ? '&nbsp;' : hvrMap['e'].variantMap['${variantId}'].proteinSequenceList.join('<br/>')" data-hvr-id="e" class="variantSeq ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['f'].variantMap['${variantId}'] == undefined ? '&nbsp;' : hvrMap['f'].variantMap['${variantId}'].proteinSequenceList.join('<br/>')" data-hvr-id="f" class="variantSeq ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <th class="index">&nbsp;</th>
            </tr>
            <tr id="" style="border: 1px solid black;">
                <th colspan="2" style="text-align: right;">is a recipient epitope? &Longrightarrow;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['a'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="a" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['b'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="b" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['c'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="c" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['d'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="d" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['e'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="e" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['f'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="f" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <th class="index">&nbsp;</th>
            </tr>
            <tr id="" style="border: 1px solid black;">
                <th colspan="2" style="text-align: right;">percentage of beads positive &Longrightarrow;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['a'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="a" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['b'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="b" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['c'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="c" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['d'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="d" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['e'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="e" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['f'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="f" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <th class="index">&nbsp;</th>
            </tr>
            <tr id="" style="border: 1px solid black;">
                <th colspan="2" style="text-align: right;">is an antibody likely? &Longrightarrow;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['a'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="a" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['b'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="b" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['c'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="c" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['d'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="d" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['e'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="e" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="${variantId} == allele.hvrVariantMap['f'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="f" class="variantId ${variantId == 1 ? "index" : ""}"></th></c:forEach><th>&nbsp;</th>
                <th class="index">&nbsp;</th>
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
var hypervariableRegions = []; // the array of hypervariableRegions
var hvrMap = new Object(); // a map of hypervariable regions, indexed by hypervariable region ID
var sabOnly = false; // only show single antigen bead alleles

// Get reagent lot number.
function getReagentLotNumber() {
    $("#working").show();
    return $.ajax({
        url: "/hladpb1/resources/hypervariableRegions/reagentLotNumber",
        dataType: "json"
    }).then(function(response) {
        reagentLotNumber = response;
    });
}

// Get all hypervariable regions.
function getHypervariableRegions() {
    $("#working").show();
    return $.ajax({
        url: "/hladpb1/resources/hypervariableRegions",
        dataType: "json"
    }).then(function(response) {
        hypervariableRegions = response;
        hypervariableRegions.forEach(function(hypervariableRegion) {
            hvrMap[hypervariableRegion.hypervariableRegionName] = hypervariableRegion;
        });
    });
}

// Get all alleles.
function getAlleles() {
    $("#working").show();
    return $.ajax({
        url: "/hladpb1/resources/alleles?synonymous=false", // always filter out synonymous alleles
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
        url: "/hladpb1/resources/alleles/" + allele.alleleName,
        dataType: "json",
        type: "PUT",
        contentType: "application/json",
        data: JSON.stringify(allele)
    });
}

// Populate rows in the report table from the alleles array. Note that this is
// only done once. All sorting and filtering is done locally. Changing the
// reference allele does involve a getAlleles() call, but the HTML table rows
// are updated in-situ.
function populateTableRows() {
    $("#working").show();
    $("#reportTable tbody tr").remove();
    var rowHtml = [];
    alleles.forEach(function(allele) {
        rowHtml.push("<tr data-value='" + allele.alleleName + "' data-sequence='" + allele.sequenceNumber + "' " + (!isRowVisible(allele) ? "style='display: none;'" : "") + ">");
        $("#columnNames th").each(function() {
            var name = $(this).data("name");
            if($(this).hasClass("hvrId")) {
                $("#hvrVariantIds th.variantId[data-hvr-id='" + $(this).data("hvr-id") + "']").each(function() {
                    var val = eval($(this).data("name"));
                    var tagAttributes = $(this)[0].outerHTML;
                    tagAttributes = tagAttributes.substring(4, tagAttributes.indexOf(">"));
                    rowHtml.push("<td " + tagAttributes + ">" + (val == undefined ? "&nbsp;" : val) + "</td>");
                });
            }
            else {
                var val = eval(name);
                var tagAttributes = $(this)[0].outerHTML;
                tagAttributes = tagAttributes.substring(4, tagAttributes.indexOf(">"));
                rowHtml.push("<td " + tagAttributes + ">" + (val == undefined ? "&nbsp;" : val) + "</td>");
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
    setTimeout(function() {
        $("#imgtDbVersion").html(alleles[0].version);
        $("#reagentLotNumber").html(reagentLotNumber);
        $("#sabFilter").removeClass("filterEnabled");
        if(sabOnly) { $("#sabFilter").addClass("filterEnabled"); }
        $("#hvrVariantSeqs th.variantSeq").each(function() {
            $(this).html(eval($(this).data("name")));
        });
        $("#reportTable tbody tr").each(function() {
            var alleleName = $(this).data("value");
            var allele = alleles.find(function(allele) { return alleleName == allele.alleleName; });
            if(isRowVisible(allele)) { $(this).show(); }
            else                     { $(this).hide(); }
            $(this).children("td").each(function() {
                //if($(this).data("name") == "allele.matchesHvrCount") {
                //    $(this).html(allele.matchesHvrCount);
                //}
            });
        });
        dfr.resolve();
    }, 1);
    return dfr.promise();
}

// Row filtering function.
function isRowVisible(allele) {
    rowVisible = true;
    if(sabOnly && !allele.singleAntigenBead) { rowVisible = false; }
    return rowVisible;
}

// Sort function.
function alleleNameSort(a, b) {
    var A = $(a).data("sequence");
    var B = $(b).data("sequence");
    if(A < B)      { return -1; }
    else if(A > B) { return  1; }
    else           { return  0; }
};

// Document ready! Let's go...
$(document).ready(function() {

    // Initial population of report table.
    getReagentLotNumber().then(getHypervariableRegions).then(getAlleles).then(populateTableRows).then(setUiState).done(function() { $("#working").hide(); });

    // Click on a row to set it as the reference allele. Handled by web service.
    //$("#reportTable tbody").on("click", "tr", function() {
    //    var alleleName = $(this).data("value");
    //    var allele = alleles.find(function(allele) { return alleleName == allele.alleleName; });
    //    allele.referenceForMatches = true;
    //    putAllele(allele).then(getAlleles).then(setUiState).done(function() { $("#working").hide(); } );
    //});

    // Single antigen bead filter toggle on/off. This is handled locally.
    $("#sabFilter").click(function() {
        sabOnly = !sabOnly;
        setUiState().done(function() { $("#working").hide(); });
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
    
});

</script>
