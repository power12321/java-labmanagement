<template>
  <div class="register">
    <el-form ref="registerRef" :model="registerForm" :rules="registerRules" class="register-form">
      <h3 class="title">实验室管理服务平台</h3>
      <!-- 用户账号 -->
      <el-form-item prop="account">
        <el-input
            v-model="registerForm.account"
            type="text"
            size="large"
            auto-complete="off"
            placeholder="账号">
          <template #prefix>
            <svg-icon icon-class="user" class="el-input__icon input-icon" />
          </template>
        </el-input>
      </el-form-item>
      <!-- 用户姓名 -->
      <el-form-item prop="name">
        <el-input
            v-model="registerForm.name"
            type="text"
            size="large"
            auto-complete="off"
            placeholder="姓名">
          <template #prefix>
            <svg-icon icon-class="user" class="el-input__icon input-icon" />
          </template>
        </el-input>
      </el-form-item>
      <!-- 角色 -->
      <el-form-item prop="roleId">
        <el-select
            v-model="registerForm.roleId"
            placeholder="选择角色"
            size="large"
            @change="handleRoleChange">
          <el-option label="管理员" value="0"></el-option>
          <el-option label="教师" value="1"></el-option>
          <el-option label="学生" value="2"></el-option>
        </el-select>
      </el-form-item>
      <!-- 性别 -->
      <el-form-item prop="roleId">
        <el-select
            v-model="registerForm.sex"
            placeholder="选择性别"
            size="large"
        >
          <el-option label="男" :value="1"></el-option>
          <el-option label="女" :value="2"></el-option>
        </el-select>
      </el-form-item>
      <!-- 手机号 -->
      <el-form-item prop="phone">
        <el-input
            v-model="registerForm.phone"
            type="text"
            size="large"
            placeholder="手机号">
          <template #prefix>
            <svg-icon icon-class="phone" class="el-input__icon input-icon" />
          </template>
        </el-input>
      </el-form-item>
      <!-- 学生相关字段 -->
      <div v-if="registerForm.roleId === '2'">
        <!-- 学院 -->
        <el-form-item prop="college">
          <el-select
               size="large"
              v-model="registerForm.college"
              placeholder="请选择"
              clearable
              @change="selectChange"
          >
            <el-option
                v-for="option in collegeList"
                :key="option.value"
                :label="option.label"
                :value="option.value"
            />
          </el-select>
        </el-form-item>
        <!-- 专业 -->
        <el-form-item prop="major">
          <el-select
              size="large"
              v-model="registerForm.major"
              placeholder="请选择"
              clearable

          >
            <el-option
                v-for="option in majorList"
                :key="option.value"
                :label="option.label"
                :value="option.value"
            />
          </el-select>
        </el-form-item>
        <!-- 年级 -->
        <el-form-item prop="grade">
          <el-input
              v-model="registerForm.grade"
              type="text"
              size="large"
              auto-complete="off"
              placeholder="年级">
            <template #prefix>
              <svg-icon icon-class="calendar" class="el-input__icon input-icon" />
            </template>
          </el-input>
        </el-form-item>
      </div>
      <!-- 密码 -->
      <el-form-item prop="password">
        <el-input
            v-model="registerForm.password"
            type="password"
            size="large"
            autocomplete="new-password"
            placeholder="密码"
            @keyup.enter="handleRegister">
          <template #prefix>
            <svg-icon icon-class="password" class="el-input__icon input-icon" />
          </template>
        </el-input>
      </el-form-item>
      <!-- 确认密码 -->
      <el-form-item prop="confirmPassword">
        <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            size="large"
            autocomplete="new-password"
            placeholder="确认密码"
            @keyup.enter="handleRegister">
          <template #prefix>
            <svg-icon icon-class="password" class="el-input__icon input-icon" />
          </template>
        </el-input>
      </el-form-item>
      <!-- 注册按钮 -->
      <el-form-item style="width:100%;">
        <el-button
            :loading="loading"
            size="large"
            type="primary"
            style="width:100%;"
            @click.prevent="handleRegister">
          <span v-if="!loading">注 册</span>
          <span v-else>注 册 中...</span>
        </el-button>
        <div style="float: right;">
          <router-link class="link-type" :to="'/login'">使用已有账户登录</router-link>
        </div>
      </el-form-item>
    </el-form>
    <!-- 底部 -->
    <div class="el-register-footer">
      <span>Spring Cloud微服务架构实验室管理服务平台</span>
    </div>
  </div>
</template>

<script setup>
import {ElMessage, ElMessageBox} from "element-plus";
import { getCodeImg, register } from "@/api/login";
import { ref, getCurrentInstance } from "vue";
import { useRouter } from "vue-router";
import {queryCollegeOrMajorListApi} from "@/api/review/index.js";

