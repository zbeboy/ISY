/**
 * Created by zbeboy on 2017/5/23.
 */
//# sourceURL=design_adjustech_adjust.js
require(["jquery", "nav_active", "handlebars", "messenger", "jquery.address", "jquery.showLoading", "tablesaw"], function ($, nav_active, Handlebars) {
    /*
     ajax url.
     */
    var ajax_url = {
        teacher_data_url: '/web/graduate/design/adjustech/teacher/data',
        student_data_url: '/web/graduate/design/adjustech/student/data',
        adjust_teacher: '/web/graduate/design/adjustech/teachers',
        update: '/web/graduate/design/adjustech/update',
        del: '/web/graduate/design/adjustech/delete',
        back: '/web/menu/graduate/design/adjustech'
    };

    // 刷新时选中菜单
    nav_active(ajax_url.back);

    function startLoading() {
        // 显示遮罩
        $('#page-wrapper').showLoading();
    }

    function endLoading() {
        // 去除遮罩
        $('#page-wrapper').hideLoading();
    }

    $('#refresh').click(function () {
        init();
    });

    var teacherData = '#teacherData';
    var studentData = '#studentData';

    /*
     返回
     */
    $('#page_back').click(function () {
        $.address.value(ajax_url.back);
    });

    init();

    function init() {
        startLoading();
        $.get(web_path + ajax_url.teacher_data_url, {id: init_page_param.graduationDesignReleaseId}, function (data) {
            endLoading();
            if (data.state) {
                teacherListData(data);
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    }

    /**
     * 获取相应教师下的学生数据
     * @param graduationDesignTeacherId 指导教师ID
     */
    function initStudentData(graduationDesignTeacherId) {
        startLoading();
        $.get(web_path + ajax_url.student_data_url, {
            graduationDesignTeacherId: graduationDesignTeacherId
        }, function (data) {
            endLoading();
            if (data.state) {
                studentListData(data);
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    }

    /**
     * 教师列表数据
     * @param data 数据
     */
    function teacherListData(data) {
        var template = Handlebars.compile($("#teacher-template").html());
        $(teacherData).html(template(data));
        $('#teacherTable').tablesaw().data("tablesaw").refresh();
    }

    /**
     * 学生列表数据
     * @param data 数据
     */
    function studentListData(data) {
        var template = Handlebars.compile($("#student-template").html());
        $(studentData).html(template(data));
        $('#studentTable').tablesaw().data("tablesaw").refresh();
    }

    /**
     * 教师数据
     * @param data 数据
     */
    function teachersData(data) {
        var template = Handlebars.compile($("#adjust-teacher-template").html());
        $('#teachers').html(template(data));
    }

    /*
     详情
     */
    $(teacherData).delegate('.teacher_detail', "click", function () {
        $('#teacherName').text($(this).attr('data-name'));
        $('#teacherMobile').text($(this).attr('data-mobile'));
        initStudentData($(this).attr('data-id'));
    });

    /*
     调整
     */
    $(studentData).delegate('.adjust_teacher', "click", function () {
        var graduationDesignTutorId = $(this).attr('data-id');
        var graduationDesignTeacherId = $(this).attr('data-teacher');
        $.get(ajax_url.adjust_teacher, {
            id: init_page_param.graduationDesignReleaseId,
            graduationDesignTeacherId: graduationDesignTeacherId
        }, function (data) {
            if (data.state) {
                teachersData(data);
                $('#teacherGraduationDesignTutorId').val(graduationDesignTutorId);
                $('#teacherGraduationDesignTeacherId').val(graduationDesignTeacherId);
                $('#teacherModal').modal('show');
            } else {
                Messenger().post({
                    message: data.msg,
                    type: 'error',
                    showCloseButton: true
                });
            }
        });
    });

    /*
     删除
     */
    $(studentData).delegate('.delete_teacher', "click", function () {
        var graduationDesignTutorId = $(this).attr('data-id');
        var studentName = $(this).attr('data-student');
        var graduationDesignTeacherId = $(this).attr('data-teacher');
        var msg;
        msg = Messenger().post({
            message: "确定删除 '" + studentName + "' 吗?",
            actions: {
                retry: {
                    label: '确定',
                    phrase: 'Retrying TIME',
                    action: function () {
                        msg.cancel();
                        del(graduationDesignTutorId, graduationDesignTeacherId);
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
     * 保存调整
     */
    $('#saveTeacher').click(function () {
        $.post(ajax_url.update, $('#teacher_form').serialize(), function (data) {
            if (data.state) {
                $('#teacherModal').modal('hide');
                $('#teacher_error_msg').addClass('hidden').removeClass('text-danger').text('');
                initStudentData($('#teacherGraduationDesignTeacherId').val());
            } else {
                $('#teacher_error_msg').removeClass('hidden').addClass('text-danger').text(data.msg);
            }
        });
    });

    function del(graduationDesignTutorId, graduationDesignTeacherId) {
        sendDelAjax(graduationDesignTutorId, graduationDesignTeacherId, '删除');
    }

    /**
     * 删除ajax
     * @param graduationDesignTutorId
     * @param graduationDesignTeacherId
     * @param message
     */
    function sendDelAjax(graduationDesignTutorId, graduationDesignTeacherId, message) {
        Messenger().run({
            successMessage: message + '学生成功',
            errorMessage: message + '学生失败',
            progressMessage: '正在' + message + '学生....'
        }, {
            url: web_path + ajax_url.del,
            type: 'post',
            data: {
                id: init_page_param.graduationDesignReleaseId,
                graduationDesignTutorIds: graduationDesignTutorId
            },
            success: function (data) {
                if (data.state) {
                    initStudentData(graduationDesignTeacherId);
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