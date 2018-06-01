/**
 * Created by lenovo on 2016-12-12.
 */
//# sourceURL=internship_change_history.js
require(["jquery", "handlebars", "nav_active", "jquery.address", "css!" + web_path + "/css/custom/timeline.css"],
    function ($, Handlebars, nav_active) {

        /*
         ajax url.
         */
        var ajax_url = {
            data_url: '/web/internship/statistical/record/apply/data',
            back: '/web/internship/statistical/submitted',
            nav: '/web/menu/internship/statistical'
        };

        // 刷新时选中菜单
        nav_active(ajax_url.nav);

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back + "?id=" + init_page_param.internshipReleaseId);
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
            var template = Handlebars.compile($("#timeline-template").html());
            var count = 0;

            Handlebars.registerHelper('timeline_state_css', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(badgeCss(this.state)));
            });

            Handlebars.registerHelper('icon', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(iconCss(this.state)));
            });

            Handlebars.registerHelper('internship_title', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.internshipTitle));
            });

            Handlebars.registerHelper('time', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.applyTimeStr));
            });

            Handlebars.registerHelper('organize_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.organizeName));
            });

            Handlebars.registerHelper('real_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.realName));
            });

            Handlebars.registerHelper('state', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(internshipApplyStateCode(this.state)));
            });

            Handlebars.registerHelper('inverted', function () {
                var value = '';
                if (count % 2 === 0) {
                    value = Handlebars.escapeExpression('timeline-inverted');
                } else {
                    value = Handlebars.escapeExpression('');
                }
                count++;
                return new Handlebars.SafeString(value);
            });
            $('#timeData').html(template(data));
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