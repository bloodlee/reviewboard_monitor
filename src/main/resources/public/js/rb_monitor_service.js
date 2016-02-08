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
                    },
                    {
                        title: 'Requests grouped by product in last 30 days',
                        remote_service: 'get_requests_groupd_by_product_in_last_30_days'
                    }
                ]
            },
            {
                name: 'People',
                icon: 'People-icon.png',
                diagrams: [
                    {
                        title: 'In last month, who posted requests more than 5',
                        remote_service: 'last_month_people_post'
                    },
                    {
                        title: 'New added users per month in last year',
                        remote_service: 'new_added_user_per_month_in_last_year'
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