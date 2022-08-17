package pers.lzy.template.excel.core;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;

/**
 * @author immort-lzy
 * @since 2021/8/17
 * <p>
 * Excel导出操作统一的后置处理规范。
 */
public interface OperateExcelPostProcessor {

    /**
     * 对Excel进行个性化处理的方法
     *
     * @param sheet                要操作的sheet
     * @param params               需要的参数列表 (当然，此数据可以在整个handler中流转)
     * @param expressionCalculator 表达式计算器
     */
    void operatePostProcess(Sheet sheet, Map<String, Object> params, ExpressionCalculator expressionCalculator);


}
