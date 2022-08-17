package pers.lzy.template.excel.core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;

/**
 * Excel操作顶层规范接口
 *
 * @author immort-lzy
 * @since 2021/7/15
 */
public interface OperateExcelCellHandler {


    /**
     * 对Excel进行个性化处理的方法
     *
     * @param sheet                要操作的sheet
     * @param cell                 要操作的cell
     * @param params               需要的参数列表(当然，此数据可以在整个handler中流转)
     * @param expressionCalculator 表达式计算器
     */
    void operate(Sheet sheet, Cell cell, Map<String, Object> params, ExpressionCalculator expressionCalculator);

}
