package com.example.demo_excel.run;

import com.example.demo_excel.func.ExcelUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 一、功能描述：实现将excel文件的数据设计表格生成创建数据表语句
 *
 * 二、开始之前你必须做确保您的excel表格正确，例如：
 *    1.列数必须是8列，分别为【字段描述、字段名、数据类型、P_K、F_K、M、D、备注】
 *    2.默认值必须全部为整形数字
 *    3.每个sheet第一行的括号必须是中文括号
 *    4.不处理外键，外键需要您手动添加
 *
 * 三、此功能做了一些基本校验：
 *    1.为了开发人员理解，我们认为字段名称应该有字段描述的首字母拼接而成，如果不匹配，则会打印出不匹配的提示信息，方便您修改；如果您不在乎也可以忽略提示
 *
 * 四、可以参照项目根目录下面的excel格式
 */
public class CreateTable {
    public static void main(String[] args) {
        //您的excel表的位置
        String FILE_NAME = "C:\\Users\\15790\\Desktop\\药房药库数据表梳理\\云HIS2.0药库系统设计概要1.0_OK.xlsx";
        //您要将数据表创建语句导出到哪个文件
        String TO_FILE_NAME = "C:\\Users\\15790\\Desktop\\药房药库数据表梳理\\print.sql";
         //这里是您要导入的数据表所在的sheet名字
        String[] strs=("sheet1,sheet2").split(",");

        ExcelUtil.run(FILE_NAME, TO_FILE_NAME, Arrays.asList(strs));
    }
}
