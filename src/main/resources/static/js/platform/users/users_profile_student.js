/**
 * Created by lenovo on 2016-10-23.
 */
//# sourceURL=users_profile_student.js
require(["jquery", "handlebars", "jquery.showLoading", "messenger", "bootstrap","jquery.address"],
    function ($, Handlebars, loading, messenger, bootstrap,jqueryAddress) {
        /*
         ajax url
         */
        var ajax_url = {
            department_data_url: '/user/departments',
            science_data_url: '/user/sciences',
            grade_data_url: '/user/grades',
            organize_data_url: '/user/organizes',
            school_update: '/anyone/users/profile/school/update',
            profile_edit: '/anyone/users/profile/edit',
            finish: '/anyone/users/profile'
        };

        function startLoading() {
            // 显示遮罩
            $('#loading_region').showLoading();
        }

        function endLoading() {
            // 去除遮罩
            $('#loading_region').hideLoading();
        }

        /*
        用户信息编辑
         */
        $('#profileEdit').click(function(){
            $.address.value(ajax_url.profile_edit);
        });

        /*
         错误验证
         */
        function validErrorDom(inputId, errorId, msg) {
            $(inputId).removeClass('has-success').addClass('has-error');
            $(errorId).removeClass('hidden').text(msg);
        }

        /*
         正确验证
         */
        function validSuccessDom(inputId, errorId) {
            $(inputId).removeClass('has-error').addClass('has-success');
            $(errorId).addClass('hidden').text('');
        }

        /*
         清除验证
         */
        function validCleanDom(inputId, errorId) {
            $(inputId).removeClass('has-error').removeClass('has-success');
            $(errorId).addClass('hidden').text('');
        }

        /*
         参数id
         */
        var paramId = {
            select_department: '#select_department',
            select_science: '#select_science',
            select_grade: '#select_grade',
            select_organize: '#select_organize'
        };

        /*
         参数
         */
        var param = {
            department: $(paramId.select_department).val().trim(),
            science: $(paramId.select_science).val().trim(),
            grade: $(paramId.select_grade).val().trim(),
            organize: $(paramId.select_organize).val().trim()
        };

        /*
         初始化参数
         */
        function initParam() {
            param.department = $(paramId.select_department).val().trim();
            param.science = $(paramId.select_science).val().trim();
            param.grade = $(paramId.select_grade).val().trim();
            param.organize = $(paramId.select_organize).val().trim();
        }

        /*
         验证form id
         */
        var validId = {
            valid_department: '#valid_department',
            valid_science: '#valid_science',
            valid_grade: '#valid_grade',
            valid_organize: '#valid_organize'
        };

        /*
         错误消息 id
         */
        var errorMsgId = {
            department_error_msg: '#department_error_msg',
            science_error_msg: '#science_error_msg',
            grade_error_msg: '#grade_error_msg',
            organize_error_msg: '#organize_error_msg'
        };

        /*
         页面加载时初始化选中
         */
        var selectedDepartmentCount = true;
        var selectedScienceCount = true;
        var selectedGradeCount = true;
        var selectedOrganizeCount = true;

        // 当改变系时，变换专业数据.
        $(paramId.select_department).change(function () {
            initParam();
            var department = param.department;
            changeScience(department);// 根据系重新加载专业数据
            changeGrade(0);// 清空年级数据
            changeOrganize(0);// 清空班级数据

            if (Number(department) > 0) {
                validSuccessDom(validId.valid_department, errorMsgId.department_error_msg);
            } else {
                validErrorDom(validId.valid_department, errorMsgId.department_error_msg, '请选择系');
            }

            validCleanDom(validId.valid_science, errorMsgId.science_error_msg);

            validCleanDom(validId.valid_grade, errorMsgId.grade_error_msg);

            validCleanDom(validId.valid_organize, errorMsgId.organize_error_msg);
        });

        // 当改变专业时，变换年级数据.
        $(paramId.select_science).change(function () {
            initParam();
            var science = param.science;
            changeGrade(science);// 根据专业重新加载年级
            changeOrganize(0);// 清空班级数据

            if (Number(science) > 0) {
                validSuccessDom(validId.valid_science, errorMsgId.science_error_msg);
            } else {
                validErrorDom(validId.valid_science, errorMsgId.science_error_msg, '请选择专业');
            }

            validCleanDom(validId.valid_grade, errorMsgId.grade_error_msg);

            validCleanDom(validId.valid_organize, errorMsgId.organize_error_msg);
        });

        // 当改变年级时，变换班级数据.
        $(paramId.select_grade).change(function () {
            initParam();
            var grade = param.grade;
            changeOrganize(grade);// 根据年级重新加载班级数据

            if (Number(grade) > 0) {
                validSuccessDom(validId.valid_grade, errorMsgId.grade_error_msg);
            } else {
                validErrorDom(validId.valid_grade, errorMsgId.grade_error_msg, '请选择年级');
            }

            validCleanDom(validId.valid_organize, errorMsgId.organize_error_msg);
        });

        // 当改变班级时
        $(paramId.select_organize).change(function () {
            initParam();
            var organize = param.organize;
            if (Number(organize) > 0) {
                validSuccessDom(validId.valid_organize, errorMsgId.organize_error_msg);
            } else {
                validErrorDom(validId.valid_organize, errorMsgId.organize_error_msg, '请选择班级');
            }
        });

        /**
         * 选中系
         */
        function selectedDepartment() {
            var realDepartmentId = init_page_param.departmentId;
            var departmentChildrens = $('#select_department').children();
            for (var i = 0; i < departmentChildrens.length; i++) {
                if (Number($(departmentChildrens[i]).val()) == realDepartmentId) {
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
            var realScienceId = init_page_param.scienceId;
            var scienceChildrens = $('#select_science').children();
            for (var i = 0; i < scienceChildrens.length; i++) {
                if (Number($(scienceChildrens[i]).val()) == realScienceId) {
                    $(scienceChildrens[i]).prop('selected', true);
                    changeGrade($(scienceChildrens[i]).val());
                    break;
                }
            }
        }

        /**
         * 选中年级
         */
        function selectedGrade() {
            var realGrade = init_page_param.grade;
            var gradeChildrens = $('#select_grade').children();
            for (var i = 0; i < gradeChildrens.length; i++) {
                if ($(gradeChildrens[i]).val() === realGrade) {
                    $(gradeChildrens[i]).prop('selected', true);
                    changeOrganize($(gradeChildrens[i]).val());
                    break;
                }
            }
        }

        /**
         * 选中年级
         */
        function selectedOrganize() {
            var realOrganize = init_page_param.organizeId;
            var organizeChildrens = $('#select_organize').children();
            for (var i = 0; i < organizeChildrens.length; i++) {
                if (Number($(organizeChildrens[i]).val()) == realOrganize) {
                    $(organizeChildrens[i]).prop('selected', true);
                    break;
                }
            }
        }

        /**
         * 初始化学校信息选中
         */
        function init() {
            changeDepartment(init_page_param.collegeId);
        }

        init();

        /**
         * 改变系选项
         * @param college_id 院id
         */
        function changeDepartment(college_id) {

            if (Number(college_id) == 0) {
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
                $(paramId.select_department).html(html);
            } else {
                // 根据院id查询全部系
                // 显示遮罩
                startLoading();
                $.post(web_path + ajax_url.department_data_url, {collegeId: college_id}, function (data) {
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
                    $(paramId.select_department).html(html);
                    // 只在页面初始化时执行
                    if (selectedDepartmentCount) {
                        selectedDepartment();
                        selectedDepartmentCount = false;
                    }
                    // 去除遮罩
                    endLoading();
                });
            }
        }

        /**
         * 改变专业选项
         * @param department_id 系id
         */
        function changeScience(department_id) {

            if (Number(department_id) == 0) {
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
                $(paramId.select_science).html(html);
            } else {
                // 根据系id查询全部专业
                // 显示遮罩
                startLoading();
                $.post(web_path + ajax_url.science_data_url, {departmentId: department_id}, function (data) {
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
                    $(paramId.select_science).html(html);
                    // 只在页面初始化时执行
                    if (selectedScienceCount) {
                        selectedScience();
                        selectedScienceCount = false;
                    }
                    // 去除遮罩
                    endLoading();
                });
            }
        }

        /**
         * 改变年级选项
         * @param science_id 专业id
         */
        function changeGrade(science_id) {

            if (Number(science_id) == 0) {
                var source = $("#grade-template").html();
                var template = Handlebars.compile(source);

                var context = {
                    listResult: [
                        {name: "请选择年级", value: ""}
                    ]
                };

                Handlebars.registerHelper('grade_value', function () {
                    var value = Handlebars.escapeExpression(this.value);
                    return new Handlebars.SafeString(value);
                });

                Handlebars.registerHelper('grade_name', function () {
                    var name = Handlebars.escapeExpression(this.name);
                    return new Handlebars.SafeString(name);
                });

                var html = template(context);
                $(paramId.select_grade).html(html);
            } else {
                // 根据专业id查询全部年级
                // 显示遮罩
                startLoading();
                $.post(web_path + ajax_url.grade_data_url, {scienceId: science_id}, function (data) {
                    var source = $("#grade-template").html();
                    var template = Handlebars.compile(source);

                    Handlebars.registerHelper('grade_value', function () {
                        var value = Handlebars.escapeExpression(this.value);
                        return new Handlebars.SafeString(value);
                    });

                    Handlebars.registerHelper('grade_name', function () {
                        var name = Handlebars.escapeExpression(this.text);
                        return new Handlebars.SafeString(name);
                    });

                    var html = template(data);
                    $(paramId.select_grade).html(html);
                    // 只在页面初始化时执行
                    if (selectedGradeCount) {
                        selectedGrade();
                        selectedGradeCount = false;
                    }
                    // 去除遮罩
                    endLoading();
                });
            }
        }

        /**
         * 改变班级选项
         * @param grade 年级
         */
        function changeOrganize(grade) {

            if (grade === '') {
                var source = $("#organize-template").html();
                var template = Handlebars.compile(source);

                var context = {
                    listResult: [
                        {name: "请选择班级", value: ""}
                    ]
                };

                Handlebars.registerHelper('organize_value', function () {
                    var value = Handlebars.escapeExpression(this.value);
                    return new Handlebars.SafeString(value);
                });

                Handlebars.registerHelper('organize_name', function () {
                    var name = Handlebars.escapeExpression(this.name);
                    return new Handlebars.SafeString(name);
                });

                var html = template(context);
                $(paramId.select_organize).html(html);
            } else {
                // 根据年级查询全部班级
                // 显示遮罩
                startLoading();
                $.post(web_path + ajax_url.organize_data_url, {grade: grade}, function (data) {
                    var source = $("#organize-template").html();
                    var template = Handlebars.compile(source);

                    Handlebars.registerHelper('organize_value', function () {
                        var value = Handlebars.escapeExpression(this.organizeId);
                        return new Handlebars.SafeString(value);
                    });

                    Handlebars.registerHelper('organize_name', function () {
                        var name = Handlebars.escapeExpression(this.organizeName);
                        return new Handlebars.SafeString(name);
                    });

                    var html = template(data);
                    $(paramId.select_organize).html(html);
                    // 只在页面初始化时执行
                    if (selectedOrganizeCount) {
                        selectedOrganize();
                        selectedOrganizeCount = false;
                    }
                    // 去除遮罩
                    endLoading();
                });
            }
        }

        /**
         * 表单提交时检验
         */
        $('#school_submit').click(function () {
            // 显示遮罩
            startLoading();
            initParam();
            var department = param.department;
            var science = param.science;
            var grade = param.grade;
            var organize = param.organize;

            if (Number(department) <= 0) {
                validErrorDom(validId.valid_department, errorMsgId.department_error_msg, '请选择系');
                // 去除遮罩
                endLoading();
                return;
            } else {
                validSuccessDom(validId.valid_department, errorMsgId.department_error_msg);
            }

            if (Number(science) <= 0) {
                validErrorDom(validId.valid_science, errorMsgId.science_error_msg, '请选择专业');
                // 去除遮罩
                endLoading();
                return;
            } else {
                validSuccessDom(validId.valid_science, errorMsgId.science_error_msg);
            }

            if (Number(grade) <= 0) {
                validErrorDom(validId.valid_grade, errorMsgId.grade_error_msg, '请选择年级');
                // 去除遮罩
                endLoading();
                return;
            } else {
                validSuccessDom(validId.valid_grade, errorMsgId.grade_error_msg);
            }

            if (Number(organize) <= 0) {
                validErrorDom(validId.valid_organize, errorMsgId.organize_error_msg, '请选择班级');
                // 去除遮罩
                endLoading();
            } else {
                validSuccessDom(validId.valid_organize, errorMsgId.organize_error_msg);
                $.post(web_path + ajax_url.school_update, $('#school_form').serialize(), function (data) {
                    if (data.state) {
                        $('#schoolModal').modal('hide');
                        window.location.reload(true);
                    } else {
                        Messenger().post({
                            message: data.msg,
                            type: 'error',
                            showCloseButton: true
                        });
                        // 去除遮罩
                        endLoading();
                    }
                });
            }
        });
    });