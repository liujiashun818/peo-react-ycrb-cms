import dva from 'dva';
import './cms/cms.html';
import { Jt,config } from '../utils';
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
app.model(require('../modules/cms/models/cms'));
app.model(require('../modules/cms/models/comment'));
// 4. Router
app.router(require('./cms/router'));

// 5. Start
app.start('#root');
