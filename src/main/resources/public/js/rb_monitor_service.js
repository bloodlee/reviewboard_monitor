/**
 * Created by yli on 1/25/16.
 */

(function() {
    'use strict';

    angular.module('rb_monitors').service('rbMonitorService', ['$q', RbMonitorService]);

    function RbMonitorService($q) {
        var rbMonitorItems = [
            {
                name: 'Review Requests',
                icon: 'trends-icon.png',
                diagrams: [
                    {
                        title: 'Request posted in last 180 days',
                        remote_service: 'review_request_per_month_last_half_year'
                    }
                ]
            },
            {
                name: 'People',
                icon: 'People-icon.png',
                diagrams: [
                    {
                        title: 'People posted request in last 30 days',
                        remote_service: 'last_month_people_post'
                    }
                ]
            }
        ];

        return {
            loadAllMonitorItems : function() {
                return $q.when(rbMonitorItems);
            }
        };
    }

})();