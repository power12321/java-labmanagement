import { createWebHistory, createRouter } from 'vue-router'
/* Layout */
import Layout from '@/layout'

// 公共路由
export const constantRoutes = [
  {
    path: "/login",
    component: () => import("@/views/login.vue"),
    hidden: true,
  },
  {
    path: "/register",
    component: () => import("@/views/register.vue"),
    hidden: true,
  },
  {
    path: '/',
    component: Layout,
    redirect: '/review',
    roleId: 0,
    children: [
      {
        path: 'review',
        component: () => import('@/views/review'),

        meta: { title: '审核管理', icon: 'dashboard',  }
      }
    ]
  },
  {
    path: '/student',
    component: Layout,
    roleId: 0,
    children: [
      {
        path: '',
        component: () => import('@/views/student'),
        meta: { title: '学生管理', icon: 'online' }
      }
    ]
  },
  {
    path: '/teacher',
    component: Layout,
    roleId: 0,
    children: [
      {
        path: '',
        component: () => import('@/views/teacher'),
        meta: { title: '教师管理', icon: 'post' }
      }
    ]
  },
  {
    path: '/equipment',
    component: Layout,
    roleId:0,
    children: [
      {
        path: '',
        component: () => import('@/views/equipment'),
        meta: { title: '设备管理', icon: 'post' }
      }
    ]
  },
  {
    path: '/equipment',
    component: Layout,
    roleId:1,
    children: [
      {
        path: '',
        component: () => import('@/views/equipment'),
        meta: { title: '设备预约', icon: 'post' }
      }
    ]
  },
  {
    path: '/course',
    component: Layout,
    roleId: 0,
    children: [
      {
        path: '',
        component: () => import('@/views/course'),
        meta: { title: '实验管理', icon: 'code' }
      }
    ]
  },
  {
    path: '/plan',
    component: Layout,
    roleId: 0,
    children: [
      {
        path: '',
        component: () => import('@/views/plan'),
        meta: { title: '排课管理', icon: 'date' }
      }
    ]
  },
  {
    path: '/echarts',
    component: Layout,
    roleId: 0,
    children: [
      {
        path: '',
        component: () => import('@/views/echarts'),
        meta: { title: '可视化统计', icon: 'post' }
      }
    ]
  },

  {
    path: '/choose',
    component: Layout,
    roleId: 2,
    children: [
      {
        path: '',
        component: () => import('@/views/choose'),
        meta: { title: '实验选课', icon: 'online' }
      }
    ]
  },
  {
    path: '/comment',
    component: Layout,
    roleId: 2,
    children: [
      {
        path: '',
        component: () => import('@/views/comment'),
        meta: { title: '实验提交', icon: 'form' }
      }
    ]
  },
  {
    path: '/detail',
    component: Layout,
    roleId: 1,
    children: [
      {
        path: '',
        component: () => import('@/views/detail'),
        meta: { title: '实验详情', icon: 'form' }
      }
    ]
  },
  {
    path: '/statistics',
    component: Layout,
    children: [
      {
        path: '',
        component: () => import('@/views/statistics'),
        meta: { title: '数据统计', icon: 'post' }
      }
    ]
  },

]



const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  },
});

export default router;
