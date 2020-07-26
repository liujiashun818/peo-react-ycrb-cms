package cn.people.one.appapi.util;

/**
 * Created by wilson on 2018-10-29.
 */
public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {

    public static boolean equals(Integer n1, Integer n2) {
        if (n1 != null && n2 != null) {
            return n1.equals(n2);
        }

        return n1 == null && n2 == null;
    }

    public static boolean equals(Long n1, Long n2) {
        if (n1 != null && n2 != null) {
            return n1.equals(n2);
        }

        return n1 == null && n2 == null;
    }

    public static boolean equals(Double n1, Float n2) {
        if (n1 != null && n2 != null) {
            return n1.equals(n2);
        }

        return n1 == null && n2 == null;
    }

    public static boolean equals(Float n1, Float n2) {
        if (n1 != null && n2 != null) {
            return n1.equals(n2);
        }

        return n1 == null && n2 == null;
    }

    public static boolean equals(Short n1, Short n2) {
        if (n1 != null && n2 != null) {
            return n1.equals(n2);
        }

        return n1 == null && n2 == null;
    }

    public static boolean equals(Byte n1, Byte n2) {
        if (n1 != null && n2 != null) {
            return n1.equals(n2);
        }

        return n1 == null && n2 == null;
    }

}
