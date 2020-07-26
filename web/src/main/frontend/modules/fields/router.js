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
                history.push(routerPath.FIELD_GROUP_LIST);
            },
            childRoutes: [
            {
                path: routerPath.FIELD_GROUP,
                getComponent() {
                    history.push(routerPath.FIELD_GROUP_LIST);
                }
            },
            {
                path: routerPath.FIELD_GROUP_LIST,
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        if(!app._models.some(val=>(val.namespace ===  'fieldGroup'))){
                            app.model(require('./models/fieldGroup'));
                        }
                        cb(null, require('./routes/fieldGroupList'));
                    });
                }
            },
            {
                path: routerPath.FIELD_GROUP_EDIT,
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        if(!app._models.some(val=>(val.namespace ===  'fieldGroup'))){
                            app.model(require('./models/fieldGroup'));
                        }
                        cb(null, require('./routes/fieldGroupEdit'));
                    });
                }
            },
            {
                path: routerPath.FIELD_LIST,
                getComponent(nextState, cb) {
                    require.ensure([], require => {
                        if(!app._models.some(val=>(val.namespace ===  'field'))){
                            app.model(require('./models/field'));
                        }
                        cb(null, require('./routes/fieldList'));
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

    return <Router history={history} routes={routes} />
};