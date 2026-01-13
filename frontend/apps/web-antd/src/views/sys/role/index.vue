<script lang="ts" setup>
/**
 * 角色管理列表页
 * 提供角色的增删改查、菜单权限分配、数据权限设置等功能
 *
 * @author CX
 * @since 2026-01-13
 */
import { ref, reactive, onMounted, h, computed } from 'vue';
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
  InputNumber,
  Tree,
  TreeSelect,
  message,
  Switch,
  Textarea,
} from 'ant-design-vue';
import {
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined,
  SafetyCertificateOutlined,
  ApartmentOutlined,
} from '@ant-design/icons-vue';
import type { TableColumnsType, TablePaginationConfig, TreeProps } from 'ant-design-vue';
import {
  getRoleList,
  getRoleDetail,
  createRole,
  updateRole,
  deleteRole,
  assignRoleMenus,
  setRoleDataScope,
  type RoleRecord,
  type RoleQueryParams,
} from '#/api/sys/role';
import { getMenuTree, type MenuRecord } from '#/api/sys/menu';
import { getDeptTree, type DeptRecord } from '#/api/sys/dept';

defineOptions({ name: 'RoleManagement' });

// ============ 数据权限选项 ============

/** 数据权限类型选项 */
const dataScopeOptions = [
  { value: 1, label: '全部数据' },
  { value: 2, label: '本部门数据' },
  { value: 3, label: '本部门及下级' },
  { value: 4, label: '仅本人数据' },
  { value: 5, label: '自定义' },
];

/** 根据数据权限值获取标签颜色 */
function getDataScopeColor(dataScope: number): string {
  const colors: Record<number, string> = {
    1: 'red',
    2: 'blue',
    3: 'green',
    4: 'orange',
    5: 'purple',
  };
  return colors[dataScope] || 'default';
}

/** 根据数据权限值获取文本 */
function getDataScopeText(dataScope: number): string {
  const texts: Record<number, string> = {
    1: '全部数据',
    2: '本部门数据',
    3: '本部门及下级',
    4: '仅本人数据',
    5: '自定义',
  };
  return texts[dataScope] || '未知';
}

// ============ 搜索相关 ============

/** 搜索表单数据 */
const searchForm = reactive<RoleQueryParams>({
  roleName: '',
  roleCode: '',
  status: undefined,
});

/** 重置搜索表单 */
function handleResetSearch() {
  searchForm.roleName = '';
  searchForm.roleCode = '';
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
const dataSource = ref<RoleRecord[]>([]);

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
  { title: '角色名称', dataIndex: 'roleName', width: 150 },
  { title: '角色编码', dataIndex: 'roleCode', width: 150 },
  { title: '排序', dataIndex: 'sort', width: 80 },
  {
    title: '数据权限',
    dataIndex: 'dataScope',
    width: 120,
    customRender: ({ record }) => {
      return h(
        Tag,
        { color: getDataScopeColor(record.dataScope) },
        () => getDataScopeText(record.dataScope)
      );
    },
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
    customRender: ({ record }) => {
      return h(
        Tag,
        { color: record.status === 1 ? 'success' : 'error' },
        () => (record.status === 1 ? '启用' : '禁用')
      );
    },
  },
  { title: '备注', dataIndex: 'remark', ellipsis: true },
  { title: '创建时间', dataIndex: 'createdAt', width: 180 },
  {
    title: '操作',
    key: 'action',
    width: 280,
    fixed: 'right',
  },
];

