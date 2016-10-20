/**
 * Created by lenovo on 2016-08-17.
 */
// require(["module/name", ...], function(params){ ... });
require(["jquery"], function ($) {
    // Preloader
    $('#status').delay(300).fadeOut();
    $('#preloader').delay(300).fadeOut('slow');
    $('body').delay(550).css({'overflow': 'visible'});
});