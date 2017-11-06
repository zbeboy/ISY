/**
 * Created by lenovo on 2016-08-19.
 */
requirejs.config({
    map: {
        '*': {
            'css': web_path + '/webjars/require-css/css.min.js' // or whatever the path to require-css is
        }
    },
    // pathsオプションの設定。"module/name": "path"を指定します。拡張子（.js）は指定しません。
    paths: {
        "jquery.showLoading": web_path + "/plugin/loading/js/jquery.showLoading.min",
        "csrf": web_path + "/js/util/csrf",
        "emails": web_path + "/js/util/emails",
        "jquery.entropizer": web_path + "/plugin/jquery_entropizer/js/jquery-entropizer.min",
        "entropizer": web_path + "/plugin/jquery_entropizer/js/entropizer.min",
        "bootstrap-typeahead": ["https://cdn.bootcss.com/bootstrap-3-typeahead/4.0.2/bootstrap3-typeahead.min",
            web_path + "/plugin/bootstrap-typeahead/bootstrap3-typeahead.min"],
        "bootstrap-select": web_path + "/plugin/bootstrap-select/js/bootstrap-select.min",
        "bootstrap-select-zh-CN": web_path + "/plugin/bootstrap-select/js/i18n/defaults-zh_CN.min"
    },
    // shimオプションの設定。モジュール間の依存関係を定義します。
    shim: {
        "jquery.showLoading": {
            // jQueryに依存するのでpathsで設定した"module/name"を指定します。
            deps: ["jquery"]
        },
        "bootstrap-select-zh-CN": {
            deps: ["bootstrap-select", "css!" + web_path + "/plugin/bootstrap-select/css/bootstrap-select.min"]
        }
    }
});

