//const Ajax = require('robe-ajax');
import axios from 'axios';
import qs from 'qs';
import {URL_PREFIX} from '../constants';

/**
 * Requests a URL, returning a promise.
 *
 * @param  {string} url       The URL we want to request
 * @param  {object} [options] The options we want to pass to "fetch"
 * @return {object}           An object containing either "data" or "err"
 */

// export default function request (url, options,file) {
//     if (options.cross) { //跨域
//         return Ajax.getJSON('http://query.yahooapis.com/v1/public/yql', {
//             q: "select * from json where url='" + url + '?' + Ajax.param(options.data) + "'",
//             format: 'json'
//         });
//     }else {
//         url = url.indexOf('http') >= 0 ? url : URL_PREFIX + url;
//         const opt = {
//             url: url,
//             method: options.method || 'get',
//             data: options.data || {},
//         }
//         if(file){
//             opt.processData = false,
//             opt.contentType = false
//         }else{
//             opt.processData = (options.method === 'get'),
//             opt.dataType = 'JSON';
//             opt.contentType = "application/json; charset=utf-8";
//         }
//         return Ajax.ajax(opt).then((data) => {
//             if (data.code === -4) {
//                 return Promise.reject(new Error('logout'));
//             }else if(data.code === -3 && data.data && data.data.direct && data.data.direct != '') {
//                 window.location.href = data.data.direct;
//             }else if (data.code === -2){
//                 return Promise.reject(new Error('系统错误，请联系管理员'));
//             }else if(data.code < 0){
//                 return Promise.reject(new Error(data.msg));
//             }else{
//                 return data;
//             }
//         });
//     }
// }

export default function request (url, options,file) {
    if (options.cross) { //跨域
        return axios({
            url: 'http://query.yahooapis.com/v1/public/yql',
            method: 'get',
            params: {
                q:  "select * from json where url='" + url + '?' + qs.stringify(options.data) + "'",
                format: 'json'
            }
        }).then(({data}) => {
            return data;
        });
    }else {
        const method = options.method.toLowerCase() || 'get';
        const opts = {
            url,
            method,
            baseURL: URL_PREFIX,
            headers: {'X-Requested-With': 'XMLHttpRequest'}
        };
        let data = options.data;
        data = data && typeof(data) !== 'object' ? JSON.parse(data) : data;
        method === 'get' ? (opts.params = data) : (opts.data = data);
        return axios(opts).then(({data}) => {
            if (data.code === -4) {
                location.href = "dashboard.html"
                return Promise.reject(new Error('logout'));
            }else if(data.code === -10000){
                if(window.Mydispatch){
                    window.Mydispatch({'type':'app/logoutSuccess', payload: {
                            expired: false
                        }})
                }
                return Promise.reject(new Error('请重新登录'));
            }else if(data.code === -3 && data.data && data.data.direct && data.data.direct != '') {
                window.location.href = data.data.direct;
            }else if (data.code === -2){
                return Promise.reject(new Error(data.msg?data.msg:'系统异常，请稍后再试'));
            }else if(data.code < 0){
                window.Mydispatch({'type':'article/reducer:update', payload: {
                    loading: false
                }})
                return Promise.reject(new Error(data.msg));

            }else{
                return data;
            }
        });
    }
}
