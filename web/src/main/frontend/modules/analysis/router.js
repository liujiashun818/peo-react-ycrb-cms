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
                    if(!app._models.some(val=>(val.namespace ===  'log'))){
                        app.model(require('./models/log'));
                    }
                    cb(null, {component: require('./routes/log')});
                });
            },
            childRoutes: [
                {
                    path: routerPath.ANLS,
                    getComponent (nextState, cb) {
                        history.push(routerPath.ANLS_LOG);
                    }
                },
                {
                    path: routerPath.ANLS_LOG,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'anls_log'))){
                                app.model(require('./models/log'));
                            }
                            cb(null, require('./routes/log'));
                        })
                    }
                },
                {
                    path: routerPath.ANLS_CATG,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'anls_catg'))){
                                app.model(require('./models/catg'));
                            }
                            cb(null, require('./routes/catg'));
                        })
                    }
                },
                {
                    path: routerPath.ANLS_AUTH,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'anls_auth'))){
                              app.model(require('./models/auth'));
                            }
                            cb(null, require('./routes/auth'));
                        });
                    }
                },
                {
                    path: routerPath.ANLS_SENDING,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'anls_sending'))){
                                app.model(require('./models/sending'));
                            }
                            cb(null, require('./routes/sending'));
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
