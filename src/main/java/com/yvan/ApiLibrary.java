package com.yvan;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Function：
 * Created by YangWang on 2018-03-31 21:45.
 */
public class ApiLibrary extends SourceCode {
    public static String path = "/Users/dahaohaozai/Desktop/report.html";
    public static int mark;// 用于记录每次接口用例执行
    public static int test_mark = ApiLibrary.getInstance().getMark();// 用于记录每次自动执行
    public static List<Map<String, String>> apiGroups = new ArrayList<Map<String, String>>();// 用于管理接口
    public static String HOST;//需要测试的环境的host地址
    public static final String POST = "post";
    public static final String GET = "get";
    public static boolean hostType;// 如果是ture代表正式环境，false代表测试环境
    public static boolean casesType = true;// 控制测试数据写入库，true写入正式数据看，false写入测试数据库
    protected static CloseableHttpClient httpClient = getCloseableHttpClients();

    /**
     * 获取接口的请求类型
     *
     * @param api_name
     *            接口名称
     * @return 返回接口请求类型
     */
    public String getApiType(String api_name) {
        String type = null;
        for (int i = 0; i < apiGroups.size(); i++) {
            Map<String, String> group = apiGroups.get(i);
            type = group.get(api_name);
            if (type != null) {
                return type;
            }
        }
        return type;
    }

    /**
     * 获取httpclient对象
     *
     * @return 返回HTTPclient对象
     */
    private static CloseableHttpClient getCloseableHttpClients() {
        return HttpClients.createDefault();
    }

    /**
     * 获取get对象
     *
     * @param url
     *            表示请求地址
     * @param apiCase
     *            表示传入数据
     * @return 返回get对象
     */
    private HttpGet getHttpGet(String url, Map<String, String> apiCase) {
        String uri = url + changeJsonToArguments(apiCase);
        return new HttpGet(deleteAllCharFromString(" ", uri));
    }

    /**
     * 获取post对象，以form表单提交数据
     *
     * @param uri
     *            请求地址
     * @param apiCase
     *            请求数据，form表单形式设置请求实体
     * @return 返回post对象
     */
    private HttpPost getHttpPostAndSetFormEntity(String uri, Map<String, String> apiCase) {
        HttpPost httpPost = new HttpPost(uri);
        setFormHttpEntity(httpPost, apiCase);
        return httpPost;
    }

    /**
     * 获取响应实体，暂无header设置
     *
     * @param request
     *            请求对象
     * @return 返回json类型的对象
     */
    public JSONObject getHttpResponseEntityByJson(HttpRequestBase request) {
        output(request.toString());
        JSONObject jsonObject = new JSONObject();
        CloseableHttpResponse response = null;// 创建响应对象
        long data_size = 0;// 用于存放数据大小
        Map<String, String> info = getRequestInfo(request);
        String api_name = info.get("api_name");
        String type = info.get("type");
        String host_name = info.get("host_name");
        request.addHeader(HTTP.USER_AGENT, "okhttp/3.6.0");// 符合阿里应用防火墙
        Date start = getDate();// 记录开始时间
        try {
            response = httpClient.execute(request);
        } catch (ClientProtocolException e1) {
            output("client 异常", e1);
        } catch (IOException e1) {
            output("执行请求时java IO 异常！", e1);
        } // 获取响应
        Date end = getDate();// 记录结束时间
        double elapsed_time = outputTimeDiffer(start, end, "接口：" + api_name);// 获取响应耗时
        int status = response.getStatusLine().getStatusCode();// 获取响应状态
        output("状态码是：" + status);
        HttpEntity entity = response.getEntity();// 获取响应实体
        data_size = entity.getContentLength();// 获取相应数据大小
        if (data_size == -1) {// 如果为-1，则重置data_size
            data_size = 0;
        }
        String content = null;
        try {
            content = EntityUtils.toString(entity);// 用string接收响应实体
            EntityUtils.consume(entity);// 消耗响应实体
        } catch (ParseException e1) {
            output("解析响应实体异常！", e1);
        } catch (IOException e1) {
            output("解析响应实体时java IO 异常！", e1);
        } // 解析响应
        try {
            response.close();
        } catch (IOException e2) {
            output("响应关闭失败！", e2);
        }
        if (data_size == 0) {// 如果被重置或者没有获取到，则data_size等于解析string大小
            try {
                data_size = content.length();
            } catch (Exception e) {
                data_size = 1;
                output("获取响应长度异常！", e);
            }
        }
        if (status == 200) {
            try {
                jsonObject = new JSONObject(content);
            } catch (Exception e) {
                output(content + LINE + "45156", e);
            }
        } else {
            output("响应内容：" + content);

        }
        // request.releaseConnection();//此处容易造成socket close
        if (hostType) {
            MySqlOnline.getInstance().saveApiTestDate(host_name, api_name, data_size, elapsed_time, status, type, mark);
        } else {
            LocalMySql.getInstance().saveApiTestDate(host_name, api_name, data_size, elapsed_time, status, type, mark);
        }
        return jsonObject;
    }

