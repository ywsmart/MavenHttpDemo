package com.yvan.util;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 属性文件工具类
 *
 * @author hongjun.hu
 */
public class PropertiesUtil {
    private Properties properties;
    private String fileName;

    /**
     * @param fileName webRoot后的文件名，不包含扩展名
     * @throws IOException
     */
    public PropertiesUtil(String fileName) throws IOException {
        this.fileName = fileName;
        FileInputStream in = new FileInputStream(fileName);
        properties = new Properties();
        properties.load(in);
        in.close();
    }

    public Object get(String str) {
        return properties.get(str);
    }

    public String getString(String str) {
        return StringUtil.valueOfNull(properties.get(str));
    }

    public int getInt(String str) throws NumberFormatException {
        return Integer.parseInt(getString(str));
    }



    /**
     * 加载指定路径的属性文件
     *
     * @throws IOException
     */
    public static Properties load(String fileName) throws IOException {
        return load(fileName, System.getProperty("file.encoding"));
    }

    public static Properties loadUtf8(String fileName) throws IOException {
        return load(fileName, "utf-8");
    }

    public static Properties loadProperties(String fileName) throws IOException {
        return load(fileName + ".properties");
    }

    public static Properties loadPropertiesUtf8(String fileName) throws IOException {
        return load(fileName + ".properties", "utf-8");
    }

    public static Properties load(String fileName, String charsetName) throws IOException {
        InputStreamReader in = new InputStreamReader(new FileInputStream(fileName), charsetName);
        Properties properties = new Properties();
        properties.load(in);
        in.close();
        return properties;
    }

    public static Properties load(File file, String charsetName) throws IOException {
        InputStreamReader in = new InputStreamReader(new FileInputStream(file), charsetName);
        Properties properties = new Properties();
        properties.load(in);
        in.close();
        return properties;
    }

    public static Properties load(File file) throws IOException {
        return load(file, System.getProperty("file.encoding"));
    }

    public static Properties loadUtf8(File file) throws IOException {
        return load(file, "utf-8");
    }

    /**
     * 覆盖写入数据
     *
     * @param map
     * @throws IOException
     */
    public void writeProperties(Map<String, Object> map) throws IOException {
        writeProperties(fileName, map);
    }

    /**
     * 向指定路径的属性文件覆盖写数据
     *
     * @param filePath
     * @param map
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    public void writeProperties(String filePath, Map<String, Object> map) throws IOException {
        File file = new File(filePath);
        FileOutputStream fos = new FileOutputStream(file);

        Set set = map.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            String value = map.get(key).toString();
            fos.write((key + "=" + value).getBytes());
            if (iterator.hasNext()) {
                fos.write("\n".getBytes());
            }
        }

        fos.flush();
        fos.close();
    }

    /**
     * 覆盖写入数据
     *
     * @param key
     * @param value
     * @throws IOException
     */
    public void writeProperties(String key, String value) throws IOException {
        writeProperties(fileName, key, value);
    }

    /**
     * 向指定路径的属性文件覆盖写数据
     *
     * @param filePath
     * @param key
     * @param value
     * @throws IOException
     */
    public void writeProperties(String filePath, String key, String value) throws IOException {
        File file = new File(filePath);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write((key + "=" + value).getBytes());
        fos.flush();
        fos.close();
    }

    /**
     * 向配置文件中替换写入数据，但数据位置会随机排放，注释会被删除
     *
     * @param key
     * @param value
     */
    public boolean setProperties(String key, Object value) {
        return setProperties(fileName, key, value);
    }

    /**
     * 向配置文件中替换写入数据，但数据位置会随机排放，注释会被删除
     *
     * @param filePath
     * @param key
     * @param value
     * @return
     */
    public boolean setProperties(String filePath, String key, Object value) {
        properties.put(key, StringUtil.valueOfEmpty(value));
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            properties.store(fos, null);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

}
