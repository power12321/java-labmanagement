<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="员工ID" prop="employeeId">
        <el-input
            v-model="queryParams.employeeId"
            placeholder="请输入员工ID"
            clearable
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="日期" prop="date">
        <el-date-picker clearable
                        v-model="queryParams.date"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择日期">
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
<!--            v-hasPermi="['system:availability:edit']"-->
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
<!--            v-hasPermi="['system:availability:remove']"-->
<!--        >删除</el-button>-->
<!--      </el-col>-->
    </el-row>

    <el-table v-loading="loading" :data="availabilityList" @selection-change="handleSelectionChange">


      <el-table-column label="员工ID" align="center" prop="employeeId" />
      <el-table-column label="是否出勤" align="center" prop="isAvailable" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
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
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
    />

    <!-- 添加或修改【请填写功能名称】对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="form" :model="form"  label-width="80px">

        <el-form-item label="员工ID" prop="employeeId">
          <el-input v-model="form.employeeId" placeholder="请输入员工ID" />
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker clearable
                          v-model="form.date"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="是否出勤" prop="isAvailable">
          <el-input v-model="form.isAvailable" placeholder="请输入是否出勤" />
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
  listAvailability,
  getAvailability,
  delAvailability,
  addAvailability,
  updateAvailability
} from "@/api/system/availability";

const loading = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const availabilityList = ref([]);
const title = ref("");
const open = ref(false);
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  availabilityId: null,
  employeeId: null,
  date: null,
  isAvailable: null
});
const form = ref({});

// Fetching data on component mount
const getList = async () => {
  loading.value = true;
  try {
    const response = await listAvailability(queryParams.value);
    availabilityList.value = response.rows;
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

const reset = () => {
  form.value = {
    availabilityId: null,
    employeeId: null,
    date: null,
    isAvailable: null
  };

};

const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

const resetQuery = () => {
   reset()
  handleQuery();
};

const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.availabilityId);
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
  const availabilityId = row.availabilityId || ids.value;
  const response = await getAvailability(availabilityId);
  form.value = response.data;
  open.value = true;
  title.value = "修改【请填写功能名称】";
};

const submitForm = () => {
  const valid = validateForm(); // Implement your validation logic here
  if (valid) {
    if (form.value.availabilityId != null) {
      updateAvailability(form.value).then(() => {
        this.$modal.msgSuccess("修改成功");
        open.value = false;
        getList();
      });
    } else {
      addAvailability(form.value).then(() => {
        this.$modal.msgSuccess("新增成功");
        open.value = false;
        getList();
      });
    }
  }
};

const handleDelete = async (row) => {
  const availabilityIds = row.availabilityId || ids.value;
  try {
    await this.$modal.confirm(`是否确认删除【请填写功能名称】编号为"${availabilityIds}"的数据项？`);
    await delAvailability(availabilityIds);
    getList();
    this.$modal.msgSuccess("删除成功");
  } catch {
    // Handle cancellation or error
  }
};

const handleExport = () => {
  download('system/availability/export', {
    ...queryParams.value
  }, `availability_${new Date().getTime()}.xlsx`);
};
</script>
