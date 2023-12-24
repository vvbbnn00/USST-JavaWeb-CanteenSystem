<template>
  <div>
    <div class="container">
      <div class="handle-box">
        <el-select v-model="query.canteenId" placeholder="食堂名称" class="handle-select mr10" clearable>
          <el-option v-for="item in canteenList" :key="item.canteenId" :label="item.name"
                     :value="item.canteenId"></el-option>
        </el-select>
        <el-select v-model="query.cuisineId" placeholder="菜系名称" class="handle-select mr10" v-if="cuisineList.length > 0" clearable>
          <el-option v-for="item in cuisineList" :key="item.cuisineId" :label="item.name"
                     :value="item.cuisineId"></el-option>
        </el-select>
        <el-select v-model="query.recommended" placeholder="是否为推荐菜品" class="handle-select mr10" clearable>
          <el-option key=true label="推荐菜品" value=true></el-option>
          <el-option key=false label="非推荐菜品" value=false></el-option>
        </el-select>
        <el-input v-model="query.kw" placeholder="关键词" class="handle-input mr10"></el-input>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button type="primary" :icon="Plus" @click="handleCreate">新增</el-button>
      </div>
      <el-table :data="itemData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
        <el-table-column prop="itemId" label="菜品Id"></el-table-column>
        <el-table-column prop="name" label="菜品名"></el-table-column>
        <el-table-column prop="cuisineId" label="关联菜系Id"></el-table-column>
        <el-table-column label="菜品图片" align="center" width="130">
          <template #default="scope">
            <el-image
                class="Pic"
                :src="scope.row.image.x256Url"
                :z-index="10"
                :preview-src-list="[scope.row.image.originalUrl]"
                preview-teleported
            >
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格"></el-table-column>
        <el-table-column prop="promotionPrice" label="优惠价格"></el-table-column>
        <el-table-column prop="compScore" label="综合评分"></el-table-column>
        <el-table-column label="推荐与否" align="center">
          <template #default="scope">
            <el-tag
                :type="scope.row.recommended === true ? 'success' : scope.row.recommended === false ? 'danger' : ''"
            >
              {{ scope.row.recommended === true ? '推荐' : '非推荐' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="菜品评论" width="150" align="center">
          <template #default="scope">
            <el-button class="el-icon-lx-calendar mb5" @click="handleComment(scope.row)">
              查看评论
            </el-button>
          </template>
        </el-table-column>
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
        <el-table-column prop="updated_at" label="更新记录时间" width="160px">
          <template #default="scope">
            <div>
              {{ parseDateTime(scope.row.updatedAt) }}
            </div>
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

    <!-- 介绍详情 -->
    <el-dialog title="介绍详情" v-model="infoVisible">
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

    <!-- 编辑弹出框 -->
    <el-dialog title="编辑" v-model="editVisible">
      <el-form label-width="120px" :model="form" :rules="validateForm" ref="formRef">
        <el-form-item label="菜品Id" required prop="itemId" v-if="form.itemId">
          <el-input v-model="form.itemId" placeholder="请输入菜品ID" disabled></el-input>
        </el-form-item>
        <el-form-item label="所属菜系" required prop="cuisineId">
          <el-select v-model="editCanteenId" placeholder="食堂名称" class="handle-select mr10" clearable>
            <el-option v-for="item in canteenList" :key="item.canteenId" :label="item.name"
                       :value="item.canteenId"></el-option>
          </el-select>
          <el-select v-model="form.cuisineId" placeholder="请选择菜系" clearable v-if="editCuisineList.length > 0">
            <el-option v-for="item in editCuisineList" :key="item.cuisineId" :label="item.name" :value="item.cuisineId"></el-option>
          </el-select>
          <div v-if="form.itemId" class="tips">
            不需要更改菜系不做选择
          </div>
        </el-form-item>

        <el-form-item label="菜品名称" required prop="name">
          <el-input v-model="form.name" placeholder="请输入菜品名字"></el-input>
        </el-form-item>
        <el-form-item label="菜品价格" required prop="price">
          <el-input v-model="form.price" type="number"></el-input>
        </el-form-item>
        <el-form-item label="菜品推荐价格">
          <el-input v-model="form.promotionPrice" required prop="promotionPrice" type="number"></el-input>
        </el-form-item>
        <el-form-item label="菜品图片">
          <el-upload
              class="avatar-uploader"
              :action="picUploadUrl"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
              :http-request="ajaxUpload"
              method="PUT"
          >
            <img v-if="imageUrl" :src="imageUrl" class="avatar" alt="upload"/>
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="菜品介绍">
          <el-input type="textarea" v-model="form.introduction" placeholder="请输入菜品介绍" rows="5"
                    maxlength="250"></el-input>
          <div>
            介绍最多 250 字符
          </div>
        </el-form-item>
        <el-form-item label="是否推荐" required prop="recommended">
          <el-radio-group v-model="form.recommended">
            <el-radio-button label=true>推荐</el-radio-button>
            <el-radio-button label=false>不推荐</el-radio-button>
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

    <!-- 菜品评论 弹出框 -->
    <el-dialog title="菜品评论" v-model="commentVisible">
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
          <el-table-column label="操作" width="200" align="center" fixed="right" v-if="isAdmin === 'admin'">
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
          <el-table-column label="不良评论标记" align="center">
            <template #default="scope">
              <el-tag
                  :type="scope.row.flagged === true ? 'danger' : scope.row.flagged === false ? 'success' : ''"
              >
                {{ scope.row.flagged === true ? '不良' : '正常' }}
              </el-tag>
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
import {Delete, Edit, Search, Plus} from '@element-plus/icons-vue';
import {parseDateTime} from "../utils/string";
import type {UploadProps} from 'element-plus'
import {getUploadUrl} from "../api";
import {getCuisineList} from "../api/cuisine";
import {ajaxUpload} from "../api/upload";
import {getCanteenList, getUserCanteen} from "../api/canteen";
import {getItemList, deleteItem, createItem, updateItem, getItemComment, deleteItemComment} from "../api/item";

const query = reactive({
  kw: '',
  canteenId: undefined as unknown as number,
  cuisineId: undefined as unknown as number,
  recommended: null as boolean | null,
  currentPage: 1,
  pageSize: 15,
});
const isAdmin = localStorage.getItem('ms_role');
const canteenList = ref([]);

watch(() => query.canteenId, (newCanteenId) => {
  if (newCanteenId) {
    getCuisine(newCanteenId);
  } else {
    cuisineList.value = [];
  }
});

const getCanteen = () => {
  if (isAdmin === 'admin') {
    getCanteenList({currentPage:1,pageSize:100}).then(res => {
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
getCanteen();

const cuisineList = ref([]);
const getCuisine = (canteenId: number) => {
  // 这里根据传入的食堂ID来获取相关的菜系列表
  getCuisineList({canteenId, currentPage: 1, pageSize: 100}).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }
    cuisineList.value = data?.list;
  });
};

const editCanteenId = ref();
const editCuisineList = ref([])
const getEditCuisine = (canteenId : number) => {
  // 这里根据传入的食堂ID来获取相关的菜系列表
  getCuisineList({canteenId, currentPage: 1, pageSize: 100}).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }
    editCuisineList.value = data?.list;
  });
};


