export const PAGE_SIZE = 10;

export const URL_PREFIX = '.';

export const CACHE_PREFIX = 'xz_cms_';

const pathname_array = window.location.pathname.split("/");
pathname_array.pop();
const url_path_pre = window.location.host + pathname_array.join("/");
const url_path_full = window.location.protocol + '//' + url_path_pre;
const url_path_ws =   'ws://' + url_path_pre;

export const urlPath = {
	'UPLOAD_FILE': url_path_full + '/api/upload/file',
	'UPLOAD_FILES': url_path_full + '/api/upload/files',
	'LIVE_TALK': url_path_ws + '/api/live/talk/',
	'DEFAULT': url_path_full
}

//DEV_UNLOGIN为true则临时禁用登陆验证
export const DEV_UNLOGIN = false;

export const formItemMainLayout = {
	labelCol: {
		span: 4
	},
	wrapperCol: {
		span: 18
	}
};

export const routerPath = {
	// 新闻爆料配置
	NEWS: '/news',
	NEWS_LIST: '/news/list',
	NEWS_DETAIL: '/news/list/edit',
	CLIENT_NEWS: 'client/newslist',
	CLIENT_NEWS_DETAIL: 'client/newslist/detail',
	// END
	CMS: '/cms',
	CATEGORY: '/cms/category',
	CATEGORY_LIST: '/cms/category/list',
	CATEGORY_EDIT: '/cms/category/edit',
	ARTICLE: '/cms/articles',
	ARTICLE_LIST: '/cms/articles/list',
	ARTICLE_EDIT: '/cms/articles/edit',
	ARTICLE_EDIT_PAPER: '/cms/articles/edit-paper',
	LIVE_EDIT: '/cms/articles/liveEdit',
	LIVE_ROOM: '/cms/articles/liveRoom',
	TOPIC_EDIT: '/cms/articles/topicEdit',
	TOPIC_DETAIL: '/cms/articles/topicDetail',
	COMMENT: '/cms/comment',
	SYS: '/sys',
	DICT: '/sys/dict',
	CLIENT: '/client',
	CLIENT_PUSH: '/client/clientPush',
	USERS: '/users',
	USERS_USER: '/users/user',
	ROLE: '/users/role',
	ROLE_LIST: '/users/role/list',
	ROLE_EDIT: '/users/role/edit',
	ANLS: '/analysis',
	ANLS_LOG: '/analysis/log',
	ANLS_CATG: '/analysis/statistics',
    ANLS_SENDING: '/analysis/sending',
    ANLS_AUTH: '/analysis/count',
	LOADINGIMGS: '/client/loadingImgs',
	GUESTBOOK: '/client/guestbook',
	ACTIVITYCODE: '/client/activityCode',
	FIELD_GROUP:'/fields/fieldGroup',
	FIELD_GROUP_LIST:'/fields/fieldGroup/list',
	FIELD_GROUP_EDIT:'/fields/fieldGroup/edit',
	FIELD_LIST: '/fields/field',
    ASKList:'/ask/asks',
    ASK:"/ask",
    ASKEDIT:'/ask/edit'
};

