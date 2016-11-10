/**
 * Created by lenovo on 2016-11-10.
 */
require(["jquery", "handlebars", "messenger", "jquery.address"], function ($, Handlebars) {

    /*
     ajax url.
     */
    var ajax_url = {
        release_url: '/web/internship/release/add'
    };

    /*
     发布
     */
    $('#release').click(function () {
        $.address.value(ajax_url.release_url);
    });

});