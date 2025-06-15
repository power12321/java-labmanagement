<template>
  <div class="app-container">




    <el-table v-loading="loading" :data="tableData" @selection-change="handleSelectionChange">

      <el-table-column
          v-for="item in tableHeader"
          :key="item.key"
          :label="item.title"
          :align="item.align"
          :prop="item.key"
      >
        <template #default="scope">
          <span v-if="item.key === 'type'">
            {{ scope.row[item.key] === 0 ? '必修课' : '选修课' }}
          </span>
          <span v-else>
            {{ scope.row[item.key] }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="实验概述" prop="content" align="center">
        <template #default="scope">
          <span>{{ scope.row.content }}</span>
        </template>
      </el-table-column>
      <el-table-column label="实验指导书" prop="uploadResource" align="center">
        <template #default="scope">
          <a v-if="scope.row.uploadResource" :href="scope.row.uploadResource" target="_blank" class="download-link">下载</a>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="detail(scope.row)"
          >详情</el-button>

          <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="showUploadDialog(scope.row)"
          >发布</el-button>

        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.current"
        v-model:limit="queryParams.size"
        @pagination="getList"
    />

    <el-dialog
        v-model="dialogVisible"
        title="评价"
        width="80%"
    >
      <el-table :data="dialogList" style="width: 100%">
        <!-- 评分列 -->
        <el-table-column prop="score" label="评分" width="150">
          <template #default="{ row }">
            <el-rate v-model="row.score" disabled show-score :max="10" />
          </template>
        </el-table-column>

        <!-- 评价列 -->
        <el-table-column
            prop="evaluate"
            label="评价"
            show-overflow-tooltip
        />
        <el-table-column prop="uploadResource" label="提交作业" width="150">
          <template #default="{ row }">
            <a v-if="row.uploadResource" :href="row.uploadResource" target="_blank" class="download-link">下载</a>
          </template>
        </el-table-column>


      </el-table>




    </el-dialog>

    <el-dialog
        v-model="uploadDialogVisible"
        title="发布实验"
        width="40%"
        :modal="true"
        :close-on-click-modal="false"
        :custom-class="'custom-dialog'"
    >
      <!-- <span>您确定要发布实验吗？</span> -->
      
      <!-- 引入 FileUpload 组件 -->
      <FileUpload
        v-model="uploadedFiles"
        :limit="5"
        :fileSize="5"
        :fileType="['pdf', 'doc', 'xls']"
        isShowTip
        @update:uploadList="handleUploadListUpdate"
      />

      <!-- 添加实验概述输入框 -->
      <el-input
        v-model="content"
        placeholder="请输入实验概述"
        style="margin-top: 10px; height: 50px; font-size: 16px;"
      />

      <span slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmUpload">确 定</el-button>
      </span>
    </el-dialog>

  


    
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import SearchForm from "@/components/SearchForm";
import AddEdit from "@/components/AddEdit";
import FileUpload from "@/components/FileUpload"; // 引入 FileUpload 组件
import { ElMessage, ElMessageBox } from "element-plus";
import {
  addOrUpdateCourseApi, addOrUpdateScheduleApi,
  deleteCourseByIdsApi,
  queryCollegeOrMajorListApi, queryCourseByPageApi,
  queryScheduleByPageApi, queryTeacherByPageApi
} from "@/api/review/index.js";

const searchFormList = ref([
  { label: "教师姓名", prop: 'teacherName' },
  { label: "教师id", prop: 'teacherId' },


]);
const dialogVisible = ref(false);
const uploadDialogVisible = ref(false);
const dialogList = ref([]);
const detail = row=>{
  dialogVisible.value = true;
  dialogList.value = row.scoreAndEvaluateList
  console.log('dddd',dialogList.value,row)

}
const tableData = ref([]);
const tableHeader = ref([
  // { title: "教师姓名", key: "teacherName", align: "center" },
  { title: "实验编号", key: "courseNumber", align: "center" },
  { title: "实验名称", key: "courseName", align: "center" },
  { title: "开始时间", key: "startTime", align: "center" },
  { title: "结束时间", key: "endTime", align: "center" },
]);

const addEditRef = ref(null);
const loading = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const queryParams = ref({
  current: 1,
  size: 5,
});
const formData = ref({});
const uploadedFiles = ref([]); // 用于存储上传的文件
const uploadList = ref([]); // 新增这一行
const experimentOverview = ref(""); // 新增实验概述变量
const loadingUpload = ref(false); // 新增加载状态变量
const content = ref(""); // 新增实验概述变量

const initQueryParams = () => {
  searchFormList.value.forEach(item => { queryParams.value[item.prop] = '' });
};
initQueryParams();

const getList = async () => {
  loading.value = true;
  queryParams.value.teacherId = sessionStorage.getItem('userId');
  try {
    const response = await queryScheduleByPageApi(queryParams.value);
    tableData.value = response.data.list;
    total.value = response.data.total;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  getList();
});

const cancel = () => {
  reset();
  addEditRef.value.closeDialog();
};

const formList = ref([

  { label: "教师", prop: 'userId', type: 'select', options: [] },
  { label: "课程", prop: 'courseId', type: 'select', options: [] },

  { label: "开始时间", prop: 'startTime',type:'datetime' },
  { label: "结束时间", prop: 'endTime',type:'datetime' },

]);

const rules = ref({});

const setRule = () => {
  formList.value.forEach(item => {
    rules.value[item.prop] = [
      { required: true, message: `请输入${item.label}`, trigger: 'blur' }
    ];
  });
};

setRule();

const reset = () => {
  formList.value.forEach(item => { formData.value[item.prop] = '' });
};

const handleQuery = () => {
  queryParams.value.current = 1;
  getList();
};

const resetQuery = () => {
  initQueryParams();
  queryParams.value.current = 1;
  handleQuery();
};

const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.courseId).join(',');
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

