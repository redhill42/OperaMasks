/*
 * $Id: DateTimeConverter.java,v 1.8 2008/01/19 04:09:34 lishaochuan Exp $
 *
 * Copyright (C) 2006 Operamasks Community.
 * Copyright (C) 2000-2006 Apusic Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */

package org.operamasks.faces.convert;

import static org.operamasks.resources.Resources.JSF_DATETIME_CONVERTER;
import static org.operamasks.resources.Resources._T;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;

import org.operamasks.faces.validator.ClientValidator;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.faces.util.FacesUtils;
import static org.operamasks.resources.Resources.*;

public class DateTimeConverter extends javax.faces.convert.DateTimeConverter
    implements ClientValidator
{
	public DateTimeConverter(){
		TimeZone initTimeZone = FacesUtils.getInitTimeZone();
		
		if (initTimeZone != null)
			super.setTimeZone(initTimeZone);
	}
	
    public String getValidatorScript(FacesContext context, UIComponent component) {
        return null;
    }

    public String getValidatorInstanceScript(FacesContext context, UIComponent component) {
        try {
            Locale locale = this.getLocale();
            if (locale == null) {
                locale = context.getViewRoot().getLocale();
            }

            SimpleDateFormat format = getDateFormat(locale);
            if (format == null) {
                return null;
            }

            Compiler c = new Compiler(format.toPattern(), format.getDateFormatSymbols());
            c.compile();

            String message = (String)component.getAttributes().get("converterMessage");
            if (message == null) {
                message = _T(JSF_DATETIME_CONVERTER,
                             FacesUtils.getLabel(context, component),
                             format.toPattern());
            }

            return "new ClientValidator('" +
                   component.getClientId(context) + "'," +
                   HtmlEncoder.enquote(message, '\'') + ",'" +
                   FacesUtils.getMessageComponentId(context, component) + "'," +
                   "function(value){" + c.generate() + "})";

        } catch (Exception ex) {
            // fall back to server side validation
            return null;
        }
    }

    /**
     * Return a <code>SimpleDateFormat</code> instance to use for formating
     * and parsing in this <code>Converter</code>.
     */
    private SimpleDateFormat getDateFormat(Locale locale) {
        String pattern = this.getPattern();
        String type = this.getType();

        if (pattern == null && type == null) {
            return null;
        }

        int dateStyle = getStyle(this.getDateStyle());
        int timeStyle = getStyle(this.getTimeStyle());

        DateFormat df;
        if (pattern != null) {
            df = new SimpleDateFormat(pattern, locale);
        } else if (type.equals("both")) {
            df = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
        } else if (type.equals("date")) {
            df = DateFormat.getDateInstance(dateStyle, locale);
        } else if (type.equals("time")) {
            df = DateFormat.getTimeInstance(timeStyle, locale);
        } else {
            return null;
        }
        if (df instanceof SimpleDateFormat) {
            df.setLenient(false);
            return ((SimpleDateFormat)df);
        }
        return null;
    }

    private static int getStyle(String name) {
        if ("default".equals(name)) {
            return DateFormat.DEFAULT;
        } else if ("short".equals(name)) {
            return DateFormat.SHORT;
        } else if ("medium".equals(name)) {
            return DateFormat.MEDIUM;
        } else if ("long".equals(name)) {
            return DateFormat.LONG;
        } else if ("full".equals(name)) {
            return DateFormat.FULL;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static class Compiler {
        private String pattern;
        private DateFormatSymbols symbols;

        private StringBuilder output;
        private int yearPlace, yearDigits;
        private int monthPlace, monthDigits;
        private int datePlace, dateDigits;
        private int lastPlace;

        public Compiler(String pattern, DateFormatSymbols symbols) {
            this.pattern = pattern;
            this.symbols = symbols;
        }

        private void initialize() {
            output = new StringBuilder();
            yearPlace = monthPlace = datePlace = -1;
            yearDigits = monthDigits = dateDigits = 0;
            lastPlace = 0;
        }

        public void compile() {
            initialize();

            int length = pattern.length();
            boolean inQuote = false;
            int count = 0;
            int lastTag = -1;

            for (int i = 0; i < length; i++) {
                char c = pattern.charAt(i);

                if (c == '\'') {
                    // '' is treated as a single quote regardless of being
                    // in a quoted section.
                    if ((i + 1) < length) {
                        c = pattern.charAt(i + 1);
                        if (c == '\'') {
                            i++;
                            if (count != 0) {
                                encode(lastTag, count);
                                lastTag = -1;
                                count = 0;
                            }
                            output.append("\\'");
                            continue;
                        }
                    }
                    if (!inQuote) {
                        if (count != 0) {
                            encode(lastTag, count);
                            lastTag = -1;
                            count = 0;
                        }
                        inQuote = true;
                    } else {
                        inQuote = false;
                    }
                    continue;
                }
                if (inQuote) {
                    encodeChar(c);
                    continue;
                }
                if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')) {
                    if (count != 0) {
                        encode(lastTag, count);
                        lastTag = -1;
                        count = 0;
                    }
                    encodeChar(c);
                    continue;
                }

                int tag = c;
                if (lastTag == -1 || lastTag == tag) {
                    lastTag = tag;
                    count++;
                    continue;
                }
                encode(lastTag, count);
                lastTag = tag;
                count = 1;
            }

            if (count != 0) {
                encode(lastTag, count);
            }
        }

        private void encodeChar(char c) {
            switch (c) {
            case '/':
            case '\\':
            case '^':
            case '$':
            case '*':
            case '+':
            case '?':
            case '.':
            case '(':
            case ')':
            case '|':
            case '{':
            case '}':
            case '[':
            case ']':
                output.append('\\').append(c);
                break;

            case ' ': case '\t':
                output.append("\\s+");
                break;

            default:
                output.append(c);
                break;
            }
        }

        private void encode(int tag, int count) {
            switch (tag) {
            case 'G': // AD|BC
                String[] eras = symbols.getEras();
                output.append("(?:");
                output.append(eras[0]);
                output.append('|');
                output.append(eras[1]);
                output.append(")");
                break;

            case 'y': // 1996; 96
                yearPlace = ++lastPlace;
                yearDigits = count;
                output.append("(\\d{1,4})");
                break;

            case 'M': // July; Jul; 07
                monthPlace = ++lastPlace;
                monthDigits = count;
                if (count <= 2) {
                    output.append("(0?[1-9]|1[0-2])");
                } else {
                    String[] months = symbols.getShortMonths();
                    output.append('(');
                    output.append(months[0]);
                    for (int i = 1; i < 12; i++) {
                        output.append('|');
                        output.append(months[i]);
                    }
                    months = symbols.getMonths();
                    for (int i = 0; i < 12; i++) {
                        output.append('|');
                        output.append(months[i]);
                    }
                    output.append(')');
                }
                break;

            case 'w': // 27
                output.append("\\d{1,2}");
                break;

            case 'W': // 2
                output.append("\\d");
                break;

            case 'D': // 189
                output.append("\\d{1,3}");
                break;

            case 'd': // 10
                datePlace = ++lastPlace;
                dateDigits = count;
                output.append("(\\d{1,2})");
                break;

            case 'F': // 2
                output.append("\\d{1,2}");
                break;

            case 'E': // Tuesday; Tue
                String[] weakdays = symbols.getShortWeekdays();
                output.append("(?:");
                output.append(weakdays[0]);
                for (int i = 1; i < 7; i++) {
                    output.append('|');
                    output.append(weakdays[i]);
                }
                weakdays = symbols.getWeekdays();
                for (int i = 0; i < 7; i++) {
                    output.append('|');
                    output.append(weakdays[i]);
                }
                output.append(")");
                break;

            case 'a': // AM|PM
                String[] ampm = symbols.getAmPmStrings();
                output.append("(?:");
                output.append(ampm[0]);
                output.append('|');
                output.append(ampm[1]);
                output.append(")");
                break;

            case 'H': // 0-23
                output.append("(?:[01]?[0-9]|2[0-3])");
                break;

            case 'k': // 1-24
                output.append("(?:0?[1-9]|1[0-9]|2[0-4])");
                break;

            case 'K': // 0-11
                output.append("(?:0?[0-9]|1[01])");
                break;

            case 'h': // 1-12
                output.append("(?:0?[1-9]|1[012])");
                break;

            case 'm': // 0-59
            case 's':
                output.append("(?:[0-5]?[0-9])");
                break;

            case 'S':
                output.append("\\d{1,3}");
                break;

            case 'z':
                output.append("(?:\\w{3}|GMT[-+](?:[01]?[0-9]|2[0-3]):[0-5][0-9])");
                break;

            case 'Z':
                output.append("[-+](?:[01][0-9]|2[0-3])[0-5][0-9]");
                break;
            }
        }

        public String generate() {
            StringBuilder code = new StringBuilder();

            code.append("value=value.trim();");
            code.append("if(value.length==0)return true;");
            
            code.append("var r=value.match(/^").append(output).append("$/);");
            code.append("if(r==null)return false;");

            if (yearPlace != -1 && monthPlace != -1 && datePlace != -1) {
                String yearVal  = "r[" + yearPlace + "]";
                String monthVal = "r[" + monthPlace + "]";
                String dateVal  = "r[" + datePlace + "]";

                code.append("var y=parseInt(").append(yearVal).append(");");

                if (yearDigits <= 2) {
                    // handle two digits year number
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                    calendar.setTime(new Date());
                    calendar.add(Calendar.YEAR, -80);
                    int centuryStartYear = calendar.get(Calendar.YEAR);

                    code.append("if(").append(yearVal).append(".length<=2)");
                    code.append("y+=").append(centuryStartYear/100*100);
                    code.append("+(y<").append(centuryStartYear%100).append("?100:0);");
                }

                if (monthDigits > 2) {
                    String[] months = symbols.getShortMonths();
                    code.append("var m=[");
                    for (int i = 0; i < 12; i++) {
                        if (i != 0) code.append(',');
                        code.append(HtmlEncoder.enquote(months[i], '\''));
                    }
                    code.append("].indexOf(").append(monthVal).append(")+1;");

                    months = symbols.getMonths();
                    code.append("if(m==-1)");
                    code.append("m=[");
                    for (int i = 0; i < 12; i++) {
                        if (i != 0) code.append(',');
                        code.append(HtmlEncoder.enquote(months[i], '\''));
                    }
                    code.append("].indexOf(").append(monthVal).append(")+1;");
                    code.append("if(m==-1)return false;");
                } else {
                    code.append("var m=parseInt(").append(monthVal).append(");");
                }

                code.append("var d=parseInt(").append(dateVal).append(");");

                code.append("switch(m){");
                code.append("case 2:if(d>((y%4==0&&(y%100!=0||y%400==0))?29:28))return false;break;");
                code.append("case 1:case 3:case 5:case 7:case 8:case 10:case 12:");
                code.append("if(d>31)return false;break;");
                code.append("default:if(d>30)return false;break;");
                code.append("}");
            }

            code.append("return true;");

            return code.toString();
        }
    }
}
