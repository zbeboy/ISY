/**
 * Created by zbeboy on 2017/6/22.
 */
define(function () {
    return function (size) {
        var str = "";
        if (size < 1024) {
            str = size + "B";
        } else if (size >= 1024 && size < 1024 * 1024) {
            str = (size / 1024).toFixed(2) + "KB";
        } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
            str = (size / (1024 * 1024)).toFixed(2) + "MB";
        } else {
            str = (size / (1024 * 1024 * 1024)).toFixed(2) + "GB";
        }

        return str;
    }
});