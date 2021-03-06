package com.kmwlyy.registry.bean;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016-8-17.
 */
public class IdCard {
    /**
     * 方法名：parseGender
     * 详述：根据所传身份证号解析其性别
     *
     * @param 说明参数含义
     * @return 说明返回值含义
     * @throw 说明发生此异常的条件
     */
    public static String parseGender(String cid) {
        if (TextUtils.isEmpty(cid)) {
            return "";
        }
        String gender = null;
        char c = cid.charAt(cid.length() - 2);
        int sex = Integer.parseInt(String.valueOf(c));
        if (sex % 2 == 0) {
            gender = "1";//女
        } else {
            gender = "0";//男
        }
        return gender;
    }

    /**
     * 方法名：parseBirthday
     * 详述：根据身份证号截取出生日期
     *
     * @param 说明参数含义
     * @return 说明返回值含义
     * @throw 说明发生此异常的条件
     */
    public static String parseBirthday(String cid) {
        if (TextUtils.isEmpty(cid)) {
            return "";
        }
        //通过身份证号来读取出生日期
        String birthday = "";
        //如果没有身份证，那么不进行字符串截取工作。
        if (checkCardId(cid)) {
            String year = cid.substring(6, 10);
            String month = cid.substring(10, 12);
            String day = cid.substring(12, 14);
            birthday = year + "-" + month + "-" + day;
        }
        return birthday;
    }

    /**
     * 方法名：parseAge
     * 详述：根据身份证号码，返回年龄
     *
     * @param 说明参数含义
     * @return 说明返回值含义
     * @throw 说明发生此异常的条件
     */
    public static int parseAge(String cid) {
        int age = 0;
        String birthDayStr = cid.substring(6, 14);
        Date birthDay = null;
        try {
            birthDay = new SimpleDateFormat("yyyyMMdd").parse(birthDayStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) {
            throw new IllegalArgumentException("您还没有出生么？");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayBirth = cal.get(Calendar.DAY_OF_MONTH);
        age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth && dayNow < dayBirth) {
                age--;
            }
        } else {
            age--;
        }
        return age;
    }

    /**
     * 校验规则：
     * 1、将前面的身份证号码17位数分别乘以不同的系数。第i位对应的数为[2^(18-i)]mod11。从第一位到第十七位的系数分别为：7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2 ；
     * 2、将这17位数字和系数相乘的结果相加；
     * 3、用加出来和除以11，看余数是多少？；
     * 4、余数只可能有0 1 2 3 4 5 6 7 8 9 10这11个数字。其分别对应的最后一位身份证的号码为1 0 X 9 8 7 6 5 4 3 2；
     *
     * @return 返回false说明，身份证号码不符合规则，返回true说明身份证号码符合规则
     */
    public static boolean checkCardId(String cid) {
        boolean flag = false;
        int len = cid.length();
        int kx = 0;
        for (int i = 0; i < len - 1; i++) {
            int x = Integer.parseInt(String.valueOf(cid.charAt(i)));
            int k = 1;
            for (int j = 1; j < 18 - i; j++) {
                k *= 2;
            }
            kx += k * x;
        }
        int mod = kx % 11;
        int[] mods = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Character[] checkMods = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        for (int i = 0; i < 11; i++) {
            if (mod == mods[i]) {
                Character lastCode = cid.charAt(len - 1);
                if (checkMods[i].equals(lastCode)) {
                    flag = true;
                }
            }
        }
        return flag;
    }
}