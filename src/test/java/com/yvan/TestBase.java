package com.yvan;

import com.yvan.util.AssertUtil;
import com.yvan.util.ReportUtil;
import com.yvan.util.StringUtil;

/**
 * Function：测试用例基类
 * Created by yawa1hz1 on 2018/4/12 10:29.
 */
public class TestBase {

    /**
     * 验证结果
     *
     * @param sourchData 原数据
     * @param verifyData 待验证数据
     */
    protected void verifyResult(String sourchData, String verifyData) {
        if (StringUtil.isEmpty(verifyData)) {
            return;
        }
        ReportUtil.log("验证数据：" + verifyData);
        // 断言结果包含
        AssertUtil.assertIsContains(sourchData, verifyData);
    }

}
