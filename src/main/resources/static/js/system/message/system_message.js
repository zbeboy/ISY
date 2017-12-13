/**
 * Created by lenovo on 2016-12-29.
 */
//# sourceURL=system_message.js
require(["jquery", "handlebars", "messenger", "jquery.address", "jquery.simple-pagination", "jquery.showLoading"],
    function ($, Handlebars) {

        /*
         ajax url.
         */
        var ajax_url = {
            message_data_url: '/anyone/message/data'
        };

        /*
         参数
         */
        var param = {
            searchParams: '',
            pageNum: 0,
            pageSize: 15,
            displayedPages: 3
        };

        /*
        web storage key.
        */
        var webStorageKey = {
            MESSAGE_TITLE: 'SYSTEM_MESSAGE_TITLE_SEARCH'
        };

        /*
         参数id
         */
        var paramId = {
            messageTitle: '#search_message_title'
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
            $(paramId.messageTitle).val('');
        }

        /**
         * 刷新查询参数
         */
        function refreshSearch() {
            if (typeof(Storage) !== "undefined") {
                sessionStorage.setItem(webStorageKey.MESSAGE_TITLE, $(paramId.messageTitle).val());
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

        $(paramId.messageTitle).keyup(function (event) {
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
            var template = Handlebars.compile($("#message-more-template").html());
            Handlebars.registerHelper('real_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.realName));
            });
            $(tableData).html(template(data));
        }

        init();
        initSearchInput();

        /**
         * 初始化数据
         */
        function init() {
            initSearchContent();
            startLoading();
            $.get(web_path + ajax_url.message_data_url, param, function (data) {
                endLoading();
                createPage(data);
                listData(data);
            });
        }

        /*
       初始化搜索内容
        */
        function initSearchContent() {
            var messageTitle = null;
            var params = {
                messageTitle: ''
            };
            if (typeof(Storage) !== "undefined") {
                messageTitle = sessionStorage.getItem(webStorageKey.MESSAGE_TITLE);
            }
            if (messageTitle !== null) {
                params.messageTitle = messageTitle;
            } else {
                params.messageTitle = $(paramId.messageTitle).val();
            }
            param.pageNum = 0;
            param.searchParams = JSON.stringify(params);
        }

        /*
       初始化搜索框
        */
        function initSearchInput() {
            var messageTitle = null;
            if (typeof(Storage) !== "undefined") {
                messageTitle = sessionStorage.getItem(webStorageKey.MESSAGE_TITLE);
            }
            if (messageTitle !== null) {
                $(paramId.messageTitle).val(messageTitle);
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
            $.get(web_path + ajax_url.message_data_url, param, function (data) {
                endLoading();
                listData(data);
            });
        }

    });