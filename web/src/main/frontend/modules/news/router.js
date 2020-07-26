import React from 'react';
import { Router } from 'dva/router';
import { routerPath } from '../../constants';
import App from '../../routes/app';

export default function ({history, app}) {
    const routes = [
        {
            path: '/',
            component: App,
            getIndexRoute () {
                history.push(routerPath.NEWS_LIST);
            },
            childRoutes: [
                //  新闻爆料 
                {
                    path:  routerPath.NEWS_LIST,
                    name: 'newslist',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'newslist'))){
                                app.model(require('./models/newslist'));
                            }
                            cb(null, require('./routes/newslist'));
                        });
                    }
                },
                {
                    path:  routerPath.NEWS_DETAIL,
                    name: 'newslistDetail',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'newslist'))){
                                app.model(require('./models/newslist'));
                            }
                            cb(null, require('./routes/newslistDetail'));
                        });
                    }
                },  //

                // {
                //     path: routerPath.NEWS_LIST,
                //     getComponent (nextState, cb) {
                //         require.ensure([], require => {
                //             if(!app._models.some(val=>(val.namespace ===  'news_list'))){
                //                 app.model(require('./models/list'));
                //             }
                //             cb(null, require('./routes/list'));
                //         });
                //     }
                // },
                // {
                //     path: routerPath.NEWS_EDIT,
                //     getComponent (nextState, cb) {
                //         require.ensure([], require => {
                //             if(!app._models.some(val=>(val.namespace ===  'news_edit'))){
                //                 app.model(require('./models/list'));
                //             }
                //             cb(null, require('./routes/edit'));
                //         });
                //     }
                // },
                {
                    path: '*',
                    name: 'error',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            cb(null, require('../../routes/error'));
                        });
                    }
                }
            ]
        }
    ];

    return <Router history={history} routes={routes} />;
}
