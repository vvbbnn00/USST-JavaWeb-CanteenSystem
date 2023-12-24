<template>
  <div>
    <div class="container">
      <div class="handle-box">
        <el-select v-model="query.isStarted" placeholder="投票状态选择" class="mr10" clearable>
          <el-option key=true label="进行中" value=true></el-option>
          <el-option key=false label="未开始/已结束" value=false></el-option>
        </el-select>
        <el-button type="primary" :icon="Plus" @click="handleCreate" class="flex-end">新增投票</el-button>
      </div>
      <el-table :data="voteData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="voteId" label="投票ID"></el-table-column>
        <el-table-column prop="voteName" label="投票名称"></el-table-column>
        <el-table-column prop="voteIntro" label="投票介绍"></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160px">
          <template #default="scope">
            <div>
              {{ parseDateTime(scope.row.startTime) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160px">
          <template #default="scope">
            <div>
              {{ parseDateTime(scope.row.endTime) }}
            </div>
          </template>
        </el-table-column>
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
        <el-table-column label="操作" width="270px" align="center" fixed="right">
          <template #default="scope">
            <el-button text :icon="Edit" @click="handleEdit(scope.$index, scope.row)">
              编辑基础信息
            </el-button>
            <el-button text :icon="CirclePlus" class="green" @click="" v-if="!scope.row.isStarted">
              编辑选项
            </el-button>
            <el-button text :icon="PieChart" class="blue" @click="" v-if="scope.row.isStarted">
              查看统计信息
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

      </el-form>
      <template #footer>
				<span class="dialog-footer">
					<el-button @click="createVisible = false">取 消</el-button>
					<el-button type="primary" @click="">确 定</el-button>
				</span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import {ref, reactive, watch} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {Plus, Edit, Delete, CirclePlus, PieChart} from '@element-plus/icons-vue';
import {getVoteList} from "../api/vote";
import {parseDateTime} from "../utils/string";

const query = reactive({
  isStarted: null as boolean | null,
  currentPage: 1,
  pageSize: 10
});

const validateForm = reactive({
});

const voteData = ref([]);
let pageTotal = ref(0);

// 获取表格数据
const getData = () => {
  getVoteList(query).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }

    voteData.value = data?.list;
    pageTotal.value = data?.total || 0;
    query.currentPage = data?.currentPage || 1;
    query.pageSize = data?.pageSize;
  });
};
getData();

watch(() => query.isStarted, () => {
  getData();
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

      })
      .catch(() => {
      });
};

let createForm = reactive({

});
const handleCreate = () => {
  clearForm();
  createVisible.value = true;
};

const saveEdit = () => {

};

const handleEdit = async (index: number, row: any) => {
  createForm = reactive(JSON.parse(JSON.stringify(row)));
  createVisible.value = true;
};


const clearForm = () => {

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

.green {
  color: #67C23A;
}

.blue {
  color: rgba(64, 158, 255, 0.93);
}

</style>
