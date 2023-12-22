import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router';
import { usePermissStore } from '../store/permiss';
import Home from '../views/home.vue';

const routes: RouteRecordRaw[] = [
    {
        path: '/',
        redirect: '/dashboard',
    },
    {
        path: '/',
        name: 'Home',
        component: Home,
        children: [
            {
                path: '/dashboard',
                name: 'dashboard',
                meta: {
                    title: '首页',
                    permiss: '1',
                },
                component: () => import(/* webpackChunkName: "dashboard" */ '../views/dashboard.vue'),
            },
            {
                path: '/user',
                name: 'user',
                meta: {
                    title: '用户管理',
                    permiss: '3',
                },
                component: () => import(/* webpackChunkName: "dashboard" */ '../views/user.vue'),
            },
            {
                path: '/canteen',
                name: 'canteen',
                meta: {
                    title: '食堂管理',
                    permiss: '2',
                },
                component: () => import(/* webpackChunkName: "dashboard" */ '../views/canteen.vue'),
            },
            {
                path: '/cuisine',
                name: 'cuisine',
                meta: {
                    title: '菜系管理',
                    permiss: '2',
                },
                component: () => import(/* webpackChunkName: "dashboard" */ '../views/cuisine.vue'),
            },
            {
                path: '/complaint',
                name: 'complaint',
                meta: {
                    title: '投诉管理',
                    permiss: '2',
                },
                component: () => import(/* webpackChunkName: "dashboard" */ '../views/complaint.vue'),
            },
            {
                path: '/item',
                name: 'item',
                meta: {
                    title: '菜品管理',
                    permiss: '2',
                },
                component: () => import(/* webpackChunkName: "dashboard" */ '../views/item.vue'),
            }
        ],
    },
    {
        path: '/login',
        name: 'Login',
        meta: {
            title: '登录',
        },
        component: () => import(/* webpackChunkName: "login" */ '../views/login.vue'),
    },
    {
        path: '/403',
        name: '403',
        meta: {
            title: '没有权限',
        },
        component: () => import(/* webpackChunkName: "403" */ '../views/403.vue'),
    },
    {
        path: '/:pathMatch(.*)*',
        name: '404',
        meta: {
            title: '页面不存在',
        },
        component: () => import(/* webpackChunkName: "404" */ '../views/404.vue'),
    },
];

const router = createRouter({
    history: createWebHashHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    document.title = `${to.meta.title} | 食堂管理系统`;
    const role = localStorage.getItem('ms_keys');
    const permiss = usePermissStore();
    if (!role && to.path !== '/login') {
        next('/login');
    } else if (to.meta.permiss && !permiss.key.includes(to.meta.permiss)) {
        // 如果没有权限，则进入403
        next('/403');
    } else {
        next();
    }
});

export default router;
