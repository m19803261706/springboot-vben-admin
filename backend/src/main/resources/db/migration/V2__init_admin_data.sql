-- V2__init_admin_data.sql
-- 作者: CX
-- 日期: 2026-01-13
-- 描述: 初始化管理员账号和基础数据

-- =============================================
-- 初始化部门数据
-- =============================================
INSERT INTO sys_dept (id, parent_id, dept_name, leader, sort, status) VALUES
(1, 0, '总公司', '管理员', 0, 1),
(2, 1, '研发部', NULL, 1, 1),
(3, 1, '运营部', NULL, 2, 1),
(4, 1, '财务部', NULL, 3, 1);

-- =============================================
-- 初始化角色数据
-- =============================================
INSERT INTO sys_role (id, role_name, role_code, data_scope, sort, status, remark) VALUES
(1, '超级管理员', 'admin', 1, 0, 1, '拥有所有权限'),
(2, '普通用户', 'user', 4, 1, 1, '普通用户角色');

-- =============================================
-- 初始化用户数据 (密码: admin123 使用 BCrypt 加密)
-- =============================================
INSERT INTO sys_user (id, username, password, real_name, phone, dept_id, status) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', '13800138000', 1, 1);

-- =============================================
-- 初始化用户角色关联
-- =============================================
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1);

-- =============================================
-- 初始化菜单数据
-- =============================================
-- 系统管理目录
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, permission, icon, sort, visible, status) VALUES
(1, 0, '系统管理', 0, '/sys', NULL, NULL, 'ant-design:setting-outlined', 0, 1, 1);

-- 用户管理菜单
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, permission, icon, sort, visible, status) VALUES
(2, 1, '用户管理', 1, '/sys/user', '/sys/user/index', 'sys:user:list', 'ant-design:user-outlined', 1, 1, 1),
(3, 2, '用户新增', 2, NULL, NULL, 'sys:user:add', NULL, 1, 1, 1),
(4, 2, '用户编辑', 2, NULL, NULL, 'sys:user:edit', NULL, 2, 1, 1),
(5, 2, '用户删除', 2, NULL, NULL, 'sys:user:delete', NULL, 3, 1, 1),
(6, 2, '重置密码', 2, NULL, NULL, 'sys:user:reset-pwd', NULL, 4, 1, 1);

-- 角色管理菜单
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, permission, icon, sort, visible, status) VALUES
(7, 1, '角色管理', 1, '/sys/role', '/sys/role/index', 'sys:role:list', 'ant-design:team-outlined', 2, 1, 1),
(8, 7, '角色新增', 2, NULL, NULL, 'sys:role:add', NULL, 1, 1, 1),
(9, 7, '角色编辑', 2, NULL, NULL, 'sys:role:edit', NULL, 2, 1, 1),
(10, 7, '角色删除', 2, NULL, NULL, 'sys:role:delete', NULL, 3, 1, 1),
(11, 7, '分配权限', 2, NULL, NULL, 'sys:role:assign', NULL, 4, 1, 1);

-- 菜单管理菜单
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, permission, icon, sort, visible, status) VALUES
(12, 1, '菜单管理', 1, '/sys/menu', '/sys/menu/index', 'sys:menu:list', 'ant-design:menu-outlined', 3, 1, 1),
(13, 12, '菜单新增', 2, NULL, NULL, 'sys:menu:add', NULL, 1, 1, 1),
(14, 12, '菜单编辑', 2, NULL, NULL, 'sys:menu:edit', NULL, 2, 1, 1),
(15, 12, '菜单删除', 2, NULL, NULL, 'sys:menu:delete', NULL, 3, 1, 1);

-- 部门管理菜单
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, permission, icon, sort, visible, status) VALUES
(16, 1, '部门管理', 1, '/sys/dept', '/sys/dept/index', 'sys:dept:list', 'ant-design:apartment-outlined', 4, 1, 1),
(17, 16, '部门新增', 2, NULL, NULL, 'sys:dept:add', NULL, 1, 1, 1),
(18, 16, '部门编辑', 2, NULL, NULL, 'sys:dept:edit', NULL, 2, 1, 1),
(19, 16, '部门删除', 2, NULL, NULL, 'sys:dept:delete', NULL, 3, 1, 1);

-- =============================================
-- 为超级管理员角色分配所有菜单权限
-- =============================================
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
(1, 7), (1, 8), (1, 9), (1, 10), (1, 11),
(1, 12), (1, 13), (1, 14), (1, 15),
(1, 16), (1, 17), (1, 18), (1, 19);

-- 为普通用户角色分配部分菜单权限 (只能查看)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, 1), (2, 2);
