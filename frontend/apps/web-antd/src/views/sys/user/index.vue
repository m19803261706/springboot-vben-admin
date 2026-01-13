<script lang="ts" setup>
/**
 * 用户管理列表页
 * 提供用户的增删改查、状态切换、角色分配等功能
 *
 * @author CX
 * @since 2026-01-13
 */
import { ref, reactive, onMounted, h } from 'vue';
import {
  Card,
  Form,
  FormItem,
  Input,
  Select,
  SelectOption,
  Button,
  Space,
  Table,
  Tag,
  Popconfirm,
  Modal,
  InputPassword,
  TreeSelect,
  message,
  Switch,
} from 'ant-design-vue';
import {
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined,
  KeyOutlined,
  UserSwitchOutlined,
} from '@ant-design/icons-vue';
import type { TableColumnsType, TablePaginationConfig } from 'ant-design-vue';
import {
  getUserList,
  createUser,
  updateUser,
  deleteUser,
  resetUserPassword,
  updateUserStatus,
  assignUserRoles,
  type UserRecord,
  type UserQueryParams,
  type UserCreateParams,
  type UserUpdateParams,
} from '#/api/sys/user';
import { getDeptTree, type DeptRecord } from '#/api/sys/dept';
import { getAllRoles, type RoleRecord } from '#/api/sys/role';

defineOptions({ name: 'UserList' });

// ============ 搜索相关 ============

/** 搜索表单数据 */
const searchForm = reactive<UserQueryParams>({
  username: '',
  realName: '',
  phone: '',
  deptId: undefined,
  status: undefined,
});

/** 重置搜索表单 */
function handleResetSearch() {
  searchForm.username = '';
  searchForm.realName = '';
  searchForm.phone = '';
  searchForm.deptId = undefined;
  searchForm.status = undefined;
  handleSearch();
}

/** 执行搜索 */
function handleSearch() {
  pagination.current = 1;
  loadData();
}

// ============ 表格相关 ============

/** 加载状态 */
const loading = ref(false);

/** 表格数据 */
const dataSource = ref<UserRecord[]>([]);

