<template>
  <div>
    <div class="container">
      <div class="handle-box">
        <el-select v-model="query.isStarted" placeholder="投票状态选择" class="mr10" clearable>
          <el-option key=true label="已发布" value=true></el-option>
          <el-option key=false label="未开始/已结束" value=false></el-option>
        </el-select>
        <el-button type="primary" :icon="Plus" @click="handleCreate" class="flex-end">新增投票基础信息</el-button>
      </div>
      <el-table :data="voteData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="voteId" label="投票ID" width="100px"></el-table-column>
        <el-table-column label="发布状态" align="center">
          <template #default="scope">
            <el-tag
                :type="scope.row.isStarted === true ? 'success' : scope.row.isStarted === false ? 'danger' : ''"
            >
              {{ scope.row.isStarted === true ? '已发布' : '未发布/已结束' }}
            </el-tag>
          </template>
        </el-table-column>
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
            <el-button text :icon="Edit" @click="handleEdit(scope.row)">
              编辑基础信息
            </el-button>
            <el-button text :icon="CirclePlus" class="green" @click="handleOptionsEdit(scope.row)" v-if="!scope.row.isStarted">
              编辑选项
            </el-button>
            <el-button text :icon="PieChart" class="blue" @click="handleVoteResult(scope.row)">
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

    <!-- 编辑弹出框 -->
    <el-dialog title="编辑" v-model="createVisible" width="40%">
      <el-form label-width="90px" :model="editForm" :rules="validateForm">
        <el-form-item label="投票ID" required prop="voteId" v-if="editForm.voteId">
          <el-input v-model="editForm.voteId" disabled></el-input>
        </el-form-item>
        <el-form-item label="投票名称" prop="voteName">
          <el-input v-model="editForm.voteName" placeholder="请输入投票名称"></el-input>
        </el-form-item>
        <el-form-item label="投票介绍" prop="voteIntro">
          <el-input v-model="editForm.voteIntro" placeholder="请输入投票介绍"></el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
              v-model="editForm.startTime"
              type="datetime"
              placeholder="选择日期时间"
              value-format="x"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
              v-model="editForm.endTime"
              type="datetime"
              placeholder="选择日期时间"
              value-format="x"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="发布状态" v-if="editForm.voteId">
          <el-radio-group v-model="editForm.isStarted">
            <el-radio-button label=true>已发布</el-radio-button>
            <el-radio-button label=false>未发布</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
				<span class="dialog-footer">
					<el-button @click="createVisible = false">取 消</el-button>
					<el-button type="primary" @click="saveEditForm">确 定</el-button>
				</span>
      </template>
    </el-dialog>

    <!-- 投票选项 弹出框 -->
    <el-dialog title="编辑选项" v-model="voteOptionsVisible">
      <el-form label-width="400px">
        <el-table :data="voteOptionsData" border class="table" ref="multipleTable">
          <el-table-column type="index" :index="indexMethod" width="100"></el-table-column>
          <el-table-column prop="name" label="选项内容"></el-table-column>
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="scope">
              <el-button text :icon="Edit" @click="handleEditOptions(scope.row)">
                编辑
              </el-button>
              <el-button
                  text
                  :icon="Delete"
                  class="red"
                  @click="handleDeleteOptions(scope.row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="voteOptionsVisible = false">返 回</el-button>
          <el-button type="primary" :icon="Plus" @click="handleCreateVoteOptions">新增</el-button>
        </span>
      </template>
    </el-dialog>

    <!--  选项  -->
    <el-dialog title="编辑选项" v-model="createVoteOptionsVisible" width="40%">
      <el-form label-width="90px" :model="optionsName" :rules="validateForm">
        <el-form-item label="选项内容" required prop="name">
          <el-input v-model="optionsName.name" placeholder="请输入选项内容"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
				<span class="dialog-footer">
					<el-button @click="createVoteOptionsVisible = false">取 消</el-button>
					<el-button type="primary" @click="saveOption">确 定</el-button>
				</span>
      </template>
    </el-dialog>

    <!--  投票结果  -->
    <el-dialog v-model="voteResultVisible" width="70%" @close="voteResultChartVisible = false">
      <el-card shadow="hover">
        <schart v-if="voteResultChartVisible" class="schart" canvasId="bar1" :options="options"></schart>
      </el-card>
      <template #footer>
    <span class="dialog-footer">
      <el-button @click="voteResultVisible = false">取 消</el-button>
    </span>
      </template>
    </el-dialog>


  </div>
</template>

<script setup lang="ts">
import {ref, reactive, watch} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {Plus, Edit, Delete, CirclePlus, PieChart} from '@element-plus/icons-vue';
import {
  getVoteList,
  deleteVote,
  updateVote,
  newVote,
  getVoteOptionsList,
  createVoteOption,
  deleteVoteOption,
  updateVoteOption,
  getVoteResult
} from "../api/vote";
import {parseDateTime} from "../utils/string";
import Schart from 'vue-schart';
import {nextTick} from "vue";

const userId = localStorage.getItem('ms_user_id');

const query = reactive({
  isStarted: null as boolean | null,
  userId: Number(userId),
  currentPage: 1,
  pageSize: 10
});

