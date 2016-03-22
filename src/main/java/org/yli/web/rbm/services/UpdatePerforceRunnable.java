package org.yli.web.rbm.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yli.web.rbm.perforce.P4ClUtil;

/**
 * Created by yli on 3/22/16.
 */
public class UpdatePerforceRunnable implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(UpdatePerforceRunnable.class);

    private P4ClUtil util = new P4ClUtil(System.getProperty("p4username"), System.getProperty("p4passwd"));

    private boolean updating = false;

    @Override
    public void run() {
        LOGGER.debug("Updating is starting running.");

        while (true) {
            updating = true;
            util.updateNewClToP4();
            updating = false;
            try {
                // wait for another hour to start another round of updating.
                Thread.sleep(60 * 60 * 1000);
            } catch (InterruptedException e) {
                LOGGER.debug("Thread is interrupted. Stopping running.");
                break;
            }
        }

    }

    public boolean isUpdating() {
        return updating;
    }
}
