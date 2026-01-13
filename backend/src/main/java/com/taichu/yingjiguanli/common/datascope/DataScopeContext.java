package com.taichu.yingjiguanli.common.datascope;

/**
 * 数据权限上下文
 * 使用 ThreadLocal 存储当前请求的数据权限 SQL 片段
 *
 * @author CX
 * @since 2026-01-13
 */
public class DataScopeContext {

    /**
     * 存储数据权限 SQL 条件
     */
    private static final ThreadLocal<String> DATA_SCOPE_SQL = new ThreadLocal<>();

    /**
     * 设置数据权限 SQL
     *
     * @param sql SQL 条件片段
     */
    public static void setDataScopeSql(String sql) {
        DATA_SCOPE_SQL.set(sql);
    }

    /**
     * 获取数据权限 SQL
     *
     * @return SQL 条件片段
     */
    public static String getDataScopeSql() {
        return DATA_SCOPE_SQL.get();
    }

    /**
     * 清除数据权限 SQL
     */
    public static void clear() {
        DATA_SCOPE_SQL.remove();
    }

    /**
     * 是否有数据权限条件
     *
     * @return true 有条件，false 无条件
     */
    public static boolean hasDataScope() {
        String sql = DATA_SCOPE_SQL.get();
        return sql != null && !sql.isEmpty();
    }
}
