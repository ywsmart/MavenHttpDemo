package com.yvan.util;

import org.apache.poi.ss.usermodel.*;

import java.io.*;

/**
 * Function：Excel工具类
 * Created by YangWang on 2018-03-25 2:09.
 */
public class ExcelUtil {
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

    public static void write(String filepath, int sheetNum, String caseId, int cellNum, String result) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(new File(filepath));
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(sheetNum - 1);
            int lastRowNum = sheet.getLastRowNum();
            Row matchedRow = null;
            for (int i = 0; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                Cell cell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellType(CellType.STRING);
                String cellValue = cell.getStringCellValue();
                if (caseId.equals(cellValue)) {
                    matchedRow = row;
                    break;
                }
            }
            Cell cell = matchedRow.getCell(cellNum - 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(result);
            os = new FileOutputStream(new File(filepath));
            workbook.write(os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//    public static void main(String[] args) {
//        Object[][] datas = read("src/rest_infos.xlsx", 1, 2, 3, 1, 4);
//        for (Object[] objects :
//                datas) {
//            for (Object object :
//                    objects) {
//                System.out.print("【"+object+"】");
//            }
//            System.out.println();
//        }
//
//    }

}
