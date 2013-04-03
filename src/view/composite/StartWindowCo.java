/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import view.ImageRepository;
import view.Window;

/**
 * The Class StartWindowCo displays all widgets which are necessary to list all
 * modes the program can execute.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class StartWindowCo extends WindowCo {

    /**
     * Instantiates a bunch of SWT elements which are used to display the start
     * window composite.
     *
     * @param parent the parent element
     * @param style  the style
     * @param window the window class
     */
    public StartWindowCo(final Composite parent, final int style,
                         final Window window) {
        super(parent, style, 0, 0);

        this.setLayout(new GridLayout(2, true));

        final Label headingLabel = new Label(this, SWT.NONE);
        headingLabel.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, true,
                false, 2, 1));
        headingLabel.setImage(ImageRepository.getInstance().getLogo());

        final Label subHeadingLabel = new Label(this, SWT.NONE);
        subHeadingLabel.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, true,
                false, 2, 1));
        subHeadingLabel.setText("Protocol Dissection Fuzzer");

        final GridData labelGridData = new GridData(SWT.FILL, SWT.NONE, false,
                false);
        // Set a very small width hint to get a correct wrap for the label
        labelGridData.widthHint = this.getShell().getSize().x / 10000;

        final Group learnGroup = new Group(this, SWT.NONE);
        learnGroup
                .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        learnGroup.setLayout(new GridLayout(1, false));

        final Button learnButton = new Button(learnGroup, SWT.PUSH);
        learnButton.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, true,
                false));
        learnButton.setText(" Learn Protocol ");
        learnButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                window.showCollectWindow();
            }
        });

        final Label learnLabel = new Label(learnGroup, SWT.WRAP);
        learnLabel.setLayoutData(labelGridData);
        learnLabel
                .setText("This mode is used to learn the protocol structure of an unknown protocol. To collect the messages between two systems, ProDisFuzz can watch a specific folder for communication files created by for example a proxy server.");

        final Group loadGroup = new Group(this, SWT.NONE);
        loadGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        loadGroup.setLayout(new GridLayout(1, false));

        final Button loadButton = new Button(loadGroup, SWT.PUSH);
        loadButton
                .setLayoutData(new GridData(SWT.CENTER, SWT.NONE, true, false));
        loadButton.setText(" Load Protocol ");
        loadButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                window.showLoadXmlWindow();
            }
        });

        final Label loadLabel = new Label(loadGroup, SWT.WRAP);
        loadLabel.setLayoutData(labelGridData);
        loadLabel
                .setText("This mode is used to fuzz a destination with the protocol structure gained in the learning mode. Depending on the structure different fuzzed messages are generated and sent to the destination. Crashes are detected automatically and can be exported in a final report.");
    }

}
