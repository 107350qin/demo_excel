package com.example.demo_excel.func;


public class TableEntity {
    private String desc;//
    private String fieldName;//
    private String fieldType;//
    private Boolean primaryKey;
    private Boolean empty;//
    private String defaultValue;
    private String remark;//

    public TableEntity(String desc, String fieldName, String fieldType, Boolean primaryKey, Boolean empty, String defaultValue, String remark) {
        this.desc = desc;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.primaryKey = primaryKey;
        this.empty = empty;
        this.defaultValue = defaultValue;
        this.remark = remark;
    }

    private TableEntity() {
    }

    public String getDesc() {
        return desc;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public Boolean getPrimaryKey() {
        return primaryKey;
    }

    public Boolean getEmpty() {
        return empty;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getRemark() {
        return remark;
    }

}
