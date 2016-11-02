/**
 * Created by lenovo on 2016-11-02.
 */
require(["jquery", "jquery.address"],
    function ($, jqueryAddress) {
        /*
         ajax url
         */
        var ajax_url = {
            profile_edit: '/anyone/users/profile/edit'
        };

        /*
         用户信息编辑
         */
        $('#profileEdit').click(function () {
            $.address.value(ajax_url.profile_edit);
        });

    });