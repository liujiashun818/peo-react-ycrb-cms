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
                    if(!app._models.some(val=>(val.namespace ===  'menus'))){
                        app.model(require('./models/menus'));
                    }
                    cb(null, {component: require('./routes/menus')});
                });
            },
            childRoutes: [
                {
                    path: routerPath.SYS,
                    getComponent (nextState, cb) {
                        history.push('/sys/menu');
                    }
                },
                {
                    path: 'sys/menu',
                    name: 'menus',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'menus'))){
                                app.model(require('./models/menus'));
                            }
                            cb(null, require('./routes/menus'));
                        });
                    }
                },
                {
                    path: 'sys/menu/menusEdit',
                    name: 'menusEdit',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'menus'))){
                                app.model(require('./models/menus'));
                            }
                            cb(null, require('./routes/menusEdit'));
                        });
                    }
                },
                {
                    path: 'sys/dict',
                    name: 'dict',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'dict'))){
                                app.model(require('./models/dict'));
                            }
                            cb(null, require('./routes/dict'));
                        });
                    }
                },
                {
                    path: 'sys/dict/dictEdit',
                    name: 'dictEdit',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'dict'))){
                                app.model(require('./models/dict'));
                            }
                            cb(null, require('./routes/dictEdit'));
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
    return <Router history={history} routes={routes} />;
}