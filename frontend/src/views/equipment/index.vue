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
      <el-col :span="1.5" v-if="roleId == 0">
        <el-button type="primary" plain size="small" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </el-col>
      <el-col :span="1.5" v-if="deviceIds.length > 0 && roleId == 1">
        <el-popconfirm
            class="box-item"
            title="确认申请设借用备吗？"
            placement="bottom-start"
            @confirm="handleApplication"
          >
            <template #reference>
              <el-button
                plain
                size="small"
                type="primary"
                >申请</el-button
              >
            </template>
          </el-popconfirm>
      
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="tableData" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" :selectable="(row) => row.status == 0"/>
      <el-table-column
        v-for="item in tableHeader"
        :key="item.key"
        :label="item.title"
        :align="item.align"
        :prop="item.key"
      >
        <template #default="scope">
          <span v-if="item.key === 'status'">
            {{
              scope.row.status === 0
                ? "待借用"
                : scope.row.status === 1
                ? "借用中"
                : "申请中"
            }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
  
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template v-slot="scope">
          <el-button
              v-if="roleId == 0"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
                v-if="roleId == 0"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            >删除</el-button
          >
          <el-popconfirm
            class="box-item"
            title="确认申请设借用备吗？"
            placement="bottom-start"
            @confirm="handleCation(scope.row)"
          >
            <template #reference>
                <el-button
            size="mini"
            type="text"
                v-if="scope.row.status == 0 && roleId == 1"
            icon="el-icon-delete"
            >申请</el-button
          >
            </template>
          </el-popconfirm>

         
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
      v-if="dialogVisible"
      v-model="dialogVisible"
      :title="title"
      width="500"
      :before-close="handleClose"
    >
      <el-form
        ref="ruleFormRef"
        style="max-width: 600px"
        :model="ruleForm"
        :rules="rules"
        label-width="auto"
      >
        <el-form-item label="设备编号" prop="deviceCode">
          <el-input v-model="ruleForm.deviceCode" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="ruleForm.deviceName" />
        </el-form-item>
        <el-form-item label="设备分类" prop="category">
          <el-input v-model="ruleForm.category" />
        </el-form-item>
        <el-form-item label="设备状态" prop="status" v-if="title === '修改'">
          <el-select v-model="ruleForm.status">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addEquipment(ruleFormRef)">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from "vue";
import SearchForm from "@/components/SearchForm";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getDevicePage,
  pushDeviceAdd,
  delDevice,
  pushDeviceUp,
  teDevice
} from "@/api/review/index.js";
const ruleFormRef = ref(null);
const ruleForm = reactive({
  deviceCode: "",
  deviceName: "",
  category: "",
  deviceId: "",
});
const dialogVisible = ref(false);
const rules = reactive({
  deviceCode: [{ required: true, message: "请输入设备编号", trigger: "blur" }],
  category: [{ required: true, message: "请输入设备分类", trigger: "blur" }],
  deviceName: [{ required: true, message: "请输入设备名称", trigger: "blur" }],
  status: [{ required: true, message: "请输入设备状态", trigger: "blur" }],
});
const searchFormList = ref([
  { label: "设备编号", prop: "deviceCode" },
  { label: "设备名称", prop: "deviceName" },
  { label: "分类", prop: "category" },
  {
    label: "状态",
    prop: "status",
    type: "select",
    options: [
      { label: "待借用", value: 0 },
      { label: "借用中", value: 1 },
      { label: "申请中", value: 2 },
    ],
  },
]);
const options =  ref([
      { label: "待借用", value: 0 },
      { label: "借用中", value: 1 },
      { label: "申请中", value: 2 },
    ])
const tableData = ref([]);
const tableHeader = ref([
  { title: "设备名称", key: "deviceName", align: "center" },
  { title: "设备编号", key: "deviceCode", align: "center" },
  { title: "分类", key: "category", align: "center" },
  { title: "状态", key: "status", align: "center" },
  { title: "借用教师", key: "teacherName", align: "center" },
]);
const deviceIds = ref([]);
const loading = ref(true);
const roleId = ref(null);
const total = ref(0);
const title = ref("");
const queryParams = ref({
  current: 1,
  size: 5,
  deviceName: "",
  deviceCode: "",
  category: "",
  status: "",
});

