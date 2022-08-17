package pers.lzy.template.excel.handler;

import com.google.auto.service.AutoService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import pers.lzy.template.excel.anno.HandlerOrder;
import pers.lzy.template.excel.constant.TagNameConstant;
import pers.lzy.template.excel.core.AbstractOperateExcelCellHandler;
import pers.lzy.template.excel.core.ExpressionCalculator;
import pers.lzy.template.excel.core.OperateExcelCellHandler;

import java.util.Map;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/23  18:12
 * 处理 simple 标签的 handler
 */
@HandlerOrder(10000)
@AutoService(OperateExcelCellHandler.class)
public class ExcelSimpleEvalOperateHandler extends AbstractOperateExcelCellHandler {


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
        // 说明需要处理, 计算表达式并赋值。
        String result = expressionCalculator.calculate(expressionStr, params);
        // 设置到单元格中
        cell.setCellValue(result);
    }

    /**
     * 确定子类实现的标签
     *
     * @return 标签名称
     */
    @Override
    protected String determineTagName() {
        return TagNameConstant.SIMPLE_TAG_NAME;
    }

}
