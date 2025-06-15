<script setup>
const props = defineProps({
  queryParams: {
    type: Object,
    required: true,
  },
  searchFormList: {
    type: Array,
    required: true,
  },
});

const emit = defineEmits(['update:queryParams', 'handleQuery', 'resetQuery','selectChange']);

// 使用计算属性的 setter 来实现 v-model
const queryParamsModel = computed({
  get: () => props.queryParams,
  set: (value) => emit('update:queryParams', value),
});

const handleQuery = () => {
  emit('handleQuery');
};

const resetQuery = () => {
  emit('resetQuery');
};
const selectChange = (prop, value) => {
  emit('selectChange', prop, value );
};
</script>

<template>
  <el-form :model="queryParamsModel" ref="queryForm" size="small" label-width="65px">
    <el-row>
      <el-col :span="4" v-for="item in searchFormList" :key="item.prop">
        <el-form-item :label="item.label" :prop="item.prop">
          <!-- 根据 type 字段判断渲染不同的表单控件 -->
          <el-input
              v-if="!item.type || item.type === 'input'"
              v-model="queryParamsModel[item.prop]"
              placeholder="请输入"
              clearable
              @keyup.enter.native="handleQuery"
          />
          <el-select
              v-else-if="item.type === 'select'"
              v-model="queryParamsModel[item.prop]"
              placeholder="请选择"
              clearable
              @change="selectChange(item.prop,queryParamsModel[item.prop])"
          >
            <el-option
                v-for="option in item.options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
            />
          </el-select>

        </el-form-item>
      </el-col>

      <el-col :span="4">
        <el-form-item>
          <el-button type="primary" size="small" @click="handleQuery">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button size="small" @click="resetQuery">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>
