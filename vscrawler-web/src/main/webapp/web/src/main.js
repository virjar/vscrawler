// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'

import store from './store/'

import iView from 'iview'
import 'iview/dist/styles/iview.css'
import './assets/main.less'

// 全局注册过滤器
import * as filters from './filters'
Vue.prototype.filters = filters

// 全局注册服务
import service from './service'
Vue.prototype.service = service

Vue.use(iView)

Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: {
    App
  }
})
