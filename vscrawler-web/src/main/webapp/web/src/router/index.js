import Vue from 'vue'
import Router from 'vue-router'

import routes from './router'

// use路由
Vue.use(Router)

// 配置路由
export default new Router({
  routes: routes
})
