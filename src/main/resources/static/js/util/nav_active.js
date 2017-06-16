/**
 * Created by lenovo on 2016-10-30.
 */
define(["jquery"], function ($) {
    return function (activeMenu) {
        var url = activeMenu;
        var element = $('ul.nav a').filter(function () {
            var subStr = '';
            if(this.href !== '' && this.href.indexOf('#') !== -1){
                subStr = this.href.substring(this.href.lastIndexOf('#') + 1);
            }

            return this.href === url || (subStr === url && subStr !== '');
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