package com.yvan.liukaidi;

import org.apache.http.ParseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Liu KaiDi
 * 为http写的小性能测试程序，待以后优化
 */
public class HttpTest {
    private static final Logger log = Logger.getLogger(HttpTest.class.getName());

    /**
     * 测试url
     **/
    private String url;

    /**
     * method
     **/
    private String method = HttpUtil.GET;
    /**
     * 访问次数，次数结束后程序退出
     **/
    private Integer endNum;
    /**
     * 结束时间（秒），时间结束后程序退出
     **/
    private Integer endTime;
    /**
     * 访问时间间隔（毫秒）
     **/
    private int intervalTime = 100;
    /**
     * 超出某个时间的记录下来（毫秒）
     **/
    private Integer exceedTime;

    /**
     * 已经访问的次数
     **/
    private int sumNum = 0;
    /**
     * 已经用时间（秒）
     **/
    private long sumTime = 0;
    /**
     * 最小通信时间（毫秒）
     **/
    private int minTime = 0;
    /**
     * 最大通信时间（毫秒）
     **/
    private int maxTime = 0;
    /**
     * 平均时间（毫秒）
     **/
    private double avgTime = 0;
    /**
     * 通信失败次数
     **/
    private int errorNum = 0;
    /**
     * 超出某个时间的次数
     **/
    private int exceedNum = 0;

    public static void main(String rags[]) {
        String path = HttpTest.class.getClassLoader().getResource("").getPath().replace("%20", " ");
        Properties pro;
        try {
            pro = loadUtf8(path + "..\\config.txt");
        } catch (IOException e) {
            e.printStackTrace();
            log.warning("读取数据错误" + e.getMessage());
            return;
        }
        String url = pro.getProperty("url");
        String method = pro.getProperty("method");
        Integer endNum = null;
        Integer endTime = null;
        int intervalTime = 100;
        Integer exceedTime = null;
        try {
            String t = pro.getProperty("endNum");
            if (t != null || t.length() != 0) {
                endNum = Integer.parseInt(t);
            }
            t = pro.getProperty("endTime");
            if (t != null || t.length() != 0) {
                endTime = Integer.parseInt(t);
            }
            intervalTime = Integer.parseInt(pro.getProperty("intervalTime"));

            t = pro.getProperty("exceedTime");
            if (t != null || t.length() != 0) {
                exceedTime = Integer.parseInt(t);
            }
            exceedTime = Integer.parseInt(t);
        } catch (NumberFormatException e) {
            log.warning("参数错误，必须为数值" + e.getMessage());
        }
        if (endNum == null && endTime == null) {
            log.warning("ndNum 和 endTime 不能都为空");
            return;
        }
        HttpTest test = new HttpTest(url);
//		test.endTime = 60 * 1;
//		test.exceedTime = 300;
        test.method = method;
        test.endNum = endNum;
        test.endTime = endTime;
        test.intervalTime = intervalTime;
        test.exceedTime = exceedTime;
        test.test();
    }

    public HttpTest(String url) {
        this.url = url;
    }

    private long startTime;

    private void test() {
        if (endNum == null && endTime == null) {
            throw new RuntimeException("endNum 和 endTime 至少有一个值");
        }
        startTime = System.currentTimeMillis();
        boolean flag = true;
        //优先使用次数
        if (endNum != null) {
            Long time = null;
            if (endTime != null) {
                time = (long) (endTime * 1000);
            }
            while (isRun(time)) {
                flag = run(flag);
            }
        }
        startTime = System.currentTimeMillis() - startTime;
        avgTime = (double) startTime / sumNum;
        sumTime = (long) Math.ceil(startTime / 1000.0);
        printlnString();
    }

    private boolean isRun(Long time) {
        return (endNum != null ? endNum != sumNum : false) && (time != null ? System.currentTimeMillis() - startTime <= time : false);
    }

    private boolean run(boolean flag) {
        long time = System.currentTimeMillis();
        try {
            HttpUtil.http(method, url, null);
        } catch (ParseException | IOException e) {
            log.warning("http接口调用错误：" + e.getMessage());
            errorNum++;
        }
        time = System.currentTimeMillis() - time;
        if (flag) {
            flag = false;
            maxTime = (int) time;
            minTime = (int) time;
        } else {
            if (time > maxTime) {
                maxTime = (int) time;
            }
            if (time < minTime) {
                minTime = (int) time;
            }
        }
        if (exceedTime != null && time > exceedTime) {
            exceedNum++;
        }
        sumNum++;
        log.info("执行次数" + sumNum + "\n运行时间（毫秒）" + (System.currentTimeMillis() - startTime));
        try {
            Thread.sleep(intervalTime);
        } catch (InterruptedException e) {
        }
        return false;
    }

    private String printlnString() {
        StringBuilder sb = new StringBuilder();
        sb.append("地址：").append(url).append('\n');
        sb.append("用时（秒）：").append(sumTime).append('\n');
        sb.append("访问次数：").append(sumNum).append('\n');
        sb.append("访问时间间隔（毫秒）").append(intervalTime).append('\n');
        sb.append("最小通信时间（毫秒）：").append(minTime).append('\n');
        sb.append("最大通信时间（毫秒）：").append(maxTime).append('\n');
        sb.append("平均时间（毫秒）：").append(avgTime).append('\n');
        sb.append("通信失败次数：").append(errorNum).append('\n');
        if (exceedTime != null) {
            sb.append("超出").append(exceedTime).append("（毫秒）的次数").append(exceedNum).append('\n');
        }
        log.info(sb.toString());
        return sb.toString();
    }

    public static Properties loadUtf8(String fileName) throws IOException {
        return load(fileName, "utf-8");
    }

    public static Properties load(String fileName, String charsetName) throws IOException {
        InputStreamReader in = new InputStreamReader(new FileInputStream(fileName), charsetName);
        Properties properties = new Properties();
        properties.load(in);
        in.close();
        return properties;
    }


}
