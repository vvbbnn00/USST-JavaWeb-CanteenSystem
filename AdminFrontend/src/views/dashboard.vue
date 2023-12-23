<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover" class="mgb20">
          <div class="user-info">
            <el-avatar :size="120" :src="avatar" />
            <div class="user-info-cont">
              <div class="user-info-name">{{ name }}</div>
              <div>{{ email }}</div>
            </div>
          </div>
        </el-card>
        <el-card shadow="hover" style="padding-bottom: 20px">
          <template #header>
            <div class="clearfix">
              <span>一言</span>
            </div>
          </template>
          <div style="align-items: center">
            <div class="bracketLeft">『</div>
            <div class="word">{{ hitokoto.hitokoto }}</div>
            <div class="bracketRight">』</div>
            <div class="author">—— {{ hitokoto.from_who }} 「{{ hitokoto.from }}」</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-row :gutter="20" class="mgb20">
          <el-col :span="8">
            <el-card shadow="hover" :body-style="{ padding: '0px' }">
              <div class="grid-content grid-con-1" v-if="role === 'admin'" @click="gotoUserManagement">
                <el-icon class="grid-con-icon">
                  <User/>
                </el-icon>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ query.userCount }}</div>
                  <div>用户</div>
                </div>
              </div>
              <div class="grid-content grid-con-1" v-if="role === 'canteen_admin'">
                <el-icon class="grid-con-icon"><Promotion /></el-icon>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ query.community }}</div>
                  <div>公告</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" :body-style="{ padding: '0px' }">
              <div class="grid-content grid-con-2" v-if="role === 'admin'">
                <el-icon class="grid-con-icon">
                  <ChatDotRound/>
                </el-icon>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ query.commentCount }}</div>
                  <div>交流社区信息</div>
                </div>
              </div>
              <div class="grid-content grid-con-3" v-if="role === 'canteen_admin'" @click="gotoComplaintManagement">
                <el-icon class="grid-con-icon"><WarningFilled /></el-icon>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ query.complaintCount }}</div>
                  <div>未处理投诉信息</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" :body-style="{ padding: '0px' }">
              <div class="grid-content grid-con-3" @click="gotoCanteenManagement">
                <el-icon class="grid-con-icon">
                  <HomeFilled/>
                </el-icon>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ query.canteenCount }}</div>
                  <div>食堂</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <el-card shadow="hover">
          <template #header>
            <div class="clearfix">
              <span>每日TODOLIST</span>
              <el-button style="float: right; padding: 3px 0" text @click="handleState">添加</el-button>
              <el-button style="float: right; padding: 3px; margin-right: 10px" text @click="handleReset">重置</el-button>
            </div>
          </template>

          <el-table
              :show-header="false"
              :data="todoList"
              style="width: 100%;height: 100%;"
          >
            <el-table-column width="40">
              <template #default="scope">
                <el-checkbox v-model="scope.row.status" @change="saveTodoList"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column>
              <template #default="scope">
                <div
                    class="todo-item"
                    :class="{
										'todo-item-del': scope.row.status
									}"
                >
                  {{ scope.row.title }}
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog title="新增待办" v-model="addView" width="30%">
      <el-form label-width="70px">
        <el-form-item label="事项">
          <el-input v-model="todo.title" placeholder="请输入事务"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
				<span class="dialog-footer">
					<el-button @click="addView = false">取 消</el-button>
					<el-button type="primary" @click="addToDoList">确 定</el-button>
				</span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {reactive, ref} from 'vue';
