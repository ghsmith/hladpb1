<!DOCTYPE html>
<html>
    <head>

        <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css" />
        <script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
        <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js" integrity="sha256-VazP97ZCwtekAsvgPBSUwPFKdrwD3unUfSGVYrahUqU=" crossorigin="anonymous"></script>

        <link type="text/css" rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jsgrid/1.5.3/jsgrid.min.css" />
        <link type="text/css" rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jsgrid/1.5.3/jsgrid-theme.min.css" />
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jsgrid/1.5.3/jsgrid.min.js"></script>

        <style>
            
            td {
                white-space: nowrap;
            }
            
        </style>
        
    </head>

    <body>

        <div id="jsGrid"></div>

    </body>

</html>

<script>

    $("#jsGrid").jsGrid({
        width: "100%",
        height: "80vh",

        sorting: true,
        paging: false,
        autoload: true,

        controller: {
            loadData: function() {
                var d = $.Deferred();
                $.ajax({
                    url: "<%= request.getContextPath() %>/resources/alleles",
                    dataType: "json"
                }).done(function(response) {
                    d.resolve(response);
                });
                return d.promise();
            }
        },
        
        fields: [
            { name: "alleleName", title: "Allele Name", type: "text" },
            { name: "c1", title: "1", type: "text" },
            { name: "c10", title: "", type: "text" },
            { name: "c20", title: "", type: "text" },
            { name: "c30", title: "", type: "text" },
            { name: "c40", title: "", type: "text" },
            { name: "c50", title: "", type: "text" },
            { name: "c60", title: "", type: "text" },
            { name: "c70", title: "", type: "text" },
            { name: "c80", title: "", type: "text" },
            { name: "c90", title: "10", type: "text" }
        ]
    });

</script>
