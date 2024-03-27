package org.apex.dataverse.port.core.launcher.spark;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.launcher.SparkAppHandle;

/**
 * @author Danny.Huo
 * @date 2023/6/7 10:45
 * @since 0.1.0
 */
@Getter
@Slf4j
public class SparkHandleListener implements SparkAppHandle.Listener {

    public final static String FINISHED = "FINISHED";
    public final static String FAILED = "FAILED";
    public final static String KILLED = "KILLED";

    /**
     * Application ID
     */
    private String applicationId;

    /**
     * Application  state
     */
    private String applicationState;

    /**
     * Spark application finished flag
     */
    private boolean isFinished;

    /**
     * 默认无参构造
     */
    public SparkHandleListener() {}

    @Override
    public void stateChanged(SparkAppHandle sparkAppHandle) {
        if(null != sparkAppHandle.getAppId()) {
            applicationId = sparkAppHandle.getAppId();
        }
        if(null !=  sparkAppHandle.getState()) {
            applicationState = sparkAppHandle.getState().toString();
        }

        log.info("Application [{}] state changed => {}", applicationId, applicationState);

        //check is finished or not
        if (FINISHED.equalsIgnoreCase(applicationState) ||
                FAILED.equalsIgnoreCase(applicationState) ||
                KILLED.equalsIgnoreCase(applicationState)) {

            log.info("The spark app of {}, state is {}", applicationId, applicationState);

            isFinished = true;

            // TODO engine finished, update engine state to db
        }
    }

    @Override
    public void infoChanged(SparkAppHandle sparkAppHandle) {
        log.info("Spark application of {}, info changed to {}",
                sparkAppHandle.getAppId(), sparkAppHandle.getState());
        if(null != sparkAppHandle.getAppId()) {
            applicationId = sparkAppHandle.getAppId();
        }
        if(null !=  sparkAppHandle.getState()) {
            applicationState = sparkAppHandle.getState().toString();
        }
    }
}
