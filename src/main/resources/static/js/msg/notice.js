/**
 * Created by lenovo on 2016-08-27.
 */
// require(["module/name", ...], function(params){ ... });
require(["jquery"], function ($) {
    var ajax_url = {
        login: '/login'
    };
    $('#back').click(function () {
        window.location.href = web_path + ajax_url.login;
    });
});