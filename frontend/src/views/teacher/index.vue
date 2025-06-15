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
          <span v-else>
            {{ scope.row[item.key] }}
          </span>
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
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import {queryTeacherByPageApi} from '@/api/review/index.js';

const loading = ref(true);
const tableData = ref([]);
const tableHeader = ref([
  {title: '名称', key: 'name', align: 'center'},
  {title: '手机号', key: 'phone', align: 'center'},
  {title: '性别', key: 'sex', align: 'center'},
]);
const queryParams = ref({
  pageNum: 1,
  pageSize: 5,
});
const total = ref(0);

const getList = async () => {
  loading.value = true;
  try {
    const postData = {
      current: queryParams.value.pageNum,
      size: queryParams.value.pageSize,
    };
    const response = await queryTeacherByPageApi(postData);
    tableData.value = response.data.list;
    total.value = response.data.total;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
  ;
};

onMounted(() => {
  getList();
});
</script>
