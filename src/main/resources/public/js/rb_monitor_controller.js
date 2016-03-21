/**
 * Created by yli on 1/25/16.
 */

(function() {
    'use strict';

    angular.module('rb_monitors')
        .controller('rb_monitor_controller', [
            'rbMonitorService', '$http', '$scope', RbMonitorController
        ]);

    function RbMonitorController(rbMonitorService, $http, $scope) {
        var self = this;

        self.selected = null;
        self.monitorItems = [];
        self.selectMonitorItem = selectItem;

        self.selected_cl = []
        self.query = {
            order: 'reviewedClCount',
            limit: 200,
            page: 1
        };

        function getPerforceData(query) {
            $scope.promise = $http.get("/get_perforce_data").then(function (response) {
               $scope.cl_datas = response;
            });
        }

        $scope.onReorder = function (order) {
            getPerforceData(angular.extend({}, self.query, {order: order}));
        };

        rbMonitorService
            .loadAllMonitorItems()
            .then(function (items) {
                self.monitorItems = [].concat(items);

                selectItem(items[0]);

                getPerforceData(self.query);
            });

        function selectItem(item) {
            self.selected = item;

            item.diagrams.forEach(function(entry) {

                $http.get("/" + entry.remote_service)
                    .then(function(response) {
                        var data_x = [];
                        var data_y = [];

                        angular.forEach(response.data, function(value) {
                            this.push(value.val0);
                        }, data_x);

                        angular.forEach(response.data, function(value) {
                            this.push(value.val1);
                        }, data_y);

                        var data = [{
                            x: data_x,
                            y: data_y,
                            type: 'bar'
                        }];

                        Plotly.newPlot('plot_' + entry.remote_service, data);

                        $("#spinner_" + entry.remote_service).hide();
                    });
            });
        }

    }

})();