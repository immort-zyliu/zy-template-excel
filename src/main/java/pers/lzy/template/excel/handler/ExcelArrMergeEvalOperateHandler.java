package pers.lzy.template.excel.handler;

import com.google.auto.service.AutoService;
import pers.lzy.template.excel.anno.HandlerOrder;
import pers.lzy.template.excel.constant.TagNameConstant;
import pers.lzy.template.excel.core.OperateExcelCellHandler;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/28  13:30
 * 具有合并功能的 处理器
 * 此处理器只处理 arr-merge 标签
 */
@HandlerOrder(20000)
@AutoService(OperateExcelCellHandler.class)
public class ExcelArrMergeEvalOperateHandler extends AbstractExcelArrEvalOperateHandler {

    /**
     * 确定子类实现的标签
     *
     * @return 标签名称
     */
    @Override
    protected String determineTagName() {
        return TagNameConstant.ARR_MERGE_TAG_NAME;
    }
}
