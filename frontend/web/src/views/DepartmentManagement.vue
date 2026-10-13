<template>
  <div class="department-management">
    <div class="page-header">
      <el-button type="primary" :icon="Plus" @click="openCreateDialog">添加部门</el-button>
      <el-button :icon="Refresh" :loading="loading" @click="loadDepartments">刷新</el-button>
    </div>

    <el-table v-loading="loading" :data="departments" border stripe empty-text="暂无部门" style="width: 100%">
      <el-table-column prop="name" label="部门名称" min-width="140" show-overflow-tooltip />
      <el-table-column prop="code" label="部门编码" min-width="120" show-overflow-tooltip />
      <el-table-column prop="description" label="部门描述" min-width="180" show-overflow-tooltip />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="更新时间" width="180">
        <template #default="{ row }">{{ formatTime(row.updateTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="openEditDialog(row)">编辑</el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑部门' : '添加部门'"
      width="min(500px, calc(100vw - 32px))"
      :close-on-click-modal="!submitting"
      :close-on-press-escape="!submitting"
      :show-close="!submitting"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" :disabled="submitting" label-width="90px" @submit.prevent="handleSubmit">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入部门名称" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="部门编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入部门编码" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="部门描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入部门描述" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button :disabled="submitting" @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { departmentApi } from '@/api'
import type { Department, DepartmentInput } from '@/api'
import { isAxiosError } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(0)
const departments = ref<Department[]>([])
const formRef = ref<FormInstance>()
const formData = reactive<DepartmentInput>({ name: '', code: '', description: '', status: 1 })

const formRules: FormRules = {
  name: [
    { required: true, whitespace: true, message: '请输入部门名称', trigger: 'blur' },
    { max: 100, message: '部门名称不能超过100个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, whitespace: true, message: '请输入部门编码', trigger: 'blur' },
    { max: 50, message: '部门编码不能超过50个字符', trigger: 'blur' }
  ],
  description: [{ max: 500, message: '部门描述不能超过500个字符', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function formatTime(value: string | null) {
  return value ? value.replace('T', ' ').slice(0, 19) : '-'
}

function showRequestError(error: unknown, message: string) {
  if (!isAxiosError(error)) {
    ElMessage.error(error instanceof Error ? error.message : message)
  }
}

async function loadDepartments() {
  loading.value = true
  try {
    departments.value = await departmentApi.getAll()
  } catch (error) {
    showRequestError(error, '加载部门列表失败')
  } finally {
    loading.value = false
  }
}

function resetForm() {
  Object.assign(formData, { name: '', code: '', description: '', status: 1 })
  isEdit.value = false
  editId.value = 0
  formRef.value?.clearValidate()
}

function openCreateDialog() {
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row: Department) {
  resetForm()
  isEdit.value = true
  editId.value = row.id
  Object.assign(formData, {
    name: row.name,
    code: row.code,
    description: row.description || '',
    status: row.status
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value || submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid || submitting.value) return

  submitting.value = true
  try {
    const data = { ...formData, name: formData.name.trim(), code: formData.code.trim() }
    if (isEdit.value) {
      await departmentApi.update(editId.value, data)
      ElMessage.success('部门更新成功')
    } else {
      await departmentApi.create(data)
      ElMessage.success('部门创建成功')
    }
    dialogVisible.value = false
    await loadDepartments()
  } catch (error) {
    showRequestError(error, '保存部门失败')
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row: Department) {
  try {
    await ElMessageBox.confirm(`确定要删除部门 "${row.name}" 吗？`, '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    await departmentApi.delete(row.id)
    ElMessage.success('部门删除成功')
    await loadDepartments()
  } catch (error) {
    showRequestError(error, '删除部门失败')
  }
}

onMounted(loadDepartments)
</script>

<style scoped>
.department-management {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.page-header {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
</style>
