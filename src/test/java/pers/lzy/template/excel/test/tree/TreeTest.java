package pers.lzy.template.excel.test.tree;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import pers.lzy.template.excel.core.TemplateExcelFiller;
import pers.lzy.template.excel.core.TemplateExcelFillerFactory;
import pers.lzy.template.excel.test.pojo.OutAddress;
import pers.lzy.template.excel.test.pojo.People;
import pers.lzy.template.excel.test.pojo.PeopleProvince;
import pers.lzy.template.excel.utils.ExcelUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/7/11  16:33
 * <p>
 * 树形结构的测试
 */
public class TreeTest {


    private TemplateExcelFiller templateExcelFiller;

    @Before
    public void before() {

        // 构建填充着
        templateExcelFiller = TemplateExcelFillerFactory.defaultTemplateExcelFillerBuilder()
                .expressionCacheSize(500)
                .functions("math", Math.class)
                .functions("StringUtils", StringUtils.class)
                .build();

    }

    @Test
    public void test() throws IOException, InvalidFormatException {
        Map<String, Object> param = initParam();
        //获取文件的URL
        URL url = this.getClass().getClassLoader().getResource("03peopleInfo.xlsx");
        assert url != null;

        Workbook workbook = new XSSFWorkbook(new File(url.getPath()));
        Sheet sheet = workbook.getSheetAt(0);


        // 调用我们的框架进行数据的填充
        templateExcelFiller.fillData(() -> sheet, () -> param);

        // 将Excel 写出去，查看结果
        ExcelUtil.writeFile(workbook, new File("C:\\Users\\immortal\\Desktop\\03peopleInfo1111.xlsx"));
    }


    private Map<String, Object> initParam() {
        Map<String, Object> res = new HashMap<>();

        // 联系人信息
        Map<String, Object> contractInfo = new HashMap<>();
        contractInfo.put("contactName", "liuzy");
        contractInfo.put("contactTel", "15020000000");
        res.put("contractInfo", contractInfo);


        // 人员信息（注意顺序的问题，如果顺序不对，是不能进行合并的。。。）
        List<PeopleProvince> peopleProvinceList = new ArrayList<>();
        peopleProvinceList.add(new PeopleProvince("河北省", "石家庄市", "兼顾村", "张三"));
        peopleProvinceList.add(new PeopleProvince("河北省", "石家庄市", "兼顾村", "张三"));
        peopleProvinceList.add(new PeopleProvince("河北省", "保定市", "牛村", "李四2"));
        peopleProvinceList.add(new PeopleProvince("河北省", "保定市", "牛村", "李四3"));
        peopleProvinceList.add(new PeopleProvince("北京市", "海淀区", "厉害村", "风"));
        peopleProvinceList.add(new PeopleProvince("北京市", "朝阳区", "牛逼村", "扽给"));
        res.put("peopleInfo", peopleProvinceList);

        return res;
    }
}
