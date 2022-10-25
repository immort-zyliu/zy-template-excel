package pers.lzy.template.excel.core;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/10/25  13:17
 */
public class OperateExcelCellHandlerHolder {

    /**
     * cell handler
     */
    private final OperateExcelCellHandler operateExcelCellHandler;

    /**
     * tag name
     */
    private final String cellHandlerTagName;

    public OperateExcelCellHandlerHolder(OperateExcelCellHandler operateExcelCellHandler, String cellHandlerTagName) {
        this.operateExcelCellHandler = operateExcelCellHandler;
        this.cellHandlerTagName = cellHandlerTagName;
    }

    public OperateExcelCellHandler getOperateExcelCellHandler() {
        return operateExcelCellHandler;
    }

    public String getCellHandlerTagName() {
        return cellHandlerTagName;
    }
}
