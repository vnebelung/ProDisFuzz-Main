/*
 * This file is part of ProDisFuzz, modified on 03.10.13 22:25.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page;

import info.clearthought.layout.TableLayout;
import model.InjectedProtocolPart;
import model.Model;
import model.ProtocolPart;
import model.process.FuzzOptionsProcess;
import view.ImageRepository;
import view.component.Frame;
import view.component.InjectionPanel;
import view.component.ProtocolPane;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class FuzzOptionsPage extends AbstractPage implements Observer {

    private final JRadioButton simInjectionButton;
    private final JRadioButton sepInjectionButton;
    private final JRadioButton saveAllComButton;
    private final JRadioButton saveCriticalComButton;
    private final JLabel targetStatusIcon;
    private final JLabel targetStatusLabel;
    private final JPanel partsInjectionPanel;
    private final ProtocolPane sourceInjectionPane;
    private final JTextField targetAddressText;
    private final JSpinner targetPortSpinner;
    private Timer targetTimer;
    private Timer timeoutTimer;
    private Timer intervalTimer;
    private final JSpinner intervalSpinner;
    private final JSpinner timeoutSpinner;
    private final List<InjectionPanel> partPanels;
    private int lastProtocolHash;
    private final Frame frame;

    /**
     * Instantiates a new fuzz options page.
     *
     * @param frame the parent frame
     */
    public FuzzOptionsPage(final Frame frame) {
        super(frame);
        this.frame = frame;
        Model.INSTANCE.getFuzzOptionsProcess().addObserver(this);
        final double[][] areaLayout = {{TableLayout.FILL}, {TableLayout.MINIMUM, 2 * Frame.SPACE,
                TableLayout.MINIMUM, 2 * Frame.SPACE, TableLayout.FILL}};
        area.setLayout(new TableLayout(areaLayout));

        final double[][] targetLayout = {{TableLayout.MINIMUM, Frame.SPACE, 30, TableLayout.FILL, 2 * Frame.SPACE,
                TableLayout.MINIMUM, Frame.SPACE, TableLayout.MINIMUM}, {TableLayout.MINIMUM, TableLayout.MINIMUM}};
        final JPanel targetPanel = new JPanel(new TableLayout(targetLayout));
        area.add(targetPanel, "0, 0, f, c");

        final JLabel targetAddressLabel = new JLabel("Target address:");
        targetPanel.add(targetAddressLabel, "0, 0, r, c");

        targetAddressText = new JTextField();
        targetPanel.add(targetAddressText, "2, 0, 3, 0, f, c");
        targetAddressText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                targetTimer();
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                targetTimer();
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                targetTimer();
            }
        });

        final JLabel targetPortLabel = new JLabel("Port:");
        targetPanel.add(targetPortLabel, "5, 0, r, c");

        targetPortSpinner = new JSpinner(new SpinnerNumberModel(1, FuzzOptionsProcess.PORT_MIN,
                FuzzOptionsProcess.PORT_MAX, 1));
        targetPortSpinner.setEditor(new JSpinner.NumberEditor(targetPortSpinner, "0"));
        targetPanel.add(targetPortSpinner, "7, 0, f, c");
        targetPortSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                targetTimer();
            }
        });

        targetStatusIcon = new JLabel();
        targetPanel.add(targetStatusIcon, "2, 1, c, c");

        targetStatusLabel = new JLabel();
        targetPanel.add(targetStatusLabel, "3, 1, l, c");

        final double[][] miscLayout = {{TableLayout.MINIMUM, Frame.SPACE, TableLayout.FILL, 2 * Frame.SPACE,
                TableLayout.MINIMUM, Frame.SPACE, TableLayout.FILL, 2 * Frame.SPACE, TableLayout.MINIMUM,
                Frame.SPACE, TableLayout.FILL}, {TableLayout.MINIMUM, TableLayout.MINIMUM, TableLayout.MINIMUM}};
        final JPanel miscPanel = new JPanel(new TableLayout(miscLayout));
        area.add(miscPanel, "0, 2, f, c");

        final JLabel timeoutLabel = new JLabel("Connection timeout in ms:");
        miscPanel.add(timeoutLabel, "0, 0, r, c");

        timeoutSpinner = new JSpinner(new SpinnerNumberModel(FuzzOptionsProcess.TIMEOUT_MIN,
                FuzzOptionsProcess.TIMEOUT_MIN, FuzzOptionsProcess.TIMEOUT_MAX, 50));
        miscPanel.add(timeoutSpinner, "2, 0, l, c");
        timeoutSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                timeoutTimer();
            }
        });

        final JLabel intervalLabel = new JLabel("Fuzzing interval in ms:");
        miscPanel.add(intervalLabel, "0, 1, r, c");

        intervalSpinner = new JSpinner(new SpinnerNumberModel(FuzzOptionsProcess.INTERVAL_MIN,
                FuzzOptionsProcess.INTERVAL_MIN, FuzzOptionsProcess.INTERVAL_MAX, 50));
        miscPanel.add(intervalSpinner, "2, 1, l, c");
        intervalSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                intervalTimer();
            }
        });

        final JLabel injectionLabel = new JLabel("Data injection:");
        miscPanel.add(injectionLabel, "4, 0, r, c");

        simInjectionButton = new JRadioButton(new AbstractAction("Simultaneous") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.INSTANCE.getFuzzOptionsProcess().setSimultaneousInjectionMode();
            }
        });
        miscPanel.add(simInjectionButton, "6, 0, l, c");

        sepInjectionButton = new JRadioButton(new AbstractAction("Separate") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.INSTANCE.getFuzzOptionsProcess().setSeparateInjectionMethod();
            }
        });
        miscPanel.add(sepInjectionButton, "6, 1, l, c");

        final ButtonGroup injectionButtonGroup = new ButtonGroup();
        injectionButtonGroup.add(simInjectionButton);
        injectionButtonGroup.add(sepInjectionButton);

        final JLabel communicationLabel = new JLabel("Save communication:");
        miscPanel.add(communicationLabel, "8, 0, r, c");

        saveCriticalComButton = new JRadioButton(new AbstractAction("Only critical") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.INSTANCE.getFuzzOptionsProcess().setSaveCriticalCommunication();
            }
        });
        miscPanel.add(saveCriticalComButton, "10, 0, l, c");

        saveAllComButton = new JRadioButton(new AbstractAction("All") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.INSTANCE.getFuzzOptionsProcess().setSaveAllCommunication();
            }
        });
        miscPanel.add(saveAllComButton, "10, 1, l, c");

        final ButtonGroup communicationGroup = new ButtonGroup();
        communicationGroup.add(saveAllComButton);
        communicationGroup.add(saveCriticalComButton);

        final double[][] protocolInjectionLayout = {{TableLayout.FILL, TableLayout.FILL}, {TableLayout.FILL}};
        final JPanel protocolInjectionPanel = new JPanel(new TableLayout(protocolInjectionLayout));
        area.add(protocolInjectionPanel, "0, 4, f, f");

        sourceInjectionPane = new ProtocolPane(14);

        final JScrollPane sourceInjectionScrollPane = new JScrollPane(sourceInjectionPane,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        protocolInjectionPanel.add(sourceInjectionScrollPane, "0, 0, f, f");

        partsInjectionPanel = new JPanel();
        partsInjectionPanel.setLayout(new BoxLayout(partsInjectionPanel, BoxLayout.Y_AXIS));
        partPanels = new ArrayList<>();

        final JScrollPane partsInjectionScrollPane = new JScrollPane(partsInjectionPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        protocolInjectionPanel.add(partsInjectionScrollPane, "1, 0, f, f");


    }

    /**
     * Starts the timer for proceeding the target input. If the timer is already running, it will be reset.
     */
    private void targetTimer() {
        if (targetTimer == null) {
            targetTimer = new javax.swing.Timer(10, new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    Model.INSTANCE.getFuzzOptionsProcess().setTarget(targetAddressText.getText(),
                            (int) targetPortSpinner.getModel().getValue());
                }
            });
            targetTimer.setInitialDelay(1500);
            targetTimer.setRepeats(false);
        }
        if (targetTimer.isRunning()) {
            targetTimer.restart();
        } else {
            targetTimer.start();
        }
    }

    /**
     * Starts the timer for proceeding the timeout input. If the timer is already running, it will be reset.
     */
    private void timeoutTimer() {
        if (timeoutTimer == null) {
            timeoutTimer = new javax.swing.Timer(10, new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    Model.INSTANCE.getFuzzOptionsProcess().setTimeout((int) timeoutSpinner.getValue());
                }
            });
            timeoutTimer.setInitialDelay(1500);
            timeoutTimer.setRepeats(false);
        }
        if (timeoutTimer.isRunning()) {
            timeoutTimer.restart();
        } else {
            timeoutTimer.start();
        }
    }

    /**
     * Starts the timer for proceeding the interval input. If the timer is already running, it will be reset.
     */
    private void intervalTimer() {
        if (intervalTimer == null) {
            intervalTimer = new javax.swing.Timer(10, new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    Model.INSTANCE.getFuzzOptionsProcess().setInterval((int) intervalSpinner.getValue());
                }
            });
            intervalTimer.setInitialDelay(1500);
            intervalTimer.setRepeats(false);
        }
        if (intervalTimer.isRunning()) {
            intervalTimer.restart();
        } else {
            intervalTimer.start();
        }
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final FuzzOptionsProcess data = (FuzzOptionsProcess) o;

        targetStatusIcon.setIcon(data.isTargetReachable() ? ImageRepository.INSTANCE.getOkIcon() : ImageRepository
                .INSTANCE.getErrorIcon());
        targetStatusLabel.setText(data.isTargetReachable() ? "Target reachable" : "Please enter a valid target " +
                "address");

        sepInjectionButton.setSelected(data.getInjectionMethod() == FuzzOptionsProcess.InjectionMethod.SEPARATE);
        simInjectionButton.setSelected(data.getInjectionMethod() == FuzzOptionsProcess.InjectionMethod.SIMULTANEOUS);

        saveAllComButton.setSelected(data.getSaveCommunication() == FuzzOptionsProcess.CommunicationSave.ALL);
        saveCriticalComButton.setSelected(data.getSaveCommunication() == FuzzOptionsProcess.CommunicationSave.CRITICAL);

        targetPortSpinner.setValue(data.getTarget().getPort());

        intervalSpinner.setValue(data.getInterval());
        timeoutSpinner.setValue(data.getTimeout());

        synchronized (this) {
            final List<ProtocolPart> parts = new ArrayList<>(data.getInjectedProtocolParts().size());
            for (final InjectedProtocolPart injectedPart : data.getInjectedProtocolParts()) {
                parts.add(injectedPart.getProtocolPart());
            }
            sourceInjectionPane.addProtocolText(parts);
        }

        // Update the insertion panels only if the number of current insertion panels is different from the
        // number of variable protocol parts
        final List<InjectedProtocolPart> varParts = data.filterVarParts(data.getInjectedProtocolParts());
        if (lastProtocolHash != varParts.hashCode()) {
            lastProtocolHash = varParts.hashCode();
            partPanels.clear();
            // Create new part injection panels for every variable protocol part
            for (final InjectedProtocolPart part : varParts) {
                partPanels.add(new InjectionPanel(frame, part.hashCode()));
            }

            partsInjectionPanel.removeAll();
            // Update the parent panel
            for (final InjectionPanel panel : partPanels) {
                partsInjectionPanel.add(panel);
                partsInjectionPanel.add(Box.createRigidArea(new Dimension(0, Frame.SPACE)));
            }
        }
        for (int i = 0; i < varParts.size(); i++) {
            final InjectedProtocolPart.DataInjectionMethod dataInjectionMethod = varParts.get(i)
                    .getDataInjectionMethod();
            final boolean enabled = !(data.getInjectionMethod() == FuzzOptionsProcess.InjectionMethod.SIMULTANEOUS &&
                    i > 0);
            final Path library = varParts.get(i).getLibrary();
            partPanels.get(i).update(dataInjectionMethod, enabled, library);
        }

        boolean next = data.isTargetReachable();
        for (final InjectedProtocolPart injectedProtocolPart : varParts) {
            if (injectedProtocolPart.getDataInjectionMethod() == InjectedProtocolPart.DataInjectionMethod.LIBRARY &&
                    injectedProtocolPart.getLibrary() == null) {
                next = false;
                break;
            }
        }
        if (next) {
            enableNext();
        } else {
            disableNext();
        }
    }

    @Override
    protected Action nextAction(final Frame frame) {
        return new AbstractAction("Next >") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.INSTANCE.getFuzzingProcess().init();
                frame.showFuzzingPage();
            }
        };
    }
}
