package github.charon.common.utils;

public class StringUtil {
    public static boolean isBlank(String s) {
        if (null == s || s.length() == 0) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
