package com.taichu.yingjiguanli.common.datascope;

/**
 * 数据权限类型枚举
 *
 * @author CX
 * @since 2026-01-13
 */
public enum DataScopeType {

    /**
     * 全部数据权限
     */
    DATA_SCOPE_ALL(1, "全部数据"),

    /**
     * 本部门数据权限
     */
    DATA_SCOPE_DEPT(2, "本部门数据"),

    /**
     * 本部门及下级部门数据权限
     */
    DATA_SCOPE_DEPT_AND_CHILD(3, "本部门及下级数据"),

    /**
     * 仅本人数据权限
     */
    DATA_SCOPE_SELF(4, "仅本人数据"),

    /**
     * 自定义数据权限
     */
    DATA_SCOPE_CUSTOM(5, "自定义数据");

    private final int code;
    private final String desc;

    DataScopeType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据code获取枚举
     */
    public static DataScopeType fromCode(int code) {
        for (DataScopeType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return DATA_SCOPE_ALL;
    }
}
