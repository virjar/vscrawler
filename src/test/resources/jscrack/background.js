/*这是一个chrome插件,通过这个插件注入js代码到浏览器里面,实现js逆向*/
/*参考资料http://www.cnblogs.com/rsail/archive/2012/09/10/2679085.html*/
/*使用方法,chrome扩展程序里面-》加载已解压扩展程序-》选择jscrack文件夹,允许权限即可*/
/**
 * @author virjar@virjar.com
 */
chrome.webRequest.onBeforeRequest.addListener(
    function (details) {
        var url = details.url;
        if (url.indexOf("geetest.5.10.10.js") != -1) {
            return {redirectUrl: chrome.extension.getURL("geetest_new.js")};
        } else if(url.indexOf("vm_login_single_built") != -1){
            return {redirectUrl: chrome.extension.getURL("login_single.js")};
        }
        return true;
    },
    {
        //拦截登陆请求
        urls: ["http://static.geetest.com/static/js/geetest.5.10.10.js"/*登陆加密脚本*/
            ,"http://cache.qixin.com/web/javascripts/viewmodels/vm_login_single_built.20170410.js"
        ]
    },
    ["blocking"] //类型blocking为拦截,
);