    /**
     * 设置post接口上传表单
     *
     * @param httpPost
     *            post请求
     * @param apiCase
     *            传入的参数map
     */
    public void setFormHttpEntity(HttpPost httpPost, Map<String, String> apiCase) {
        Set<String> keys = apiCase.keySet();
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        for (String key : keys) {
            formparams.add(new BasicNameValuePair(key, apiCase.get(key)));
        }
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            output("form表单错误！");
            e.printStackTrace();
        }
        httpPost.setEntity(entity);
    }

    /**
     * 解析response，使用char数组，注意编码格式
     *
     * @param response
     *            传入的response，非closedresponse
     * @param encoding
     *            编码格式
     * @return string类型的response
     * @throws UnsupportedOperationException
     * @throws IOException
     */
    public String parseResponeEntityByChar(HttpResponse response, String encoding)
            throws UnsupportedOperationException, IOException {
        HttpEntity entity = response.getEntity();// 获取响应实体
        InputStream inputStream = entity.getContent();// 创建并实例化字节输入流，使用响应实体作为输入流
        InputStreamReader reader = new InputStreamReader(inputStream, encoding);// 创建并实例化字符输入流，并设置编码格式
        StringBuffer buffer = new StringBuffer(" ");// 创建并实例化stringbuffer，存放响应信息
        char[] buff = new char[512];// 创建并实例化字符数组
        int length = 0;// 声明变量length，表示读取长度
        while ((length = reader.read(buff)) != -1) {// 循环读取字符输入流
            String x = new String(buff, 0, length);// 获取读取到的有效内容
            buffer.append(x);// 将读取到的内容添加到stringbuffer中
        }
        // output(buffer.toString());//输出相应实体
        return buffer.toString();
    }

    /**
     * 从json数据中获取包含文本的验证，支持10个
     *
     * @param response
     *            相应json数据
     * @param key
     *            要获取的文本值
     * @param value
     *            期望值
     * @return 如果包含返回原文本，如果不包含，对应文本替换为author
     */
    private String getDataContains(JSONObject response, String key, String value) {
        String value1 = value;// 存放一个value，因为后面回去value进行编辑
        if (key == null || key.isEmpty()) {
            return value;
        }
        if (key.equals("text")) {// 如果key字段名为text，表示进入文本包含验证
            value = value.replace("，", ",");// 替换掉中文逗号
            if (value.indexOf(",") == -1) {// 如果不存在逗号，则直接进入验证
                boolean result = response.toString().contains(value);
                return result ? value1 : author;
            } else {// 如果包含逗号，先将中文逗号转化为英文都好
                List<String> list = new ArrayList<>();// 用list存放获取到的value中的值
                for (int i = 0; i < 10; i++) {// for循环获取验证文本
                    if (value.indexOf(",") == -1) {// 如果已经到头，则直接赋值并添加list，跳出循环
                        String key1 = value;
                        list.add(key1);
                        break;
                    }
                    String key1 = value.substring(0, value.indexOf(","));// 截取字符串
                    list.add(key1);// 添加list
                    value = deleteCharFromString(key1 + ",", value);// 删除截取到的字符串
                }
                List<String> results = new ArrayList<String>();
                for (int i = 0; i < list.size(); i++) {
                    if (response.toString().contains(list.get(i))) {
                        results.add(list.get(i));
                    } else {
                        results.add(author);
                    }
                }
                StringBuffer text = new StringBuffer();
                for (int i = 0; i < results.size(); i++) {
                    if (i == results.size() - 1) {
                        text.append(results.get(i));
                        continue;
                    }
                    text.append(results.get(i) + ",");
                }
                String texts = text.toString();
                return texts;// 返回值
            }
        } else {
            return author;// 如果不是text，则返回getdatevalue()方法的返回值
        }
    }

    /**
     * 获取验证字段类型，此方法是验证数字类型
     *
     * @param lines
     *            传入参数，lines是list<string>类型
     * @param key
     *            检查字段值的类型
     * @param value
     *            期望值
     * @return
     */
    private String getDateTypeIsNum(List<String> lines, String key, String value) {
        String content = lines.toString();
        String value1 = value;// 存放一个value，因为后面回去value进行编辑
        if (key == null || key.isEmpty() || lines.isEmpty()) {
            return value1;
        }
        if (key.equalsIgnoreCase("int")) {
            value = value.replace("，", ",");// 替换掉中文逗号
            if (value.indexOf(",") == -1) {// 如果不存在逗号，则直接进入验证
                if (!content.contains(value + ":")) {
                    return author;
                }
                for (int k = 0; k < lines.size(); k++) {
                    String line = lines.get(k);
                    if (line.startsWith(value + ":")) {
                        String lineValue = deleteCharFromString(value + ":", line);
                        if (!isNumber(lineValue)) {
                            return author;
                        }
                    }
                }
                return value1;
            } else {// 如果包含逗号，先将中文逗号转化为英文都好
                List<String> list = new ArrayList<>();// 用list存放获取到的value中的值
                for (int i = 0; i < 10; i++) {// for循环获取验证文本
                    if (value.indexOf(",") == -1) {// 如果已经到头，则直接赋值并添加list，跳出循环
                        String key1 = value;
                        list.add(key1);
                        break;
                    }
                    String key1 = value.substring(0, value.indexOf(","));// 截取字符串
                    list.add(key1);// 添加list
                    value = deleteCharFromString(key1 + ",", value);// 删除截取到的字符串
                }
                List<String> results = new ArrayList<String>();
                for (int i = 0; i < list.size(); i++) {
                    String keys = list.get(i);
                    if (!content.contains(keys + ":")) {
                        results.add(author);
                        continue;
                    }
                    boolean rs = true;
                    for (int k = 0; k < lines.size(); k++) {
                        String line = lines.get(k);
                        if (line.startsWith(keys + ":")) {
                            String lineValue = deleteCharFromString(keys + ":", line);
                            if (!isNumber(lineValue)) {
                                rs = false;
                                break;
                            }
                        }
                    }
                    if (rs) {
                        results.add(keys);
                    } else {
                        results.add(author);
                    }
                }
                StringBuffer text = new StringBuffer();
                for (int i = 0; i < results.size(); i++) {
                    if (i == results.size() - 1) {
                        text.append(results.get(i));
                        continue;
                    }
                    text.append(results.get(i) + ",");
                }
                String texts = text.toString();
                return texts;// 返回值
            }
        } else {
            return author;
        }
    }

    /**
     * 获取验证字段不为空
     *
     * @param lines
     * @param key
     * @param value
     * @return
     */
    private String checkDataNotNull(List<String> lines, String key, String value) {
        String content = lines.toString();
        String value1 = value;// 存放一个value，因为后面回去value进行编辑
        if (key == null || key.isEmpty() || lines.isEmpty()) {
            return value1;
        }
        value = value.replace("，", ",");// 替换掉中文逗号
        if (key.equalsIgnoreCase("notnull")) {
            if (value.indexOf(",") == -1) {// 如果不存在逗号，则直接进入验证
                if (!content.contains(value1)) {
                    return author;
                }
                boolean rs = true;
                for (int k = 0; k < lines.size(); k++) {
                    String line = lines.get(k);
                    if (line.contains(value + ":")) {
                        String lineValue = deleteCharFromString(value + ":", line);
                        if (lineValue != null && !lineValue.isEmpty()) {
                            continue;
                        } else {
                            rs = false;
                            break;
                        }
                    }
                }
                return rs ? value1 : author;
            } else {// 如果包含逗号，先将中文逗号转化为英文都好
                List<String> list = new ArrayList<>();// 用list存放获取到的value中的值
                for (int i = 0; i < 10; i++) {// for循环获取验证文本
                    if (value.indexOf(",") == -1) {// 如果已经到头，则直接赋值并添加list，跳出循环
                        String key1 = value;
                        list.add(key1);
                        break;
                    }
                    String key1 = value.substring(0, value.indexOf(","));// 截取字符串
                    list.add(key1);// 添加list
                    value = deleteCharFromString(key1 + ",", value);// 删除截取到的字符串
                }
                List<String> results = new ArrayList<String>();
                for (int i = 0; i < list.size(); i++) {
                    String keys = list.get(i);// 获取验证点的值
                    if (!content.contains(keys + ":")) {
                        results.add(author);
                        continue;
                    }
                    boolean rs = true;
                    for (int k = 0; k < lines.size(); k++) {
                        String line = lines.get(k);// 获取行数据
                        if (line.contains(keys + ":")) {
                            String lineValue = deleteCharFromString(keys + ":", line);
                            if (lineValue != null && !lineValue.isEmpty()) {
                                continue;
                            } else {
                                results.add(author);
                                rs = false;
                                break;
                            }
                        }
                    }
                    if (rs) {
                        results.add(keys);
                    }
                }
                StringBuffer text = new StringBuffer();
                for (int i = 0; i < results.size(); i++) {
                    if (i == results.size() - 1) {
                        text.append(results.get(i));
                        continue;
                    }
                    text.append(results.get(i) + ",");
                }
                String texts = text.toString();
                return texts;// 返回值
            }
        } else {
            return author;
        }
    }

    /**
     * 把json数据转化为参数，为get请求和post请求stringentity的时候使用
     *
     * @param argument
     *            请求参数，json数据类型，map类型，可转化
     * @return 返回拼接参数后的地址
     */
    @SuppressWarnings("unused")
    private String changeJsonToArguments(JSONObject argument) {
        String one = argument.toString();
        String two = "?" + one.substring(1, one.length() - 1).replace(",", "&").replace(":", "=").replace("\"", "");
        return two;
    }

    private String changeJsonToArguments(Map<String, String> apiCase) {
        Set<String> keys = apiCase.keySet();
        StringBuffer arg = new StringBuffer("?");
        for (String key : keys) {
            arg.append((key) + "=" + urlEncoderText(apiCase.get(key)) + "&");
        }
        return arg.deleteCharAt(arg.length() - 1).toString();// 此处为了兼容case内容为空
    }

    /**
     * 从textview中获取json数据，此方法用于复制textview直接请求接口
     *
     * @param textView
     *            请求参数的textview
     * @return 返回一个json类型的数据
     */
    private JSONObject getJsonFromTextView(String textView) {
        String two = urlDecoderText("{\"" + textView + "\"}");
        String three = two.replace("=", "\":\"").replaceAll("&", "\",\"");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(three);
        } catch (JSONException e) {
            output("json数组转化错误！");
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 获取用例执行结果，传入参数个数不定，但必须是偶数
     *
     * @param params
     *            传入参数，一般是expect和actual的形式
     * @return 若成功则为true，失败则为false
     */
    private boolean getResult(String... params) {
        boolean result = true;
        if (params.length % 2 == 1) {
            output("获取result的参数个数错啦！");
            return false;
        } else {
            for (int i = 0; i < params.length - 1; i += 2) {
                if (params[i] == null) {
                    continue;
                }
                if (params[i + 1] != null) {
                    params[i + 1] = params[i + 1].replaceAll("，", ",");
                }
                params[i] = params[i].replaceAll("，", ",");
                if (params[i] == null || params[i].isEmpty()) {
                    params[i] = author;
                }
                if (params[i + 1] == null || params[i + 1].isEmpty()) {
                    params[i + 1] = author;
                }
                if (!result) {
                    return false;
                }
                result = result && params[i].equals(params[i + 1]);
            }
        }
        return result;
    }

    /**
     * 从json数据中获取固定字段的值，如果字段有重复，则去第一个字段值返回
     *
     * @param lines
     *            传输参数，解析过的响应json数据
     * @param key
     *            字段名
     * @return 返回字段值
     */
    public String getValueFromJson(List<String> lines, String key) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith(key + ":")) {
                return deleteCharFromString(key + ":", line);
            }
        }
        return null;
    }

    /**
     * 根据方法名执行相应方法，利用反射，适用于带参方法
     *
     * @param api_name
     *            接口名称
     * @param use
     *            传入参数，此处为适配新的请求方法
     * @return 返回请求json数据
     */
    @SuppressWarnings("unused")
    private JSONObject executeMethodByName(String api_name, Map<String, String> use) {
        JSONObject jsonObject = new JSONObject();
        Object obj = null;
        Method method = null;
        try {
            // 里面写自己的类名及路径
            Class<?> c = Class.forName("juziyule.Special_juzi");
            obj = c.newInstance();
            // 第一个参数写的是方法名,第二个\第三个\...写的是方法参数列表中参数的类型
            method = c.getMethod(api_name, Map.class);
            // invoke是执行该方法,并携带参数值
        } catch (Exception e) {
            output("反射执行出错！", e);
        }
        try {
            jsonObject = (JSONObject) method.invoke(obj, new Object[] { use });
        } catch (Exception e) {
            output("反射运行方法异常！", e);
        }
        return jsonObject;
    }

    /**
     * 执行请求并获取json类型的响应值
     *
     * @param api_name
     *            接口名称
     * @param apiCase
     *            传入参数，map类型，请求数据是已经删除之后
     * @return 返回相应类型的json数据
     */
    private JSONObject getJsonResponse(String api_name, Map<String, String> apiCase) {
        String uri = HOST + api_name;
        String type = getApiType(api_name);
        if (type == null) {
            output("该接口尚未记录！");
            return new JSONObject();
        }
        deleteMap(apiCase);
        JSONObject response = new JSONObject();
        HttpRequestBase request = null;
        if (type.equalsIgnoreCase("get")) {
            request = getHttpGet(uri, apiCase);
        } else if (type.equalsIgnoreCase("post")) {
            request = getHttpPostAndSetFormEntity(uri, apiCase);
        } else {
            output("执行方法参数错误啦！");
        }
        response = getHttpResponseEntityByJson(request);
        return response;
    }




    /**
     * 执行一个接口的所有用例，先去读取用例，遍历执行每条用例，并讲结果记录在数据库里面
     *
     * @param api_name
     *            接口名称
     */
    protected void executeCases(String api_name) {
        mark = getMark();
        List<Map<String, String>> data = null;
        if (!hostType) {
            data = LocalMySql.getInstance().getCaseFromMySql(api_name);
        } else {
            data = MySqlOnline.getInstance().getCaseFromMySql(api_name);
        }
        if (data.size() == 0) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {// 遍历执行用例
            Map<String, String> use = data.get(i);// 获取单个用例
            String case_id = use.get("case_id");
            String depend_case = use.get("depend_case");
            if (isInt(depend_case)) {
                executeCase(changeStringToInt(depend_case));
            }
            String expect_value1 = use.get("verify_value1");// 获取验证点期望值
            String expect_value2 = use.get("verify_value2");// 获取验证点期望值
            String expect_value3 = use.get("verify_value3");// 获取验证点期望值
            String expect_value4 = use.get("verify_value4");// 获取验证点期望值
            String expect_value5 = use.get("verify_value5");// 获取验证点期望值
            String actual_key1 = use.get("verify_key1");// 获取检查点key
            String actual_key2 = use.get("verify_key2");// 获取检查点key
            String actual_key3 = use.get("verify_key3");// 获取检查点key
            String actual_key4 = use.get("verify_key4");// 获取检查点key
            String actual_key5 = use.get("verify_key5");// 获取检查点key
            String params = use.toString();// 记录传入参数
            JSONObject response = getJsonResponse(api_name, use);
            List<String> lines = parseJsonLines(response);
            String actual_value1 = getValueFromJson(lines, actual_key1);// 获取验证点实际值
            String actual_value2 = getValueFromJson(lines, actual_key2);// 获取验证点实际值
            String actual_value3 = getDataContains(response, actual_key3, expect_value3);// 获取验证点实际值，此为包含验证
            String actual_value4 = getDateTypeIsNum(lines, actual_key4, expect_value4);// 获取验证点实际值，此为包含验证
            String actual_value5 = checkDataNotNull(lines, actual_key5, expect_value5);// 获取验证点实际值，此为包含验证
            String[] data2 = { expect_value1, actual_value1, expect_value2, actual_value2, expect_value3, actual_value3,
                    expect_value4, actual_value4, expect_value5, actual_value5 };
            int result = getResult(data2) ? 1 : 2;// 获取测试结果，1为通过，2为失败
            if (!hostType) {
                LocalMySql.getInstance().saveApiTestResult(case_id, mark, result, api_name, expect_value1,
                        actual_value1, expect_value2, actual_value2, expect_value3, actual_value3, expect_value4,
                        actual_value4, expect_value5, actual_value5, params);// 写入数据库
            } else {
                MySqlOnline.getInstance().saveApiTestResult(case_id, mark, result, api_name, expect_value1,
                        actual_value1, expect_value2, actual_value2, expect_value3, actual_value3, expect_value4,
                        actual_value4, expect_value5, actual_value5, params);// 写入数据库
            }
        }
        if (!hostType) {// 统计本次运行所有用例结果，online不用统计
            LocalMySql.getInstance().addApiTestResult(api_name, mark, test_mark);
        } else {
            MySqlOnline.getInstance().addApiTestResult(api_name, mark, test_mark);
        }
    }

    /**
     * 添加一个case到数据库中
     *
     * @param url
     *            接口请求地址
     * @param textView
     *            请求的数据
     * @param verify
     *            验证点数组
     */
    protected void addCaseToMySql(String url, String textView, String[] verify) {
        if (verify.length % 2 == 1) {// 检查数据个数
            output("错误的参数个数！");
            return;
        }
        JSONObject jsonObject = getJsonFromTextView(urlDecoderText(textView));// 转化数据为jsonobject
        String one = url.substring(url.indexOf("."));
        String api_name = one.substring(one.indexOf("/"));
        LocalMySql.getInstance().addCaseFromDate(api_name, jsonObject, verify);// 插入数据库
    }

    /**
     * 封装获取请求的各种信息的方法
     *
     * @param httpRequestBase
     *            传入请求对象
     * @return 返回一个map，包含api_name,host_name,type
     */
    private Map<String, String> getRequestInfo(HttpRequestBase httpRequestBase) {
        Map<String, String> info = new HashMap<>();
        String url = httpRequestBase.getURI().toString();
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        String one = url.substring(url.indexOf("."));
        String api_name = one.substring(one.indexOf("/"));
        info.put("api_name", api_name);
        String two = url.substring(url.indexOf("//") + 2);
        String host_name = two.substring(0, two.indexOf("/"));
        info.put("host_name", host_name);
        String type = url.substring(0, url.indexOf("//") - 1);
        info.put("type", type);
        return info;
    }

    /**
     * 删除从数据库读取到的无用的数据
     *
     * @param apiCase
     *            从数据库读取到的用例数据
     */

    private void deleteMap(Map<String, String> apiCase) {
        apiCase.remove("api_name");// 此处兼容读取单个用例的时候获取api_name的情况
        apiCase.remove("depend_case");// 删除获取depend_case的情况
        apiCase.remove("case_id");// 删除获取depend_case的情况
        apiCase.remove("remark");// 删除获取depend_case的情况
        List<String> delete = new ArrayList<>();
        Set<String> keys = apiCase.keySet();
        for (String key : keys) {
            if (key.contains("verify_")) {
                delete.add(key);
            }
        }
        for (int i = 0; i < delete.size(); i++) {
            apiCase.remove(delete.get(i));
        }
    }

    /**
     * 执行单个用例的方法
     *
     * @param caseId
     *            用例ID
     * @return 返回相应的json数据
     */
    protected JSONObject executeCase(int caseId) {
        Map<String, String> use = LocalMySql.getInstance().getCaseFromMySql(caseId);// 数据库读取用例
        String api_name = use.get("api_name");
        String depend_case = use.get("depend_case");
        if (isInt(depend_case)) {
            if (depend_case.equals("55555")) {
                executeCaseNoDepend(caseId);
                executeCaseNoDepend(caseId);
                executeCaseNoDepend(caseId);
                executeCaseNoDepend(caseId);
                executeCaseNoDepend(caseId);
                executeCaseNoDepend(caseId);
            } else {
                executeCase(changeStringToInt(depend_case));
            }
        }
        JSONObject response = getJsonResponse(api_name, use);
        return response;
    }

    /**
     * 执行单挑用例，不执行依赖
     *
     * @param caseId
     *            用例id
     */
    protected void executeCaseNoDepend(int caseId) {
        Map<String, String> use = null;
        if (!hostType) {
            use = LocalMySql.getInstance().getCaseFromMySql(caseId);// 数据库读取用例
        } else {
            use = MySqlOnline.getInstance().getCaseFromMySql(caseId);// 数据库读取用例
        }
        String api_name = use.get("api_name");
        getJsonResponse(api_name, use);
    }

    /**
     * 执行一条用例，输出相应json，记录执行结果到数据库中
     *
     * @param caseId
     */
    protected void executeCaseAndOutputEntityAndSaveResult(int caseId, boolean entityKey) {
        Map<String, String> use = new HashMap<>();
        if (!hostType & casesType) {
            use = LocalMySql.getInstance().getCaseFromMySql(caseId);// 数据库读取用例
        } else {
            use = MySqlOnline.getInstance().getCaseFromMySql(caseId);
        }
        String api_name = use.get("api_name");
        String case_id = use.get("case_id");
        String depend_case = use.get("depend_case");
        if (isInt(depend_case)) {
            executeCase(changeStringToInt(depend_case));
        }
        String expect_value1 = use.get("verify_value1");// 获取验证点期望值
        String expect_value2 = use.get("verify_value2");// 获取验证点期望值
        String expect_value3 = use.get("verify_value3");// 获取验证点期望值
        String expect_value4 = use.get("verify_value4");// 获取验证点期望值
        String expect_value5 = use.get("verify_value5");// 获取验证点期望值
        String actual_key1 = use.get("verify_key1");// 获取检查点key
        String actual_key2 = use.get("verify_key2");// 获取检查点key
        String actual_key3 = use.get("verify_key3");// 获取检查点key
        String actual_key4 = use.get("verify_key4");// 获取检查点key
        String actual_key5 = use.get("verify_key5");// 获取检查点key
        String params = use.toString();// 记录传入参数
        JSONObject response = getJsonResponse(api_name, use);
        if (entityKey) {
            output("传入参数是：" + params);
            output("响应实体：");
            output(response);
        }
        List<String> lines = parseJsonLines(response);
        String actual_value1 = getValueFromJson(lines, actual_key1);// 获取验证点实际值
        String actual_value2 = getValueFromJson(lines, actual_key2);// 获取验证点实际值
        String actual_value3 = getDataContains(response, actual_key3, expect_value3);// 获取验证点实际值，此为包含验证
        String actual_value4 = getDateTypeIsNum(lines, actual_key4, expect_value4);// 获取验证点实际值，此为包含验证
        String actual_value5 = checkDataNotNull(lines, actual_key5, expect_value5);// 获取验证点实际值，此为包含验证
        String[] data2 = { expect_value1, actual_value1, expect_value2, actual_value2, expect_value3, actual_value3,
                expect_value4, actual_value4, expect_value5, actual_value5 };
        int result = getResult(data2) ? 1 : 2;// 获取测试结果，1为通过，2为失败
        output(data2);
        output("验证结果是：" + result);
        if (!hostType) {
            LocalMySql.getInstance().saveApiTestResult(case_id, mark, result, api_name, expect_value1, actual_value1,
                    expect_value2, actual_value2, expect_value3, actual_value3, expect_value4, actual_value4,
                    expect_value5, actual_value5, params);// 写入数据库
        } else {
            MySqlOnline.getInstance().saveApiTestResult(case_id, mark, result, api_name, expect_value1, actual_value1,
                    expect_value2, actual_value2, expect_value3, actual_value3, expect_value4, actual_value4,
                    expect_value5, actual_value5, params);// 写入数据库
        }
    }

    /**
     * 执行线上用例,记录执行结果到数据库中
     *
     * @param caseId
     */
    protected void executeOnlineCase(Map<String, String> use) {
        String api_name = use.get("api_name");
        String case_id = use.get("case_id");
        String depend_case = use.get("depend_case");
        if (isInt(depend_case)) {
            executeCase(changeStringToInt(depend_case));
        }
        String expect_value1 = use.get("verify_value1");// 获取验证点期望值
        String expect_value2 = use.get("verify_value2");// 获取验证点期望值
        String expect_value3 = use.get("verify_value3");// 获取验证点期望值
        String expect_value4 = use.get("verify_value4");// 获取验证点期望值
        String expect_value5 = use.get("verify_value5");// 获取验证点期望值
        String actual_key1 = use.get("verify_key1");// 获取检查点key
        String actual_key2 = use.get("verify_key2");// 获取检查点key
        String actual_key3 = use.get("verify_key3");// 获取检查点key
        String actual_key4 = use.get("verify_key4");// 获取检查点key
        String actual_key5 = use.get("verify_key5");// 获取检查点key
        String params = use.toString();// 记录传入参数
        JSONObject response = getJsonResponse(api_name, use);
        List<String> lines = parseJsonLines(response);
        String actual_value1 = getValueFromJson(lines, actual_key1);// 获取验证点实际值
        String actual_value2 = getValueFromJson(lines, actual_key2);// 获取验证点实际值
        String actual_value3 = getDataContains(response, actual_key3, expect_value3);// 获取验证点实际值，此为包含验证
        String actual_value4 = getDateTypeIsNum(lines, actual_key4, expect_value4);// 获取验证点实际值，此为包含验证
        String actual_value5 = checkDataNotNull(lines, actual_key5, expect_value5);// 获取验证点实际值，此为包含验证
        String[] data2 = { expect_value1, actual_value1, expect_value2, actual_value2, expect_value3, actual_value3,
                expect_value4, actual_value4, expect_value5, actual_value5 };
        int result = getResult(data2) ? 1 : 2;// 获取测试结果，1为通过，2为失败
        output(data2);
        output("验证结果是：" + result);
        if (!hostType) {
            LocalMySql.getInstance().saveApiTestResult(case_id, test_mark, result, api_name, expect_value1,
                    actual_value1, expect_value2, actual_value2, expect_value3, actual_value3, expect_value4,
                    actual_value4, expect_value5, actual_value5, params);// 写入数据库
        } else {
            MySqlOnline.getInstance().saveApiTestResult(case_id, test_mark, result, api_name, expect_value1,
                    actual_value1, expect_value2, actual_value2, expect_value3, actual_value3, expect_value4,
                    actual_value4, expect_value5, actual_value5, params);// 写入数据库
        }
    }

    /**
     * 多次执行一条用例
     *
     * @param caseId
     *            用例ID
     * @param times
     *            执行的次数
     */
    protected void executeCase(int caseId, int times) {
        Map<String, String> apiCase = LocalMySql.getInstance().getCaseFromMySql(caseId);// 数据库读取用例
        String api_name = apiCase.get("api_name");
        for (int i = 0; i < times; i++) {
            getJsonResponse(api_name, apiCase);
        }
    }

    /**
     * 执行map中接口的所有用例的方法
     *
     * @param apiGroup
     */
    protected void executeAllCases(Map<String, String> apiGroup) {
        Set<String> api_names = apiGroup.keySet();
        for (String api_name : api_names) {
            mark = getMark();
            executeCases(api_name);
        }
    }

    /**
     * 关闭httpclient，输出报告，默认路径在静态代码块中定义 一般为桌面或者当前工作目录下
     */
    protected void closeHttpClientAndOutputReport() {
        colsHttpClient();
        List<String[]> results = LocalMySql.getInstance().getApiTestResult(test_mark);// 获取所有用例运行结果
        WriteHtml.getInstance().createWebReport(results, path);// 生成html报告，默认路径为桌面
        LocalMySql.getInstance().mySqlOver();
    }

    /**
     * 关闭httpclient对象，输出报告
     *
     * @param path
     *            输出文件地址
     */
    protected void closeHttpClientAndOutputReport(String path) {
        colsHttpClient();
        if (hostType) {
            List<String[]> results = MySqlOnline.getInstance().getApiTestResult(test_mark);// 获取所有用例运行结果
            WriteHtml.getInstance().createWebReport(results, path);// 生成html报告
            MySqlOnline.getInstance().mySqlOver();
        } else {
            List<String[]> results = LocalMySql.getInstance().getApiTestResult(test_mark);// 获取所有用例运行结果
            WriteHtml.getInstance().createWebReport(results, path);// 生成html报告
            LocalMySql.getInstance().mySqlOver();
        }
    }

    /**
     * 关闭httpclient对象，输出报告
     *
     * @param path
     *            输出文件地址
     */
    protected void closeHttpClientAndClearUp() {
        colsHttpClient();
        MySqlOnline.getInstance().ClearUpOnlineData(test_mark);// 统计本次运行所有用例结果
        MySqlOnline.getInstance().mySqlOver();
    }

    public void colsHttpClient() {
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}