package com.stellar.muggle.invoke;

import java.io.File;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/21 16:20
 */
public class MuggleStringInvoke {
    // 分隔符
    private static String SPLIT_SIGN = "\n";
    static String paddingStringToLines(String str, int len, int lines) {
        int index = 0;
        int cur = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while (lines > 1) {
            if (cur < str.length()) {
                stringBuilder.append(str.charAt(cur));
                index = chinesePredicate(str.charAt(cur)) ? index + 2 : index + 1;
            } else {
                stringBuilder.append(" ");
                index += 1;
            }
            cur++;
            if (index >= len) {
                lines--;
                index = 0;
                stringBuilder.append(SPLIT_SIGN);
            }
        }
        return stringBuilder.toString();
    }

     // 判断是否是中文
    static boolean chinesePredicate(char c) {
        return Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN;
    }

    private static final String[] NUMBERS = {"零","壹","贰","叁","肆","伍","陆","柒","捌","玖"};
    private static final String[] IUNIT = {"元","拾","佰","仟","万","拾","佰","仟","亿","拾","佰","仟","万","拾","佰","仟"};
    private static final String[] DUNIT = {"角","分","厘"};

    public static String toChinese(String str) {
        // 判断输入的金额字符串是否符合要求
        if (!str.matches("(-)?[\\d]*(.)?[\\d]*")) {
            return "抱歉，请输入数字！";
        }

        if("0".equals(str) || "0.00".equals(str) || "0.0".equals(str)) {
            return "零元";
        }

        // 判断金额数字中是否存在负号"-"
        boolean flag = false;
        if(str.startsWith("-")){
            // 标志位，标志此金额数字为负数
            flag = true;
            str = str.replaceAll("-", "");
        }

        // 去掉金额数字中的逗号","
        str = str.replaceAll(",", "");
        String integerStr;//整数部分数字
        String decimalStr;//小数部分数字

        // 初始化：分离整数部分和小数部分
        if(str.indexOf(".")>0) {
            integerStr = str.substring(0,str.indexOf("."));
            decimalStr = str.substring(str.indexOf(".") + 1);
        }else if(str.indexOf(".")==0) {
            integerStr = "";
            decimalStr = str.substring(1);
        }else {
            integerStr = str;
            decimalStr = "";
        }

        // beyond超出计算能力，直接返回
        if(integerStr.length()>IUNIT.length) {
            return "超出计算能力！";
        }

        // 整数部分数字
        int[] integers = toIntArray(integerStr);
        // 判断整数部分是否存在输入012的情况
        if (integers.length>1 && integers[0] == 0) {
            return "抱歉，输入数字不符合要求！";
        }
        // 设置万单位
        boolean isWan = isWan5(integerStr);
        // 小数部分数字
        int[] decimals = toIntArray(decimalStr);
        // 返回最终的大写金额
        String result = getChineseInteger(integers, isWan) + getChineseDecimal(decimals);
        if(flag){
            // 如果是负数，加上"负"
            return "负" + result;
        }else{
            return result;
        }
    }

    private static boolean isWan5(String integerStr) {
        int length = integerStr.length();
        if(length > 4) {
            String subInteger = "";
            if(length > 8) {
                subInteger = integerStr.substring(length- 8,length -4);
            }else {
                subInteger = integerStr.substring(0,length - 4);
            }
            return Integer.parseInt(subInteger) > 0;
        }else {
            return false;
        }
    }

    private static int[] toIntArray(String number) {
        int[] array = new int[number.length()];
        for(int i = 0;i<number.length();i++) {
            array[i] = Integer.parseInt(number.substring(i,i+1));
        }
        return array;
    }

    // 小数转大写金额
    static String getChineseDecimal(int[] decimals) {
        StringBuilder chineseDecimal = new StringBuilder();
        for (int i = 0; i < decimals.length; i++) {
            if (i == 3) {
                break;
            }
            if (decimals[i] > 0) {
                chineseDecimal.append((i - 1 >= 0 && decimals[i - 1] == 0) ? NUMBERS[0] + NUMBERS[decimals[i]] + DUNIT[i] : NUMBERS[decimals[i]] + DUNIT[i]);
            }
        }
        return chineseDecimal.toString();
    }

    public static String getChineseInteger(int[] integers,boolean isWan) {
        StringBuffer chineseInteger = new StringBuffer("");
        int length = integers.length;
        if (length == 1 && integers[0] == 0) {
            return "";
        }
        for(int i=0; i<length; i++) {
            String key = "";
            if(integers[i] == 0) {
                if((length - i) == 13)//万（亿）
                    key = IUNIT[4];
                else if((length - i) == 9) {//亿
                    key = IUNIT[8];
                }else if((length - i) == 5 && isWan) {//万
                    key = IUNIT[4];
                }else if((length - i) == 1) {//元
                    key = IUNIT[0];
                }
                if((length - i)>1 && integers[i+1]!=0) {
                    key += NUMBERS[0];
                }
            }
            chineseInteger.append(integers[i]==0?key:(NUMBERS[integers[i]]+IUNIT[length - i -1]));
        }
        return chineseInteger.toString();
    }

    public static void main(String[] args) {
        String test = "7.0";
        System.out.println(toChinese(test));
    }
}