// require(["module/name", ...], function(params){ ... });
require(["jquery", "handlebars", "emails", "jquery.showLoading", "csrf", "bootstrap",
        "jquery.entropizer", "bootstrap-typeahead", "bootstrap-select-zh-CN"],
    function ($, Handlebars, emails) {

        /*
         ajax url
         */
        var ajax_url = {
            school_data_url: '/user/schools',
            college_data_url: '/user/colleges',
            department_data_url: '/user/departments',
            science_data_url: '/user/sciences',
            grade_data_url: '/user/grades',
            organize_data_url: '/user/organizes',
            valid_student_url: '/user/register/valid/student',
            valid_users_url: '/user/register/valid/users',
            valid_mobile_url: '/user/register/valid/mobile',
            mobile_code_url: '/user/register/mobile/code',
            register_student_url: '/user/register/student',
            finish: '/register/finish'
        };

        /*
         正则
         */
        var valid_regex = {
            student_number_valid_regex: /^\d{13,}$/,
            email_valid_regex: /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/,
            mobile_valid_regex: /^1[0-9]{10}$/,
            phone_verify_code_valid_regex: /^\w+$/,
            password_valid_regex: /^[a-zA-Z0-9]\w{5,17}$/
        };

        /*
         消息
         */
        var msg = {
            real_name_error_msg: '请填写姓名',
            student_number_error_msg: '学号至少13位数字',
            email_error_msg: '邮箱格式不正确',
            mobile_error_msg: '手机号格式不正确',
            phone_verify_code_error_msg: '验证码不正确',
            password_error_msg: '密码6-16位任意字母或数字，以及下划线',
            confirm_password_error_msg: '密码不一致'
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
            select_school: '#select_school',
            schoolName: '#schoolName',
            select_college: '#select_college',
            collegeName: '#collegeName',
            select_department: '#select_department',
            departmentName: '#departmentName',
            select_science: '#select_science',
            scienceName: '#scienceName',
            select_grade: '#select_grade',
            select_organize: '#select_organize',
            organizeName: '#organizeName',
            realName: '#realName',
            studentNumber: '#studentNumber',
            email: '#email',
            mobile: '#mobile',
            phoneVerifyCode: '#phoneVerifyCode',
            password: '#password',
            confirmPassword: '#confirmPassword'
        };

        /*
         参数
         */
        var param = {
            realName: $(paramId.realName).val(),
            studentNumber: $(paramId.studentNumber).val(),
            email: $(paramId.email).val(),
            mobile: $(paramId.mobile).val(),
            phoneVerifyCode: $(paramId.phoneVerifyCode).val(),
            password: $(paramId.password).val(),
            confirmPassword: $(paramId.confirmPassword).val(),
            school: $(paramId.select_school).val(),
            schoolName: '',
            college: $(paramId.select_college).val(),
            collegeName: '',
            department: $(paramId.select_department).val(),
            departmentName: '',
            science: $(paramId.select_science).val(),
            scienceName: '',
            grade: $(paramId.select_grade).val(),
            organize: $(paramId.select_organize).val(),
            organizeName: ''
        };

        /*
         初始化参数
         */
        function initParam() {
            param.realName = $(paramId.realName).val();
            param.studentNumber = $(paramId.studentNumber).val();
            param.email = $(paramId.email).val();
            param.mobile = $(paramId.mobile).val();
            param.phoneVerifyCode = $(paramId.phoneVerifyCode).val();
            param.password = $(paramId.password).val();
            param.confirmPassword = $(paramId.confirmPassword).val();
            param.school = $(paramId.select_school).val();
            param.schoolName = $(paramId.select_school).find('option:selected').text();
            param.college = $(paramId.select_college).val();
            param.collegeName = $(paramId.select_college).find('option:selected').text();
            param.department = $(paramId.select_department).val();
            param.departmentName = $(paramId.select_department).find('option:selected').text();
            param.science = $(paramId.select_science).val();
            param.scienceName = $(paramId.select_science).find('option:selected').text();
            param.grade = $(paramId.select_grade).val();
            param.organize = $(paramId.select_organize).val();
            param.organizeName = $(paramId.select_organize).find('option:selected').text();
        }

        /*
         验证form id
         */
        var validId = {
            valid_school: '#valid_school',
            valid_college: '#valid_college',
            valid_department: '#valid_department',
            valid_science: '#valid_science',
            valid_grade: '#valid_grade',
            valid_organize: '#valid_organize',
            valid_real_name: '#valid_real_name',
            valid_student_number: '#valid_student_number',
            valid_email: '#valid_email',
            valid_mobile: '#valid_mobile',
            valid_phone_verify_code: '#valid_phone_verify_code',
            valid_password: '#valid_password',
            valid_confirm_password: '#valid_confirm_password'
        };

        /*
         错误消息 id
         */
        var errorMsgId = {
            school_error_msg: '#school_error_msg',
            college_error_msg: '#college_error_msg',
            department_error_msg: '#department_error_msg',
            science_error_msg: '#science_error_msg',
            grade_error_msg: '#grade_error_msg',
            organize_error_msg: '#organize_error_msg',
            real_name_error_msg: '#real_name_error_msg',
            student_number_error_msg: '#student_number_error_msg',
            email_error_msg: '#email_error_msg',
            mobile_error_msg: '#mobile_error_msg',
            phone_verify_code_error_msg: '#phone_verify_code_error_msg',
            password_error_msg: '#password_error_msg',
            confirm_password_error_msg: '#confirm_password_error_msg'
        };

        // 密码强度检测
        $('#meter2').entropizer({
            target: paramId.password,
            update: function (data, ui) {
                ui.bar.css({
                    'background-color': data.color,
                    'width': data.percent + '%'
                });
            }
        });

        // 初始化学校数据.
        // 显示遮罩
        startLoading();
        $.get(web_path + ajax_url.school_data_url, function (data) {
            initAllSelectInput();
            schoolData(data);
        });

        /**
         * 初始化选择框
         */
        function initAllSelectInput() {
            $(paramId.select_school).selectpicker({
                maxOptions: 1
            });

            $(paramId.select_college).selectpicker({
                maxOptions: 1
            });

            $(paramId.select_department).selectpicker({
                maxOptions: 1
            });

            $(paramId.select_science).selectpicker({
                maxOptions: 1
            });

            $(paramId.select_grade).selectpicker({
                maxOptions: 1
            });

            $(paramId.select_organize).selectpicker({
                maxOptions: 1
            });
        }

        // 当改变学校时，变换学院数据.
        $(paramId.select_school).change(function () {
            initParam();
            var school = param.school;
            changeCollege(school);// 根据学校重新加载院数据
            changeDepartment(0);// 清空系数据
            changeScience(0);// 清空专业数据
            changeGrade(0);// 清空年级数据
            changeOrganize(0, 0);// 清空班级数据

            // 改变选项时，检验
            if (Number(school) > 0) {
                validSuccessDom(validId.valid_school, errorMsgId.school_error_msg);
            } else {
                validErrorDom(validId.valid_school, errorMsgId.school_error_msg, '请选择学校');
            }

            validCleanDom(validId.valid_college, errorMsgId.college_error_msg);

            validCleanDom(validId.valid_department, errorMsgId.department_error_msg);

            validCleanDom(validId.valid_science, errorMsgId.science_error_msg);

            validCleanDom(validId.valid_grade, errorMsgId.grade_error_msg);

            validCleanDom(validId.valid_organize, errorMsgId.organize_error_msg);
        });

        // 当改变学院时，变换系数据.
        $(paramId.select_college).change(function () {
            initParam();
            var college = param.college;
            changeDepartment(college);// 根据院重新加载系数据
            changeScience(0);// 清空专业数据
            changeGrade(0);// 清空年级数据
            changeOrganize(0, 0);// 清空班级数据

            if (Number(college) > 0) {
                validSuccessDom(validId.valid_college, errorMsgId.college_error_msg);
            } else {
                validErrorDom(validId.valid_college, errorMsgId.college_error_msg, '请选择院');
            }

            validCleanDom(validId.valid_department, errorMsgId.department_error_msg);

            validCleanDom(validId.valid_science, errorMsgId.science_error_msg);

            validCleanDom(validId.valid_grade, errorMsgId.grade_error_msg);

            validCleanDom(validId.valid_organize, errorMsgId.organize_error_msg);
        });

        // 当改变系时，变换专业数据.
        $(paramId.select_department).change(function () {
            initParam();
            var department = param.department;
            changeScience(department);// 根据系重新加载专业数据
            changeGrade(0);// 清空年级数据
            changeOrganize(0, 0);// 清空班级数据

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
            changeOrganize(0, 0);// 清空班级数据

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
            var science = param.science;
            changeOrganize(grade, science);// 根据年级重新加载班级数据

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

            $(paramId.select_school).html(template(data));

            $(paramId.select_school).selectpicker('refresh');

            // 去除遮罩
            endLoading();
        }

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

                $(paramId.select_college).html(template(context));

                $(paramId.select_college).selectpicker('refresh');
            } else {
                // 根据学校id查询院数据
                // 显示遮罩
                startLoading();
                $.post(web_path + ajax_url.college_data_url, {schoolId: school_id}, function (data) {
                    var template = Handlebars.compile($("#college-template").html());

                    Handlebars.registerHelper('college_value', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.collegeId));
                    });

                    Handlebars.registerHelper('college_name', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.collegeName));
                    });

                    $(paramId.select_college).html(template(data));

                    $(paramId.select_college).selectpicker('refresh');
                    // 去除遮罩
                    endLoading();
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

                $(paramId.select_department).html(template(context));

                $(paramId.select_department).selectpicker('refresh');
            } else {
                // 根据院id查询全部系
                // 显示遮罩
                startLoading();
                $.post(web_path + ajax_url.department_data_url, {collegeId: college_id}, function (data) {
                    var template = Handlebars.compile($("#department-template").html());

                    Handlebars.registerHelper('department_value', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.departmentId));
                    });

                    Handlebars.registerHelper('department_name', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.departmentName));
                    });

                    $(paramId.select_department).html(template(data));

                    $(paramId.select_department).selectpicker('refresh');
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

                $(paramId.select_science).html(template(context));

                $(paramId.select_science).selectpicker('refresh');
            } else {
                // 根据系id查询全部专业
                // 显示遮罩
                startLoading();
                $.post(web_path + ajax_url.science_data_url, {departmentId: department_id}, function (data) {
                    var template = Handlebars.compile($("#science-template").html());

                    Handlebars.registerHelper('science_value', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.scienceId));
                    });

                    Handlebars.registerHelper('science_name', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.scienceName));
                    });

                    $(paramId.select_science).html(template(data));

                    $(paramId.select_science).selectpicker('refresh');
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
                var template = Handlebars.compile($("#grade-template").html());

                var context = {
                    listResult: [
                        {name: "请选择年级", value: ""}
                    ]
                };

                Handlebars.registerHelper('grade_value', function () {
                    return new Handlebars.SafeString(Handlebars.escapeExpression(this.value));
                });

                Handlebars.registerHelper('grade_name', function () {
                    return new Handlebars.SafeString(Handlebars.escapeExpression(this.name));
                });

                $(paramId.select_grade).html(template(context));

                $(paramId.select_grade).selectpicker('refresh');
            } else {
                // 根据专业id查询全部年级
                // 显示遮罩
                startLoading();
                $.post(web_path + ajax_url.grade_data_url, {scienceId: science_id}, function (data) {
                    var template = Handlebars.compile($("#grade-template").html());

                    Handlebars.registerHelper('grade_value', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.value));
                    });

                    Handlebars.registerHelper('grade_name', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.text));
                    });

                    $(paramId.select_grade).html(template(data));

                    $(paramId.select_grade).selectpicker('refresh');
                    // 去除遮罩
                    endLoading();
                });
            }
        }

        /**
         * 改变班级选项
         * @param grade 年级
         * @param scienceId 专业id
         */
        function changeOrganize(grade, scienceId) {

            if (grade == 0 || grade === '' || scienceId <= 0) {
                var template = Handlebars.compile($("#organize-template").html());

                var context = {
                    listResult: [
                        {name: "请选择班级", value: ""}
                    ]
                };

                Handlebars.registerHelper('organize_value', function () {
                    return new Handlebars.SafeString(Handlebars.escapeExpression(this.value));
                });

                Handlebars.registerHelper('organize_name', function () {
                    return new Handlebars.SafeString(Handlebars.escapeExpression(this.name));
                });

                $(paramId.select_organize).html(template(context));

                $(paramId.select_organize).selectpicker('refresh');
            } else {
                // 根据年级查询全部班级
                // 显示遮罩
                startLoading();
                $.post(web_path + ajax_url.organize_data_url, {grade: grade, scienceId: scienceId}, function (data) {
                    var template = Handlebars.compile($("#organize-template").html());

                    Handlebars.registerHelper('organize_value', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.organizeId));
                    });

                    Handlebars.registerHelper('organize_name', function () {
                        return new Handlebars.SafeString(Handlebars.escapeExpression(this.organizeName));
                    });

                    $(paramId.select_organize).html(template(data));

                    $(paramId.select_organize).selectpicker('refresh');
                    // 去除遮罩
                    endLoading();
                });
            }
        }

        // 即时检验
        $(paramId.realName).blur(function () {
            initParam();
            var realName = param.realName;
            if (realName === '') {
                validErrorDom(validId.valid_real_name, errorMsgId.real_name_error_msg, msg.real_name_error_msg);
            } else {
                validSuccessDom(validId.valid_real_name, errorMsgId.real_name_error_msg);
            }
        });

        // 即时检验
        $(paramId.studentNumber).blur(function () {
            initParam();
            var studentNumber = param.studentNumber;
            if (!valid_regex.student_number_valid_regex.test(studentNumber)) {
                validErrorDom(validId.valid_student_number, errorMsgId.student_number_error_msg, msg.student_number_error_msg);
            } else {
                // ajax 检验
                $.post(web_path + ajax_url.valid_student_url, {studentNumber: studentNumber}, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_student_number, errorMsgId.student_number_error_msg);
                    } else {
                        validErrorDom(validId.valid_student_number, errorMsgId.student_number_error_msg, '该学号已被注册');
                    }
                });
            }
        });

        // 自动完成账号
        $(paramId.email).typeahead({
            source: function (query, process) {
                if (query.indexOf('@') === -1) {
                    var tempArr = [];
                    for (var i = 0; i < emails.mailArr.length; i++) {
                        tempArr.push(query + emails.mailArr[i])
                    }
                    process(tempArr);
                }
            },
            afterSelect: function (item) {
                //选择项之后的事件 ，item是当前选中的。
                if (valid_regex.email_valid_regex.test(item)) {
                    validSuccessDom(validId.valid_email, errorMsgId.email_error_msg);
                }
            },
            autoSelect: true
        });

        $(paramId.email).blur(function () {
            initParam();
            var email = param.email;
            if (!valid_regex.email_valid_regex.test(email)) {
                validErrorDom(validId.valid_email, errorMsgId.email_error_msg, msg.email_error_msg);
            } else {
                // ajax 检验
                $.post(web_path + ajax_url.valid_users_url, {username: email, validType: 1}, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_email, errorMsgId.email_error_msg, msg.email_error_msg);
                    } else {
                        validErrorDom(validId.valid_email, errorMsgId.email_error_msg, data.msg);
                    }
                });
            }
        });

        $(paramId.mobile).blur(function () {
            initParam();
            var mobile = param.mobile;
            if (!valid_regex.mobile_valid_regex.test(mobile)) {
                validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, msg.mobile_error_msg);
            } else {
                // ajax 检验
                $.post(web_path + ajax_url.valid_users_url, {mobile: mobile, validType: 2}, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_mobile, errorMsgId.mobile_error_msg);
                    } else {
                        validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, data.msg);
                    }
                });
            }
        });

        $(paramId.phoneVerifyCode).blur(function () {
            initParam();
            var phoneVerifyCode = param.phoneVerifyCode;
            var mobile = param.mobile;

            if (!valid_regex.mobile_valid_regex.test(mobile)) {
                validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, msg.mobile_error_msg);
            } else {
                // ajax 检验
                $.post(web_path + ajax_url.valid_users_url, {mobile: mobile, validType: 2}, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_mobile, errorMsgId.mobile_error_msg);
                        if (!valid_regex.phone_verify_code_valid_regex.test(phoneVerifyCode)) {
                            validErrorDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg, msg.phone_verify_code_error_msg);
                        } else {
                            // ajax 检验 参数：手机号，验证码 后台保存这两个参数，在提交时再次检验
                            $.post(web_path + ajax_url.valid_mobile_url, {
                                mobile: mobile,
                                phoneVerifyCode: phoneVerifyCode
                            }, function (data) {
                                if (data.state) {
                                    validSuccessDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg);
                                } else {
                                    validErrorDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg, data.msg);
                                }
                            });
                        }
                    } else {
                        validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, data.msg);
                    }
                });
            }
        });

        $(paramId.password).blur(function () {
            initParam();
            var password = param.password;
            if (!valid_regex.password_valid_regex.test(password)) {
                validErrorDom(validId.valid_password, errorMsgId.password_error_msg, msg.password_error_msg);
            } else {
                validSuccessDom(validId.valid_password, errorMsgId.password_error_msg);
            }
        });

        $(paramId.confirmPassword).blur(function () {
            initParam();
            var password = param.password;
            var confirmPassword = param.confirmPassword;
            if (!valid_regex.password_valid_regex.test(password)) {
                validErrorDom(validId.valid_password, errorMsgId.password_error_msg, msg.password_error_msg);
            } else {
                validSuccessDom(validId.valid_password, errorMsgId.password_error_msg);
                if (confirmPassword !== password) {
                    validErrorDom(validId.valid_confirm_password, errorMsgId.confirm_password_error_msg, msg.confirm_password_error_msg);
                } else {
                    validSuccessDom(validId.valid_confirm_password, errorMsgId.confirm_password_error_msg)
                }
            }
        });

        /**
         * 获取手机验证码
         */
        var InterValObj; //timer变量，控制时间
        var count = 120; //间隔函数，1秒执行
        var curCount;//当前剩余秒数
        var btnId = '#get_verification_code';
        $(btnId).click(function () {
            initParam();
            var mobile = param.mobile;
            if (valid_regex.mobile_valid_regex.test(mobile)) {

                $.post(web_path + ajax_url.valid_users_url, {mobile: mobile, validType: 2}, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_mobile, errorMsgId.mobile_error_msg);
                        curCount = count;
                        //设置button效果，开始计时
                        $(btnId).attr("disabled", "true");
                        $(btnId).val(curCount + "秒后重新获取验证码");
                        InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次

                        $.get(web_path + ajax_url.mobile_code_url, {mobile: mobile}, function (data) {
                            if (data.state) {
                                $(errorMsgId.phone_verify_code_error_msg).removeClass('hidden').text(data.msg);
                            } else {
                                validErrorDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg, data.msg);
                            }
                        });
                    } else {
                        validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, data.msg);
                    }
                });
            } else {
                validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, msg.mobile_error_msg);
            }
        });

        //timer处理函数
        function SetRemainTime() {
            if (curCount == 0) {
                window.clearInterval(InterValObj);//停止计时器
                $(btnId).removeAttr("disabled");//启用按钮
                $(btnId).val("重新发送验证码");
            }
            else {
                curCount--;
                $(btnId).val(curCount + "秒后重新获取验证码");
            }
        }

        /**
         * 表单提交时检验
         */
        $('#register').click(function () {
            // 显示遮罩
            startLoading();
            initParam();
            var school = param.school;
            var college = param.college;
            var department = param.department;
            var science = param.science;
            var grade = param.grade;
            var organize = param.organize;

            if (Number(school) <= 0) {
                // 去除遮罩
                endLoading();
                validErrorDom(validId.valid_school, errorMsgId.school_error_msg, '请选择学校');
                return;
            } else {
                validSuccessDom(validId.valid_school, errorMsgId.school_error_msg);
            }

            if (Number(college) <= 0) {
                // 去除遮罩
                endLoading();
                validErrorDom(validId.valid_college, errorMsgId.college_error_msg, '请选择院');
                return;
            } else {
                validSuccessDom(validId.valid_college, errorMsgId.college_error_msg);
            }

            if (Number(department) <= 0) {
                // 去除遮罩
                endLoading();
                validErrorDom(validId.valid_department, errorMsgId.department_error_msg, '请选择系');
                return;
            } else {
                validSuccessDom(validId.valid_department, errorMsgId.department_error_msg);
            }

            if (Number(science) <= 0) {
                // 去除遮罩
                endLoading();
                validErrorDom(validId.valid_science, errorMsgId.science_error_msg, '请选择专业');
                return;
            } else {
                validSuccessDom(validId.valid_science, errorMsgId.science_error_msg);
            }

            if (Number(grade) <= 0) {
                // 去除遮罩
                endLoading();
                validErrorDom(validId.valid_grade, errorMsgId.grade_error_msg, '请选择年级');
                return;
            } else {
                validSuccessDom(validId.valid_grade, errorMsgId.grade_error_msg);
            }

            if (Number(organize) <= 0) {
                // 去除遮罩
                endLoading();
                validErrorDom(validId.valid_organize, errorMsgId.organize_error_msg, '请选择班级');
                return;
            } else {
                validSuccessDom(validId.valid_organize, errorMsgId.organize_error_msg);
            }
            // 填充数据
            $(paramId.schoolName).val($(paramId.select_school).find('option:selected').text());
            $(paramId.collegeName).val($(paramId.select_college).find('option:selected').text());
            $(paramId.departmentName).val($(paramId.select_department).find('option:selected').text());
            $(paramId.scienceName).val($(paramId.select_science).find('option:selected').text());
            $(paramId.organizeName).val($(paramId.select_organize).find('option:selected').text());

            validRealName();//开始顺序检验
        });

        function validRealName() {
            initParam();
            var realName = param.realName;
            if (realName === '') {
                // 去除遮罩
                endLoading();
                validErrorDom(validId.valid_real_name, errorMsgId.real_name_error_msg, msg.real_name_error_msg);
            } else {
                validSuccessDom(validId.valid_real_name, errorMsgId.real_name_error_msg);
                validStudentNumber();
            }
        }

        /**
         * 检验数据是否正常
         * 注：后台session中应存取手机号，防止验证码过后，变更手机号，造成不一致的情况。
         */
        function validStudentNumber() {
            initParam();
            var studentNumber = param.studentNumber;
            if (!valid_regex.student_number_valid_regex.test(studentNumber)) {
                // 去除遮罩
                endLoading();
                validErrorDom(validId.valid_student_number, errorMsgId.student_number_error_msg, msg.student_number_error_msg);
            } else {
                // ajax 检验
                $.post(web_path + ajax_url.valid_student_url, {studentNumber: studentNumber}, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_student_number, errorMsgId.student_number_error_msg);
                        validEmail();
                    } else {
                        // 去除遮罩
                        endLoading();
                        validErrorDom(validId.valid_student_number, errorMsgId.student_number_error_msg, '该学号已被注册');
                    }
                });
            }
        }

        function validEmail() {
            initParam();
            var email = param.email;
            if (!valid_regex.email_valid_regex.test(email)) {
                // 去除遮罩
                endLoading();
                validErrorDom(validId.valid_email, errorMsgId.email_error_msg, msg.email_error_msg);
            } else {
                // ajax 检验
                $.post(web_path + ajax_url.valid_users_url, {username: email, validType: 1}, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_email, errorMsgId.email_error_msg);
                        validMobile();
                    } else {
                        // 去除遮罩
                        endLoading();
                        validErrorDom(validId.valid_email, errorMsgId.email_error_msg, data.msg);
                    }
                });
            }
        }

        function validMobile() {
            initParam();
            var mobile = param.mobile;
            if (!valid_regex.mobile_valid_regex.test(mobile)) {
                // 去除遮罩
                endLoading();
                validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, msg.mobile_error_msg);
            } else {
                // ajax 检验
                $.post(web_path + ajax_url.valid_users_url, {mobile: mobile, validType: 2}, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_mobile, errorMsgId.mobile_error_msg);
                        validPhoneVerifyCode();
                    } else {
                        // 去除遮罩
                        endLoading();
                        validErrorDom(validId.valid_mobile, errorMsgId.mobile_error_msg, data.msg);
                    }
                });
            }
        }

        function validPhoneVerifyCode() {
            initParam();
            var phoneVerifyCode = param.phoneVerifyCode;
            var mobile = param.mobile;
            if (!valid_regex.phone_verify_code_valid_regex.test(phoneVerifyCode)) {
                // 去除遮罩
                endLoading();
                validErrorDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg, msg.phone_verify_code_error_msg);
            } else {
                // ajax 检验 参数：手机号，验证码 后台保存这两个参数，在提交时再次检验
                $.post(web_path + ajax_url.valid_mobile_url, {
                    mobile: mobile,
                    phoneVerifyCode: phoneVerifyCode
                }, function (data) {
                    if (data.state) {
                        validSuccessDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg);
                        validPassword();
                    } else {
                        // 去除遮罩
                        endLoading();
                        validErrorDom(validId.valid_phone_verify_code, errorMsgId.phone_verify_code_error_msg, data.msg);
                    }
                });
            }
        }

        function validPassword() {
            initParam();
            var password = param.password;
            var confirmPassword = param.confirmPassword;
            if (!valid_regex.password_valid_regex.test(password)) {
                // 去除遮罩
                endLoading();
                validErrorDom(validId.valid_password, errorMsgId.password_error_msg, msg.password_error_msg);
            } else {
                validSuccessDom(validId.valid_password, errorMsgId.password_error_msg);
                if (confirmPassword !== password) {
                    // 去除遮罩
                    endLoading();
                    validErrorDom(validId.valid_confirm_password, errorMsgId.confirm_password_error_msg, msg.confirm_password_error_msg);
                } else {
                    validSuccessDom(validId.valid_confirm_password, errorMsgId.confirm_password_error_msg);
                    $.post(web_path + ajax_url.register_student_url, $('#student_register_form').serialize(), function (data) {
                        if (data.state) {
                            window.location.href = web_path + ajax_url.finish;
                        } else {
                            // 去除遮罩
                            endLoading();
                            $('#error_msg').removeClass('hidden').text(data.msg);
                        }
                    });
                }
            }
        }

    });