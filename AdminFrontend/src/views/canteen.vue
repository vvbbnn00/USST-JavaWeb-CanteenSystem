<template>
  <div>
    <div class="container">
      <div class="handle-box" v-if="isAdmin === 'admin'">
        <el-input v-model="query.kw" placeholder="食堂名称" class="handle-input mr10"></el-input>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button type="primary" :icon="Plus" @click="handleCreate">新增</el-button>
      </div>
      <el-table :data="canteenData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="canteenId" label="食堂ID"></el-table-column>
        <el-table-column prop="name" label="食堂名"></el-table-column>
        <el-table-column prop="location" label="食堂地址"></el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <el-button text :icon="Edit" @click="handleEdit(scope.$index, scope.row)">
              编辑
            </el-button>

            <el-button v-if="isAdmin === 'admin'"
                text
                :icon="Delete"
                class="red"
                @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
        <!--    显示食堂介绍     -->
        <el-table-column label="介绍" width="150" align="center">
          <template #default="scope">
            <el-button class="el-icon-lx-skinfill" @click="handleInfo(scope.$index, scope.row)">
              查看介绍
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="160px">
          <template #default="scope">
            <div>
              {{ parseDateTime(scope.row.createdAt) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新记录时间" width="160px">
          <template #default="scope">
            <div>
              {{ parseDateTime(scope.row.updatedAt) }}
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination" v-if="isAdmin === 'admin'">
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

    <!-- 新增弹出框 -->
    <el-dialog title="新增食堂" v-model="createVisible" width="40%">
      <el-form label-width="90px" :model="createForm" :rules="validateForm">
        <el-form-item label="食堂ID" required prop="canteenId" v-if="createForm.canteenId">
          <el-input v-model="createForm.canteenId" placeholder="请输入食堂ID" disabled></el-input>
        </el-form-item>
        <el-form-item label="食堂名称" required prop="name">
          <el-input v-model="createForm.name" placeholder="请输入食堂名称"></el-input>
        </el-form-item>
        <el-form-item label="食堂地址" required prop="location">
          <el-input v-model="createForm.location" placeholder="请输入食堂地址"></el-input>
        </el-form-item>
        <el-form-item label="食堂介绍">
          <el-input type="textarea" v-model="createForm.introduction" placeholder="请输入食堂介绍" rows="5"
                    maxlength="250"></el-input>
          <div>
            介绍最多 250 字符
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
				<span class="dialog-footer">
					<el-button @click="createVisible = false">取 消</el-button>
					<el-button type="primary" @click="saveNewCanteen">确 定</el-button>
				</span>
      </template>
    </el-dialog>

    <!-- 食堂介绍 弹出框 -->
    <el-dialog title="食堂介绍" v-model="infoVisible">
      <el-form label-width="200px">
        <el-table :data="infoData" style="width: 100%">
          <el-table-column label="简介" align="left">
            <template #default="scope">
              <div class="plugins-tips">{{ scope.row.introduction }}</div>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
				<span class="dialog-footer">
					<el-button @click="infoVisible = false">返 回</el-button>
				</span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import {ref, reactive} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {Search, Plus, Edit, Delete} from '@element-plus/icons-vue';
import {getCanteenList, deleteCanteen, updateCanteen, newCanteen, getUserCanteen} from "../api/canteen";
import {parseDateTime} from "../utils/string";

const isAdmin = localStorage.getItem('ms_role');

const query = reactive({
  canteen_id: undefined as unknown as number,
  kw: '',
  currentPage: 1,
  pageSize: 10
});

const validateForm = reactive({
  name: [
    {required: true, message: '请输入食堂名称', trigger: 'blur'},
    {max: 20, message: '最多输入20个字符', trigger: 'blur'}
  ],
  location: [
    {required: true, message: '请输入食堂地址', trigger: 'blur'},
    {max: 30, message: '最多输入30个字符', trigger: 'blur'}
  ],
  introduction: [
    {message: '请输入食堂介绍', trigger: 'blur'},
    {max: 250, message: '最多输入250个字符', trigger: 'blur'}
  ],
});

const canteenData = ref([]);
let pageTotal = ref(0);

// 获取表格数据
const getData = () => {
  if (isAdmin === 'admin') {
    getCanteenList(query).then(res => {
      let data = res.data;
      if (data.code !== 200) {
        ElMessage.error(data.message);
        return;
      }

      canteenData.value = data?.list;
      pageTotal.value = data?.total || 0;
      query.currentPage = data?.currentPage || 1;
      query.pageSize = data?.pageSize;
    });
  } else {
    getUserCanteen().then(res => {
      let data = res.data;
      if (data.code !== 200) {
        ElMessage.error(data.message);
        return;
      }
      canteenData.value = data?.data;
      pageTotal.value = data?.data?.length;
      query.currentPage = data?.currentPage || 1;
      query.pageSize = data?.pageSize;
    });
  }
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

const infoVisible = ref(false);
const infoData = ref([]);
const handleInfo = (index: number, row: any) => {
  infoData.value = canteenData.value.filter(item => {
    return row.canteenId === item.canteenId;
  });
  infoVisible.value = true;
};

const createVisible = ref(false);
const handleDelete = (row: any) => {
  console.log(row.canteenId)
  // 二次确认删除
  ElMessageBox.confirm('确定要删除/恢复吗？', '提示', {
    type: 'warning'
  })
      .then(async () => {
        try {
          await deleteCanteen(row.canteenId)
          ElMessage.success(`删除/恢复成功`);
          getData();
        } catch (e) {
          ElMessage.error(`删除/恢复失败`);
        }
      })
      .catch(() => {
      });
};

let createForm = reactive({
  canteenId: undefined as unknown as number,
  name: '',
  location: '',
  introduction: '',
});
const handleCreate = () => {
  clearForm();
  createVisible.value = true;
};

const saveNewCanteen = async () => {
  createVisible.value = false;
  try {

    if (createForm.canteenId) {
      await updateCanteen(createForm);
    } else {
      await newCanteen(createForm);
    }

    ElMessage.success(`新增/编辑成功`);
    getData();
  } catch (e) {
    ElMessage.error(`新增/编辑失败`);
  }
};

const handleEdit = async (index: number, row: any) => {
  createForm = reactive(JSON.parse(JSON.stringify(row)));
  createVisible.value = true;
};


const clearForm = () => {
  createForm = reactive({
    canteenId: undefined as unknown as number,
    name: '',
    location: '',
    introduction: '',
  });
}

</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
}

.handle-input {
  width: 300px;
}

.table {
  width: 100%;
  font-size: 14px;
}

.red {
  color: #F56C6C;
}

.mr10 {
  margin-right: 10px;
}

</style>
