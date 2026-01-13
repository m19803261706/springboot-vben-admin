<script lang="ts" setup>
/**
 * 菜单管理页面
 * 提供菜单的树形展示、增删改查功能
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
  TreeSelect,
  Radio,
  RadioGroup,
  Switch,
  message,
} from 'ant-design-vue';
import {
  PlusOutlined,
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined,
  FolderOutlined,
  MenuOutlined,
  ControlOutlined,
} from '@ant-design/icons-vue';
import { IconifyIcon } from '@vben/icons';
import type { TableColumnsType } from 'ant-design-vue';
import {
  getMenuTree,
  createMenu,
  updateMenu,
  deleteMenu,
  MenuType,
  type MenuRecord,
  type MenuFormData,
} from '#/api/sys/menu';

defineOptions({ name: 'MenuManagement' });

// ============ 常用图标列表 ============

/** 常用图标选项 - Ant Design 图标 */
const iconOptions = [
  // 系统管理相关
  'ant-design:setting-outlined',
  'ant-design:user-outlined',
  'ant-design:team-outlined',
  'ant-design:menu-outlined',
  'ant-design:apartment-outlined',
  'ant-design:safety-outlined',
  'ant-design:lock-outlined',
  'ant-design:key-outlined',
  // 导航和布局
  'ant-design:home-outlined',
  'ant-design:dashboard-outlined',
  'ant-design:appstore-outlined',
  'ant-design:layout-outlined',
  'ant-design:menu-fold-outlined',
  'ant-design:menu-unfold-outlined',
  // 文件和文档
  'ant-design:file-outlined',
  'ant-design:file-text-outlined',
  'ant-design:folder-outlined',
  'ant-design:folder-open-outlined',
  'ant-design:copy-outlined',
  // 数据和图表
  'ant-design:database-outlined',
  'ant-design:table-outlined',
  'ant-design:bar-chart-outlined',
  'ant-design:line-chart-outlined',
  'ant-design:pie-chart-outlined',
  'ant-design:area-chart-outlined',
  // 通信
  'ant-design:mail-outlined',
  'ant-design:message-outlined',
  'ant-design:notification-outlined',
  'ant-design:bell-outlined',
  'ant-design:comment-outlined',
  // 媒体
  'ant-design:picture-outlined',
  'ant-design:video-camera-outlined',
  'ant-design:sound-outlined',
  'ant-design:camera-outlined',
  // 商业
  'ant-design:shop-outlined',
  'ant-design:shopping-cart-outlined',
  'ant-design:money-collect-outlined',
  'ant-design:wallet-outlined',
  'ant-design:credit-card-outlined',
  // 工具
  'ant-design:tool-outlined',
  'ant-design:thunderbolt-outlined',
  'ant-design:api-outlined',
  'ant-design:code-outlined',
  'ant-design:cloud-outlined',
  'ant-design:cloud-server-outlined',
  'ant-design:desktop-outlined',
  'ant-design:mobile-outlined',
  // 状态和操作
  'ant-design:search-outlined',
  'ant-design:edit-outlined',
  'ant-design:delete-outlined',
  'ant-design:plus-outlined',
  'ant-design:minus-outlined',
  'ant-design:check-outlined',
  'ant-design:close-outlined',
  'ant-design:info-circle-outlined',
  'ant-design:exclamation-circle-outlined',
  'ant-design:question-circle-outlined',
  // 其他常用
  'ant-design:calendar-outlined',
  'ant-design:clock-circle-outlined',
  'ant-design:environment-outlined',
  'ant-design:global-outlined',
  'ant-design:link-outlined',
  'ant-design:star-outlined',
  'ant-design:heart-outlined',
  'ant-design:eye-outlined',
  'ant-design:download-outlined',
  'ant-design:upload-outlined',
  'ant-design:export-outlined',
  'ant-design:import-outlined',
  'ant-design:filter-outlined',
  'ant-design:sort-ascending-outlined',
  'ant-design:sort-descending-outlined',
];

// ============ 菜单类型相关 ============

/** 菜单类型选项 */
const menuTypeOptions = [
  { value: MenuType.DIRECTORY, label: '目录', icon: FolderOutlined, color: 'blue' },
  { value: MenuType.MENU, label: '菜单', icon: MenuOutlined, color: 'green' },
  { value: MenuType.BUTTON, label: '按钮', icon: ControlOutlined, color: 'orange' },
];

/** 获取菜单类型标签 */
function getMenuTypeTag(type: MenuType) {
  const option = menuTypeOptions.find((o) => o.value === type);
  if (!option) return null;
  return h(
    Tag,
    { color: option.color },
    { default: () => [h(option.icon), ' ', option.label] }
  );
}

// ============ 表格相关 ============

/** 加载状态 */
const loading = ref(false);

/** 菜单树数据 */
const menuTree = ref<MenuRecord[]>([]);

/** 展开的行 */
const expandedRowKeys = ref<number[]>([]);

/** 渲染图标 */
function renderIcon(icon: string) {
  if (!icon) return h('span', { class: 'text-gray-400' }, '-');
  return h(IconifyIcon, { icon, class: 'text-xl' });
}

