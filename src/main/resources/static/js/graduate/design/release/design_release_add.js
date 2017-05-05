/**
 * Created by lenovo on 2017/5/5.
 */
//# sourceURL=graduate_design_release_add.js.js
require(["jquery", "handlebars", "nav_active", "moment", "bootstrap-daterangepicker", "messenger", "jquery.address",
        "bootstrap-datetimepicker-zh-CN", "jquery.fileupload-validate", "bootstrap-maxlength", "jquery.showLoading"],
    function ($, Handlebars, nav_active, moment) {

        /*
         ajax url.
         */
        var ajax_url = {
            school_data_url: '/user/schools',
            college_data_url: '/user/colleges',
            department_data_url: '/user/departments',
            grade_data_url: '/user/department/grades',
            science_data_url: '/user/grade/sciences',
            valid: '/web/graduate/design/release/save/valid',
            file_upload_url: '/anyone/users/upload/graduate/design',
            delete_file_url: '/anyone/users/delete/file',
            save: '/web/graduate/design/release/save',
            back: '/web/menu/graduate/design/release'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.back);

        /*
         参数id
         */
        var paramId = {
            graduationDesignTitle: '#graduationDesignTitle',
            startTime: '#startTime',
            endTime: '#endTime',
            fillTeacherTime: '#fillTeacherTime',
            schoolId: '#select_school',
            collegeId: '#select_college',
            departmentId: '#select_department',
            grade: '#select_grade',
            scienceId: '#select_science',
            graduationDesignIsDel: '#graduationDesignIsDel'
        };

        /*
         参数
         */
        var param = {
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
            fillTeacherTime: '#fillTeacherTime',
            schoolId: '#valid_school',
            collegeId: '#valid_college',
            departmentId: '#valid_department',
            grade: '#valid_grade',
            scienceId: '#valid_science'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            graduationDesignTitle: '#release_title_error_msg',
            startTime: '#start_time_error_msg',
            endTime: '#end_time_error_msg',
            fillTeacherTime: '#fill_teacher_time_error_msg',
            schoolId: '#school_error_msg',
            collegeId: '#college_error_msg',
            departmentId: '#department_error_msg',
            grade: '#grade_error_msg',
            scienceId: '#science_error_msg'
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
            if (init_page_param.collegeId != -1) {
                param.collegeId = init_page_param.collegeId;
                param.schoolId = 0;
            } else {
                param.collegeId = $(paramId.collegeId).val();
            }
            if (init_page_param.departmentId != -1) {
                param.departmentId = init_page_param.departmentId;
                param.schoolId = 0;
                param.collegeId = 0;
            } else {
                param.departmentId = $(paramId.departmentId).val();
            }
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
        $(paramId.startTime).datetimepicker({format: 'yyyy-mm-dd hh:ii:ss'});
        // 结束时间
        $(paramId.endTime).datetimepicker({format: 'yyyy-mm-dd hh:ii:ss'});

        // 学生申报教师时间
        $(paramId.fillTeacherTime).daterangepicker({
            "startDate": moment().add(1, "days"),
            "endDate": moment().add(2, "days"),
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
            if (init_page_param.departmentId != -1) {
                changeGrade(init_page_param.departmentId);
            } else if (init_page_param.collegeId != -1) {
                changeDepartment(init_page_param.collegeId);
            } else {
                startLoading();
                $.get(web_path + ajax_url.school_data_url, function (data) {
                    endLoading();
                    schoolData(data);
                });
            }

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

        /**
         * 学校数据展现
         * @param data json数据
         */
        function schoolData(data) {
            var template = Handlebars.compile($("#school-template").html());

            Handlebars.registerHelper('school_value', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.schoolId));
            });

            Handlebars.registerHelper('school_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.schoolName));
            });
            $(paramId.schoolId).html(template(data));
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

        // 当改变学校时，变换学院数据.
        $(paramId.schoolId).change(function () {
            initParam();
            var school = param.schoolId;
            changeCollege(school);// 根据学校重新加载院数据
            changeDepartment(0);// 清空系数据
            changeGrade(0);// 清空年级数据
            changeScience(0);// 清空专业数据

            // 改变选项时，检验
            if (Number(school) > 0) {
                validSuccessDom(validId.schoolId, errorMsgId.schoolId);
            } else {
                validErrorDom(validId.schoolId, errorMsgId.schoolId, '请选择学校');
            }

            validCleanDom(validId.collegeId, errorMsgId.collegeId);

            validCleanDom(validId.departmentId, errorMsgId.departmentId);

            validCleanDom(validId.grade, errorMsgId.grade);

            validCleanDom(validId.scienceId, errorMsgId.scienceId);
        });

        // 当改变学院时，变换系数据.
        $(paramId.collegeId).change(function () {
            initParam();
            var college = param.collegeId;
            changeDepartment(college);// 根据院重新加载系数据
            changeGrade(0);// 清空年级数据
            changeScience(0);// 清空专业数据

            if (Number(college) > 0) {
                validSuccessDom(validId.collegeId, errorMsgId.collegeId);
            } else {
                validErrorDom(validId.collegeId, errorMsgId.collegeId, '请选择院');
            }

            validCleanDom(validId.departmentId, errorMsgId.departmentId);

            validCleanDom(validId.grade, errorMsgId.grade);

            validCleanDom(validId.scienceId, errorMsgId.scienceId);
        });

        // 当改变系时，变换年级数据.
        $(paramId.departmentId).change(function () {
            initParam();
            var department = param.departmentId;
            changeGrade(department);// 根据系重新加载年级数据
            changeScience(0);// 清空专业数据

            if (Number(department) > 0) {
                validSuccessDom(validId.departmentId, errorMsgId.departmentId);
            } else {
                validErrorDom(validId.departmentId, errorMsgId.departmentId, '请选择系');
            }
            validCleanDom(validId.grade, errorMsgId.grade);

            validCleanDom(validId.scienceId, errorMsgId.scienceId);
        });

        // 当改变年级时，变换专业数据.
        $(paramId.grade).change(function () {
            initParam();
            var grade = param.grade;
            var department = param.departmentId;
            changeScience(grade, department);// 根据系重新加载专业数据

            if (Number(grade) > 0) {
                validSuccessDom(validId.grade, errorMsgId.grade);
            } else {
                validErrorDom(validId.grade, errorMsgId.grade, '请选择年级');
            }
            validCleanDom(validId.scienceId, errorMsgId.scienceId);
        });

        // 变换专业数据.
        $(paramId.scienceId).change(function () {
            initParam();
            var scienceId = param.scienceId;

            if (Number(scienceId) > 0) {
                validSuccessDom(validId.scienceId, errorMsgId.scienceId);
            } else {
                validErrorDom(validId.scienceId, errorMsgId.scienceId, '请选择专业');
            }
        });

        /**
         * 改变学院选项
         * @param school_id 学校id
         */
        function changeCollege(school_id) {
            if (Number(school_id) == 0) {
                var template = Handlebars.compile($("#college-template").html());

                var context = {
                    listResult: [
                        {name: "请选择院", value: ""}
                    ]
                };

                Handlebars.registerHelper('college_value', function () {
                    return new Handlebars.SafeString(Handlebars.escapeExpression(this.value));
                });

                Handlebars.registerHelper('college_name', function () {
                    return new Handlebars.SafeString(Handlebars.escapeExpression(this.name));
                });
                $(paramId.collegeId).html(template(context));
            } else {
                // 根据学校id查询院数据
                startLoading();
                $.post(web_path + ajax_url.college_data_url, {schoolId: school_id}, function (data) {
                    endLoading();
                    var template = Handlebars.compile($("#college-template").html());

                    Handlebars.registerHelper('college_value', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.collegeId));
                    });

                    Handlebars.registerHelper('college_name', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.collegeName));
                    });
                    $(paramId.collegeId).html(template(data));
                });
            }
        }

        /**
         * 改变系选项
         * @param college_id 院id
         */
        function changeDepartment(college_id) {

            if (Number(college_id) == 0) {
                var template = Handlebars.compile($("#department-template").html());

                var context = {
                    listResult: [
                        {name: "请选择系", value: ""}
                    ]
                };

                Handlebars.registerHelper('department_value', function () {
                    return new Handlebars.SafeString(Handlebars.escapeExpression(this.value));
                });

                Handlebars.registerHelper('department_name', function () {
                    return new Handlebars.SafeString(Handlebars.escapeExpression(this.name));
                });
                $(paramId.departmentId).html(template(context));
            } else {
                // 根据院id查询全部系
                startLoading();
                $.post(web_path + ajax_url.department_data_url, {collegeId: college_id}, function (data) {
                    endLoading();
                    var template = Handlebars.compile($("#department-template").html());

                    Handlebars.registerHelper('department_value', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.departmentId));
                    });

                    Handlebars.registerHelper('department_name', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.departmentName));
                    });
                    $(paramId.departmentId).html(template(data));
                });
            }
        }

        /**
         * 改变年级选项
         * @param department_id 系id
         */
        function changeGrade(department_id) {
            if (Number(department_id) == 0) {
                var template = Handlebars.compile($("#grade-template").html());

                var context = {
                    listResult: [
                        {name: "请选择年级", value: "0"}
                    ]
                };

                Handlebars.registerHelper('grade_value', function () {
                    return new Handlebars.SafeString(Handlebars.escapeExpression(this.value));
                });

                Handlebars.registerHelper('grade_name', function () {
                    return new Handlebars.SafeString(Handlebars.escapeExpression(this.name));
                });
                $(paramId.grade).html(template(context));
            } else {
                // 根据系id查询全部年级
                startLoading();
                $.post(web_path + ajax_url.grade_data_url, {departmentId: department_id}, function (data) {
                    endLoading();
                    var template = Handlebars.compile($("#grade-template").html());

                    Handlebars.registerHelper('grade_value', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.value));
                    });

                    Handlebars.registerHelper('grade_name', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.text));
                    });
                    $(paramId.grade).html(template(data));
                });
            }
        }

        /**
         * 改变专业选项
         * @param grade 年级
         * @param department 系
         */
        function changeScience(grade, department) {

            if (Number(grade) == 0) {
                var template = Handlebars.compile($("#science-template").html());

                var context = {
                    listResult: [
                        {name: "请选择专业", value: ""}
                    ]
                };

                Handlebars.registerHelper('science_value', function () {
                    return new Handlebars.SafeString(Handlebars.escapeExpression(this.value));
                });

                Handlebars.registerHelper('science_name', function () {
                    return new Handlebars.SafeString(Handlebars.escapeExpression(this.name));
                });

                $(paramId.scienceId).html(template(context));
            } else {
                // 根据年级查询全部专业
                startLoading();
                $.post(web_path + ajax_url.science_data_url, {grade: grade, departmentId: department}, function (data) {
                    endLoading();
                    var template = Handlebars.compile($("#science-template").html());

                    Handlebars.registerHelper('science_value', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.scienceId));
                    });

                    Handlebars.registerHelper('science_name', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.scienceName));
                    });
                    $(paramId.scienceId).html(template(data));
                });
            }

        }

        // 上传组件
        $('#fileupload').fileupload({
            url: web_path + ajax_url.file_upload_url,
            dataType: 'json',
            maxFileSize: 100000000,// 100MB
            formAcceptCharset: 'utf-8',
            submit: function (e, data) {
                initParam();
                if (init_page_param.departmentId == -1 && init_page_param.collegeId == -1 && Number(param.schoolId) <= 0) {
                    Messenger().post({
                        message: '请选择学校',
                        type: 'error',
                        showCloseButton: true
                    });
                    return false;
                }

                if (init_page_param.departmentId == -1 && init_page_param.collegeId == -1 && Number(param.collegeId) <= 0) {
                    Messenger().post({
                        message: '请选择院',
                        type: 'error',
                        showCloseButton: true
                    });
                    return false;
                }

                if (((init_page_param.departmentId == -1 && init_page_param.collegeId == -1) ||
                    (init_page_param.departmentId == -1 && init_page_param.collegeId != -1))
                    && Number(param.departmentId) <= 0) {
                    Messenger().post({
                        message: '请选择系',
                        type: 'error',
                        showCloseButton: true
                    });
                    return false;
                }

                if (((init_page_param.departmentId == -1 && init_page_param.collegeId == -1) ||
                    (init_page_param.departmentId == -1 && init_page_param.collegeId != -1) ||
                    (init_page_param.departmentId != -1 && init_page_param.collegeId == -1))
                    && param.grade === '0') {
                    Messenger().post({
                        message: '请选择年级',
                        type: 'error',
                        showCloseButton: true
                    });
                    return false;
                }

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

        /*
         删除附件
         */
        $('#fileShow').delegate('.clearfile', "click", function () {
            var path = $(this).attr('data-file-path');
            var obj = $(this);
            $.post(web_path + ajax_url.delete_file_url, {filePath: path}, function (data) {
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
            var releaseTitle = param.releaseTitle;
            var msg;
            msg = Messenger().post({
                message: "确定添加毕业设计 '" + releaseTitle + "'  吗?",
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
                            if (init_page_param.departmentId != -1) {
                                validGrade();
                            } else if (init_page_param.collegeId != -1) {
                                validDepartmentId();
                            } else {
                                validSchoolId();
                            }
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

        /**
         * 检验学校id
         */
        function validSchoolId() {
            var schoolId = param.schoolId;
            if (Number(schoolId) <= 0) {
                Messenger().post({
                    message: '请选择学校',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validCollegeId();
            }
        }

        /**
         * 检验院id
         */
        function validCollegeId() {
            var collegeId = param.collegeId;
            if (Number(collegeId) <= 0) {
                Messenger().post({
                    message: '请选择院',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validDepartmentId();
            }
        }

        /**
         * 检验系id
         */
        function validDepartmentId() {
            var departmentId = param.departmentId;
            if (Number(departmentId) <= 0) {
                Messenger().post({
                    message: '请选择系',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validGrade();
            }
        }

        /**
         * 检验年级
         */
        function validGrade() {
            var grade = param.grade;
            if (Number(grade) <= 0) {
                Messenger().post({
                    message: '请选择年级',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                validScienceId();
            }
        }

        /**
         * 检验专业id
         */
        function validScienceId() {
            var scienceId = param.scienceId;
            if (Number(scienceId) <= 0) {
                Messenger().post({
                    message: '请选择专业',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                sendAjax();
            }
        }

        function sendAjax() {
            Messenger().run({
                successMessage: '保存数据成功',
                errorMessage: '保存数据失败',
                progressMessage: '正在保存数据....'
            }, {
                url: web_path + ajax_url.save,
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