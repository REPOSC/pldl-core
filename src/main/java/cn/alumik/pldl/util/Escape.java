package cn.alumik.pldl.util;

public class Escape {

    public static String unescapeChar(Character escapedChar) {
        String unescapedChar = escapedChar.toString();
        switch (escapedChar) {
            case '\t':
                unescapedChar = "\\\\t";
                break;
            case '\r':
                unescapedChar = "\\\\r";
                break;
            case '\n':
                unescapedChar = "\\\\n";
                break;
            case '\f':
                unescapedChar = "\\\\f";
                break;
            case '\\':
                unescapedChar = "\\\\";
                break;
            case '\0':
                unescapedChar = "null";
                break;
        }
        return unescapedChar;
    }
}
