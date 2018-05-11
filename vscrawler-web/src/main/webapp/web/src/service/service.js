import httpMethod from './method'
import md5 from 'js-md5'

let { getService, postService } = httpMethod

const url = '/api'

let request = {

    /**
     * 获取验证码
     * @return {[type]} [description]
     */
    getCaptcha: () => {
        return '/auth/captcha?r=' + Math.random()
    },

    /**
     * 登录
     * @param  {[type]} data [description]
     * @return {[type]}      [description]
     */
    Login: (data) => postService('/auth/login.do?merchant_name=' + data.merchant_name + '&merchant_pwd=' + md5(data.merchant_pwd) + '&captcha=' + data.captcha, undefined, true),

    getCrawler: () => getService(`${url}/crawler/crawlerStatus`),

}

export default request