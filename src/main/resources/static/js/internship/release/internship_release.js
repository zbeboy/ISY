/**
 * Created by lenovo on 2016-11-10.
 */
//# sourceURL=internship_release.js
require(["jquery", "handlebars", "messenger", "jquery.address", "jquery.simple-pagination", "jquery.showLoading"],
    function ($, Handlebars) {

        /*
         ajax url.
         */
        var ajax_url = {
            internship_release_data_url: '/web/internship/release/data',
            add: '/web/internship/release/add',
            edit: '/web/internship/release/edit',
            updateDel: '/web/internship/release/update/del'
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
        web storage key.
        */
        var webStorageKey = {
            INTERNSHIP_TITLE: 'INTERNSHIP_RELEASE_TITLE_SEARCH'
        };

        /*
         参数id
         */
        var paramId = {
            internshipTitle: '#search_internship_title'
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
            $(paramId.internshipTitle).val('');
        }

        /**
         * 刷新查询参数
         */
        function refreshSearch() {
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.INTERNSHIP_TITLE, $(paramId.internshipTitle).val());
            }
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

        /*
         发布
         */
        $('#release').click(function () {
            $.address.value(ajax_url.add);
        });

        /**
         * 列表数据
         * @param data 数据
         */
        function listData(data) {
            var template = Handlebars.compile($("#internship-release-template").html());

            Handlebars.registerHelper('internship_title', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.internshipTitle));
            });

            Handlebars.registerHelper('school_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.schoolName));
            });

            Handlebars.registerHelper('college_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.collegeName));
            });

            Handlebars.registerHelper('department_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.departmentName));
            });

            Handlebars.registerHelper('real_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.realName));
            });

            $(tableData).html(template(data));
        }

        /*
         编辑
         */
        $(tableData).delegate('.edit', "click", function () {
            $.address.value(ajax_url.edit + '?id=' + $(this).attr('data-id'));
        });

        /*
         注销
         */
        $(tableData).delegate('.del', "click", function () {
            internshipReleaseDel($(this).attr('data-id'), $(this).attr('data-name'));
        });

        /*
         恢复
         */
        $(tableData).delegate('.recovery', "click", function () {
            internshipReleaseRecovery($(this).attr('data-id'), $(this).attr('data-name'));
        });

        /**
         * 注销确认
         * @param id 实习发布id
         * @param name 标题
         */
        function internshipReleaseDel(id, name) {
            var msg;
            msg = Messenger().post({
                message: "确定注销实习发布 '" + name + "' 吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            del(id);
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
         * 恢复确认
         * @param id 实习发布id
         * @param name 标题
         */
        function internshipReleaseRecovery(id, name) {
            var msg;
            msg = Messenger().post({
                message: "确定恢复实习发布 '" + name + "' 吗?",
                actions: {
                    retry: {
                        label: '确定',
                        phrase: 'Retrying TIME',
                        action: function () {
                            msg.cancel();
                            recovery(id);
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
         * 注销
         * @param id
         */
        function del(id) {
            sendUpdateDelAjax(id, "注销", 1);
        }

        /**
         * 恢复
         * @param id
         */
        function recovery(id) {
            sendUpdateDelAjax(id, "恢复", 0);
        }

        /**
         * 注销或恢复ajax
         * @param internshipReleaseId
         * @param message
         * @param isDel
         */
        function sendUpdateDelAjax(internshipReleaseId, message, isDel) {
            Messenger().run({
                successMessage: message + '实习发布成功',
                errorMessage: message + '实习发布失败',
                progressMessage: '正在' + message + '实习发布....'
            }, {
                url: web_path + ajax_url.updateDel,
                type: 'post',
                data: {internshipReleaseId: internshipReleaseId, isDel: isDel},
                success: function (data) {
                    if (data.state) {
                        init();
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
        initSearchInput();

        /**
         * 初始化数据
         */
        function init() {
            initSearchContent();
            startLoading();
            $.get(web_path + ajax_url.internship_release_data_url, param, function (data) {
                endLoading();
                createPage(data);
                listData(data);
            });
        }

        /*
      初始化搜索内容
       */
        function initSearchContent() {
            var internshipTitle = null;
            var params = {
                internshipTitle: ''
            };
            if (typeof(Storage) !== "undefined") {
                internshipTitle = sessionStorage.getItem(webStorageKey.INTERNSHIP_TITLE);
            }
            if (internshipTitle !== null) {
                params.internshipTitle = internshipTitle;
            } else {
                params.internshipTitle = $(paramId.internshipTitle).val();
            }
            param.pageNum = 0;
            param.searchParams = JSON.stringify(params);
        }

        /*
       初始化搜索框
        */
        function initSearchInput() {
            var internshipTitle = null;
            if (typeof(Storage) !== "undefined") {
                internshipTitle = sessionStorage.getItem(webStorageKey.INTERNSHIP_TITLE);
            }
            if (internshipTitle !== null) {
                $(paramId.internshipTitle).val(internshipTitle);
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
            $.get(web_path + ajax_url.internship_release_data_url, param, function (data) {
                endLoading();
                listData(data);
            });
        }

    });