/** 加载表格数据 */
async function loadData() {
  loading.value = true;
  try {
    const res = await getRoleList({
      page: (pagination.current || 1) - 1,
      size: pagination.pageSize,
      ...searchForm,
    });
    dataSource.value = res.content;
    pagination.total = res.totalElements;
  } catch (error) {
    console.error('加载角色列表失败:', error);
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

/** 删除角色 */
async function handleDelete(id: number) {
  try {
    await deleteRole(id);
    message.success('删除成功');
    loadData();
  } catch (error) {
    console.error('删除角色失败:', error);
  }
}

// ============ 角色表单弹窗 ============

/** 表单弹窗可见性 */
const formVisible = ref(false);

/** 表单模式 */
const formMode = ref<'create' | 'edit'>('create');

/** 编辑中的角色ID */
const editingId = ref<number>();

/** 表单数据 */
const formData = reactive({
  roleName: '',
  roleCode: '',
  sort: 0,
  dataScope: 1,
  status: 1,
  remark: '',
});

/** 表单提交中 */
const formSubmitting = ref(false);

/** 打开新增弹窗 */
function handleCreate() {
  formMode.value = 'create';
  editingId.value = undefined;
  formData.roleName = '';
  formData.roleCode = '';
  formData.sort = 0;
  formData.dataScope = 1;
  formData.status = 1;
  formData.remark = '';
  formVisible.value = true;
}

/** 打开编辑弹窗 */
function handleEdit(record: RoleRecord) {
  formMode.value = 'edit';
  editingId.value = record.id;
  formData.roleName = record.roleName;
  formData.roleCode = record.roleCode;
  formData.sort = record.orderNum || 0;
  formData.dataScope = record.dataScope;
  formData.status = record.status;
  formData.remark = record.remark || '';
  formVisible.value = true;
}

/** 提交表单 */
async function handleFormSubmit() {
  formSubmitting.value = true;
  try {
    const data = {
      roleName: formData.roleName,
      roleCode: formData.roleCode,
      orderNum: formData.sort,
      dataScope: formData.dataScope,
      status: formData.status,
      remark: formData.remark,
    };
    if (formMode.value === 'create') {
      await createRole(data);
      message.success('角色创建成功');
    } else {
      await updateRole(editingId.value!, data);
      message.success('角色更新成功');
    }
    formVisible.value = false;
    loadData();
  } catch (error) {
    console.error('保存角色失败:', error);
  } finally {
    formSubmitting.value = false;
  }
}

// ============ 菜单权限分配弹窗 ============

/** 菜单权限弹窗可见性 */
const menuVisible = ref(false);

/** 菜单权限角色ID */
const menuRoleId = ref<number>();

/** 菜单权限角色名称 */
const menuRoleName = ref('');

/** 菜单树数据 */
const menuTree = ref<MenuRecord[]>([]);

/** 已选中的菜单ID */
const checkedMenuIds = ref<number[]>([]);

/** 菜单权限提交中 */
const menuSubmitting = ref(false);

/** 加载菜单树 */
async function loadMenuTree() {
  try {
    menuTree.value = await getMenuTree();
  } catch (error) {
    console.error('加载菜单树失败:', error);
  }
}

/** 转换菜单树为 Tree 组件格式 */
function transformMenuTree(menus: MenuRecord[]): TreeProps['treeData'] {
  return menus.map((menu) => ({
    key: menu.id,
    title: menu.menuName,
    children: menu.children ? transformMenuTree(menu.children) : undefined,
  }));
}

/** 计算菜单树数据 */
const menuTreeData = computed(() => transformMenuTree(menuTree.value));

/** 打开菜单权限弹窗 */
async function handleAssignMenu(record: RoleRecord) {
  menuRoleId.value = record.id;
  menuRoleName.value = record.roleName;
  // 从详情接口获取完整的角色信息（包含 menuIds）
  try {
    const roleDetail = await getRoleDetail(record.id);
    checkedMenuIds.value = roleDetail.menuIds || [];
  } catch (error) {
    console.error('获取角色详情失败:', error);
    checkedMenuIds.value = [];
  }
  menuVisible.value = true;
}

/** 菜单树选中事件 */
function handleMenuCheck(checked: number[] | { checked: number[]; halfChecked: number[] }) {
  if (Array.isArray(checked)) {
    checkedMenuIds.value = checked;
  } else {
    checkedMenuIds.value = checked.checked;
  }
}

/** 提交菜单权限 */
async function handleMenuSubmit() {
  menuSubmitting.value = true;
  try {
    await assignRoleMenus(menuRoleId.value!, checkedMenuIds.value);
    message.success('菜单权限分配成功');
    menuVisible.value = false;
    loadData();
  } catch (error) {
    console.error('分配菜单权限失败:', error);
  } finally {
    menuSubmitting.value = false;
  }
}

// ============ 数据权限设置弹窗 ============

/** 数据权限弹窗可见性 */
const dataScopeVisible = ref(false);

/** 数据权限角色ID */
const dataScopeRoleId = ref<number>();

/** 数据权限角色名称 */
const dataScopeRoleName = ref('');

/** 数据权限类型 */
const dataScopeType = ref(1);

/** 部门树数据 */
const deptTree = ref<DeptRecord[]>([]);

/** 已选中的部门ID */
const checkedDeptIds = ref<number[]>([]);

/** 数据权限提交中 */
const dataScopeSubmitting = ref(false);

/** 加载部门树 */
async function loadDeptTree() {
  try {
    deptTree.value = await getDeptTree();
  } catch (error) {
    console.error('加载部门树失败:', error);
  }
}

/** 转换部门树为 Tree 组件格式 */
function transformDeptTree(depts: DeptRecord[]): TreeProps['treeData'] {
  return depts.map((dept) => ({
    key: dept.id,
    title: dept.deptName,
    children: dept.children ? transformDeptTree(dept.children) : undefined,
  }));
}

/** 计算部门树数据 */
const deptTreeData = computed(() => transformDeptTree(deptTree.value));

/** 打开数据权限弹窗 */
async function handleSetDataScope(record: RoleRecord) {
  dataScopeRoleId.value = record.id;
  dataScopeRoleName.value = record.roleName;
  dataScopeType.value = record.dataScope;
  // 从详情接口获取完整的角色信息（包含 deptIds）
  try {
    const roleDetail = await getRoleDetail(record.id);
    checkedDeptIds.value = roleDetail.deptIds || [];
  } catch (error) {
    console.error('获取角色详情失败:', error);
    checkedDeptIds.value = [];
  }
  dataScopeVisible.value = true;
}

/** 部门树选中事件 */
function handleDeptCheck(checked: number[] | { checked: number[]; halfChecked: number[] }) {
  if (Array.isArray(checked)) {
    checkedDeptIds.value = checked;
  } else {
    checkedDeptIds.value = checked.checked;
  }
}

/** 提交数据权限 */
async function handleDataScopeSubmit() {
  dataScopeSubmitting.value = true;
  try {
    const deptIds = dataScopeType.value === 5 ? checkedDeptIds.value : undefined;
    await setRoleDataScope(dataScopeRoleId.value!, dataScopeType.value, deptIds);
    message.success('数据权限设置成功');
    dataScopeVisible.value = false;
    loadData();
  } catch (error) {
    console.error('设置数据权限失败:', error);
  } finally {
    dataScopeSubmitting.value = false;
  }
}

// ============ 生命周期 ============

onMounted(() => {
  loadData();
  loadMenuTree();
  loadDeptTree();
});
</script>

<template>
  <div class="p-4">
    <!-- 搜索区域 -->
    <Card class="mb-4">
      <Form layout="inline" :model="searchForm">
        <FormItem label="角色名称">
          <Input
            v-model:value="searchForm.roleName"
            placeholder="请输入角色名称"
            allow-clear
            @pressEnter="handleSearch"
          />
        </FormItem>
        <FormItem label="角色编码">
          <Input
            v-model:value="searchForm.roleCode"
            placeholder="请输入角色编码"
            allow-clear
            @pressEnter="handleSearch"
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
            新增角色
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
              <Button type="link" size="small" @click="handleAssignMenu(record)">
                <SafetyCertificateOutlined />
                菜单权限
              </Button>
              <Button type="link" size="small" @click="handleSetDataScope(record)">
                <ApartmentOutlined />
                数据权限
              </Button>
              <Popconfirm
                title="确定要删除该角色吗？"
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

    <!-- 角色表单弹窗 -->
    <Modal
      v-model:open="formVisible"
      :title="formMode === 'create' ? '新增角色' : '编辑角色'"
      :confirm-loading="formSubmitting"
      @ok="handleFormSubmit"
    >
      <Form :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <FormItem label="角色名称" required>
          <Input
            v-model:value="formData.roleName"
            placeholder="请输入角色名称"
          />
        </FormItem>
        <FormItem label="角色编码" required>
          <Input
            v-model:value="formData.roleCode"
            placeholder="请输入角色编码"
            :disabled="formMode === 'edit'"
          />
        </FormItem>
        <FormItem label="排序">
          <InputNumber
            v-model:value="formData.sort"
            :min="0"
            :max="9999"
            style="width: 100%"
          />
        </FormItem>
        <FormItem label="状态">
          <Select v-model:value="formData.status">
            <SelectOption :value="1">启用</SelectOption>
            <SelectOption :value="0">禁用</SelectOption>
          </Select>
        </FormItem>
        <FormItem label="备注">
          <Textarea
            v-model:value="formData.remark"
            placeholder="请输入备注"
            :rows="3"
          />
        </FormItem>
      </Form>
    </Modal>

    <!-- 菜单权限弹窗 -->
    <Modal
      v-model:open="menuVisible"
      title="分配菜单权限"
      :confirm-loading="menuSubmitting"
      width="500px"
      @ok="handleMenuSubmit"
    >
      <div class="mb-4">
        <span class="text-gray-500">角色：</span>
        <Tag color="blue">{{ menuRoleName }}</Tag>
      </div>
      <div class="border rounded p-3 max-h-96 overflow-auto">
        <Tree
          v-model:checkedKeys="checkedMenuIds"
          :tree-data="menuTreeData"
          checkable
          default-expand-all
          @check="handleMenuCheck"
        />
      </div>
    </Modal>

    <!-- 数据权限弹窗 -->
    <Modal
      v-model:open="dataScopeVisible"
      title="设置数据权限"
      :confirm-loading="dataScopeSubmitting"
      width="500px"
      @ok="handleDataScopeSubmit"
    >
      <Form :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <FormItem label="角色">
          <Tag color="blue">{{ dataScopeRoleName }}</Tag>
        </FormItem>
        <FormItem label="权限范围" required>
          <Select v-model:value="dataScopeType" style="width: 100%">
            <SelectOption
              v-for="option in dataScopeOptions"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </SelectOption>
          </Select>
        </FormItem>
        <FormItem v-if="dataScopeType === 5" label="选择部门">
          <div class="border rounded p-3 max-h-64 overflow-auto">
            <Tree
              v-model:checkedKeys="checkedDeptIds"
              :tree-data="deptTreeData"
              checkable
              default-expand-all
              @check="handleDeptCheck"
            />
          </div>
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>
