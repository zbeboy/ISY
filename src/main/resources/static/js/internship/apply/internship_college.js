/**
 * Created by lenovo on 2016/11/25.
 */
//# sourceURL=internship_college.js
require(["jquery", "handlebars","nav_active","moment", "messenger", "jquery.address","bootstrap-select-zh-CN","bootstrap-daterangepicker"],
    function ($, Handlebars,nav_active,moment) {
        /*
         ajax url.
         */
        var ajax_url = {
            save: '/web/internship/teacher_distribution/save',
            teacher_data_url: '/web/internship/teacher_distribution/batch/distribution/teachers',
            back: ''
        };

        // 刷新时选中菜单
        nav_active(ajax_url.back);

        /*
         参数id
         */
        var paramId = {
            studentName: '#studentName',
            qqMailbox: '#qqMailbox',
            parentalContact: '#parentalContact',
            headmaster: '#select_headmaster',
            internshipCollegeName: '#internshipCollegeName',
            internshipCollegeAddress: '#internshipCollegeAddress',
            internshipCollegeContacts: '#internshipCollegeContacts',
            internshipCollegeTel: '#internshipCollegeTel',
            time: '#time'
        };

        /*
         参数
         */
        var param = {
            studentName: $(paramId.studentName).val(),
            qqMailbox: $(paramId.qqMailbox).val(),
            parentalContact: $(paramId.parentalContact).val(),
            headmaster: $(paramId.headmaster).val(),
            internshipCollegeName: $(paramId.internshipCollegeName).val(),
            internshipCollegeAddress: $(paramId.internshipCollegeAddress).val(),
            internshipCollegeContacts: $(paramId.internshipCollegeContacts).val(),
            internshipCollegeTel: $(paramId.internshipCollegeTel).val(),
            time: $(paramId.time).val()
        };

        /*
         检验id
         */
        var validId = {
            studentName: '#valid_student_name',
            qqMailbox: '#valid_qq_mailbox',
            parentalContact: '#valid_parental_contact',
            headmaster: '#valid_headmaster',
            internshipCollegeName: '#valid_internship_college_name',
            internshipCollegeAddress: '#valid_internship_college_address',
            internshipCollegeContacts: '#valid_internship_college_contacts',
            internshipCollegeTel: '#valid_internship_college_tel',
            time: '#valid_time'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            studentName: '#student_name_error_msg',
            qqMailbox: '#qq_mailbox_error_msg',
            parentalContact: '#parental_contact_error_msg',
            headmaster: '#headmaster_error_msg',
            internshipCollegeName: '#internship_college_name_error_msg',
            internshipCollegeAddress: '#internship_college_address_error_msg',
            internshipCollegeContacts: '#internship_college_contacts_error_msg',
            internshipCollegeTel: '#internship_college_tel_error_msg',
            time: '#time'
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

        /**
         * 初始化参数
         */
        function initParam() {
            param.studentName = $(paramId.studentName).val();
            param.qqMailbox = $(paramId.qqMailbox).val();
            param.parentalContact = $(paramId.parentalContact).val();
            param.headmaster = $(paramId.headmaster).val();
            param.internshipCollegeName = $(paramId.internshipCollegeName).val();
            param.internshipCollegeAddress = $(paramId.internshipCollegeAddress).val();
            param.internshipCollegeContacts = $(paramId.internshipCollegeContacts).val();
            param.internshipCollegeTel = $(paramId.internshipCollegeTel).val();
            param.time = $(paramId.time).val();
        }

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back);
        });

        // 实习时间
        $(paramId.time).daterangepicker({
            "startDate": moment().add(2, "days"),
            "endDate": moment().add(3, "days"),
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

        // 选择班主任
        $(paramId.headmaster).selectpicker({
            liveSearch: true
        });

        init();

        function init(){
            // 初始化班主任数据
        }

        /**
         * 班主任数据 展现
         * @param data json数据
         */
        function headmasterData(data) {
            var source = $("#teacher-template").html();
            var template = Handlebars.compile(source);

            Handlebars.registerHelper('teacher_value', function () {
                var value = Handlebars.escapeExpression(this.staffId);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('teacher_name', function () {
                var name = Handlebars.escapeExpression(this.realName + ' ' + this.mobile);
                return new Handlebars.SafeString(name);
            });

            var html = template(data);
            $(paramId.headmaster).html(html);
        }
    });