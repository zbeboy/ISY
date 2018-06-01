/**
 * Created by zbeboy on 2017/7/7.
 */
require(["jquery", "handlebars", "messenger", "jquery.address", "jquery.simple-pagination", "jquery.showLoading"],
    function ($, Handlebars) {

        /*
         ajax url.
         */
        var ajax_url = {
            release_data_url: '/web/graduate/design/replan/design/data',
            arrange_url: '/web/graduate/design/replan/arrange',
            group_url: '/web/graduate/design/replan/group',
            group_condition: '/web/graduate/design/replan/group/condition',
            order_url: '/web/graduate/design/replan/order',
            divide_url: '/web/graduate/design/replan/divide',
            divide_condition: '/web/graduate/design/replan/divide/condition'
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
            GRADUATION_DESIGN_TITLE: 'GRADUATE_DESIGN_REPLAN_TITLE_SEARCH'
        };

        /*
         参数id
         */
        var paramId = {
            graduationDesignTitle: '#search_title'
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
            $(paramId.graduationDesignTitle).val('');
        }

        /**
         * 刷新查询参数
         */
        function refreshSearch() {
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.GRADUATION_DESIGN_TITLE, $(paramId.graduationDesignTitle).val());
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

        $(paramId.graduationDesignTitle).keyup(function (event) {
            if (event.keyCode === 13) {
                refreshSearch();
                init();
            }
        });

        /**
         * 列表数据
         * @param data 数据
         */
        function listData(data) {
            var template = Handlebars.compile($("#graduation-design-template").html());

            Handlebars.registerHelper('graduation_design_title', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.graduationDesignTitle));
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

            Handlebars.registerHelper('science_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.scienceName));
            });

            Handlebars.registerHelper('publisher', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.publisher));
            });

            $(tableData).html(template(data));
        }

        /*
         设置
         */
        $(tableData).delegate('.design_replan_arrange', "click", function () {
            var id = $(this).attr('data-id');
            $.address.value(ajax_url.arrange_url + '?id=' + id);
        });

        /*
         组管理
         */
        $(tableData).delegate('.design_replan_group', "click", function () {
            var id = $(this).attr('data-id');
            $.post(web_path + ajax_url.group_condition, {id: id}, function (data) {
                if (data.state) {
                    $.address.value(ajax_url.group_url + '?id=' + id);
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
         教师分组
         */
        $(tableData).delegate('.design_replan_divide', "click", function () {
            var id = $(this).attr('data-id');
            $.post(web_path + ajax_url.divide_condition, {id: id}, function (data) {
                if (data.state) {
                    $.address.value(ajax_url.divide_url + '?id=' + id);
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
         生成顺序
         */
        $(tableData).delegate('.design_replan_order', "click", function () {
            var id = $(this).attr('data-id');
            $.post(web_path + ajax_url.group_condition, {id: id}, function (data) {
                if (data.state) {
                    $.address.value(ajax_url.order_url + '?id=' + id);
                } else {
                    Messenger().post({
                        message: data.msg,
                        type: 'error',
                        showCloseButton: true
                    });
                }
            });
        });

        init();
        initSearchInput();

        /**
         * 初始化数据
         */
        function init() {
            initSearchContent();
            startLoading();
            $.get(web_path + ajax_url.release_data_url, param, function (data) {
                endLoading();
                createPage(data);
                listData(data);
            });
        }

        /*
        初始化搜索内容
        */
        function initSearchContent() {
            var graduationDesignTitle = null;
            var params = {
                graduationDesignTitle: ''
            };
            if (typeof(Storage) !== "undefined") {
                graduationDesignTitle = sessionStorage.getItem(webStorageKey.GRADUATION_DESIGN_TITLE);
            }
            if (graduationDesignTitle !== null) {
                params.graduationDesignTitle = graduationDesignTitle;
            } else {
                params.graduationDesignTitle = $(paramId.graduationDesignTitle).val();
            }
            param.pageNum = 0;
            param.searchParams = JSON.stringify(params);
        }

        /*
        初始化搜索框
        */
        function initSearchInput() {
            var graduationDesignTitle = null;
            if (typeof(Storage) !== "undefined") {
                graduationDesignTitle = sessionStorage.getItem(webStorageKey.GRADUATION_DESIGN_TITLE);
            }
            if (graduationDesignTitle !== null) {
                $(paramId.graduationDesignTitle).val(graduationDesignTitle);
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
            $.get(web_path + ajax_url.release_data_url, param, function (data) {
                endLoading();
                listData(data);
            });
        }

    });