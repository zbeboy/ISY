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
                Messenger().post("loading js " + textStatus + " !");
            })
            .fail(function (jqxhr, settings, exception) {
                Messenger().post("loading js fail !");
            });
    }

    return function (url, param, targetId, jsPath) {
        startLoading(targetId);
        if (param != null && param.length > 0) {
            $.get(url, param, function (data) {
                $(targetId).html($(data).html());
                if (jsPath != null && jsPath.length > 0) {
                    loadJs(jsPath);
                }
                endLoading(targetId);
                Messenger().post("loading finish !");
            });
        } else {
            $.get(url, function (data) {
                $(targetId).html($(data).html());
                if (jsPath != null && jsPath.length > 0) {
                    loadJs(jsPath);
                }
                endLoading(targetId);
                Messenger().post("loading finish !");
            });
        }
    };
});