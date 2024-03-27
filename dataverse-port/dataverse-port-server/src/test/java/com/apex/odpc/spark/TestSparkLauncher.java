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
public class TestSparkLauncher {

    public static void main(String[] args) throws IOException, AppLaunchException {
        SparkLauncherContext context = new SparkLauncherContext();
        context.setMainClass("org.apex.dataverse.engine.EngineApp");
        //context.setEngineJar("/Users/danny/works/idea/dataverse/dataverse-engine/dataverse-spark-engine/target/dataverse-spark-engine.jar");
        context.setEngineJar("hdfs://localhost:9000/tmp/datavs/jars/dataverse-spark-engine.jar");
        context.setMaster("yarn");
        //context.setDeployMode("cluster");
        //context.setDeployMode("client");
        context.setAppName("test-spark-engine");
        context.setRedirectError("/Users/danny/works/idea/NEXUS/datavs/nexus-odpc/odpc-server/logs/spark-error.log");
        context.setRedirectOut("/Users/danny/works/idea/NEXUS/datavs/nexus-odpc/odpc-server/logs/spark-out.log");

        Map<String, String> appArgs = new HashMap<>();
        appArgs.put("--master", "yarn");
        appArgs.put("--tick-server-address", "127.0.0.1");
        appArgs.put("--tick-server-port", "20001");
        appArgs.put("--tick-client-threads", "2");
        appArgs.put("--tick-interval-ms", "1000");
        appArgs.put("--engine-server-port", "59622");
        appArgs.put("--engine-server-boss-threads", "2");
        appArgs.put("--engine-server-worker-threads", "4");
        appArgs.put("--engine-id", "38");
        context.setAppArgs(appArgs);

        Map<String, String> sparkArgs = new HashMap<>();
        context.setSparkArgs(sparkArgs);
        sparkArgs.put("--driver-memory", "2G");
        sparkArgs.put("--executor-memory", "4G");
        sparkArgs.put("--num-executors", "2");
        SparkHandleListener sparkHandleListener = SparkLauncherUtil.launcherSpark(context);
        System.out.println(sparkHandleListener.getApplicationId());

//        try {
//            Thread.sleep(100000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

    }
}
