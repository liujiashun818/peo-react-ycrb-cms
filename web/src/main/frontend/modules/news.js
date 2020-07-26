import dva from 'dva';
import './news/news.html'; //为了与我们本身的app区分 将新闻爆料模块起名为news
import { Jt ,config} from '../utils';
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
app.router(require('./news/router'));

// 5. Start
app.start('#root');
