Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

var browser = {
    versions: function () {
        var u = navigator.userAgent,
            app = navigator.appVersion;
        return {
            trident: u.indexOf('Trident') > -1, //IE内核
            presto: u.indexOf('Presto') > -1, //opera内核
            webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
            gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1,//火狐内核
            mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
            ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
            android: u.indexOf('Android') > -1 || u.indexOf('Adr') > -1, //android终端
            iPhone: u.indexOf('iPhone') > -1, //是否为iPhone或者QQHD浏览器
            iPad: u.indexOf('iPad') > -1, //是否iPad
            webApp: u.indexOf('Safari') == -1, //是否web应该程序，没有头部与底部
            weixin: u.indexOf('MicroMessenger') > -1, //是否微信 （2015-01-22新增）
            qq: u.match(/\sQQ/i) == " qq" //是否QQ
        };
    }(),
    language: (navigator.browserLanguage || navigator.language).toLowerCase()
}

//设置cookie
function setCookie(cname, cvalue, exdays) {
    var expires = "";
    if (exdays != 0) {
        var d = new Date();
        d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
        expires = "expires=" + d.toUTCString();
    }
    document.cookie = cname + "=" + cvalue + "; " + expires;
}

//获取cookie
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1);
        if (c.indexOf(name) != -1) return c.substring(name.length, c.length);
    }
    return "";
}

//清除cookie
function clearCookie(name) {
    setCookie(name, "", -1);
}

function isPhoneAvailable(mobileVal) {
    var myreg = /^[1][3,4,5,7,8,9][0-9]{9}$/;
    if (!myreg.test(mobileVal)) {
        return false;
    } else {
        return true;
    }
}

var showAlert = function (msg, time, callback) {
    // alert(msg);
    toastr.options = {
                "timeOut": time||3000,
                "closeButton": true,
                "onCloseClick": function(){callback}
            }
    toastr['info'](msg, '' , { positionClass: 'toast-bottom-center' });

    // toastr(msg, time || 3000, callback);
}

// var closeAlert = function () {
//     $(".toast").hide();
// }

var showHideLeftMenu = function (show) {
    if (show) {
        $("#nav-mobile").css("transform", "translateX(0%)");
    } else {
        $("#nav-mobile").css("transform", "translateX(-100%)");
    }
}

var ajaxPost = function (url, data, callback) {
    $.post(url, data, callback, "json");
}
var ajaxGet = function (url, callback) {
    $.get(url, callback, "json");
}
var ajaxHeaderPost = function (url, data, headerMap, callback, failCallback) {
    $.ajax({
        url: url,
        data: data,
        dataType: "json",
        type: "post",
        headers: headerMap,
        cache: false,
        success: callback,
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            if (failCallback) {
                doCallback(failCallback, [XMLHttpRequest, textStatus, errorThrown])
            } else {
                console.error("ajax error." + textStatus + " " + errorThrown);
            }
        }
    });
}

//ajax载入时增加进度条
var enableAjaxSetup = function () {
    $.ajaxSetup({
        beforeSend: function (xMLHttpRequest) {
            $("div.progress").removeClass("hide");
        },
        complete: function (xMLHttpRequest, textStatus) {
            $("div.progress").addClass("hide");
        },
        error: function (xMLHttpRequest, textStatus, errorThrown) {
            $("div.progress").addClass("hide");
            if ("errCode999" == errorThrown) {
                errCode999();
            } else if ("Not Found" == errorThrown) {
                showAlert("请求页面或服务不存在");
            } else {
                if (xMLHttpRequest.responseJSON != null) {
                    var msg = xMLHttpRequest.responseJSON.message;
                    if (msg != undefined) {
                        if(msg.startsWith("权限限制")){
                            showAlert(msg);
                        } else {
                            showAlert("服务器错误:" + msg);
                        }
                    } else {
                        showAlert("服务器错误:" + xMLHttpRequest.responseJSON);
                    }
                } else {
                    var txt = xMLHttpRequest.responseText;

                    var parseJsonErr = false;
                    try {
                        var obj = JSON.parse(txt);
                        showAlert("服务器错误:" + obj.message);
                    } catch (e) {
                        parseJsonErr = true;
                    }
                    if (parseJsonErr) {
                        showAlert("服务器错误:" + txt);
                    }
                }
            }
        }
    });
}

