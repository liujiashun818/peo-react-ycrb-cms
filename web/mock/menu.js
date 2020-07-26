const menu = [
  /*{
    key: 'dashboard',
    name: '仪表盘',
    icon: 'laptop',
    href:"/dashboard.html",
  },*/
  {
    key: 'cms',
    name: '内容管理',
    icon: 'bars',
    child: [
        {
          key: 'category',
          name: '栏目管理',
          icon: 'bars',
          child: [
            {
              key: 'list',
              name: '栏目列表',
              icon: 'bars'
            },
            {
              key: 'edit',
              name: '栏目编辑',
              icon: 'edit'
            }
          ]
        },
        {
          key: 'articles',
          name: '文章管理',
          icon: 'bars',
          child: [
            {
              key: 'list2',
              name:'文章列表',
              icon: 'bars'
            },
            {
              key: 'edit2',
              name: '文章编辑',
              icon: 'edit'
            }
          ]
        }
    ]
  },
  {
    key: 'users',
    name: '用户管理',
    icon: 'user',
    href:"/users.html",
  },
  {
    key: 'example',
    name: '示例模块',
    icon: 'laptop',
    href:"/example.html",
    child: [
      {
        key: 'exa_1',
        name: '示例一'
      },
      {
        key: 'exa_2',
        name: '示例二'
      }
    ]
  },
  {
    key: 'ui',
    name: 'UI组件',
    icon: 'camera-o',
    clickable: false,
    child: [
      {
        key: 'ico',
        name: 'Ico 图标'
      },
      {
        key: 'search',
        name: 'Search 搜索'
      }
    ]
  },
  {
    key: 'navigation',
    name: '测试导航',
    icon: 'setting',
    child: [
      {
        key: 'navigation1',
        name: '二级导航1'
      },
      {
        key: 'navigation2',
        name: '二级导航2',
        child: [
          {
            key: 'navigation21',
            name: '三级导航1'
          },
          {
            key: 'navigation22',
            name: '三级导航2'
          }
        ]
      }
    ]
  },
  {
    key: 'testoutlink',
    name: '外链测试',
    icon: 'link',
    type:"outlink",
    href:"/testoutlink.html"
  }
]

export default {
  'GET /mockapi/menu' (req, res) {
    res.json({data:menu})
  }
}
