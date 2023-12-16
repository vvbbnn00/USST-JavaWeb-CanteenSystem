<template>
  <div>
    <div class="container">
      <div class="handle-box">
        <el-select
            v-model="query.user_id"
            placeholder="筛选用户"
            class="handle-select mr10"
            style="width: 200px;"
            clearable
            filterable
            remote
            reserve-keyword
            :remote-method="loadUserList"
        >
          <el-option
              v-for="item in userList"
              :key="item.user_id"
              :label="item.username"
              :value="item.user_id"
          ></el-option>
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      </div>
      <el-table :data="commentData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="comment_id" label="评论ID" width="100px"></el-table-column>
        <el-table-column prop="created_by" label="评论创建人"></el-table-column>
        <el-table-column prop="score" label="评论评分"></el-table-column>
        <el-table-column label="评论内容" align="center">
          <template #default="scope">
            <el-button class="el-icon-lx-comment" @click="handleInfo(scope.$index, scope.row)">
              查看详情内容
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="发布时间"></el-table-column>
        <el-table-column prop="updated_at" label="更新时间"></el-table-column>
        <el-table-column label="操作" align="center" fixed="right">
          <template #default="scope">
            <el-button text icon="Edit" @click="handleCheck(scope.row)">审核</el-button>
            <el-button text icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
            background
            layout="total, prev, pager, next"
            :current-page="query.pageIndex"
            :page-size="query.pageSize"
            :total="pageTotal"
            @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>

    <!-- 评论详情 -->
    <el-dialog title="评论详情" v-model="infoVisible" width="50%">
      <el-form label-width="100px">
        <el-table :data="filteredComment" style="width: 100%">
          <el-table-column label="评论内容" align="center">
            <template #default="scope">
              <div style="text-align: left;word-wrap: break-word;word-break: break-all">
                {{ scope.row.content }}
              </div>
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

    <!-- 审核详情 -->
    <el-dialog title="审核详情" v-model="moderationVisible">
      <el-form label-width="200px" class="pageInfo" v-loading="loadingModeration">

        <div style="color:black; font-size: 16px">
          <b>可能违反的规则:</b>
        </div>

        <div style="padding: 10px 0 20px 0">
          <el-tag v-for="item in moderation?.flagged_categories" type="danger" style="margin-left: 10px">
            {{ item.toString().toUpperCase() }}
          </el-tag>
        </div>


        <el-descriptions
            class="margin-top"
            :title="`审核结果:${ moderation?.flagged ? '不通过' : '通过' }`"
            :column="2"
            border
        >
          <el-descriptions-item
              v-for="(item, index) in moderation?.category_scores"
              width="100px"
          >
            <template #label>
              <div class="cell-item">
                <span>{{ index.toString().toUpperCase() }}</span>
              </div>
            </template>
            <template #default>
              <div class="cell-item">
                <span style="color:red; font-weight: bold" v-if="item>0.5">{{ (item * 100).toFixed(2) }}%</span>
                <span v-else>{{ (item * 100).toFixed(2) }}%</span>
              </div>
            </template>
          </el-descriptions-item>

        </el-descriptions>

      </el-form>
      <template #footer>
        <div class="dialog-footer" v-loading="loadingModeration">
          <el-button @click="moderationVisible = false">返 回</el-button>
          <el-button @click="handleDelete(selectedComment)"
                     v-if="moderation?.flagged && selectedComment.is_deleted === 0" type="danger">删除
          </el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts">
import {ref, reactive} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {Search} from '@element-plus/icons-vue';
import {getUserList, getCommentList} from '../api/userRequest';

const userList = ref([]);

const loadUserList = (query: string) => {
  // getUserList({
  //   is_deleted: 0,
  //   name: query
  // }).then(res => {
  //   let data = res.data;
  //   if (data.code !== 200) {
  //     ElMessage.error(data.message);
  //     return;
  //   }
  //   userList.value = data?.data?.list;
  // });

  getUserList().then(res => {
    let data = res.data;
    console.log(data);
    userList.value = data?.data;
  });
};

loadUserList('');

const query = reactive({
  user_id: undefined as unknown as number,
  pageIndex: 1,
  pageSize: 10
});

const selectedComment = ref();

const commentData = ref([]);
const filteredComment = ref([]);
const loadingModeration = ref(false);
let pageTotal = ref(0);
// 获取表格数据
const getData = () => {
  // getCommentList(query).then(res => {
  //   let data = res.data;
  //   if (data.code !== 200) {
  //     ElMessage.error(data.message);
  //     return;
  //   }
  //   data = data?.data;
  //
  //   commentData.value = data?.list;
  //   pageTotal.value = data?.pageTotal || 0;
  //   query.pageIndex = data?.pageIndex || 1;
  //   query.pageSize = data?.pageSize;
  // });
  getCommentList().then(res => {
    let data = res.data;

    commentData.value = data?.data;
    pageTotal.value = data?.pageTotal || 0;
    query.pageIndex = data?.pageIndex || 1;
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
  query.pageIndex = val;
  getData();
};

let idx: number = -1;

const infoVisible = ref(false);
// 详情界面
const handleInfo = (index: number, row: any) => {
  idx = index;
  filteredComment.value = commentData.value.filter(item => {
    const reviewMatch = row.comment_id === item.comment_id;
    return reviewMatch;
  });
  infoVisible.value = true;
};

// 删除操作
const handleDelete = (row: any) => {
  // 二次确认删除
  ElMessageBox.confirm('确定要删除该条评论吗？', '提示', {
    type: 'warning'
  })
      .then(async () => {
        try {
          await deleteComment({
            review_id: row.review_id,
          })
          ElMessage.success(`删除成功`);
          getData();
          moderationVisible.value = false;
        } catch (e) {
          ElMessage.error(`删除失败`);
        }
      })
      .catch(() => {
      });
};
let moderation = ref();
const moderationVisible = ref(false);
const handleCheck = async (row: any) => {
  loadingModeration.value = true;
  moderationVisible.value = true;
  selectedComment.value = row;
  try {
    const data = await getCommentModeration({review_id: row.review_id});
    if (data.data.code !== 200) {
      ElMessage.error(data.data.message);
      moderationVisible.value = false;
      return;
    }
    moderation.value = data.data?.data;
  } catch (e) {
    ElMessage.error(`获取审核信息失败`);
    moderationVisible.value = false;
  }

  loadingModeration.value = false;
  console.log(JSON.stringify(moderation.value));
};
</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
}

.handle-select {
  width: 120px;
}

.table {
  width: 100%;
  font-size: 14px;
}

.pageInfo {
  padding: 20px;
  border-radius: 10px;
  border: 2px solid #eee;
}

.mr10 {
  margin-right: 10px;
}
</style>
