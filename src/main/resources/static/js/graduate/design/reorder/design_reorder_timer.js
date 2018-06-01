/**
 * Created by zbeboy on 2017/7/21.
 */
// require(["module/name", ...], function(params){ ... });
require(["jquery", "bootstrap"], function ($) {

    /*
     web storage key.
    */
    var webStorageKey = {
        DEFENSE_ORDER_ID: 'DEFENSE_ORDER_ID_'
    };

    var clock = new clock();
    var countdown = $('#countdown');
    var w;
    var isPlay = true;

    startWorker();

    function startWorker() {
        if (typeof(Worker) !== "undefined") {
            if (typeof(w) === "undefined") {
                w = new Worker(web_path + "/js/graduate/design/reorder/design_reorder_timer_script.js");
            }
            w.onmessage = function (event) {
                if (isPlay) {
                    clock.move();
                }
            };
        } else {
            countdown.html("Sorry, your browser does not support Web Workers...");
        }
    }

    function clock() {
        /*s是clock()中的变量，非var那种全局变量，代表剩余秒数*/
        this.s = localStorage.getItem(webStorageKey.DEFENSE_ORDER_ID + defenseOrderId);
        this.move = function () {
            /*输出前先调用exchange函数进行秒到分秒的转换，因为exchange并非在主函数window.onload使用，因此不需要进行声明*/
            countdown.html(exchange(this.s));
            localStorage.setItem(webStorageKey.DEFENSE_ORDER_ID + defenseOrderId, this.s);
            /*每被调用一次，剩余秒数就自减*/
            this.s = this.s - 1;
            /*如果时间耗尽，那么，弹窗，使按钮不可用，停止不停调用clock函数中的move()*/
            if (this.s < 0) {
                alert("时间到");
                w.terminate();
            }
        }
    }

    function exchange(time) {
        /*javascript的除法是浮点除法，必须使用Math.floor取其整数部分*/
        this.m = Math.floor(time / 60);
        /*存在取余运算*/
        this.s = (time % 60);
        this.text = this.m + "分" + this.s + "秒";
        /*传过来的形式参数time不要使用this，而其余在本函数使用的变量则必须使用this*/
        return this.text;
    }

    $('#play').click(function () {
        isPlay = true;
        $(this).prop('disabled', true);
        $('#pause').prop('disabled', false);
    });

    $('#pause').click(function () {
        isPlay = false;
        $(this).prop('disabled', true);
        $('#play').prop('disabled', false);
    });

    $('#rePlay').click(function () {
        localStorage.setItem(webStorageKey.DEFENSE_ORDER_ID + defenseOrderId, reorder_timer * 60);
        location.reload();
    });

});