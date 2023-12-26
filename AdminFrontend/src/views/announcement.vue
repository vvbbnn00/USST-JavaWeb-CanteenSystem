<template>
  <div class="container">
    <el-select v-model="canteenId" placeholder="食堂名称" class="mb10" clearable>
      <el-option v-for="item in canteenList" :key="item.canteenId" :label="item.name"
                 :value="item.canteenId"></el-option>
    </el-select>
    <el-tabs
        v-model="message"
        type="card"
    >
      <el-tab-pane label="公告栏" name="first" v-if="canteenId">
        <el-table
            :data="announcementData"
            :show-header="false"
            style="width: 100%"
            highlight-current-row
        >
          <el-table-column>
            <template #default="scope">
              <span class="message-title" @click="()=>{
                handleInfo(scope.row)
              }">{{ scope.row.title }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" width="240px">
            <template #default="scope">
              <div>
                上传时间: {{ parseDateTime(scope.row.createdAt) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="更新时间" width="240px">
            <template #default="scope">
              <div>
                更新时间: {{ parseDateTime(scope.row.updatedAt) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column width="150px">
            <template #default="scope">
              <el-button
                  :icon="Delete"
                  class="red"
                  @click="handleDelete(scope.row.announcementId)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="handle-row">
          <el-button type="primary" @click="handleCreate">新增公告</el-button>
        </div>
      </el-tab-pane>
      <el-tab-pane label="请选择食堂以继续!" name="2" v-else></el-tab-pane>
    </el-tabs>

    <!-- 新增弹出框 -->
    <el-dialog title="编辑" v-model="editVisible" width="40%">
      <el-form label-width="90px" :model="createForm" :rules="validateForm">
        <el-form-item label="公告ID" required prop="announcementId" v-if="createForm.announcementId">
          <el-input v-model="createForm.announcementId" placeholder="请输入评论ID" disabled></el-input>
        </el-form-item>
        <el-form-item label="公告标题" required prop="title">
          <el-input v-model="createForm.title" placeholder="请输入公告标题"></el-input>
        </el-form-item>
        <el-form-item label="公告内容" required prop="content">
          <el-input type="textarea" v-model="createForm.content" placeholder="请输入公告内容" rows="4"
                    maxlength="100"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
				<span class="dialog-footer">
					<el-button type="primary" @click="saveAnnouncement">确 定</el-button>
          <el-button @click="editVisible = false">取 消</el-button>
          <el-button
              v-if="createForm.announcementId"
              :icon="Delete"
              class="red"
              @click="handleDelete(createForm.announcementId)"
          >
                删除
              </el-button>
				</span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup lang="ts" name="tabs">
import {ref, reactive, watch} from 'vue';
import {getCanteenList, getUserCanteen} from "../api/canteen";
import {ElMessage} from "element-plus";
import {deleteAnnouncement, getAnnouncementList, newAnnouncement, updateAnnouncement} from "../api/announcement";
import {parseDateTime} from "../utils/string";
import {Delete} from "@element-plus/icons-vue";
import {ElMessageBox} from "element-plus";

const canteenId = ref();
const message = ref('first');
const canteenList = ref([]);
const isAdmin = localStorage.getItem('ms_role');
const getCanteenListData = () => {
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

      canteenList.value = data?.list;
    });
  } else {
    getUserCanteen().then(res => {
      let data = res.data;
      if (data.code !== 200) {
        ElMessage.error(data.message);
        return;
      }
      canteenList.value = data?.data;
    });
  }
};
getCanteenListData();

const announcementData = ref([]);
const getAnnouncementData = () => {
  getAnnouncementList(canteenId.value).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }

    announcementData.value = data.list;
  });
}

watch(() => canteenId.value, () => {
  getAnnouncementData();
});

const editVisible = ref(false);
let createForm = reactive({
  announcementId: undefined as unknown as number,
  title: '',
  content: ''
});

const validateForm = reactive({
  title: [
    {required: true, message: '请输入公告标题', trigger: 'blur'},
    {max: 20, message: '最多输入20个字符', trigger: 'blur'}
  ],
  content: [
    {required: true, message: '请输入公告内容', trigger: 'blur'},
    {max: 200, message: '最多输入200个字符', trigger: 'blur'}
  ]
});

const handleDelete = (id: number) => {
  // 二次确认删除
  ElMessageBox.confirm('确定要删除吗？', '提示', {
    type: 'warning'
  })
      .then(async () => {
        try {
          await deleteAnnouncement(canteenId.value,id);
          ElMessage.success(`删除公告成功`);
          editVisible.value = false;
          getAnnouncementData();
        } catch (e) {
          ElMessage.error(`删除公告失败`);
        }
      })
      .catch(() => {
      });

};

const handleInfo = (value : any) => {
  createForm = reactive(JSON.parse(JSON.stringify(value)));
  editVisible.value = true;
}

const clearForm = () => {
  createForm = reactive({
    announcementId: undefined as unknown as number,
    title: '',
    content: ''
  });
}

const handleCreate = () => {
  clearForm();
  editVisible.value = true;
}

const saveAnnouncement = async () => {
  editVisible.value = false;
  try {
    if (createForm.announcementId) {
      await updateAnnouncement(canteenId.value,createForm.announcementId,createForm);
    } else {
      await newAnnouncement(canteenId.value,createForm);
    }
    getAnnouncementData();
    ElMessage.success(`新增/编辑公告成功`);

  } catch (e) {
    ElMessage.error(`新增/编辑公告失败`);
  }
}
</script>

<style>
.message-title {
  cursor: pointer;
}
.handle-row {
  margin-top: 30px;
}

.mb10 {
  margin-bottom: 10px;
}

.red {
  color: #F56C6C;
}
</style>