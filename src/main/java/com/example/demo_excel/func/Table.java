package com.example.demo_excel.func;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private String tableName;
    private List<TableEntity> list = new ArrayList<>();

    private List<String> commentList = new ArrayList<>();
    private List<String> defaultList = new ArrayList<>();
    private String pk;
    private String baseCreate;


    public String getBaseCreate() {
        return baseCreate;
    }

    public void setBaseCreate(String baseCreate) {
        this.baseCreate = baseCreate;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<TableEntity> getList() {
        return list;
    }

    public void setList(List<TableEntity> list) {
        this.list = list;
    }

    public List<String> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<String> commentList) {
        this.commentList = commentList;
    }

    public List<String> getDefaultList() {
        return defaultList;
    }

    public void setDefaultList(List<String> defaultList) {
        this.defaultList = defaultList;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }
}
