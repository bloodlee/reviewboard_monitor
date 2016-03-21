/**
 * Created by yli on 1/25/16.
 */

(function() {
    'use strict';

    var myApp = angular.module('rb_monitors', ['ngMaterial']);

    myApp.directive('ngIf', function () {
        return {
            link: function(scope, element, attrs) {
                if(scope.$eval(attrs.ngIf)) {
                    // remove '<div ng-if...></div>'
                    element.replaceWith(element.children())
                } else {
                    element.replaceWith(' ')
                }
            }
        }
    });

})();
