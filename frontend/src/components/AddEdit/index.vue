<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps(['title', 'formData', 'formList', 'rules'])
const emit = defineEmits(['submitData', 'cancel','selectChange','update:formData'])


const open = ref(false)
const form = ref(null) // 获取表单的引用



const localForm = computed({
  get: () => props.formData,
  set: (value) => emit('update:formData', value),
});
const openDialog = () => {
  open.value = true
}

const closeDialog = () => {
  open.value = false
}

const cancel = () => {
  emit('cancel')
}

const submitForm = () => {

  form.value.validate((valid) => {
    if (valid) {
      console.log('localForm', localForm.value)
      emit('submitData', localForm.value) // 提交本地表单数据
    } else {
      console.log('表单校验不通过')
      // 这里可以添加一些提示，比如使用 Element Plus 的 Message 组件
    }
  })
}
const selectChange = (prop, value) => {
  emit('selectChange', prop, value );
};
defineExpose({
  openDialog,
  closeDialog,
})
</script>

<template>
  <div>
    <el-dialog :title="title" v-model="open" width="50%" append-to-body>
      <el-form ref="form" :model="localForm" :rules="rules" label-width="120px">
        <el-form-item
            :label="item.label"
            :prop="item.prop"
            v-for="item in formList"
            :key="item.prop"
        >

          <el-input
              v-if="!item.type || item.type === 'input'"
              v-model="localForm[item.prop]"
              placeholder="请输入"
              clearable

          />
          <el-select
              v-else-if="item.type === 'select'"
              v-model="localForm[item.prop]"
              placeholder="请选择"
              clearable
              @change="selectChange(item.prop,localForm[item.prop])"
          >
            <el-option
                v-for="option in item.options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
            />
          </el-select>
          <el-date-picker
              v-else-if="item.type === 'datetime'"
              v-model="localForm[item.prop]"
              type="datetime"
              placeholder="请选择时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              clearable
          />

        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
/* 样式可以根据需要添加 */
</style>
