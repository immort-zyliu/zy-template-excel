package pers.lzy.template.excel.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import pers.lzy.template.excel.anno.CellOperateHandler;
import pers.lzy.template.excel.common.TagParser;
import pers.lzy.template.excel.exception.OperateExcelCellHandlerInitException;

import java.util.Map;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/24  13:43
 * 公用判断抽取
 */
public abstract class AbstractOperateExcelCellHandler implements OperateExcelCellHandler {

    /**
     * 当前 handler 所处理的标签
     */
    protected final String tagName;

    public AbstractOperateExcelCellHandler() {

        CellOperateHandler cellOperateHandler = this.getClass().getAnnotation(CellOperateHandler.class);
        if (cellOperateHandler == null) {
            throw new OperateExcelCellHandlerInitException("The OperateExcelCellHandler must identify the CellOperateHandler annotation");
        }

        tagName = cellOperateHandler.tagName();
    }

    /**
     * 对Excel进行个性化处理的方法
     *
     * @param sheet                要操作的sheet
     * @param cell                 要操作的cell
     * @param params               需要的参数列表(当然，此数据可以在整个handler中流转)
     * @param expressionCalculator 表达式计算器
     */
    @Override
    public void operate(Sheet sheet, Cell cell, Map<String, Object> params, ExpressionCalculator expressionCalculator) {
        String expression = TagParser.parseCellTagContent(cell, tagName);
        // 说明没有解析出来表达式，不需要本handler处理。
        if (StringUtils.isBlank(expression)) {
            return;
        }

        // 说明需要本handler 处理。，则调用目标方法进行处理
        this.doOperate(sheet, cell, params, expression, expressionCalculator);
    }

    /**
     * 说明判断通过，使用此方法让子类进行真正的处理
     *
     * @param sheet                sheet
     * @param cell                 被操作的单元格
     * @param params               需要参数列表
     * @param expressionStr        需要计算的表达式字符串
     * @param expressionCalculator 表达式计算器
     */
    protected abstract void doOperate(Sheet sheet, Cell cell, Map<String, Object> params, String expressionStr, ExpressionCalculator expressionCalculator);

}
