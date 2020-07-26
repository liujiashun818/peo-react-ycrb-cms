import React from 'react';
import { Router } from 'dva/router';
import { routerPath } from '../../constants';
import App from '../../routes/app';

export default function ({history, app}) {
  const routes = [
    {
      path: '/',
      component: App,
      getIndexRoute (nextState, cb) {},
      childRoutes: [
        {
          path: 'myCenter/userInfo',
          name: 'userInfo',
          getComponent (nextState, cb) {
            require.ensure([], require => {
              if(!app._models.some(val=>(val.namespace ===  'userInfo'))){
                 app.model(require('./models/userInfo'))
              }
              cb(null, require('./routes/userInfo'))
            })
          }
        },
        {
          path: '*',
          name: 'error',
          getComponent (nextState, cb) {
            require.ensure([], require => {
              cb(null, require('../../routes/error'))
            })
          }
        }
      ]
    }
  ]

  return <Router history={history} routes={routes} />
}