package pers.lzy.template.excel.handler;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import pers.lzy.template.excel.constant.CommonDataNameConstant;
import pers.lzy.template.excel.core.AbstractOperateExcelCellHandler;
import pers.lzy.template.excel.core.ExpressionCalculator;
import pers.lzy.template.excel.pojo.ArrInfo;
import pers.lzy.template.excel.pojo.MergeArrInfo;
import pers.lzy.template.excel.utils.ExcelUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/24  17:32
 * <p>
 * 处理 arr 标签的 抽象  handler
 */
public abstract class AbstractExcelArrEvalOperateHandler extends AbstractOperateExcelCellHandler {

    /**
     * 说明判断通过，使用此方法让子类进行真正的处理
     *
     * @param sheet                sheet
     * @param cell                 被操作的单元格
     * @param params               需要参数列表
     * @param expressionStr        需要计算的表达式字符串
     * @param expressionCalculator 表达式计算器
     */
    @Override
    protected void doOperate(Sheet sheet, Cell cell, Map<String, Object> params, String expressionStr, ExpressionCalculator expressionCalculator) {

        // 获取目标数组
        Collection<?> arrInParamsByExpression = expressionCalculator.parseObjArrInParamMap(expressionStr, params);

        // 获取数组标记量
        @SuppressWarnings("unchecked")
        Map<String, ArrInfo> arrHistory = (Map<String, ArrInfo>) params.get(CommonDataNameConstant.ARR_HISTORY);

        // 初始化遍历容量
        int traverseNumber = arrInParamsByExpression.size();

        // 根据数组的长度插入行(如果需要的话)
        int rowIndexKey = cell.getRowIndex();

        // 获取arrInfo
        ArrInfo arrInfo = parseArrInfo(arrHistory, rowIndexKey, cell, arrInParamsByExpression);

        // 如果是arr-merge 标签的话，需要将当前的 arrInfo 放入,以便后面合并的时候使用。
        saveMargeArrInfoIfNecessary(arrInfo, cell, params);

        // 确定需要遍历的行数
        traverseNumber = determineTraverseNumber(traverseNumber, arrInfo);

        // 插入行，如果需要的话
        insertRowsIfNecessary(sheet, cell, arrInfo);

        // 对这一列进行数据的填充
        fillArrDataToSheet(sheet, cell, traverseNumber, arrInfo, params, expressionStr, expressionCalculator);
    }

    /**
     * 生成marge arr info ，如果需要的话。
     * <p>
     * 保存 mergeArrInfo的作用就是为了在后面进行相同单元格的合并。
     *
     * @param arrInfo arrInfo信息
     * @param cell    当前单元格
     * @param params  全局参数
     */
    private void saveMargeArrInfoIfNecessary(ArrInfo arrInfo, Cell cell, Map<String, Object> params) {

        // 决定是否需要合并arr 单元格
        if (!mergeArrCell()) {
            return;
        }

        // 构造 pojo 并添加。
        @SuppressWarnings("unchecked")
        List<MergeArrInfo> mergeArrInfoList = (List<MergeArrInfo>) params.get(CommonDataNameConstant.MERGE_ARR_INFO);

        mergeArrInfoList.add(new MergeArrInfo(
                arrInfo.getStartRow(),
                arrInfo.getStartRow() + arrInfo.getSize(),
                cell.getColumnIndex()
        ));

    }

    /**
     * 是否需要合并相同单元格
     *
     * @return true:合并，false不合并
     */
    protected abstract boolean mergeArrCell();

    private int determineTraverseNumber(int traverseNumber, ArrInfo arrInfo) {
        // 确定第一次
        Integer arrTraverseNumber = arrInfo.getSize();
        // 确定 最小的遍历容量。
        return Math.min(traverseNumber, arrTraverseNumber);
    }

    private ArrInfo parseArrInfo(Map<String, ArrInfo> arrHistory, int rowIndex, Cell cell, Collection<?> arrInParamsByExpression) {
        ArrInfo arrInfo = arrHistory.get(String.valueOf(rowIndex));
        if (arrInfo == null) {
            // 说明需要插入行
            arrInfo = new ArrInfo();
            arrInfo.setStartRow(rowIndex);
            arrInfo.setSize(arrInParamsByExpression.size());
            arrInfo.setInsertRowFlag(true);
            arrInfo.setMinColumnIndexFromGiven(cell.getColumnIndex());
            arrHistory.put(String.valueOf(rowIndex), arrInfo);
        } else {
            // 说明是其他列的，则试探一下最小（不过一般是不会成功的,因为第一次设置的那个就是最小的）
            arrInfo.setMinColumnIndexFromGiven(cell.getColumnIndex());
            // 说明已经插入过行了。
            arrInfo.setInsertRowFlag(false);
        }
        return arrInfo;
    }

    /**
     * @param sheet                sheet
     * @param cell                 单元格
     * @param traverseNumber       需要遍历的次数
     * @param arrInfo              数组参数
     * @param params               全部的数据集合
     * @param expressionStr        标签内的表达式
     * @param expressionCalculator 表达式计算器
     */
    private void fillArrDataToSheet(Sheet sheet, Cell cell, int traverseNumber, ArrInfo arrInfo, Map<String, Object> params, String expressionStr, ExpressionCalculator expressionCalculator) {

        boolean blankFlag = false;

        if (traverseNumber <= 0) {
            // 此时就说明没有数组数据，或者是数组元素个数为0
            // 我们就要设置 空 flag 为true; 同时让其遍历一次，使其将当前单元格制空（因为没有值嘛）
            traverseNumber ++;
            blankFlag = true;
        }



        for (int index = 0; index < traverseNumber; index++) {

            // 默认为空
            Object realValue = null;

            // 说明此时填充的数组是有的，同时也是有元素的，
            // 所以我们就计算即可。
            if (!blankFlag) {
                // 将expressionStr中的[] 中填充数字，方便取出数据
                String realExpressionStr = expressionStr.replaceAll("\\[]", String.format("[%d]", index));
                // 说明需要处理, 计算表达式并赋值。
                realValue = expressionCalculator.calculateNoFormat(realExpressionStr, params);

            }

            realValue = this.formatCellValue(realValue);

            // 替换到单元格中
            ExcelUtil.setCellObjValue(sheet,
                    arrInfo.getStartRow() + index,
                    cell.getColumnIndex(),
                    realValue,
                    cell.getCellStyle());
        }
    }

    private void insertRowsIfNecessary(Sheet sheet, Cell cell, ArrInfo arrInfo) {
        // 第一次是需要插入行的。
        // 3. 判断是否应该插入行，如果成立，则进行行的插入。
        if (arrInfo.getInsertRowFlag()) {
            // 进行行的插入
            ExcelUtil.insertRowAndCopyStyle(sheet, arrInfo.getStartRow(), arrInfo.getSize() - 1);
            //合并左侧的单元格，如资金详细信息左侧有个合并的资金信息的标题
            // 当然啦，只有左侧是合并单元格的时候，才会合并左侧的单元格
            // 还有一个条件，就是这是再插入arrMerge标签的时候
            ExcelUtil.mergeLeft(sheet, cell, arrInfo.getSize() - 1);
        }
    }

    /**
     * 格式化 计算出来两单元格的值,子类可以重写更改
     *
     * @param realValue 计算出来的值
     * @return 格式化后的值
     */
    protected Object formatCellValue(Object realValue) {
        return realValue;
    }

}
