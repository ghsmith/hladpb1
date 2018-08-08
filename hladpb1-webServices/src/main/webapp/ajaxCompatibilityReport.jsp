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
th.hvrId {
    text-align: center;
    border-left: 1px solid black;
}
th.variantId, th.variantSeq, th.recipientEpitope,
th.pctBeadsPositive, th.antibodyPresent, th.knownReactiveEpitope {
    text-align: center;
    border: 1px solid black;
}
td.variantId {
    text-align: center;
    border-left: 1px solid black;
}
th.index, td.index {
    border-left: 3px solid black;
}
th.indexRight, td.indexRight {
    border-right: 3px solid black;
}
a.filterEnabled {
    background-color: yellow;
}
#reportTable tbody tr {
    border-bottom: 1px solid transparent;
}
#reportTable tbody tr:hover {
    background-color: yellow;
    border-bottom: 1px solid black;
}   
#reportTable td.mismatch, #reportTable th.mismatch {
    background-color: yellow;
}
table.statusDescriptionTable td {
    text-align: left;
    border: 1px solid black;
}

</style>

<title>HLA-DPB1 Compatibility Report</title>
        
</head>

<body>

    <div id="working" style="position: fixed; top: 0px; width: 150px; left: 50%; margin-left: -75px; text-align: center; background-color: red; color: white; font-weight: bold;">working...</div>

    <h1>HLA-DPB1 Compatibility Report (<a href="https://github.com/ghsmith/hladpb1/blob/master/hladpb1-webServices/src/main/webapp/ajaxCompatibilityReport.html">source code</a>)</h1>
    
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
            <tr><td>LC</td><td>likely&nbsp;compatible</td><td>the single antigen bead (SAB) corresponding to the donor allele is NOT positive and antibody specificity for an epitope of the donor allele is NOT inferred</td></tr>
            <tr><td>LI</td><td>likely&nbsp;incompatible</i></td><td>antibody specificity for an epitope of the donor allele is inferred</td></tr>
            <tr><td>I</td><td>incompatible</i></td><td>the single antigen bead corresponding to the donor allele is positive</td></tr>
            <tr><td>AA</td><td>auto-antibody</td><td>the single antigen bead is positive, but is also a recipient allele</td></tr>
        </table>
    </p>
    <p>
        Selection of antibodies to alleles that are not the subject of a single
        antigen bead is currently not allowed. Hypervariable region variants
        that are not associated with a variant ID (i.e., appear in the <i>n/a</i>
        column below), are currently not considered when assigning a
        compatibility status.
    </p>
    <p>
        The IMGT allele database version is <span id="imgtDbVersion">...</span>
        and the single antigen bead (SAB) reagent lot number is
        <span id="reagentLotNumber">...</span>. The session ID is
        <span id="sessionId">...</span>
        (<a id="resetSessionLink" href="javascript:void(0);">reset session</a>).
    </p>
    <p>
        This report is for research use only.
    </p>
    
    <p>
        <table>
            <tr><td style="text-align: right;"><span id="allelesLoaded">...</span></td><td style="text-align: left;">&nbsp;alleles are loaded into memory</td></tr>
            <tr><td style="text-align: right;"><span id="allelesShown">...</span></td><td style="text-align: left;">&nbsp;alleles match filter criteria</td></tr>
            <tr><td style="text-align: right;"><span id="allelesFiltered">...</span></td><td style="text-align: left;">&nbsp;alleles do not match filter criteria</td></tr>
        </table>
    </p>

    <table id="reportTable">
        <thead>
            <tr>
                <th class="alleleName">[<a id="alleleNameSort" href="javascript:void(0);">sort</a>]</th>
                <th>[<a id="sabFilter" href="javascript:void(0);">filter=Y</a>]</th>
                <th colspan="3" style="border-bottom: 1px solid black;">[<a id="selectedFilter" href="javascript:void(0);">filter=Y</a>]</th>
                <th>[<a id="statusSort" href="javascript:void(0);">sort</a>]</th>
                <th colspan="42" style="border-bottom: 1px solid black;">hypervariable region name</th>
            </tr>
            <tr id="columnNames">
                <th data-name="allele.alleleName" class="alleleName">allele name</th>
                <th data-name="allele.singleAntigenBead ? 'Y' : '&nbsp;'">SAB</th>
                <th data-name="allele.donorTypeForCompat" data-label="D">donor<br/>type<br/>(D)</th>
                <th data-name="allele.recipientTypeForCompat" data-label="R">recip<br/>type<br/>(R)</th>
                <th data-name="allele.recipientAntibodyForCompat" data-label="Ab">recip<br/>antib<br/>(Ab)</th>
                <th data-name="allele.compatInterpretation">status</th>
                <th colspan="7" data-hvr-id="a" class="hvrId index">a</th>
                <th colspan="7" data-hvr-id="b" class="hvrId index">b</th>
                <th colspan="7" data-hvr-id="c" class="hvrId index">c</th>
                <th colspan="7" data-hvr-id="d" class="hvrId index">d</th>
                <th colspan="7" data-hvr-id="e" class="hvrId index">e</th>
                <th colspan="7" data-hvr-id="f" class="hvrId index indexRight">f</th>
            </tr>
            <!-- Set up for a maximum of 6 hypervariable region variants per
                 hypervariable region. This is reasonable to start with. -->
            <tr id="hvrVariantIds" style="border: 1px solid black;">
                <th colspan="6" style="text-align: right;">hypervariable region variant ID &Longrightarrow;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="allele.hvrVariantMap['a'] != undefined && ${variantId} == allele.hvrVariantMap['a'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="a" class="variantId ${variantId == 1 ? "index" : ""}">${variantId}</th></c:forEach>
                    <th data-name="allele.hvrVariantMap['a'] != undefined && allele.hvrVariantMap['a'].variantId.match(/[A-Z]+/) ? allele.hvrVariantMap['a'].variantId : '&nbsp;'" data-hvr-id="a" class="variantId indexRight">n/a</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="allele.hvrVariantMap['b'] != undefined && ${variantId} == allele.hvrVariantMap['b'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="b" class="variantId ${variantId == 1 ? "index" : ""}">${variantId}</th></c:forEach>
                    <th data-name="allele.hvrVariantMap['b'] != undefined && allele.hvrVariantMap['b'].variantId.match(/[A-Z]+/) ? allele.hvrVariantMap['b'].variantId : '&nbsp;'" data-hvr-id="b" class="variantId indexRight"}">n/a</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="allele.hvrVariantMap['c'] != undefined && ${variantId} == allele.hvrVariantMap['c'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="c" class="variantId ${variantId == 1 ? "index" : ""}">${variantId}</th></c:forEach>
                    <th data-name="allele.hvrVariantMap['c'] != undefined && allele.hvrVariantMap['c'].variantId.match(/[A-Z]+/) ? allele.hvrVariantMap['c'].variantId : '&nbsp;'" data-hvr-id="c" class="variantId indexRight"}">n/a</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="allele.hvrVariantMap['d'] != undefined && ${variantId} == allele.hvrVariantMap['d'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="d" class="variantId ${variantId == 1 ? "index" : ""}">${variantId}</th></c:forEach>
                    <th data-name="allele.hvrVariantMap['d'] != undefined && allele.hvrVariantMap['d'].variantId.match(/[A-Z]+/) ? allele.hvrVariantMap['d'].variantId : '&nbsp;'" data-hvr-id="d" class="variantId indexRight"}">n/a</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="allele.hvrVariantMap['e'] != undefined && ${variantId} == allele.hvrVariantMap['e'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="e" class="variantId ${variantId == 1 ? "index" : ""}">${variantId}</th></c:forEach>
                    <th data-name="allele.hvrVariantMap['e'] != undefined && allele.hvrVariantMap['e'].variantId.match(/[A-Z]+/) ? allele.hvrVariantMap['e'].variantId : '&nbsp;'" data-hvr-id="e" class="variantId indexRight"}">n/a</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="allele.hvrVariantMap['f'] != undefined && ${variantId} == allele.hvrVariantMap['f'].variantId ? ${variantId} : '&nbsp;'" data-hvr-id="f" class="variantId ${variantId == 1 ? "index" : ""}">${variantId}</th></c:forEach>
                    <th data-name="allele.hvrVariantMap['f'] != undefined && allele.hvrVariantMap['f'].variantId.match(/[A-Z]+/) ? allele.hvrVariantMap['f'].variantId : '&nbsp;'" data-hvr-id="f" class="variantId indexRight"}">n/a</th>
            </tr>
            <tr id="hvrVariantSeqs" style="border: 1px solid black;">
                <th colspan="6" style="text-align: right;">protein sequence(s) &Longrightarrow;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['a'].variantMap['${variantId}'] == undefined ? '&nbsp;' : hvrMap['a'].variantMap['${variantId}'].proteinSequenceList.join('<br/>')" data-hvr-id="a" class="variantSeq ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="a" class="variantSeq indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['b'].variantMap['${variantId}'] == undefined ? '&nbsp;' : hvrMap['b'].variantMap['${variantId}'].proteinSequenceList.join('<br/>')" data-hvr-id="b" class="variantSeq ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="b" class="variantSeq indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['c'].variantMap['${variantId}'] == undefined ? '&nbsp;' : hvrMap['c'].variantMap['${variantId}'].proteinSequenceList.join('<br/>')" data-hvr-id="c" class="variantSeq ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="c" class="variantSeq indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['d'].variantMap['${variantId}'] == undefined ? '&nbsp;' : hvrMap['d'].variantMap['${variantId}'].proteinSequenceList.join('<br/>')" data-hvr-id="d" class="variantSeq ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="d" class="variantSeq indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['e'].variantMap['${variantId}'] == undefined ? '&nbsp;' : hvrMap['e'].variantMap['${variantId}'].proteinSequenceList.join('<br/>')" data-hvr-id="e" class="variantSeq ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="e" class="variantSeq indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['f'].variantMap['${variantId}'] == undefined ? '&nbsp;' : hvrMap['f'].variantMap['${variantId}'].proteinSequenceList.join('<br/>')" data-hvr-id="f" class="variantSeq ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="f" class="variantSeq indexRight">&nbsp;</th>
            </tr>
            <tr id="hvrKnownReactiveEpitopes" style="border: 1px solid black;">
                <th colspan="6" style="text-align: right;">is recipient known to have an antibody for this epitope? &Longrightarrow;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['a'].variantMap['${variantId}'].knownReactiveEpitopeForCompat" data-container="hvrMap['a'].variantMap['${variantId}']" data-hvr-id="a" class="knownReactiveEpitope ${variantId == 1 ? "index" : ""}"><input type="checkbox"></th></c:forEach><th data-hvr-id="a" class="knownReactiveEpitope indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['b'].variantMap['${variantId}'].knownReactiveEpitopeForCompat" data-container="hvrMap['b'].variantMap['${variantId}']" data-hvr-id="b" class="knownReactiveEpitope ${variantId == 1 ? "index" : ""}"><input type="checkbox"></th></c:forEach><th data-hvr-id="b" class="knownReactiveEpitope indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['c'].variantMap['${variantId}'].knownReactiveEpitopeForCompat" data-container="hvrMap['c'].variantMap['${variantId}']" data-hvr-id="c" class="knownReactiveEpitope ${variantId == 1 ? "index" : ""}"><input type="checkbox"></th></c:forEach><th data-hvr-id="c" class="knownReactiveEpitope indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['d'].variantMap['${variantId}'].knownReactiveEpitopeForCompat" data-container="hvrMap['d'].variantMap['${variantId}']" data-hvr-id="d" class="knownReactiveEpitope ${variantId == 1 ? "index" : ""}"><input type="checkbox"></th></c:forEach><th data-hvr-id="d" class="knownReactiveEpitope indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['e'].variantMap['${variantId}'].knownReactiveEpitopeForCompat" data-container="hvrMap['e'].variantMap['${variantId}']" data-hvr-id="e" class="knownReactiveEpitope ${variantId == 1 ? "index" : ""}"><input type="checkbox"></th></c:forEach><th data-hvr-id="e" class="knownReactiveEpitope indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['f'].variantMap['${variantId}'].knownReactiveEpitopeForCompat" data-container="hvrMap['f'].variantMap['${variantId}']" data-hvr-id="f" class="knownReactiveEpitope ${variantId == 1 ? "index" : ""}"><input type="checkbox"></th></c:forEach><th data-hvr-id="f" class="knownReactiveEpitope indexRight">&nbsp;</th>
            </tr>
            <tr id="hvrRecipientEpitopes" style="border: 1px solid black;">
                <th colspan="6" style="text-align: right;">is this a recipient epitope? &Longrightarrow;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['a'].variantMap['${variantId}'] != undefined && hvrMap['a'].variantMap['${variantId}'].compatIsRecipientEpitope ? 'Y' : '&nbsp;'" data-hvr-id="a" class="recipientEpitope ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="a" class="recipientEpitope indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['b'].variantMap['${variantId}'] != undefined && hvrMap['b'].variantMap['${variantId}'].compatIsRecipientEpitope ? 'Y' : '&nbsp;'" data-hvr-id="b" class="recipientEpitope ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="b" class="recipientEpitope indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['c'].variantMap['${variantId}'] != undefined && hvrMap['c'].variantMap['${variantId}'].compatIsRecipientEpitope ? 'Y' : '&nbsp;'" data-hvr-id="c" class="recipientEpitope ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="c" class="recipientEpitope indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['d'].variantMap['${variantId}'] != undefined && hvrMap['d'].variantMap['${variantId}'].compatIsRecipientEpitope ? 'Y' : '&nbsp;'" data-hvr-id="d" class="recipientEpitope ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="d" class="recipientEpitope indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['e'].variantMap['${variantId}'] != undefined && hvrMap['e'].variantMap['${variantId}'].compatIsRecipientEpitope ? 'Y' : '&nbsp;'" data-hvr-id="e" class="recipientEpitope ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="e" class="recipientEpitope indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['f'].variantMap['${variantId}'] != undefined && hvrMap['f'].variantMap['${variantId}'].compatIsRecipientEpitope ? 'Y' : '&nbsp;'" data-hvr-id="f" class="recipientEpitope ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="f" class="recipientEpitope indexRight">&nbsp;</th>
            </tr>
            <tr id="hvrPctBeadsPositives" style="border: 1px solid black;">
                <th colspan="6" style="text-align: right;">percentage of single antigen beads positive &Longrightarrow;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['a'].variantMap['${variantId}'] != undefined ? hvrMap['a'].variantMap['${variantId}'].compatPositiveSabPct : '&nbsp;'" data-hvr-id="a" class="pctBeadsPositive ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="a" class="pctBeadsPositive indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['b'].variantMap['${variantId}'] != undefined ? hvrMap['b'].variantMap['${variantId}'].compatPositiveSabPct : '&nbsp;'" data-hvr-id="b" class="pctBeadsPositive ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="b" class="pctBeadsPositive indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['c'].variantMap['${variantId}'] != undefined ? hvrMap['c'].variantMap['${variantId}'].compatPositiveSabPct : '&nbsp;'" data-hvr-id="c" class="pctBeadsPositive ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="c" class="pctBeadsPositive indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['d'].variantMap['${variantId}'] != undefined ? hvrMap['d'].variantMap['${variantId}'].compatPositiveSabPct : '&nbsp;'" data-hvr-id="d" class="pctBeadsPositive ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="d" class="pctBeadsPositive indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['e'].variantMap['${variantId}'] != undefined ? hvrMap['e'].variantMap['${variantId}'].compatPositiveSabPct : '&nbsp;'" data-hvr-id="e" class="pctBeadsPositive ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="e" class="pctBeadsPositive indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['f'].variantMap['${variantId}'] != undefined ? hvrMap['f'].variantMap['${variantId}'].compatPositiveSabPct : '&nbsp;'" data-hvr-id="f" class="pctBeadsPositive ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="f" class="pctBeadsPositive indexRight">&nbsp;</th>
            </tr>
            <tr id="hvrAntibodyPresents" style="border: 1px solid black;">
                <th colspan="6" style="text-align: right;">is an antibody for this epitope considered present? &Longrightarrow;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['a'].variantMap['${variantId}'] != undefined && hvrMap['a'].variantMap['${variantId}'].compatAntibodyConsideredPresent ? 'Y' : '&nbsp;'" data-hvr-id="a" class="antibodyPresent ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="a" class="antibodyPresent indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['b'].variantMap['${variantId}'] != undefined && hvrMap['b'].variantMap['${variantId}'].compatAntibodyConsideredPresent ? 'Y' : '&nbsp;'" data-hvr-id="b" class="antibodyPresent ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="b" class="antibodyPresent indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['c'].variantMap['${variantId}'] != undefined && hvrMap['c'].variantMap['${variantId}'].compatAntibodyConsideredPresent ? 'Y' : '&nbsp;'" data-hvr-id="c" class="antibodyPresent ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="c" class="antibodyPresent indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['d'].variantMap['${variantId}'] != undefined && hvrMap['d'].variantMap['${variantId}'].compatAntibodyConsideredPresent ? 'Y' : '&nbsp;'" data-hvr-id="d" class="antibodyPresent ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="d" class="antibodyPresent indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['e'].variantMap['${variantId}'] != undefined && hvrMap['e'].variantMap['${variantId}'].compatAntibodyConsideredPresent ? 'Y' : '&nbsp;'" data-hvr-id="e" class="antibodyPresent ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="e" class="antibodyPresent indexRight">&nbsp;</th>
                <c:forEach var="variantId" begin="1" end="6"><th data-name="hvrMap['f'].variantMap['${variantId}'] != undefined && hvrMap['f'].variantMap['${variantId}'].compatAntibodyConsideredPresent ? 'Y' : '&nbsp;'" data-hvr-id="f" class="antibodyPresent ${variantId == 1 ? "index" : ""}"></th></c:forEach><th data-hvr-id="f" class="antibodyPresent indexRight">&nbsp;</th>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>
        
    <p><br/>Copyright &copy; 2018, Geoffrey H. Smith, MD

