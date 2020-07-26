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
                history.push(routerPath.ARTICLE_LIST);
            },
            childRoutes: [
                {
                    path: routerPath.CMS,
                    getComponent() {
                        history.push(routerPath.ARTICLE_LIST);
                    }
                },
                {
                    path: routerPath.CATEGORY,
                    getComponent() {
                        history.push(routerPath.CATEGORY_LIST);
                    }
                },
                {
                    path: routerPath.CATEGORY_LIST,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val => (val.namespace === 'category'))) {
                                app.model(require('./models/category'));
                            }
                            cb(null, require('./routes/categoryList'));
                        })
                    }
                },
                {
                    path: routerPath.CATEGORY_EDIT,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val => (val.namespace === 'category'))) {
                                app.model(require('./models/category'));
                            }
                            cb(null, require('./routes/categoryEdit'));
                        })
                    }
                },
                {
                    path: 'cms/labelMenu',
                    name: 'labelMenu',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'labelMenu'))){
                                app.model(require('./models/labelMenu'));
                            }
                            cb(null, require('./routes/labelMenu'));
                        });
                    }
                },
                {
                    path: routerPath.ARTICLE,
                    getComponent() {
                        history.push(routerPath.ARTICLE_LIST);
                    }
                },
                {
                    path: routerPath.ARTICLE_LIST,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'article'))){
                                app.model(require('./models/article'));
                            }
                            if(!app._models.some(val=>(val.namespace ===  'comment'))){
                                app.model(require('./models/comment'));
                            }
                            cb(null, require('./routes/articleList'));
                        });
                    }
                },
                {
                    path: routerPath.ARTICLE_EDIT,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'article'))){
                                app.model(require('./models/article'));
                            }
                            cb(null, require('./routes/articleEdit'));
                        });
                    }
                },
                {
                    path: routerPath.ARTICLE_EDIT_PAPER,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'article'))){
                                app.model(require('./models/article'));
                            }
                            cb(null, require('./routes/paperEdit'));
                        });
                    }
                },
                {
                    path: routerPath.LIVE_EDIT,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace === 'live'))) {
                                app.model(require('./models/live'));
                            }
                            cb(null, require('./routes/liveEdit'));
                        });
                    }
                },
                {
                    path: routerPath.LIVE_ROOM,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace === 'live'))) {
                                app.model(require('./models/live'));
                            }
                            cb(null, require('./routes/liveRoom'));
                        });
                    }
                },
                {
                    path: routerPath.TOPIC_EDIT,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace === 'topic'))) {
                                app.model(require('./models/topic'));
                            }
                            if(!app._models.some(val=>(val.namespace ===  'article'))){
                                app.model(require('./models/article'));
                            }
                            cb(null, require('./routes/topicEdit'));
                        });
                    }
                },
                {
                    path: routerPath.TOPIC_DETAIL,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace === 'topic'))) {
                                app.model(require('./models/topic'));
                            }
                            cb(null, require('./routes/topicDetail'));
                        });
                    }
                },
                {
                    path: routerPath.COMMENT,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace === 'comment'))) {
                                app.model(require('./models/comment'));
                            }
                            cb(null, require('./routes/comment'));
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
    ]

    return <Router history={history} routes={routes} />
}