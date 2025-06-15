<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="员工id" prop="employeeId">
        <el-input
            v-model="queryParams.employeeId"
            placeholder="请输入员工id"
            clearable
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="开始时间" prop="createdate">
        <el-date-picker
            clearable
            v-model="queryParams.createdate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择创建时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="archivedate">
        <el-date-picker
            clearable
            v-model="queryParams.archivedate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择完成时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary"  size="small" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button  size="small" @click="resetQuery"> <el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            size="small"
            @click="handleAdd"><el-icon><Plus /></el-icon>新增</el-button>
      </el-col>
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--            type="success"-->
<!--            plain-->
<!--            icon="el-icon-edit"-->
<!--            size="mini"-->
<!--            :disabled="single"-->
<!--            @click="handleUpdate"-->
<!--            v-hasPermi="['system:work:edit']"-->
<!--        >修改</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--            type="danger"-->
<!--            plain-->
<!--            icon="el-icon-delete"-->
<!--            size="mini"-->
<!--            :disabled="multiple"-->
<!--            @click="handleDelete"-->
<!--            v-hasPermi="['system:work:remove']"-->
<!--        >删除</el-button>-->
<!--      </el-col>-->

    </el-row>

    <el-table v-loading="loading" :data="workList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="员工id" align="center" prop="employeeId" />
      <el-table-column label="当前工作量" align="center" prop="workload" />
      <el-table-column label="累计工作量" align="center" prop="workload" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:work:edit']"
          >修改</el-button>
          <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:work:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
    />

    <!-- 添加或修改【请填写功能名称】对话框 -->
    <el-dialog :title="title" v-model="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="员工id" prop="employeeId">
          <el-input v-model="form.employeeId" placeholder="请输入员工id" />
        </el-form-item>
        <el-form-item label="当前工作量" prop="workload">
          <el-input v-model="form.workload" placeholder="请输入当前工作量" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import {
listWork,
getWork,
delWork,
addWork,
updateWork
} from "@/api/system/work";

const loading = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const workList = ref([]);
const title = ref("");
const open = ref(false);
const queryParams = ref({
pageNum: 1,
pageSize: 10,
employeeId: null,
ordertypeId: null,
workload: null
});
const form = ref({});

// Fetch initial data
const getList = async () => {
loading.value = true;
try {
const response = await listWork(queryParams.value);
workList.value = response.rows;
total.value = response.total;
} catch (e) {
console.error(e);
} finally {
loading.value = false;
}
};

onMounted(() => {
getList();
});

const cancel = () => {
open.value = false;
reset();
};

const reset = () => {
form.value = {
employeeId: null,
ordertypeId: null,
workload: null
};
// Reset your form validation logic here if needed
};

const handleQuery = () => {
queryParams.value.pageNum = 1;
getList();
};

const resetQuery = () => {
// Reset your form validation logic here if needed
handleQuery();
};

const handleSelectionChange = (selection) => {
ids.value = selection.map(item => item.employeeId);
single.value = selection.length !== 1;
multiple.value = !selection.length;
};

const handleAdd = () => {
reset();
open.value = true;
title.value = "添加";
};

const handleUpdate = async (row) => {
reset();
const employeeId = row.employeeId || ids.value;
try {
const response = await getWork(employeeId);
form.value = response.data;
open.value = true;
title.value = "修改【请填写功能名称】";
} catch (e) {
console.error(e);
}
};

const submitForm = () => {
// Implement your form validation logic here
const valid = true; // Replace with actual validation
if (valid) {
if (form.value.employeeId != null) {
updateWork(form.value).then(() => {
// Show success message here
open.value = false;
getList();
});
} else {
addWork(form.value).then(() => {
// Show success message here
open.value = false;
getList();
});
}
}
};

const handleDelete = async (row) => {
const employeeIds = row.employeeId || ids.value;
try {
// Confirm deletion
await this.$modal.confirm(`是否确认删除【请填写功能名称】编号为"${employeeIds}"的数据项？`);
await delWork(employeeIds);
getList();
// Show success message here
} catch (e) {
// Handle error or cancellation
}
};

const handleExport = () => {
download('system/work/export', {
...queryParams.value
}, `work_${new Date().getTime()}.xlsx`);
};

</script>
