package com.apex.odpc.spark;

import org.apex.dataverse.port.core.exception.AppLaunchException;
import org.apex.dataverse.port.core.launcher.spark.SparkHandleListener;
import org.apex.dataverse.port.core.launcher.spark.SparkLauncherContext;
import org.apex.dataverse.port.core.launcher.spark.SparkLauncherUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @version : v1.0
 * @projectName : nexus-odpc
 * @package : com.apex.odpc.spark
 * @className : TestSparkLauncher
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2023/6/7 11:27
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
public class TestSparkLauncherExample {

    public static void main(String[] args) throws IOException, AppLaunchException {
        SparkLauncherContext context = new SparkLauncherContext();
        context.setMainClass("org.apache.spark.examples.SparkPi");
        context.setEngineJar("/Users/danny/big_data/components/spark/examples/jars/spark-examples_2.12-3.2.0.jar");
        context.setMaster("local");
        //context.setDeployMode("client");
        context.setAppName("test-spark-engine");
        context.setRedirectError("/Users/danny/works/idea/NEXUS/datavs/nexus-odpc/odpc-server/logs/spark-error-demo.log");
        context.setRedirectOut("/Users/danny/works/idea/NEXUS/datavs/nexus-odpc/odpc-server/logs/spark-out-demo.log");

        Map<String, String> appArgs = new HashMap<>();
        appArgs.put("--master", "yarn");
        appArgs.put("100", "");
        context.setAppArgs(appArgs);

        Map<String, String> sparkArgs = new HashMap<>();
        context.setSparkArgs(sparkArgs);
        sparkArgs.put("--driver-memory", "2G");
        sparkArgs.put("--executor-memory", "4G");
        sparkArgs.put("--num-executors", "2");
        SparkHandleListener sparkHandleListener = SparkLauncherUtil.launcherSpark(context);
        System.out.println(sparkHandleListener.getApplicationId());

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
