package com.yvan.util;

import org.testng.Assert;

/**
 * Function：断言工具类
 * Created by yawa1hz1 on 2018/4/12 10:54.
 */
public class AssertUtil {

    /**
     * 断言是否包含
     *
     * @param source 原数据
     * @param search 待检数据
     */
    public static void assertIsContains(String source, String search) {
        Assert.assertTrue(source.contains(search), String.format("\n预期结果\t'%s'\n不包含实际结果'%s'。", source, search));
    }

}