const router = useRouter();
const { proxy } = getCurrentInstance();

const registerForm = ref({
  account: "",
  name: "",
  roleId: "",
  sex: null,
  phone: "",
  college: "",
  major: "",
  grade: "",
  password: "",
  confirmPassword: "",

});

const equalToPassword = (rule, value, callback) => {
  if (registerForm.value.password !== value) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const registerRules = {
  account: [
    { required: true, trigger: "blur", message: "请输入您的账号" },
    { min: 2, max: 20, message: "用户账号长度必须介于 2 和 20 之间", trigger: "blur" }
  ],
  name: [
    { required: true, trigger: "blur", message: "请输入您的姓名" },
    { min: 2, max: 20, message: "姓名长度必须介于 2 和 20 之间", trigger: "blur" }
  ],
  roleId: [
    { required: true, trigger: "blur", message: "请选择角色" }
  ],
  sex: [
    { required: true, trigger: "blur", message: "请选择性别" }
  ],
  password: [
    { required: true, trigger: "blur", message: "请输入您的密码" },
    { min: 5, max: 20, message: "用户密码长度必须介于 5 和 20 之间", trigger: "blur" },
    { pattern: /^[^<>"'|\\]+$/, message: "不能包含非法字符：< > \" ' \\\ |", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, trigger: "blur", message: "请再次输入您的密码" },
    { validator: equalToPassword, trigger: "blur" }
  ],
  code: [{ required: true, trigger: "change", message: "请输入验证码" }],
  college: [
    { required: true, trigger    : "blur", message: "请输入学院", when: (model) => model.roleId === "2" }
  ],
  major: [
    { required: true, trigger: "blur", message: "请输入专业", when: (model) => model.roleId === "2" }
  ],
  grade: [
    { required: true, trigger: "blur", message: "请输入年级", when: (model) => model.roleId === "2" }
  ]
};

const codeUrl = ref("");
const loading = ref(false);
const captchaEnabled = ref(true);

function handleRegister() {
  proxy.$refs.registerRef.validate((valid) => {
    if (valid) {
      loading.value = true;
      let postData = JSON.parse(JSON.stringify(registerForm.value));
      delete postData.confirmPassword;
      register(postData).then((res) => {
        const account = registerForm.value.account;
        if(res.message === 'ok'){
          ElMessage({ message: res.data || '注册成功', type: 'success' })
          router.push("/login");
          // ElMessageBox.alert(
          //     `<font color='red'>恭喜你，您的账号 ${account} 注册成功！</font>`,
          //     "系统提示",
          //     {
          //       dangerouslyUseHTMLString: true,
          //       type: "success",
          //     }
          // ).then(() => {
          //   router.push("/login");
          // }).catch(() => {});
        }else{
          ElMessage({ message: res.message || '注册失败', type: 'error' })
        }

      }).catch(() => {
        loading.value = false;

      });
    }
  });
}

function getCode() {
  getCodeImg().then((res) => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled;
    if (captchaEnabled.value) {
      codeUrl.value = "data:image/gif;base64," + res.img;
      registerForm.value.uuid = res.uuid;
    }
  });
}


const collegeList = ref([])

const queryCollegeOrMajorList = async () => {
  try {
    const res = await queryCollegeOrMajorListApi();
    if (Array.isArray(res.data)) {
      collegeList.value = res.data.map(item => {
        return {label: item.dictLabel, value: item.dictValue + '',code:item.dictCode};
      });
     ;
    }
  } catch (error) {
    console.error(error);
  }
};

queryCollegeOrMajorList();
const majorList = ref([])
const selectChange = async () => {

    registerForm.value.major = "";
    const code = collegeList.value.find(item => item.value == registerForm.value.college).code;

    let data = {
      value: code,
    };
    try {
      const res = await queryCollegeOrMajorListApi(data);
      if (Array.isArray(res.data)) {
        majorList.value= res.data.map(item => {
          return {label: item.dictLabel, value: item.dictValue + ''};
        });
      }
    } catch (error) {
      console.error(error);
    }

};
// 角色变更时的处理
function handleRoleChange(value) {
  if (value === "2") {
    // 如果选择学生角色，清空学院、专业、年级
    registerForm.value.college = "";
    registerForm.value.major = "";
    registerForm.value.grade = "";
  }
}
</script>

<style lang='scss' scoped>
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.register-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  .el-input {
    height: 40px;
    input {
      height: 40px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0px;
  }
}
.register-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.register-code {
  width: 33%;
  height: 40px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.el-register-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
.register-code-img {
  height: 40px;
  padding-left: 12px;
}
</style>
