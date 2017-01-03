/**
 * Created by lenovo on 2016-12-29.
 */
require(["jquery", "jquery.address"],
    function ($) {

        var ajax_url = {
            back: '/anyone/message/'
        };

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back);
        });

    });