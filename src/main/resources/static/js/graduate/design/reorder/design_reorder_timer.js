/**
 * Created by zbeboy on 2017/7/21.
 */
requirejs.config({
    // pathsオプションの設定。"module/name": "path"を指定します。拡張子（.js）は指定しません。
    paths: {
        "jquery.timer": web_path + "/plugin/jquery_timer/jquery.timer.min"
    },
    // shimオプションの設定。モジュール間の依存関係を定義します。
    shim: {
        "jquery.timer": {
            // jQueryに依存するのでpathsで設定した"module/name"を指定します。
            deps: ["jquery"]
        }
    }
});
// require(["module/name", ...], function(params){ ... });
require(["jquery", "jquery.timer", "bootstrap"], function ($) {
    /**
     * Example 2 is similar to example 1.  The biggest difference
     * besides counting up is the ability to reset the timer to a
     * specific time.  To do this, there is an input text field
     * in a form.
     */
    var Example2 = new (function () {

        var $countdown,
            $form, // Form used to change the countdown time
            incrementTime = 70,
            currentTime = 30000,
            updateTimer = function () {
                $countdown.html(formatTime(currentTime));
                if (currentTime == 0) {
                    Example2.Timer.stop();
                    timerComplete();
                    Example2.resetCountdown();
                    return;
                }
                currentTime -= incrementTime / 10;
                if (currentTime < 0) currentTime = 0;
            },
            timerComplete = function () {
                alert('Example 2: Countdown timer complete!');
            },
            init = function () {
                $countdown = $('#countdown');
                Example2.Timer = $.timer(updateTimer, incrementTime, true);
                $form = $('#example2form');
                $form.bind('submit', function () {
                    Example2.resetCountdown();
                    return false;
                });
            };
        this.resetCountdown = function () {
            var newTime = parseInt($form.find('input[name=startTime]').val()) * 100;
            if (newTime > 0) {
                currentTime = newTime;
            }
            this.Timer.stop().once();
        };
        $(init);
    });

    // Common functions
    function pad(number, length) {
        var str = '' + number;
        while (str.length < length) {
            str = '0' + str;
        }
        return str;
    }

    function formatTime(time) {
        var min = parseInt(time / 6000),
            sec = parseInt(time / 100) - (min * 60),
            hundredths = pad(time - (sec * 100) - (min * 6000), 2);
        return (min > 0 ? pad(min, 2) : "00") + ":" + pad(sec, 2) + ":" + hundredths;
    }

    // 开始或暂停
    $('#play').click(function () {
        Example2.Timer.toggle();
    });

    // 停止或重置
    $('#stop').click(function () {
        Example2.resetCountdown();
    });

});