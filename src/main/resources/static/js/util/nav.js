/**
 * Created by lenovo on 2016-09-14.
 */
define(["jquery", "sb-admin"], function ($) {
        // start 小屏幕修复收缩菜单
        topOffset = 50;
        width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
        if (width < 768) {
            $('div.navbar-collapse').addClass('collapse');
            topOffset = 100; // 2-row-menu
        } else {
            $('div.navbar-collapse').removeClass('collapse');
        }

        height = ((this.window.innerHeight > 0) ? this.window.innerHeight : this.screen.height) - 1;
        height = height - topOffset;
        if (height < 1) height = 1;
        if (height > topOffset) {
            $("#page-wrapper").css("min-height", (height) + "px");
        }
        // end

        // 修复三级菜单用
        var url = window.location;
        var element = $('ul.nav a').filter(function () {

            var subStr = '';
            if(this.href !== '' && this.href.indexOf('#') !== -1){
                subStr = this.href.substring(this.href.lastIndexOf('#') + 1);
            }

            return this.href === url || (subStr === url && subStr !== '');

        }).parent().parent().parent();
        var thirdParent = element.parent();
        if (thirdParent.is('ul')) {
            thirdParent.addClass('in');
            thirdParent.parent().addClass('active');
        }

        // 菜单搜索
        /**
         * @return {boolean}
         */
        $.expr[":"].Contains = function (a, i, m) {
            return (a.textContent || a.innerText || "").toUpperCase().indexOf(m[3].toUpperCase()) >= 0;
        };
        function filterList(header, list) {
            // @header 头部元素
            // @list 无需列表
            // 创建一个搜素表单
            var form = $("<form>").attr({
                "class": "filterform",
                action: "#"
            }), input = $("<input>").attr({
                "class": "filterinput form-control",
                type: "text",
                placeholder: "Search..."
            });
            $(form).append(input).appendTo(header);
            $(input).change(function () {
                var filter = $(this).val();
                if (filter) {
                    $matches = $(list).find("a:Contains(" + filter + ")").parent();
                    $("li", list).not($matches).css('display', 'none');
                    $matches.parents("li").css('display', 'block');
                    $matches.css('display', 'block');
                } else {
                    $(list).find("li").css('display', 'block');
                }
                return false;
            }).keyup(function () {
                $(this).change();
            });
        }

        filterList($("#form"), "#side-menu");
    }
);