const formRef = ref();
const picUploadUrl = ref('');
const uploadFileKey = ref('');
const imageUrl = ref('');

const validateForm = reactive({
  itemId: [
    { required: true, message: '请输入菜品ID', trigger: 'blur' },
  ],
  name: [
    { required: true, message: '请输入菜品名称', trigger: 'blur' },
    { min: 2, max: 30, message: '菜品名称长度在 2 到 30 个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入菜品价格', trigger: 'blur' },
  ],
  recommended: [
    { required: true, message: '请选择是否推荐', trigger: 'change' }
  ]
});

const itemData = ref([]);
const pageTotal = ref(0);
let form = reactive({
  itemId: undefined as unknown as number,
  name: '',
  canteenId: undefined as unknown as number,
  cuisineId: undefined as unknown as number,
  price: undefined as unknown as number,
  promotionPrice: undefined as unknown as number,
  introduction: '',
  recommended: false,
  fileKey: ''
});

watch(() => editCanteenId.value, () => {
  if (editCanteenId.value) {
    getEditCuisine(editCanteenId.value);
  } else {
    editCuisineList.value = [];
  }
});

// 获取表格数据
const getData = () => {
  getItemList(query).then(res => {
    let data = res.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }

    itemData.value = data?.list;
    pageTotal.value = data?.total || 0;
    query.currentPage = data?.currentPage || 1;
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
const handleState = async (index: number, row: any) => {
  idx = index;
  form = reactive(JSON.parse(JSON.stringify(row)));
  const uploadResp = await getUploadUrl();
  if (uploadResp.data.code !== 200) {
    ElMessage.error(uploadResp.data.message);
    return;
  }

  picUploadUrl.value = uploadResp.data.url;
  uploadFileKey.value = uploadResp.data?.fileKey;
  imageUrl.value = row.image.originalUrl || '';
  editVisible.value = true;
};

const infoVisible = ref(false);
const infoData = ref([]);
const handleInfo = (index: number, row: any) => {
  infoData.value = itemData.value.filter(item => {
    return row.itemId === item.itemId;
  });
  infoVisible.value = true;
};

// 删除操作
const handleDelete = (row: any) => {
  // 二次确认删除
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    type: 'warning'
  })
      .then(async () => {
        try {
          await deleteItem(row.itemId);
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
      if (form.itemId) {
        await updateItem(form);
      } else {
        await createItem(form, editCanteenId.value);
      }

      ElMessage.success(`新增/修改成功`);
      getData();
      editVisible.value = false;
    } catch (e) {
      ElMessage.error(`新增/修改失败`);
    }
  });
};

