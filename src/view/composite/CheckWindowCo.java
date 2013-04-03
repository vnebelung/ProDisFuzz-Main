/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view.composite;

import model.ProtocolFile;
import model.process.CheckP;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import view.ImageRepository;

import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

/**
 * The Class CheckWindowCo displays all widgets which are necessary to check all
 * collected files.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class CheckWindowCo extends WindowCo implements Observer {

    /**
     * The file table.
     */
    private final Table fileTable;

    /**
     * The table column for the file name.
     */
    private final TableColumn nameTableCol;

    /**
     * The table column for the last modified property.
     */
    private final TableColumn lastModifiedCol;

    /**
     * The table column for the file size.
     */
    private final TableColumn sizeTableCol;

    /**
     * The table column for the file MD5 checksum.
     */
    private final TableColumn md5TableCol;

    /**
     * The file table group.
     */
    private final Group tableGroup;

    /**
     * The status info label.
     */
    private final Label statusInfoLabel;

    /**
     * The status icon label.
     */
    private final Label statusIconLabel;

    /**
     * Instantiates a new check window composite.
     *
     * @param parent    the parent
     * @param style     the style
     * @param page      the page number
     * @param totalPage the total page number
     */
    public CheckWindowCo(final Composite parent, final int style,
                         final int page, final int totalPage) {
        super(parent, style, page, totalPage);

        this.setLayout(new GridLayout(1, false));

        tableGroup = new Group(this, SWT.NONE);
        tableGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tableGroup.setLayout(new GridLayout(1, false));
        tableGroup.setText("File Checking");

        final Label explanationLabel = new Label(tableGroup, SWT.NONE);
        explanationLabel
                .setText("Deselect all files which you do not want to include in the protocol learning process.");

        fileTable = new Table(tableGroup, SWT.BORDER | SWT.CHECK | SWT.SINGLE
                | SWT.FULL_SELECTION);
        fileTable.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        fileTable.setHeaderVisible(true);

        nameTableCol = new TableColumn(fileTable, SWT.LEFT);
        nameTableCol.setText("File Name");

        lastModifiedCol = new TableColumn(fileTable, SWT.CENTER);
        lastModifiedCol.setText("Modified");

        sizeTableCol = new TableColumn(fileTable, SWT.RIGHT);
        sizeTableCol.setText("Size in KB");

        md5TableCol = new TableColumn(fileTable, SWT.CENTER);
        md5TableCol.setText("MD5 Checksum");

        final Composite statusComposite = new Composite(tableGroup, SWT.NONE);
        statusComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
                false));
        statusComposite.setLayout(new GridLayout(2, false));

        statusIconLabel = new Label(statusComposite, SWT.NONE);
        statusIconLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
                false, false));

        statusInfoLabel = new Label(statusComposite, SWT.NONE);
        statusInfoLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
                false));

        insertButtons();
        setCancelable(true);
    }

    /**
     * Adds the listener to the table.
     *
     * @param adapter the adapter
     */
    public void addListenerToTable(final SelectionAdapter adapter) {
        fileTable.addSelectionListener(adapter);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable observable, final Object arg) {
        final CheckP data = (CheckP) observable;

        TableItem tableItem;
        final DecimalFormat numberFormat = new DecimalFormat("#0.00");
        final DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm",
                Locale.getDefault());
        String name;
        String lastModified;
        String size;
        String md5;
        // Delete all items of the table
        fileTable.removeAll();
        ProtocolFile file;
        String[] values = new String[4];
        // Fill the table with all current items
        for (final ListIterator<ProtocolFile> iterator = data.getFiles()
                .listIterator(); iterator.hasNext(); ) {
            file = iterator.next();
            tableItem = new TableItem(fileTable, SWT.NONE); // NOPMD
            name = file.getName();
            lastModified = dateFormat.format(new Date(file.getLastModified())); // NOPMD
            // The file size in KB
            size = numberFormat.format(file.getSize() / 1024.0);
            md5 = file.getMD5();
            values[0] = name;
            values[1] = lastModified;
            values[2] = size;
            values[3] = md5;
            tableItem.setText(values);
            tableItem.setChecked(file.isChecked());
        }
        nameTableCol.pack();
        lastModifiedCol.pack();
        sizeTableCol.pack();
        md5TableCol.pack();

        // At least two files must be checked to proceed
        if (data.numOfCheckedFiles() < 2) {
            statusIconLabel.setImage(ImageRepository.getInstance()
                    .getErrorIcon());
            statusInfoLabel.setText("Select at least two files");
        } else {
            ImageRepository.getInstance();
            statusIconLabel.setImage(ImageRepository.getInstance().getOkIcon());
            statusInfoLabel.setText(data.numOfCheckedFiles()
                    + " files selected");
        }

        setComplete(data.numOfCheckedFiles() >= 2);

        tableGroup.layout();
    }

    /**
     * Gets the file table.
     *
     * @return the file table
     */
    public Table getFileTable() {
        return fileTable;
    }

}
