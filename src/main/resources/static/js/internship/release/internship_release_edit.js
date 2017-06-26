/**
 * Created by lenovo on 2016/11/18.
 */
//# sourceURL=internship_release_edit.js
require(["jquery", "handlebars", "nav_active", "moment", "files", "bootstrap-daterangepicker", "messenger", "jquery.address",
        "jquery.fileupload-validate", "bootstrap-maxlength", "jquery.showLoading"],
    function ($, Handlebars, nav_active, moment, files) {

        /*
         ajax url.
         */
        var ajax_url = {
            valid: '/web/internship/release/update/valid',
            file_upload_url: '/anyone/users/upload/internship',
            delete_file_url: '/anyone/users/delete/file/internship',
            internship_files_url: '/user/internship/files',
            update: '/web/internship/release/update',
            back: '/web/menu/internship/release'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.back);

        /*
         参数id
         */
        var paramId = {
            internshipReleaseId: '#internshipReleaseId',
            schoolId: '#schoolId',
            collegeId: '#collegeId',
            departmentId: '#departmentId',
            releaseTitle: '#releaseTitle',
            teacherDistributionTime: '#teacherDistributionTime',
            time: '#time',
            internshipReleaseIsDel: '#internshipReleaseIsDel'
        };

        /*
         参数
         */
        var param = {
            schoolId: $(paramId.schoolId).val(),
            collegeId: $(paramId.collegeId).val(),
            departmentId: $(paramId.departmentId).val(),
            internshipReleaseId: $(paramId.internshipReleaseId).val(),
            releaseTitle: $(paramId.releaseTitle).val().trim(),
            teacherDistributionTime: $(paramId.teacherDistributionTime).val(),
            time: $(paramId.time).val(),
            internshipReleaseIsDel: $('input[name="internshipReleaseIsDel"]:checked').val(),
            files: ''
        };

        /*
         检验id
         */
        var validId = {
            releaseTitle: '#valid_release_title',
            teacherDistributionTime: '#valid_teacher_distribution_time',
            time: '#valid_time'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            releaseTitle: '#release_title_error_msg',
            teacherDistributionTime: '#teacher_distribution_time_error_msg',
            time: '#time_error_msg'
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
            startLoading();
            $.get(web_path + ajax_url.internship_files_url, {internshipReleaseId: param.internshipReleaseId}, function (data) {
                endLoading();
                initFileShow(data);
            });

            initMaxLength();
        }

        /**
         * 初始化参数
         */
        function initParam() {
            param.internshipReleaseId = $(paramId.internshipReleaseId).val();
            param.schoolId = $(paramId.schoolId).val();
            param.collegeId = $(paramId.collegeId).val();
            param.departmentId = $(paramId.departmentId).val();
            param.releaseTitle = $(paramId.releaseTitle).val().trim();
            param.teacherDistributionTime = $(paramId.teacherDistributionTime).val();
            param.time = $(paramId.time).val();
            param.internshipReleaseIsDel = $('input[name="internshipReleaseIsDel"]:checked').val();
            if (typeof(param.internshipReleaseIsDel) == "undefined") {
                param.internshipReleaseIsDel = 0;
            }
            var f = $('.fileobj');
            var p = [];
            for (var i = 0; i < f.length; i++) {
                p.push(new fileObj($(f[i]).attr('data-original-file-name'),
                    $(f[i]).attr('data-new-name'),
                    $(f[i]).attr('data-file-path'),
                    $(f[i]).attr('data-ext'),
                    $(f[i]).attr('data-size')
                ));
            }
            if (p.length > 0) {
                param.files = JSON.stringify(p);
            } else {
                param.files = '';
            }
        }

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back);
        });

        // 教师分配时间
        $(paramId.teacherDistributionTime).daterangepicker({
            "startDate": $('#teacherDistributionStartTime').val(),
            "endDate": $('#teacherDistributionEndTime').val(),
            "timePicker": true,
            "timePicker24Hour": true,
            "timePickerIncrement": 30,
            "locale": {
                format: 'YYYY-MM-DD HH:mm:ss',
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
            console.log('New date range selected: ' + start.format('YYYY-MM-DD HH:mm:ss') + ' to ' + end.format('YYYY-MM-DD HH:mm:ss') + ' (predefined range: ' + label + ')');
        });

        // 实习时间
        $(paramId.time).daterangepicker({
            "startDate": $('#startTime').val(),
            "endDate": $('#endTime').val(),
            "timePicker": true,
            "timePicker24Hour": true,
            "timePickerIncrement": 30,
            "locale": {
                format: 'YYYY-MM-DD HH:mm:ss',
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
            console.log('New date range selected: ' + start.format('YYYY-MM-DD HH:mm:ss') + ' to ' + end.format('YYYY-MM-DD HH:mm:ss') + ' (predefined range: ' + label + ')');
        });

        /**
         * 初始化Input max length
         */
        function initMaxLength() {
            $(paramId.releaseTitle).maxlength({
                alwaysShow: true,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });
        }

        // 检验实习标题
        $(paramId.releaseTitle).blur(function () {
            initParam();
            var releaseTitle = param.releaseTitle;
            if (releaseTitle.length <= 0 || releaseTitle.length > 100) {
                validErrorDom(validId.releaseTitle, errorMsgId.releaseTitle, '标题100个字符以内');
            } else {
                // 标题是否重复
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + ajax_url.valid,
                    type: 'post',
                    data: param,
                    success: function (data) {
                        if (data.state) {
                            validSuccessDom(validId.releaseTitle, errorMsgId.releaseTitle);
                        } else {
                            validErrorDom(validId.releaseTitle, errorMsgId.releaseTitle, data.msg);
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

        // 上传组件
        $('#fileupload').fileupload({
            url: web_path + ajax_url.file_upload_url,
            dataType: 'json',
            maxFileSize: 100000000,// 100MB
            formAcceptCharset: 'utf-8',
            messages: {
                maxFileSize: '单文件上传仅允许100MB大小'
            },
            submit: function (e, data) {
                initParam();
                data.formData = param;
            },
            done: function (e, data) {
                initParam();
                if (data.result.state) {
                    fileShow(data.result);
                } else {
                    Messenger().post({
                        message: data.result.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            },
            progressall: function (e, data) {
                var progress = parseInt(data.loaded / data.total * 100, 10);
                $('#progress').find('.progress-bar').css(
                    'width',
                    progress + '%'
                );
            }
        }).on('fileuploadadd', function(evt, data) {
            var isOk = true;
            var $this = $(this);
            var validation = data.process(function () {
                return $this.fileupload('process', data);
            });
            validation.fail(function(data) {
                isOk = false;
                Messenger().post({
                    message: '上传失败: ' + data.files[0].error,
                    type: 'error',
                    showCloseButton: true
                });
            });
            return isOk;
        });

        /**
         * 文件对象
         * @param originalFileName 原名
         * @param newName 新名
         * @param relativePath 相对路径
         * @param ext 后缀
         * @param size 尺寸
         */
        function fileObj(originalFileName, newName, relativePath, ext, size) {
            this.originalFileName = originalFileName;
            this.newName = newName;
            this.relativePath = relativePath;
            this.ext = ext;
            this.size = size;
        }

        /**
         * 文件显示
         * @param data 数据
         */
        function fileShow(data) {
            var template = Handlebars.compile($("#file-template").html());

            Handlebars.registerHelper('original_file_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.originalFileName));
            });

            Handlebars.registerHelper('size', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(files(this.size)));
            });

            Handlebars.registerHelper('lastPath', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(data.objectResult + this.newName));
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
         删除附件
         */
        $('#fileShow').delegate('.clearfile', "click", function () {
            initParam();
            var path = $(this).attr('data-file-path');
            var originalName = $(this).attr('data-original-file-name');
            var fileId = $(this).attr('data-file-id');
            var internshipReleaseId = param.internshipReleaseId;
            var obj = $(this);
            var msg;
            msg = Messenger().post({
                message: "确定删除附件 '" + originalName + "'  吗? 不可恢复!",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            $.post(web_path + ajax_url.delete_file_url, {
                                filePath: path,
                                fileId: fileId,
                                internshipReleaseId: internshipReleaseId
                            }, function (data) {
                                if (data.state) {
                                    Messenger().post({
                                        message: data.msg,
                                        type: 'success',
                                        showCloseButton: true
                                    });
                                    obj.parent().parent().remove();
                                } else {
                                    Messenger().post({
                                        message: data.msg,
                                        type: 'error',
                                        showCloseButton: true
                                    });
                                }
                            });
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
        });

        $('#save').click(function () {
            add();
        });

        /*
         添加询问
         */
        function add() {
            initParam();
            var releaseTitle = param.releaseTitle;
            var msg;
            msg = Messenger().post({
                message: "确定更新实习 '" + releaseTitle + "'  吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            validReleaseTitle();
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
         * 检验实习标题
         */
        function validReleaseTitle() {
            initParam();
            var releaseTitle = param.releaseTitle;
            if (releaseTitle.length <= 0 || releaseTitle.length > 100) {
                Messenger().post({
                    message: '标题为1~100个字符',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                // 标题是否重复
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + ajax_url.valid,
                    type: 'post',
                    data: param,
                    success: function (data) {
                        if (data.state) {
                            sendAjax();
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
        }

        function sendAjax() {
            Messenger().run({
                successMessage: '保存数据成功',
                errorMessage: '保存数据失败',
                progressMessage: '正在保存数据....'
            }, {
                url: web_path + ajax_url.update,
                type: 'post',
                data: param,
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