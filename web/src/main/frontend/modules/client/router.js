import React from 'react';
import { Router } from 'dva/router';
import { routerPath } from '../../constants';
import App from '../../routes/app';

export default function ({history, app}) {
    const routes = [
        {
            path: '/',
            component: App,
            getIndexRoute (nextState, cb) {
                require.ensure([], require => {
                    if(!app._models.some(val=>(val.namespace ===  'client'))){
                        app.model(require('./models/client'));
                    }
                    cb(null, {component: require('./routes/clientMenuList')});
                });
            },
            childRoutes: [
                {
                    path: routerPath.CLIENT,
                    getComponent (nextState, cb) {
                        history.push('/client/clientMenu');
                    }
                },
                {
                    path: 'client/floatMenu',
                    name: 'floatMenu',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'floatMenu'))){
                                app.model(require('./models/floatMenu'));
                            }
                            cb(null, require('./routes/floatMenu'));
                        });
                    }
                },
                {
                    path: 'client/clientMenu',
                    name: 'clientMenuList',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'client'))){
                                app.model(require('./models/client'));
                            }
                            cb(null, require('./routes/clientMenuList'));
                        });
                    }
                },
                {
                    path: 'client/clientMenu/clientMenuEdit',
                    name: 'clientMenuEdit',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'client'))){
                                app.model(require('./models/client'));
                            }
                            cb(null, require('./routes/clientMenuEdit'));
                        });
                    }
                },
                {
                    path: 'client/loadingImgs',
                    name: 'loadingImgs',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'loadingImgs'))){
                                app.model(require('./models/loadingImgs'));
                            }
                            cb(null, require('./routes/loadingImgs'));
                        });
                    }
                },
                //  新闻爆料 
                // {
                //     path: 'client/newslist',
                //     name: 'newslist',
                //     getComponent (nextState, cb) {
                //         require.ensure([], require => {
                //             if(!app._models.some(val=>(val.namespace ===  'newslist'))){
                //                 app.model(require('./models/newslist'));
                //             }
                //             cb(null, require('./routes/newslist'));
                //         });
                //     }
                // },
                // {
                //     path: 'client/newslist/detail',
                //     name: 'newslistDetail',
                //     getComponent (nextState, cb) {
                //         require.ensure([], require => {
                //             if(!app._models.some(val=>(val.namespace ===  'newslist'))){
                //                 app.model(require('./models/newslist'));
                //             }
                //             cb(null, require('./routes/newslistDetail'));
                //         });
                //     }
                // },
                // 
                {
                    path: 'client/loadingImgs/loadingImgsEdit',
                    name: 'loadingImgsEdit',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'loadingImgs'))){
                                app.model(require('./models/loadingImgs'));
                            }
                            cb(null, require('./routes/loadingImgsEdit'));
                        });
                    }
                },
                {
                    path: 'client/clientPush',
                    name: 'clientPush',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'client'))){
                                app.model(require('./models/client'));
                            }
                            cb(null, require('./routes/clientPush'));
                        });
                    }
                },
                {
                    path: 'client/guestbook',
                    name: 'guestbook',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'guestbook'))){
                                app.model(require('./models/guestbook'))
                            }
                            cb(null, require('./routes/guestbook'))
                        })
                    }
                },
                {
                    path: 'client/activityCode',
                    name: 'activityCode',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'activityCode'))){
                                app.model(require('./models/activityCode'));
                            }
                            cb(null, require('./routes/activityCodeList'));
                        })
                    }
                },
                {
                    path: 'client/activityCode/activityCodeEdit',
                    name: 'activityCodeEdit',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'activityCode'))){
                                app.model(require('./models/activityCode'));
                            }
                            cb(null, require('./routes/activityCodeEdit'));
                        });
                    }
                },
                {
                    path: 'client/searchKeywords',
                    name: 'searchKeywords',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'searchKeywords'))){
                                app.model(require('./models/searchKeywords'));
                            }
                            cb(null, require('./routes/searchKeywords'));
                        });
                    }
                },
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