</body>

</html>

<script>

var sessionId;
var reagentLotNumber; // the single antigen bead reagent lot number
var alleles = []; // the array of alleles
var hypervariableRegions = []; // the array of hypervariableRegions
var hvrMap = new Object(); // a map of hypervariable regions, indexed by hypervariable region ID
var sabOnly = false; // only show single antigen bead alleles
var selectedOnly = false; // only show donor type alleles or recipient type alleles or recipient antibody alleles
var statusSortMap = {
    'LC': { sequence: 3 },
    'LI': { sequence: 2 },
    'I':  { sequence: 1 },
    'AA': { sequence: 0 }
}

function getSessionId() {
    $("#working").show();
    return $.ajax({
        url: "/hladpb1/resources/session",
        dataType: "text"
    }).then(function(response) {
        sessionId = response;
    });
}

function resetSession() {
    $("#working").show();
    return $.ajax({
        url: "/hladpb1/resources/session/reset",
        dataType: "json",
        type: "PUT",
        contentType: "application/json"
    });
}

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

// Put a hypervariable region (updates known reactive epitope attribute).
function putHypervariableRegion(hypervariableRegion) {
    $("#working").show();
    return $.ajax({
        url: "/hladpb1/resources/hypervariableRegions/" + hypervariableRegion.hypervariableRegionName,
        dataType: "json",
        type: "PUT",
        contentType: "application/json",
        data: JSON.stringify(hypervariableRegion)
    });
}

