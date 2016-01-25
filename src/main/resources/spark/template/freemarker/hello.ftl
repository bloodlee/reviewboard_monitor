<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>ReviewBoard Monitor</title>
    <meta name="viewport" content="initial-scale=1" />

    <script type="text/javascript" src="./bower_components/plotlyjs/plotly.js"></script>
    <link href="./bower_components/angular-material/angular-material.css" rel="stylesheet" />
    <link rel="stylesheet" href="assets/app.css"/>
</head>

<body ng-app="starterApp" layout="column" ng-controller="rb_monitor_controller as rbm">

<!-- Container #1 (see wireframe) -->
<md-toolbar layout="row" >
    <h3>Review Board Monitor</h3>
</md-toolbar>

<!-- Container #2 -->
<div flex layout="row">

    <!-- Container #3 -->
    <md-sidenav md-is-locked-open="true" class="md-whiteframe-z2">
        <md-list>

            <!-- List item #1 -->
            <md-list-item ng-repeat="item in rbm.monitorItems">
                <md-button ng-click="rbm.selectMonitorItem(item)" ng-class="{'selected' : item === rbm.selected }">
                    <img src="./assets/img/{{item.icon}}" class="avatar"></img>
                    {{item.name}}
                </md-button>
            </md-list-item>

            <#--<!-- List item #2 &ndash;&gt;-->
            <#--<md-list-item >-->
                <#--<md-button>-->
                    <#--&lt;#&ndash;<md-icon md-svg-src="./assets/svg/avatar-4.svg" class="avatar"></md-icon>&ndash;&gt;-->
                    <#--<img src="./assets/img/People-icon.png" class="avatar"></img>-->
                    <#--People-->
                <#--</md-button>-->
            <#--</md-list-item>-->

        </md-list>
    </md-sidenav>

    <!-- Container #4 -->
    <md-content flex id="content">
        <h2>Review Request sent in last 30 days</h2>
        <div id="myDiv"><!-- Plotly chart will be drawn inside this DIV --></div>
        <svg id="spinner1" class="spinner" width="65px" height="65px" viewBox="0 0 66 66" xmlns="http://www.w3.org/2000/svg">
            <circle class="path" fill="none" stroke-width="6" stroke-linecap="round" cx="33" cy="33" r="30"></circle>
        </svg>

        <h2>Per Month New Added Review Requests (in last half year)</h2>
        <div id="per_month_review_requests"><!-- Plotly chart will be drawn inside this DIV --></div>
        <svg id="spinner2" class="spinner" width="65px" height="65px" viewBox="0 0 66 66" xmlns="http://www.w3.org/2000/svg">
            <circle class="path" fill="none" stroke-width="6" stroke-linecap="round" cx="33" cy="33" r="30"></circle>
        </svg>
    </md-content>

</div>

<script src="./bower_components/angular/angular.js" type="text/javascript" ></script>
<script src="./bower_components/angular-animate/angular-animate.js" type="text/javascript" ></script>
<script src="./bower_components/angular-aria/angular-aria.js" type="text/javascript" ></script>
<script src="./bower_components/angular-material/angular-material.js" type="text/javascript" ></script>
<script src="./bower_components/jquery/dist/jquery.js" type="text/javascript" ></script>

<script src="./js/rb_monitors.js" type="text/javascript" ></script>
<script src="./js/rb_monitor_service.js" type="text/javascript" ></script>
<script src="./js/rb_monitor_controller.js" type="text/javascript" ></script>

<script>
    // Include the dependency upon ngMaterial - important !!
    var app = angular.module('starterApp', ['ngMaterial', 'rb_monitors']);
//    app.controller('data', function($scope, $http) {
//        $http.get("/last_month_people_post").then(function(response) {
//            var data_x = [];
//            var data_y = [];
//
//            angular.forEach(response.data, function(value) {
//                this.push(value.val0);
//            }, data_x);
//
//            angular.forEach(response.data, function(value) {
//                this.push(value.val1);
//            }, data_y);
//
//            var data = [{
//                x: data_x,
//                y: data_y,
//                type: 'bar'
//            }];
//
//            Plotly.newPlot('myDiv', data);
//
//            $("#spinner1").hide();
//        });
//
//        $http.get("/review_request_per_month_last_half_year").then(function(response) {
//            var data_x = [];
//            var data_y = [];
//
//            angular.forEach(response.data, function(value) {
//                this.push(value.val0);
//            }, data_x);
//
//            angular.forEach(response.data, function(value) {
//                this.push(value.val1);
//            }, data_y);
//
//            var data = [{
//                x: data_x,
//                y: data_y,
//                type: 'bar'
//            }];
//
//            Plotly.newPlot('per_month_review_requests', data);
//            $("#spinner2").hide();
//        });
    });

//    angular.run(function($log) {
//        $log.debug("starterApp + ngMaterial running...");
//    });
</script>


</body>
</html>