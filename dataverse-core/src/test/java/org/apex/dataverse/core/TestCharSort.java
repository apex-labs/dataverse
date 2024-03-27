package org.apex.dataverse.core;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @version : v1.0
 * @projectName : nexus-msg
 * @package : com.apex.jms.test
 * @className : TestCharSort
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2023/5/24 14:24
 * @updateUser :
 * @updateDate :
 * @updateRemark :
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
