<template>
  <div>
    <div class="container">
      <div class="handle-box">
        点击此处选择食堂:
        <el-select v-model="query.canteenId" placeholder="食堂选择" class="mr10" clearable>
          <el-option v-for="item in canteenList" :key="item.canteenId" :label="item.name"
                     :value="item.canteenId"></el-option>
        </el-select>
        <el-button type="primary" :icon="Plus" @click="handleCreate" class="flex-end" v-if="query.canteenId">新增菜系</el-button>
      </div>
      <el-table :data="cuisineData" border class="table" ref="multipleTable" header-cell-class-name="table-header" empty-text="请选择食堂以继续">
        <el-table-column prop="cuisineId" label="菜系ID"></el-table-column>
        <el-table-column prop="name" label="菜系名称"></el-table-column>
        <el-table-column prop="canteenId" label="所属食堂ID"></el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160px">
          <template #default="scope">
            <div>
              {{ parseDateTime(scope.row.createdAt) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="160px">
          <template #default="scope">
            <div>
              {{ parseDateTime(scope.row.updatedAt) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <el-button text :icon="Edit" @click="handleEdit(scope.$index, scope.row)">
              编辑
            </el-button>

            <el-button
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

    <!-- 新增弹出框 -->
    <el-dialog title="编辑" v-model="createVisible" width="40%">
      <el-form label-width="90px" :model="createForm" :rules="validateForm">
        <el-form-item label="菜系ID" required prop="canteenId" v-if="createForm.cuisineId">
          <el-input v-model="createForm.cuisineId" placeholder="请输入菜系ID" disabled></el-input>
        </el-form-item>
        <el-form-item label="菜系名称" required prop="name">
          <el-input v-model="createForm.name" placeholder="请输入菜系名称"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
				<span class="dialog-footer">
					<el-button @click="createVisible = false">取 消</el-button>
					<el-button type="primary" @click="saveNewCuisine">确 定</el-button>
				</span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import {ref, reactive, watch} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {Plus, Edit, Delete} from '@element-plus/icons-vue';
import {getUserCanteen} from "../api/canteen";
import {deleteCuisine, getCuisineList, updateCuisine, createCuisine} from "../api/cuisine";
import {parseDateTime} from "../utils/string";

const canteenList = ref([]);

const query = reactive({
  canteenId: undefined as unknown as number,
  currentPage: 1,
  pageSize: 10
});

const getCanteenList = () => {
  getUserCanteen().then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }
    canteenList.value = data?.data;
  });
};
getCanteenList();

const validateForm = reactive({
  name: [
    {required: true, message: '请输入菜品名称', trigger: 'blur'},
    {max: 20, message: '最多输入20个字符', trigger: 'blur'}
  ]
});

const cuisineData = ref([]);
let pageTotal = ref(0);

// 获取表格数据
const getData = () => {
  getCuisineList(query).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }
    console.log(data)
    cuisineData.value = data?.list;
    pageTotal.value = data?.total || 0;
    query.currentPage = data?.currentPage || 1;
    query.pageSize = data?.pageSize;
  });
};
getData();

// 下拉框直接查询
watch(() => query.canteenId, (canteenId) => {
  if (canteenId) {
    getData();
  } else {
    cuisineData.value = null;
  }
})

// 分页导航
const handlePageChange = (val: number) => {
  query.currentPage = val;
  getData();
};

const createVisible = ref(false);
const handleDelete = (row: any) => {
  // 二次确认删除
  ElMessageBox.confirm('确定要删除吗？', '提示', {
    type: 'warning'
  })
      .then(async () => {
        try {
          await deleteCuisine({
            canteenId:query.canteenId,
            cuisineId:row.cuisineId
          })
          ElMessage.success(`删除菜品成功`);
          getData();
        } catch (e) {
          ElMessage.error(`删除菜品失败`);
        }
      })
      .catch(() => {
      });
};

let createForm = reactive({
  canteenId: undefined as unknown as number,
  cuisineId: undefined as unknown as number,
  name: '',
});
const handleCreate = () => {
  clearForm();
  createVisible.value = true;
};

const saveNewCuisine = async () => {
  createVisible.value = false;
  try {

    if (createForm.cuisineId) {
      await updateCuisine(createForm);
    } else {
      await createCuisine(createForm);
    }

    ElMessage.success(`新增/编辑菜系成功`);
    getData();
  } catch (e) {
    ElMessage.error(`新增/编辑菜系失败`);
  }
};

const handleEdit = async (index: number, row: any) => {
  createForm = reactive(JSON.parse(JSON.stringify(row)));
  createVisible.value = true;
};


const clearForm = () => {
  createForm = reactive({
    canteenId: undefined as unknown as number,
    cuisineId: undefined as unknown as number,
    name: '',
  });
  createForm.canteenId = query.canteenId;
}

</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
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
