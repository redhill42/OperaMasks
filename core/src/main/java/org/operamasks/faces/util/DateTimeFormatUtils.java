package org.operamasks.faces.util;

import java.util.Arrays;

import javax.faces.convert.Converter;
import javax.faces.convert.DateTimeConverter;

import org.operamasks.faces.binding.factories.DelegatingDateTimeConverter;
import org.operamasks.faces.binding.impl.ConverterAdapter;

public class DateTimeFormatUtils {
    public static final String DEFAUTL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAUTL_TIMEZONE = "GMT+8";
    public static final char SERVER_ESCAPED = '\\';
    public static final char CLIENT_ESCAPED = '\\';
    
    private static char[] CLIENT_DATE_PATTERNS = new char[] {
        'd', 'D', 'j', 'l', 'S', 'w', 'z', 'W', 'F', 'm', 'M', 'n', 
        'Y', 'y', 'A', 'g', 'G', 'h', 'H', 'i', 's', 'O'
    };
    
    private static String[] SERVER_DATE_PATTERNS = new String[] {
        "dd", "EEE", "d", "EEEE", "'th'", "E", "D", "w", "MMMM", "MM", "MMM", "M",
        "yyyy", "yy", "a", "h", "k", "hh", "kk", "mm", "ss", "Z"
    };
    
    //此列表包含SERVER_DATE_PATTERNS列表中只有一个字母的pattern，用户若要使用这些字母时，需要加上反除号转义符(\)
    private static char[] ESCAPE_NEEDED_SERVER_PATTERNS = new char[] {
        'd', 'E', 'D', 'w', 'M', 'a', 'h', 'k', 'Z'
    };
    
    //Patterns in EXT2 that is not supported on server side
    private static char[] NOT_SUPPORTED_CLIENT_PATTERNS = new char[] {
        't', 'N', 'L', 'o', 'a', 'u', 'P', 'T', 'Z', 'c', 'U'
    };
    
    {
        Arrays.sort(CLIENT_DATE_PATTERNS);
        Arrays.sort(ESCAPE_NEEDED_SERVER_PATTERNS);
        Arrays.sort(NOT_SUPPORTED_CLIENT_PATTERNS);
    }
    
    public static String converToClientFormat(String serverFormat) {
        StringBuffer clientFormat = new StringBuffer();
        int pos = 0;
        while (pos < serverFormat.length()) {
            char c = serverFormat.charAt(pos);
            //若使用了服务器端转义符
            if (c == SERVER_ESCAPED && pos < serverFormat.length() - 1) {
                pos++;
                char escaped = serverFormat.charAt(pos);
                if (charInList(escaped, ESCAPE_NEEDED_SERVER_PATTERNS) || escaped == SERVER_ESCAPED) {
                    if (charInList(escaped, CLIENT_DATE_PATTERNS, NOT_SUPPORTED_CLIENT_PATTERNS)) {
                        //被转义的字符同时是服务器端与客户端使用的pattern，需要加上客户端转义
                        clientFormat.append(CLIENT_ESCAPED).append(escaped);
                    } else {
                        //被转义的字符只是服务器端使用的pattern，可直接转换
                        clientFormat.append(escaped);
                    }
                } else {
                    //被转义的字符不是服务器端使用的pattern，不作任何转换
                    clientFormat.append(SERVER_ESCAPED).append(escaped);
                }
                pos++;
                continue;
            }
            
            //若是服务器端pattern
            int matched = bestMatchedPattern(serverFormat, pos);
            if (matched >= 0) {
                clientFormat.append(CLIENT_DATE_PATTERNS[matched]);
                pos += SERVER_DATE_PATTERNS[matched].length();
                continue;
            }

            //若字符不是服务器端使用的pattern，但是客户端使用的pattern，需要加上客户端转义符
            if (charInList(c, CLIENT_DATE_PATTERNS, NOT_SUPPORTED_CLIENT_PATTERNS)) {
                clientFormat.append(CLIENT_ESCAPED);
            }
            
            clientFormat.append(serverFormat.charAt(pos));
            pos++;
        }
        return clientFormat.toString();
    }
    
    //lists must be sorted before calling this method
    private static boolean charInList(char c, char[]... lists) {
        for (char[] list : lists) {
            if (Arrays.binarySearch(list, c) != -1) return true;
        }
        return false;
    }
    
    private static int bestMatchedPattern(String source, int pos) {
        int len = 0;
        int matched = -1;
        for (int i = 0; i < SERVER_DATE_PATTERNS.length; i++) {
            int pLen = SERVER_DATE_PATTERNS[i].length();
            if (len < pLen && pos + pLen <= source.length()) {
                String candidate =  source.substring(pos, pos + pLen);
                if (SERVER_DATE_PATTERNS[i].equals(candidate)) {
                    matched = i;
                    len = pLen;
                }
            }
        }
        return matched;
    }
    
    public static String getPattenFromConverter(Converter converter){
        if(converter instanceof DateTimeConverter){
            DateTimeConverter dc = (DateTimeConverter)converter;
            return dc.getPattern();
        }
        if(converter instanceof ConverterAdapter){
            ConverterAdapter ca = (ConverterAdapter)converter;
            DelegatingDateTimeConverter dtc = (DelegatingDateTimeConverter)(ca.getFallback());
            if(dtc != null){
                return dtc.getPattern();
            }
        }
        return null;
    }
}
