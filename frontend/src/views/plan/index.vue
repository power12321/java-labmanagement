<template>
  <div class="app-container">
    <SearchForm
        @selectChange="selectChange"
        :searchFormList="searchFormList"
        v-model:queryParams="queryParams"
        @handleQuery="handleQuery"
        @resetQuery="resetQuery"
    />

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain size="small" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            size="small"
            :disabled="multiple"
            @click="handleDelete"
        >
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="tableData" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
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
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
          >删除</el-button>
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

    <AddEdit
        @selectChange="selectChange1"
        :title="title"
        v-model:formData="formData"
        :rules="rules"
        :formList="formList"
        @submitData="submitData"
        @cancel="cancel"
        ref="addEditRef"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import SearchForm from "@/components/SearchForm";
import AddEdit from "@/components/AddEdit";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  addOrUpdateCourseApi, addOrUpdateScheduleApi,
  deleteScheduleByIdsApi,
  queryCollegeOrMajorListApi, queryCourseByPageApi,
  queryScheduleByPageApi, queryTeacherByPageApi
} from "@/api/review/index.js";

const searchFormList = ref([
  { label: "教师姓名", prop: 'teacherName' },
  { label: "教师id", prop: 'teacherId' },


]);

const tableData = ref([]);
const tableHeader = ref([
  { title: "教师姓名", key: "teacherName", align: "center" },
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

const initQueryParams = () => {
  searchFormList.value.forEach(item => { queryParams.value[item.prop] = '' });
};
initQueryParams();

const getList = async () => {
  loading.value = true;
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
  ids.value = selection.map(item => item.scheduleId).join(',');
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
  const deleteIDs = row.scheduleId || ids.value;
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
        deleteScheduleByIdsApi({ids: deleteIDs}).then((res) => {
          if(!res.data){
            ElMessage({
              type: 'error',
              message: res.message || '删除失败',
            });
          }else{
            ElMessage({
              type: 'success',
              message: '删除成功',
            });
            getList();
          }
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
</script>
