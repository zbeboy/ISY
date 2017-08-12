/**
 * Created by lenovo on 2016-12-01.
 */
//# sourceURL=graduation_practice_unify_edit.js
require(["jquery", "handlebars", "nav_active", "moment", "lodash", "files", "messenger", "jquery.address",
        "bootstrap-select-zh-CN", "bootstrap-daterangepicker", "bootstrap-maxlength", "jquery.showLoading"],
    function ($, Handlebars, nav_active, moment, D, files) {
        /*
         ajax url.
         */
        var ajax_url = {
            update: '/web/internship/apply/graduation/unify/update',
            teacher_data_url: '/web/internship/apply/teachers',
            internship_files_url: '/user/internship/files',
            download_file: '/anyone/users/download/file',
            back: '/web/menu/internship/apply'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.back);

        /*
         参数id
         */
        var paramId = {
            studentId: '#studentId',
            studentName: '#studentName',
            qqMailbox: '#qqMailbox',
            parentalContact: '#parentalContact',
            headmaster: '#select_headmaster',
            graduationPracticeUnifyName: '#graduationPracticeUnifyName',
            graduationPracticeUnifyAddress: '#graduationPracticeUnifyAddress',
            graduationPracticeUnifyContacts: '#graduationPracticeUnifyContacts',
            graduationPracticeUnifyTel: '#graduationPracticeUnifyTel',
            startTime: '#startTime',
            endTime: '#endTime'
        };

        /*
         参数
         */
        var param = {
            studentId: $(paramId.studentId).val(),
            studentName: $(paramId.studentName).val(),
            qqMailbox: $(paramId.qqMailbox).val(),
            parentalContact: $(paramId.parentalContact).val(),
            headmaster: $(paramId.headmaster).val(),
            graduationPracticeUnifyName: $(paramId.graduationPracticeUnifyName).val(),
            graduationPracticeUnifyAddress: $(paramId.graduationPracticeUnifyAddress).val(),
            graduationPracticeUnifyContacts: $(paramId.graduationPracticeUnifyContacts).val(),
            graduationPracticeUnifyTel: $(paramId.graduationPracticeUnifyTel).val(),
            startTime: $(paramId.startTime).val(),
            endTime: $(paramId.endTime).val()
        };

        /*
         检验id
         */
        var validId = {
            studentName: '#valid_student_name',
            qqMailbox: '#valid_qq_mailbox',
            parentalContact: '#valid_parental_contact',
            headmaster: '#valid_headmaster',
            graduationPracticeUnifyName: '#valid_graduation_practice_unify_name',
            graduationPracticeUnifyAddress: '#valid_graduation_practice_unify_address',
            graduationPracticeUnifyContacts: '#valid_graduation_practice_unify_contacts',
            graduationPracticeUnifyTel: '#valid_graduation_practice_unify_tel',
            startTime: '#valid_start_time',
            endTime: '#valid_end_time'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            studentName: '#student_name_error_msg',
            qqMailbox: '#qq_mailbox_error_msg',
            parentalContact: '#parental_contact_error_msg',
            headmaster: '#headmaster_error_msg',
            graduationPracticeUnifyName: '#graduation_practice_unify_name_error_msg',
            graduationPracticeUnifyAddress: '#graduation_practice_unify_address_error_msg',
            graduationPracticeUnifyContacts: '#graduation_practice_unify_contacts_error_msg',
            graduationPracticeUnifyTel: '#graduation_practice_unify_tel_error_msg',
            startTime: '#start_time_error_msg',
            endTime: '#end_time_error_msg'
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
            param.studentId = $(paramId.studentId).val();
            param.studentName = $(paramId.studentName).val();
            param.qqMailbox = $(paramId.qqMailbox).val();
            param.parentalContact = $(paramId.parentalContact).val();
            param.headmaster = $(paramId.headmaster).val();
            param.graduationPracticeUnifyName = $(paramId.graduationPracticeUnifyName).val();
            param.graduationPracticeUnifyAddress = $(paramId.graduationPracticeUnifyAddress).val();
            param.graduationPracticeUnifyContacts = $(paramId.graduationPracticeUnifyContacts).val();
            param.graduationPracticeUnifyTel = $(paramId.graduationPracticeUnifyTel).val();
            param.startTime = $(paramId.startTime).val();
            param.endTime = $(paramId.endTime).val();
        }

        var selectedHeadmasterCount = true;

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back);
        });

        function startLoading() {
            // 显示遮罩
            $('#page-wrapper').showLoading();
        }

        function endLoading() {
            // 去除遮罩
            $('#page-wrapper').hideLoading();
        }

        init();

        function init() {
            initParam();
            // 初始化班主任数据
            startLoading();
            $.get(web_path + ajax_url.teacher_data_url, {
                id: init_page_param.internshipReleaseId,
                studentId: param.studentId
            }, function (data) {
                endLoading();
                headmasterData(data);
            });

            $.get(web_path + ajax_url.internship_files_url, {internshipReleaseId: init_page_param.internshipReleaseId}, function (data) {
                initFileShow(data);
            });

            initInputState();
            initMaxLength();
        }

        /**
         * 初始化Input max length
         */
        function initMaxLength() {
            var internshipApplyState = init_page_param.internshipApplyState;
            if (internshipApplyState !== '') {
                if (internshipApplyState == 5) { // 基本信息修改状态，不允许修改单位信息
                    $(paramId.studentName).maxlength({
                        alwaysShow: true,
                        threshold: 10,
                        warningClass: "label label-success",
                        limitReachedClass: "label label-danger"
                    });

                    $(paramId.qqMailbox).maxlength({
                        alwaysShow: true,
                        threshold: 10,
                        warningClass: "label label-success",
                        limitReachedClass: "label label-danger"
                    });
                } else if (internshipApplyState == 7) {// 单位信息修改状态，不允许修改基本信息
                    $(paramId.graduationPracticeUnifyName).maxlength({
                        alwaysShow: true,
                        threshold: 10,
                        warningClass: "label label-success",
                        limitReachedClass: "label label-danger"
                    });

                    $(paramId.graduationPracticeUnifyAddress).maxlength({
                        alwaysShow: true,
                        threshold: 10,
                        warningClass: "label label-success",
                        limitReachedClass: "label label-danger"
                    });

                    $(paramId.graduationPracticeUnifyContacts).maxlength({
                        alwaysShow: true,
                        threshold: 10,
                        warningClass: "label label-success",
                        limitReachedClass: "label label-danger"
                    });
                } else {
                    $(paramId.studentName).maxlength({
                        alwaysShow: true,
                        threshold: 10,
                        warningClass: "label label-success",
                        limitReachedClass: "label label-danger"
                    });

                    $(paramId.qqMailbox).maxlength({
                        alwaysShow: true,
                        threshold: 10,
                        warningClass: "label label-success",
                        limitReachedClass: "label label-danger"
                    });

                    $(paramId.graduationPracticeUnifyName).maxlength({
                        alwaysShow: true,
                        threshold: 10,
                        warningClass: "label label-success",
                        limitReachedClass: "label label-danger"
                    });

                    $(paramId.graduationPracticeUnifyAddress).maxlength({
                        alwaysShow: true,
                        threshold: 10,
                        warningClass: "label label-success",
                        limitReachedClass: "label label-danger"
                    });

                    $(paramId.graduationPracticeUnifyContacts).maxlength({
                        alwaysShow: true,
                        threshold: 10,
                        warningClass: "label label-success",
                        limitReachedClass: "label label-danger"
                    });
                }
            }
        }

        /**
         * 初始化input
         */
        function initInputState() {
            var internshipApplyState = init_page_param.internshipApplyState;
            if (internshipApplyState !== '') {
                if (internshipApplyState == 5) { // 基本信息修改状态，不允许修改单位信息
                    $(paramId.graduationPracticeUnifyName).attr("readonly", true);
                    $(paramId.graduationPracticeUnifyAddress).attr("readonly", true);
                    $(paramId.graduationPracticeUnifyContacts).attr("readonly", true);
                    $(paramId.graduationPracticeUnifyTel).attr("readonly", true);
                    initInternshipTime();// 初始化时间选择
                } else if (internshipApplyState == 7) {// 单位信息修改状态，不允许修改基本信息
                    $(paramId.studentName).attr("readonly", true);
                    $(paramId.qqMailbox).attr("readonly", true);
                    $(paramId.parentalContact).attr("readonly", true);
                    $(paramId.startTime).attr("readonly", true);
                    $(paramId.endTime).attr("readonly", true);
                    $(paramId.startTime).val(moment().format('YYYY-MM-DD', init_page_param.startTime));
                    $(paramId.endTime).val(moment().format('YYYY-MM-DD', init_page_param.endTime));
                    $('#man').attr("disabled", true);
                    $('#woman').attr("disabled", true);
                    $(paramId.headmaster).attr("disabled", true);
                } else {
                    initInternshipTime();// 初始化时间选择
                }
            }
        }

        /**
         * 初始化实习时间
         */
        function initInternshipTime() {
            // 实习开始时间
            $(paramId.startTime).daterangepicker({
                "singleDatePicker": true,
                "startDate": init_page_param.startTime,
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

            // 实习结束时间
            $(paramId.endTime).daterangepicker({
                "singleDatePicker": true,
                "startDate": init_page_param.endTime,
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
        }

        /**
         * 班主任数据 展现
         * @param data json数据
         */
        function headmasterData(data) {
            var template = Handlebars.compile($("#teacher-template").html());

            Handlebars.registerHelper('teacher_value', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.realName + ' ' + this.mobile));
            });

            Handlebars.registerHelper('teacher_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.realName + ' ' + this.mobile));
            });

            $(paramId.headmaster).html(template(data));
            initHeadmasterSelect();
        }

        // 选择班主任
        function initHeadmasterSelect() {
            $(paramId.headmaster).selectpicker({
                liveSearch: true,
                maxOptions: 1
            });
        }

        /**
         * 文件显示
         * @param data 数据
         */
        function initFileShow(data) {
            var template = Handlebars.compile($("#file-template").html());

            Handlebars.registerHelper('original_file_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.originalFileName));
            });

            Handlebars.registerHelper('size', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(files(this.size)));
            });

            Handlebars.registerHelper('lastPath', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.relativePath));
            });

            Handlebars.registerHelper('new_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.newName));
            });

            Handlebars.registerHelper('ext', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.ext));
            });

            Handlebars.registerHelper('l_size', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.size));
            });

            $('#fileShow').append(template(data));
        }

        /*
         下载附件
         */
        $('#fileShow').delegate('.downloadfile', "click", function () {
            var id = $(this).attr('data-file-id');
            window.location.href = web_path + ajax_url.download_file + '?fileId=' + id;
        });

        $(paramId.headmaster).on('loaded.bs.select', function (e) {
            // do something...
            // 只在页面初始化加载一次
            if (selectedHeadmasterCount) {
                selectedHeadmaster();
                $(paramId.headmaster).selectpicker('refresh');
                selectedHeadmasterCount = false;
            }
        });

        function selectedHeadmaster() {
            var realHeadmaster = $('#headmaster').val() + ' ' + $('#headmasterContact').val();
            var headmasterChildrens = $(paramId.headmaster).children();
            for (var i = 0; i < headmasterChildrens.length; i++) {
                if ($(headmasterChildrens[i]).val() === realHeadmaster) {
                    $(headmasterChildrens[i]).prop('selected', true);
                    break;
                }
            }
        }

        $(paramId.studentName).blur(function () {
            initParam();
            var studentName = param.studentName;
            if (studentName.length <= 0 || studentName.length > 15) {
                validErrorDom(validId.studentName, errorMsgId.studentName, '姓名15个字符以内');
            } else {
                validSuccessDom(validId.studentName, errorMsgId.studentName);
            }
        });

        $(paramId.qqMailbox).blur(function () {
            initParam();
            var qqMailbox = param.qqMailbox;
            if (qqMailbox.length <= 0 || qqMailbox.length > 100) {
                validErrorDom(validId.qqMailbox, errorMsgId.qqMailbox, 'qq邮箱100个字符以内');
            } else {
                if (D.endsWith(qqMailbox, '@qq.com')) {
                    validSuccessDom(validId.qqMailbox, errorMsgId.qqMailbox);
                } else {
                    validErrorDom(validId.qqMailbox, errorMsgId.qqMailbox, '请填写正确的qq邮箱');
                }
            }
        });

        $(paramId.parentalContact).blur(function () {
            initParam();
            var parentalContact = param.parentalContact;
            var regex = /^1[0-9]{10}$/;
            var isPhone = /^([0-9]{3,4}-)?[0-9]{7,8}$/;
            if (!regex.test(parentalContact)) {
                if (!isPhone.test(parentalContact)) {
                    validErrorDom(validId.parentalContact, errorMsgId.parentalContact, '请填写正确的联系方式');
                } else {
                    validSuccessDom(validId.parentalContact, errorMsgId.parentalContact);
                }
            } else {
                validSuccessDom(validId.parentalContact, errorMsgId.parentalContact);
            }
        });

        $(paramId.headmaster).blur(function () {
            initParam();
            var headmaster = param.headmaster;
            if (headmaster.length <= 0) {
                validErrorDom(validId.headmaster, errorMsgId.headmaster, '请选择班主任');
            } else {
                validSuccessDom(validId.headmaster, errorMsgId.headmaster);
            }
        });


        $(paramId.graduationPracticeUnifyName).blur(function () {
            initParam();
            var graduationPracticeUnifyName = param.graduationPracticeUnifyName;
            if (graduationPracticeUnifyName.length <= 0 || graduationPracticeUnifyName.length > 200) {
                validErrorDom(validId.graduationPracticeUnifyName, errorMsgId.graduationPracticeUnifyName, '实习单位200个字符以内');
            } else {
                validSuccessDom(validId.graduationPracticeUnifyName, errorMsgId.graduationPracticeUnifyName);
            }
        });

        $(paramId.graduationPracticeUnifyAddress).blur(function () {
            initParam();
            var graduationPracticeUnifyAddress = param.graduationPracticeUnifyAddress;
            if (graduationPracticeUnifyAddress.length <= 0 || graduationPracticeUnifyAddress.length > 500) {
                validErrorDom(validId.graduationPracticeUnifyAddress, errorMsgId.graduationPracticeUnifyAddress, '实习单位地址500个字符以内');
            } else {
                validSuccessDom(validId.graduationPracticeUnifyAddress, errorMsgId.graduationPracticeUnifyAddress);
            }
        });

        $(paramId.graduationPracticeUnifyContacts).blur(function () {
            initParam();
            var graduationPracticeUnifyContacts = param.graduationPracticeUnifyContacts;
            if (graduationPracticeUnifyContacts.length <= 0 || graduationPracticeUnifyContacts.length > 10) {
                validErrorDom(validId.graduationPracticeUnifyContacts, errorMsgId.graduationPracticeUnifyContacts, '实习单位联系人10个字符以内');
            } else {
                validSuccessDom(validId.graduationPracticeUnifyContacts, errorMsgId.graduationPracticeUnifyContacts);
            }
        });

        $(paramId.graduationPracticeUnifyTel).blur(function () {
            initParam();
            var graduationPracticeUnifyTel = param.graduationPracticeUnifyTel;
            var regex = /^1[0-9]{10}$/;
            var isPhone = /^([0-9]{3,4}-)?[0-9]{7,8}$/;
            if (!regex.test(graduationPracticeUnifyTel)) {
                if (!isPhone.test(graduationPracticeUnifyTel)) {
                    validErrorDom(validId.graduationPracticeUnifyTel, errorMsgId.graduationPracticeUnifyTel, '请填写正确的联系方式');
                } else {
                    validSuccessDom(validId.graduationPracticeUnifyTel, errorMsgId.graduationPracticeUnifyTel);
                }
            } else {
                validSuccessDom(validId.graduationPracticeUnifyTel, errorMsgId.graduationPracticeUnifyTel);
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
                message: "确定保存吗?",
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

        function validStudentName() {
            var studentName = param.studentName;
            if (studentName.length <= 0 || studentName.length > 15) {
                Messenger().post({
                    message: '姓名15个字符以内',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validQqMailbox();
            }
        }

        function validQqMailbox() {
            var qqMailbox = param.qqMailbox;
            if (qqMailbox.length <= 0 || qqMailbox.length > 100) {
                Messenger().post({
                    message: 'qq邮箱100个字符以内',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                if (D.endsWith(qqMailbox, '@qq.com')) {
                    validParentalContact();
                } else {
                    Messenger().post({
                        message: '请填写正确的qq邮箱',
                        type: 'error',
                        showCloseButton: true
                    });
                }
            }
        }

        function validParentalContact() {
            var parentalContact = param.parentalContact;
            var regex = /^1[0-9]{10}$/;
            var isPhone = /^([0-9]{3,4}-)?[0-9]{7,8}$/;
            if (!regex.test(parentalContact)) {
                if (!isPhone.test(parentalContact)) {
                    Messenger().post({
                        message: '请正确填写父母联系方式',
                        type: 'error',
                        showCloseButton: true
                    });
                } else {
                    validHeadmaster();
                }
            } else {
                validHeadmaster();
            }
        }

        function validHeadmaster() {
            var headmaster = param.headmaster;
            if (headmaster.length <= 0) {
                Messenger().post({
                    message: '请选择班主任',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validGraduationPracticeUnifyName();
            }
        }


        function validGraduationPracticeUnifyName() {
            var graduationPracticeUnifyName = param.graduationPracticeUnifyName;
            if (graduationPracticeUnifyName.length <= 0 || graduationPracticeUnifyName.length > 200) {
                Messenger().post({
                    message: '实习单位200个字符以内',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validGraduationPracticeCollegeAddress();
            }
        }

        function validGraduationPracticeCollegeAddress() {
            var graduationPracticeUnifyAddress = param.graduationPracticeUnifyAddress;
            if (graduationPracticeUnifyAddress.length <= 0 || graduationPracticeUnifyAddress.length > 500) {
                Messenger().post({
                    message: '实习单位地址500个字符以内',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validGraduationPracticeCollegeContacts();
            }
        }

        function validGraduationPracticeCollegeContacts() {
            var graduationPracticeUnifyContacts = param.graduationPracticeUnifyContacts;
            if (graduationPracticeUnifyContacts.length <= 0 || graduationPracticeUnifyContacts.length > 10) {
                Messenger().post({
                    message: '实习单位联系人10个字符以内',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validGraduationPracticeCollegeTel();
            }
        }

        function validGraduationPracticeCollegeTel() {
            var graduationPracticeUnifyTel = param.graduationPracticeUnifyTel;
            var regex = /^1[0-9]{10}$/;
            var isPhone = /^([0-9]{3,4}-)?[0-9]{7,8}$/;
            if (!regex.test(graduationPracticeUnifyTel)) {
                if (!isPhone.test(graduationPracticeUnifyTel)) {
                    Messenger().post({
                        message: '请正确填写实习单位联系人联系方式',
                        type: 'error',
                        showCloseButton: true
                    });
                } else {
                    sendAjax();
                }
            } else {
                sendAjax();
            }
        }

        /**
         * 发送数据到后台
         */
        function sendAjax() {
            $('#man').attr("disabled", false);
            $('#woman').attr("disabled", false);
            $(paramId.headmaster).attr("disabled", false);
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