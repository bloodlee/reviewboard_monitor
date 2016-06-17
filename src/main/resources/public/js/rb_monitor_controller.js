/**
 * Created by yli on 1/25/16.
 */

(function() {
    'use strict';

    angular.module('rb_monitors')
        .controller('rb_monitor_controller', [
            'rbMonitorService', '$http', '$scope', '$interval', RbMonitorController
        ]);

    function RbMonitorController(rbMonitorService, $http, $scope, $interval) {
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

        self.latestClId = -1;

        self.p4_updator_status = "";
        self.p4_is_updating = false;

        self.p4_statistic_start_date = new Date();
        self.p4_statistic_start_date.setMonth(self.p4_statistic_start_date.getMonth()-1);

        console.log(self.p4_statistic_start_date);

        function getPerforceData(query) {
            console.log("get the perforce data...");

            $scope.promise = $http.get(
                "/get_perforce_data/" + self.p4_statistic_start_date.getFullYear() + "/"
                + (self.p4_statistic_start_date.getMonth() + 1) + "/" + self.p4_statistic_start_date.getDate()).then(function (response) {
                $scope.cl_datas = response;

                var compareFun = function (a, b) {
                    var order = query.order;

                    var opposite = true;
                    if (order.startsWith('-')) {
                        opposite = false;
                        order = order.substr(1, order.length - 1);
                    }

                    var result = 1;
                    if (order == 'firstName' || order == 'lastName' || order == 'p4Account') {
                        if (a[order] == null) {
                            result = -1;
                        } else if (b[order] == null) {
                            result = 1;
                        } else {
                            result = a[order].localeCompare(b[order]);
                        }
                    } else {
                        if (a[order] > b[order]) {
                            result = 1;
                        } else if (a[order] < b[order]) {
                            result = -1;
                        } else {
                            result = 0;
                        }
                    }

                    if (opposite) {
                        result = -1 * result;
                    }

                    return result;
                };

                $scope.cl_datas.data.sort(compareFun);
            });
        }

        self.update_p4_data = function () {
            getPerforceData(self.query);
        };

        self.get_reviewed_request = function(p4Account) {
            console.log(p4Account);

            $scope.rr_promise = $http.get(
                "/get_review_request/" + p4Account + "/" + self.p4_statistic_start_date.getFullYear() + "/"
                + (self.p4_statistic_start_date.getMonth() + 1) + "/" + self.p4_statistic_start_date.getDate()).then(function (response) {
                $scope.rr_datas = response;
            });

            self.selected.type = "reviewrequest_details";
        };

        self.get_p4_cl_details = function(p4Account) {
            console.log(p4Account);

            $scope.rr_promise = $http.get(
                "/get_changelist/" + p4Account + "/" + self.p4_statistic_start_date.getFullYear() + "/"
                + (self.p4_statistic_start_date.getMonth() + 1) + "/" + self.p4_statistic_start_date.getDate()).then(function (response) {
                $scope.cl_datas = response;
            });

            self.selected.type = "p4changelist_details";
        };

        function refreshLatestClId() {
            $http.get("/get_max_cl_id").then(function (response) {
                self.latestClId = response.data.latest_id;
            })
        }

        self.onReorder = function (order) {
            getPerforceData(angular.extend({}, self.query, {order: order}));
        };

        rbMonitorService
            .loadAllMonitorItems()
            .then(function (items) {
                self.monitorItems = [].concat(items);

                selectItem(items[0]);

                refreshLatestClId();
                getPerforceData(self.query);
                update_p4_status();

                $http.get("/get_rb_host").then(function (response) {
                    self.rb_host = response.data;
                });

                $http.get("/get_p4web_host").then(function (response) {
                    self.p4web_host = response.data;
                });

            });
        
        var promise_to_update_p4_status;

        self.start_p4_updator_monitor = function() {
            // stops any running interval to avoid two intervals running at the same time
            self.stop_p4_updator_monitor();

            // store the interval promise
            promise_to_update_p4_status = $interval(update_p4_status, 1000 * 30);
        };

        self.stop_p4_updator_monitor = function() {
            $interval.cancel(promise_to_update_p4_status);
        };

        function selectItem(item) {
            self.selected = item;

            if (item.type == "chart") {
                item.diagrams.forEach(function (entry) {

                    $http.get("/" + entry.remote_service)
                        .then(function (response) {
                            var data_x = [];
                            var data_y = [];

                            angular.forEach(response.data, function (value) {
                                this.push(value.val0);
                            }, data_x);

                            angular.forEach(response.data, function (value) {
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
                self.stop_p4_updator_monitor();
            } else if (item.type == 'utilities') {
                self.start_p4_updator_monitor();
            }
        }



        function update_p4_status() {
            console.log('updating p4 updator status');

            $http.get("/p4_updator_status").then(function (response) {
                self.p4_updator_status = response.data;
                self.p4_is_updating = (self.p4_updator_status.indexOf("stopped") == -1);
            });

            refreshLatestClId();
        }

        function startP4UpdateingImmediately() {
            $http.get("/start_p4_updating").then(function (reponse) {
               console.log(reponse.data);
            });
        }
    }

})();