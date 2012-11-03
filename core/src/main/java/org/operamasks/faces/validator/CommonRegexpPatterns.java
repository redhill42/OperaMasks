package org.operamasks.faces.validator;

public class CommonRegexpPatterns {
    /**
     * Email地址
     */
    public static final String EMAIL_ADDRESS = "^[A-Za-z0-9_]+(?:[.-][A-Za-z0-9_]+)*@[A-Za-z0-9]+(?:[.-][A-Za-z0-9]+)*\\.[A-Za-z]{2,5}$";
    
    /**
     * IP地址
     */
    public static final String IP_ADDRESS = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    
    /**
     * 货币,允许带,千位符, 可选具有两位小数
     */
    public static final String CURRENCY = "^[0-9]{1,3}(?:,?[0-9]{3})*(?:\\.[0-9]{2})?$";
    
    /**
     * 浮点数,允许科学记数法
     */
    public static final String FLOAT_NUMBER = "^[-+]?[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?$";
    
    /**
     * 整数
     */
    public static final String INTEGER_NUMBER = "^[-+]?\\d+$";
    
    /**
     * 网址
     */
    public static final String URL = "^(https?|ftp|file)://[-A-Z0-9+&@#/%?=~_|!:,.;]*[-A-Z0-9+&@#/%=~_|]$";
    
    /**
     * 身份证号码,15位或18位
     */
    public static final String IDENTITY_CARD = "\\d{15}|\\d{18}";
    
    /**
     * 中文字符
     */
    public static final String CHINESE_CHARACTER = "[\u4e00-\u9fa5]+";
    
    /**
     * 信用卡
     */
    public static final String CREDITCARD_GENERAL = "^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|6011[0-9]{14}|3(?:0[0-5]|[68][0-9])[0-9]{11}|3[47][0-9]{13})$";

}
