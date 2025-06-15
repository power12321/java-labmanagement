<template>
  <div class="app-container">
    <el-table v-loading="loading" :data="tableData">
      <el-table-column
          v-for="item in tableHeader"
          :key="item.key"
          :label="item.title"
          :align="item.align"
          :prop="item.key"
      >
        <template #default="scope">
          <span v-if="item.key === 'sex'">
            {{ scope.row[item.key] === 1? '男' : '女' }}
          </span>
          <span v-else-if="item.key === 'roleId'">
            {{ scope.row[item.key] === 0? '管理员' : scope.row[item.key] === 1? '教师' : '学生' }}
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
              @click="openReviewDialog(scope.row)"
          >审核</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

    <!-- 审核弹框 -->
    <el-dialog
        title="审核"
        v-model="reviewDialogVisible"
        width="30%"
        @close="closeReviewDialog"
    >
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="审核状态">
          <el-radio-group v-model="reviewForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="0">不通过</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeReviewDialog">取消</el-button>
        <el-button type="primary" @click="submitReview">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import {queryReviewByPageApi, updateReviewStatusApi} from '@/api/review/index.js';
import {ElMessage} from "element-plus";

const loading = ref(true);
const tableData = ref([]);
const tableHeader = ref([
  {title: '账号', key: 'account', align: 'center'},
  {title: '名称', key: 'name', align: 'center'},
  {title: '性别', key: 'sex', align: 'center'},
  {title: '角色', key: 'roleId', align: 'center'},
  {title: '创建时间', key: 'createTime', align: 'center'},
]);
const queryParams = ref({
  pageNum: 1,
  pageSize: 5,
});
const total = ref(0);

// 审核弹框相关
const reviewDialogVisible = ref(false);
const reviewForm = ref({
  userId: null,
  status: 1, // 默认选择通过
});

const getList = async () => {
  loading.value = true;
  try {
    const postData = {
      current: queryParams.value.pageNum,
      size: queryParams.value.pageSize,
    };
    const response = await queryReviewByPageApi(postData);
    tableData.value = response.data.list;
    total.value = response.data.total;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const openReviewDialog = (row) => {
  reviewForm.value.userId = row.userId;
  reviewDialogVisible.value = true;
};

const closeReviewDialog = () => {
  reviewDialogVisible.value = false;
  reviewForm.value = {
    userId: null,
    status: 1,
  };
};

const submitReview = async () => {
  try {
    await updateReviewStatusApi(reviewForm.value);
    closeReviewDialog();
    getList(); // 重新获取列表数据
    ElMessage.success('审核状态更新成功');
  } catch (error) {
    console.error(error);
    ElMessage.error('审核状态更新失败');
  }
};

onMounted(() => {
  getList();
});
</script>
