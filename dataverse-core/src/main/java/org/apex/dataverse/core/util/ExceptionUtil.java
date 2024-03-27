package org.apex.dataverse.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author : Danny.Huo
 * @date : 2023/4/27 17:45
 * @since : 0.1.0
 */
public class ExceptionUtil {

    /**
     * 将异常堆栈转为字符串
     * @param e
     * @return
     */
    public static String getStackTrace(Throwable e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw, true);
            e.printStackTrace(pw);
            return sw.toString();
        } finally {
            if(null != sw) {
                try {
                    sw.close();
                } catch (IOException ex) {
                    ex.getStackTrace();
                }
            }
            if(null != pw) {
                pw.close();
            }
        }
    }
}
