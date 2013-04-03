/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * The Class WindowCo is the super class for all windows composites.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class WindowCo extends Composite {

    /**
     * The cancel button.
     */
    private Button cancelButton;

    /**
     * The next button.
     */
    private Button nextButton;

    /**
     * The finish button.
     */
    private Button finishButton;

    /**
     * The page number.
     */
    private final int page;

    /**
     * The total page number.
     */
    private final int totalPage;

    /**
     * Instantiates a new window composite.
     *
     * @param parent    the parent
     * @param style     the style
     * @param page      the page number
     * @param totalPage the total page number
     */
    public WindowCo(final Composite parent, final int style, final int page,
                    final int totalPage) {
        super(parent, style);
        this.page = page;
        this.totalPage = totalPage;
    }

    /**
     * Shows the navigation buttons.
     */
    public void insertButtons() {
        final Composite buttonsComposite = new Composite(this, SWT.NONE);
        buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.None, true,
                false));
        buttonsComposite.setLayout(new GridLayout(4, false));

        final Label buttonsSeperator = new Label(buttonsComposite,
                SWT.SEPARATOR | SWT.HORIZONTAL);
        buttonsSeperator.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true,
                false, 4, 1));

        final Label pageLabel = new Label(buttonsComposite, SWT.NONE);
        pageLabel.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, true, false));
        pageLabel.setText("Step " + page + "/" + totalPage);

        cancelButton = new Button(buttonsComposite, SWT.PUSH);
        cancelButton.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false,
                false));
        cancelButton.setText(" Cancel ");
        cancelButton.setEnabled(false);

        nextButton = new Button(buttonsComposite, SWT.PUSH);
        nextButton
                .setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
        nextButton.setText(" Next > ");
        nextButton.setEnabled(false);

        finishButton = new Button(buttonsComposite, SWT.PUSH);
        finishButton.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false,
                false));
        finishButton.setText(" Finish ");
        finishButton.setEnabled(false);

        layout();
    }

    /**
     * Enables the cancel button if the argument is true, and disables it
     * otherwise.
     *
     * @param enabled the new enabled state
     */
    protected void setCancelable(final boolean enabled) {
        cancelButton.setEnabled(enabled);
    }

    /**
     * Enables the next button if the argument is true, and disables it
     * otherwise.
     *
     * @param enabled the new enabled state
     */
    protected void setComplete(final boolean enabled) {
        nextButton.setEnabled(enabled);
    }

    /**
     * Enables the finish button if the argument is true, and disables it
     * otherwise.
     *
     * @param enabled the new enabled state
     */
    protected void setFinish(final boolean enabled) {
        finishButton.setEnabled(enabled);
    }

    /**
     * Adds the listener to the cancel button.
     *
     * @param adapter the adapter
     */
    public void addListenerToCancelButton(final SelectionAdapter adapter) {
        cancelButton.addSelectionListener(adapter);
    }

    /**
     * Adds the listener to the next button.
     *
     * @param adapter the adapter
     */
    public void addListenerToNextButton(final SelectionAdapter adapter) {
        nextButton.addSelectionListener(adapter);
    }

    /**
     * Adds the listener to the next button.
     *
     * @param adapter the adapter
     */
    public void addListenerToFinishButton(final SelectionAdapter adapter) {
        finishButton.addSelectionListener(adapter);
    }

}