export const pageTypes = {
	'CmsCategoryList': '栏目列表',
	'CmsCategoryEdit': '栏目编辑',
	'CmsArticlesList': '文章列表',
	'CmsArticlesEdit': '文章编辑',
	'CmsArticlesEditPaper': '报纸编辑',
	'CmsArticlesLiveEdit': '直播编辑',
	'CmsArticlesLiveRoom': '直播间',
	'CmsArticlesTopicEdit': '专题编辑',
	'CmsArticlesTopicDetail': '专题详情',
	'SysMenuMenusEdit': '菜单编辑',
	'SysDictDictEdit': '字典编辑',
	'UsersUserUserEdit': '用户编辑',
	'ClientClientMenuClientMenuEdit': '客户端菜单编辑',
	'UsersRoleList': '角色列表',
	'UsersRoleEdit': '角色编辑',
	'UsersOfficeOfficeEdit': '机构编辑',
	'ClientLoadingImgsLoadingImgsEdit':'开屏图编辑',
    'AskEdit':'问政编辑'
};
export const fieldMap = {
	'CATG_NAME': '栏目名称',
	'NAME': '名称',
	'TITLE': '标题',
	'WEIGHT': '权重',
	'TYPE': '类型',
	'HITS': '访问量',
	'COMMENT': '评论数',
	'PUB_DATE': '发布时间',
	'CONTRIBUTOR': '发稿人',
	'ACTION': '操作',
	'OFFICE_NAME': '归属机构',
	'MODEL': '栏目模型',
	'AUTO_ONLINE': '先发后审',
	'SORT': '排序',
	'DELFLAG': '状态',
	'ORG_TITLE': '原文标题',
	'COM_CONTENT': '评论内容',
    'COM_REPLY': '管理员回复',
	'COM_USER': '评论人',
	'COM_TIME': '评论时间',
	'AREA': '所在地',
	'LIKES': '点赞数',
	'HREF': '链接',
	'SHOW': '可见',
	'KEY': '键值',
	'LABEL': '标签',
	'DESCRIBE': '描述',
	'REMARK': '备注',
	'USERNAME': '登录名',
	'FULLNAME': '姓名',
	'ROLE_NAME': '角色名称',
	'DATASCOPE': '数据范围',
	'URI': 'URI',
	'METHOD': '提交方式',
	'REMOTE_ADDR': '操作者ip',
	'ACT_TIME': '操作时间',
	'AUTHORS': '作者',
	'ARTICAL_COUNT': '信息量',
	'COMMENT_COUNT': '评论数',
	'HITS_COUNT': '点击量',
	'LAST_UPDATE': '最后更新时间',
	'PARENTNAME': '父级栏目',
	'CONTENT': '内容',
	'PUSH_TIME': '推送时间',
	'PUSH_PLATFORM': '推送平台',
    'PUSH_STATUS': '推送状态',
	'SIMPLENAME': '简称(英文名)',
	'POS': '位置',
	'PIC': '图片',
	'SHOWTIMES': '显示时长',
	'INTRO': '说明',
	'DIGEST': '摘要',
	'TYPENAME': '类别',
	'ACT_CODE': '邀请码',
	'GUEST_CONTENT': '留言内容',
	'ENVIRONMENT': '应用环境',
	'CONTACTOR': '联系人',
	'CONTACT': '联系方式',
	'GUEST_TIME': '留言时间',
	'STATE': '当前状态',
	'START_TIME': '创建时间',
	'TIMESCOPE': '有限时间范围',
	'CEILINGTIMES': '使用上限次数',
	'USEDTIMES': '已使用次数',
    'HITSCOUNT':'点击量'
}
export const artTypes = {
	'subject': {
		label: '专题',
		color: 'pink'
	},
	'live': {
		label: '直播',
		color: 'red'
	},
	'common': {
		label: '新闻',
		color: 'green'
	},
	'image': {
		label: '图集',
		color: 'orange'
	},
	'audio': {
		label: '音频',
		color: 'cyan'
	},
	'video': {
		label: '视频',
		color: 'blue'
	},
	'link': {
		label: '链接',
		color: '#87d068'
	},
	'cite': {
		label: '引用',
		color: '#2db7f5'
	},
	'help': {
		label: '公益',
		color: 'orange'
	},
	'ask': {
		label: '问政',
		color: 'yellow'
	}
};


export const blocks = [{
	value: '1',
	label: '头图'
}, {
	value: '2',
	label: '列表'
},
{
	value: '3',
	label: '待选区'
}];

export const delFlags = [{
	value: '0',
	label: '上线'
}, {
	value: '1',
	label: '下线'
}, {
	value: '2',
	label: '审核'
},
{
	value: '5',
	label: '定时发布'
},
{
	value: '3',
	label: '回收站'
}
];

