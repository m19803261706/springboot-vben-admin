-- V3__update_admin_account.sql
-- 作者: CX
-- 日期: 2026-01-13
-- 描述: 更新管理员账号为 taichu/tcxj888

UPDATE sys_user
SET username = 'taichu',
    password = '$2a$10$Fa60Zk724rUjtvc5/jAlXewRewSCBytzdZMzRY3Tu0kF2Lv0O/RwC',
    real_name = '太初管理员'
WHERE id = 1;