/** 表格列配置 */
const columns: TableColumnsType = [
  { title: '菜单名称', dataIndex: 'menuName', width: 180 },
  {
    title: '图标',
    dataIndex: 'icon',
    width: 70,
    align: 'center',
    customRender: ({ record }) => renderIcon(record.icon),
  },
  {
    title: '类型',
    dataIndex: 'menuType',
    width: 90,
    align: 'center',
    customRender: ({ record }) => getMenuTypeTag(record.menuType),
  },
  { title: '排序', dataIndex: 'orderNum', width: 70, align: 'center' },
  { title: '路由路径', dataIndex: 'path', width: 140, ellipsis: true },
  { title: '组件路径', dataIndex: 'component', width: 160, ellipsis: true },
  { title: '权限标识', dataIndex: 'permission', width: 140, ellipsis: true },
  {
    title: '操作',
    key: 'action',
    width: 180,
    fixed: 'right',
    align: 'center',
  },
];

/** 加载菜单树数据 */
async function loadData() {
  loading.value = true;
  try {
    menuTree.value = await getMenuTree();
    // 默认展开第一层
    expandedRowKeys.value = menuTree.value.map((m) => m.id);
  } catch (error) {
    console.error('加载菜单树失败:', error);
  } finally {
    loading.value = false;
  }
}

/** 删除菜单 */
async function handleDelete(id: number) {
  try {
    await deleteMenu(id);
    message.success('删除成功');
    loadData();
  } catch (error) {
    console.error('删除菜单失败:', error);
  }
}

// ============ 菜单选择树 (用于表单) ============

/** 转换为 TreeSelect 数据格式 */
function transformToTreeSelect(menus: MenuRecord[]): any[] {
  return menus
    .filter((m) => m.menuType !== MenuType.BUTTON) // 按钮不能作为父级
    .map((menu) => ({
      value: menu.id,
      title: menu.menuName,
      children: menu.children ? transformToTreeSelect(menu.children) : undefined,
    }));
}

/** 计算父级菜单选项 */
const parentMenuOptions = computed(() => {
  return [
    { value: 0, title: '根目录' },
    ...transformToTreeSelect(menuTree.value),
  ];
});

// ============ 菜单表单弹窗 ============

/** 表单弹窗可见性 */
const formVisible = ref(false);

/** 表单模式 */
const formMode = ref<'create' | 'edit'>('create');

/** 编辑中的菜单ID */
const editingId = ref<number>();

/** 表单数据 */
const formData = reactive<MenuFormData>({
  parentId: 0,
  menuName: '',
  menuType: MenuType.DIRECTORY,
  path: '',
  component: '',
  permission: '',
  icon: '',
  orderNum: 0,
  status: 1,
  visible: 1,
  keepAlive: 1,
  isFrame: 0,
});

/** 表单提交中 */
const formSubmitting = ref(false);

/** 重置表单 */
function resetForm() {
  formData.parentId = 0;
  formData.menuName = '';
  formData.menuType = MenuType.DIRECTORY;
  formData.path = '';
  formData.component = '';
  formData.permission = '';
  formData.icon = '';
  formData.orderNum = 0;
  formData.status = 1;
  formData.visible = 1;
  formData.keepAlive = 1;
  formData.isFrame = 0;
}

/** 打开新增弹窗 */
function handleCreate(parentId = 0) {
  formMode.value = 'create';
  editingId.value = undefined;
  resetForm();
  formData.parentId = parentId;
  formVisible.value = true;
}

/** 打开编辑弹窗 */
function handleEdit(record: MenuRecord) {
  formMode.value = 'edit';
  editingId.value = record.id;
  formData.parentId = record.parentId;
  formData.menuName = record.menuName;
  formData.menuType = record.menuType;
  formData.path = record.path || '';
  formData.component = record.component || '';
  formData.permission = record.permission || '';
  formData.icon = record.icon || '';
  formData.orderNum = record.orderNum;
  formData.status = record.status;
  formData.visible = record.visible;
  formData.keepAlive = record.keepAlive;
  formData.isFrame = record.isFrame;
  formVisible.value = true;
}

/** 提交表单 */
async function handleFormSubmit() {
  formSubmitting.value = true;
  try {
    if (formMode.value === 'create') {
      await createMenu(formData);
      message.success('菜单创建成功');
    } else {
      await updateMenu(editingId.value!, formData);
      message.success('菜单更新成功');
    }
    formVisible.value = false;
    loadData();
  } catch (error) {
    console.error('保存菜单失败:', error);
  } finally {
    formSubmitting.value = false;
  }
}

/** 添加子菜单 */
function handleAddChild(record: MenuRecord) {
  handleCreate(record.id);
}

// ============ 生命周期 ============

onMounted(() => {
  loadData();
});
</script>

