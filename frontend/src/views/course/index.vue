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
  addOrUpdateCourseApi,
  deleteCourseByIdsApi,
  queryCollegeOrMajorListApi,
  queryCourseByPageApi
} from "@/api/review/index.js";

const searchFormList = ref([
  { label: "实验编号", prop: 'number' },
  { label: "实验名称", prop: 'courseName' },
  { label: "所属学院", prop: 'college', type: 'select', options: [] },
  { label: "所属专业", prop: 'major', type: 'select', options: [] },

]);

const tableData = ref([]);
const tableHeader = ref([
  { title: "实验编号", key: "number", align: "center" },
  { title: "实验名称", key: "courseName", align: "center" },
  { title: "所属学院", key: "collegeLabel", align: "center" },
  { title: "所属专业", key: "majorLabel", align: "center" },
  { title: "课时", key: "period", align: "center" },
  { title: "学分", key: "credits", align: "center" },
  { title: "实验类型", key: "type", align: "center" }
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
    const response = await queryCourseByPageApi(queryParams.value);
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
  { label: "实验编号", prop: 'number' },
  { label: "实验名称", prop: 'courseName' },
  { label: "所属学院", prop: 'college', type: 'select', options: [] },
  { label: "所属专业", prop: 'major', type: 'select', options: [] },
  { label: "课时", prop: 'period' },
  { label: "学分", prop: 'credits' },
  { label: "实验类型", prop: 'type', type: 'select', options: [
      { label: '必修课', value: 0 },
      { label: '选修课', value: 1 }
    ] },
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
  const currentRow = JSON.parse(JSON.stringify(row));
  delete currentRow.updateTime;
  delete currentRow.createTime;
  delete currentRow.collegeLabel;
  delete currentRow.majorLabel;
  delete currentRow.scoreAndEvaluateList;
  currentRow.college = currentRow.collegeId;
  currentRow.major = currentRow.majorId;
  delete currentRow.collegeId;
  delete currentRow.majorId;

  formData.value = currentRow;
  selectChange1('college', currentRow.college, true);
  title.value = "修改";
  addEditRef.value.openDialog();
};

const submitData = async () => {
  const res = await addOrUpdateCourseApi(formData.value);
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
      message: '操作失败',
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
        deleteCourseByIdsApi({ids: deleteIDs}).then((res) => {
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

const queryCollegeOrMajorList = async () => {
  try {
    const res = await queryCollegeOrMajorListApi();
    if (Array.isArray(res.data)) {
      searchFormList.value[2].options = res.data.map(item => {
        return {label: item.dictLabel, value: item.dictValue + '',code:item.dictCode};
      });
      formList.value[2].options = res.data.map(item => {
        return {label: item.dictLabel, value: item.dictValue + '',code:item.dictCode};
      });
    }
  } catch (error) {
    console.error(error);
  }
};

queryCollegeOrMajorList();

const selectChange = async (prop, value) => {
  if (prop === 'college') {
    queryParams.value.major = '';

    const code = searchFormList.value[2].options.find(item => item.value == value).code;
    console.log('value',value,code,searchFormList.value[2].options)
    let data = {
      value: code,
    };
    try {
      const res = await queryCollegeOrMajorListApi(data);
      if (Array.isArray(res.data)) {
        searchFormList.value[3].options = res.data.map(item => {
          return {label: item.dictLabel, value: item.dictValue + ''};
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
    const code = formList.value[2].options.find(item => item.value == value).code;
    let data = {
      value: code,
    };
    try {
      const res = await queryCollegeOrMajorListApi(data);
      if (Array.isArray(res.data)) {
        formList.value[3].options = res.data.map(item => {
          return {label: item.dictLabel, value: item.dictValue + ''};
        });
      }
    } catch (error) {
      console.error(error);
    }
  }
};
</script>
