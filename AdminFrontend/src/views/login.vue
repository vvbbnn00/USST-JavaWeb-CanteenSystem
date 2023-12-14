<template>
  <div class="login-wrap">
    <div class="ms-login">
      <div class="ms-title">食堂社区管理系统</div>
      <el-form :model="param" :rules="rules" ref="login" label-width="0px" class="ms-content">
        <el-form-item prop="username">
          <el-input v-model="param.username" placeholder="请输入您的用户名">
            <template #prepend>
              <el-button :icon="User"></el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
              type="password"
              placeholder="请输入您的密码"
              v-model="param.password"
              @keyup.enter="submitForm(login)"
          >
            <template #prepend>
              <el-button :icon="Lock"></el-button>
            </template>
          </el-input>
        </el-form-item>
        <div class="login-btn">
          <el-button type="primary" @click="submitForm(login)" :disabled="loginLoading">登录</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, reactive} from 'vue';
import {useTagsStore} from '../store/tags';
import {usePermissStore} from '../store/permiss';
import {useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';
import type {FormInstance, FormRules} from 'element-plus';
import {Lock, User} from '@element-plus/icons-vue';

interface LoginInfo {
  username: string;
  password: string;
}

const router = useRouter();
const param = reactive<LoginInfo>({
  username: '',
  password: ''
});

const rules: FormRules = {
  username: [
    {
      required: true,
      message: '请输入用户名',
      trigger: 'blur'
    }
  ],
  password: [{required: true, message: '请输入密码', trigger: 'blur'}]
};

const permiss = usePermissStore();
const login = ref<FormInstance>();
const loginLoading = ref<boolean>(false);

const submitForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  // formEl.validate((valid: boolean) => {
  //   if (valid) {
  //     loginLoading.value = true;
  //     passwordLogin(param.username, param.password).then(response => {
  //       loginLoading.value = false;
  //       if (response.code === 200) {
  //         const token = response.data.token;
  //         ElMessage.success('登录成功');
  //         localStorage.setItem('ms_username', response?.data?.user?.username);
  //         localStorage.setItem('ms_email', response?.data?.user?.email);
  //         localStorage.setItem('ms_user_id', response?.data?.user?.user_id);
  //         const keys = response?.data?.user?.role;
  //         permiss.handleSet(keys);
  //         localStorage.setItem('ms_token', token);
  //         router.push('/dashboard');
  //       } else {
  //         ElMessage.error(response.message);
  //       }
  //     }).catch(() => {
  //       loginLoading.value = false;
  //       ElMessage.error('请求失败');
  //     });
  //   }
  // });
  formEl.validate((valid: boolean) => {
    if (valid) {
      ElMessage.success('登录成功');
      localStorage.setItem('ms_username', param.username);
      const keys = permiss.defaultList[param.username == 'admin' ? 'admin' : 'user'];
      permiss.handleSet(keys);
      localStorage.setItem('ms_keys', JSON.stringify(keys));
      router.push('/');
    } else {
      ElMessage.error('登录成功');
      return false;
    }
  });
};


const tags = useTagsStore();
tags.clearTags();
</script>

<style scoped>
.login-wrap {
  position: relative;
  width: 100%;
  height: 100%;
  background-image: url(../assets/img/login-bg.webp);
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
}

.ms-title {
  width: 100%;
  line-height: 50px;
  text-align: center;
  font-size: 20px;
  color: #4b4b4b;
  border-bottom: 1px solid #bfbfbf;
  font-weight: bold;
}

.ms-login {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 350px;
  margin: -190px 0 0 -175px;
  border-radius: 5px;
  background: rgba(255, 255, 255, 0.5);
  overflow: hidden;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  padding: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
}

.ms-login:hover {
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.3);
}

.ms-content {
  padding: 30px 30px;
}

.login-btn {
  text-align: center;
}

.login-btn button {
  width: 100%;
  height: 36px;
  margin-bottom: 10px;
}

</style>
