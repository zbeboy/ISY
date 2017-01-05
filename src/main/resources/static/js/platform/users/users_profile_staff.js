/**
 * Created by lenovo on 2016-11-02.
 */
//# sourceURL=users_profile_staff.js
require(["jquery", "handlebars", "jquery.showLoading", "messenger", "bootstrap", "jquery.address"],
    function ($, Handlebars) {
        /*
         ajax url
         */
        var ajax_url = {
            department_data_url: '/user/departments',
            school_update: '/anyone/users/profile/staff/school/update',
            profile_edit: '/anyone/users/profile/edit',
            finish: '/anyone/users/profile'
        };

        function startLoading() {
            // 显示遮罩
            $('#page-wrapper').showLoading();
        }

        function endLoading() {
            // 去除遮罩
            $('#page-wrapper').hideLoading();
        }

        /*
         用户信息编辑
         */
        $('#profileEdit').click(function () {
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
            select_department: '#select_department'
        };

        /*
         参数
         */
        var param = {
            department: $(paramId.select_department).val().trim()
        };

        /*
         初始化参数
         */
        function initParam() {
            param.department = $(paramId.select_department).val().trim();
        }

        /*
         验证form id
         */
        var validId = {
            valid_department: '#valid_department'
        };

        /*
         错误消息 id
         */
        var errorMsgId = {
            department_error_msg: '#department_error_msg'
        };

        /*
         页面加载时初始化选中
         */
        var selectedDepartmentCount = true;

        // 当改变系时，变换专业数据.
        $(paramId.select_department).change(function () {
            initParam();
            var department = param.department;
            if (Number(department) > 0) {
                validSuccessDom(validId.valid_department, errorMsgId.department_error_msg);
            } else {
                validErrorDom(validId.valid_department, errorMsgId.department_error_msg, '请选择系');
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
                    break;
                }
            }
        }

        /**
         * 获取系
         */
        function getDepartment(id) {
            var text = '';
            var departmentChildrens = $('#select_department').children();
            for (var i = 0; i < departmentChildrens.length; i++) {
                if (Number($(departmentChildrens[i]).val()) == Number(id)) {
                    text = $(departmentChildrens[i]).text();
                    break;
                }
            }
            return text;
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
         * 表单提交时检验
         */
        $('#school_submit').click(function () {
            initParam();
            var department = param.department;

            if (Number(department) <= 0) {
                validErrorDom(validId.valid_department, errorMsgId.department_error_msg, '请选择系');
            } else {
                validSuccessDom(validId.valid_department, errorMsgId.department_error_msg);
                $.post(web_path + ajax_url.school_update, $('#school_form').serialize(), function (data) {
                    if (data.state) {
                        $('#updateDepartment').text(getDepartment(param.department));
                        validCleanDom(validId.valid_department, errorMsgId.department_error_msg);
                        $('#schoolModal').modal('hide');
                    } else {
                        Messenger().post({
                            message: data.msg,
                            type: 'error',
                            showCloseButton: true
                        });
                    }
                });
            }
        });
    });