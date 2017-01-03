/**
 * Created by lenovo on 2016-12-18.
 */
require(["jquery", "handlebars", "nav_active", "moment", "messenger", "jquery.address", "bootstrap-daterangepicker"],
    function ($, Handlebars, nav_active) {

        /*
         ajax url.
         */
        var ajax_url = {
            update: '/web/internship/journal/my/update',
            nav: '/web/menu/internship/journal'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         参数id
         */
        var paramId = {
            studentName: '#studentName',
            internshipJournalContent: '#internshipJournalContent'
        };

        /*
         参数
         */
        var param = {
            studentName: $(paramId.studentName).val(),
            internshipJournalContent: $(paramId.internshipJournalContent).val()
        };

        /*
         检验id
         */
        var validId = {
            studentName: '#valid_student_name',
            internshipJournalContent: '#valid_internship_journal_content'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            studentName: '#student_name_error_msg',
            internshipJournalContent: '#internship_journal_content_error_msg'
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
            param.internshipJournalContent = $(paramId.internshipJournalContent).val();
        }

        // 日志日期
        $('#internshipJournalDate').daterangepicker({
            "startDate": $('#journalDate').val(),
            "singleDatePicker": true,
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

        /*
         即时检验学生姓名
         */
        $(paramId.studentName).blur(function () {
            initParam();
            var studentName = param.studentName;
            if (studentName.length <= 0 || studentName.length > 10) {
                validErrorDom(validId.studentName, errorMsgId.studentName, '姓名10个字符以内');
            } else {
                validSuccessDom(validId.studentName, errorMsgId.studentName);
            }
        });

        /*
         即时检验内容与感想
         */
        $(paramId.internshipJournalContent).blur(function () {
            initParam();
            var internshipJournalContent = param.internshipJournalContent;
            if (internshipJournalContent.length <= 0 || internshipJournalContent.length > 10) {
                validErrorDom(validId.internshipJournalContent, errorMsgId.internshipJournalContent, '内容与感想不能为空');
            } else {
                validSuccessDom(validId.internshipJournalContent, errorMsgId.internshipJournalContent);
            }
        });

        /*
         返回
         */
        $('#page_back').click(function () {
            window.history.go(-1);
        });

        /*
         保存数据
         */
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
                message: "确定更新日志吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            validStudentName();
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

        /**
         * 检验姓名
         */
        function validStudentName() {
            initParam();
            var studentName = param.studentName;
            if (studentName.length <= 0 || studentName.length > 10) {
                Messenger().post({
                    message: '姓名10个字符以内',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validInternshipJournalContent();
            }
        }

        /**
         * 检验内容与感想
         */
        function validInternshipJournalContent() {
            var internshipJournalContent = param.internshipJournalContent;
            if (internshipJournalContent.length <= 0 || internshipJournalContent.length > 10) {
                Messenger().post({
                    message: '内容与感想不能为空',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                sendAjax();
            }
        }

        /**
         * 发送数据到后台
         */
        function sendAjax() {
            Messenger().run({
                successMessage: '保存数据成功',
                errorMessage: '保存数据失败',
                progressMessage: '正在保存数据....'
            }, {
                url: web_path + ajax_url.update,
                type: 'post',
                data: $('#edit_form').serialize(),
                success: function (data) {
                    if (data.state) {
                        window.history.go(-1);
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