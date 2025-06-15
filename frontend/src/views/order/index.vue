<template>
  <div class="app-container">
    <SearchForm :searchFormList="searchFormList" v-model:queryParams="queryParams" @handleQuery="handleQuery" @resetQuery="resetQuery" />

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            size="small"
            @click="handleAdd"><el-icon><Plus /></el-icon>新增</el-button>
      </el-col>
      <template v-if="false">
        <el-col :span="1.5">
          <el-button
              type="success"
              plain
              icon="el-icon-edit"
              size="small"
              :disabled="single"
              @click="handleUpdate"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
              type="danger"
              plain
              icon="el-icon-delete"
              size="small"
              :disabled="multiple"
              @click="handleDelete"

          >删除</el-button>
        </el-col>
      </template>

    </el-row>

        <el-table  v-loading="loading" :data="parentserviceList" @selection-change="handleSelectionChange">
          <!--      <el-table-column type="selection" width="55" align="center" />-->
          <el-table-column label="订单号" align="center" prop="parentserviceorder" />
          <el-table-column label="定单号" align="center" prop="crmquotenumber" />
          <el-table-column label="CRM产品号" align="center" prop="crmproductid" />
          <el-table-column label="业务/工程" align="center" prop="isEngineering" />
          <el-table-column label="P6定单号" align="center" prop="p6Id" />
          <el-table-column label="定单类型" align="center" prop="serviceordertype" />
          <el-table-column label="定单状态" align="center" prop="status" />
          <el-table-column label="创建时间" align="center" prop="createdate" >
            <template #default="scope">
              <span>{{ parseTime(scope.row.createdate, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="编辑时间" align="center" prop="editdate" >
            <template #default="scope">
              <span>{{ parseTime(scope.row.editdate, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>



          <el-table-column label="分派状态" align="center" prop="assignment" />
          <el-table-column label="处理人" align="center" prop="employeeId" />
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template #default="scope">
              <el-button
                  size="small"
                  type="text"
                  icon="el-icon-edit"
                  @click="handleUpdate(scope.row)"
              >修改</el-button>

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

    <!-- 添加或修改【请填写功能名称】对话框 -->
    <el-dialog :title="title" v-model="open" width="50%" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="订单号" prop="parentserviceorder">
          <el-input v-model="form.parentserviceorder" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="定单号" prop="crmquotenumber">
          <el-input v-model="form.crmquotenumber" placeholder="请输入定单号" />
        </el-form-item>
        <el-form-item label="CRM产品号" prop="crmproductid">
          <el-input v-model="form.crmproductid" placeholder="请输入CRM产品号" />
        </el-form-item>
        <el-form-item label="P6定单号" prop="p6Id">
          <el-input v-model="form.p6Id" placeholder="请输入P6定单号" />
        </el-form-item>
<!--        <el-form-item label="创建时间" prop="createdate">-->
<!--          <el-date-picker clearable-->
<!--                          v-model="form.createdate"-->
<!--                          type="date"-->
<!--                          value-format="yyyy-MM-dd"-->
<!--                          placeholder="请选择创建时间">-->
<!--          </el-date-picker>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="编辑时间" prop="editdate">-->
<!--          <el-date-picker clearable-->
<!--                          v-model="form.editdate"-->
<!--                          type="date"-->
<!--                          value-format="yyyy-MM-dd"-->
<!--                          placeholder="请选择编辑时间">-->
<!--          </el-date-picker>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="受理时间" prop="dealtime">-->
<!--          <el-date-picker clearable-->
<!--                          v-model="form.dealtime"-->
<!--                          type="date"-->
<!--                          value-format="yyyy-MM-dd"-->
<!--                          placeholder="请选择受理时间">-->
<!--          </el-date-picker>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="完成时间" prop="archivedate">-->
<!--          <el-date-picker clearable-->
<!--                          v-model="form.archivedate"-->
<!--                          type="date"-->
<!--                          value-format="yyyy-MM-dd"-->
<!--                          placeholder="请选择完成时间">-->
<!--          </el-date-picker>-->
<!--        </el-form-item>-->
        <el-form-item label="业务/工程" prop="isEngineering">
          <el-input v-model="form.isEngineering" placeholder="请输入业务/工程" />
        </el-form-item>
        <el-form-item label="订单业务类型" prop="ordertypeId">
          <el-input v-model="form.ordertypeId" placeholder="请输入订单业务类型" />
        </el-form-item>
        <el-form-item label="工单分派状态" prop="assignment">
          <el-input v-model="form.assignment" placeholder="请输入工单分派状态" />
        </el-form-item>
        <el-form-item label="处理人" prop="employeeId">
          <el-input v-model="form.employeeId" placeholder="请输入处理人" />
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
import SearchForm from "@/components/SearchForm";
import {
  listParentservice,
  getParentservice,
  delParentservice,
  addParentservice,
  updateParentservice
} from "@/api/system/parentservice";
const searchFormList = ref([
  {label:"实验编号",prop:'number'}
])
const loading = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const total = ref(0);
const parentserviceList = ref([]);
const title = ref("");
const open = ref(false);
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  parentserviceorder: null,
  crmquotenumber: null,
  crmproductid: null,
  p6Id: null,
  serviceordertype: null,
  status: null,
  createdate: null,
  editdate: null,
  dealtime: null,
  archivedate: null,
  isEngineering: null,
  ordertypeId: null,
  assignment: null,
  employeeId: null
});
const form = ref({});
const rules = ref({});

// Fetch initial data
const getList = async () => {
  loading.value = true;
  try {
    const response = await listParentservice(queryParams.value);
    parentserviceList.value = response.rows;
    total.value = response.total;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
    parentserviceList.value = [
      {
        "parentserviceorder": "PO123456",
        "crmquotenumber": "QT20230901",
        "crmproductid": "PID001",
        "isEngineering": "技术支持",
        "p6Id": "P6-1001",
        "serviceordertype": "维护",
        "status": "已完成",
        "createdate": "2023-09-01T10:00:00Z",
        "editdate": "2023-09-05T12:00:00Z",
        "assignment": "待分派",
        "employeeId": "EMP001"
      },
      // {
      //   "parentserviceorder": "PO123457",
      //   "crmquotenumber": "QT20230902",
      //   "crmproductid": "PID002",
      //   "isEngineering": "产品咨询",
      //   "p6Id": "P6-1002",
      //   "serviceordertype": "询价",
      //   "status": "进行中",
      //   "createdate": "2023-09-02T11:00:00Z",
      //   "editdate": "2023-09-06T13:00:00Z",
      //   "assignment": "已分派",
      //   "employeeId": "EMP002"
      // },
      {
        "parentserviceorder": "PO123458",
        "crmquotenumber": "QT20230903",
        "crmproductid": "PID003",
        "isEngineering": "售后服务",
        "p6Id": "P6-1003",
        "serviceordertype": "安装",
        "status": "待处理",
        "createdate": "2023-09-03T09:30:00Z",
        "editdate": "2023-09-07T14:00:00Z",
        "assignment": "进行中",
        "employeeId": "EMP003"
      },
      {
        "parentserviceorder": "PO123459",
        "crmquotenumber": "QT20230904",
        "crmproductid": "PID004",
        "isEngineering": "项目管理",
        "p6Id": "P6-1004",
        "serviceordertype": "设计",
        "status": "已完成",
        "createdate": "2023-09-04T15:45:00Z",
        "editdate": "2023-09-08T16:00:00Z",
        "assignment": "待分派",
        "employeeId": "EMP004"
      },
      {
        "parentserviceorder": "PO123460",
        "crmquotenumber": "QT20230905",
        "crmproductid": "PID005",
        "isEngineering": "技术支持",
        "p6Id": "P6-1005",
        "serviceordertype": "维护",
        "status": "进行中",
        "createdate": "2023-09-05T08:00:00Z",
        "editdate": "2023-09-09T09:00:00Z",
        "assignment": "已分派",
        "employeeId": "EMP005"
      },
      {
        "parentserviceorder": "PO123461",
        "crmquotenumber": "QT20230906",
        "crmproductid": "PID006",
        "isEngineering": "产品咨询",
        "p6Id": "P6-1006",
        "serviceordertype": "询价",
        "status": "待处理",
        "createdate": "2023-09-06T10:15:00Z",
        "editdate": "2023-09-10T11:00:00Z",
        "assignment": "进行中",
        "employeeId": "EMP006"
      },
      {
        "parentserviceorder": "PO123462",
        "crmquotenumber": "QT20230907",
        "crmproductid": "PID007",
        "isEngineering": "售后服务",
        "p6Id": "P6-1007",
        "serviceordertype": "安装",
        "status": "已完成",
        "createdate": "2023-09-07T14:30:00Z",
        "editdate": "2023-09-11T17:00:00Z",
        "assignment": "待分派",
        "employeeId": "EMP007"
      },
      ]
    total.value = 100
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
    parentserviceorder: null,
    crmquotenumber: null,
    crmproductid: null,
    p6Id: null,
    serviceordertype: null,
    status: null,
    createdate: null,
    editdate: null,
    dealtime: null,
    archivedate: null,
    isEngineering: null,
    ordertypeId: null,
    assignment: null,
    employeeId: null
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

const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.parentserviceorder);
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
  const parentserviceorder = row.parentserviceorder || ids.value;
  try {
    const response = await getParentservice(parentserviceorder);
    form.value = response.data;
    open.value = true;
    title.value = "修改【请填写功能名称】";
  } catch (error) {
    console.error(error);
  }
};

const submitForm = () => {
  // Implement your form validation logic here
  const valid = true; // Replace with actual validation
  if (valid) {
    if (form.value.parentserviceorder != null) {
      updateParentservice(form.value).then(() => {
        // Show success message here
        open.value = false;
        getList();
      });
    } else {
      addParentservice(form.value).then(() => {
        // Show success message here
        open.value = false;
        getList();
      });
    }
  }
};

const handleDelete = async (row) => {
  const parentserviceorders = row.parentserviceorder || ids.value;
  try {
    // Confirm deletion
    await this.$modal.confirm(`是否确认删除【请填写功能名称】编号为"${parentserviceorders}"的数据项？`);
    await delParentservice(parentserviceorders);
    getList();
    // Show success message here
  } catch (error) {
    // Handle error or cancellation
  }
};

const handleExport = () => {
  download('system/parentservice/export', {
    ...queryParams.value
  }, `parentservice_${new Date().getTime()}.xlsx`);
};

</script>
