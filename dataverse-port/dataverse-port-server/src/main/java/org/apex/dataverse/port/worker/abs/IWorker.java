package org.apex.dataverse.port.worker.abs;

/**
 * @author Danny.Huo
 * @date 2023/12/1 15:50
 * @since 0.1.0
 */
public interface IWorker extends Runnable {

    Long INTERVAL = 5000L;

    /**
     * Check worker is running
     *
     * @return boolean
     */
    boolean isRunning();

    /**
     * Kill worker
     *
     * @return boolean
     */
    boolean kill();

    /**
     * Sleep default interval
     */
    void sleep() throws InterruptedException;
}

