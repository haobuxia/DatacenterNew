/****
 * function:通用库
 * depend on : jquery
 * eg :
 */
!function (t) {
        "use strict";
        //字符串format方法 "aa{0}bb{1}".format('x','y')-->"aaxbby"
        String.prototype.format = function () {
            var o = arguments;
            if (o.length == 0) return this.toString();
            o = o.length == 1 && typeof o[0] == "object" ? o[0] : o;
            return this.replace(/{([^{}]+)}/g, function ($0, $1, index, self) {
                return o[$1] != undefined && o[$1] != null ? o[$1] : "";
            });
        },
        String.prototype.replaceAll = function (srcStr, newStr) {
            return this.replace(new RegExp(srcStr, 'gm'), newStr);
        },
        String.prototype.isPhone = function () {
            var phonePattern = /^(13[0-9]\d{8}|15[0-35-9]\d{8}|18[0-9]\d{8}|17[0-9]\d{8}|14[57]\d{8})$/;
            return phonePattern.test(this.toString());
        },
        String.prototype.isMail = function () {
            var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
            return filter.test(this.toString());
        },
        String.prototype.isUrl = function () {
            var filter = /^(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?$/;
            return filter.test(this.toString());
        },
        String.prototype.maskPhone = function (begin, end, maskChar) {
            var str = this.toString();
            var fstStr = str.substring(0, begin);
            var scdStr = str.substring(begin, end);
            var maskStr = "";
            var lstStr = str.substring(end, str.length);
            for (var i = 0; i < scdStr.length; i++) {
                maskStr += maskChar;
            }
            return fstStr + maskStr + lstStr;
        },
        String.prototype.PadLeft = function (totalWidth, paddingChar) {
            if (paddingChar != null) {
                return this.PadHelper(totalWidth, paddingChar, false);
            } else {
                return this.PadHelper(totalWidth, ' ', false);
            }
        },
        String.prototype.PadRight = function (totalWidth, paddingChar) {
            if (paddingChar != null) {
                return this.PadHelper(totalWidth, paddingChar, true);
            } else {
                return this.PadHelper(totalWidth, ' ', true);
            }
        },
        String.prototype.PadHelper = function (totalWidth, paddingChar, isRightPadded) {
            if (this.length < totalWidth) {
                var paddingString = new String();
                for (var i = 1; i <= (totalWidth - this.length); i++) {
                    paddingString += paddingChar;
                }
                if (isRightPadded) {
                    return (this + paddingString);
                } else {
                    return (paddingString + this);
                }

            } else {
                return this;
            }
        },
        /**
         * 获取queryString
         */
        t.queryString = function (n) {
            var reg = new RegExp('(?:^\\?|&)' + n + '=([^&]*)(?:&|$)');
            var match = location.search.match(reg);
            if (match && match.length > 1) {
                return decodeURIComponent(match[1]);
            }
            return "";
        },
        t.newGuid = function () {
            var t = function () {
                return (65536 * (1 + Math.random()) | 0).toString(16).substring(1)
            };
            return t() + t() + "-" + t() + "-" + t() + "-" + t() + "-" + t() + t() + t()
        },
    "function" == typeof define && define.amd && define("utils", [], function () {
        return t
    })
}("undefined" == typeof exports ? window.utils = {} : exports);

