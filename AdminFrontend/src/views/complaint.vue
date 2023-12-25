<template>
  <div>
    <div class="container">
      <div class="handle-box">
        <el-select v-model="query.status" placeholder="投诉状态" class="handle-select mr10" clearable>
          <el-option key="pending" label="未处理" value="pending"></el-option>
          <el-option key="replied" label="已回复" value="replied"></el-option>
          <el-option key="finished" label="已完成" value="finished"></el-option>
        </el-select>
        <el-input v-model="query.kw" placeholder="投诉标题" class="handle-input mr10"></el-input>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      </div>
      <el-table :data="complaintData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="complaintId" label="投诉信息ID"></el-table-column>
        <el-table-column prop="canteen.name" label="所属食堂"></el-table-column>
        <el-table-column label="投诉处理状态" align="center">
          <template #default="scope">
            <el-tag
                :type="scope.row.status === 'pending' ? 'danger' : scope.row.status === 'replied' ? 'warning' : scope.row.status === 'processing' ? 'primary' : 'success'"
            >
              {{ scope.row.status === 'pending' ? '未处理' : scope.row.status === 'replied' ? '已回复' : scope.row.status === 'processing' ? '处理中' : '已完成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="投诉内容详情" align="center">
          <template #default="scope">
            <el-button class="el-icon-lx-skinfill" @click="handleInfo(scope.$index, scope.row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间">
          <template #default="scope">
            <div>
              {{ parseDateTime(scope.row.createdAt) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间">
          <template #default="scope">
            <div>
              {{ parseDateTime(scope.row.updatedAt) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <el-button text :icon="Edit" @click="handleReply(scope.row)" v-if="scope.row.status !== 'finished'">
              回复
            </el-button>

            <el-button
                text
                :icon="Check"
                class="green"
                @click="handleCheck(scope.row)"
                v-if="scope.row.status !== 'finished'"
            >
              完成
            </el-button>
            <div v-if="scope.row.status === 'finished'">投诉处理已完成 !</div>
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

    <!-- 投诉详情 -->
    <el-dialog title="详情" v-model="complaintInfoVisible">
      <el-form label-width="200px">
        <el-table :data="complaintText" style="width: 100%">
          <el-table-column label="投诉标题" align="left">
            <template #default="scope">
              <div class="plugins-tips">{{ scope.row.title }}</div>
            </template>
          </el-table-column>
        </el-table>
        <el-table :data="complaintText" style="width: 100%">
          <el-table-column label="投诉内容" align="left">
            <template #default="scope">
              <MdPreview :modelValue="scope.row.content"/>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
				<span class="dialog-footer">
					<el-button @click="complaintInfoVisible = false">返 回</el-button>
				</span>
      </template>
    </el-dialog>

    <!-- 投诉回复 -->
    <el-dialog title="回复" v-model="complaintReplyVisible" width="65%">
      <el-form label-width="90px" :model="complaintReplyComment" :rules="validateForm">
        <el-form-item label="回复内容" required prop="content">
          <el-input type="textarea" v-model="complaintReplyComment.content" placeholder="请输入投诉回复内容" rows="3"></el-input>
        </el-form-item>
      </el-form>
      <el-table :data="replyData" border class="table" ref="multipleTable">
        <el-table-column type="index" :index="indexMethod" width="100"></el-table-column>
        <el-table-column prop="user.username" label="发送用户" width="150px"></el-table-column>
        <el-table-column prop="content" label="回复内容"></el-table-column>
      </el-table>
      <template #footer>
				<span class="dialog-footer">
					<el-button @click="complaintReplyVisible = false">取 消</el-button>
					<el-button type="primary" @click="saveComplaintReply">确 定</el-button>
				</span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import {ref, reactive} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {Edit, Search, Check} from '@element-plus/icons-vue';
import {parseDateTime} from "../utils/string";
import {complaintReply, getComplaintList, shutComplaint, getComplaintInfo} from "../api/complain";
import {MdPreview} from "md-editor-v3";
import {getCanteenList, getUserCanteen} from "../api/canteen";

const query = reactive({
  kw: '',
  status: '',
  currentPage: 1,
  pageSize: 10
});

const canteenData = ref([]);
const isAdmin = localStorage.getItem('ms_role');
if (isAdmin === 'admin') {
  getCanteenList({
    currentPage: 1,
    pageSize: 100
  }).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }

    canteenData.value = data?.list;
  });
  getCanteenList();
} else {
  getUserCanteen().then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }
    canteenData.value = data?.data;
  });
  getUserCanteen();
}

const validateForm = reactive({
  content: [
    {required: true, message: '请输入投诉回复', trigger: 'blur'},
    {max: 30, message: '最多输入20个字符', trigger: 'blur'}
  ]
});

const complaintData = ref([]);
let pageTotal = ref(0);

// 获取表格数据
const getData = () => {
  getComplaintList(query).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }
    if (isAdmin !== 'admin') {
      const complaintList = data?.list || [];
      const filteredComplaints = complaintList.filter(complaint => {
        return canteenData.value.some(item => item.canteenId === complaint.canteenId);
      });

      complaintData.value = filteredComplaints;
      pageTotal.value = filteredComplaints.length;
      query.currentPage = data?.currentPage || 1;
      query.pageSize = data?.pageSize;
    } else {
      complaintData.value = data?.list;
      pageTotal.value = data?.total || 0;
      query.currentPage = data?.currentPage || 1;
      query.pageSize = data?.pageSize;
    }
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

const complaintInfoVisible = ref(false);
const complaintText = ref([])
const handleInfo = (index: number, row: any) => {
  complaintText.value = complaintData.value.filter(item => {
    return row.complaintId === item.complaintId;
  });

  complaintInfoVisible.value = true;
};

const handleCheck = (row: any) => {
  // 二次确认删除
  ElMessageBox.confirm('完成该条投诉？', '提示', {
    type: 'warning'
  })
      .then(async () => {
        try {
          await shutComplaint({
            complaintId: row.complaintId,
            status: 'finished'
          });
          ElMessage.success(`投诉状态修改成功`);
          getData();
        } catch (e) {
          ElMessage.error(`投诉状态修改失败`);
        }
      })
      .catch(() => {
      });
};

const complaintReplyVisible = ref(false);
const complaintReplyComment = reactive({
  content: '',
})
const chooseComplaintId = ref(0);
const replyData = ref([]);
const getReplyData = (complaintId: number) => {
  getComplaintInfo(complaintId).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }

    replyData.value = data?.data?.comments;
  })
}
const handleReply = (row: any) => {
  getReplyData(row.complaintId);
  chooseComplaintId.value = row.complaintId;
  complaintReplyVisible.value = true;
};
const saveComplaintReply = async () => {
  complaintReplyVisible.value = false;
  try {
    await complaintReply(chooseComplaintId.value, complaintReplyComment.content);
    ElMessage.success(`回复成功`);
    getData();
  } catch (e) {
    ElMessage.error(`回复失败`);
  }
  complaintReplyComment.content = '';
};

const indexMethod = (index: number) => {
  return index + 1;
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

.handle-input {
  width: 300px;
}

.red {
  color: #F56C6C;
}

.green {
  color: #67C23A;
}

.handle-select {
  width: 120px;
}

.mr10 {
  margin-right: 10px;
}

</style>
