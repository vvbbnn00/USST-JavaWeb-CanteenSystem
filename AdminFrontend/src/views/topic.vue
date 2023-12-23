<template>
  <div>
    <div class="container">
      <div class="handle-box">
        <el-select v-model="query.userId" placeholder="筛选用户" class="handle-select mr10" clearable>
          <el-option v-for="item in userList" :key="item.userId" :label="item.username"
                     :value="item.userId"></el-option>
        </el-select>
      </div>
      <el-table :data="topicData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="topicId" label="话题ID"></el-table-column>
        <el-table-column prop="user.username" label="创建者"></el-table-column>
        <el-table-column prop="title" label="话题标题"></el-table-column>
        <el-table-column label="话题内容" align="center">
          <template #default="scope">
            <el-button class="el-icon-lx-skinfill" @click="handleTopicMessage(scope.row)">
              查看内容
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="likeCount" label="喜欢人数" width="100px"></el-table-column>
        <el-table-column label="话题评论" align="center">
          <template #default="scope">
            <el-button class="el-icon-lx-comment" @click="handleComment(scope.row)">
              查看评论
            </el-button>
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
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
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

    <!-- 话题内容详情 -->
    <el-dialog title="话题内容" v-model="topicContentVisible">
      <el-form label-width="200px">
        <el-table :data="topicContentInfo" style="width: 100%" :show-header="false">
          <el-table-column align="left">
            <template #default="scope">
              <MdPreview :modelValue="scope.row.content"/>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
				<span class="dialog-footer">
					<el-button @click="topicContentVisible = false">返 回</el-button>
				</span>
      </template>
    </el-dialog>

    <!-- 话题评论 弹出框 -->
    <el-dialog title="话题评论" v-model="commentVisible">
      <el-form label-width="400px">
        <el-table :data="commentData" border class="table" ref="multipleTable">
          <el-table-column prop="commentId" label="评论ID"></el-table-column>
          <el-table-column prop="content" label="评论内容"></el-table-column>
          <el-table-column prop="user.username" label="评论用户"></el-table-column>
          <el-table-column prop="createdAt" label="评论时间" width="160px">
            <template #default="scope">
              <div>
                {{ parseDateTime(scope.row.createdAt) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="不良评论标记" align="center">
            <template #default="scope">
              <el-tag
                  :type="scope.row.flagged === true ? 'danger' : scope.row.flagged === false ? 'success' : ''"
              >
                {{ scope.row.flagged === true ? '不良' : '正常' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="scope">
              <el-button
                  text
                  :icon="Delete"
                  class="red"
                  @click="handleCommentDelete(scope.row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="commentVisible = false">返 回</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import {ref, reactive, watch} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {Delete, Search} from '@element-plus/icons-vue';
import {getUserList} from "../api/user";
import {deleteTopicComment, getTopicCommentList, getTopicList, deleteTopic} from "../api/topic";
import {parseDateTime} from "../utils/string";
import {MdPreview} from "md-editor-v3";

const userList = ref([]);

const query = reactive({
  userId: undefined as unknown as number,
  currentPage: 1,
  pageSize: 10
});

const getUserData = () => {
  getUserList({
    currentPage: 1,
    pageSize: 100,
  }).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }
    userList.value = data?.list;
  });
};
getUserData();

const topicData = ref([]);
let pageTotal = ref(0);

// 获取表格数据
const getData = () => {
  getTopicList(query).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }

    topicData.value = data.list;
    console.log(topicData.value)
    pageTotal.value = data?.total || 0;
    query.currentPage = data?.currentPage || 1;
    query.pageSize = data?.pageSize;
  });
};
getData();

// 下拉框直接筛选
watch(() => query.userId, () => {
  getData();
})

// 分页导航
const handlePageChange = (val: number) => {
  query.currentPage = val;
  getData();
};

const topicContentVisible = ref(false);
const topicContentInfo = ref([]);
const handleTopicMessage = (row : any) => {
  topicContentInfo.value = topicData.value.filter(item => {
    return row.topicId === item.topicId;
  });
  topicContentVisible.value = true;
}

const commentVisible = ref(false);
const commentData = ref([]);
const commentChosen = ref(0);

const getCommentData = (topicId: number) => {
  getTopicCommentList(topicId).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }

    commentData.value = data?.list;
  })
}

const handleComment = (row : any) => {
  commentChosen.value = row.topicId;
  getCommentData(row.topicId);
  commentVisible.value = true;
}

const handleCommentDelete = (row : any) => {
  ElMessageBox.confirm('确定要删除该评论吗？', '提示', {
    type: 'warning'
  })
      .then(async () => {
        try {
          await deleteTopicComment(commentChosen.value, row.commentId);
          ElMessage.success(`删除评论成功`);
          getCommentData(commentChosen.value);
        } catch (e) {
          ElMessage.error(`删除评论失败`);
        }
      })
      .catch(() => {
      });
}

const handleDelete = (row: any) => {
  // 二次确认删除
  ElMessageBox.confirm('确定要删除吗？', '提示', {
    type: 'warning'
  })
      .then(async () => {
        try {
          await deleteTopic(row.topicId);
          ElMessage.success(`删除话题成功`);
          getData();
        } catch (e) {
          ElMessage.error(`删除话题失败`);
        }
      })
      .catch(() => {
      });
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

</style>
