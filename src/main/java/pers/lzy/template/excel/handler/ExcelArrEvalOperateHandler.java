package pers.lzy.template.excel.handler;

import com.google.auto.service.AutoService;
import pers.lzy.template.excel.anno.CellOperateHandler;
import pers.lzy.template.excel.anno.HandlerOrder;
import pers.lzy.template.excel.constant.TagNameConstant;
import pers.lzy.template.excel.core.OperateExcelCellHandler;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/28  13:27
 */
@HandlerOrder(20000)
@CellOperateHandler(tagName = TagNameConstant.ARR_TAG_NAME)
@AutoService(OperateExcelCellHandler.class)
public class ExcelArrEvalOperateHandler extends AbstractExcelArrEvalOperateHandler {
    /**
     * 是否需要合并相同单元格
     *
     * @return true:合并，false不合并
     */
    @Override
    protected boolean mergeArrCell() {
        return false;
    }
}
