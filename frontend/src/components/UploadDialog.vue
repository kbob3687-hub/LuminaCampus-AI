<script setup>
import { ref } from 'vue'
import { uploadDocument } from '../api/document'
import { ElMessage } from 'element-plus'

const props = defineProps({ modelValue: Boolean })
const emit = defineEmits(['update:modelValue'])

const fileList = ref([])
const subject = ref('')
const uploading = ref(false)

function handleClose() {
  emit('update:modelValue', false)
  fileList.value = []
  subject.value = ''
}

async function handleUpload() {
  if (!fileList.value.length) {
    ElMessage.warning('请选择要上传的文件')
    return
  }
  uploading.value = true
  try {
    for (const file of fileList.value) {
      await uploadDocument(file.raw, subject.value)
    }
    ElMessage.success('上传成功')
    handleClose()
  } catch (e) {
    ElMessage.error('上传失败，请重试')
  } finally {
    uploading.value = false
  }
}
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    @update:model-value="emit('update:modelValue', $event)"
    title="上传学习资料"
    width="460px"
    :close-on-click-modal="false"
    class="upload-dialog"
  >
    <div class="upload-body">
      <el-upload
        drag
        :auto-upload="false"
        v-model:file-list="fileList"
        accept=".pdf,.doc,.docx,.txt,.md"
        :limit="5"
        class="upload-area"
      >
        <el-icon class="upload-icon"><UploadFilled /></el-icon>
        <div class="upload-text">拖拽文件到此处，或 <em>点击选择</em></div>
        <template #tip>
          <div class="upload-tip">支持 PDF / Word / 纯文本 / Markdown，单文件最大 10MB</div>
        </template>
      </el-upload>

      <div class="subject-select">
        <span class="label">关联学科</span>
        <el-select v-model="subject" placeholder="可选，不选则通用问答" clearable>
          <el-option label="数据结构" value="数据结构" />
          <el-option label="计算机网络" value="计算机网络" />
          <el-option label="高等数学" value="高等数学" />
        </el-select>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="uploading" @click="handleUpload">
        上传并构建索引
      </el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.upload-body {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.upload-area :deep(.el-upload-dragger) {
  border-radius: var(--radius);
  border-color: var(--border);
  padding: 32px 20px;
  transition: all 0.15s;
}

.upload-area :deep(.el-upload-dragger:hover) {
  border-color: var(--primary);
}

.upload-icon {
  font-size: 48px;
  color: var(--primary-light);
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  color: var(--text-secondary);
}

.upload-text em {
  color: var(--primary);
  font-style: normal;
  cursor: pointer;
}

.upload-tip {
  font-size: 12px;
  color: var(--text-light);
  margin-top: 8px;
}

.subject-select {
  display: flex;
  align-items: center;
  gap: 12px;
}

.subject-select .label {
  font-size: 14px;
  color: var(--text);
  font-weight: 500;
  white-space: nowrap;
}

.subject-select .el-select {
  flex: 1;
}
</style>