//错误码999表示登录过期
var errCode999 = function () {
    location.href = "/common/login";
}

//激活页面前进后退支持
var enableHistory = function () {
    //chrome、safari可以,ie,firefox不行
    if (window.history && window.history.pushState) {
        $(window).off('popstate').on('popstate', function (e) {
            // console.debug('popstate事件发生，准备载入默认页面.');
            loadDefaultPage();
        });
    }
}

//激活导航按钮样式
var navMenuActive = function (dataLink) {
    var alinks = $("ul.collapsible li a[data-href]");
    var activeSuccess = false;
    $.each(alinks, function (idx, link) {
        var href = $(link).attr("data-href");
        if (href == dataLink || href.indexOf(dataLink) == 0 || dataLink.indexOf(href) == 0) {  //|| sameStart(href,dataLink)
            // console.debug('激活菜单样式.' + idx + ".href=" + href + ",dataLink=" + dataLink);

            //去掉所有li和a上的active属性
            $("#rootMenu li").removeClass("active").removeClass("red");
            $("#rootMenu a").removeClass("active").removeClass("red");
            $("#rootMenu div.collapsible-body").css("display", "none");

            var $link = $(link);
            var linkDirectParentLi = $link.parent("li");
            var linkRootParentLi = $(link).parents("li.bold");
            var linkRootParentLiHeader = linkRootParentLi.find("a.collapsible-header");
            var linkRootParentLiBody = linkRootParentLi.find("div.collapsible-body");

            //在激活链接的直属父亲li和上级菜单li、上级菜单li的header链接上都添加active
            linkDirectParentLi.addClass("active").addClass("red");
            linkRootParentLi.addClass("active");
            linkRootParentLiHeader.addClass("active");
            linkRootParentLiBody.css("display", "block");

            var first = linkRootParentLiHeader.text();
            var second = $link.text();
            activeSuccess = true;
            return false;//break;
        }
    });

    if (!activeSuccess) {
        //未能激活
        $(".breadcrumb:gt(0)").hide();
    }
}


var navMobileWidth = "200px";
//进入全屏
function requestFullScreen() {
    var de = document.documentElement;
    if (de.requestFullscreen) {
        de.requestFullscreen();
    } else if (de.mozRequestFullScreen) {
        de.mozRequestFullScreen();
    } else if (de.webkitRequestFullScreen) {
        de.webkitRequestFullScreen();
    } else {
        showAlert("浏览器不支持全屏");
        return;
    }

    navMobileWidth = $("#nav-mobile").css("width");
    $("#nav-mobile").hide();
    $("main").css("padding-left", "10px");
    $("header").css("padding-left", "10px");

    $("#opeFullScreenBtn").hide();
    $("#closeFullScreenBtn").show();
    $("header div.nav-wrapper").append("");
}

//退出全屏
function exitFullscreen() {
    var de = document;
    if (de.exitFullscreen) {
        de.exitFullscreen();
    } else if (de.mozCancelFullScreen) {
        de.mozCancelFullScreen();
    } else if (de.webkitCancelFullScreen) {
        de.webkitCancelFullScreen();
    } else {
        showAlert("浏览器不支持全屏");
        return;
    }

    $("header div.nav-wrapper").append("");
    $("main").css("padding-left", navMobileWidth);
    $("header").css("padding-left", navMobileWidth);

    $("#opeFullScreenBtn").show();
    $("#closeFullScreenBtn").hide();
    $("#nav-mobile").show();
}

