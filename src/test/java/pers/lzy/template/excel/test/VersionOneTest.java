package pers.lzy.template.excel.test;

import org.apache.commons.lang3.StringUtils;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/25  13:17
 */
public class VersionOneTest {
    TemplateExcelFiller templateExcelFiller;

    @Before
    public void before() {

        // 构建导出着
        templateExcelFiller = TemplateExcelFillerFactory.defaultTemplateExcelFillerBuilder()
                .expressionCacheSize(500)
                .functions("math", Math.class)
                .functions("StringUtils", StringUtils.class)
                .build();

    }

    @Test
    public void test() throws IOException {
        Map<String, Object> param = initParam();

        //获取工作簿
        Workbook book = new XSSFWorkbook(new FileInputStream("C:\\Users\\immortal\\Desktop\\test1.xlsx"));
        //获取工作表
        Sheet sheet = book.getSheetAt(0);


        templateExcelFiller.fillData(sheet, param);

        ExcelUtil.writeFile(book, new File("C:\\Users\\immortal\\Desktop\\test1Copy.xlsx"));

    }

    private Map<String, Object> initParam() {
        Map<String, Object> res = new HashMap<>();

        Map<String, Object> out = new HashMap<>();
        out.put("name", "liuzy");
        out.put("code", "15020000000");

        List<OutAddress> outAddresses = new ArrayList<>();
        outAddresses.add(new OutAddress("a", 1));
        outAddresses.add(new OutAddress("b", 2));
        outAddresses.add(new OutAddress("c", 3));
        outAddresses.add(new OutAddress("d", 4));
        out.put("detail", outAddresses);

        res.put("out", out);

        List<People> peopleList = new ArrayList<>();
        peopleList.add(new People("超哥2", 28, "没有"));
        peopleList.add(new People("超哥3", 38, "没有"));
        peopleList.add(new People("超哥4", 138, "没有"));
        res.put("people", peopleList);

        List<PeopleProvince> peopleProvinceList = new ArrayList<>();
        peopleProvinceList.add(new PeopleProvince("河北省", "石家庄市", "兼顾村", "刘志"));
        peopleProvinceList.add(new PeopleProvince("河北省", "石家庄市", "兼顾村", "刘志"));
        peopleProvinceList.add(new PeopleProvince("河北省", "保定市", "牛村", "牛志2"));
        peopleProvinceList.add(new PeopleProvince("河北省", "保定市", "牛村", "牛志3"));
        peopleProvinceList.add(new PeopleProvince("北京市", "海淀区", "厉害村", "风"));
        peopleProvinceList.add(new PeopleProvince("北京市", "朝阳区", "牛逼村", "扽给"));
        res.put("province", peopleProvinceList);

        return res;
    }
}
