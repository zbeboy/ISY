/**
 * Created by zbeboy on 2017/6/22.
 */
//# sourceURL=graduate_design_release_edit.js.js
require(["jquery", "handlebars", "nav_active", "files", "messenger", "jquery.address", "jquery.showLoading"],
    function ($, Handlebars, nav_active, files) {

        /*
         ajax url.
         */
        var ajax_url = {
            graduate_design_release_files_url: '/user/graduate/design/files',
            download_file: '/anyone/users/download/file',
            back: '/web/menu/graduate/design/proposal'
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

        /*
         返回
         */
        $('#page_back').click(function () {
            $.address.value(ajax_url.back);
        });

        init();

        function init() {
            startLoading();
            $.get(web_path + ajax_url.graduate_design_release_files_url,
                {graduationDesignReleaseId: init_page_param.graduationDesignReleaseId}, function (data) {
                    endLoading();
                    initFileShow(data);
                });
        }

        /**
         * 文件显示
         * @param data 数据
         */
        function initFileShow(data) {
            var template = Handlebars.compile($("#file-template").html());

            Handlebars.registerHelper('original_file_name', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.originalFileName));
            });

            Handlebars.registerHelper('size', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(files(this.size)));
            });

            Handlebars.registerHelper('ext', function () {
                return new Handlebars.SafeString(Handlebars.escapeExpression(this.ext));
            });
            $('#fileShow').append(template(data));
        }

        /*
         下载附件
         */
        $('#fileShow').delegate('.downloadfile', "click", function () {
            var id = $(this).attr('data-file-id');
            window.location.href = web_path + ajax_url.download_file + '?fileId=' + id;
        });

    });