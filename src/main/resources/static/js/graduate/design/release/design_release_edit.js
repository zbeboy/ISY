/**
 * Created by lenovo on 2017-05-06.
 */
//# sourceURL=graduate_design_release_edit.js.js
require(["jquery", "handlebars", "nav_active", "moment", "bootstrap-daterangepicker", "messenger", "jquery.address",
        "bootstrap-datetimepicker-zh-CN", "jquery.fileupload-validate", "bootstrap-maxlength", "jquery.showLoading"],
    function ($, Handlebars, nav_active, moment) {

        /*
         ajax url.
         */
        var ajax_url = {
            valid: '/web/graduate/design/release/update/valid',
            file_upload_url: '/anyone/users/upload/graduate/design',
            delete_file_url: '/anyone/users/delete/file/graduate/design',
            graduate_design_release_files_url: '/user/graduate/design/files',
            update: '/web/graduate/design/release/update',
            back: '/web/menu/graduate/design/release'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.back);

        /*
         参数id
         */
        var paramId = {
            graduationDesignReleaseId: '#graduationDesignReleaseId',
            graduationDesignTitle: '#graduationDesignTitle',
            startTime: '#startTime',
            endTime: '#endTime',
            fillTeacherTime: '#fillTeacherTime',
            schoolId: '#schoolId',
            collegeId: '#collegeId',
            departmentId: '#departmentId',
            grade: '#grade',
            scienceId: '#scienceId',
            graduationDesignIsDel: '#graduationDesignIsDel'
        };

        /*
         参数
         */
        var param = {
            graduationDesignReleaseId: $(paramId.graduationDesignReleaseId).val(),
            graduationDesignTitle: $(paramId.graduationDesignTitle).val(),
            startTime: $(paramId.startTime).val(),
            endTime: $(paramId.endTime).val(),
            fillTeacherTime: $(paramId.fillTeacherTime).val(),
            schoolId: $(paramId.schoolId).val(),
            collegeId: $(paramId.collegeId).val(),
            departmentId: $(paramId.departmentId).val(),
            grade: $(paramId.grade).val(),
            scienceId: $(paramId.scienceId).val(),
            graduationDesignIsDel: $('input[name="graduationDesignIsDel"]:checked').val(),
            files: ''
        };

        /*
         检验id
         */
        var validId = {
            graduationDesignTitle: '#valid_release_title',
            startTime: '#valid_start_time',
            endTime: '#valid_end_time',
            fillTeacherTime: '#fillTeacherTime'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            graduationDesignTitle: '#release_title_error_msg',
            startTime: '#start_time_error_msg',
            endTime: '#end_time_error_msg',
            fillTeacherTime: '#fill_teacher_time_error_msg'
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

        /**
         * 初始化参数
         */
        function initParam() {
            param.graduationDesignTitle = $(paramId.graduationDesignTitle).val();
            param.startTime = $(paramId.startTime).val();
            param.endTime = $(paramId.endTime).val();
            param.fillTeacherTime = $(paramId.fillTeacherTime).val();
            param.schoolId = $(paramId.schoolId).val();
            param.collegeId = $(paramId.collegeId).val();
            param.departmentId = $(paramId.departmentId).val();
            param.grade = $(paramId.grade).val().trim();
            param.scienceId = $(paramId.scienceId).val();
            param.graduationDesignIsDel = $('input[name="graduationDesignIsDel"]:checked').val();
            if (typeof(param.graduationDesignIsDel) == "undefined") {
                param.graduationDesignIsDel = 0;
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

        // 开始时间
        $(paramId.startTime).datetimepicker({format: 'yyyy-mm-dd hh:ii:ss', language: 'zh-CN'});
        // 结束时间
        $(paramId.endTime).datetimepicker({format: 'yyyy-mm-dd hh:ii:ss', language: 'zh-CN'});

        // 学生申报教师时间
        $(paramId.fillTeacherTime).daterangepicker({
            "startDate": $('#fillTeacherStartTime').val(),
            "endDate": $('#fillTeacherEndTime').val(),
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

        init();

        function init() {
            initParam();
            startLoading();
            $.get(web_path + ajax_url.graduate_design_release_files_url,
                {graduationDesignReleaseId: param.graduationDesignReleaseId}, function (data) {
                endLoading();
                initFileShow(data);
            });

            initMaxLength();
        }

        /**
         * 初始化Input max length
         */
        function initMaxLength() {
            $(paramId.graduationDesignTitle).maxlength({
                alwaysShow: true,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });
        }

        // 检验毕业设计发布标题
        $(paramId.graduationDesignTitle).blur(function () {
            initParam();
            var graduationDesignTitle = param.graduationDesignTitle;
            if (graduationDesignTitle.length <= 0 || graduationDesignTitle.length > 100) {
                validErrorDom(validId.graduationDesignTitle, errorMsgId.graduationDesignTitle, '标题100个字符以内');
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
                            validSuccessDom(validId.graduationDesignTitle, errorMsgId.graduationDesignTitle);
                        } else {
                            validErrorDom(validId.graduationDesignTitle, errorMsgId.graduationDesignTitle, data.msg);
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
                return new Handlebars.SafeString(Handlebars.escapeExpression(transformationFileUnit(this.size)));
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
                return new Handlebars.SafeString(Handlebars.escapeExpression(transformationFileUnit(this.size)));
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
            var graduationDesignReleaseId = param.graduationDesignReleaseId;
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
                                graduationDesignReleaseId: graduationDesignReleaseId
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

        $('#save').click(function () {
            add();
        });

        /*
         添加询问
         */
        function add() {
            initParam();
            var graduationDesignTitle = param.graduationDesignTitle;
            var msg;
            msg = Messenger().post({
                message: "确定更新毕业设计 '" + graduationDesignTitle + "'  吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            validGraduationDesignTitle();
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
         * 检验毕业设计标题
         */
        function validGraduationDesignTitle() {
            var graduationDesignTitle = param.graduationDesignTitle;
            if (graduationDesignTitle.length <= 0 || graduationDesignTitle.length > 100) {
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