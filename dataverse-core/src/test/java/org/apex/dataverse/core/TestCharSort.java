package org.apex.dataverse.core;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/5/24 14:24
 */
public class TestCharSort {

    public static void main(String[] args) {
        String[] arrs = {"赵龚沿","赵龚铲","赵龟敛","赵龚b",
                "赵龚a","赵龚c","赵龚睁","赵龚袒","赵龙雀","赵龟"};
        Comparator<String> comparator = String::compareTo;
        Arrays.sort(arrs, comparator);
        System.out.println(Arrays.toString(arrs));
    }
}
