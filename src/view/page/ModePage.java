/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:27.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page;

import info.clearthought.layout.TableLayout;
import view.ImageRepository;
import view.component.Frame;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ModePage extends AbstractPage {

    /**
     * Instantiates the page.
     *
     * @param frame the parent frame
     */
    public ModePage(final Frame frame) {
        super(frame);
        hideButtons();
        final double[][] areaLayout = {{0.5, 20, TableLayout.FILL}, {0.3, TableLayout.FILL, 10, TableLayout.FILL}};
        area.setLayout(new TableLayout(areaLayout));

        final JLabel logo = new JLabel(ImageRepository.getInstance().getLogo());
        area.add(logo, "0, 0, 2, 0, c, c");

        final JButton learningButton = new JButton(new AbstractAction("Learning Mode") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                frame.showCollectPage();
            }
        });
        area.add(learningButton, "0, 1, c, b");

        final JButton fuzzingButton = new JButton(new AbstractAction("Fuzzing Mode") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                frame.showImportPage();
            }
        });
        area.add(fuzzingButton, "2, 1, c, b");

        final String html1 = "<html><body style='width: 100%'>";
        final String html2 = "</body></html>";

        final JLabel learningLabel = new JLabel(html1 + "This mode is used to learn the protocol structure of an " +
                "unknown protocol. To collect the messages between two systems, ProDisFuzz can watch a specific " +
                "folder for communication files created by for example a proxy server." + html2);
        area.add(learningLabel, "0, 3, c, t");

        final JLabel fuzzingLabel = new JLabel(html1 + "This mode is used to fuzz a destination with the protocol " +
                "structure gained in the learning mode. Depending on the structure different fuzzed messages are " +
                "generated and sent to the destination. Crashes are detected automatically and can be exported in a " +
                "final report." + html2);
        area.add(fuzzingLabel, "2, 3, c, t");
    }
}