// 图片上传前的处理
const beforeUpload = (file: {
  type: string;
  size: number;
}) => {
  const isJPG = file.type === 'image/jpeg';
  const isPNG = file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG && !isPNG) {
    ElMessage.error('上传图片只能是 JPG/PNG 格式!');
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!');
  }

  return (isJPG || isPNG) && isLt2M;
}

const handleUploadSuccess: UploadProps['onSuccess'] = async (
    response,
    uploadFile
) => {
  imageUrl.value = URL.createObjectURL(uploadFile.raw!);
  form.fileKey = uploadFileKey.value;
}

const handleCreate = async () => {
  const uploadResp = await getUploadUrl();
  if (uploadResp.data.code !== 200) {
    ElMessage.error(uploadResp.data.message);
    return;
  }

  picUploadUrl.value = uploadResp.data.url;
  uploadFileKey.value = uploadResp.data?.fileKey;
  clearForm();
  editCanteenId.value = undefined;
  imageUrl.value = '';
  editVisible.value = true;
};

const clearForm = () => {
  form = reactive({
    itemId: undefined as unknown as number,
    name: '',
    canteenId: undefined as unknown as number,
    cuisineId: undefined as unknown as number,
    price: undefined as unknown as number,
    promotionPrice: undefined as unknown as number,
    introduction: '',
    recommended: false,
    fileKey: ''
  });
}

const commentData = ref([]);
const chooseItemId = ref(0);
const getCommentData = async () => {
  try {
    const response = await getItemComment(chooseItemId.value);
    let data = response.data;
    if (data.code !== 200) {
      ElMessage.error(data.message);
      return;
    }

    commentData.value = data?.list;
  } catch (error) {
    console.error("获取数据错误:", error);
    ElMessage.error("获取数据错误");
  }
};

const commentVisible = ref(false);
const handleComment = (row: any) => {
  chooseItemId.value = row.itemId;
  getCommentData();
  commentVisible.value = true;
};

const handleCommentDelete = (row: any) => {
  // 二次确认删除
  ElMessageBox.confirm('确定要删除评论吗？', '提示', {
    type: 'warning'
  })
      .then(async () => {
        try {
          await deleteItemComment(chooseItemId.value, row.commentId)
          ElMessage.success(`删除评论成功`);
          await getCommentData();
        } catch (e) {
          ElMessage.error(`删除评论失败`);
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

.handle-select {
  width: 150px;
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

.avatar-uploader .avatar {
  width: 180px;
  height: 180px;
  display: block;
}

.avatar-uploader{
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  width: 180px;
  height: 180px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 180px;
  height: 180px;
  text-align: center;
}

.tips {
  margin-left: 10px;
}
</style>
