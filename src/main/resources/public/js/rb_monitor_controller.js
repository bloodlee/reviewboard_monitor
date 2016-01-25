/**
 * Created by yli on 1/25/16.
 */

(function() {
    'use strict';

    angular.module('rb_monitors')
        .controller('rb_monitor_controller', [
            'rbMonitorService', RbMonitorController
        ]);

    function RbMonitorController(rbMonitorService) {
        var self = this;

        self.selected = null;
        self.monitorItems = [];
        self.selectMonitorItem = selectItem;

        rbMonitorService
            .loadAllMonitorItems()
            .then(function (items) {
                self.monitorItems = [].concat(items);
                self.selected = items[0];
            });

        function selectItem(item) {
            self.selected = item;
        }
    }

})();