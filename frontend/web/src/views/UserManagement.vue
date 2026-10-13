<template>
  <div class="user-management">
    <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent="handleSearch">
      <el-form-item label="用户名">
        <el-input v-model="searchForm.username" placeholder="搜索用户名" clearable maxlength="50" @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 130px">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="权限级别">
        <el-select v-model="searchForm.roleLevel" placeholder="全部权限" clearable style="width: 160px">
          <el-option label="用户权限" :value="1" />
          <el-option label="管理员权限" :value="2" />
          <el-option label="超级管理员权限" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="page-header">
      <el-button type="primary" :icon="Plus" @click="openCreateDialog">
        添加用户
      </el-button>
      <el-button :icon="Refresh" @click="loadUsers">刷新</el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="users"
      empty-text="暂无用户"
      border
      stripe
      style="width: 100%"
    >
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column prop="phone" label="联系方式" width="140" />
      <el-table-column label="所属部门" min-width="140" show-overflow-tooltip>
        <template #default="{ row }">{{ row.departmentName || '-' }}</template>
      </el-table-column>
      <el-table-column label="权限级别" width="120">
        <template #default="{ row }">
          <el-tag :type="roleTagType(row.roleLevel)">
            {{ roleText(row.roleLevel) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="openEditDialog(row)">
            编辑
          </el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-config-provider :locale="zhCn">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalElements"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @update:current-page="handlePageChange"
          @update:page-size="handleSizeChange"
        />
      </el-config-provider>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="min(500px, calc(100vw - 32px))"
      :close-on-click-modal="!submitting"
      :close-on-press-escape="!submitting"
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        :disabled="submitting"
        label-width="90px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="formData.gender" placeholder="请选择性别" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系方式" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="所属部门" prop="departmentId">
          <el-select v-model="formData.departmentId" placeholder="请选择所属部门（可不选）" clearable filterable :loading="departmentLoading" style="width: 100%" @clear="formData.departmentId = null">
            <el-option v-if="currentDepartmentUnavailable" :label="`${editingDepartmentName || '原部门'}（不可选）`" :value="formData.departmentId!" disabled />
            <el-option v-for="department in departments" :key="department.id" :label="department.name" :value="department.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            show-password
            :placeholder="isEdit ? '留空则不修改密码' : '留空则默认123456'"
          />
        </el-form-item>
        <el-form-item label="权限级别" prop="roleLevel">
          <el-select v-model="formData.roleLevel" placeholder="请选择权限级别" style="width: 100%">
            <el-option label="用户权限" :value="1" />
            <el-option label="管理员权限" :value="2" />
            <el-option label="超级管理员权限" :value="3" />
          </el-select>
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
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { accountApi, departmentApi } from '@/api'
import type { Account, AccountInput, AccountQuery, Department } from '@/api'
import { isAxiosError } from 'axios'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Refresh, Search } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number>(0)

const users = ref<Account[]>([])
const departments = ref<Department[]>([])
const departmentLoading = ref(false)
const editingDepartmentName = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const totalElements = ref(0)
const searchForm = reactive({ username: '', status: undefined as number | undefined, roleLevel: undefined as number | undefined })
const appliedFilters = reactive<AccountQuery>({})
let loadSequence = 0

const formRef = ref<FormInstance>()

const formData = reactive({
  username: '',
  name: '',
  gender: '男',
  phone: '',
  password: '',
  roleLevel: 1,
  status: 1,
  isSuper: 0,
  departmentId: null as number | null
})

const currentDepartmentUnavailable = computed(() => isEdit.value && formData.departmentId !== null &&
  !departments.value.some(department => department.id === formData.departmentId))

const formRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  roleLevel: [{ required: true, message: '请选择权限级别', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function roleText(level: number): string {
  const map: Record<number, string> = { 1: '用户权限', 2: '管理员权限', 3: '超级管理员' }
  return map[level] || '未知'
}

function roleTagType(level: number): 'info' | 'warning' | 'danger' {
  const map: Record<number, 'info' | 'warning' | 'danger'> = {
    1: 'info',
    2: 'warning',
    3: 'danger'
  }
  return map[level] || 'info'
}

async function loadUsers() {
  const sequence = ++loadSequence
  loading.value = true
  try {
    let result = await accountApi.getPage({ ...appliedFilters, page: currentPage.value - 1, size: pageSize.value })
    if (sequence !== loadSequence) return
    const lastPage = Math.max(result.totalPages, 1)
    if (currentPage.value > lastPage) {
      currentPage.value = lastPage
      result = await accountApi.getPage({ ...appliedFilters, page: currentPage.value - 1, size: pageSize.value })
      if (sequence !== loadSequence) return
    }
    users.value = result.list
    totalElements.value = result.totalElements
  } catch (error) {
    showRequestError(error, '加载用户列表失败')
  } finally {
    if (sequence === loadSequence) loading.value = false
  }
}

async function loadDepartments() {
  departmentLoading.value = true
  try {
    departments.value = await departmentApi.getAll()
  } catch (error) {
    showRequestError(error, '加载部门列表失败')
  } finally {
    departmentLoading.value = false
  }
}

function showRequestError(error: unknown, message: string) {
  if (!isAxiosError(error)) ElMessage.error(error instanceof Error ? error.message : message)
}

function handleSearch() {
  appliedFilters.username = searchForm.username.trim() || undefined
  appliedFilters.status = searchForm.status ?? undefined
  appliedFilters.roleLevel = searchForm.roleLevel ?? undefined
  currentPage.value = 1
  loadUsers()
}

function resetSearch() {
  searchForm.username = ''
  searchForm.status = undefined
  searchForm.roleLevel = undefined
  handleSearch()
}

function handlePageChange(page: number) {
  if (page === currentPage.value) return
  currentPage.value = page
  loadUsers()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  currentPage.value = 1
  loadUsers()
}

function resetForm() {
  formData.username = ''
  formData.name = ''
  formData.gender = '男'
  formData.phone = ''
  formData.password = ''
  formData.roleLevel = 1
  formData.status = 1
  formData.isSuper = 0
  formData.departmentId = null
  editingDepartmentName.value = ''
  isEdit.value = false
  editId.value = 0
  formRef.value?.clearValidate()
}

function openCreateDialog() {
  resetForm()
  dialogVisible.value = true
  loadDepartments()
}

function openEditDialog(row: Account) {
  resetForm()
  isEdit.value = true
  editId.value = row.id
  formData.username = row.username
  formData.name = row.name
  formData.gender = row.gender || '男'
  formData.phone = row.phone || ''
  formData.roleLevel = row.roleLevel
  formData.status = row.status
  formData.isSuper = row.isSuper
  formData.departmentId = row.departmentId ?? null
  editingDepartmentName.value = row.departmentName || ''
  dialogVisible.value = true
  loadDepartments()
}

async function handleSubmit() {
  if (!formRef.value || submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid || submitting.value) return

  submitting.value = true
  try {
    const { password, ...fields } = formData
    const data: AccountInput = { ...fields, ...(password ? { password } : {}) }
    if (isEdit.value) {
      await accountApi.update(editId.value, data)
      ElMessage.success('用户更新成功')
    } else {
      await accountApi.create(data)
      ElMessage.success('用户创建成功')
    }
    dialogVisible.value = false
    await loadUsers()
  } catch (error) {
    showRequestError(error, '保存用户失败')
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row: Account) {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${row.username}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
  } catch {
    return
  }

  try {
    await accountApi.delete(row.id)
    ElMessage.success('用户删除成功')
    await loadUsers()
  } catch (error) {
    showRequestError(error, '删除用户失败')
  }
}

onMounted(() => {
  loadUsers()
  loadDepartments()
})
</script>

<style scoped>
.user-management {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.page-header {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-form .el-form-item {
  margin-right: 12px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  overflow-x: auto;
}
</style>
