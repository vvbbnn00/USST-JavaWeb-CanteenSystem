<template>
  <div class="sidebar">
    <el-menu
        class="sidebar-el-menu"
        :default-active="onRoutes"
        :collapse="sidebar.collapse"
        background-color="#324157"
        text-color="#bfcbd9"
        active-text-color="#20a0ff"
        unique-opened
        router
    >
      <template v-for="item in items">
        <template v-if="item.subs">
          <el-sub-menu :index="item.index" :key="item.index">
            <template #title>
              <el-icon>
                <component :is="item.icon"></component>
              </el-icon>
              <span>{{ item.title }}</span>
            </template>
            <template v-for="subItem in item.subs">
              <el-sub-menu
                  v-if="subItem.subs"
                  :index="subItem.index"
                  :key="subItem.index"
              >
                <template #title>{{ subItem.title }}</template>
                <el-menu-item v-for="(threeItem, i) in subItem.subs" :key="i" :index="threeItem.index">
                  {{ threeItem.title }}
                </el-menu-item>
              </el-sub-menu>
              <el-menu-item v-else :index="subItem.index">
                {{ subItem.title }}
              </el-menu-item>
            </template>
          </el-sub-menu>
        </template>
        <template v-else>
          <el-menu-item :index="item.index" :key="item.index">
            <el-icon>
              <component :is="item.icon"></component>
            </el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </template>
      </template>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import {computed} from 'vue';
import {useSidebarStore} from '../store/sidebar';
import {useRoute} from 'vue-router';

const items: {
  icon: string;
  index: string;
  title: string;
  subs?: {
    index: string;
    title: string;
    subs?: {
      index: string;
      title: string;
    }[];
  }[];
}[] = [
  {
    icon: 'Odometer',
    index: '/dashboard',
    title: '首页',
  },
  {
    icon: 'Calendar',
    index: '1',
    title: '食堂基本信息管理',
    subs: [
      {
        index: '/canteen',
        title: '食堂管理',
      },
      {
        index: '/cuisine',
        title: '菜系管理',
      },
      {
        index: '/item',
        title: '菜品管理',
      }
    ],
  },
  {
    icon: 'User',
    index: '2',
    title: '用户管理',
    subs: [
      {
        index: '/user',
        title: '用户列表',
      },
    ],
  },
  {
    icon: 'Message',
    title: '反馈管理',
    index: '3',
    subs: [
      {
        index: '/comment',
        title: '评论管理',
      },
      {
        index: '/community',
        title: '社区信息管理',
      },
    ]
  }
];

const route = useRoute();
const onRoutes = computed(() => {
  return route.path;
});

const sidebar = useSidebarStore();
</script>

<style scoped>
.sidebar {
  display: block;
  position: absolute;
  left: 0;
  top: 70px;
  bottom: 0;
  overflow-y: scroll;
}

.sidebar::-webkit-scrollbar {
  width: 0;
}

.sidebar-el-menu:not(.el-menu--collapse) {
  width: 250px;
}

.sidebar > ul {
  height: 100%;
}
</style>
