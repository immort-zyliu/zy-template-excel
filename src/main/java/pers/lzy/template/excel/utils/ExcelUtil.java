package pers.lzy.template.excel.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/24  11:10
 */
public class ExcelUtil {


    /**
     * 合并单元格
     */
    public static CellRangeAddress mergedRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        if (firstRow == lastRow && firstCol == lastCol) {
            return null;
        }
        CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow,
                firstCol, lastCol);
        sheet.addMergedRegion(cellRangeAddress);
        return cellRangeAddress;
    }



    /**
     * 合并单元格
     */
    public static void mergedRegionNoRtn(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        if (firstRow == lastRow && firstCol == lastCol) {
            return;
        }
        CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow,
                firstCol, lastCol);
        sheet.addMergedRegion(cellRangeAddress);
    }

    /**
     * 获取单元格的内容
     *
     * @param cell 单元格
     * @return 内容（转换为字符串后的）
     */
    public static String getCellValue(Cell cell) {
        CellType cellType = cell.getCellType();
        Object value;
        switch (cellType) {
            default:
            case _NONE:
            case STRING:
            case BLANK:
                value = cell.getStringCellValue();
                break;
            case NUMERIC:
                value = cell.getNumericCellValue();
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case FORMULA:
                value = cell.getCellFormula();
                break;
            case ERROR:
                value = cell.getErrorCellValue();
                break;
        }

        return String.valueOf(value);


    }

    /**
     * 获取指定单元格的值
     * @param sheet sheet
     * @param rowNumber 行索引
     * @param columnNumber 列索引
     * @return 单元格的值
     */
    public static String getCellValue(Sheet sheet, Integer rowNumber, Integer columnNumber) {
        Cell cell = sheet.getRow(rowNumber).getCell(columnNumber);
        return getCellValue(cell);
    }

    /**
     * 插入行
     *
     * @param starRow 从第几行开始插入
     * @param rows    插入多少行
     */
    public static void insertRowAndCopyStyle(Sheet sheet, int starRow, int rows) {
        //复制单元格格式
        if (starRow + 1 <= sheet.getLastRowNum()) {
            sheet.shiftRows(starRow + 1, sheet.getLastRowNum(), rows, true, false);
        }
        starRow = starRow - 1;
        for (int i = 0; i < rows; i++) {
            Row sourceRow;
            Row targetRow;
            Cell sourceCell;
            Cell targetCell;
            int m;
            starRow = starRow + 1;
            sourceRow = sheet.getRow(starRow);
            targetRow = sheet.createRow(starRow + 1);
            for (m = sourceRow.getFirstCellNum(); m < sourceRow.getLastCellNum(); m++) {
                sourceCell = sourceRow.getCell(m);
                targetCell = targetRow.createCell(m);
                if (sourceCell == null) {
                    continue;
                }
                targetCell.setCellStyle(sourceCell.getCellStyle());
                targetCell.setCellType(sourceCell.getCellType());
            }
        }
    }

    /**
     * 插入行
     *
     * @param starRow 从第几行开始插入
     * @param rows    插入多少行
     */
    public static void insertRow(Sheet sheet, int starRow, int rows) {
        //构造单元格合并信息
        int firstRow = starRow;
        List<CellRangeAddress> addMergedRegionList = new ArrayList<>();
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() > firstRow) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(
                        cellRangeAddress.getFirstRow() + rows, cellRangeAddress.getLastRow() + rows,
                        cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
                addMergedRegionList.add(newCellRangeAddress);
            }
            if (cellRangeAddress.getFirstRow() == firstRow) {
                for (int j = 1; j <= rows; j++) {
                    CellRangeAddress newCellRangeAddress = new CellRangeAddress(
                            cellRangeAddress.getFirstRow() + j, cellRangeAddress.getLastRow() + j,
                            cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
                    addMergedRegionList.add(newCellRangeAddress);
                }
            }
        }

        //复制单元格格式
        if (starRow + 1 <= sheet.getLastRowNum()) {
            sheet.shiftRows(starRow + 1, sheet.getLastRowNum(), rows, true, false);
        }
        starRow = starRow - 1;
        for (int i = 0; i < rows; i++) {
            Row sourceRow;
            Row targetRow;
            Cell sourceCell;
            Cell targetCell;
            int m;
            starRow = starRow + 1;
            sourceRow = sheet.getRow(starRow);
            targetRow = sheet.createRow(starRow + 1);
            for (m = sourceRow.getFirstCellNum(); m < sourceRow.getLastCellNum(); m++) {
                sourceCell = sourceRow.getCell(m);
                targetCell = targetRow.createCell(m);
                if (sourceCell == null) {
                    continue;
                }
                targetCell.setCellStyle(sourceCell.getCellStyle());
                targetCell.setCellType(sourceCell.getCellType());
            }
        }

        //执行单元格合并
       /* for (CellRangeAddress newCellRangeAddress : addMergedRegionList) {
            try {
                sheet.addMergedRegion(newCellRangeAddress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    /**
     * 合并左侧的单元格，如资金详细信息左侧有个合并的资金信息的标题
     */
    public static void mergeLeft(Sheet sheet, Cell cell, int size) {

        int curRow = cell.getRowIndex();
        int curCol = cell.getColumnIndex();
        if (curCol > 0) {
            //判断当前 行 的 左边是否有合并的单元格
            int leftCol = curCol - 1;
            boolean isMerged = ExcelUtil.isMergedRegion(sheet, curRow, leftCol);
            // 如果是合并的，则记录原始合并的起始行列，结束行列
            if (isMerged) {
                int firstColumn = 0;
                int lastColumn = 0;
                int firstRow = 0;
                int lastRow = 0;
                int sheetMergeCount = sheet.getNumMergedRegions();
                for (int i = 0; i < sheetMergeCount; i++) {
                    CellRangeAddress ca = sheet.getMergedRegion(i);
                    firstColumn = ca.getFirstColumn();
                    lastColumn = ca.getLastColumn();
                    firstRow = ca.getFirstRow();
                    lastRow = ca.getLastRow();

                    if (curRow >= firstRow && curRow <= lastRow) {
                        if (leftCol >= firstColumn && leftCol <= lastColumn) {
                            //拆分当前单元格
                            sheet.removeMergedRegion(i);
                            break;
                        }
                    }
                }
                //扩展单元格的合并
                lastRow = lastRow + size;
                CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow,
                        firstColumn, lastColumn);
                sheet.addMergedRegion(cellRangeAddress);
            }
        }
    }

    /**
     * 设置单元格内容。单元格为空时自动创建
     */
    public static void setCellValue(Sheet sheet, int rowNum, int colNum, String val, CellStyle style) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = sheet.getRow(rowNum).createCell(colNum);
        }
        cell.setCellValue(val);
        if (style == null && colNum > 0) {
            Cell preCell = sheet.getRow(rowNum).getCell(colNum - 1);
            if (preCell != null) {
                style = preCell.getCellStyle();
            }
        }
        cell.setCellStyle(style);
    }

    /**
     * 判断指定的单元格是否是合并单元格
     */
    public static boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();

        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 写入excel文件
     */
    public static void writeFile(Workbook hw, File localModuleFile) {
        FileOutputStream excelFileOutPutStream = null;
        try {
            excelFileOutPutStream = new FileOutputStream(localModuleFile);
            hw.write(excelFileOutPutStream);
            excelFileOutPutStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (excelFileOutPutStream != null) {
                try {
                    excelFileOutPutStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
