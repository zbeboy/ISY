/**
 * Created by lenovo on 2016-10-30.
 */
define(["jquery"], function ($) {
    return function (activeMenu) {
        var url = activeMenu;
        var element = $('ul.nav a').filter(function () {
            return this.href == url || this.href.indexOf(url) >= 0;
        }).addClass('active').parent().parent().addClass('in').parent();
        if (element.is('li')) {
            element.addClass('active');
        }

        var thirdParent = element.parent();
        if(thirdParent.is('ul')){
            thirdParent.addClass('in');
            thirdParent.parent().addClass('active');
        }
    }
});