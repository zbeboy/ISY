/**
 * Created by zbeboy on 2017/6/5.
 */
require(["jquery", "handlebars", "nav_active", "quill", "messenger", "jquery.address", "bootstrap-maxlength"],
    function ($, Handlebars, nav_active, Quill) {

        /*
         ajax url.
         */
        var ajax_url = {
            update: '/web/graduate/design/subject/my/update',
            valid_title: '/web/graduate/design/subject/update/valid/title',
            nav: '/web/menu/graduate/design/subject'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         参数id
         */
        var paramId = {
            graduationDesignPresubjectId: '#graduationDesignPresubjectId',
            presubjectTitle: '#presubjectTitle',
            presubjectPlanHtml: '#presubjectPlanHtml',
            presubjectPlan: '#presubjectPlan',
            publicLevel: '#publicLevel'
        };

        /*
         参数
         */
        var param = {
            graduationDesignPresubjectId: $(paramId.graduationDesignPresubjectId).val(),
            presubjectTitle: $(paramId.presubjectTitle).val(),
            presubjectPlan: $(paramId.presubjectPlan).val(),
            publicLevel: $(paramId.publicLevel).val()
        };

        /*
         检验id
         */
        var validId = {
            presubjectTitle: '#valid_presubject_title',
            presubjectPlan: '#valid_presubject_plan'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            presubjectTitle: '#presubject_title_error_msg',
            presubjectPlan: '#presubject_plan_error_msg'
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

        // 初始化富文本框
        var quill = new Quill(paramId.presubjectPlanHtml, {
            placeholder: '计划',
            theme: 'bubble'
        });

        /**
         * 初始化参数
         */
        function initParam() {
            param.graduationDesignPresubjectId = $(paramId.graduationDesignPresubjectId).val();
            param.presubjectTitle = $(paramId.presubjectTitle).val();
            param.presubjectPlan = quill.getText(0, quill.getLength());
            param.publicLevel = $(paramId.publicLevel).val();
        }


        init();

        function init() {
            checkedPublicLevel();
            quill.setContents(JSON.parse($(paramId.presubjectPlan).val()));
            initMaxLength();
        }

        /**
         * 选中级别
         */
        function checkedPublicLevel() {
            var u = $(paramId.publicLevel).val();
            var t = $("input[name='publicLevel']");
            var size = t.length;
            for (var i = 0; i < size; i++) {
                var v = $(t[i]).val();
                if (v === u) {
                    $(t[i]).prop('checked', true);
                    break;
                }
            }
        }

        /**
         * 初始化Input max length
         */
        function initMaxLength() {
            $(paramId.presubjectTitle).maxlength({
                alwaysShow: true,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });
        }

        /*
         即时检验题目
         */
        $(paramId.presubjectTitle).blur(function () {
            initParam();
            var presubjectTitle = param.presubjectTitle;
            var graduationDesignPresubjectId = param.graduationDesignPresubjectId;
            if (presubjectTitle.length <= 0 || presubjectTitle.length > 100) {
                validErrorDom(validId.presubjectTitle, errorMsgId.presubjectTitle, '题目100个字符以内');
            } else {
                $.post(web_path + ajax_url.valid_title, {
                    presubjectTitle: presubjectTitle,
                    graduationDesignPresubjectId: graduationDesignPresubjectId
                }, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.presubjectTitle, errorMsgId.presubjectTitle);
                    } else {
                        validErrorDom(validId.presubjectTitle, errorMsgId.presubjectTitle, data.msg);
                    }
                });
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
                message: "确定保存吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            validPresubjectTitle();
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
         * 检验题目
         */
        function validPresubjectTitle() {
            var presubjectTitle = param.presubjectTitle;
            var graduationDesignPresubjectId = param.graduationDesignPresubjectId;
            if (presubjectTitle.length <= 0 || presubjectTitle.length > 100) {
                Messenger().post({
                    message: '题目100个字符以内',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                $.post(web_path + ajax_url.valid_title, {
                    presubjectTitle: presubjectTitle,
                    graduationDesignPresubjectId: graduationDesignPresubjectId
                }, function (data) {
                    if (data.state) {
                        validPresubjectPlan();
                    } else {
                        Messenger().post({
                            message: data.msg,
                            type: 'error',
                            showCloseButton: true
                        });
                    }
                });
            }
        }

        /**
         * 检验计划
         */
        function validPresubjectPlan() {
            var presubjectPlan = param.presubjectPlan;
            if (presubjectPlan.length <= 1) {
                Messenger().post({
                    message: '计划不能为空',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                $(paramId.presubjectPlan).val(JSON.stringify(quill.getContents()));
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