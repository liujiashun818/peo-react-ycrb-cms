import dva from 'dva';
import './example/example.html';
import { Jt } from '../utils';
import { hashHistory } from 'dva/router';
// 1. Initialize
const app = dva({
	history: hashHistory,

	//全局错误处理配置
	onError(e, dispatch) {
		Jt.onError(e, dispatch);
	}
});

// 2. Plugins
// app.use({});

// 3. Model
app.model(require('../models/app'));

// 4. Router
app.router(require('./example/router'));

// 5. Start
app.start('#root');
