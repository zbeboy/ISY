/**
 * Created by zbeboy on 2017/7/31.
 */
//# sourceURL=design_reorder_question_edit.js
require(["jquery", "handlebars", "nav_active", "quill", "messenger", "jquery.address"],
    function ($, Handlebars, nav_active, Quill) {

        /*
         ajax url.
         */
        var ajax_url = {
            update: '/web/graduate/design/reorder/question',
            nav: '/web/menu/graduate/design/reorder'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         参数id
         */
        var paramId = {
            questionHtml: '#questionHtml',
            defenseQuestion: '#defenseQuestion'
        };

        /*
         参数
         */
        var param = {
            defenseQuestion: $(paramId.defenseQuestion).val()
        };

        // 初始化富文本框
        var quill = new Quill(paramId.questionHtml, {
            placeholder: '问题',
            theme: 'bubble'
        });

        /**
         * 初始化参数
         */
        function initParam() {
            param.defenseQuestion = quill.getText(0, quill.getLength());
        }


        init();

        function init() {
            if ($(paramId.defenseQuestion).val() !== '') {
                quill.setContents(JSON.parse($(paramId.defenseQuestion).val()));
            }
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
                message: "确定保存吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            $(paramId.defenseQuestion).val(JSON.stringify(quill.getContents()));
                            sendAjax();
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