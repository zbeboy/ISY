/**
 * Created by lenovo on 2016-12-12.
 */
//# sourceURL=internship_change_history.js
require(["jquery", "handlebars", "jquery.address", "css!" + web_path + "/css/custom/timeline.css"],
    function ($, Handlebars) {

        /*
         ajax url.
         */
        var ajax_url = {
            data_url: '/web/internship/statistical/record/apply/data',
            back: '/web/internship/statistical/submitted'
        };

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.submitted_url + "?id=" + init_page_param.internshipReleaseId);
        });

        function startLoading() {
            // 显示遮罩
            $('#page-wrapper').showLoading();
        }

        function endLoading() {
            // 去除遮罩
            $('#page-wrapper').hideLoading();
        }

        /**
         * 列表数据
         * @param data 数据
         */
        function listData(data) {
            var source = $("#timeline-template").html();
            var template = Handlebars.compile(source);

            Handlebars.registerHelper('timeline_state_css', function () {
                var value = Handlebars.escapeExpression(badgeCss(this.state));
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('icon', function () {
                var value = Handlebars.escapeExpression(iconCss(this.state));
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('internship_title', function () {
                var value = Handlebars.escapeExpression(this.internshipTitle);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('time', function () {
                var value = Handlebars.escapeExpression(this.applyTime);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('organize_name', function () {
                var value = Handlebars.escapeExpression(this.organizeName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('real_name', function () {
                var value = Handlebars.escapeExpression(this.realName);
                return new Handlebars.SafeString(value);
            });

            Handlebars.registerHelper('state', function () {
                var value = Handlebars.escapeExpression(internshipApplyStateCode(this.state));
                return new Handlebars.SafeString(value);
            });

            var html = template(data);
            $('#timeData').html(html);
        }

        /**
         * 显示css表
         * @param state 状态码
         * @returns {string}
         */
        function badgeCss(state) {
            var css = '';
            switch (state) {
                case 0:
                    css = '';
                    break;
                case 1:
                    css = 'info';
                    break;
                case 2:
                    css = 'success';
                    break;
                case 3:
                    css = 'danger';
                    break;
                case 4:
                    css = 'info';
                    break;
                case 5:
                    css = 'warning';
                    break;
                case 6:
                    css = 'info';
                    break;
                case 7:
                    css = 'warning';
                    break;
                case -1:
                    css = 'danger';
                    break;
            }
            return css;
        }

        /**
         * icon表
         * @param state 状态码
         * @returns {string}
         */
        function iconCss(state) {
            var css = '';
            switch (state) {
                case 0:
                    css = 'fa fa-clock-o';
                    break;
                case 1:
                    css = 'fa fa-eye';
                    break;
                case 2:
                    css = 'fa fa-check';
                    break;
                case 3:
                    css = 'fa fa-times';
                    break;
                case 4:
                    css = 'fa fa-eye';
                    break;
                case 5:
                    css = 'fa fa-pencil';
                    break;
                case 6:
                    css = 'fa fa-eye';
                    break;
                case 7:
                    css = 'fa fa-pencil';
                    break;
                case -1:
                    css = 'fa fa-times';
                    break;
            }
            return css;
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
                case -1:
                    msg = '撤消';
                    break;
            }
            return msg;
        }


        init();

        /**
         * 初始化数据
         */
        function init() {
            startLoading();
            $.get(web_path + ajax_url.data_url, init_page_param, function (data) {
                endLoading();
                listData(data);
            });
        }

    });