/**
 * Created by lenovo on 2016/11/25.
 */
//# sourceURL=internship_apply.js
require(["jquery", "handlebars", "messenger", "jquery.address", "jquery.simple-pagination", "jquery.showLoading", "bootstrap"],
    function ($, Handlebars) {

        /*
         ajax url.
         */
        var ajax_url = {
            internship_apply_data_url: '/web/internship/apply/data',
            access_url: '/web/internship/apply/access',
            valid_is_student: '/anyone/valid/cur/is/student',
            valid_student: '/web/internship/apply/valid/student',
            access_condition_url: '/web/internship/apply/condition'
        };

        /*
         参数id
         */
        var paramId = {
            internshipTitle: '#search_internship_title',
            studentUsername: '#studentUsername',
            studentNumber: '#studentNumber'
        };

        /*
         参数
         */
        var param = {
            searchParams: '',
            pageNum: 0,
            pageSize: 2,
            displayedPages: 3
        };

        /*
         检验id
         */
        var validId = {
            student: '#valid_student'
        };

        /*
         错误消息id
         */
        var errorMsgId = {
            student: '#student_error_msg'
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

        var tableData = '#tableData';

        function startLoading(targetId) {
            // 显示遮罩
            $(targetId).showLoading();
        }

        function endLoading(targetId) {
            // 去除遮罩
            $(targetId).hideLoading();
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(paramId.internshipTitle).val('');
        }

        /**
         * 刷新查询参数
         */
        function refreshSearch() {
            var params = {
                internshipTitle: $(paramId.internshipTitle).val()
            };
            param.pageNum = 0;
            param.searchParams = JSON.stringify(params);
        }

        /*
         搜索
         */
        $('#search').click(function () {
            refreshSearch();
            init();
        });

        /*
         重置
         */
        $('#reset_search').click(function () {
            cleanParam();
            refreshSearch();
            init();
        });

        $('#refresh').click(function () {
            init();
        });

        $(paramId.internshipTitle).keyup(function (event) {
            if (event.keyCode == 13) {
                refreshSearch();
                init();
            }
        });

        /**
         * 列表数据
         * @param data 数据
         */
        function listData(data) {
            var source = $("#internship-release-template").html();
            var template = Handlebars.compile(source);

            Handlebars.registerHelper('internship_title', function () {
                var value = Handlebars.escapeExpression(this.internshipTitle);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('school_name', function () {
                var value = Handlebars.escapeExpression(this.schoolName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('college_name', function () {
                var value = Handlebars.escapeExpression(this.collegeName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('department_name', function () {
                var value = Handlebars.escapeExpression(this.departmentName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('real_name', function () {
                var value = Handlebars.escapeExpression(this.realName);
                return new Handlebars.SafeString(value);
            });

            var html = template(data);
            $(tableData).html(html);
        }

        /*
         进入申请
         */
        $(tableData).delegate('.apply', "click", function () {
            var id = $(this).attr('data-id');
            // 如果用户类型不是学生，则这里需要一个弹窗，填写学生账号或学生学号以获取学生id
            $.get(web_path + ajax_url.valid_is_student, function (data) {
                if (data.state) {
                    accessApply(id, data.objectResult);
                } else {
                    $('#studentInfoInternshipReleaseId').val(id);
                    $('#studentModal').modal('show');
                }
            })
        });

        /**
         * 进入申请页面
         * @param id
         * @param studentId
         */
        function accessApply(id, studentId) {
            // 进入判断
            $.post(web_path + ajax_url.access_condition_url, {id: id, studentId: studentId}, function (data) {
                if (data.state) {
                    $.address.value(ajax_url.access_url + "?id=" + id + "&studentId=" + studentId);
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
         * 检验学生信息
         */
        function validStudent() {
            var studentUsername = $(paramId.studentUsername).val();
            var studentNumber = $(paramId.studentNumber).val();
            if (studentUsername.length <= 0 && studentNumber.length <= 0) {
                validErrorDom(validId.student,errorMsgId.student,'请至少填写一项学生信息');
            } else {
                var student = "";
                var type = -1;
                if (studentUsername.length > 0) {
                    student = studentUsername;
                    type = 0;
                }

                if (studentNumber.length > 0) {
                    student = studentNumber;
                    type = 1;
                }

                // 检验学生信息
                Messenger().run({
                    errorMessage: '请求失败'
                }, {
                    url: web_path + ajax_url.valid_student,
                    type: 'post',
                    data: {student: student, type: type},
                    success: function (data) {
                        if (data.state) {
                            accessApply($('#studentInfoInternshipReleaseId').val(),data.objectResult);
                        } else {
                            validErrorDom(validId.student,errorMsgId.student,data.msg);
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

        /*
         学生 form 确定
         */
        $('#studentInfo').click(function () {
            validStudent();
        });

        init();

        /**
         * 初始化数据
         */
        function init() {
            startLoading(tableData);
            $.get(web_path + ajax_url.internship_apply_data_url, param, function (data) {
                endLoading(tableData);
                if (data.listResult.length > 0) {
                    createPage(data);
                }
                listData(data);
            });
        }

        /**
         * 创建分页
         * @param data 数据
         */
        function createPage(data) {
            $('#pagination').pagination({
                pages: data.paginationUtils.totalPages,
                displayedPages: data.paginationUtils.displayedPages,
                hrefTextPrefix: '',
                prevText: '上一页',
                nextText: '下一页',
                cssStyle: '',
                listStyle: 'pagination',
                onPageClick: function (pageNumber, event) {
                    // Callback triggered when a page is clicked
                    // Page number is given as an optional parameter
                    console.log(pageNumber);
                    nextPage(pageNumber);
                }
            });
        }

        /**
         * 下一页
         * @param pageNumber 当前页
         */
        function nextPage(pageNumber) {
            param.pageNum = pageNumber;
            startLoading(tableData);
            $.get(web_path + ajax_url.internship_apply_data_url, param, function (data) {
                endLoading(tableData);
                listData(data);
            });
        }

    });