//载入登录后首页右侧的默认页面
var lastManualSetHash = '';//记录页面载入后自动设置的hash值避免popstate触发页面二次刷新
var loadDefaultPage = function () {
    //如果当前页面不是首页则载入对应页面否则载入首页
    var hash = window.location.hash;
    if (hash.length > 0) {
        // console.debug('hash存在.');
        hash = hash.substring(1);//去掉#号
        if (hash.length > 0 && hash.substring(0, 1) != "!") {
            // console.debug('hash存在.hash='+hash);
            if (hash != lastManualSetHash) {
                //非人工设置的hash才自动载入
                // console.debug('hash不是人工设置的，则载入hash的页面:'+hash);
                var hashParts = hash.split("#");
                var firstHash = hashParts[0];
                // var success = navMenuActive(firstHash);
                if (hashParts.length > 1) {
                    var secondHash = hashParts[1];
                    // console.debug('载入2级hash的页面:'+secondHash);
                    loadMainContent(secondHash);
                } else {
                    // console.debug('载入1级hash的页面:'+firstHash);
                    loadMainContent(firstHash);
                }
            } else {
                // console.debug('hash是人工设置的，则不载入indexContent页面内容。'+hash);
            }
        }
    } else {
        // console.debug('页面没有hash.载入首页indexContent内容.hash='+hash);
        if ($("#rootMenu").length > 0) {
            var defaultUrl = "/personal/index";
            loadMainContent(defaultUrl);
        }
    }
}

var updateBreadcrumb = function (first, second) {
    window.setTimeout(function () {
        $(".breadcrumb").show();
    }, 500);
    $(".breadcrumb:eq(1)").text(first);
    $(".breadcrumb:eq(2)").text(second);

    // $.each($(".breadcrumb"), function (idx, bread) {
    //     if (idx == 1) $(bread).text(first);
    //     if (idx == 2) $(bread).text(second);
    // });
}

var mainContentUnloadListeners = [];
var addMainContentUnloadListener = function (callback) {
    mainContentUnloadListeners.push(callback);
}

var loadMainContent = function (url,callbackFunc) {
    //清除页面定时器
    // $.each(_intervalIdArray,function(idx,intervalId){
    //     window.clearInterval(intervalId);
    // });
    // _intervalIdArray = [];

    $.each(mainContentUnloadListeners, function (idx, listener) {
        try {
            doCallback(listener);
        } catch (e) {
        }
    });
    mainContentUnloadListeners = [];

    if (url == "" || url == "/" || url == "/index" || url == "index")
        url = "/console/welcome";
    loadContent("main", url, function () {
        // console.debug('main内容载入完毕。url='+url);
        navMenuActive(url);
        updateLocationHash(url);
        if(callbackFunc != null){
            doCallback(callbackFunc);
        }
    });
};

var updateLocationHash = function (url) {
    var newHash = url.replace(/\.\.\//g, "");
    if (newHash.indexOf("?") >= 0) {
        newHash = newHash.substring(0, newHash.indexOf("?"));
    }
    lastManualSetHash = newHash;
    document.location.hash = newHash;
    // console.debug('设置页面和手工设置的hash值为:'+newHash+",载入的页面url:"+url);
}

//部分区域数据载入
var loadContent = function (selector, url, callback) {
    $(selector).load(url, function (html) {
        if (callback) {
            doCallback(callback, html);
        }
    });
}

var getElementIntHeight = function (eleSelector) {
    var ele = $(eleSelector);
    var height = ele.css("height");
    if (height && height.toLowerCase().indexOf("px") == height.length - 2) {
        return height.substring(0, height.length - 2) * 1;
    }
    return 0;
}

//执行函数回调
var doCallback = function (callback, args) {
    var argsType = $.type(args);
    if (argsType == null || argsType == "undefined" || argsType == 'null') {
        eval(callback).apply(this);
    } else if ($.isArray(args)) {
        eval(callback).apply(this, args);
    } else {
        eval(callback).apply(this, [args]);
    }
}

var loadJs = function (jsUrl, callback) {
    $.getScript(jsUrl)
        .done(function () {
            doCallback(callback)
        })
        .fail(function () {
        });
}

$(function () {
    enableHistory();
    loadDefaultPage();
    enableAjaxSetup();
});