<template>
  <div class="p-4">
    <!-- 操作区域 -->
    <Card class="mb-4">
      <Space>
        <Button type="primary" @click="handleCreate(0)">
          <PlusOutlined />
          新增菜单
        </Button>
        <Button @click="loadData">
          <ReloadOutlined />
          刷新
        </Button>
      </Space>
    </Card>

    <!-- 表格区域 -->
    <Card>
      <Table
        :columns="columns"
        :data-source="menuTree"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 1400 }"
        v-model:expandedRowKeys="expandedRowKeys"
        row-key="id"
        :indent-size="20"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <Space>
              <Button
                v-if="record.menuType !== MenuType.BUTTON"
                type="link"
                size="small"
                @click="handleAddChild(record)"
              >
                <PlusOutlined />
                添加
              </Button>
              <Button type="link" size="small" @click="handleEdit(record)">
                <EditOutlined />
                编辑
              </Button>
              <Popconfirm
                title="确定要删除该菜单吗？删除后子菜单也会被删除！"
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

    <!-- 菜单表单弹窗 -->
    <Modal
      v-model:open="formVisible"
      :title="formMode === 'create' ? '新增菜单' : '编辑菜单'"
      :confirm-loading="formSubmitting"
      width="650px"
      @ok="handleFormSubmit"
    >
      <Form :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <!-- 父级菜单 -->
        <FormItem label="父级菜单">
          <TreeSelect
            v-model:value="formData.parentId"
            :tree-data="parentMenuOptions"
            placeholder="请选择父级菜单"
            tree-default-expand-all
            :field-names="{ label: 'title', value: 'value', children: 'children' }"
          />
        </FormItem>

        <!-- 菜单类型 -->
        <FormItem label="菜单类型" required>
          <RadioGroup v-model:value="formData.menuType">
            <Radio
              v-for="option in menuTypeOptions"
              :key="option.value"
              :value="option.value"
            >
              <component :is="option.icon" class="mr-1" />
              {{ option.label }}
            </Radio>
          </RadioGroup>
        </FormItem>

        <!-- 菜单名称 -->
        <FormItem label="菜单名称" required>
          <Input
            v-model:value="formData.menuName"
            placeholder="请输入菜单名称"
          />
        </FormItem>

        <!-- 图标 (目录和菜单显示) -->
        <FormItem
          v-if="formData.menuType !== MenuType.BUTTON"
          label="菜单图标"
        >
          <Select
            v-model:value="formData.icon"
            placeholder="请选择图标"
            allow-clear
            show-search
            :filter-option="(input: string, option: any) => option.value.toLowerCase().includes(input.toLowerCase())"
          >
            <SelectOption v-for="icon in iconOptions" :key="icon" :value="icon">
              <div class="flex items-center gap-2">
                <IconifyIcon :icon="icon" class="text-lg" />
                <span class="text-xs text-gray-500">{{ icon }}</span>
              </div>
            </SelectOption>
          </Select>
        </FormItem>

        <!-- 路由路径 (目录和菜单显示) -->
        <FormItem
          v-if="formData.menuType !== MenuType.BUTTON"
          label="路由路径"
        >
          <Input
            v-model:value="formData.path"
            placeholder="请输入路由路径，如 /sys/user"
          />
        </FormItem>

        <!-- 组件路径 (菜单显示) -->
        <FormItem v-if="formData.menuType === MenuType.MENU" label="组件路径">
          <Input
            v-model:value="formData.component"
            placeholder="请输入组件路径，如 sys/user/index"
          />
        </FormItem>

        <!-- 权限标识 (菜单和按钮显示) -->
        <FormItem
          v-if="formData.menuType !== MenuType.DIRECTORY"
          label="权限标识"
        >
          <Input
            v-model:value="formData.permission"
            placeholder="请输入权限标识，如 sys:user:list"
          />
        </FormItem>

        <!-- 排序 -->
        <FormItem label="排序">
          <InputNumber
            v-model:value="formData.orderNum"
            :min="0"
            :max="9999"
            style="width: 100%"
          />
        </FormItem>

        <!-- 是否外链 (目录和菜单显示) -->
        <FormItem
          v-if="formData.menuType !== MenuType.BUTTON"
          label="是否外链"
        >
          <RadioGroup v-model:value="formData.isFrame">
            <Radio :value="0">否</Radio>
            <Radio :value="1">是</Radio>
          </RadioGroup>
        </FormItem>

        <!-- 是否缓存 (菜单显示) -->
        <FormItem v-if="formData.menuType === MenuType.MENU" label="是否缓存">
          <RadioGroup v-model:value="formData.keepAlive">
            <Radio :value="1">缓存</Radio>
            <Radio :value="0">不缓存</Radio>
          </RadioGroup>
        </FormItem>

        <!-- 是否可见 (目录和菜单显示) -->
        <FormItem
          v-if="formData.menuType !== MenuType.BUTTON"
          label="是否可见"
        >
          <RadioGroup v-model:value="formData.visible">
            <Radio :value="1">显示</Radio>
            <Radio :value="0">隐藏</Radio>
          </RadioGroup>
        </FormItem>

        <!-- 状态 -->
        <FormItem label="状态">
          <RadioGroup v-model:value="formData.status">
            <Radio :value="1">启用</Radio>
            <Radio :value="0">禁用</Radio>
          </RadioGroup>
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>
