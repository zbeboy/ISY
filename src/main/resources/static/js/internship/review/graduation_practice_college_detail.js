/**
 * Created by lenovo on 2016/12/9.
 */
//# sourceURL=graduation_practice_college_detail.js
require(["jquery", "handlebars", "nav_active", "messenger", "jquery.address"],
    function ($, Handlebars, nav_active) {
        /*
         ajax url.
         */
        var ajax_url = {
            save: '/web/internship/review/audit/save',
            audit_pass_url: '/web/internship/review/audit/pass',
            audit_fail_url: '/web/internship/review/audit/fail',
            nav: '/web/menu/internship/review'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

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

        /*
         返回
         */
        $('#page_back').click(function () {
            window.history.go(-1);
        });

        /*
         保存
         */
        $('#save').click(function () {
            var form = $('#edit_form');
            $.post(web_path + ajax_url.save, form.serialize(), function (data) {
                Messenger().post({
                    message: data.msg,
                    type: 'info',
                    showCloseButton: true
                });
            });
        });

        /*
         通过
         */
        $('#pass').click(function () {
            var id = $('#internshipReleaseId').val();
            var studentId = $('#studentId').val();
            var studentName = $('#studentName').val();
            ask(id, studentId, '', 2, studentName, ajax_url.audit_pass_url);
        });

        /*
         通过
         */
        $('#fail').click(function () {
            var id = $('#internshipReleaseId').val();
            var studentId = $('#studentId').val();
            showStateModal(3, id, studentId, '审核不通过');
        });

        /*
         提交变更申请
         */
        $('#stateOk').click(function () {
            stateAdd();
        });

        /*
         取消变更申请
         */
        $('#stateCancel').click(function () {
            hideStateModal();
        });

        /*
         状态申请提交询问
         */
        function stateAdd() {
            var msg;
            msg = Messenger().post({
                message: '确定不通过吗?',
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            validReason();
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
         * 展示变更模态框
         * @param state
         * @param internshipReleaseId
         * @param studentId
         * @param title
         */
        function showStateModal(state, internshipReleaseId, studentId, title) {
            $('#applyState').val(state);
            $('#applyInternshipReleaseId').val(internshipReleaseId);
            $('#applyStudentId').val(studentId);
            $('#stateModalLabel').text(title);
            $('#stateModal').modal('show');
        }

        /**
         * 隐藏变更模态框
         */
        function hideStateModal() {
            $('#applyState').val('');
            $('#applyInternshipReleaseId').val('');
            $('#applyStudentId').val('');
            $('#reason').val('');
            $('#stateModalLabel').text('');
            validCleanDom('#valid_reason', '#reason_error_msg');
            $('#stateModal').modal('hide');
        }

        var to_audit = false;
        $('#stateModal').on('hidden.bs.modal', function (e) {
            // do something...
            if (to_audit) {
                to_audit = false;
                window.history.go(-1);
            }
        });

        /*
         检验原因字数
         */
        $('#reason').blur(function () {
            var reason = $('#reason').val();
            if (reason.length <= 0 || reason.length > 500) {
                validErrorDom('#valid_reason', '#reason_error_msg', '原因500个字符以内');
            } else {
                validSuccessDom('#valid_reason', '#reason_error_msg');
            }
        });

        function validReason() {
            var reason = $('#reason').val();
            if (reason.length <= 0 || reason.length > 500) {
                validErrorDom('#valid_reason', '#reason_error_msg', '原因500个字符以内');
            } else {
                sendStateAjax();
            }
        }

        /**
         * 发送状态申请
         */
        function sendStateAjax() {
            $.post(web_path + ajax_url.audit_fail_url, $('#state_form').serialize(), function (data) {
                if (data.state) {
                    to_audit = true;
                    hideStateModal();
                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        }

        /**
         * 状态修改询问
         * @param internshipReleaseId 实习发布id
         * @param studentId 学生id
         * @param reason 原因
         * @param state 状态
         * @param studentName 学生名
         * @param url 提交url
         */
        function ask(internshipReleaseId, studentId, reason, state, studentName, url) {
            var msg;
            msg = Messenger().post({
                message: "确定通过学生 '" + studentName + "'  吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            sendAjax(internshipReleaseId, studentId, reason, state, url);
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
        function sendAjax(internshipReleaseId, studentId, reason, state, url) {
            Messenger().run({
                successMessage: '保存数据成功',
                errorMessage: '保存数据失败',
                progressMessage: '正在保存数据....'
            }, {
                url: web_path + url,
                type: 'post',
                data: {
                    internshipReleaseId: internshipReleaseId,
                    studentId: studentId,
                    reason: reason,
                    internshipApplyState: state
                },
                success: function (data) {
                    if (data.state) {
                        Messenger().post({
                            message: data.msg,
                            type: 'info',
                            showCloseButton: true
                        });
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