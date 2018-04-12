package com.yvan.listener;

import com.yvan.exception.BaseException;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * Function：覆写重试分析接口
 * Created by yawa1hz1 on 2018/4/12 18:03.
 */
public class TestngRetry implements IRetryAnalyzer {

    private static int retryCount = 1;
    private static int maxRetryCount = 2;

    public boolean retry(ITestResult result) {
        // TODO Auto-generated method stub
        if (result.getThrowable() instanceof BaseException && retryCount % maxRetryCount != 0) {
//			String message = "running retry for  '" + result.getName()
//					+ "' on class " + this.getClass().getName() + " Retrying "
//					+ retryCount + " times";
            Reporter.setCurrentTestResult(result);

            Reporter.log("RunCount=" + (retryCount + 1));
            retryCount++;
            return true;
        } else {
            resetRetryCount();
            return false;
        }
    }

    public static void resetRetryCount() {
        retryCount = 1;
    }

}