const initQueryParams = () => {
  searchFormList.value.forEach((item) => {
    queryParams.value[item.prop] = "";
  });
};
initQueryParams();

const getList = async () => {
  loading.value = true;
  try {
    const response = await getDevicePage(queryParams.value);
    tableData.value = response.data.records;
    total.value = response.data.total;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  getList();
  roleId.value = sessionStorage.getItem("roleId");
});

const addEquipment = async (formEl) => {
  // 添加 async
  if (!formEl) return;
  formEl.validate(async (valid, fields) => {
    // 回调函数也加 async
    if (valid) {
      try {
        const response = await (ruleForm.deviceId
          ? pushDeviceUp(ruleForm)
          : pushDeviceAdd(ruleForm));
        console.log("🚢 ~ 当前打印的内容 ~ response:", response);

        if (response.code === 0) {
          dialogVisible.value = false;
            getList();
            ElMessage({
            type: "success",
            message: ruleForm.deviceId ? "编辑成功" : "添加成功",
          });
        }else{
          ElMessage({
            type: "error",
            message: response.message,
          });
        }
      } catch (error) {
        console.error("请求出错:", error);
        ElMessage({
          type: "error",
          message: "操作失败",
        });
      }
    } else {
      console.log("error submit!", fields);
    }
  });
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

const handleAdd = () => {
  ruleForm.deviceCode = "";
  ruleForm.deviceName = "";
  ruleForm.category = "";
  ruleForm.deviceId = "";
  title.value = "添加";
  dialogVisible.value = true;
};

const handleUpdate = async (row) => {
  console.log("🚢 ~ 当前打印的内容 ~ row:", row);
  ruleForm.deviceCode = row.deviceCode;
  ruleForm.deviceName = row.deviceName;
  ruleForm.category = row.category;
  ruleForm.deviceId = row.deviceId;
  ruleForm.status = row.status;
  dialogVisible.value = true;
  title.value = "修改";
};

const handleSelectionChange = (selection) => {
  console.log("🚢 ~ 当前打印的内容 ~ selection:", selection);
  deviceIds.value = selection.map((item) => item.deviceId); 
  console.log("🚢 ~ 当前打印的内容 ~  deviceIds.value:",  deviceIds.value);
}
const handleCation = (e) => {
  console.log("🚢 ~ 当前打印的内容 ~ deviceIds.value :", deviceIds.value );
  const teacherId = sessionStorage.getItem("userId");
  teDevice({  teacherId: teacherId,
    deviceIds: [e.deviceId] }).then((res) => {
    if (res.code === 0) {
      ElMessage({
        type: "success",
        message:"提交成功",
      });
      getList();
    }
  })
};


const handleApplication = () => {
  console.log("🚢 ~ 当前打印的内容 ~ deviceIds.value :", deviceIds.value );
  const teacherId = sessionStorage.getItem("userId");
  teDevice({  teacherId: teacherId,
    deviceIds: deviceIds.value }).then((res) => {
    if (res.code === 0) {
      ElMessage({
        type: "success",
        message:"提交成功",
      });
      getList();
    }else{
      ElMessage({
        type: "error",
        message: res.message,
      });
    }
  })
};

const handleDelete = async (row) => {
  const deleteIDs = row.deviceId;
  ElMessageBox.confirm("是否删除该条记录？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      delDevice({ ids: deleteIDs }).then((res) => {
        if (res.code !=0) {
          ElMessage({
            type: "error",
            message: res.message || "删除失败",
          });
        } else {
          ElMessage({
            type: "success",
            message: "删除成功",
          });
          getList();
        }
      });
    })
    .catch(() => {});
};
</script>
