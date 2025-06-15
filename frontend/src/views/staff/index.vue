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

      <el-form-item label="组" prop="group">
        <el-input
            v-model="queryParams.group"
            placeholder="请输入组"
            clearable
            @keyup.enter.native="handleQuery"
        />
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
<!--            v-hasPermi="['system:employee:edit']"-->
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
<!--            v-hasPermi="['system:employee:remove']"-->
<!--        >删除</el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--            type="warning"-->
<!--            plain-->
<!--            icon="el-icon-download"-->
<!--            size="mini"-->
<!--            @click="handleExport"-->
<!--            v-hasPermi="['system:employee:export']"-->
<!--        >导出</el-button>-->
<!--      </el-col>-->
<!--      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>-->
    </el-row>

    <el-table v-loading="loading" :data="employeeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="员工id" align="center" prop="employeeId" />
      <el-table-column label="是否外包" align="center" prop="isEpiboly" />

      <el-table-column label="组" align="center" prop="group" />
      <el-table-column label="工程/业务" align="center" prop="isEngineering" />
      <el-table-column label="订单业务类型" align="center" prop="ordertype" />
      <el-table-column label="是否可用" align="center" prop="enable" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['system:employee:edit']"
          >修改</el-button>
          <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['system:employee:remove']"
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

    <!-- 添加或修改员工对话框 -->
    <el-dialog :title="title" v-model="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="员工id" prop="employeeId">
          <el-input v-model="form.employeeId" placeholder="请输入员工id" />
        </el-form-item>
        <el-form-item label="是否外包" prop="isEpiboly">
          <el-input v-model="form.isEpiboly" placeholder="请输入是否外包" />
        </el-form-item>
        <el-form-item label="组" prop="group">
          <el-input v-model="form.group" placeholder="请输入组" />
        </el-form-item>
        <el-form-item label="工程/业务" prop="isEngineering">
          <el-input v-model="form.isEngineering" placeholder="请输入工程/业务" />
        </el-form-item>
        <el-form-item label="订单业务类型" prop="isEngineering">
          <el-input v-model="form.ordertype" placeholder="订单业务类型" />
        </el-form-item>
        <el-form-item label="是否可用" prop="enable">
          <el-input v-model="form.enable" placeholder="请输入是否可用" />
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
  listEmployee,
  getEmployee,
  delEmployee,
  addEmployee,
  updateEmployee
} from "@/api/system/employee";

const loading = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const employeeList = ref([]);
const title = ref("");
const open = ref(false);

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  employeeId: null,
  isEpiboly: null,
  name: null,
  group: null,
  isEngineering: null,
  enable: null
});

const form = ref({
  ordertype:''
});
const rules = ref({});

// Fetch initial data
const getList = async () => {
  loading.value = true;
  try {
    const response = await listEmployee(queryParams.value);
    employeeList.value = response.rows;
    total.value = response.total;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// Call getList when component is mounted
onMounted(() => {
  getList();
});

// Cancel button
const cancel = () => {
  open.value = false;
  reset();
};

// Reset form
const reset = () => {
  form.value = {
    employeeId: null,
    isEpiboly: null,
    name: null,
    group: null,
    isEngineering: null,
    enable: null
  };
  // Reset your form validation logic if needed
};

const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

const resetQuery = () => {
  // Reset your query form validation logic if needed
  handleQuery();
};

// Handle selection change
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.employeeId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

// Handle add button
const handleAdd = () => {
  reset();
  open.value = true;
  title.value = "添加员工";
};

// Handle update button
const handleUpdate = async (row) => {
  reset();
  const employeeId = row.employeeId || ids.value;
  try {
    const response = await getEmployee(employeeId);
    form.value = response.data;
    open.value = true;
    title.value = "修改员工";
  } catch (error) {
    console.error(error);
  }
};

// Submit form
const submitForm = () => {
  // Implement your form validation logic here
  const valid = true; // Replace with actual validation
  if (valid) {
    if (form.value.employeeId != null) {
      updateEmployee(form.value).then(() => {
        // Show success message here
        open.value = false;
        getList();
      });
    } else {
      addEmployee(form.value).then(() => {
        // Show success message here
        open.value = false;
        getList();
      });
    }
  }
};

// Handle delete button
const handleDelete = async (row) => {
  const employeeIds = row.employeeId || ids.value;
  try {
    // Confirm deletion
    await this.$modal.confirm(`是否确认删除员工编号为"${employeeIds}"的数据项？`);
    await delEmployee(employeeIds);
    getList();
    // Show success message here
  } catch (error) {
    // Handle error or cancellation
  }
};

</script>
