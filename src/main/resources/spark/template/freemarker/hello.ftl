<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>ReviewBoard Monitor</title>
    <meta name="viewport" content="initial-scale=1" />

    <script type="text/javascript" src="./bower_components/plotlyjs/plotly.js"></script>
    <link href="./bower_components/angular-material/angular-material.css" rel="stylesheet" />
    <link href="./bower_components/angular-material-data-table/dist/md-data-table.min.css" rel="stylesheet" type="text/css"/>
    <link rel="shortcut icon" href="./assets/img/rb_32x32.png" media="screen"/>
    <link rel="stylesheet" href="assets/app.css"/>
</head>

<body ng-app="starterApp" layout="column" ng-controller="rb_monitor_controller as rbm">

<!-- Container #1 (see wireframe) -->
<md-toolbar layout="row" >
    <div>
        <table style="width:auto">
            <tr>
                <td>
                    <img src="./assets/img/rb_32x32.png" align="middle"/>
                </td>
                <td>
                    <h3>Review Board Monitor</h3>
                </td>
            </tr>
        </table>
    </div>
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
        </md-list>
    </md-sidenav>

    <!-- Container #4 -->
    <md-content flex id="content">
        <div ng-show="rbm.selected.type == 'chart'">
            <div ng-repeat="diagram in rbm.selected.diagrams">
                <h2>{{diagram.title}}</h2>

                <div id="plot_{{diagram.remote_service}}"></div>
                <#--<svg id="spinner_{{diagram.remote_service}}" class="spinner" width="65px" height="65px" viewBox="0 0 66 66" xmlns="http://www.w3.org/2000/svg">-->
                    <#--<circle class="path" fill="none" stroke-width="6" stroke-linecap="round" cx="33" cy="33" r="30"></circle>-->
                <#--</svg>-->
                <div layout="row" layout-sm="column" layout-align="space-around" id="spinner_{{diagram.remote_service}}">
                    <md-progress-circular md-mode="indeterminate" ></md-progress-circular>
                </div>
            </div>
        </div>
        <div ng-show="rbm.selected.type == 'perforce'">
            <md-toolbar class="md-table-toolbar md-default">
                <div class="md-toolbar-tools">
                    <h2>Reviewed Perforce Changelist (last month)</h2>
                    <span>Latest changelist in database is {{rbm.latestClId}}</span>
                </div>
            </md-toolbar>

            <#--<div>-->
                <#--Data From:-->
                <#--<md-datepicker ng-model="myDate" md-placeholder="Enter date"></md-datepicker>-->
            <#--</div>-->

            <!-- exact table from live demo -->
            <md-table-container>
                <table md-table md-progress="promise">
                    <thead md-head md-order="rbm.query.order" md-on-reorder="rbm.onReorder">
                    <tr md-row>
                        <th md-column md-order-by="firstNameToLower"><span>First Name</span></th>
                        <th md-column md-order-by="lastNameToLower"><span>Last Name</span></th>
                        <th md-column md-order-by="p4AccountToLower"><span>P4 Account</span></th>
                        <th md-column md-numeric>Reviewed P4 CL Count</th>
                        <th md-column md-numeric>All P4 CL Count</th>
                        <th md-column md-numeric>Reviewed Ratio (%)</th>
                    </tr>
                    </thead>
                    <tbody md-body>
                    <tr md-row md-select="cl_data" md-select-id="{{cl_data.name}}" md-auto-select ng-repeat="cl_data in cl_datas.data">
                        <td md-cell>{{cl_data.firstName}}</td>
                        <td md-cell>{{cl_data.lastName}}</td>
                        <td md-cell>{{cl_data.p4Account}}</td>
                        <td md-cell>{{cl_data.reviewedClCount}}</td>
                        <td md-cell>{{cl_data.allClCount}}</td>
                        <td md-cell>{{cl_data.ratio}}</td>
                    </tr>
                    </tbody>
                </table>
            </md-table-container>
        </div>
        <#--<div ng-if="rbm.selected.type == 'utilities'">-->
            <#--<md-toolbar>-->
                <#--<div class="md-toolbar-tools">-->
                    <#--<h2>-->
                        <#--<span>Perforce</span>-->
                    <#--</h2>-->
                <#--</div>-->
            <#--</md-toolbar>-->
            <#--<md-content flex>-->
                <#--<md-button class="md-raised md-primary" ng-disabled="rmb.selected.p4.initialized"-->
                           <#--ng-click="rbm.initialize_perforce(rbm.selected)">Initialize</md-button>-->
            <#--</md-content>-->
        <#--</div>-->
    </md-content>

</div>

<script src="./bower_components/angular/angular.js" type="text/javascript" ></script>
<script src="./bower_components/angular-animate/angular-animate.js" type="text/javascript" ></script>
<script src="./bower_components/angular-aria/angular-aria.js" type="text/javascript" ></script>
<script src="./bower_components/angular-material/angular-material.js" type="text/javascript" ></script>
<script src="./bower_components/jquery/dist/jquery.js" type="text/javascript" ></script>

<script src="./js/rb_monitors.js"></script>
<script src="./js/rb_monitor_service.js"></script>
<script src="./js/rb_monitor_controller.js"></script>

<script>
    angular.module('starterApp', ['ngMaterial', 'rb_monitors', 'md.data.table']);
</script>

<script type="text/javascript" src="./bower_components/angular-material-data-table/dist/md-data-table.min.js"></script>

</body>
</html>