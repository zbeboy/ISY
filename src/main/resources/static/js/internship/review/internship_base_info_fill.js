/**
 * Created by lenovo on 2016-12-10.
 */
//# sourceURL=internship_base_info_fill.js
require(["jquery", "handlebars", "nav_active", "messenger", "jquery.address", "jquery.simple-pagination", "jquery.showLoading"],
    function ($, Handlebars, nav_active) {

        /*
         ajax url.
         */
        var ajax_url = {
            base_info_fill_url: '/web/internship/review/base_info_fill/data',
            audit_pass_url: '/web/internship/review/audit/pass',
            science_data_url: '/web/internship/review/audit/sciences',
            organize_data_url: '/web/internship/review/audit/organizes',
            back: '/web/menu/internship/review'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.back);

        /*
         参数id
         */
        var paramId = {
            studentName: '#search_real_name',
            studentNumber: '#search_student_number',
            scienceName: '#search_science_name',
            organizeName: '#search_organize_name'
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

        var tableData = '#tableData';

        function startLoading() {
            // 显示遮罩
            $('#page-wrapper').showLoading();
        }

        function endLoading() {
            // 去除遮罩
            $('#page-wrapper').hideLoading();
        }

        /*
         清空参数
         */
        function cleanParam() {
            $(paramId.studentName).val('');
            $(paramId.studentNumber).val('');
            $(paramId.scienceName).val('');
            $(paramId.organizeName).val('');
        }

        /**
         * 刷新查询参数
         */
        function refreshSearch() {
            param.pageNum = 0;
        }

        /**
         * 初始化参数
         */
        function initParam() {
            var params = {
                studentName: $(paramId.studentName).val(),
                studentNumber: $(paramId.studentNumber).val(),
                scienceName: $(paramId.scienceName).val(),
                organizeName: $(paramId.organizeName).val(),
                internshipReleaseId: init_page_param.internshipReleaseId
            };
            param.searchParams = JSON.stringify(params);
        }

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

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back);
        });

        $(paramId.studentName).keyup(function (event) {
            if (event.keyCode == 13) {
                refreshSearch();
                init();
            }
        });

        $(paramId.studentNumber).keyup(function (event) {
            if (event.keyCode == 13) {
                refreshSearch();
                init();
            }
        });

        $(paramId.scienceName).change(function (event) {
            refreshSearch();
            var science = $(paramId.scienceName).val();
            changeOrganize(science);
            init();
        });

        $(paramId.organizeName).change(function (event) {
            refreshSearch();
            init();
        });

        /**
         * 列表数据
         * @param data 数据
         */
        function listData(data) {
            var source = $("#internship-audit-template").html();
            var template = Handlebars.compile(source);
            Handlebars.registerHelper('student_name', function () {
                var value = Handlebars.escapeExpression(this.studentName);
                return new Handlebars.SafeString(value);
            });
            Handlebars.registerHelper('science_name', function () {
                var value = Handlebars.escapeExpression(this.scienceName);
                return new Handlebars.SafeString(value);
            });
            Handlebars.registerHelper('organize_name', function () {
                var value = Handlebars.escapeExpression(this.organizeName);
                return new Handlebars.SafeString(value);
            });
            Handlebars.registerHelper('internship_apply_state', function () {
                var value = Handlebars.escapeExpression(internshipApplyStateCode(this.internshipApplyState));
                return new Handlebars.SafeString(value);
            });
            Handlebars.registerHelper('reason', function () {
                var value = Handlebars.escapeExpression(this.reason);
                return new Handlebars.SafeString(value);
            });
            var html = template(data);
            $(tableData).html(html);
        }

        /**
         * 状态码表
         * @param state 状态码
         * @returns {string}
         */
        function internshipApplyStateCode(state) {
            var msg = '';
            switch (state) {
                case 0:
                    msg = '未提交';
                    break;
                case 1:
                    msg = '审核中...';
                    break;
                case 2:
                    msg = '已通过';
                    break;
                case 3:
                    msg = '未通过';
                    break;
                case 4:
                    msg = '基本信息变更审核中...';
                    break;
                case 5:
                    msg = '基本信息变更填写中...';
                    break;
                case 6:
                    msg = '单位信息变更申请中...';
                    break;
                case 7:
                    msg = '单位信息变更填写中...';
                    break;
            }
            return msg;
        }

        /*
         通过
         */
        $(tableData).delegate('.pass_apply', "click", function () {
            var id = $(this).attr('data-id');
            var studentId = $(this).attr('data-student');
            var studentName = $(this).attr('data-name');
            ask(id, studentId, '', 2, studentName, $(this), ajax_url.audit_pass_url);
        });

        /**
         * 状态修改询问
         * @param internshipReleaseId 实习发布id
         * @param studentId 学生id
         * @param reason 原因
         * @param state 状态
         * @param studentName 学生名
         * @param obj 当前dom对象
         * @param url 提交url
         */
        function ask(internshipReleaseId, studentId, reason, state, studentName, obj, url) {
            var msg;
            msg = Messenger().post({
                message: "确定通过学生 '" + studentName + "'  吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            sendAjax(internshipReleaseId, studentId, reason, state, obj, url);
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
         * 发送数据到后台
         */
        function sendAjax(internshipReleaseId, studentId, reason, state, obj, url) {
            Messenger().run({
                successMessage: '保存数据成功',
                errorMessage: '保存数据失败',
                progressMessage: '正在保存数据....'
            }, {
                url: web_path + url,
                type: 'post',
                data: {
                    internshipReleaseId: internshipReleaseId,
                    studentId: studentId,
                    reason: reason,
                    internshipApplyState: state
                },
                success: function (data) {
                    if (data.state) {
                        obj.parent().parent().remove();
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

        init();
        initSearchSciences();

        /**
         * 初始化数据
         */
        function init() {
            startLoading();
            initParam();
            $.post(web_path + ajax_url.base_info_fill_url, param, function (data) {
                endLoading();
                if (data.listResult.length > 0) {
                    createPage(data);
                }
                listData(data);
            });
        }

        /**
         * 初始化专业数据
         */
        function initSearchSciences() {
            $.post(web_path + ajax_url.science_data_url, {internshipReleaseId: init_page_param.internshipReleaseId}, function (data) {
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
                $(paramId.scienceName).html(html);
            });
        }

        /**
         * 改变班级选项
         * @param science 专业
         */
        function changeOrganize(science) {

            if (science === 0) {
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
                $.post(web_path + ajax_url.organize_data_url, {scienceId: science}, function (data) {
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
                    $(paramId.organizeName).html(html);
                });
            }
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
            startLoading();
            $.get(web_path + ajax_url.base_info_fill_url, param, function (data) {
                endLoading();
                listData(data);
            });
        }

    });