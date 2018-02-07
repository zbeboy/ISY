/**
 * Created by zbeboy on 2017/7/10.
 */
require(["jquery", "handlebars", "nav_active", "messenger", "bootstrap-daterangepicker", "clockface",
        "jquery.address"],
    function ($, Handlebars, nav_active) {
        /*
         ajax url.
         */
        var ajax_url = {
            update: '/web/graduate/design/replan/arrange/update',
            back: '/web/menu/graduate/design/replan'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.back);

        /*
         参数id
         */
        var paramId = {
            paperTime: '#paperTime',
            paperStartTime: '#paperStartTime',
            paperEndTime: '#paperEndTime',
            defenseTime: '#defenseTime',
            defenseStartTime: '#defenseStartTime',
            defenseEndTime: '#defenseEndTime',
            day_defense: '.day_defense',
            intervalTime: '#intervalTime',
            defenseNote: '#defenseNote'
        };

        /*
         参数
         */
        var param = {
            paperTime: $(paramId.paperTime).val(),
            defenseTime: $(paramId.defenseTime).val(),
            day_defense: [],
            intervalTime: $(paramId.intervalTime).val(),
            defenseNote: $(paramId.defenseNote).val()
        };

        /*
         检验id
         */
        var validId = {
            paperTime: '#valid_paper_time',
            defenseTime: '#valid_defense_time',
            intervalTime: '#valid_interval_time',
            defenseNote: '#valid_defense_note'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            paperTime: '#paper_time_error_msg',
            defenseTime: '#defense_time_error_msg',
            dayDefense: '#day_defense_error_msg',
            intervalTime: '#interval_time_error_msg',
            defenseNote: '#defense_note_error_msg'
        };

        /**
         * 检验成功
         * @param validId
         * @param errorMsgId
         */
        function validSuccessDom(validId, errorMsgId) {
            $(validId).addClass('has-success').removeClass('has-error');
            $(errorMsgId).addClass('hidden').text('');
        }

        /**
         * 检验失败
         * @param validId
         * @param errorMsgId
         * @param msg
         */
        function validErrorDom(validId, errorMsgId, msg) {
            $(validId).addClass('has-error').removeClass('has-success');
            $(errorMsgId).removeClass('hidden').text(msg);
        }

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back);
        });

        /**
         * 初始化参数
         */
        function initParam() {
            param.paperTime = $(paramId.paperTime).val();
            param.defenseTime = $(paramId.defenseTime).val();
            param.intervalTime = $(paramId.intervalTime).val();
            param.defenseNote = $(paramId.defenseNote).val();
        }

        $(paramId.paperTime).daterangepicker({
            "startDate": $(paramId.paperStartTime).val(),
            "endDate": $(paramId.paperEndTime).val(),
            "locale": {
                format: 'YYYY-MM-DD HH:mm:ss',
                applyLabel: '确定',
                cancelLabel: '取消',
                fromLabel: '起始时间',
                toLabel: '结束时间',
                customRangeLabel: '自定义',
                separator: ' 至 ',
                daysOfWeek: ['日', '一', '二', '三', '四', '五', '六'],
                monthNames: ['一月', '二月', '三月', '四月', '五月', '六月',
                    '七月', '八月', '九月', '十月', '十一月', '十二月']
            }
        }, function (start, end, label) {
            console.log('New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')');
        });

        $(paramId.defenseTime).daterangepicker({
            "startDate": $(paramId.defenseStartTime).val(),
            "endDate": $(paramId.defenseEndTime).val(),
            "locale": {
                format: 'YYYY-MM-DD HH:mm:ss',
                applyLabel: '确定',
                cancelLabel: '取消',
                fromLabel: '起始时间',
                toLabel: '结束时间',
                customRangeLabel: '自定义',
                separator: ' 至 ',
                daysOfWeek: ['日', '一', '二', '三', '四', '五', '六'],
                monthNames: ['一月', '二月', '三月', '四月', '五月', '六月',
                    '七月', '八月', '九月', '十月', '十一月', '十二月']
            }
        }, function (start, end, label) {
            console.log('New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')');
        });

        $(paramId.day_defense).clockface({format: 'HH:mm'});

        // 添加时段
        $('#addDefense').click(function () {
            defenseTemplate();
        });

        /**
         * 每日答辩时间
         */
        function defenseTemplate() {
            $(errorMsgId.dayDefense).before($("#defense-template").html());
            $(paramId.day_defense).clockface({format: 'HH:mm'});
        }

        // 删除每日答辩时间
        $('#add_form').delegate('.defenseDel', "click", function () {
            $(this).parent().parent().remove();
        });

        $(paramId.intervalTime).blur(function () {
            initParam();
            var intervalTime = param.intervalTime;
            if (!/^\d+$/.test(intervalTime)) {
                validErrorDom(validId.intervalTime, errorMsgId.intervalTime, '间隔时间为整数的任意数字');
            } else {
                validSuccessDom(validId.intervalTime, errorMsgId.intervalTime);
            }
        });

        $('#save').click(function () {
            add();
        });

        /*
         添加询问
         */
        function add() {
            initParam();
            var msg;
            msg = Messenger().post({
                message: "确定保存设置吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            validDayDefense();
                        }
                    },
                    cancel: {
                        label: '取消',
                        action: function () {
                            return msg.cancel();
                        }
                    }
                }
            });
        }

        // 检验每日答辩时间
        function validDayDefense() {
            var isValid = true;
            for (var i = 0; i < $(paramId.day_defense).length; i++) {
                if (i > 0) {
                    var cs = $($(paramId.day_defense)[i]).val().split(':');
                    var ds = $($(paramId.day_defense)[i - 1]).val().split(':');
                    if (Number(cs[0]) < Number(ds[0])) {
                        isValid = false;
                        break;
                    } else if (Number(cs[0]) === Number(ds[0])) {
                        if (Number(cs[1]) <= Number(ds[1])) {
                            isValid = false;
                            break;
                        }
                    }
                }
            }
            if (isValid) {
                validIntervalTime();
            } else {
                Messenger().post({
                    message: '每日答辩时间设置有误',
                    type: 'error',
                    showCloseButton: true
                });
            }
        }

        function validIntervalTime() {
            var intervalTime = param.intervalTime;
            if (!/^\d+$/.test(intervalTime)) {
                Messenger().post({
                    message: '间隔时间为整数的任意时间',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                sendAjax();
            }
        }

        function sendAjax() {
            Messenger().run({
                successMessage: '保存数据成功',
                errorMessage: '保存数据失败',
                progressMessage: '正在保存数据....'
            }, {
                url: web_path + ajax_url.update,
                type: 'post',
                data: $('#add_form').serialize(),
                success: function (data) {
                    if (data.state) {
                        $.address.value(ajax_url.back);
                    } else {
                        Messenger().post({
                            message: data.msg,
                            type: 'error',
                            showCloseButton: true
                        });
                    }
                },
                error: function (xhr) {
                    if ((xhr != null ? xhr.status : void 0) === 404) {
                        return "请求失败";
                    }
                    return true;
                }
            });
        }

    });