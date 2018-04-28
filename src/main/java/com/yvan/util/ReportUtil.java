package com.yvan.util;

import org.testng.Reporter;

import java.util.Calendar;

/**
 * Function：报告工具类
 * Created by yawa1hz1 on 2018/4/12 10:36.
 */
public class ReportUtil {
    private static String splitTimeAndMsg = "===";
    private static String reportName = "自动化测试报告";

    public static void log(String msg) {
        long timeMillis = Calendar.getInstance().getTimeInMillis();
        Reporter.log(timeMillis + splitTimeAndMsg + msg, true);
    }

    public static String getReportName() {
        return reportName;
    }

    public static String getSpiltTimeAndMsg() {
        return splitTimeAndMsg;
    }

    public static void setReportName(String reportName) {
        if (StringUtil.isNotEmpty(reportName)) {
            ReportUtil.reportName = reportName;
        }
    }
}
