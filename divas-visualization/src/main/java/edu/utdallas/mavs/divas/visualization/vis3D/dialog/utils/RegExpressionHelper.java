package edu.utdallas.mavs.divas.visualization.vis3D.dialog.utils;

import java.util.regex.Pattern;

/*
 * Helper class for regular expression
 */
@SuppressWarnings("javadoc")
public class RegExpressionHelper
{
    public static String NUMBER_PATTERN = "[0-9]+";
    public static String RANGE_PATTERN  = "[0-9]+\\-[0-9]+";

    public static boolean isNumberPattern(String string)
    {
        Pattern numPattern = Pattern.compile(NUMBER_PATTERN);
        return numPattern.matcher(string).matches();
    }

    public static boolean isNumberRangePattern(String string)
    {
        Pattern rangePattern = Pattern.compile(RANGE_PATTERN);
        return rangePattern.matcher(string).matches();
    }
}
