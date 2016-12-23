/**
 * Created by lenovo on 2016/12/23.
 */
require(["jquery", "handlebars", "nav_active","moment", "messenger", "jquery.address","bootstrap-daterangepicker","bootstrap-select-zh-CN"],
    function ($, Handlebars, nav_active) {

        /*
         ajax url.
         */
        var ajax_url = {
            save: '/web/internship/regulate/my/save',
            student_data_url:'/web/internship/regulate/students',
            nav:'/web/menu/internship/regulate'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         参数id
         */
        var paramId = {
            studentId:'#select_student'
        };

        /*
         参数
         */
        var param = {

        };

        /*
         检验id
         */
        var validId = {

        };

        /*
         错误消息id
         */
        var errorMsgId = {

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

        }

        init();

        function init(){
            $.get(web_path + ajax_url.student_data_url, {id: init_page_param.internshipReleaseId}, function (data) {
                studentData(data);
            });
        }

        /**
         * 学生数据
         * @param data
         */
        function studentData(data) {
            var source = $("#student-template").html();
            var template = Handlebars.compile(source);

            Handlebars.registerHelper('student_value', function () {
                var value = Handlebars.escapeExpression(this.studentId);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('student_name', function () {
                var name = Handlebars.escapeExpression(this.realName + ' ' + this.studentNumber);
                return new Handlebars.SafeString(name);
            });

            var html = template(data);
            $(paramId.studentId).html(html);
            initStudentSelect();
        }

        function initStudentSelect(){
            $(paramId.studentId).selectpicker({
                liveSearch: true,
                maxOptions: 1
            });
        }

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
                message: "确定保存日志吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            // validStudentName();
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
         * 发送数据到后台
         */
        function sendAjax() {
            Messenger().run({
                successMessage: '保存数据成功',
                errorMessage: '保存数据失败',
                progressMessage: '正在保存数据....'
            }, {
                url: web_path + ajax_url.save,
                type: 'post',
                data: $('#add_form').serialize(),
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