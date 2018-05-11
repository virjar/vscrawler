import {

  GET_HEADER

} from './mutation-types.js'


export default {

  [GET_HEADER](state, obj) {
    state.header = obj
  }

}
