/**
 * Created by lenovo on 2016-09-14.
 */
define(function () {
    /*
     去除空格方法(防止IE浏览器不支持)
     */
    String.prototype.trim = function () {
        return this.replace(/(^\s*)|(\s*$)/g, "");
    };
    // 去除左空格
    String.prototype.ltrim = function () {
        return this.replace(/(^\s*)/g, "");
    };
    // 去除右空格
    String.prototype.rtrim = function () {
        return this.replace(/(\s*$)/g, "");
    };
});