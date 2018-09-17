package ru.unpro.apktool.util;

import android.text.*;

public class TextUtil
{
    private TextUtil()
    {
        throw new UnsupportedOperationException();
    }

    public static boolean isEmpty(CharSequence str)
    {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence s)
    {
        return !isEmpty(s);
    }

    public static boolean notEmpty(CharSequence s)
    {
        return !isEmpty(s);
    }
	
    public static boolean isDigit(CharSequence str)
    {
        return !isEmpty(str) && TextUtils.isDigitsOnly(str);
    }

    public static boolean isNumber(CharSequence str)
    {
        return isDigit(str);
    }

    public static String reverse(CharSequence s)
    {
        if (isEmpty(s))
        {
            return emptyString();
        }

        int length = s.length();
        if (length <= 1)
        {
            return s.toString();
        }

        int mid = length >> 1;
        char[] chars = s.toString().toCharArray();
        char c;
        for (int i = 0; i < mid; ++i)
        {
            c = chars[i];
            chars[i] = chars[length - i - 1];
            chars[length - i - 1] = c;
        }

        return new String(chars);
    }

    public static String emptyString()
    {
        return "";
    }

    public static String[] split(String str, String regex)
    {
        if (str == null || regex == null)
        {
            return new String[0];
        }

        String s = str;
        while (s.endsWith(regex))
        {
            s = s.substring(0, s.lastIndexOf(regex));
        }

        return s.split(regex);
    }

    public static boolean isBlank(CharSequence str)
    {
        return isEmpty(str) || isEmpty(str.toString().trim());
    }

    public static boolean equals(CharSequence str, CharSequence str2)
    {
        if (str == str2)
        {
            return true;
        }

        try
        {
            return str.toString().equals(str2.toString());
        }
		catch (NullPointerException e)
        {
        }

        return false;
    }

    public static boolean equalsIgnoreCase(CharSequence str, CharSequence str2)
    {
        if (str == str2)
        {
            return true;
        }

        try
        {
            return str.toString().equalsIgnoreCase(str2.toString());
        }
		catch (NullPointerException e)
        {
        }

        return false;
    }

    public static boolean notEquals(CharSequence str, CharSequence str2)
    {
        return !equals(str, str2);
    }

    public static boolean notEqualsIgnoreCase(CharSequence str, CharSequence str2)
    {
        return !equalsIgnoreCase(str, str2);
    }
}
