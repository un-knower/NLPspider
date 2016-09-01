package dmir.nlp.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Richard on 2016-08-17.
 */
public class ParseUtil {

    public ParseUtil(){}


    /**
     *
     * @param str
     *            待匹配字符串,返回group（0），则正则表达式必须加括号
     * @param regex
     *            正则表达式
     * @return 默认返回group（1）
     */
    public static String matchStringGroup1(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String res = matcher.group(1);
            if (res != null && res != "")
                return res;
            else
                continue;
        }
        return null;
    }
}
