import axios from 'axios'
import service from '../service'
import * as types from './mutation-types.js'

/* 异步操作 */
export default {
  /**
   * 获取header
   * @param  {[type]} options.commit [description]
   * @param  {[type]} options.state  [description]
   * @return {[type]}                [description]
   */
  getHeader: ({ commit, state }) => {
    /**
     * 获取数据
     * @param  {[type]} ).then((response [description]
     * @return {[type]}                  [description]
     */
    service.getHeader().then((response) => {
      let header
      if (response.response_code == '00') {
        header = response.content
        commit(types.GET_HEADER, header)
      } else {
        commit(types.GET_HEADER, header)
      }
    })
  }

}
