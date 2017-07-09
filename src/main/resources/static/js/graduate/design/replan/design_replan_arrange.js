/**
 * Created by lenovo on 2017-07-08.
 */
require(["jquery", "handlebars", "nav_active", "moment", "messenger", "bootstrap-daterangepicker", "clockface",
        "jquery.address", "jquery.showLoading"],
    function ($, Handlebars, nav_active, moment) {
        /*
         ajax url.
         */
        var ajax_url = {
            back: '/web/menu/graduate/design/replan'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.back);

        /*
         参数id
         */
        var paramId = {
            paperTime: '#paperTime',
            defenseTime: '#defenseTime',
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
         清除验证
         */
        function validCleanDom(inputId, errorId) {
            $(inputId).removeClass('has-error').removeClass('has-success');
            $(errorId).addClass('hidden').text('');
        }

        function startLoading() {
            // 显示遮罩
            $('#page-wrapper').showLoading();
        }

        function endLoading() {
            // 去除遮罩
            $('#page-wrapper').hideLoading();
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
            "startDate": moment().add(1, "days"),
            "endDate": moment().add(2, "days"),
            "locale": {
                format: 'YYYY-MM-DD',
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
            "startDate": moment().add(1, "days"),
            "endDate": moment().add(2, "days"),
            "locale": {
                format: 'YYYY-MM-DD',
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

    });