<template>
  <div class="navbar">
    <hamburger
      id="hamburger-container"
      :is-active="appStore.sidebar.opened"
      class="hamburger-container"
      @toggleClick="toggleSideBar"
    />
    <breadcrumb
      id="breadcrumb-container"
      class="breadcrumb-container"
      v-if="!settingsStore.topNav"
    />
    <top-nav
      id="topmenu-container"
      class="topmenu-container"
      v-if="settingsStore.topNav"
    />
    <div class="right-menu">
      <div class="avatar-container">
        <el-dropdown
          @command="handleCommand"
          class="right-menu-item hover-effect"
          trigger="click"
        >
          <div class="avatar-wrapper">
            <img :src="userStore.avatar" class="user-avatar" />
            <el-icon><caret-bottom /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <!--              <router-link to="/user/profile">-->
              <!--                <el-dropdown-item>个人中心</el-dropdown-item>-->
              <!--              </router-link>-->
              <!--              <el-dropdown-item command="setLayout" v-if="settingsStore.showSettings">-->
              <!--                <span>布局设置</span>-->
              <!--              </el-dropdown-item>-->
              <el-dropdown-item command="logout">
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, h } from "vue";
import { ElMessageBox, ElNotification,ElMessage } from "element-plus";
import Breadcrumb from "@/components/Breadcrumb";
import TopNav from "@/components/TopNav";
import Hamburger from "@/components/Hamburger";
import Screenfull from "@/components/Screenfull";
import SizeSelect from "@/components/SizeSelect";
import HeaderSearch from "@/components/HeaderSearch";
import RuoYiGit from "@/components/RuoYi/Git";
import RuoYiDoc from "@/components/RuoYi/Doc";
import useAppStore from "@/store/modules/app";
import useUserStore from "@/store/modules/user";
import useSettingsStore from "@/store/modules/settings";
import { getDevices,getApprove } from "@/api/review/index.js";
const appStore = useAppStore();
const userStore = useUserStore();
const settingsStore = useSettingsStore();
const notification = ref(null);
onMounted(() => {
  if(sessionStorage.getItem('roleId') == 0){
    getDevice();

  }
});

const getDevice = async () => {
  try {
    const res = await getDevices({});
    if (res.code === 0) {
      res.data.forEach((element) => {
        setTimeout(() => {
          const ElNot = ElNotification({
          title: "申请设备通知",
          duration: 0,
          message: h("div", [
            h("div", `教师-${element.teacherName} 申请使用${element.deviceNames.join(',')}实验设备`),
            h(
              "div",
              {
                style:
                  "margin-top: 10px; display: flex; justify-content: flex-end; gap: 8px;",
              },
              [
                h(
                  "button",
                  {
                    style:
                      "padding: 5px 10px; background: #f56c6c; color: white; border: none; border-radius: 4px;",
                    onClick: () => {
                      ElNot.close();
                      closeNotification(element,false);
                    },
                  },
                  "拒绝"
                ),
                h(
                  "button",
                  {
                    style:
                      "padding: 5px 10px; background: #67c23a; color: white; border: none; border-radius: 4px;",
                    onClick: () => {
                      ElNot.close();
                      closeNotification(element,true);
                    },
                  },
                  "确定"
                ),
              ]
            ),
          ]),
        });
        }, 100);
      });
    }
  } catch (error) {
    console.error(error);
  }
};

const closeNotification = (item,type) => {
  console.log("🚢 ~ 当前打印的内容 ~ item:", item);
  getApprove({ deviceIds: item.deviceIds ,approved:type}).then((res) => {
    if (res.code === 0) {
      ElMessage({
        type: "success",
        message: "操作成功",
      });
    }
  })
};

function toggleSideBar() {
  appStore.toggleSideBar();
}

function handleCommand(command) {
  switch (command) {
    case "setLayout":
      setLayout();
      break;
    case "logout":
      logout();
      break;
    default:
      break;
  }
}

function logout() {
  ElMessageBox.confirm("确定注销并退出系统吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      userStore.logOut().then(() => {
        location.href = "/login";
      });
    })
    .catch(() => {});
}

const emits = defineEmits(["setLayout"]);
function setLayout() {
  emits("setLayout");
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: var(--navbar-bg);
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;
    display: flex;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: var(--navbar-text);
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }
      }

      &.theme-switch-wrapper {
        display: flex;
        align-items: center;

        svg {
          transition: transform 0.3s;

          &:hover {
            transform: scale(1.15);
          }
        }
      }
    }

    .avatar-container {
      margin-right: 40px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        i {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
<style>
.el-notification__content {
  width: 200px;
}
</style>
