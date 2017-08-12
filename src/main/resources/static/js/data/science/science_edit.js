/**
 * Created by lenovo on 2016-09-24.
 */
require(["jquery", "handlebars", "constants", "nav_active", "messenger", "jquery.address", "bootstrap-maxlength", "jquery.showLoading"],
    function ($, Handlebars, constants, nav_active) {
        /*
         ajax url.
         */
        var ajax_url = {
            school_data_url: '/user/schools',
            college_data_url: '/user/colleges',
            department_data_url: '/user/departments',
            update: '/web/data/science/update',
            valid: '/web/data/science/update/valid/name',
            valid_code: '/web/data/science/update/valid/code',
            back: '/web/menu/data/science'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.back);

        /*
         参数id
         */
        var paramId = {
            schoolId: '#select_school',
            collegeId: '#select_college',
            departmentId: '#select_department',
            scienceId: '#scienceId',
            scienceName: '#scienceName',
            scienceCode: '#scienceCode'
        };

        /*
         参数
         */
        var param = {
            schoolId: $(paramId.schoolId).val(),
            collegeId: $(paramId.collegeId).val(),
            departmentId: $(paramId.departmentId).val(),
            scienceId: $(paramId.scienceId).val(),
            scienceName: $(paramId.scienceName).val(),
            scienceCode: $(paramId.scienceCode).val()
        };

        /*
         检验id
         */
        var validId = {
            schoolId: '#valid_school',
            collegeId: '#valid_college',
            departmentId: '#valid_department',
            scienceName: '#valid_science_name',
            scienceCode: '#valid_science_code'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            schoolId: '#school_error_msg',
            collegeId: '#college_error_msg',
            departmentId: '#department_error_msg',
            scienceName: '#science_name_error_msg',
            scienceCode: '#science_code_error_msg'
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

        /*
         页面加载时初始化选中
         */
        var selectedSchoolCount = true;
        var selectedCollegeCount = true;
        var selectedDepartmentCount = true;

        /**
         * 初始化参数
         */
        function initParam() {
            param.schoolId = $(paramId.schoolId).val();
            param.collegeId = $(paramId.collegeId).val();
            param.departmentId = $(paramId.departmentId).val();
            param.scienceId = $(paramId.scienceId).val();
            param.scienceName = $(paramId.scienceName).val();
            param.scienceCode = $(paramId.scienceCode).val();
        }

        /*
         初始化页面
         */
        init();

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

            if (selectedSchoolCount) {
                selectedSchool();
                selectedSchoolCount = false;
            }
        }

        /**
         * 初始化数据
         */
        function init() {
            if (init_page_param.currentUserRoleName === constants.global_role_name.system_role) {
                startLoading();
                $.get(web_path + ajax_url.school_data_url, function (data) {
                    endLoading();
                    schoolData(data);
                });
            } else if (init_page_param.currentUserRoleName === constants.global_role_name.admin_role) {
                changeDepartment(init_page_param.collegeId);
            }

            initMaxLength();
        }

        /**
         * 初始化Input max length
         */
        function initMaxLength() {
            $(paramId.scienceName).maxlength({
                alwaysShow: true,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });

            $(paramId.scienceCode).maxlength({
                alwaysShow: true,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });
        }

        /**
         * 选中学校
         */
        function selectedSchool() {
            var realSchoolId = $('#schoolId').val();
            var schoolChildrens = $('#select_school').children();
            for (var i = 0; i < schoolChildrens.length; i++) {
                if ($(schoolChildrens[i]).val() === realSchoolId) {
                    $(schoolChildrens[i]).prop('selected', true);
                    changeCollege($(schoolChildrens[i]).val());
                    break;
                }
            }
        }

        /**
         * 选中院
         */
        function selectedCollege() {
            var realCollegeId = $('#collegeId').val();
            var collegeChildrens = $('#select_college').children();
            for (var i = 0; i < collegeChildrens.length; i++) {
                if ($(collegeChildrens[i]).val() === realCollegeId) {
                    $(collegeChildrens[i]).prop('selected', true);
                    changeDepartment($(collegeChildrens[i]).val());
                    break;
                }
            }
        }

        /**
         * 选中系
         */
        function selectedDepartment() {
            var realDepartmentId = $('#departmentId').val();
            var departmentChildrens = $('#select_department').children();
            for (var i = 0; i < departmentChildrens.length; i++) {
                if ($(departmentChildrens[i]).val() === realDepartmentId) {
                    $(departmentChildrens[i]).prop('selected', true);
                    break;
                }
            }
        }

        // 当改变学校时，变换学院数据.
        $(paramId.schoolId).change(function () {
            initParam();
            var school = param.schoolId;
            changeCollege(school);// 根据学校重新加载院数据
            changeDepartment(0);// 清空系数据

            // 改变选项时，检验
            if (Number(school) > 0) {
                validSuccessDom(validId.schoolId, errorMsgId.schoolId);
            } else {
                validErrorDom(validId.schoolId, errorMsgId.schoolId, '请选择学校');
            }

            validCleanDom(validId.collegeId, errorMsgId.collegeId);

            validCleanDom(validId.departmentId, errorMsgId.departmentId);
        });

        // 当改变学院时，变换系数据.
        $(paramId.collegeId).change(function () {
            initParam();
            var college = param.collegeId;
            changeDepartment(college);// 根据院重新加载系数据

            if (Number(college) > 0) {
                validSuccessDom(validId.collegeId, errorMsgId.collegeId);
            } else {
                validErrorDom(validId.collegeId, errorMsgId.collegeId, '请选择院');
            }

            validCleanDom(validId.departmentId, errorMsgId.departmentId);
        });

        // 当改变系时，变换专业数据.
        $(paramId.departmentId).change(function () {
            initParam();
            var department = param.departmentId;

            if (Number(department) > 0) {
                validSuccessDom(validId.departmentId, errorMsgId.departmentId);
            } else {
                validErrorDom(validId.departmentId, errorMsgId.departmentId, '请选择系');
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

                    if (selectedCollegeCount) {
                        selectedCollege();
                        selectedCollegeCount = false;
                    }
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

                    if (selectedDepartmentCount) {
                        selectedDepartment();
                        selectedDepartmentCount = false;
                    }
                });
            }
        }

        /*
         即时检验系名
         */
        $(paramId.scienceName).blur(function () {
            initParam();
            var scienceName = param.scienceName;
            if (scienceName.length <= 0 || scienceName.length > 200) {
                validErrorDom(validId.scienceName, errorMsgId.scienceName, '专业名200个字符以内');
            } else {
                // 专业名是否重复
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + ajax_url.valid,
                    type: 'post',
                    data: param,
                    success: function (data) {
                        if (data.state) {
                            validSuccessDom(validId.scienceName, errorMsgId.scienceName);
                        } else {
                            validErrorDom(validId.scienceName, errorMsgId.scienceName, data.msg);
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

        /*
         即时检验系代码
         */
        $(paramId.scienceCode).blur(function () {
            initParam();
            var scienceCode = param.scienceCode;
            if (scienceCode.length <= 0 || scienceCode.length > 20) {
                validErrorDom(validId.scienceCode, errorMsgId.scienceCode, '专业代码20个字符以内');
            } else {
                // 专业代码是否重复
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + ajax_url.valid_code,
                    type: 'post',
                    data: param,
                    success: function (data) {
                        if (data.state) {
                            validSuccessDom(validId.scienceCode, errorMsgId.scienceCode);
                        } else {
                            validErrorDom(validId.scienceCode, errorMsgId.scienceCode, data.msg);
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

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back);
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
            var scienceName = param.scienceName;
            var msg;
            msg = Messenger().post({
                message: "确定更改专业为 '" + scienceName + "'  吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            if (init_page_param.currentUserRoleName === constants.global_role_name.system_role) {
                                validSchoolId();
                            } else if (init_page_param.currentUserRoleName === constants.global_role_name.admin_role) {
                                validDepartmentId();
                            }
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
         * 检验学校id
         * @param msg
         */
        function validSchoolId() {
            initParam();
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
                validScienceName();
            }
        }

        /**
         * 添加时检验并提交数据
         */
        function validScienceName() {
            initParam();
            var scienceName = param.scienceName;
            if (scienceName.length <= 0 || scienceName.length > 200) {
                Messenger().post({
                    message: '专业名1~200个字符',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                // 专业名是否重复
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + ajax_url.valid,
                    type: 'post',
                    data: param,
                    success: function (data) {
                        if (data.state) {
                            validScienceCode();
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
         * 添加时检验并提交数据
         */
        function validScienceCode() {
            initParam();
            var scienceCode = param.scienceCode;
            if (scienceCode.length <= 0 || scienceCode.length > 20) {
                Messenger().post({
                    message: '专业名1~20个字符',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                // 专业代码是否重复
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + ajax_url.valid_code,
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