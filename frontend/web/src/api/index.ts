import request from '@/utils/request'

// 认证相关 API
export const authApi = {
  login(data: { username: string; password: string }) {
    return request.post('/auth/login', data)
  },
  logout() {
    return request.post('/auth/logout')
  },
  validate() {
    return request.get('/auth/validate')
  }
}

// 账号管理 API
export const accountApi = {
  getAll() {
    return request.get<Account[], Account[]>('/accounts')
  },
  getById(id: number) {
    return request.get<Account, Account>(`/accounts/${id}`)
  },
  getPage(params: AccountPageParams) {
    return request.get<PageResult<Account>, PageResult<Account>>('/accounts/page', { params })
  },
  create(data: AccountInput) {
    return request.post<Account, Account>('/accounts', data)
  },
  update(id: number, data: AccountInput) {
    return request.put<Account, Account>(`/accounts/${id}`, data)
  },
  delete(id: number) {
    return request.delete(`/accounts/${id}`)
  }
}

export interface Account {
  id: number
  username: string
  name: string
  gender: string | null
  phone: string | null
  roleLevel: number
  status: number
  isSuper: number
  departmentId: number | null
  departmentName: string | null
  createTime: string
  updateTime: string
}

export interface AccountInput {
  username: string
  name: string
  gender: string
  phone: string
  password?: string
  roleLevel: number
  status: number
  isSuper: number
  departmentId: number | null
}

export interface AccountQuery {
  username?: string
  status?: number
  roleLevel?: number
}

export interface AccountPageParams extends AccountQuery {
  page: number
  size: number
}

export interface PageResult<T> {
  list: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}

export interface Department {
  id: number
  name: string
  code: string
  description: string | null
  status: number
  createTime: string
  updateTime: string
}

export interface DepartmentInput {
  name: string
  code: string
  description: string
  status: number
}

export const departmentApi = {
  getAll() {
    return request.get<Department[], Department[]>('/departments')
  },
  getById(id: number) {
    return request.get<Department, Department>(`/departments/${id}`)
  },
  create(data: DepartmentInput) {
    return request.post<Department, Department>('/departments', data)
  },
  update(id: number, data: DepartmentInput) {
    return request.put<Department, Department>(`/departments/${id}`, data)
  },
  delete(id: number) {
    return request.delete<void, void>(`/departments/${id}`)
  }
}
