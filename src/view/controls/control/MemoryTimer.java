/*
 * This file is part of ProDisFuzz, modified on 08.02.14 23:13.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.controls.control;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

class MemoryTimer {

    private final Label label;
    private Timer timer;

    /**
     * Instantiates a new timer for displaying the current memory consumption in periodic intervals.
     *
     * @param l the label the memory consumption will be displayed in
     */
    public MemoryTimer(Label l) {
        label = l;
        timer = new Timer();
    }

    /**
     * Starts the timer.
     */
    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DecimalFormat numberFormat = new DecimalFormat("#0");
                final String totalMemory = numberFormat.format(Runtime.getRuntime().totalMemory() / 1024 / 1024);
                final String usedMemory = numberFormat.format((Runtime.getRuntime().totalMemory() - Runtime
                        .getRuntime().freeMemory()) / 1024 / 1024);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        label.setText(usedMemory + "/" + totalMemory + " MB");
                    }
                });
            }
        }, 2000, 2000);
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        timer.cancel();
    }

}
