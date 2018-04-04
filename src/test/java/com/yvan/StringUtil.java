package com.yvan;

import java.text.SimpleDateFormat;

/**
 * String工具类
 */
public class StringUtil {

    /**
     * 返回字符串的副本，忽略左侧空字符
     *
     * @param str
     * @return
     */
    public String lTrim(String str) {
        char[] val = str.toCharArray();
        int len = val.length;
        int st = 0;

        while ((st < len) && (val[st] <= ' ')) {
            st++;
        }
        return (st > 0) ? str.substring(st, len) : str;
    }

    /**
     * 返回字符串的副本，忽略右侧空字符
     *
     * @param str
     * @return
     */
    public String rTrim(String str) {
        char[] val = str.toCharArray();
        int len = val.length;
        int st = 0;

        while ((st < len) && (val[len - 1] <= ' ')) {
            len--;
        }
        return ((len < val.length)) ? str.substring(st, len) : str;
    }

    /**
     * 生成指定个数的空格
     *
     * @param num
     * @return
     */
    public static String space(int num) {
        return produceStr(" ", num);
    }

    /**
     * 生成指定个数的字符
     *
     * @param num
     * @param str
     * @return
     */
    public static String produceStr(String str, int num) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++) {
            builder.append(str);
        }
        return builder.toString();
    }

    /**
     * 根据字符串宽度右侧补空格，全角占两个宽度
     *
     * @param str    原字符串
     * @param length 需要字符串的总长度，全角占两个长度
     * @return
     */
    public static String addSpace(String str, int length) {
        return str + space(length - getLength(str));
    }

    /**
     * 返回字符串长度半角占1个长度，全角占两个长度
     *
     * @param str
     * @return
     */
    public static int getLength(String str) {
        if (str == null) {
            throw new NullPointerException("参数不能为空");
        }
        int tmpLeng = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.substring(i, i + 1).getBytes().length == 1) {
                tmpLeng++;
            } else {
                tmpLeng += 2;
            }
        }
        return tmpLeng;
    }

    public static int getLength2(Object obj) {
        if (obj == null) {
            return 0;
        }
        return obj.toString().length();
    }

    /**
     * 将字符串指定位置替换为新字符
     *
     * @param str
     * @param index
     * @param replacement
     * @return
     */
    public static String replace(String str, int index, char replacement) {
        if (index < 0) {
            throw new StringIndexOutOfBoundsException(index);
        }
        if (index > str.length()) {
            throw new StringIndexOutOfBoundsException(index);
        }
        char[] chars = str.toCharArray();
        chars[index] = replacement;
        return new String(chars);
    }

    /**
     * 验证18位身份证号
     *
     * @param idNum 18位身份证号
     */
    public static boolean CheckIDCard18(String idNum) {
        long n = 0;
        try {
            n = Long.parseLong(idNum.substring(0, 17));
            if (n < Math.pow(10, 16)) {
                return false;// 数字验证
            }
            n = Long.parseLong(idNum.replace("x", "0").replace("X", "0"));
        } catch (Exception ex) {
            return false;
        }

        String address = "11x22x35x44x53x12x23x36x45x54x13x31x37x46x61x14x32" + "x41x50x62x15x33x42x51x63x21x34x43x52x64x65x71x81x82x91";
        if (!address.contains(idNum.substring(0, 2))) {
            return false;// 省份验证
        }
        String birth = idNum.substring(6, 10) + "-" + idNum.substring(10, 12) + "-" + idNum.substring(12, 14);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.parse(birth);
        } catch (Exception ex) {
            return false;// 生日验证
        }
        String[] arrVarifyCode = ("1,0,x,9,8,7,6,5,4,3,2").split(",");
        String[] Wi = ("7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2").split(",");
        char[] Ai = idNum.substring(0, 17).toCharArray();
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += Integer.parseInt(Wi[i]) * Integer.parseInt(String.valueOf(Ai[i]));
        }
        int y = -1;
        y = sum % 11;
        return arrVarifyCode[y].equals(idNum.substring(17).toLowerCase());
    }

    /**
     * return obj == null ? null : obj.toString();
     *
     * @param obj
     * @return
     */
    public static String valueOfNull(Object obj) {
        return obj == null ? null : obj.toString();
    }

    /**
     * return obj == null ? "" : obj.toString();
     *
     * @param obj
     * @return
     */
    public static String valueOfEmpty(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /**
     * 返回指定子字符串在此字符串中第n此出现的位置<br>
     * 不存在返回-1<br>
     * 示例<br>
     * 123, 12, 1 = 0<br>
     * 1123， 12， 1 = 1<br>
     * 1123， 12， 2 = -1<br>
     *
     * @param
     * @param number 第几次出现，第一次值为1
     * @return
     */
    public static int indexOfNum(String str1, String str2, int number) {
        String value = new String(str1);
        int indexSum = 0;
        for (int i = 0; i < number; i++) {
            int index = value.indexOf(str2);
            if (index == -1) {
                return -1;
            }
            indexSum += index;
            if (i != 0) {
                indexSum++;
            }
            value = value.substring(index + 1);
        }
        return indexSum;
    }
}
