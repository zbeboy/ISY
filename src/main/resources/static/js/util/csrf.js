/**
 * Created by lenovo on 2016-09-14.
 */
define(["jquery"], function($) {
        /*
          init csrf token for ajax.
          在需要ajax的地方，需要带上该参数
        */
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function (e, xhr, options) {
            xhr.setRequestHeader(header, token);
         });

         return {
            token:token,
            header:header
         };
    }
);