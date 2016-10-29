/**
 * Created by lenovo on 2016/10/25.
 */
define(["jquery", "jquery.showLoading", "messenger"], function ($, showLoading, messenger) {

    function startLoading(targetId) {
        // 显示遮罩
        $(targetId).showLoading();
    }

    function endLoading(targetId) {
        // 去除遮罩
        $(targetId).hideLoading();
    }

    /**
     * load js
     * @param jsPath js path.
     */
    function loadJs(jsPath) {
        $.getScript(jsPath)
            .done(function (script, textStatus) {
                console.log(textStatus);
            })
            .fail(function (jqxhr, settings, exception) {
                Messenger().post("loading js fail !");
            });
    }

    return function (url, targetId, web_path) {
        endLoading(targetId);
        startLoading(targetId);
        $(targetId).empty();
        $.get(web_path + url, function (data) {
            $(targetId).html($(data).html());
            var scripts = $('.dy_script');
            for (var i = 0; i < scripts.length; i++) {
                loadJs(web_path + $(scripts[i]).val());
            }
            endLoading(targetId);
            Messenger().post("loading finish !");
        });
    };
});