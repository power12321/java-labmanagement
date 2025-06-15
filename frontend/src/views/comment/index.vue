<template>
  <div class="app-container">

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
              v-if="!scope.row.evaluate"
              @click="comment(scope.row)"
          >提交实验</el-button>
          <el-button
              size="mini"
              type="text"
              v-else
              icon="el-icon-edit"
              @click="comment(scope.row,true)"
          >查看评价</el-button>

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
        width="30%"
    >
      <el-form :model="form" label-width="80px" :disabled="isComment">
        <el-form-item label="打分">
          <el-rate v-model="form.score" />
        </el-form-item>
        <el-form-item label="评价">
          <el-input
              v-model="form.evaluate"
              type="textarea"
              :rows="4"
              placeholder="请输入您的评价"
          />
        </el-form-item>

        <!-- 修改 FileUpload 组件的布局 -->
        <el-form-item label="提交报告">
          <FileUpload 
            v-model="uploadedFiles"
            :limit="5"
            :fileSize="5"
            :fileType="['pdf', 'doc', 'xls']"
            isShowTip
            @update:uploadList="handleUploadListUpdate"
          />
        </el-form-item>
      </el-form>
      <template #footer v-if="!isComment">
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </span>
      </template>
    </el-dialog>
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
  queryScheduleByStudentIdApi, insertScoreAndEvaluateApi
} from "@/api/review/index.js";
const dialogVisible = ref(false);
const form = ref({
  score: 5,
  evaluate: '',
  choiceId: '',
});

const searchFormList = ref([
  { label: "实验编号", prop: 'number' },
  { label: "实验名称", prop: 'courseName' },
  { label: "所属学院", prop: 'college', type: 'select', options: [] },
  { label: "所属专业", prop: 'major', type: 'select', options: [] },

]);
const uploadList = ref([]); // 新增这一行
const tableData = ref([]);
const tableHeader = ref([
  {
    title: "实验编号",
    key: "courseNumber",
    align: "center"
  },
  {
    title: "实验名称",
    key: "courseName",
    align: "center"
  },
  { title: "教师", key: "teacherName", align: "center" },
  { title: "开始时间", key: "startTime", align: "center" },
  { title: "结束时间", key: "endTime", align: "center" },
]);

const uploadedFiles = ref([]); // 用于存储上传的文件
const loading = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const queryParams = ref({
   studentId: sessionStorage.getItem('userId'),
  "current": 1,
  "size": 5
});
const formData = ref({});

const initQueryParams = () => {
  searchFormList.value.forEach(item => { queryParams.value[item.prop] = '' });
};
// initQueryParams();

const getList = async () => {
  loading.value = true;
  try {
    const response = await queryScheduleByStudentIdApi(queryParams.value);
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
const isComment = ref(true);
const comment = async (row,isCommented=false) => {
    isComment.value = isCommented
    if(isCommented){
      form.value = {

        score: row.score ,
        evaluate: row.evaluate,
        choiceId: row.courseId,

      }
    }else{
      form.value = {

        score: 5,
        evaluate: '',
        choiceId: row.courseId,

      }
    }

  dialogVisible.value = true;
};
const handleSubmit = async () => {
  if (form.value.score === 0) {
    ElMessage({
      type: 'error',
      message: '请选择评分',

    })
    return;
  }
  if (form.value.evaluate === '') {
    ElMessage({
      type: 'error',
      message: '请输入评价内容',

    })
    return;
  }

    form.value.uploadResource = uploadList.value[0];

    const res = await insertScoreAndEvaluateApi(form.value);
      if (res.data === 'success') {
        ElMessage({
          type:'success',
          message: '评价成功',
        });
        dialogVisible.value = false;
        getList();
      } else {
        ElMessage({
          type: 'error',
          message:res.message||  '评价失败',
        });
      }
}
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

const handleUploadListUpdate = (list) => {
  uploadList.value = list; // 更新 uploadList
  console.log("当前上传的文件列表:", uploadList.value); // 你可以在这里处理 uploadList
};
</script>
