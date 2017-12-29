/**
 * Created by lenovo on 2016/12/23.
 */
//# sourceURL=internship_regulate_add.js
require(["jquery", "handlebars", "nav_active", "moment", "messenger", "jquery.address", "bootstrap-daterangepicker", "bootstrap-maxlength"],
    function ($, Handlebars, nav_active) {

        /*
         ajax url.
         */
        var ajax_url = {
            update: '/web/internship/regulate/my/update',
            nav: '/web/menu/internship/regulate'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         参数id
         */
        var paramId = {
            studentId: '#select_student',
            staffId: '#staffId',
            internshipReleaseId: '#internshipReleaseId',
            studentName: '#studentName',
            studentNumber: '#studentNumber',
            studentTel: '#studentTel',
            internshipContent: '#internshipContent',
            internshipProgress: '#internshipProgress',
            reportWay: '#reportWay'
        };

        /*
         参数
         */
        var param = {
            studentId: '',
            staffId: '',
            internshipReleaseId: '',
            studentName: '',
            studentNumber: '',
            studentTel: '',
            internshipContent: '',
            internshipProgress: '',
            reportWay: ''
        };

        /*
         检验id
         */
        var validId = {
            internshipContent: '#valid_internship_content',
            internshipProgress: '#valid_internship_progress',
            reportWay: '#valid_report_way'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            internshipContent: '#internship_content_error_msg',
            internshipProgress: '#internship_progress_error_msg',
            reportWay: '#report_way_error_msg'
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
            param.studentId = $(paramId.studentId).val();
            param.staffId = $(paramId.staffId).val();
            param.internshipReleaseId = $(paramId.internshipReleaseId).val();
            param.studentName = $(paramId.studentName).val();
            param.studentNumber = $(paramId.studentNumber).val();
            param.studentTel = $(paramId.studentTel).val();
            param.internshipContent = $(paramId.internshipContent).val();
            param.internshipProgress = $(paramId.internshipProgress).val();
            param.reportWay = $(paramId.reportWay).val();
        }

        // 汇报日期
        $('#reportDate').daterangepicker({
            "startDate": $('#regulateDate').val(),
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

        init();

        function init(){
            initMaxLength();
        }

        /**
         * 初始化Input max length
         */
        function initMaxLength(){
            $(paramId.internshipContent).maxlength({
                alwaysShow: true,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });

            $(paramId.internshipProgress).maxlength({
                alwaysShow: true,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });

            $(paramId.reportWay).maxlength({
                alwaysShow: true,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });
        }

        /*
         返回
         */
        $('#page_back').click(function () {
            window.history.go(-1);
        });

        $(paramId.internshipContent).blur(function () {
            initParam();
            var internshipContent = param.internshipContent;
            if (internshipContent.length <= 0 || internshipContent.length > 200) {
                validErrorDom(validId.internshipContent, errorMsgId.internshipContent, '实习内容200个字符以内');
            } else {
                validSuccessDom(validId.internshipContent, errorMsgId.internshipContent);
            }
        });

        $(paramId.internshipProgress).blur(function () {
            initParam();
            var internshipProgress = param.internshipProgress;
            if (internshipProgress.length <= 0 || internshipProgress.length > 200) {
                validErrorDom(validId.internshipProgress, errorMsgId.internshipProgress, '实习进展200个字符以内');
            } else {
                validSuccessDom(validId.internshipProgress, errorMsgId.internshipProgress);
            }
        });

        $(paramId.reportWay).blur(function () {
            initParam();
            var reportWay = param.reportWay;
            if (reportWay.length <= 0 || reportWay.length > 200) {
                validErrorDom(validId.reportWay, errorMsgId.reportWay, '汇报途径20个字符以内');
            } else {
                validSuccessDom(validId.reportWay, errorMsgId.reportWay);
            }
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
                message: "确定更新监管记录吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            validInternshipContent();
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
         * 检验实习内容
         */
        function validInternshipContent() {
            var internshipContent = param.internshipContent;
            if (internshipContent.length <= 0 || internshipContent.length > 200) {
                Messenger().post({
                    message: '实习内容200个字符以内',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validInternshipProgress();
            }
        }

        /**
         * 检验实习进展
         */
        function validInternshipProgress() {
            var internshipProgress = param.internshipProgress;
            if (internshipProgress.length <= 0 || internshipProgress.length > 200) {
                Messenger().post({
                    message: '实习进展200个字符以内',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validReportWay();
            }
        }

        /**
         * 检验汇报途径
         */
        function validReportWay() {
            var reportWay = param.reportWay;
            if (reportWay.length <= 0 || reportWay.length > 200) {
                Messenger().post({
                    message: '汇报途径20个字符以内',
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