import {useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import {getCanteenList, getUserCanteen} from "../api/canteen";
import {getUserList} from "../api/user";
import {getComplaintList} from "../api/complain";

const email: string = localStorage.getItem('ms_email') || 'vvbbnn00@foxmail.com';

const hitokoto = reactive({
  hitokoto: '',
  from: '',
  from_who: ''
})

fetch('https://v1.hitokoto.cn?c=k')
    .then(response => response.json())
    .then(data => {
      hitokoto.hitokoto = data.hitokoto;
      hitokoto.from = data.from;
      hitokoto.from_who = data.from_who;
    })
    .catch(() => {
      hitokoto.hitokoto = '网络错误';
    })

const router = useRouter();
const name = localStorage.getItem('ms_username');
const avatar = localStorage.getItem('ms_avatar');
const role = localStorage.getItem('ms_role');

const query = reactive({
  userCount: undefined as unknown as number,
  commentCount: undefined as unknown as number,
  canteenCount: undefined as unknown as number,
  community: undefined as unknown as number,
  complaintCount: undefined as unknown as number,
});

// (async () => {
//   const resp = await getDashboardInfo();
//   if (resp.data.code !== 200) {
//     ElMessage.error(resp.data.message);
//     return;
//   }
//   query.userCount = resp.data?.data?.userCount;
//   query.commentCount = resp.data?.data?.commentCount;
//   query.bookCount = resp.data?.data?.bookCount;
// })();

if (role === 'admin') {
  (async () => {
    const response = await getCanteenList({
      currentPage: 1,
      pageSize: 100
    });
    if (response.data.code !== 200) {
      ElMessage.error(response.data.message);
      return;
    }
    query.canteenCount = response.data?.total;
    // 获取交流社区评价条数
    const response3 = await getUserList({
      currentPage: 1,
      pageSize: 100
    });
    if (response3.data.code !== 200) {
      ElMessage.error(response3.data.message);
      return;
    }
    query.userCount = response3.data?.total;
  })();
} else if (role === 'canteen_admin') {
  (async () => {
    const response = await getUserCanteen();
    if (response.data.code !== 200) {
      ElMessage.error(response.data.message);
      return;
    }
    query.canteenCount = response.data?.data.length;
    // 获取公告条数

    // 获取举报条数
    const response3 = await getComplaintList({
      currentPage: 1,
      pageSize: 100
    });
    if (response3.data.code !== 200) {
      ElMessage.error(response3.data.message);
      return;
    }
    const undoneComplaints = response3.data?.list.filter( complaint => {
      return complaint.status === 'pending';
    });
    query.complaintCount = undoneComplaints.length;
  })();
}

const gotoUserManagement = () => {
  router.push('/user');
}

const gotoCanteenManagement = () => {
  router.push('/canteen');
}

const gotoComplaintManagement = () => {
  router.push('/complaint');
}

const addView = ref(false)
let todo = reactive({
  title: '',
  status: false
})

const defaultTodoList = [
  {
    title: '管理/更新食堂',
    status: false
  },
  {
    title: '账号管理',
    status: false
  },
  {
    title: '评价信息管理',
    status: false
  },
  {
    title: '交流社区信息管理',
    status: false
  },
  {
    title: '原神启动',
    status: false
  }
];

let todoList: any = reactive([]);

const loadTodoList = () => {
  const ms_todoList = localStorage.getItem('ms_todoList');
  const ms_todoList_date = localStorage.getItem('ms_todoList_date_str');

  if (ms_todoList && ms_todoList_date) {
    const date = new Date();
    const date_str = date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();
    if (ms_todoList_date === date_str) {
      todoList = reactive(JSON.parse(ms_todoList));
    } else {
      todoList = reactive(defaultTodoList);
      localStorage.setItem('ms_todoList', JSON.stringify(todoList));
      localStorage.setItem('ms_todoList_date_str', date_str);
    }
  } else {
    todoList = reactive(defaultTodoList);
    const date = new Date();
    const date_str = date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();
    localStorage.setItem('ms_todoList', JSON.stringify(todoList));
    localStorage.setItem('ms_todoList_date_str', date_str);
  }
}

const saveTodoList = () => {
  const date = new Date();
  const date_str = date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();
  localStorage.setItem('ms_todoList', JSON.stringify(todoList));
  localStorage.setItem('ms_todoList_date_str', date_str);
}

loadTodoList();

const handleReset = () => {
  todoList = reactive(defaultTodoList);
  saveTodoList();
  // Reload the page
  router.go(0);
}

const handleState = () => {
  addView.value = true
}

const addToDoList = () => {
  const newTodo = {
    title: todo.title,
    status: todo.status
  }
  todoList.push(newTodo)
  saveTodoList()
  addView.value = false
}

</script>

<style scoped>
.el-row {
  margin-bottom: 20px;
}

.grid-content {
  display: flex;
  align-items: center;
  height: 100px;
}

.grid-cont-right {
  flex: 1;
  text-align: center;
  font-size: 14px;
  color: #999;
}

.grid-num {
  font-size: 30px;
  font-weight: bold;
}

.grid-con-icon {
  font-size: 50px;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
  color: #fff;
}

.grid-con-1 .grid-con-icon {
  background: rgb(45, 140, 240);
}

.grid-con-1 .grid-num {
  color: rgb(45, 140, 240);
}

.grid-con-2 .grid-con-icon {
  background: rgb(100, 213, 114);
}

.grid-con-2 .grid-num {
  color: rgb(100, 213, 114);
}

.grid-con-3 .grid-con-icon {
  background: rgb(242, 94, 67);
}

.grid-con-3 .grid-num {
  color: rgb(242, 94, 67);
}

.user-info {
  display: flex;
  align-items: center;
  padding-bottom: 10px;
}

.user-info-cont {
  padding-left: 50px;
  flex: 1;
  font-size: 14px;
  color: #999;
}

.user-info-cont div:first-child {
  font-size: 30px;
  color: #222;
}

.user-info-list {
  font-size: 14px;
  color: #999;
  line-height: 25px;
}

.user-info-list span {
  margin-left: 70px;
}

.mgb20 {
  margin-bottom: 20px;
}

.todo-item {
  font-size: 14px;
}

.todo-item-del {
  text-decoration: line-through;
  color: #999;
}

.bracketLeft {
  text-align: left;
}

.word {
  font-size: 16px;
  text-align: center;
  word-break: normal;
  margin: 0;
  padding: 5px 15px 5px 15px;
}

.bracketRight {
  text-align: right;
}

.author {
  font-size: 14px;
  color: #999;
  float: right;
  margin-top: 10px;
}
</style>
