const Cookie = require('js-cookie')

let AdminUsers = [
  {
    username: 'guest',
    password: 'guest'
  },
  {
    username: '吴彦祖',
    password: '123456'
  }
]

module.exports = {
  'POST /mockapi/login' (req, res) {
    const userItem = {
      username: 'guest',
      password: 'guest'
    }
    const response = {
      success: false,
      message: ''
    }
    const d = AdminUsers.filter(function (item) {
      return item.username === userItem.username
    })
    if (d.length) {
      if (d[0].password === userItem.password) {
        const now = new Date()
        now.setDate(now.getDate() + 1)
        Cookie.set('user_session', now.getTime())
        Cookie.set('user_name', userItem.username)
        response.message = '登录成功'
        response.success = true
      } else {
        response.message = '密码不正确'
      }
    } else {
      response.message = '用户不存在'
    }
    res.json(response)
  },

  'GET /mockapi/userInfo' (req, res) {
    const response = {
      success: Cookie.get('user_session') && Cookie.get('user_session') > new Date().getTime(),
      username: Cookie.get('user_name') || 'guest',
      message: 'ok'
    }
    res.json(response)
  },

  'POST /mockapi/logout' (req, res) {
    Cookie.remove('user_session', { path: '' })
    Cookie.remove('user_name', { path: '' })
    res.json({
      success: true,
      message: '退出成功'
    })
  }
}