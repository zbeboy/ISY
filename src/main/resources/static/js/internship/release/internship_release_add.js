/**
 * Created by lenovo on 2016-11-10.
 */
//# sourceURL=internship_release_add.js
require(["jquery", "handlebars", "nav_active", "moment", "bootstrap-daterangepicker", "messenger", "jquery.address","bootstrap-select-zh-CN"],
    function ($, Handlebars, nav_active, moment) {

        /*
         ajax url.
         */
        var ajax_url = {
            back: '/web/menu/internship/release'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.back);

        /*
         参数id
         */
        var paramId = {
            releaseTitle: '#releaseTitle',
            teacherDistributionStartTime: '#teacherDistributionStartTime',
            teacherDistributionEndTime: '#teacherDistributionEndTime',
            startTime: '#startTime',
            endTime: '#endTime',
            departmentId: '#select_department',
            grade: '#select_grade'
        };

        /*
         参数
         */
        var param = {
            releaseTitle: $(paramId.releaseTitle).val().trim(),
            teacherDistributionStartTime: $(paramId.teacherDistributionStartTime).val(),
            teacherDistributionEndTime: $(paramId.teacherDistributionEndTime).val(),
            startTime: $(paramId.startTime).val(),
            endTime: $(paramId.endTime).val(),
            departmentId: $(paramId.departmentId).val().trim(),
            grade: $(paramId.grade).val().trim()
        };

        /*
         检验id
         */
        var validId = {
            releaseTitle: '#valid_release_title',
            teacherDistributionStartTime: '#valid_teacher_distribution_start_time',
            teacherDistributionEndTime: '#valid_teacher_distribution__end_time',
            startTime: '#valid_start_time',
            endTime: '#valid_end_time',
            departmentId: '#valid_department',
            grade: '#valid_grade'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            releaseTitle: '#release_title_error_msg',
            teacherDistributionStartTime: '#teacher_distribution_start_time_error_msg',
            teacherDistributionEndTime: '#teacher_distribution_end_time_error_msg',
            startTime: '#start_time_error_msg',
            endTime: '#end_time_error_msg',
            departmentId: '#department_error_msg',
            grade: '#grade_error_msg'
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

        /**
         * 初始化参数
         */
        function initParam() {
            param.releaseTitle = $(paramId.releaseTitle).val().trim();
            param.teacherDistributionStartTime = $(paramId.teacherDistributionStartTime).val();
            param.teacherDistributionEndTime = $(paramId.teacherDistributionEndTime).val();
            param.startTime = $(paramId.startTime).val();
            param.endTime = $(paramId.endTime).val();
            param.departmentId = $(paramId.departmentId).val().trim();
            param.grade = $(paramId.grade).val().trim();
        }

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back);
        });

        // 教师分配时间
        $('#teacherDistributionTime').daterangepicker({
            "minDate": moment(),
            "startDate": moment().add(1, "days"),
            "endDate": moment().add(2, "days"),
            "timePicker" : true,
            "timePicker24Hour": true,
            "timePickerIncrement": 30,
            "locale": {
                format: 'YYYY-MM-DD HH:mm',
                applyLabel : '确定',
                cancelLabel : '取消',
                fromLabel : '起始时间',
                toLabel : '结束时间',
                customRangeLabel : '自定义',
                daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],
                monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月',
                    '七月', '八月', '九月', '十月', '十一月', '十二月' ]
            }
        }, function (start, end, label) {
            console.log('New date range selected: ' + start.format('YYYY-MM-DD HH:mm:ss') + ' to ' + end.format('YYYY-MM-DD HH:mm:ss') + ' (predefined range: ' + label + ')');
        });

        // 实习时间
        $('#time').daterangepicker({
            "minDate": moment(),
            "startDate": moment().add(2, "days"),
            "endDate": moment().add(3, "days"),
            "timePicker" : true,
            "timePicker24Hour": true,
            "timePickerIncrement": 30,
            "locale": {
                format: 'YYYY-MM-DD HH:mm',
                applyLabel : '确定',
                cancelLabel : '取消',
                fromLabel : '起始时间',
                toLabel : '结束时间',
                customRangeLabel : '自定义',
                daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],
                monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月',
                    '七月', '八月', '九月', '十月', '十一月', '十二月' ]
            }
        }, function (start, end, label) {
            console.log('New date range selected: ' + start.format('YYYY-MM-DD HH:mm:ss') + ' to ' + end.format('YYYY-MM-DD HH:mm:ss') + ' (predefined range: ' + label + ')');
        });

        // 选择专业
        $('#select_science').selectpicker({
            liveSearch: true,
            deselectAllText:'反选',
            selectAllText:'全选'
        });


    });