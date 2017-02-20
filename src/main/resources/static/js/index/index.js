/**
 * Created by lenovo on 2016-08-17.
 */
// require(["module/name", ...], function(params){ ... });
require(["jquery", "bootstrap"], function ($) {
    // Preloader
    $('#status').delay(300).fadeOut();
    $('#preloader').delay(300).fadeOut('slow');
    $('body').delay(550).css({'overflow':'visible'});

    // qq social
    $('.fa-qq').popover({
        trigger: 'focus',
        html: true,
        animation: true,
        content: '<img src="' + web_path + '/images/social-qq.png" style="height: 150px;width: 150px;" alt="QQ群" />',
        placement: 'top'
    });

    // weixin social
    $('.fa-weixin').popover({
        trigger: 'focus',
        html: true,
        animation: true,
        content: '<img src="' + web_path + '/images/social-weixin.jpg" style="height: 150px;width: 150px;" alt="微信公众号" />',
        placement: 'top'
    });
});