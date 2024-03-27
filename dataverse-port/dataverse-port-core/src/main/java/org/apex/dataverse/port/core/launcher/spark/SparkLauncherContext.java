package org.apex.dataverse.port.core.launcher.spark;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Danny.Huo
 * @date 2023/6/7 10:45
 * @since 0.1.0
 */
@Data
public class SparkLauncherContext {

    /**
     * Master
     */
    private String master;

    /**
     * deploy mode
     */
    private String deployMode;

    /**
     * app参数
     */
    private Map<String, String> appArgs;

    /**
     * Spark Submit Args
     */
    private Map<String, String> sparkArgs;

    /**
     * Spark Config
     */
    private Map<String, String> sparkConf;

    /**
     * 启动spark程序时所用的名称
     */
    private String appName;

    /**
     * Main函数所在类
     */
    private String mainClass;

    /**
     * 依赖Jar
     */
    private String[] dependenceJars;

    /**
     * Engine Jar
     */
    private String engineJar;

    /**
     * 错误日志重定向路径
     */
    private String redirectError;

    /**
     * out put重定向路径
     */
    private String redirectOut;

    /**
     * Spark Handler Listener
     * return parameter
     */
    private SparkHandleListener sparkHandleListener;

    /**
     * 添加app参数
     * @param k key
     * @param v value
     */
    public void addAppArg (String k, String v) {
        if (null == appArgs) {
            appArgs = new HashMap<>(10);
        }
        appArgs.put(k, v);
    }
}
