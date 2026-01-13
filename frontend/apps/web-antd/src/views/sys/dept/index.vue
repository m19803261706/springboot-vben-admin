<script lang="ts" setup>
/**
 * 部门管理页面
 * 提供部门的树形展示、增删改查功能
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
  message,
} from 'ant-design-vue';
import {
  PlusOutlined,
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined,
  ApartmentOutlined,
} from '@ant-design/icons-vue';
import type { TableColumnsType } from 'ant-design-vue';
import {
  getDeptTree,
  createDept,
  updateDept,
  deleteDept,
  type DeptRecord,
} from '#/api/sys/dept';

defineOptions({ name: 'DeptManagement' });

// ============ 表格相关 ============

/** 加载状态 */
const loading = ref(false);

/** 部门树数据 */
const deptTree = ref<DeptRecord[]>([]);

/** 展开的行 */
const expandedRowKeys = ref<number[]>([]);

/** 表格列配置 */
const columns: TableColumnsType = [
  { title: '部门名称', dataIndex: 'deptName', width: 250 },
  { title: '排序', dataIndex: 'orderNum', width: 100 },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
    customRender: ({ record }) =>
      h(Tag, { color: record.status === 1 ? 'success' : 'error' }, () =>
        record.status === 1 ? '启用' : '禁用'
      ),
  },
  { title: '创建时间', dataIndex: 'createdAt', width: 180 },
  {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right',
  },
];

/** 加载部门树数据 */
async function loadData() {
  loading.value = true;
  try {
    deptTree.value = await getDeptTree();
    // 默认展开所有
    expandedRowKeys.value = getAllDeptIds(deptTree.value);
  } catch (error) {
    console.error('加载部门树失败:', error);
  } finally {
    loading.value = false;
  }
}

/** 获取所有部门ID（用于展开） */
function getAllDeptIds(depts: DeptRecord[]): number[] {
  const ids: number[] = [];
  function traverse(list: DeptRecord[]) {
    for (const dept of list) {
      ids.push(dept.id);
      if (dept.children?.length) {
        traverse(dept.children);
      }
    }
  }
  traverse(depts);
  return ids;
}

/** 删除部门 */
async function handleDelete(id: number) {
  try {
    await deleteDept(id);
    message.success('删除成功');
    loadData();
  } catch (error) {
    console.error('删除部门失败:', error);
  }
}

// ============ 部门选择树 (用于表单) ============

/** 转换为 TreeSelect 数据格式 */
function transformToTreeSelect(depts: DeptRecord[]): any[] {
  return depts.map((dept) => ({
    value: dept.id,
    title: dept.deptName,
    children: dept.children ? transformToTreeSelect(dept.children) : undefined,
  }));
}

/** 计算父级部门选项 */
const parentDeptOptions = computed(() => {
  return [
    { value: 0, title: '顶级部门' },
    ...transformToTreeSelect(deptTree.value),
  ];
});

// ============ 部门表单弹窗 ============

/** 表单弹窗可见性 */
const formVisible = ref(false);

/** 表单模式 */
const formMode = ref<'create' | 'edit'>('create');

/** 编辑中的部门ID */
const editingId = ref<number>();

/** 表单数据 */
const formData = reactive({
  parentId: 0,
  deptName: '',
  orderNum: 0,
  status: 1,
});

/** 表单提交中 */
const formSubmitting = ref(false);

/** 重置表单 */
function resetForm() {
  formData.parentId = 0;
  formData.deptName = '';
  formData.orderNum = 0;
  formData.status = 1;
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
function handleEdit(record: DeptRecord) {
  formMode.value = 'edit';
  editingId.value = record.id;
  formData.parentId = record.parentId;
  formData.deptName = record.deptName;
  formData.orderNum = record.orderNum;
  formData.status = record.status;
  formVisible.value = true;
}

/** 提交表单 */
async function handleFormSubmit() {
  if (!formData.deptName.trim()) {
    message.warning('请输入部门名称');
    return;
  }
  formSubmitting.value = true;
  try {
    if (formMode.value === 'create') {
      await createDept(formData);
      message.success('部门创建成功');
    } else {
      await updateDept(editingId.value!, formData);
      message.success('部门更新成功');
    }
    formVisible.value = false;
    loadData();
  } catch (error) {
    console.error('保存部门失败:', error);
  } finally {
    formSubmitting.value = false;
  }
}

/** 添加子部门 */
function handleAddChild(record: DeptRecord) {
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
          新增部门
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
        :data-source="deptTree"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 900 }"
        v-model:expandedRowKeys="expandedRowKeys"
        row-key="id"
        :indent-size="20"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'deptName'">
            <Space>
              <ApartmentOutlined class="text-blue-500" />
              {{ record.deptName }}
            </Space>
          </template>
          <template v-if="column.key === 'action'">
            <Space>
              <Button type="link" size="small" @click="handleAddChild(record)">
                <PlusOutlined />
                添加
              </Button>
              <Button type="link" size="small" @click="handleEdit(record)">
                <EditOutlined />
                编辑
              </Button>
              <Popconfirm
                title="确定要删除该部门吗？如有子部门或关联用户将无法删除！"
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

    <!-- 部门表单弹窗 -->
    <Modal
      v-model:open="formVisible"
      :title="formMode === 'create' ? '新增部门' : '编辑部门'"
      :confirm-loading="formSubmitting"
      width="500px"
      @ok="handleFormSubmit"
    >
      <Form :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <!-- 父级部门 -->
        <FormItem label="父级部门">
          <TreeSelect
            v-model:value="formData.parentId"
            :tree-data="parentDeptOptions"
            placeholder="请选择父级部门"
            tree-default-expand-all
            :field-names="{ label: 'title', value: 'value', children: 'children' }"
          />
        </FormItem>

        <!-- 部门名称 -->
        <FormItem label="部门名称" required>
          <Input
            v-model:value="formData.deptName"
            placeholder="请输入部门名称"
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
