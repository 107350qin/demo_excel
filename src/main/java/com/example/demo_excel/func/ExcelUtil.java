package com.example.demo_excel.func;

import java.io.*;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

public class ExcelUtil {
    static LinkedList<String> executeSqlList = new LinkedList<>();

    static String FILE_NAME;
    static String TO_FILE_NAME;
    static List<String> SHEET_NAME_LIST = new ArrayList<>();

    public static void run(String fileName, String toFileName, List<String> sheetNameList) {
        FILE_NAME = fileName;
        TO_FILE_NAME = toFileName;
        ExcelUtil.SHEET_NAME_LIST.addAll(sheetNameList);

        File file = new File(ExcelUtil.FILE_NAME);
        try {
            //读取到的所有数据
            List<Table> list = importExcel(file);
            //先删除相关数据表
            for (Table table : list) {
                executeSqlList.add("drop table " + table.getTableName() + ";");
            }
//            System.out.println(JSON.toJSONString(list));
            for (Table table : list) {
                //每一张表
                String create = "create table " + table.getTableName() + "(\n";
                List<String> commentList = new ArrayList<>();
                List<String> defaultList = new ArrayList<>();
                List<String> pkList = new ArrayList<>();
                for (TableEntity entity : table.getList()) {
                    if (entity.getPrimaryKey()) {
                        pkList.add(entity.getFieldName());
                    }
                    if (entity.getFieldName() == null || "".equals(entity.getFieldName())) {
                        continue;
                    }
                    String comment = "comment on column " + table.getTableName() + "." + entity.getFieldName() + " is '" + entity.getDesc() + ("".equals(entity.getRemark()) ? "" : ("【" + entity.getRemark() + "】")) + "';";
                    commentList.add(comment);
                    if (entity.getDefaultValue() != null && !"".equals(entity.getDefaultValue())) {
                        String defaultValue = "alter table " + table.getTableName() + " modify " + entity.getFieldName() + " default " + entity.getDefaultValue() + ";";
                        defaultList.add(defaultValue);
                    }
                    if (entity.getFieldName() == null || "".equals(entity.getFieldName())) {
                        continue;
                    }
                    create = create + entity.getFieldName() + " " + entity.getFieldType();
                    if (!entity.getEmpty()) {
//                        System.out.println(entity.getFieldName());
                        create = create + " not null";
                    }
                    create = create + ",\n";
                }
                create = create.substring(0, create.length() - 2);
                create += "\n);";

                //基本建表语句
                table.setBaseCreate(create);
                //注释
                table.setCommentList(commentList);
                //默认值
                table.setDefaultList(defaultList);
                //主键
                if (pkList.size() != 0) {
                    String pkStr = "ALTER TABLE " + table.getTableName() + " ADD CONSTRAINT PK_" + table.getTableName() + " PRIMARY KEY (";
                    for (String s : pkList) {
                        pkStr = pkStr + s + ",";
                    }
                    pkStr = pkStr.substring(0, pkStr.length() - 1);
                    pkStr = pkStr + ");";
                    table.setPk(pkStr);
                }
            }
            //转换之后的所有语句
//            System.out.println(JSON.toJSONString(list));

            //执行sql语句
            getSqls(list);

            printToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printToFile() throws Exception {
        File file = new File(TO_FILE_NAME);
        if (file.exists()) {
            file.delete();
            file.createNewFile();
        }
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(TO_FILE_NAME));
//        System.out.println(executeSqlList.toString());

        for (int i = 0; i < executeSqlList.size(); i++) {
            String tmpExecute = executeSqlList.get(i);
            if (tmpExecute == null || tmpExecute.trim().equals("null") || tmpExecute.trim().equals("not null")) {
                continue;
            }
            bufferedOutputStream.write((tmpExecute + "\n").getBytes());
        }
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    public static void getSqls(List<Table> list) {
        for (Table table : list) {
//            System.out.println("table: " + table.getTableName());
            executeSqlList.add(table.getBaseCreate());
            executeSqlList.add(table.getPk());
            executeSqlList.addAll(table.getDefaultList());
            executeSqlList.addAll(table.getCommentList());
        }
    }


    public static List<Table> importExcel(File file) throws Exception {
        Workbook wb = null;
        String fileName = file.getName();// 读取上传文件(excel)的名字，含后缀后
        // 根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
        Iterator<Sheet> sheets = null;
        List<Table> returnlist = new ArrayList<>();
        try {
            if (fileName.endsWith("xls")) {
                wb = new HSSFWorkbook(new FileInputStream(file));
                sheets = wb.iterator();
            } else if (fileName.endsWith("xlsx")) {
                wb = new XSSFWorkbook(new FileInputStream(file));
                sheets = wb.iterator();
            }
            if (sheets == null) {
                throw new Exception("excel中不含有sheet工作表");
            }

            // 遍历excel里每个sheet的数据。
            while (sheets.hasNext()) {
                Sheet sheet = sheets.next();
                if (SHEET_NAME_LIST.contains(sheet.getSheetName())) {

                    checkRowName(sheet);

                    Table table = getCellValue(sheet);
                    returnlist.add(table);
//                    System.out.println(sheet.getSheetName() + " success !");
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (wb != null) wb.close();
        }
        return returnlist;
    }

    private static void checkRowName(Sheet sheet) throws Exception {
        try {
            String desc = sheet.getRow(1).getCell(0).getStringCellValue();
            String name = sheet.getRow(1).getCell(1).getStringCellValue();
            String type = sheet.getRow(1).getCell(2).getStringCellValue();
            String pk = sheet.getRow(1).getCell(3).getStringCellValue();
            String fk = sheet.getRow(1).getCell(4).getStringCellValue();
            String m = sheet.getRow(1).getCell(5).getStringCellValue();
            String d = sheet.getRow(1).getCell(6).getStringCellValue();
            String remark = sheet.getRow(1).getCell(7).getStringCellValue();
            if (StringUtils.hasText(desc) && "字段描述".equals(desc) &&
                    StringUtils.hasText(name) && "字段名".equals(name) &&
                    StringUtils.hasText(type) && "数据类型".equals(type) &&
                    StringUtils.hasText(pk) && "P_K".equals(pk) &&
                    StringUtils.hasText(fk) && "F_K".equals(fk) &&
                    StringUtils.hasText(m) && "M".equals(m) &&
                    StringUtils.hasText(d) && "D".equals(d) &&
                    StringUtils.hasText(remark) && "备注".equals(remark)
            ) {
//                System.out.println("表" + sheet.getSheetName() + "OK");
            } else {
                throw new Exception("校验不正确->设计表缺少列！sheetName=" + sheet.getSheetName());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(sheet.getSheetName());
            throw new Exception(e.getMessage());
        }
    }


    // 获取每一个Sheet工作表中的数。
    private static Table getCellValue(Sheet sheet) throws Exception {
        List<TableEntity> list = new ArrayList<>();
        int s = sheet.getFirstRowNum();

        Row row_first = sheet.getRow(s);
        String allStr = row_first.getCell(0).getStringCellValue();

        String tableName = null;
        try {
            if (!allStr.contains("（") || !allStr.contains("）")) {
                throw new Exception("错误：没有括号");
            }
            tableName = allStr.substring(allStr.indexOf("（") + 1, allStr.lastIndexOf("）"));
        } catch (Exception e) {
            System.out.println("确认括号是否是中文括号！=" + allStr);
            System.out.println(e.getMessage());
        }

        // sheet.getPhysicalNumberOfRows():获取的是物理行数，也就是不包括那些空行（隔行）的情况
        for (int i = s + 2; i < sheet.getLastRowNum() + 1; i++) {
            // 获得第i行对象
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            int j = row.getFirstCellNum();// 获取第i行第一个单元格的下标
            if (j < 0) {
                continue;
            }

            //字段描述
            String desc = row.getCell(j++).getStringCellValue();
            //字段名字
            Cell cell = row.getCell(j++);
            if (cell == null) {
                continue;
            }
            String fieldName = cell.getStringCellValue();
            if (fieldName == null || "".equals(fieldName)) {
                continue;
            }

            //字段类型
            String fieldType = row.getCell(j++).getStringCellValue();

            // todo 校验字段描述的首字母拼接字符串和字段名字是否一致
            if (!ChineseToPinYin.getPinYinHeadChar(desc).toLowerCase().equals(fieldName.toLowerCase())) {
                System.err.println("《请校对字段名称和字段描述》=" + tableName + "  desc=" + desc + "   " + ChineseToPinYin.getPinYinHeadChar(desc).toLowerCase() + "   " + fieldName.toLowerCase());
            }

            //是否是主键
            boolean primaryKey = false;
            Cell cell_primaryKey = row.getCell(j++);
            if (cell_primaryKey != null && "Y".equals(cell_primaryKey.getStringCellValue())) {
                primaryKey = true;
            }
            //是否可以为空
            //跳过外键
            j++;
            boolean empty = false;
            Cell cell_empty = row.getCell(j++);
            if (cell_empty != null && "Y".equals(cell_empty.getStringCellValue())) {
                empty = true;
            }
            //默认值
            Cell cell_default = row.getCell(j++);
            String defaultValue = null;
            if (cell_default != null) {
                cell_default.setCellType(CellType.STRING);
                defaultValue = cell_default.getStringCellValue();
            }

            //备注
            String remark = null;
            if (row.getCell(j) != null) {
                remark = row.getCell(j).getStringCellValue();
            }
            TableEntity entity = new TableEntity(desc, fieldName, fieldType, primaryKey, empty, defaultValue, remark);
            list.add(entity);
        }
        Table table = new Table();
        table.setList(list);
        table.setTableName(tableName);
        return table;
    }

}