/** 分页配置 */
const pagination = reactive<TablePaginationConfig>({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`,
});

/** 表格列配置 */
const columns: TableColumnsType = [
  { title: '用户名', dataIndex: 'username', width: 120 },
  { title: '真实姓名', dataIndex: 'realName', width: 100 },
  { title: '手机号', dataIndex: 'phone', width: 130 },
  { title: '部门', dataIndex: 'deptName', width: 120 },
  {
    title: '角色',
    dataIndex: 'roles',
    width: 200,
    customRender: ({ record }) => {
      const roles = record.roles || [];
      return h(
        Space,
        { wrap: true },
        () => roles.map((role: { roleName: string }) =>
          h(Tag, { color: 'blue' }, () => role.roleName)
        )
      );
    },
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
    customRender: ({ record }) => {
      return h(Switch, {
        checked: record.status === 1,
        checkedChildren: '启用',
        unCheckedChildren: '禁用',
        loading: statusLoading.value[record.id],
        onChange: () => handleStatusChange(record),
      });
    },
  },
  { title: '创建时间', dataIndex: 'createdAt', width: 180 },
  {
    title: '操作',
    key: 'action',
    width: 220,
    fixed: 'right',
  },
];

/** 状态切换加载状态 */
const statusLoading = ref<Record<number, boolean>>({});

/** 加载表格数据 */
async function loadData() {
  loading.value = true;
  try {
    const res = await getUserList({
      page: (pagination.current || 1) - 1, // 后端从0开始
      size: pagination.pageSize,
      ...searchForm,
    });
    dataSource.value = res.content;
    pagination.total = res.totalElements;
  } catch (error) {
    console.error('加载用户列表失败:', error);
  } finally {
    loading.value = false;
  }
}

/** 表格分页变化 */
function handleTableChange(pag: TablePaginationConfig) {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  loadData();
}

/** 切换用户状态 */
async function handleStatusChange(record: UserRecord) {
  const newStatus = record.status === 1 ? 0 : 1;
  statusLoading.value[record.id] = true;
  try {
    await updateUserStatus(record.id, newStatus);
    record.status = newStatus;
    message.success(newStatus === 1 ? '用户已启用' : '用户已禁用');
  } catch (error) {
    console.error('更新状态失败:', error);
  } finally {
    statusLoading.value[record.id] = false;
  }
}

/** 删除用户 */
async function handleDelete(id: number) {
  try {
    await deleteUser(id);
    message.success('删除成功');
    loadData();
  } catch (error) {
    console.error('删除用户失败:', error);
  }
}

// ============ 部门树和角色列表 ============

/** 部门树数据 */
const deptTree = ref<DeptRecord[]>([]);

/** 角色列表 */
const roleList = ref<RoleRecord[]>([]);

/** 加载部门树 */
async function loadDeptTree() {
  try {
    deptTree.value = await getDeptTree();
  } catch (error) {
    console.error('加载部门树失败:', error);
  }
}

/** 加载角色列表 */
async function loadRoleList() {
  try {
    const res = await getAllRoles();
    roleList.value = res.content;
  } catch (error) {
    console.error('加载角色列表失败:', error);
  }
}

// ============ 用户表单弹窗 ============

/** 表单弹窗可见性 */
const formVisible = ref(false);

/** 表单模式 (create/edit) */
const formMode = ref<'create' | 'edit'>('create');

/** 编辑中的用户ID */
const editingId = ref<number>();

/** 表单数据 */
const formData = reactive<UserCreateParams>({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  avatar: '',
  deptId: undefined,
  status: 1,
  roleIds: [],
});

/** 表单提交中 */
const formSubmitting = ref(false);

/** 打开新增弹窗 */
function handleCreate() {
  formMode.value = 'create';
  editingId.value = undefined;
  // 重置表单
  formData.username = '';
  formData.password = '';
  formData.realName = '';
  formData.phone = '';
  formData.email = '';
  formData.avatar = '';
  formData.deptId = undefined;
  formData.status = 1;
  formData.roleIds = [];
  formVisible.value = true;
}

/** 打开编辑弹窗 */
function handleEdit(record: UserRecord) {
  formMode.value = 'edit';
  editingId.value = record.id;
  // 填充表单
  formData.username = record.username;
  formData.password = '';
  formData.realName = record.realName;
  formData.phone = record.phone || '';
  formData.email = record.email || '';
  formData.avatar = record.avatar || '';
  formData.deptId = record.deptId;
  formData.status = record.status;
  formData.roleIds = record.roles?.map(r => r.id) || [];
  formVisible.value = true;
}

/** 提交表单 */
async function handleFormSubmit() {
  formSubmitting.value = true;
  try {
    if (formMode.value === 'create') {
      await createUser(formData);
      message.success('用户创建成功');
    } else {
      const updateData: UserUpdateParams = {
        realName: formData.realName,
        phone: formData.phone,
        email: formData.email,
        avatar: formData.avatar,
        deptId: formData.deptId,
        status: formData.status,
        roleIds: formData.roleIds,
      };
      await updateUser(editingId.value!, updateData);
      message.success('用户更新成功');
    }
    formVisible.value = false;
    loadData();
  } catch (error) {
    console.error('保存用户失败:', error);
  } finally {
    formSubmitting.value = false;
  }
}

// ============ 重置密码弹窗 ============

/** 重置密码弹窗可见性 */
const resetPwdVisible = ref(false);

/** 重置密码的用户ID */
const resetPwdUserId = ref<number>();

/** 重置密码的用户名 */
const resetPwdUsername = ref('');

/** 新密码 */
const newPassword = ref('');

/** 重置密码提交中 */
const resetPwdSubmitting = ref(false);

/** 打开重置密码弹窗 */
function handleResetPwd(record: UserRecord) {
  resetPwdUserId.value = record.id;
  resetPwdUsername.value = record.username;
  newPassword.value = '';
  resetPwdVisible.value = true;
}

/** 提交重置密码 */
async function handleResetPwdSubmit() {
  if (!newPassword.value) {
    message.warning('请输入新密码');
    return;
  }
  resetPwdSubmitting.value = true;
  try {
    await resetUserPassword(resetPwdUserId.value!, newPassword.value);
    message.success('密码重置成功');
    resetPwdVisible.value = false;
  } catch (error) {
    console.error('重置密码失败:', error);
  } finally {
    resetPwdSubmitting.value = false;
  }
}

// ============ 分配角色弹窗 ============

/** 分配角色弹窗可见性 */
const assignRoleVisible = ref(false);

/** 分配角色的用户ID */
const assignRoleUserId = ref<number>();

/** 分配角色的用户名 */
const assignRoleUsername = ref('');

/** 已选择的角色ID */
const selectedRoleIds = ref<number[]>([]);

/** 分配角色提交中 */
const assignRoleSubmitting = ref(false);

/** 打开分配角色弹窗 */
function handleAssignRole(record: UserRecord) {
  assignRoleUserId.value = record.id;
  assignRoleUsername.value = record.username;
  selectedRoleIds.value = record.roles?.map(r => r.id) || [];
  assignRoleVisible.value = true;
}

/** 提交分配角色 */
async function handleAssignRoleSubmit() {
  assignRoleSubmitting.value = true;
  try {
    await assignUserRoles(assignRoleUserId.value!, selectedRoleIds.value);
    message.success('角色分配成功');
    assignRoleVisible.value = false;
    loadData();
  } catch (error) {
    console.error('分配角色失败:', error);
  } finally {
    assignRoleSubmitting.value = false;
  }
}

// ============ 生命周期 ============

onMounted(() => {
  loadData();
  loadDeptTree();
  loadRoleList();
});
</script>

<template>
  <div class="p-4">
    <!-- 搜索区域 -->
    <Card class="mb-4">
      <Form layout="inline" :model="searchForm">
        <FormItem label="用户名">
          <Input
            v-model:value="searchForm.username"
            placeholder="请输入用户名"
            allow-clear
            @pressEnter="handleSearch"
          />
        </FormItem>
        <FormItem label="真实姓名">
          <Input
            v-model:value="searchForm.realName"
            placeholder="请输入真实姓名"
            allow-clear
            @pressEnter="handleSearch"
          />
        </FormItem>
        <FormItem label="手机号">
          <Input
            v-model:value="searchForm.phone"
            placeholder="请输入手机号"
            allow-clear
            @pressEnter="handleSearch"
          />
        </FormItem>
        <FormItem label="部门">
          <TreeSelect
            v-model:value="searchForm.deptId"
            :tree-data="deptTree"
            :field-names="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择部门"
            allow-clear
            style="width: 180px"
          />
        </FormItem>
        <FormItem label="状态">
          <Select
            v-model:value="searchForm.status"
            placeholder="请选择状态"
            allow-clear
            style="width: 120px"
          >
            <SelectOption :value="1">启用</SelectOption>
            <SelectOption :value="0">禁用</SelectOption>
          </Select>
        </FormItem>
        <FormItem>
          <Space>
            <Button type="primary" @click="handleSearch">
              <SearchOutlined />
              查询
            </Button>
            <Button @click="handleResetSearch">
              <ReloadOutlined />
              重置
            </Button>
          </Space>
        </FormItem>
      </Form>
    </Card>

    <!-- 表格区域 -->
    <Card>
      <template #title>
        <Space>
          <Button type="primary" @click="handleCreate">
            <PlusOutlined />
            新增用户
          </Button>
        </Space>
      </template>

      <Table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="pagination"
        :scroll="{ x: 1200 }"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <Space>
              <Button type="link" size="small" @click="handleEdit(record)">
                <EditOutlined />
                编辑
              </Button>
              <Button type="link" size="small" @click="handleResetPwd(record)">
                <KeyOutlined />
                重置密码
              </Button>
              <Button type="link" size="small" @click="handleAssignRole(record)">
                <UserSwitchOutlined />
                分配角色
              </Button>
              <Popconfirm
                title="确定要删除该用户吗？"
                @confirm="handleDelete(record.id)"
              >
                <Button type="link" size="small" danger>
                  <DeleteOutlined />
                  删除
                </Button>
              </Popconfirm>
            </Space>
          </template>
        </template>
      </Table>
    </Card>

    <!-- 用户表单弹窗 -->
    <Modal
      v-model:open="formVisible"
      :title="formMode === 'create' ? '新增用户' : '编辑用户'"
      :confirm-loading="formSubmitting"
      @ok="handleFormSubmit"
    >
      <Form :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <FormItem label="用户名" required>
          <Input
            v-model:value="formData.username"
            placeholder="请输入用户名"
            :disabled="formMode === 'edit'"
          />
        </FormItem>
        <FormItem v-if="formMode === 'create'" label="密码" required>
          <InputPassword
            v-model:value="formData.password"
            placeholder="请输入密码"
          />
        </FormItem>
        <FormItem label="真实姓名" required>
          <Input
            v-model:value="formData.realName"
            placeholder="请输入真实姓名"
          />
        </FormItem>
        <FormItem label="手机号">
          <Input
            v-model:value="formData.phone"
            placeholder="请输入手机号"
          />
        </FormItem>
        <FormItem label="邮箱">
          <Input
            v-model:value="formData.email"
            placeholder="请输入邮箱"
          />
        </FormItem>
        <FormItem label="部门">
          <TreeSelect
            v-model:value="formData.deptId"
            :tree-data="deptTree"
            :field-names="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择部门"
            allow-clear
          />
        </FormItem>
        <FormItem label="角色">
          <Select
            v-model:value="formData.roleIds"
            mode="multiple"
            placeholder="请选择角色"
            :options="roleList.map(r => ({ label: r.roleName, value: r.id }))"
          />
        </FormItem>
        <FormItem label="状态">
          <Select v-model:value="formData.status">
            <SelectOption :value="1">启用</SelectOption>
            <SelectOption :value="0">禁用</SelectOption>
          </Select>
        </FormItem>
      </Form>
    </Modal>

    <!-- 重置密码弹窗 -->
    <Modal
      v-model:open="resetPwdVisible"
      title="重置密码"
      :confirm-loading="resetPwdSubmitting"
      @ok="handleResetPwdSubmit"
    >
      <Form :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <FormItem label="用户名">
          <Input :value="resetPwdUsername" disabled />
        </FormItem>
        <FormItem label="新密码" required>
          <InputPassword
            v-model:value="newPassword"
            placeholder="请输入新密码"
          />
        </FormItem>
      </Form>
    </Modal>

    <!-- 分配角色弹窗 -->
    <Modal
      v-model:open="assignRoleVisible"
      title="分配角色"
      :confirm-loading="assignRoleSubmitting"
      @ok="handleAssignRoleSubmit"
    >
      <Form :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <FormItem label="用户名">
          <Input :value="assignRoleUsername" disabled />
        </FormItem>
        <FormItem label="角色" required>
          <Select
            v-model:value="selectedRoleIds"
            mode="multiple"
            placeholder="请选择角色"
            :options="roleList.map(r => ({ label: r.roleName, value: r.id }))"
            style="width: 100%"
          />
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>
