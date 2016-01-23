<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>ReviewBoard Monitor</title>
    <script type="text/javascript" src="js/plotly-latest.min.js"></script>
</head>
<body>

<h1>Last Month</h1>

<#--<div id="tester" style="width:600px;height:250px;"></div>-->

<#--<script>-->
    <#--TESTER = document.getElementById('tester');-->
    <#--Plotly.plot( TESTER, [{-->
        <#--x: [1, 2, 3, 4, 5],-->
        <#--y: [1, 2, 4, 8, 16] }], {-->
        <#--margin: { t: 0 } } );-->
<#--</script>-->

<div id="myDiv" style="width: 800px; height: 400px;"><!-- Plotly chart will be drawn inside this DIV --></div>
<script>
    var data = [{
        x: [<#list names as name>"${name}",</#list>],
        y: [<#list counts as count>${count},</#list>],
        type: 'bar'
    }];

    Plotly.newPlot('myDiv', data);
</script>

</body>
</html>