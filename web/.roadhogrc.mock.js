import menu from './mock/menu';
import dashboard from './mock/dashboard';
import login from './mock/login';
import users from './mock/users';

export default {
  ...menu,
  ...login,
  ...dashboard,
  ...users,
  // 支持值为 Object 和 Array
  'GET /mockapi/test': { testers: [1,2,3] },

  // GET POST 可省略
  '/mockapi/test/1': { id: 1 },

  // 支持自定义函数，API 参考 express@4
  'POST /mockapi/test/add': (req, res) => { res.end('OK'); },

  'POST /mockapi/test/add/:id': (req, res) => {
  	res.end(req.param('id'));
   },
};