package org.apex.dataverse.port.core.launcher.spark;

import org.apex.dataverse.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.apex.dataverse.port.core.exception.AppLaunchException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Danny.Huo
 * @date 2023/6/7 10:45
 * @since 0.1.0
 */
@Slf4j
public class SparkLauncherUtil {

    /**
     * Default startup timeout
     * <p>
     * after which an exception will be thrown if the app is not successfully started
     */
    private final static long DEFAULT_STARTUP_TIMEOUT = 1000 * 60 * 1;

    /**
     * Scan application state per sleep interval
     */
    private final static int SLEEP_INTERVAL = 1000;

    /**
     * Spark application running state
     */
    private final static String RUNNING = "RUNNING";

    /**
     * Start Spark Application with default timeout
     *
     * @param context SparkLauncherContext
     * @return SparkHandleListener
     * @throws IOException IOException
     */
    public static SparkHandleListener launcherSpark(SparkLauncherContext context)
            throws IOException, AppLaunchException {
        return launcherSpark(context, null);
    }

    /**
     * Start Spark Application with timeout
     *
     * @param context SparkLauncherContext
     * @param timeout launch max time
     * @return SparkHandleListener
     * @throws IOException IOException
     */
    public static SparkHandleListener launcherSpark(SparkLauncherContext context, Long timeout)
            throws IOException, AppLaunchException {

        // Build spark launcher
        SparkLauncher sparkLauncher = SparkLauncherUtil.buildSparkLauncher(context);

        // Set redirect error log
        if (StringUtil.isNotBlank(context.getRedirectError())) {
            log.info("Will redirect error => " + context.getRedirectError());
            sparkLauncher.redirectError(new File(context.getRedirectError()));
        }

        // Set redirect out log
        if (StringUtil.isNotBlank(context.getRedirectOut())) {
            log.info("Will redirect output => " + context.getRedirectOut());
            sparkLauncher.redirectOutput(new File(context.getRedirectOut()));
        }

        // Launcher spark
        SparkHandleListener listener = new SparkHandleListener();
        context.setSparkHandleListener(listener);
        SparkAppHandle sparkAppHandle = sparkLauncher.startApplication(listener);

        long startTime = System.currentTimeMillis();
        boolean startOkay = false;
        long t = null == timeout ? DEFAULT_STARTUP_TIMEOUT : timeout;
        while (System.currentTimeMillis() - startTime <= t) {

            if (null != sparkAppHandle.getAppId() &&
                    RUNNING.equals(listener.getApplicationState())) {
                startOkay = true;
                break;
            }

            try {
                Thread.sleep(SLEEP_INTERVAL);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if(!startOkay) {
            log.error("Start spark application is failed, throw and AppLaunchException");
            throw AppLaunchException.newException();
        }

        log.info("The spark application is started, application id  = {}", sparkAppHandle.getAppId());
        return listener;
    }

    /**
     * Build sparkLauncher tool
     */
    private static SparkLauncher buildSparkLauncher(SparkLauncherContext context) {
        SparkLauncher sparkLauncher = new SparkLauncher();

        if (StringUtil.isNotBlank(context.getAppName())) {
            sparkLauncher.setAppName(context.getAppName());
            log.info("The app name => " + context.getAppName());
        }

        if (StringUtil.isNotBlank(context.getEngineJar())) {
            sparkLauncher.setAppResource(context.getEngineJar());
            log.info("The app jar => " + context.getEngineJar());
        }

        if (StringUtil.isNotBlank(context.getMaster())) {
            sparkLauncher.setMaster(context.getMaster());
            log.info("--master => " + context.getMaster());
        } else {
            throw new IllegalArgumentException("The spark master must be set");
        }

        if (StringUtil.isNotBlank(context.getDeployMode())) {
            sparkLauncher.setDeployMode(context.getDeployMode());
            log.info("--deploy-mode => " + context.getDeployMode());
        }

        if (StringUtil.isNotBlank(context.getMainClass())) {
            sparkLauncher.setMainClass(context.getMainClass());
            log.info("--class => " + context.getMainClass());
        }

        if(null != context.getDependenceJars()) {
            for (String jar : context.getDependenceJars()) {
                sparkLauncher.addJar(jar);
                log.info("Add dependence jar => {}", jar);
            }
        }

        //set app args
        setAppArgs(sparkLauncher, context.getAppArgs());

        //set app args
        setSparkArgs(sparkLauncher, context.getSparkArgs());

        //set spark conf
        setSparkConf(sparkLauncher, context.getSparkConf());

        //set dependence jars
        if (null != context.getDependenceJars()) {
            for (String jar : context.getDependenceJars()) {
                sparkLauncher.addJar(jar);
                if(log.isDebugEnabled()) {
                    log.debug("Add dependence jars : " + jar);
                }
            }
        }

        return sparkLauncher;
    }

    /**
     * Set spark conf
     *
     * @param sparkLauncher SparkLauncher
     */
    private static void setSparkConf(SparkLauncher sparkLauncher, Map<String, String> sparkConf) {
        if (null != sparkConf  && !sparkConf.isEmpty()) {
            for (Map.Entry<String, String> conf : sparkConf.entrySet()) {
                sparkLauncher.setConf(conf.getKey(), conf.getValue());
                if(log.isDebugEnabled()) {
                    log.debug("Set spark conf : " +
                            conf.getKey() + "=" + conf.getValue());
                }
            }
        }
    }

    /**
     * Set application args
     *
     * @param sparkLauncher SparkLauncher
     */
    private static void setAppArgs(SparkLauncher sparkLauncher, Map<String, String> appArgs) {
        if (null != appArgs && !appArgs.isEmpty()) {
            for (Map.Entry<String, String> arg : appArgs.entrySet()) {
                sparkLauncher.addAppArgs(arg.getKey(), arg.getValue());
                if(log.isDebugEnabled()) {
                    log.debug("Add spark app args : " +
                            arg.getKey() + "=" + arg.getValue());
                }
            }
        }
    }

    /**
     * Set spark args
     *
     * @param sparkLauncher SparkLauncher
     */
    private static void setSparkArgs(SparkLauncher sparkLauncher, Map<String, String> sparkArgs) {
        if (null != sparkArgs && !sparkArgs.isEmpty()) {
            for (Map.Entry<String, String> arg : sparkArgs.entrySet()) {
                sparkLauncher.addSparkArg(arg.getKey(), arg.getValue());
                if(log.isDebugEnabled()) {
                    log.debug("Add spark args : " +
                            arg.getKey() + "=" + arg.getValue());
                }
            }
        }
    }
}
