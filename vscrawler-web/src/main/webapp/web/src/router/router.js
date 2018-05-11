const Index = r => require.ensure([], () => r(require('../pages/Index')), 'Index')

// 嵌套在 '/' 中的路由
let ins = [{
    path: '/dashboard',
    component: r => require.ensure([], () => r(require('../pages/Dashboard')), 'Dashboard')
}, {
    path: '/list',
    component: r => require.ensure([], () => r(require('../pages/List')), 'List')
}, {
    path: '/source',
    component: r => require.ensure([], () => r(require('../pages/DataSource')), 'DataSource')
}, {
    path: '/type',
    component: r => require.ensure([], () => r(require('../pages/DataType')), 'DataType')
}, {
    path: '/test',
    component: r => require.ensure([], () => r(require('../pages/Test')), 'Test')
}];

// 单独页面
let single = [{
    path: '/single',
    component: r => require.ensure([], () => r(require('../pages/Dashboard')), 'Dashboard')
}];

let router = [{
    path: '/',
    component: Index,
    redirect: ins[0].path,
    children: [...ins]
}, ...single]

export default router