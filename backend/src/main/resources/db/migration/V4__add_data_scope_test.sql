-- V4__add_data_scope_test.sql
-- 作者: CX
-- 日期: 2026-01-16
-- 描述: 添加数据权限测试模块 (测试表、菜单权限、测试数据)

-- =============================================
-- 1. 创建数据权限测试表
-- =============================================
CREATE TABLE test_data_scope (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content VARCHAR(500) COMMENT '内容',
    dept_id BIGINT COMMENT '所属部门ID',
    dept_name VARCHAR(50) COMMENT '所属部门名称',
    create_by BIGINT COMMENT '创建人ID',
    create_by_name VARCHAR(50) COMMENT '创建人姓名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据权限测试表';

-- =============================================
-- 2. 添加更多部门 (用于测试层级数据权限)
-- =============================================
INSERT INTO sys_dept (id, parent_id, dept_name, leader, sort, status) VALUES
(5, 2, '前端组', NULL, 1, 1),
(6, 2, '后端组', NULL, 2, 1),
(7, 3, '市场组', NULL, 1, 1),
(8, 3, '客服组', NULL, 2, 1);

-- =============================================
-- 3. 添加测试用户 (不同部门)
-- 密码统一为: test123 (BCrypt加密)
-- =============================================
INSERT INTO sys_user (id, username, password, real_name, phone, dept_id, status) VALUES
(3, 'zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt.2Xr6', '张三', '13800000001', 2, 1),
(4, 'lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt.2Xr6', '李四', '13800000002', 5, 1),
(5, 'wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt.2Xr6', '王五', '13800000003', 3, 1),
(6, 'zhaoliu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt.2Xr6', '赵六', '13800000004', 7, 1),
(7, 'sunqi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt.2Xr6', '孙七', '13800000005', 4, 1);

-- =============================================
-- 4. 创建测试角色 (不同数据权限范围)
-- =============================================
INSERT INTO sys_role (id, role_name, role_code, data_scope, sort, status, remark) VALUES
(3, '全部数据', 'test_all', 1, 10, 1, '测试: 可查看全部数据'),
(4, '本部门数据', 'test_dept', 2, 11, 1, '测试: 只能查看本部门数据'),
(5, '本部门及下级', 'test_dept_child', 3, 12, 1, '测试: 可查看本部门及下级部门数据'),
(6, '仅本人数据', 'test_self', 4, 13, 1, '测试: 只能查看自己创建的数据'),
(7, '自定义部门', 'test_custom', 5, 14, 1, '测试: 可查看自定义部门数据');

-- 为自定义部门角色指定可访问的部门 (研发部和财务部)
INSERT INTO sys_role_dept (role_id, dept_id) VALUES
(7, 2), (7, 4);

-- =============================================
-- 5. 为测试用户分配角色
-- =============================================
INSERT INTO sys_user_role (user_id, role_id) VALUES
(3, 3),  -- 张三: 全部数据
(4, 4),  -- 李四: 本部门数据
(5, 5),  -- 王五: 本部门及下级
(6, 6),  -- 赵六: 仅本人数据
(7, 7);  -- 孙七: 自定义部门

-- =============================================
-- 6. 添加权限测试菜单
-- =============================================
-- 获取当前最大菜单ID
SET @max_menu_id = (SELECT COALESCE(MAX(id), 100) FROM sys_menu);

-- 权限测试目录
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, permission, icon, sort, visible, status) VALUES
(@max_menu_id + 1, 0, '权限测试', 0, '/test', NULL, NULL, 'ant-design:experiment-outlined', 99, 1, 1);

-- 数据权限测试菜单
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, permission, icon, sort, visible, status) VALUES
(@max_menu_id + 2, @max_menu_id + 1, '数据权限测试', 1, '/test/data-scope', '/test/data-scope/index', 'test:dataScope:list', 'ant-design:database-outlined', 1, 1, 1),
(@max_menu_id + 3, @max_menu_id + 2, '新增', 2, NULL, NULL, 'test:dataScope:add', NULL, 1, 1, 1),
(@max_menu_id + 4, @max_menu_id + 2, '编辑', 2, NULL, NULL, 'test:dataScope:edit', NULL, 2, 1, 1),
(@max_menu_id + 5, @max_menu_id + 2, '删除', 2, NULL, NULL, 'test:dataScope:delete', NULL, 3, 1, 1);

-- =============================================
-- 7. 为超级管理员分配新菜单权限
-- =============================================
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id > @max_menu_id;

-- 为所有测试角色分配权限测试菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 3, id FROM sys_menu WHERE id > @max_menu_id;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 4, id FROM sys_menu WHERE id > @max_menu_id;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 5, id FROM sys_menu WHERE id > @max_menu_id;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 6, id FROM sys_menu WHERE id > @max_menu_id;
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 7, id FROM sys_menu WHERE id > @max_menu_id;

-- =============================================
-- 8. 插入测试数据 (不同部门、不同创建人)
-- =============================================
INSERT INTO test_data_scope (title, content, dept_id, dept_name, create_by, create_by_name) VALUES
-- 总公司数据 (admin创建)
('总公司公告', '这是总公司发布的公告内容', 1, '总公司', 1, '超级管理员'),
('年度计划', '公司年度发展计划', 1, '总公司', 1, '超级管理员'),

-- 研发部数据 (张三创建)
('研发部周报', '本周研发进度汇报', 2, '研发部', 3, '张三'),
('技术方案评审', '新项目技术方案讨论', 2, '研发部', 3, '张三'),
('代码规范更新', '更新了代码规范文档', 2, '研发部', 1, '超级管理员'),

-- 前端组数据 (李四创建)
('前端框架升级', 'Vue3升级计划', 5, '前端组', 4, '李四'),
('UI组件库开发', '自研组件库进度', 5, '前端组', 4, '李四'),

-- 后端组数据
('后端架构优化', '微服务架构升级', 6, '后端组', 1, '超级管理员'),

-- 运营部数据 (王五创建)
('运营部月报', '本月运营数据汇总', 3, '运营部', 5, '王五'),
('活动策划方案', '618活动策划', 3, '运营部', 5, '王五'),

-- 市场组数据 (赵六创建)
('市场推广计划', 'Q2市场推广方案', 7, '市场组', 6, '赵六'),
('竞品分析报告', '主要竞品分析', 7, '市场组', 6, '赵六'),

-- 客服组数据
('客服培训资料', '新员工培训文档', 8, '客服组', 1, '超级管理员'),

-- 财务部数据 (孙七创建)
('财务报表', 'Q1财务报表', 4, '财务部', 7, '孙七'),
('预算申请', '研发部门预算申请', 4, '财务部', 7, '孙七'),
('报销审核', '本月报销审核汇总', 4, '财务部', 1, '超级管理员');
