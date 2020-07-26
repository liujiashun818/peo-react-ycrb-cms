## Theone
[BUG汇总](http://10.3.37.65/jira/browse/SXRBAPP-1)

> 可定制的内容管理系统，统一支持网站和客户端应用

#### 系统结构

整体结构划分为以下几个模块：

- core：包含通用基类base、基础配置config和通用工具类util（已经分离出其他工程）
- service：包含数据数据访问层dao和业务层，同时有划分为多个业务模块modules，在演进过程中要符合以下几个基本要求：
  - 未来可以拆分成多个子服务，单独部署
  - 可以简单配置之后提供dubbo接口
  - 可以与web集成部署
- web：管理端展现层接口，并集成reactjs，进行前后端分离的开发，基于service提供业务
- appapi：对展现端app端的api接口，使用thrift框架提供，供php接口层调用



#### 系统模块

| 名称      | 编号       | 说明                                       |
| ------- | -------- | ---------------------------------------- |
| 内容管理模块  | cms      | 栏目管理(category)、文章管理(article)、直播管理(live)、评论管理(comment) |
| 系统管理模块  | sys      | 包含系统菜单管理(menu)、字典管理(dict)、字段(field)和字段组(fieldGroup) |
| 用户管理模块  | users    | 包含机构管理(office)、用户管理(user)、角色管理(role)     |
| 统计分析模块  | analysis | 包含日志管理(log)、统计分析(statistics)             |
| 客户端管理模块 | client      | 包含客户端菜单管理(clientMenu)、开屏图管理(loadingImgs)、生活服务管理（lifeService）、邀请码管理（ activityCode）、意见反馈（guestbook）|
| 个人中心模块 | myCenter      | 包含个人信息（userinfo）修改及其他|
| 问政管理模块 | ask      | 问政的查看、修改、审核等 |
| 新闻爆料模块 | news     | 查看新闻爆料  |
#### 系统文档
- 需求文档  [查看](http://10.3.39.176:62011/银川日报/#g=1&p=更新说明)
- 设计文档  [查看](http://10.100.10.86/ui/theone/#g=1&p=管理母版示例)
- 接口文档  [查看](http://10.3.38.58:8092/swagger-ui.html)

#### 测试环境  [进入](http://10.3.38.58:8090/dashboard.html#/?_k=6jsxd8)


#### 私有NPM使用
- 全局安装cnpm客户端 ``` $ npm install cnpm -g --registry=https://registry.npm.taobao.org ```
- 指定cnpm的源 ```cnpm set registry http://10.100.10.194:7001```
- 安装包 ```cnpm install```
- 安装特定私有包 ```cnpm install @people/react-umeditor```
- 引用私有包 ```import Editor from '@people/react-umeditor'```