// Get all alleles.
function getAlleles() {
    $("#working").show();
    return $.ajax({
        url: "/hladpb1/resources/alleles?noCodons=true&synonymous=false", // always filter out synonymous alleles
        dataType: "json"
    }).then(function(response) {
        alleles = response;
    });
}

// Put an allele (updates donor type, recipient type, and recipient antibodies).
function putAllele(allele) {
    $("#working").show();
    return $.ajax({
        url: "/hladpb1/resources/alleles/" + allele.alleleName + "?noCodons=true",
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
            else if(name.match(/.*ForCompat/)) {
                var val = eval($(this).data("name"));
                var tagAttributes = $(this)[0].outerHTML;
                tagAttributes = tagAttributes.substring(4, tagAttributes.indexOf(">"));
                rowHtml.push("<td " + tagAttributes + "><input type='checkbox'>" + $(this).data("label") + "</td>");
                
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
        $("#sessionId").html(sessionId);
        $("#imgtDbVersion").html(alleles[0].version);
        $("#reagentLotNumber").html(reagentLotNumber);
        $("#sabFilter").removeClass("filterEnabled");      if(sabOnly )     { $("#sabFilter").addClass("filterEnabled"); }
        $("#selectedFilter").removeClass("filterEnabled"); if(selectedOnly) { $("#selectedFilter").addClass("filterEnabled"); }
        $("#hvrVariantSeqs th.variantSeq, #hvrRecipientEpitopes th.recipientEpitope, #hvrPctBeadsPositives th.pctBeadsPositive").each(function() {
            $(this).html(eval($(this).data("name")));
        });
        $("#reportTable th, #reportTable td").removeClass("mismatch");
        $("#hvrAntibodyPresents th.antibodyPresent").each(function() {
            var val = eval($(this).data("name"));
            $(this).html(val);
            if(val == "Y") {
                var colNo = $(this).parent().children().index($(this)) + 6;
                $(this).addClass("mismatch");
                $("#reportTable tbody tr > td:nth-child(" + colNo + ")").addClass("mismatch");
            }
        });
        $("#hvrKnownReactiveEpitopes th.knownReactiveEpitope").each(function() {
            $(this).children("input").prop("checked", eval($(this).data("container")) != undefined && eval($(this).data("name")));
        });
        $("#reportTable tbody tr").each(function() {
            var alleleName = $(this).data("value");
            var allele = alleles.find(function(allele) { return alleleName == allele.alleleName; });
            if(isRowVisible(allele)) { $(this).show(); }
            else                     { $(this).hide(); }
            $(this).children("td").each(function() {
                if($(this).data("name").match(/.*ForCompat/)) {
                    $(this).children("input").prop("checked", eval($(this).data("name")));
                }
                if($(this).data("name") == "allele.compatInterpretation") {
                    $(this).html(eval($(this).data("name")));
                }
            });
        });
        
        $("#allelesLoaded").html($("#reportTable tbody tr").length);
        $("#allelesShown").html($("#reportTable tbody tr:visible").length);
        $("#allelesFiltered").html($("#reportTable tbody tr:hidden").length);
        dfr.resolve();
    }, 1);
    return dfr.promise();
}

// Row filtering function.
function isRowVisible(allele) {
    rowVisible = true;
    if(sabOnly && !allele.singleAntigenBead)                                                                               { rowVisible = false; }
    if(selectedOnly && !(allele.donorTypeForCompat || allele.recipientTypeForCompat || allele.recipientAntibodyForCompat)) { rowVisible = false; }
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

// Sort function.
function statusSort(a, b) {
    var A = statusSortMap[$(a).children("td").eq(5).html()].sequence;
    var B = statusSortMap[$(b).children("td").eq(5).html()].sequence;
    if(A < B)      { return -1; }
    else if(A > B) { return  1; }
    else           { return alleleNameSort(a, b); }
}

// Document ready! Let's go...
$(document).ready(function() {

    // Initial population of report table.
    getSessionId().then(getReagentLotNumber).then(getHypervariableRegions).then(getAlleles).then(populateTableRows).then(setUiState).done(function() { $("#working").hide(); });

    $("#resetSessionLink").click(function() {
        resetSession().then(getSessionId).then(getReagentLotNumber).then(getHypervariableRegions).then(getAlleles).then(populateTableRows).then(setUiState).done(function() { $("#working").hide(); });
    });

    // Change a hypervariable region input (i.e., click a checkbox).
    $("#reportTable thead").on("change", "input", function() {
        var hvrId = $(this).parent().data("hvr-id");
        if(eval($(this).parent().data("container")) != undefined) {
            eval($(this).parent().data("name") + " = " + $(this).prop("checked"));
        }
        putHypervariableRegion(hvrMap[hvrId]).then(getHypervariableRegions).then(getAlleles).then(setUiState).done(function() { $("#working").hide(); } );
    });

    // Change an allele input (i.e., click a checkbox).
    $("#reportTable tbody").on("change", "input", function() {
        var alleleName = $(this).parents("tr").data("value");
        var allele = alleles.find(function(allele) { return alleleName == allele.alleleName; });
        eval($(this).parent().data("name") + " = " + $(this).prop("checked"));
        putAllele(allele).then(getHypervariableRegions).then(getAlleles).then(setUiState).done(function() { $("#working").hide(); } );
    });

    // Single antigen bead filter toggle on/off. This is handled locally.
    $("#sabFilter").click(function() {
        sabOnly = !sabOnly;
        setUiState().done(function() { $("#working").hide(); });
    });

    // Donor type or recipient type or recipient antibody filter toggle on/off.
    // This is handled locally.
    $("#selectedFilter").click(function() {
        selectedOnly = !selectedOnly;
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
    
    // Click on sort. This is handled locally.
    $("#statusSort").click(function() {
        $("#working").show();
        setTimeout(function() {
            var rows = $('#reportTable tbody tr').get();
            rows.sort(statusSort);
            $.each(rows, function(index, row) {
                $('#reportTable').children('tbody').append(row);
            });
            $("#working").hide();
        }, 1);
    });
    
});

</script>
