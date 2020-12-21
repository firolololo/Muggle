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

    public static void main(String[] args) {
        String test = "adhkfashdjkfhskjdfhsdjfhasdfhlaksdjflasjdflksdjf";
        System.out.println(paddingStringToLines(test, 36, 3));
    }
}
