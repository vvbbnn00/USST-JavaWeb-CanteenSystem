<template>
  <div>
    <div class="container">
      <div class="handle-box">
        <el-select v-model="query.available" placeholder="是否注销" class="handle-select mr10" clearable>
          <el-option key=true label="正常" value=true></el-option>
          <el-option key=false label="已注销" value=false></el-option>
        </el-select>
        <el-select v-model="query.isVerified" placeholder="是否认证" class="handle-select mr10" clearable>
          <el-option key=true label="已认证" value=true></el-option>
          <el-option key=false label="未认证" value=false></el-option>
        </el-select>
        <el-select v-model="query.role" placeholder="用户角色" class="handle-select mr10" clearable>
          <el-option key="0" label="用户" value="user"></el-option>
          <el-option key="1" label="管理员" value="admin"></el-option>
          <el-option key="2" label="食堂管理员" value="canteen_admin"></el-option>
        </el-select>
        <el-input v-model="query.kw" placeholder="用户名" class="handle-input mr10"></el-input>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      </div>
      <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="userId" label="用户ID" width="100px"></el-table-column>
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column label="用户头像" align="center" width="100">
          <template #default="scope">
            <el-image
                class="Pic"
                :src="scope.row.avatar"
                :z-index="10"
                :preview-src-list="[scope.row.avatar]"
                preview-teleported
            >
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="权限">
          <template #default="scope">
            <el-tag
                :type="scope.row.role === 'admin' ? 'primary' : scope.row.role === 'canteen_admin' ? 'success' : ''"
            >
              {{ scope.row.role === 'admin' ? '管理员' : scope.row.role === 'canteen_admin' ? '食堂管理员' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱地址"></el-table-column>
        <el-table-column prop="level" label="等级" width="60px"></el-table-column>
        <el-table-column prop="point" label="评分" width="60px"></el-table-column>
        <el-table-column prop="name" label="用户昵称"></el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="160px">
          <template #default="scope">
            <div>
              {{ parseDateTime(scope.row.createdAt) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="updated_at" label="更新记录时间" width="160px">
          <template #default="scope">
            <div>
              {{ parseDateTime(scope.row.updatedAt) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginAt" label="最后登录时间" width="160px">
          <template #default="scope">
            <div>
              {{ parseDateTime(scope.row.lastLoginAt) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="账户认证状态" align="center">
          <template #default="scope">
            <el-tag
                :type="scope.row.isVerified === true ? 'success' : scope.row.isVerified === false ? 'danger' : ''"
            >
              {{ scope.row.isVerified === true ? '已认证' : '未认证' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="账户可用状态" align="center">
          <template #default="scope">
            <el-tag
                :type="scope.row.available === true ? 'success' : scope.row.available === false ? 'danger' : ''"
            >
              {{ scope.row.available === true ? '正常' : '已注销' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="scope">
            <el-button text :icon="Edit" @click="handleState(scope.$index, scope.row)">
              编辑
            </el-button>
            <el-button v-if="scope.row.role !== 'admin'"
                text
                :icon="Delete"
                class="red"
                @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
            background
            layout="total, prev, pager, next"
            :current-page="query.currentPage"
            :page-size="query.pageSize"
            :total="pageTotal"
            @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>

    <!-- 编辑弹出框 -->
    <el-dialog title="编辑" v-model="editVisible">
      <el-form label-width="120px" :model="form" :rules="validateForm" ref="formRef">
        <el-form-item label="用户ID" required prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" disabled></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" disabled></el-input>
        </el-form-item>
        <el-form-item label="用户名" required prop="username">
          <el-input v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item label="用户权限" required prop="role">
          <el-radio-group v-model="form.role">
            <el-radio-button label="user">用户</el-radio-button>
            <el-radio-button label="canteen_admin">食堂管理员</el-radio-button>
            <el-radio-button label="admin">管理员</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="重置密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入新密码" show-password type="password"></el-input>
        </el-form-item>
        <el-form-item label="用户可用状态" required prop="available">
          <el-radio-group v-model="form.available">
            <el-radio-button label=true>可用</el-radio-button>
            <el-radio-button label=false>不可用</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户认证状态" required prop="available">
          <el-radio-group v-model="form.isVerified">
            <el-radio-button label=true>已认证</el-radio-button>
            <el-radio-button label=false>未认证</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
				<span class="dialog-footer">
					<el-button @click="editVisible = false">取 消</el-button>
					<el-button type="primary" @click="saveEdit">确 定</el-button>
				</span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">


import {ref, reactive} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {getUserList, updateUser, deleteUser} from '../api/user';
import {Delete, Edit, Search} from '@element-plus/icons-vue';
import {parseDateTime} from "../utils/string";

interface UserItem {
  user_id: number;
  username: string;
  email: string;
  register_ip: string;
  register_at: string;
  last_login_ip: string;
  last_login_at: string;
  available: number;
}

const query = reactive({
  kw: '',
  role: '',
  available: null as boolean | null,
  isVerified: null as boolean | null,
  currentPage: 1,
  pageSize: 10,
});

const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d!@#$%^&*()_+}{":;'?/>.<,`~\[\]\\|=-]{8,20}$/;
const USERNAME_PATTERN = /^[a-zA-Z0-9_]{3,20}$/;

const formRef = ref();

const validateForm = reactive({
  username: [
    {required: true, message: '请输入用户名', trigger: 'blur'},
    {pattern: USERNAME_PATTERN, message: '用户名必须是3-20位的字母、数字或下划线', trigger: 'blur'}
  ],
  role: [
    {required: true, message: '请选择用户权限', trigger: 'blur'}
  ],
  password: [
    {required: false, message: '请输入密码', trigger: 'blur'},
    {pattern: PASSWORD_PATTERN, message: '密码必须包含大小写字母和数字的组合，不能使用特殊字符，长度在8-20之间', trigger: 'blur'}
  ],
});

const tableData = ref<UserItem[]>([]);
const pageTotal = ref(0);
let form = reactive({
  userId: undefined as unknown as number,
  username: '',
  role: '',
  available: null as boolean | null,
  password: '',
  email: '',
  isVerified: null as boolean | null,
});

// 获取表格数据
const getData = () => {
  getUserList(query).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }
    console.log(data);

    tableData.value = data?.list;
    pageTotal.value = data?.total || 0;
    query.currentPage = data?.pageIndex || 1;
    query.pageSize = data?.pageSize;
  });

};
getData();

// 查询操作
const handleSearch = () => {
  getData();
};

// 分页导航
const handlePageChange = (val: number) => {
  query.currentPage = val;
  getData();
};

let idx: number = -1;
// 修改状态
const handleState = (index: number, row: any) => {
  idx = index;
  form = reactive(JSON.parse(JSON.stringify(row)));;

  editVisible.value = true;
};

// 删除操作
const handleDelete = (row: any) => {
  // 二次确认删除
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    type: 'warning'
  })
      .then(async () => {
        try {
          await deleteUser(row.userId);
          ElMessage.success(`删除成功`);
          getData();
        } catch (e) {
          ElMessage.error(`删除失败`);
        }
      })
      .catch(() => {
      });
};

// 表格编辑时弹窗和保存
const editVisible = ref(false);

const saveEdit = async () => {
  formRef.value.validate(async (valid: boolean) => {
    if (!valid) {
      return false;
    }
    try {
      const result = await updateUser(form);
      if (result.data.code !== 200) {
        ElMessage.error(result.data.message);
        return;
      }
      ElMessage.success(`修改成功`);
      getData();
      editVisible.value = false;
    } catch (e) {
      ElMessage.error(`更新失败`);
    }
  });
};

</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
}

.handle-select {
  width: 120px;
}

.handle-input {
  width: 300px;
}

.red {
  color: #F56C6C;
}

.table {
  width: 100%;
  font-size: 14px;
}

.mr10 {
  margin-right: 10px;
}

.Pic{
  display: block;
  margin: auto;
  width: 90px;
  height: 90px;
}
</style>
