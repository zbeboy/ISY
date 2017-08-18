/**
 * Created by lenovo on 2016/10/25.
 */
define(["jquery", "jquery.showLoading", "messenger"], function ($) {

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
                Messenger().post({
                    message: 'Loading js fail !',
                    type: 'error',
                    showCloseButton: true
                });
            });
    }

    return function (url, targetId, web_path) {
        endLoading(targetId);
        var loadingMessage;
        loadingMessage = Messenger().post({
            message: 'Loading ...',
            type: 'info',
            id: 'loadingMessage',
            showCloseButton: true
        });
        startLoading(targetId);
        $(targetId).empty();
        $.get(web_path + url, function (data) {
            $(targetId).html($(data).html());
            var scripts = $('.dy_script');
            for (var i = 0; i < scripts.length; i++) {
                loadJs(web_path + $(scripts[i]).val());
            }
            endLoading(targetId);
            loadingMessage.update({
                message: 'Loading finish , enjoy you life !',
                type: 'success',
                showCloseButton: true
            });
        });
    };
});