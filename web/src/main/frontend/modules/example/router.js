import React from 'react';
import { Router } from 'dva/router';
import App from '../../routes/app';

export default function ({history, app}) {
  const routes = [
    {
      path: '/',
      component: App,
      getIndexRoute (nextState, cb) {
        require.ensure([], require => {
          if(!app._models.some(val=>(val.namespace ===  'exa_1'))){
             app.model(require('./models/exa_1'))
          }
          cb(null, {component: require('./routes/exa_1')})
        })
      },
      childRoutes: [
        {
          path: 'example/exa_1',
          name: 'exa_1',
          getComponent (nextState, cb) {
            require.ensure([], require => {
              if(!app._models.some(val=>(val.namespace ===  'exa_1'))){
                 app.model(require('./models/exa_1'))
              }
              cb(null, require('./routes/exa_1'))
            })
          }
        },
        {
          path: 'example/exa_2',
          name: 'exa_2',
          getComponent (nextState, cb) {
            require.ensure([], require => {
              cb(null, require('./routes/exa_2'))
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