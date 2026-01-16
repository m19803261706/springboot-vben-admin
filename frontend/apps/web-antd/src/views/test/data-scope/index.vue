<script lang="ts" setup>
/**
 * 数据权限测试页面
 * 用于演示和测试不同数据权限范围的效果
 *
 * @author CX
 * @since 2026-01-16
 */
import { ref, reactive, onMounted, h, computed } from 'vue';
import {
  Card,
  Form,
  FormItem,
  Input,
  Button,
  Space,
  Table,
  Tag,
  Popconfirm,
  Modal,
  Alert,
  Descriptions,
  DescriptionsItem,
  message,
  Textarea,
} from 'ant-design-vue';
import {
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined,
  InfoCircleOutlined,
} from '@ant-design/icons-vue';
import type { TableColumnsType, TablePaginationConfig } from 'ant-design-vue';
import {
  getTestDataScopeList,
  createTestDataScope,
  updateTestDataScope,
  deleteTestDataScope,
  type TestDataScopeRecord,
  type TestDataScopeQueryParams,
  type TestDataScopeDTO,
  type DataScopeInfo,
} from '#/api/test/dataScope';

defineOptions({ name: 'DataScopeTest' });

// ============ 数据权限信息 ============

/** 当前用户数据权限信息 */
const dataScopeInfo = ref<DataScopeInfo | null>(null);

/** 数据权限类型对应的标签颜色 */
const dataScopeColors: Record<number, string> = {
  1: 'green',    // 全部数据
  2: 'blue',     // 本部门
  3: 'cyan',     // 本部门及下级
  4: 'orange',   // 仅本人
  5: 'purple',   // 自定义部门
};

/** 数据权限类型描述 */
const dataScopeDescriptions: Record<number, string> = {
  1: '全部数据 - 可以查看系统中所有数据',
  2: '本部门数据 - 只能查看本部门的数据',
  3: '本部门及下级数据 - 可以查看本部门及所有子部门的数据',
  4: '仅本人数据 - 只能查看自己创建的数据',
  5: '自定义部门数据 - 可以查看指定部门的数据',
};

// ============ 搜索相关 ============

/** 搜索表单数据 */
const searchForm = reactive<TestDataScopeQueryParams>({
  title: '',
});

/** 重置搜索表单 */
function handleResetSearch() {
  searchForm.title = '';
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
const dataSource = ref<TestDataScopeRecord[]>([]);

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
  { title: 'ID', dataIndex: 'id', width: 80 },
  { title: '标题', dataIndex: 'title', width: 200 },
  { title: '内容', dataIndex: 'content', width: 250, ellipsis: true },
  {
    title: '所属部门',
    dataIndex: 'deptName',
    width: 120,
    customRender: ({ text, record }) => {
      return h(Tag, { color: 'blue' }, () => text || `部门ID: ${record.deptId}`);
    },
  },
  {
    title: '创建人',
    dataIndex: 'createByName',
    width: 100,
    customRender: ({ text, record }) => {
      return h(Tag, { color: 'green' }, () => text || `用户ID: ${record.createBy}`);
    },
  },
  { title: '创建时间', dataIndex: 'createTime', width: 180 },
  {
    title: '操作',
    key: 'action',
    width: 150,
    fixed: 'right',
  },
];

/** 加载表格数据 */
async function loadData() {
  loading.value = true;
  try {
    const params: TestDataScopeQueryParams = {
      page: pagination.current,
      size: pagination.pageSize,
      ...searchForm,
    };
    const result = await getTestDataScopeList(params);
    dataSource.value = result.content;
    pagination.total = result.total;
    dataScopeInfo.value = result.dataScope;
  } catch (error: any) {
    message.error(error.message || '加载数据失败');
  } finally {
    loading.value = false;
  }
}

/** 表格分页变化 */
function handleTableChange(pag: TablePaginationConfig) {
  pagination.current = pag.current || 1;
  pagination.pageSize = pag.pageSize || 10;
  loadData();
}

// ============ 新增/编辑弹窗 ============

/** 弹窗可见性 */
const modalVisible = ref(false);

/** 弹窗标题 */
const modalTitle = computed(() => (editingId.value ? '编辑数据' : '新增数据'));

/** 编辑中的ID */
const editingId = ref<number | null>(null);

/** 表单数据 */
const formData = reactive<TestDataScopeDTO>({
  title: '',
  content: '',
});

/** 表单提交中 */
const submitting = ref(false);

/** 打开新增弹窗 */
function handleAdd() {
  editingId.value = null;
  formData.title = '';
  formData.content = '';
  modalVisible.value = true;
}

/** 打开编辑弹窗 */
function handleEdit(record: TestDataScopeRecord) {
  editingId.value = record.id;
  formData.title = record.title;
  formData.content = record.content || '';
  modalVisible.value = true;
}

/** 提交表单 */
async function handleSubmit() {
  if (!formData.title.trim()) {
    message.warning('请输入标题');
    return;
  }

  submitting.value = true;
  try {
    if (editingId.value) {
      await updateTestDataScope(editingId.value, formData);
      message.success('更新成功');
    } else {
      await createTestDataScope(formData);
      message.success('创建成功');
    }
    modalVisible.value = false;
    loadData();
  } catch (error: any) {
    message.error(error.message || '操作失败');
  } finally {
    submitting.value = false;
  }
}

