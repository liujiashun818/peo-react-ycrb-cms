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
                 history.push(routerPath.ASKList);
            },
            childRoutes: [
                {
                    path: routerPath.ASK,
                    getComponent(nextState, cb) {
                        history.push(routerPath.ASKList);
                    }
                },
                {
                    path: routerPath.ASKList,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val => (val.namespace === 'askList'))) {
                                app.model(require('../ask/models/askList'));
                            }
                            if(!app._models.some(val => (val.namespace === 'comment'))) {
                                app.model(require('../cms/models/comment'));
                            }
                            if(!app._models.some(val => (val.namespace === 'article'))) {
                                app.model(require('../cms/models/article'));
                            }
                            cb(null, require('./routes/askList'));
                        })
                    }
                },
                {
                    path: routerPath.ASKEDIT,
                    getComponent(nextState, cb) {
                        require.ensure([], require => {
                            if(!app._models.some(val => (val.namespace === 'askList'))) {
                                app.model(require('../ask/models/askList'));
                            }
                            if(!app._models.some(val => (val.namespace === 'comment'))) {
                                app.model(require('../cms/models/comment'));
                            }
                            cb(null, require('./routes/askEdit'));
                        })
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




// export default function ({history, app}) {
//     const routes = [
//         {
//             path: '/',
//             component: App,
//             getIndexRoute (nextState, cb) {
//                 // require.ensure([], require => {
//                 //     if(!app._models.some(val=>(val.namespace ===  'ask'))){
//                 //         app.model(require('./models/ask'));
//                 //     }
//                 //      cb(null, {component: require('./routes/askList')});
//                 // });
//             },
//             childRoutes: [
//                 {
//                     path: routerPath.ASK,
//                     getComponent (nextState, cb) {
//                         history.push('/ask/asks');
//                     }
//                 },
//                 {
//                     path: 'ask/asks',
//                     getComponent (nextState, cb) {
//                         require.ensure([], require => {
//                             // if(!app._models.some(val=>(val.namespace ===  'ask'))){
//                             //     app.model(require('./models/ask'));
//                             // }
//                             cb(null, {component: require('./routes/askList')});
//                         });
//                         // if(!app._models.some(val=>(val.namespace ===  'ask'))){
//                         //     app.model(require('./models/ask'));
//                         // }
//
//                     }
//                 },
//
//
//                 {
//                     path: '*',
//                     name: 'error',
//                     getComponent (nextState, cb) {
//                         require.ensure([], require => {
//                             cb(null, require('../../routes/error'));
//                         });
//                     }
//                 }
//
//
//             ]
//         }
//     ];
//
//     return <Router history={history} routes={routes} />;
// }