const validateForm = reactive({
  voteName: [
    {required: true, message: '请输入投票名称', trigger: 'blur'},
    {max: 15, message: '投票名称最多15个字符', trigger: 'blur'}
  ],
  voteIntro: [
    {required: true, message: '请输入投票介绍', trigger: 'blur'},
    {max: 25, message: '投票介绍最多25个字符', trigger: 'blur'}
  ],
  startTime: [
    {required: true, message: '请选择开始时间', trigger: 'blur'}
  ],
  endTime: [
    {required: true, message: '请选择结束时间', trigger: 'blur'},
    {
      validator: (rule, value, callback) => {
        if (!value) {
          return callback(new Error('请选择结束时间'));
        }
        if (editForm.startTime && value <= editForm.startTime) {
          return callback(new Error('结束时间必须晚于开始时间'));
        }
        const currentTime = new Date().getTime();
        if (value <= currentTime && !editForm.voteId) {
          return callback(new Error('结束时间必须晚于当前时间'));
        }
        callback();
      },
      trigger: 'blur'
    }
  ],
  name: [
    {required: true, message: '请输入选项内容', trigger: 'blur'},
    {max: 15, message: '选项内容最多15个字符', trigger: 'blur'}
  ]
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
  ElMessageBox.confirm('确定要删除该投票吗？', '提示', {
    type: 'warning'
  })
      .then(async () => {
        let res = await deleteVote(row.voteId);
        if (res.data.code !== 200) {
          ElMessage.error(res.data.message);
          return;
        }
        ElMessage.success('删除成功');
        getData();
      })
      .catch(() => {
      });
};

let editForm = reactive({
  voteId: undefined as unknown as number,
  voteName: '',
  voteIntro: '',
  startTime: '',
  endTime: '',
  isStarted: false,
});
const handleCreate = () => {
  clearForm();
  createVisible.value = true;
};

const saveEditForm = async () => {
  createVisible.value = false;
  try {
    if (editForm.voteId) {
      await updateVote(editForm);
    } else {
      await newVote(editForm);
    }
    getData();
    ElMessage.success(`编辑投票成功`);
  } catch (e) {
    ElMessage.error(`编辑投票失败`);
  }
};

const handleEdit = async (row: any) => {
  editForm = reactive(JSON.parse(JSON.stringify(row)));
  createVisible.value = true;
};


const clearForm = () => {
  editForm = reactive({
    voteId: undefined as unknown as number,
    voteName: '',
    voteIntro: '',
    startTime: '',
    endTime: '',
    isStarted: false,
  });
}

const voteOptionsData = ref([]);
const voteSelect = ref(0);
const voteOptionsVisible = ref(false);
const indexMethod = (index: number) => {
  return index + 1;
}
const getVoteOption = () => {
  getVoteOptionsList(voteSelect.value).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }

    voteOptionsData.value = data?.list;
  });
};

const handleOptionsEdit = async (row: any) => {
  voteSelect.value = row.voteId;
  voteOptionsVisible.value = true;
  getVoteOption();
};

const createVoteOptionsVisible = ref(false);
const optionsName = reactive({
  voteOptionId: undefined as unknown as number,
  name: ''
});
const handleCreateVoteOptions = () => {
  optionsName.voteOptionId = undefined as unknown as number;
  optionsName.name = '';
  createVoteOptionsVisible.value = true;
};

const saveOption = async () => {
  createVoteOptionsVisible.value = false;
  try {
    if (optionsName.voteOptionId) {
      await updateVoteOption(voteSelect.value, optionsName.voteOptionId, optionsName.name);
    } else {
      await createVoteOption(voteSelect.value, optionsName.name);
    }
    getVoteOption();
    ElMessage.success(`创建选项成功`);
  } catch (e) {
    ElMessage.error(`创建选项失败`);
  }
};

const handleEditOptions = (row: any) => {
  optionsName.voteOptionId = row.voteOptionId;
  optionsName.name = row.name;
  createVoteOptionsVisible.value = true;
};

const handleDeleteOptions = (row: any) => {
  // 二次确认删除
  ElMessageBox.confirm('确定要删除该选项吗？', '提示', {
    type: 'warning'
  })
      .then(async () => {
        let res = await deleteVoteOption(voteSelect.value,row.voteOptionId);
        if (res.data.code !== 200) {
          ElMessage.error(res.data.message);
          return;
        }
        ElMessage.success('删除选项成功');
        getVoteOption();
      })
      .catch(() => {
      });
};

const voteResultVisible = ref(false);
const voteResultData = ref([]);
const getResultData = async (id: number) => {
  await getVoteResult(id).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }

    voteResultData.value = data?.list;
  });
}

let options = reactive({
  type: '',
  title: {
    text: ''
  },
  bgColor: '',
  labels: [],
  datasets: [
    {
      label: '',
      data: []
    }
  ]
});
const voteResultChartVisible = ref(false);

const handleVoteResult = async (row: any) => {
  await getResultData(row.voteId);
  options = {
    type: 'bar',
    title: {
      text:row.voteName,
    },
    bgColor: '#fbfbfb',
    labels: voteResultData.value.map(item => item.name),
    datasets:[
      {
        data: voteResultData.value.map(item => item.count),
        label: '投票结果'
      }
    ]
  }
  voteResultVisible.value = true;
  nextTick(() => {
    voteResultChartVisible.value = true;
  })
};

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

.schart {
  width: 100%;
  height: 500px;
}

</style>
