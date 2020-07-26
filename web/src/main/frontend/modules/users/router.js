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
                    if(!app._models.some(val=>(val.namespace ===  'office'))){
                        app.model(require('./models/office'));
                    }
                    cb(null, {component: require('./routes/officeList')});
                });
            },
            childRoutes: [
                {
                    path: routerPath.USERS,
                    getComponent() {
                        history.push(routerPath.USERS_USER);
                    }
                },
                {
                    path: 'users/office',
                    name: 'officeList',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'office'))){
                                app.model(require('./models/office'));
                            }
                            cb(null, require('./routes/officeList'));
                        });
                    }
                },
                {
                    path: 'users/office/officeEdit',
                    name: 'officeEdit',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'office'))){
                                app.model(require('./models/office'));
                            }
                            cb(null, require('./routes/officeEdit'));
                        })
                    }
                },
                {
                    path: routerPath.USERS_USER,
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'user'))){
                                app.model(require('./models/user'));
                            }
                            cb(null, require('./routes/user'));
                        });
                    }
                },
                {
                    path: 'users/user/userEdit',
                    name: 'userEdit',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'user'))){
                                app.model(require('./models/user'));
                            }
                            cb(null, require('./routes/userEdit'));
                        });
                    }
                },
                {
                    path: routerPath.ROLE,
                    getComponent() {
                        history.push(routerPath.ROLE_LIST);
                    }
                },
                {
                    path: routerPath.ROLE_LIST,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'role'))){
                                app.model(require('./models/role'));
                            }
                            cb(null, require('./routes/roleList'));
                        });
                    }
                },
                {
                    path: routerPath.ROLE_EDIT,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val=>(val.namespace ===  'role'))){
                                app.model(require('./models/role'));
                            }
                            cb(null, require('./routes/roleEdit'));
                        });
                    }
                },
                {
                    path: '*',
                    name: 'error',
                    getComponent (nextState, cb) {
                        require.ensure([], require => {
                            cb(null, require('../../routes/error'))
                        });
                    }
                }
            ]
        }
    ];
    return <Router history={history} routes={routes} />;
}