/** 删除数据 */
async function handleDelete(record: TestDataScopeRecord) {
  try {
    await deleteTestDataScope(record.id);
    message.success('删除成功');
    loadData();
  } catch (error: any) {
    message.error(error.message || '删除失败');
  }
}

// ============ 初始化 ============

onMounted(() => {
  loadData();
});
</script>

<template>
  <div class="p-4">
    <!-- 数据权限信息展示 -->
    <Card class="mb-4" title="当前用户数据权限" :bordered="false">
      <template #extra>
        <InfoCircleOutlined class="text-blue-500" />
      </template>
      <Alert
        v-if="dataScopeInfo"
        :type="dataScopeInfo.type === 1 ? 'success' : 'info'"
        show-icon
        class="mb-4"
      >
        <template #message>
          <span class="font-bold">{{ dataScopeInfo.desc }}</span>
        </template>
        <template #description>
          {{ dataScopeDescriptions[dataScopeInfo.type] }}
        </template>
      </Alert>

      <Descriptions v-if="dataScopeInfo" :column="4" bordered size="small">
        <DescriptionsItem label="权限类型">
          <Tag :color="dataScopeColors[dataScopeInfo.type]">
            {{ dataScopeInfo.desc }}
          </Tag>
        </DescriptionsItem>
        <DescriptionsItem label="用户ID">
          {{ dataScopeInfo.userId }}
        </DescriptionsItem>
        <DescriptionsItem label="部门ID">
          {{ dataScopeInfo.deptId || '无' }}
        </DescriptionsItem>
        <DescriptionsItem label="自定义部门">
          <template v-if="dataScopeInfo.customDeptIds?.length">
            <Tag v-for="id in dataScopeInfo.customDeptIds" :key="id" color="purple">
              {{ id }}
            </Tag>
          </template>
          <span v-else class="text-gray-400">无</span>
        </DescriptionsItem>
      </Descriptions>
    </Card>

    <!-- 搜索和操作 -->
    <Card class="mb-4" :bordered="false">
      <Form layout="inline" class="mb-4">
        <FormItem label="标题">
          <Input
            v-model:value="searchForm.title"
            placeholder="请输入标题关键词"
            allow-clear
            style="width: 200px"
            @press-enter="handleSearch"
          />
        </FormItem>
        <FormItem>
          <Space>
            <Button type="primary" @click="handleSearch">
              <template #icon><SearchOutlined /></template>
              搜索
            </Button>
            <Button @click="handleResetSearch">
              <template #icon><ReloadOutlined /></template>
              重置
            </Button>
            <Button type="primary" @click="handleAdd">
              <template #icon><PlusOutlined /></template>
              新增
            </Button>
          </Space>
        </FormItem>
      </Form>

      <!-- 数据表格 -->
      <Table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="pagination"
        row-key="id"
        :scroll="{ x: 1100 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <Space>
              <Button type="link" size="small" @click="handleEdit(record)">
                <template #icon><EditOutlined /></template>
                编辑
              </Button>
              <Popconfirm
                title="确定要删除此数据吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDelete(record)"
              >
                <Button type="link" danger size="small">
                  <template #icon><DeleteOutlined /></template>
                  删除
                </Button>
              </Popconfirm>
            </Space>
          </template>
        </template>
      </Table>
    </Card>

    <!-- 新增/编辑弹窗 -->
    <Modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :confirm-loading="submitting"
      @ok="handleSubmit"
    >
      <Form layout="vertical" class="mt-4">
        <FormItem label="标题" required>
          <Input
            v-model:value="formData.title"
            placeholder="请输入标题"
            :maxlength="100"
            show-count
          />
        </FormItem>
        <FormItem label="内容">
          <Textarea
            v-model:value="formData.content"
            placeholder="请输入内容"
            :rows="4"
            :maxlength="500"
            show-count
          />
        </FormItem>
      </Form>
    </Modal>

    <!-- 测试说明 -->
    <Card title="测试说明" :bordered="false">
      <Alert type="warning" show-icon class="mb-4">
        <template #message>测试账号信息</template>
        <template #description>
          <div class="mt-2">
            <p><strong>所有测试账号密码: test123</strong></p>
            <ul class="list-disc list-inside mt-2 space-y-1">
              <li><strong>张三 (zhangsan)</strong> - 全部数据权限 - 可看所有数据</li>
              <li><strong>李四 (lisi)</strong> - 本部门数据权限 - 只能看前端组数据</li>
              <li><strong>王五 (wangwu)</strong> - 本部门及下级权限 - 可看运营部及市场组、客服组数据</li>
              <li><strong>赵六 (zhaoliu)</strong> - 仅本人数据权限 - 只能看自己创建的数据</li>
              <li><strong>孙七 (sunqi)</strong> - 自定义部门权限 - 可看研发部和财务部数据</li>
            </ul>
          </div>
        </template>
      </Alert>

      <Descriptions bordered size="small" :column="1">
        <DescriptionsItem label="测试步骤">
          <ol class="list-decimal list-inside space-y-1">
            <li>使用不同测试账号登录系统</li>
            <li>访问本页面，观察上方显示的数据权限类型</li>
            <li>查看表格中的数据，验证是否符合权限范围</li>
            <li>尝试新增数据，观察数据的部门和创建人自动设置</li>
            <li>尝试编辑/删除数据，验证是否只能操作权限范围内的数据</li>
          </ol>
        </DescriptionsItem>
      </Descriptions>
    </Card>
  </div>
</template>
