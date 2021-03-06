package com.yvan.util;

import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Function：Excel工具类
 * Created by YangWang on 2018-03-25 2:09.
 */
public class ExcelUtil {
    public static Map<String, Map<Integer, String>> caseCellValueMap = new HashMap<String, Map<Integer, String>>();

    /**
     * @param filepath
     * @param sheetNum  表单序号非索引
     * @param startRow
     * @param endRow
     * @param startCell
     * @param endCell
     * @return
     */
    public static Object[][] read(String filepath, int sheetNum, int startRow, int endRow, int startCell, int endCell) {
        InputStream inputStream = null;
        Object[][] datas = new Object[endRow - startRow + 1][endCell - startCell + 1]; // 先定义对象的行列大小
        try {
            inputStream = new FileInputStream(new File(filepath));
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(sheetNum - 1);
            for (int i = startRow; i <= endRow; i++) {
                Row row = sheet.getRow(i - 1);
                for (int j = startCell; j <= endCell; j++) {
                    Cell cell = row.getCell(j - 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // 获取列，并指定一个策略，避免空指针
                    cell.setCellType(CellType.STRING); // 设置字符串类型数据
                    String value = cell.getStringCellValue(); // 通过列获取字符串数据
                    datas[i - startRow][j - startCell] = value; // 从[0][0]开始
                }
            }
            return datas;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return datas;
    }

    /**
     * 准备批量回写Excel的数据
     *
     * @param caseId  用例编号
     * @param cellNum 列号
     * @param value   需要写入对于列的值
     */
    public static void addTestResult(String caseId, Integer cellNum, String value) {
        if (caseCellValueMap.get(caseId) != null) {
            caseCellValueMap.get(caseId).put(cellNum, value);
        } else {
            Map<Integer, String> cellValueMap = new HashMap<Integer, String>();
            cellValueMap.put(cellNum, value);
            caseCellValueMap.put(caseId, cellValueMap);
        }
//        System.out.println(caseCellValueMap);
    }

    /**
     * 批量写入
     *
     * @param filepath Excel文件路径
     * @param sheetNum 表单序号
     */
    public static void batchWrite(String filepath, int sheetNum) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(new File(filepath));
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(sheetNum - 1);
            Set<String> caseIds = caseCellValueMap.keySet();
            // 处理用例编号对应行的数据写入
            for (String caseId :
                    caseIds) {
                int rowCount = sheet.getLastRowNum();
                Row wantedRow = null;
                // 匹配所有行第一列值，确定用例编号对应的行
                for (int i = 0; i <= rowCount; i++) {
                    Row row = sheet.getRow(i);
                    Cell cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellType(CellType.STRING);
                    String value = cell.getStringCellValue();
                    if (value.equals(caseId)) {
                        wantedRow = row;
                        break;
                    }
                }
                if (wantedRow != null) {
                    Map<Integer, String> cellValueMap = caseCellValueMap.get(caseId);
//                    System.out.println(cellValueMap);
                    Set<Integer> cellNums = cellValueMap.keySet();
                    // 处理某行上需要操作的所有列
                    for (Integer cellNum :
                            cellNums) {
                        String value = cellValueMap.get(cellNum);
                        Cell cell = wantedRow.getCell(cellNum - 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        cell.setCellType(CellType.STRING);
                        cell.setCellValue(value);
                    }
                }
            }
            os = new FileOutputStream(new File(filepath));
            workbook.write(os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取预期结果
     *
     * @param caseId 用例ID
     * @param cellNum 列号
     * @return 返回预期结果
     */
    public static String getExpectData(String caseId, int cellNum) {
        Object[][] datas = read("src/main/resources/rest_infos.xlsx", 2, 2, 4, 1, 4);
        String value = "";
        for (Object[] objects :
                datas) {
            for (int i = 0; i < objects.length; i++) {
                if (caseId.equals(objects[0].toString())){
                    value = objects[cellNum-1].toString();
                    break;
                }
            }
        }
        return value;
    }
}
