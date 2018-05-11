import axios from 'axios'
import router from '../router'

import iView from 'iview'

// http request 拦截器
axios.interceptors.request.use(
  config => {
    return config;
  },
  err => {
    return Promise.reject(err);
  }
)

// http response 拦截器
axios.interceptors.response.use(
  response => {
    if (response.data.response_code == '200005') {
      location.assign('https://sso-test.icekredit.com/login?redirect_uri=' + encodeURIComponent(window.location.href))
    }
    return response;
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 403:
          location.assign('https://sso-test.icekredit.com/login?redirect_uri=' + encodeURIComponent(window.location.href))
      }
    }
    return Promise.reject(error.response.data)
  }
)

let httpMethod = {

  getService: (url, no_loading) => {

    if (no_loading) {
      return new Promise((resolve, reject) => {
        axios
          .get(url)
          .then((response) => {
            if (response.data) {
              resolve(response.data)
            }
          })
          .catch((error) => {
            resolve({
              'response_code': '500',
              'message': '网络错误'
            })
          })
      })
    }

    return new Promise((resolve, reject) => {
      iView.LoadingBar.start()
      axios
        .get(url)
        .then((response) => {
          if (response.data) {
            resolve(response.data)
            iView.LoadingBar.finish()
          }
        })
        .catch((error) => {
          resolve({
            'response_code': '500',
            'message': '网络错误'
          })
          iView.LoadingBar.error()
        })
    })

  },
  postService: (url, data, no_loading) => {

    if (no_loading) {
      return new Promise((resolve, reject) => {
        axios
          .post(url, data)
          .then((response) => {
            if (response.data) {
              resolve(response.data)
            }
          })
          .catch((error) => {
            resolve({
              'response_code': '500',
              'message': '网络错误'
            })
          })
      })
    }

    return new Promise((resolve, reject) => {
      iView.LoadingBar.start()
      axios
        .post(url, data)
        .then((response) => {
          if (response.data) {
            resolve(response.data)
            iView.LoadingBar.finish()
          }
        })
        .catch((error) => {
          resolve({
            'response_code': '500',
            'message': '网络错误'
          })
          iView.LoadingBar.error()
        })
    })
  }
}

export default httpMethod
