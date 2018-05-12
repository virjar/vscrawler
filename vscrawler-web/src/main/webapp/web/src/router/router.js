const Index = r => require.ensure([], () => r(require('../pages/Index')), 'Index')

// 嵌套在 '/' 中的路由
let ins = [{
    path: '/list',
    component: r => require.ensure([], () => r(require('../pages/List')), 'List')
}, {
    path: '/test',
    component: r => require.ensure([], () => r(require('../pages/Test')), 'Test')
}];

// 单独页面
let single = [];

let router = [{
    path: '/',
    component: Index,
    redirect: ins[0].path,
    children: [...ins]
}, ...single]

export default router