export const delFlags2 = [{
	value: '0',
	label: '上线'
}, {
	value: '1',
	label: '下线'
}, {
	value: '2',
	label: '审核'
},

];

export const liveStatus = [{
	value: '1',
	label: '预告'
}, {
	value: '2',
	label: '直播中'
}, {
	value: '3',
	label: '已结束'
}];

export const IMG_UPLOAD_TYEPS = ['gif', 'png', 'jpg', 'jpeg'];


/* 自定义字段类型列表 */
export const FIELD_TYPE = {
	radio:{
		name:'单选按钮',
		icon:'close-square-o',
		swicth_rel:'multi',
		req_rel:true
	},
	select:{
		name:'下拉菜单',
		icon:'database',
		swicth_rel:'multi',
		req_rel:true
	},
	checkboxes:{
		name:'多项复选框',
		icon:'check-square-o',
		swicth_rel:'multi',
		req_rel:true
	},
	checkbox:{
		name:'复选框',
		icon:'check-square',
		swicth_rel:'signle'
	},
	media:{
		name:'多媒体文件',
		icon:'video-camera',
		swicth_rel:'media',
		req_rel:true,
		multi_rel: true
	},
	file:{
		name:'文件',
		icon:'file',
		swicth_rel:'input',
		req_rel:true,
		multi_rel: true,
		holder_rel:true,
		def_rel:true
	},
	image:{
		name:'图片',
		icon:'picture',
		swicth_rel:'input',
		req_rel:true,
		multi_rel: true,
		holder_rel:true,
		def_rel:true
	},
	input:{
		name:'单行文本',
		icon:'swap-right',
		swicth_rel:'input',
		req_rel:true,
		multi_rel: true,
		holder_rel:true,
		def_rel:true
	},
	textarea:{
		name:'多行文本',
		icon:'bars',
		swicth_rel:'input',
		req_rel:true,
		multi_rel: true,
		holder_rel:true,
		def_rel:true
	},
	url:{
		name:'链接',
		icon:'link',
		swicth_rel:'input',
		req_rel:true,
		format_rel:true,
		multi_rel: true,
		holder_rel:true,
		def_rel:true
	},
	date:{
		name:'日期',
		icon:'calendar',
		swicth_rel:'input',
		req_rel:true,
		holder_rel:true,
		multi_rel: true
	},
	email:{
		name:'邮件',
		icon:'mail',
		swicth_rel:'input',
		req_rel:true,
		format_rel:true,
		multi_rel: true,
		holder_rel:true,
		def_rel:true
	},
	numeric:{
		name:'数字',
		icon:'calculator',
		swicth_rel:'input',
		req_rel:true,
		format_rel:true,
		multi_rel: true,
		holder_rel:true,
		def_rel:true
	},
	phone:{
		name:'电话',
		icon:'phone',
		swicth_rel:'input',
		req_rel:true,
		format_rel:true,
		multi_rel: true,
		holder_rel:true,
		def_rel:true
	},
	wysiwyg:{
		name:'富文本编辑器',
		icon:'edit',
		swicth_rel:'eidt',
		req_rel:true,
		def_rel:true
	}
}

export const FIELD_KEYS = [
	'name',
	'slug',
	'type',
	'placeholder',
	'defaultValue',
	'description',
	'validate',
	'options',
	'logic',
	'simpleOrMore',
	'isAllowSearch'
];

export  const FIELED_SEND_KEYS = {
    'USERNAME':'姓名',
    'ORG':'所属机构',
    'SENDCOUNT':'发稿量',
    'COMMENTCOUNT':'评论数',
    'CLICKCOUNT':'点击量'
}
export const blocks2 = [{
	value: '2',
	label: '列表'
},
{
	value: '3',
	label: '待选区'
}];
