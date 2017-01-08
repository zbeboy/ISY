/**
 * Created by lenovo on 2016-12-01.
 */
//# sourceURL=graduation_practice_college_add.js
require(["jquery", "handlebars", "nav_active", "moment", "lodash", "messenger", "jquery.address",
        "bootstrap-select-zh-CN", "bootstrap-daterangepicker", "bootstrap-maxlength", "jquery.showLoading"],
    function ($, Handlebars, nav_active, moment, D) {
        /*
         ajax url.
         */
        var ajax_url = {
            save: '/web/internship/apply/graduation/college/save',
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
            graduationPracticeCollegeName: '#graduationPracticeCollegeName',
            graduationPracticeCollegeAddress: '#graduationPracticeCollegeAddress',
            graduationPracticeCollegeContacts: '#graduationPracticeCollegeContacts',
            graduationPracticeCollegeTel: '#graduationPracticeCollegeTel',
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
            graduationPracticeCollegeName: $(paramId.graduationPracticeCollegeName).val(),
            graduationPracticeCollegeAddress: $(paramId.graduationPracticeCollegeAddress).val(),
            graduationPracticeCollegeContacts: $(paramId.graduationPracticeCollegeContacts).val(),
            graduationPracticeCollegeTel: $(paramId.graduationPracticeCollegeTel).val(),
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
            graduationPracticeCollegeName: '#valid_graduation_practice_college_name',
            graduationPracticeCollegeAddress: '#valid_graduation_practice_college_address',
            graduationPracticeCollegeContacts: '#valid_graduation_practice_college_contacts',
            graduationPracticeCollegeTel: '#valid_graduation_practice_college_tel',
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
            graduationPracticeCollegeName: '#graduation_practice_college_name_error_msg',
            graduationPracticeCollegeAddress: '#graduation_practice_college_address_error_msg',
            graduationPracticeCollegeContacts: '#graduation_practice_college_contacts_error_msg',
            graduationPracticeCollegeTel: '#graduation_practice_college_tel_error_msg',
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
            param.graduationPracticeCollegeName = $(paramId.graduationPracticeCollegeName).val();
            param.graduationPracticeCollegeAddress = $(paramId.graduationPracticeCollegeAddress).val();
            param.graduationPracticeCollegeContacts = $(paramId.graduationPracticeCollegeContacts).val();
            param.graduationPracticeCollegeTel = $(paramId.graduationPracticeCollegeTel).val();
            param.startTime = $(paramId.startTime).val();
            param.endTime = $(paramId.endTime).val();
        }

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

        // 实习开始时间
        $(paramId.startTime).daterangepicker({
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

        // 实习结束时间
        $(paramId.endTime).daterangepicker({
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

            initMaxLength();
        }

        /**
         * 初始化Input max length
         */
        function initMaxLength() {
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

            $(paramId.graduationPracticeCollegeName).maxlength({
                alwaysShow: true,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });

            $(paramId.graduationPracticeCollegeAddress).maxlength({
                alwaysShow: true,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });

            $(paramId.graduationPracticeCollegeContacts).maxlength({
                alwaysShow: true,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });
        }

        /**
         * 班主任数据 展现
         * @param data json数据
         */
        function headmasterData(data) {
            var source = $("#teacher-template").html();
            var template = Handlebars.compile(source);

            Handlebars.registerHelper('teacher_value', function () {
                var value = Handlebars.escapeExpression(this.realName + ' ' + this.mobile);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('teacher_name', function () {
                var name = Handlebars.escapeExpression(this.realName + ' ' + this.mobile);
                return new Handlebars.SafeString(name);
            });

            var html = template(data);
            $(paramId.headmaster).html(html);
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
            var source = $("#file-template").html();
            var template = Handlebars.compile(source);

            Handlebars.registerHelper('original_file_name', function () {
                var value = Handlebars.escapeExpression(this.originalFileName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('size', function () {
                var value = Handlebars.escapeExpression(transformationFileUnit(this.size));
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('lastPath', function () {
                var value = Handlebars.escapeExpression(this.relativePath);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('new_name', function () {
                var value = Handlebars.escapeExpression(this.newName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('ext', function () {
                var value = Handlebars.escapeExpression(this.ext);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('l_size', function () {
                var value = Handlebars.escapeExpression(this.size);
                return new Handlebars.SafeString(value);
            });

            var html = template(data);
            $('#fileShow').append(html);
        }

        /*
         下载附件
         */
        $('#fileShow').delegate('.downloadfile', "click", function () {
            var id = $(this).attr('data-file-id');
            window.location.href = web_path + ajax_url.download_file + '?fileId=' + id;
        });

        /**
         * 转换文件单位
         *
         * @param size 文件大小
         * @return 文件尺寸
         */
        function transformationFileUnit(size) {
            var str = "";
            if (size < 1024) {
                str = size + "B";
            } else if (size >= 1024 && size < 1024 * 1024) {
                str = (size / 1024) + "KB";
            } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
                str = (size / (1024 * 1024)) + "MB";
            } else {
                str = (size / (1024 * 1024 * 1024)) + "GB";
            }

            return str;
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
                if(!isPhone.test(parentalContact)){
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

        $(paramId.graduationPracticeCollegeName).blur(function () {
            initParam();
            var graduationPracticeCollegeName = param.graduationPracticeCollegeName;
            if (graduationPracticeCollegeName.length <= 0 || graduationPracticeCollegeName.length > 200) {
                validErrorDom(validId.graduationPracticeCollegeName, errorMsgId.graduationPracticeCollegeName, '实习单位200个字符以内');
            } else {
                validSuccessDom(validId.graduationPracticeCollegeName, errorMsgId.graduationPracticeCollegeName);
            }
        });

        $(paramId.graduationPracticeCollegeAddress).blur(function () {
            initParam();
            var graduationPracticeCollegeAddress = param.graduationPracticeCollegeAddress;
            if (graduationPracticeCollegeAddress.length <= 0 || graduationPracticeCollegeAddress.length > 500) {
                validErrorDom(validId.graduationPracticeCollegeAddress, errorMsgId.graduationPracticeCollegeAddress, '实习单位地址500个字符以内');
            } else {
                validSuccessDom(validId.graduationPracticeCollegeAddress, errorMsgId.graduationPracticeCollegeAddress);
            }
        });

        $(paramId.graduationPracticeCollegeContacts).blur(function () {
            initParam();
            var graduationPracticeCollegeContacts = param.graduationPracticeCollegeContacts;
            if (graduationPracticeCollegeContacts.length <= 0 || graduationPracticeCollegeContacts.length > 10) {
                validErrorDom(validId.graduationPracticeCollegeContacts, errorMsgId.graduationPracticeCollegeContacts, '实习单位联系人10个字符以内');
            } else {
                validSuccessDom(validId.graduationPracticeCollegeContacts, errorMsgId.graduationPracticeCollegeContacts);
            }
        });

        $(paramId.graduationPracticeCollegeTel).blur(function () {
            initParam();
            var graduationPracticeCollegeTel = param.graduationPracticeCollegeTel;
            var regex = /^1[0-9]{10}$/;
            var isPhone = /^([0-9]{3,4}-)?[0-9]{7,8}$/;
            if (!regex.test(graduationPracticeCollegeTel)) {
                if(!isPhone.test(graduationPracticeCollegeTel)){
                    validErrorDom(validId.graduationPracticeCollegeTel, errorMsgId.graduationPracticeCollegeTel, '请填写正确的联系方式');
                } else {
                    validSuccessDom(validId.graduationPracticeCollegeTel, errorMsgId.graduationPracticeCollegeTel);
                }
            } else {
                validSuccessDom(validId.graduationPracticeCollegeTel, errorMsgId.graduationPracticeCollegeTel);
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
                if(!isPhone.test(parentalContact)){
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
                validGraduationPracticeCollegeName();
            }
        }

        function validGraduationPracticeCollegeName() {
            var graduationPracticeCollegeName = param.graduationPracticeCollegeName;
            if (graduationPracticeCollegeName.length <= 0 || graduationPracticeCollegeName.length > 200) {
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
            var graduationPracticeCollegeAddress = param.graduationPracticeCollegeAddress;
            if (graduationPracticeCollegeAddress.length <= 0 || graduationPracticeCollegeAddress.length > 500) {
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
            var graduationPracticeCollegeContacts = param.graduationPracticeCollegeContacts;
            if (graduationPracticeCollegeContacts.length <= 0 || graduationPracticeCollegeContacts.length > 10) {
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
            var graduationPracticeCollegeTel = param.graduationPracticeCollegeTel;
            var regex = /^1[0-9]{10}$/;
            var isPhone = /^([0-9]{3,4}-)?[0-9]{7,8}$/;
            if (!regex.test(graduationPracticeCollegeTel)) {
                if(!isPhone.test(graduationPracticeCollegeTel)){
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