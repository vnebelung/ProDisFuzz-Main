/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import view.composite.*;

/**
 * The Class Window implements the default window of the view.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class Window {

    /**
     * The shell.
     */
    private final Shell shell;

    /**
     * The display.
     */
    private final Display display;

    /**
     * The main area which contains the different windows.
     */
    private final Composite mainComposite;

    /**
     * The main area layout.
     */
    private final StackLayout mainCompositeLayout; // NOPMD

    /**
     * The start window.
     */
    private final StartWindowCo startWindow;

    /**
     * The collect window.
     */
    private final CollectWindowCo collectWindow;

    /**
     * The check window.
     */
    private final CheckWindowCo checkWindow;

    /**
     * The learn window.
     */
    private final LearnWindowCo learnWindow;

    /**
     * The XML generation window.
     */
    private final XMLGenWindowCo xmlGenWindow;

    /**
     * The load XML window.
     */
    private final LoadXMLWindowCo loadXmlWindow;

    /**
     * The mode selection window.
     */
    private final OptionsWindowCo optionsWindow;

    /**
     * The mode selection window.
     */
    private final FuzzingWindowCo fuzzingWindow;

    /**
     * The report generation window.
     */
    private final ReportGenWindowCo reportGenWindow;

    /**
     * Instantiates a new window and its shell.
     *
     * @param display the display
     */
    public Window(final Display display) {
        // Initialize shell
        this.display = display;
        shell = new Shell(this.display, SWT.MAX | SWT.MIN | SWT.CLOSE);
        shell.setText("ProDisFuzz");
        final GridLayout shellLayout = new GridLayout(1, false);
        shellLayout.marginHeight = 20;
        shellLayout.marginWidth = 20;
        shell.setLayout(shellLayout);
        shell.setMinimumSize(800, 640);
        shell.setSize(800, 640);
        mainComposite = new Composite(shell, SWT.NONE);
        mainComposite
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainCompositeLayout = new StackLayout();
        mainComposite.setLayout(mainCompositeLayout);

        startWindow = new StartWindowCo(mainComposite, SWT.NONE, this);
        // All windows have 2 additional parameters: the current page number and
        // the total number of pages
        collectWindow = new CollectWindowCo(mainComposite, SWT.NONE, 1,
                4);
        checkWindow = new CheckWindowCo(mainComposite, SWT.NONE, 2, 4);
        learnWindow = new LearnWindowCo(mainComposite, SWT.NONE, 3, 4);
        xmlGenWindow = new XMLGenWindowCo(mainComposite, SWT.NONE, 4, 4);
        // All windows have 2 additional parameters: the current page number and
        // the total number of pages
        loadXmlWindow = new LoadXMLWindowCo(mainComposite, SWT.NONE, 1,
                4);
        optionsWindow = new OptionsWindowCo(mainComposite, SWT.NONE, 2,
                4);
        fuzzingWindow = new FuzzingWindowCo(mainComposite, SWT.NONE, 3,
                4);
        reportGenWindow = new ReportGenWindowCo(mainComposite, SWT.NONE,
                4, 4);
    }

    /**
     * Makes the shell visible until it is closed.
     */
    public void show() {
        // Shell behaviour
        showStartWindow();
        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    /**
     * Shows the start window with its options for choosing the operation modes.
     */
    public void showStartWindow() {
        mainCompositeLayout.topControl = startWindow;
        mainComposite.layout();
        shell.layout();
    }

    /**
     * Shows the collect window where protocol files can be collected.
     */
    public void showCollectWindow() {
        mainCompositeLayout.topControl = collectWindow;
        mainComposite.layout();
    }

    /**
     * Gets the protocol window.
     *
     * @return the protocol window
     */
    public CollectWindowCo getCollectWindow() {
        return collectWindow;
    }

    /**
     * Gets the XML generation window.
     *
     * @return the XML generation window
     */
    public XMLGenWindowCo getXmlGenWindow() {
        return xmlGenWindow;
    }

    /**
     * Gets the check window.
     *
     * @return the check window
     */
    public CheckWindowCo getCheckWindow() {
        return checkWindow;
    }

    /**
     * Gets the learn window.
     *
     * @return the learn window
     */
    public LearnWindowCo getLearnWindow() {
        return learnWindow;
    }

    /**
     * Gets the load XML window.
     *
     * @return the load XML window
     */
    public LoadXMLWindowCo getLoadXmlWindow() {
        return loadXmlWindow;
    }

    /**
     * Gets the options window.
     *
     * @return the options window
     */
    public OptionsWindowCo getOptionsWindow() {
        return optionsWindow;
    }

    /**
     * Gets the fuzzing window.
     *
     * @return the fuzzing window
     */
    public FuzzingWindowCo getFuzzingWindow() {
        return fuzzingWindow;
    }

    /**
     * Gets the report generation window.
     *
     * @return the report generation window
     */
    public ReportGenWindowCo getReportGenWindow() {
        return reportGenWindow;
    }

    /**
     * Shows the check window where the set of files can be checked.
     */
    public void showCheckWindow() {
        mainCompositeLayout.topControl = checkWindow;
        mainComposite.layout();
    }

    /**
     * Shows the load XML window where a previous generated XML file can be
     * loaded.
     */
    public void showLoadXmlWindow() {
        mainCompositeLayout.topControl = loadXmlWindow;
        mainComposite.layout();
    }

    /**
     * Shows the learn window where the protocol is being learned.
     */
    public void showLearnWindow() {
        mainCompositeLayout.topControl = learnWindow;
        mainComposite.layout();
    }

    /**
     * Shows the learn window where the protocol is being learned.
     */
    public void showXmlGenWindow() {
        mainCompositeLayout.topControl = xmlGenWindow;
        mainComposite.layout();
    }

    /**
     * Shows the mode selection window where the fuzzing options are set.
     */
    public void showOptionsWindow() {
        mainCompositeLayout.topControl = optionsWindow;
        mainComposite.layout();
    }

    /**
     * Shows the fuzzing window where the fuzzing takes place.
     */
    public void showFuzzingWindow() {
        mainCompositeLayout.topControl = fuzzingWindow;
        mainComposite.layout();
    }

    /**
     * Shows the report generation window where the report is being generated.
     */
    public void showReportGenWindow() {
        mainCompositeLayout.topControl = reportGenWindow;
        mainComposite.layout();
    }

}
