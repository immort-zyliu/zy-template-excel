package pers.lzy.template.excel.handler;

import com.google.auto.service.AutoService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import pers.lzy.template.excel.anno.CellOperateHandler;
import pers.lzy.template.excel.anno.HandlerOrder;
import pers.lzy.template.excel.constant.TagNameConstant;
import pers.lzy.template.excel.core.AbstractOperateExcelCellHandler;
import pers.lzy.template.excel.core.ExpressionCalculator;
import pers.lzy.template.excel.core.OperateExcelCellHandler;

import java.util.Map;

import static pers.lzy.template.excel.utils.ExcelUtil.setCellValue;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/23  18:12
 * 处理 simple 标签的 handler
 */
@HandlerOrder(10000)
@CellOperateHandler(tagName = TagNameConstant.SIMPLE_TAG_NAME)
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
        Object result = expressionCalculator.calculateNoFormat(expressionStr, params);
        // 设置到单元格中
        setCellValue(cell, this.formatCellValue(result));
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
