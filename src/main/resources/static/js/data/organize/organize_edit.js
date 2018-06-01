/**
 * Created by lenovo on 2016-09-25.
 */
require(["jquery", "handlebars", "constants", "nav_active", "lodash_plugin", "messenger", "jquery.address", "bootstrap-maxlength", "jquery.showLoading"],
    function ($, Handlebars, constants, nav_active, DP) {

        /*
         ajax url.
         */
        var ajax_url = {
            school_data_url: '/user/schools',
            college_data_url: '/user/colleges',
            department_data_url: '/user/departments',
            science_data_url: '/user/sciences',
            update: '/web/data/organize/update',
            valid: '/web/data/organize/update/valid',
            back: '/web/menu/data/organize'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.back);

        /*
         参数id
         */
        var paramId = {
            schoolId: '#select_school',
            schoolName: '#schoolName',
            collegeId: '#select_college',
            collegeName: '#collegeName',
            departmentId: '#select_department',
            departmentName: '#departmentName',
            scienceId: '#select_science',
            scienceName: '#scienceName',
            grade: '#select_grade',
            organizeId: '#organizeId',
            organizeName: '#organizeName'
        };

        /*
         参数
         */
        var param = {
            schoolId: $(paramId.schoolId).val(),
            schoolName: '',
            collegeId: $(paramId.collegeId).val(),
            collegeName: '',
            departmentId: $(paramId.departmentId).val(),
            departmentName: '',
            scienceId: $(paramId.scienceId).val(),
            scienceName: '',
            grade: $(paramId.grade).val(),
            organizeId: $(paramId.organizeId).val(),
            organizeName: $(paramId.organizeName).val(),
            organizeIsDel: DP.defaultUndefinedValue($('input[name="organizeIsDel"]:checked').val(), 0)
        };

        /*
         检验id
         */
        var validId = {
            schoolId: '#valid_school',
            collegeId: '#valid_college',
            departmentId: '#valid_department',
            scienceId: '#valid_science',
            grade: '#valid_grade',
            organizeName: '#valid_organize_name'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            schoolId: '#school_error_msg',
            collegeId: '#college_error_msg',
            departmentId: '#department_error_msg',
            scienceId: '#science_error_msg',
            grade: '#grade_error_msg',
            organizeName: '#organize_name_error_msg'
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
        var selectedScienceCount = true;
        var selectedGradeCount = true;

        /**
         * 初始化参数
         */
        function initParam() {
            param.schoolId = $(paramId.schoolId).val();
            param.schoolName = $(paramId.schoolId).find('option:selected').text();
            param.collegeId = $(paramId.collegeId).val();
            param.collegeName = $(paramId.collegeId).find('option:selected').text();
            if (init_page_param.currentUserRoleName === constants.global_role_name.system_role
                || init_page_param.currentUserRoleName === constants.global_role_name.admin_role) {
                param.departmentId = $(paramId.departmentId).val();
            } else {
                param.departmentId = init_page_param.departmentId;
            }
            param.departmentName = $(paramId.departmentId).find('option:selected').text();
            param.scienceId = $(paramId.scienceId).val();
            param.scienceName = $(paramId.scienceId).find('option:selected').text();
            param.grade = $(paramId.grade).val();
            param.organizeId = $(paramId.organizeId).val();
            param.organizeName = $(paramId.organizeName).val();
            param.organizeIsDel = DP.defaultUndefinedValue($('input[name="organizeIsDel"]:checked').val(), 0);
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
            } else {
                changeScience(init_page_param.departmentId);
            }

            initMaxLength();
        }

        /**
         * 初始化Input max length
         */
        function initMaxLength() {
            $(paramId.organizeName).maxlength({
                alwaysShow: true,
                threshold: 10,
                warningClass: "label label-success",
                limitReachedClass: "label label-danger"
            });
        }

        /*
         初始化年级
         */
        changeGrade();

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
                    changeScience($(departmentChildrens[i]).val());
                    break;
                }
            }
        }

        /**
         * 选中专业
         */
        function selectedScience() {
            var realScienceId = $('#scienceId').val();
            var scienceChildrens = $('#select_science').children();
            for (var i = 0; i < scienceChildrens.length; i++) {
                if ($(scienceChildrens[i]).val() === realScienceId) {
                    $(scienceChildrens[i]).prop('selected', true);
                    break;
                }
            }
        }

        /**
         * 选中年级
         */
        function selectedGrade() {
            var realGradeId = $('#grade').val();
            var gradeChildrens = $('#select_grade').children();
            for (var i = 0; i < gradeChildrens.length; i++) {
                if ($(gradeChildrens[i]).val() === realGradeId) {
                    $(gradeChildrens[i]).prop('selected', true);
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
            changeScience(0);// 清空专业数据

            // 改变选项时，检验
            if (Number(school) > 0) {
                validSuccessDom(validId.schoolId, errorMsgId.schoolId);
            } else {
                validErrorDom(validId.schoolId, errorMsgId.schoolId, '请选择学校');
            }

            validCleanDom(validId.collegeId, errorMsgId.collegeId);

            validCleanDom(validId.departmentId, errorMsgId.departmentId);

            validCleanDom(validId.scienceId, errorMsgId.scienceId);
        });

        // 当改变学院时，变换系数据.
        $(paramId.collegeId).change(function () {
            initParam();
            var college = param.collegeId;
            changeDepartment(college);// 根据院重新加载系数据
            changeScience(0);// 清空专业数据

            if (Number(college) > 0) {
                validSuccessDom(validId.collegeId, errorMsgId.collegeId);
            } else {
                validErrorDom(validId.collegeId, errorMsgId.collegeId, '请选择院');
            }

            validCleanDom(validId.departmentId, errorMsgId.departmentId);

            validCleanDom(validId.scienceId, errorMsgId.scienceId);
        });

        // 当改变系时，变换专业数据.
        $(paramId.departmentId).change(function () {
            initParam();
            var department = param.departmentId;
            changeScience(department);// 根据系重新加载专业数据

            if (Number(department) > 0) {
                validSuccessDom(validId.departmentId, errorMsgId.departmentId);
            } else {
                validErrorDom(validId.departmentId, errorMsgId.departmentId, '请选择系');
            }
            validCleanDom(validId.scienceId, errorMsgId.scienceId);
        });

        // 改变专业时
        $(paramId.scienceId).change(function () {
            initParam();
            var science = param.scienceId;

            if (Number(science) > 0) {
                validSuccessDom(validId.scienceId, errorMsgId.scienceId);
            } else {
                validErrorDom(validId.scienceId, errorMsgId.scienceId, '请选择专业');
            }
        });

        // 改变年级时
        $(paramId.grade).change(function () {
            initParam();
            var grade = param.grade;

            if (Number(grade) > 0) {
                validSuccessDom(validId.grade, errorMsgId.grade);
            } else {
                validErrorDom(validId.grade, errorMsgId.grade, '请选择年级');
            }
        });

        /**
         * 改变学院选项
         * @param school_id 学校id
         */
        function changeCollege(school_id) {
            if (Number(school_id) === 0) {
                var source = $("#college-template").html();
                var template = Handlebars.compile(source);

                var context = {
                    listResult: [
                        {name: "请选择院", value: ""}
                    ]
                };

                Handlebars.registerHelper('college_value', function () {
                    var value = Handlebars.escapeExpression(this.value);
                    return new Handlebars.SafeString(value);
                });

                Handlebars.registerHelper('college_name', function () {
                    var name = Handlebars.escapeExpression(this.name);
                    return new Handlebars.SafeString(name);
                });

                var html = template(context);
                $(paramId.collegeId).html(html);
            } else {
                // 根据学校id查询院数据
                startLoading();
                $.post(web_path + ajax_url.college_data_url, {schoolId: school_id}, function (data) {
                    endLoading();
                    var source = $("#college-template").html();
                    var template = Handlebars.compile(source);

                    Handlebars.registerHelper('college_value', function () {
                        var value = Handlebars.escapeExpression(this.collegeId);
                        return new Handlebars.SafeString(value);
                    });

                    Handlebars.registerHelper('college_name', function () {
                        var name = Handlebars.escapeExpression(this.collegeName);
                        return new Handlebars.SafeString(name);
                    });

                    var html = template(data);
                    $(paramId.collegeId).html(html);

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

            if (Number(college_id) === 0) {
                var source = $("#department-template").html();
                var template = Handlebars.compile(source);

                var context = {
                    listResult: [
                        {name: "请选择系", value: ""}
                    ]
                };

                Handlebars.registerHelper('department_value', function () {
                    var value = Handlebars.escapeExpression(this.value);
                    return new Handlebars.SafeString(value);
                });

                Handlebars.registerHelper('department_name', function () {
                    var name = Handlebars.escapeExpression(this.name);
                    return new Handlebars.SafeString(name);
                });

                var html = template(context);
                $(paramId.departmentId).html(html);
            } else {
                // 根据院id查询全部系
                startLoading();
                $.post(web_path + ajax_url.department_data_url, {collegeId: college_id}, function (data) {
                    endLoading();
                    var source = $("#department-template").html();
                    var template = Handlebars.compile(source);

                    Handlebars.registerHelper('department_value', function () {
                        var value = Handlebars.escapeExpression(this.departmentId);
                        return new Handlebars.SafeString(value);
                    });

                    Handlebars.registerHelper('department_name', function () {
                        var name = Handlebars.escapeExpression(this.departmentName);
                        return new Handlebars.SafeString(name);
                    });

                    var html = template(data);
                    $(paramId.departmentId).html(html);

                    if (selectedDepartmentCount) {
                        selectedDepartment();
                        selectedDepartmentCount = false;
                    }
                });
            }
        }

        /**
         * 改变专业选项
         * @param department_id 系id
         */
        function changeScience(department_id) {

            if (Number(department_id) === 0) {
                var source = $("#science-template").html();
                var template = Handlebars.compile(source);

                var context = {
                    listResult: [
                        {name: "请选择专业", value: ""}
                    ]
                };

                Handlebars.registerHelper('science_value', function () {
                    var value = Handlebars.escapeExpression(this.value);
                    return new Handlebars.SafeString(value);
                });

                Handlebars.registerHelper('science_name', function () {
                    var name = Handlebars.escapeExpression(this.name);
                    return new Handlebars.SafeString(name);
                });

                var html = template(context);
                $(paramId.scienceId).html(html);
            } else {
                // 根据系id查询全部专业
                startLoading();
                $.post(web_path + ajax_url.science_data_url, {departmentId: department_id}, function (data) {
                    endLoading();
                    var source = $("#science-template").html();
                    var template = Handlebars.compile(source);

                    Handlebars.registerHelper('science_value', function () {
                        var value = Handlebars.escapeExpression(this.scienceId);
                        return new Handlebars.SafeString(value);
                    });

                    Handlebars.registerHelper('science_name', function () {
                        var name = Handlebars.escapeExpression(this.scienceName);
                        return new Handlebars.SafeString(name);
                    });

                    var html = template(data);
                    $(paramId.scienceId).html(html);

                    if (selectedScienceCount) {
                        selectedScience();
                        selectedScienceCount = false;
                    }
                });
            }
        }

        /**
         * 年级对象
         * @param name
         * @param value
         */
        function gradeObj(name, value) {
            this.name = name;
            this.value = value;
        }

        /**
         * 改变年级选项
         */
        function changeGrade() {
            var template = Handlebars.compile($("#grade-template").html());

            var year = new Date().getFullYear();
            var beforeYear = year - 5;
            var afterYear = year + 3;

            var yearArr = [new gradeObj('请选择年级', '')];
            for (var i = beforeYear; i <= year; i++) {
                yearArr.push(new gradeObj(i + '级', i));
            }

            for (var j = year + 1; j <= afterYear; j++) {
                yearArr.push(new gradeObj(j + '级', j));
            }

            var context = {
                listResult: yearArr
            };

            Handlebars.registerHelper('grade_value', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.value));
            });

            Handlebars.registerHelper('grade_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.name));
            });

            $(paramId.grade).html(template(context));

            if (selectedGradeCount) {
                selectedGrade();
                selectedGradeCount = false;
            }

        }

        /*
         即时检验系名
         */
        $(paramId.organizeName).blur(function () {
            initParam();
            var organizeName = param.organizeName;
            if (organizeName.length <= 0 || organizeName.length > 200) {
                validErrorDom(validId.organizeName, errorMsgId.organizeName, '班级名200个字符以内');
            } else {
                // 班级名是否重复
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + ajax_url.valid,
                    type: 'post',
                    data: param,
                    success: function (data) {
                        if (data.state) {
                            validSuccessDom(validId.organizeName, errorMsgId.organizeName);
                        } else {
                            validErrorDom(validId.organizeName, errorMsgId.organizeName, data.msg);
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
            var organizeName = param.organizeName;
            var msg;
            msg = Messenger().post({
                message: "确定更改班级为 '" + organizeName + "'  吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            // 填充数据
                            $(paramId.schoolName).val($(paramId.schoolId).find('option:selected').text());
                            $(paramId.collegeName).val($(paramId.collegeId).find('option:selected').text());
                            $(paramId.departmentName).val($(paramId.departmentId).find('option:selected').text());
                            $(paramId.scienceName).val($(paramId.scienceId).find('option:selected').text());

                            if (init_page_param.currentUserRoleName === constants.global_role_name.system_role) {
                                validSchoolId();
                            } else if (init_page_param.currentUserRoleName === constants.global_role_name.admin_role) {
                                validDepartmentId();
                            } else {
                                validScienceId();
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
                validGrade();
            }
        }

        /**
         * 检验年级
         */
        function validGrade() {
            initParam();
            var grade = param.grade;
            if (Number(grade) > 0) {
                validOrganizeName();
            } else {
                Messenger().post({
                    message: '请选择年级',
                    type: 'error',
                    showCloseButton: true
                });
            }
        }

        /**
         * 添加时检验并提交数据
         */
        function validOrganizeName() {
            initParam();
            var organizeName = param.organizeName;
            if (organizeName.length <= 0 || organizeName.length > 200) {
                Messenger().post({
                    message: '班级名1~200个字符',
                    type: 'error',
                    showCloseButton: true
                });
            } else {
                // 班级名是否重复
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