const handleAdd = () => {
  reset();
  title.value = "添加";
  addEditRef.value.openDialog();
};

const handleUpdate = async (row) => {
  const currentRow = {
    startTime: row.startTime,
    endTime: row.endTime,
    courseId: row.courseId,
    userId: row.userId,
  }

  formData.value = currentRow;
  selectChange1('college', currentRow.college, true);
  title.value = "修改";
  addEditRef.value.openDialog();
};

const submitData = async () => {
  const res = await addOrUpdateScheduleApi(formData.value);
  if (res.data === 'success') {
    ElMessage({
      type: 'success',
      message: '操作成功',
    });
    addEditRef.value.closeDialog();
    getList();
  } else {
    ElMessage({
      type: 'error',
      message: res.message || '操作失败',
    });
  }
};

const handleDelete = async (row) => {
  const deleteIDs = row.courseId || ids.value;
  ElMessageBox.confirm(
      '是否删除该条记录？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(() => {
        deleteCourseByIdsApi({ids: deleteIDs}).then(() => {
          ElMessage({
            type: 'success',
            message: '删除成功',
          });
          getList();
        });
      })
      .catch(() => {
      });
};

const getCourse = async () => {
  try {
    const response = await queryCourseByPageApi({
      current: 1,
      size:9999
    });

    formList.value[1].options = response.data.list.map(item => {
      return {label: item.courseName, value: item.courseId };
    });

  } catch (error) {
    console.error(error);
  }
};

getCourse();
const getTeacher = async () => {
  try {
    const response = await queryTeacherByPageApi({
      current: 1,
      size:9999
    });

    formList.value[0].options = response.data.list.map(item => {
      return {label: item.name, value: item.userId };
    });

  } catch (error) {
    console.error(error);
  }
};

getTeacher();

const selectChange = async (prop, value) => {
  if (prop === 'college') {
    queryParams.value.major = '';
    let data = {
      value: value,
    };
    try {
      const res = await queryCollegeOrMajorListApi(data);
      if (Array.isArray(res.data)) {
        searchFormList.value[3].options = res.data.map(item => {
          return {label: item.dictLabel, value: item.dictCode };
        });
      }
    } catch (error) {
      console.error(error);
    }
  }
};

const selectChange1 = async (prop, value, isInit) => {
  if (prop === 'college') {
    if (!isInit) {
      formData.value.major = '';
    }
    let data = {
      value: value,
    };
    try {
      const res = await queryCollegeOrMajorListApi(data);
      if (Array.isArray(res.data)) {
        formList.value[3].options = res.data.map(item => {
          return {label: item.dictLabel, value: item.dictCode };
        });
      }
    } catch (error) {
      console.error(error);
    }
  }
};

const showUploadDialog = (row) => {
  formData.value = { ...row }; // 将当前行的数据赋值给 formData
  content.value = ""; // 重置实验概述
  uploadDialogVisible.value = true;
};

const confirmUpload = async () => {
  loadingUpload.value = true; // 开始加载
  formData.value.content = content.value; // 将实验概述赋值给 formData.content
  formData.value.uploadResource = uploadList.value[0];// 将上传的文件地址赋值给 formData.uploadResource
  try {
    const res = await addOrUpdateScheduleApi(formData.value); // 调用 API
    if (res.data === 'success') {
      ElMessage({
        type: 'success',
        message: '课程发布成功',
      });
    } else {
      ElMessage({
        type: 'error',
        message: res.message || '发布失败',
      });
    }
  } catch (error) {
    console.error(error);
    ElMessage({
      type: 'error',
      message: '发布过程中发生错误',
    });
  } finally {
    loadingUpload.value = false; // 结束加载
    uploadDialogVisible.value = false; // 关闭对话框
    getList(); // 刷新列表
  }
};

const handleUploadListUpdate = (list) => {
  uploadList.value = list; // 更新 uploadList
  console.log("当前上传的文件列表:", uploadList.value); // 你可以在这里处理 uploadList
};

</script>

<style scoped>
.custom-dialog {
    border-radius: 10px;  /* 圆角 */
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);  /* 阴影效果 */
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;  /* 右对齐 */
}

.download-link {
    color: #409EFF; /* 设置链接颜色 */
    font-weight: bold; /* 加粗字体 */
    text-decoration: underline; /* 添加下划线 */
}

.download-link:hover {
    color: #66b1ff; /* 悬停时改变颜色 */
}
</style>
