/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:35.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.component;

import model.Model;
import model.logger.Logger;
import view.page.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class Frame extends JFrame implements Observer {

    private final JTextPane logArea;
    private final DateFormat dateFormat;
    private final JPanel contentArea;
    private final CardLayout contentLayout;
    private final ModePage modePage;
    private final CollectPage collectPage;
    private final LearnPage learnPage;
    private final ExportPage exportPage;
    private final ImportPage importPage;
    private final FuzzOptionsPage fuzzOptionsPage;
    private final FuzzingPage fuzzingPage;
    private final ReportPage reportPage;
    public final static int SPACE = 10;
    private final JScrollPane lowerPane;

    /**
     * Instantiates the basic window frame.
     *
     * @param s the frame name
     */
    public Frame(final String s) {
        super(s);
        dateFormat = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        Model.INSTANCE.getLogger().addObserver(this);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 768));
        setSize(new Dimension(1024, 768));
        getContentPane().setLayout(new BorderLayout());

        final JScrollPane upperPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(upperPane);

        contentLayout = new CardLayout();
        contentArea = new JPanel(contentLayout);
        contentArea.setPreferredSize(new Dimension(1000, 560));
        upperPane.getViewport().add(contentArea);

        modePage = new ModePage(this);
        contentArea.add(modePage, modePage.getClass().getSimpleName());

        collectPage = new CollectPage(this);
        contentArea.add(collectPage, collectPage.getClass().getSimpleName());

        learnPage = new LearnPage(this);
        contentArea.add(learnPage, learnPage.getClass().getSimpleName());

        exportPage = new ExportPage(this);
        contentArea.add(exportPage, exportPage.getClass().getSimpleName());

        importPage = new ImportPage(this);
        contentArea.add(importPage, importPage.getClass().getSimpleName());

        fuzzOptionsPage = new FuzzOptionsPage(this);
        contentArea.add(fuzzOptionsPage, fuzzOptionsPage.getClass().getSimpleName());

        fuzzingPage = new FuzzingPage(this);
        contentArea.add(fuzzingPage, fuzzingPage.getClass().getSimpleName());

        reportPage = new ReportPage(this);
        contentArea.add(reportPage, reportPage.getClass().getSimpleName());

        lowerPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(lowerPane);

        logArea = new JTextPane();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.addStyle("error", null);
        StyleConstants.setForeground(logArea.getStyle("error"), new Color(192, 0, 0));
        logArea.addStyle("warning", null);
        StyleConstants.setForeground(logArea.getStyle("warning"), new Color(192, 192, 0));
        logArea.addStyle("success", null);
        StyleConstants.setForeground(logArea.getStyle("success"), new Color(0, 192, 0));
        logArea.addStyle("info", null);
        StyleConstants.setForeground(logArea.getStyle("info"), Color.BLACK);
        lowerPane.getViewport().add(logArea);

        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPane, lowerPane);
        splitPane.setContinuousLayout(true);
        getContentPane().add(splitPane);
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final Logger update = (Logger) o;
        final StyledDocument styledDoc = logArea.getStyledDocument();
        if (update.getLastEntry() == null) {
            try {
                styledDoc.remove(0, styledDoc.getLength());
            } catch (BadLocationException e) {
                Model.INSTANCE.getLogger().error(e);
            }
        } else {
            final StringBuilder text = new StringBuilder();
            text.append('[').append(dateFormat.format(update.getLastEntry().getTime())).append("] ");
            final Style style;
            switch (update.getLastEntry().getType()) {
                case ERROR:
                    text.append("[ERROR] ");
                    style = logArea.getStyle("error");
                    break;
                case WARNING:
                    text.append("[WARNING] ");
                    style = logArea.getStyle("warning");
                    break;
                case FINE:
                    text.append("[SUCCESS] ");
                    style = logArea.getStyle("success");
                    break;
                default:
                    text.append("[INFO] ");
                    style = logArea.getStyle("info");
                    break;
            }
            text.append(update.getLastEntry().getText()).append(System.lineSeparator());
            try {
                styledDoc.insertString(styledDoc.getLength(), text.toString(), style);
            } catch (BadLocationException e) {
                Model.INSTANCE.getLogger().error(e);
            }
            final int diff = logArea.getText().split(System.lineSeparator()).length - 500;
            if (diff > 0) {
                for (int i = 0; i < diff; i++) {
                    try {
                        styledDoc.remove(0, logArea.getText().indexOf(System.lineSeparator()) + 1);
                    } catch (BadLocationException ignored) {
                    }
                }
            }
            lowerPane.getVerticalScrollBar().setValue(lowerPane.getVerticalScrollBar().getMaximum());
        }
    }

    /**
     * Makes the mode page visible.
     */
    public void showModePage() {
        contentLayout.show(contentArea, modePage.getClass().getSimpleName());
    }

    /**
     * Makes the collect page visible.
     */
    public void showCollectPage() {
        contentLayout.show(contentArea, collectPage.getClass().getSimpleName());
    }

    /**
     * Makes the learn page visible.
     */
    public void showLearnPage() {
        contentLayout.show(contentArea, learnPage.getClass().getSimpleName());
    }

    /**
     * Makes the export page visible.
     */
    public void showExportPage() {
        contentLayout.show(contentArea, exportPage.getClass().getSimpleName());
    }

    /**
     * Makes the import page visible.
     */
    public void showImportPage() {
        contentLayout.show(contentArea, importPage.getClass().getSimpleName());
    }

    /**
     * Makes the fuzz options page visible.
     */
    public void showFuzzOptionsPage() {
        contentLayout.show(contentArea, fuzzOptionsPage.getClass().getSimpleName());
    }

    /**
     * Makes the fuzzing page visible.
     */
    public void showFuzzingPage() {
        contentLayout.show(contentArea, fuzzingPage.getClass().getSimpleName());
    }

    /**
     * Makes the report page visible.
     */
    public void showReportPage() {
        contentLayout.show(contentArea, reportPage.getClass().getSimpleName());
    }
}
