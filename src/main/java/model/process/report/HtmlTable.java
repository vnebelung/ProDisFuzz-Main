package model.process.report;

import nu.xom.Attribute;
import nu.xom.Element;

class HtmlTable {

    private Element table;

    /**
     * Creates a new HTML table in XOM. The first row contains only header cells.
     *
     * @param rows    the number of rows
     * @param columns the number of columns
     */
    public HtmlTable(int rows, int columns) {
        table = new Element("table", ReportProcess.NAMESPACE);
        for (int i = 0; i < rows; i++) {
            Element tr = new Element("tr", ReportProcess.NAMESPACE);
            for (int j = 0; j < columns; j++) {
                tr.appendChild(new Element(i == 0 ? "th" : "td", ReportProcess.NAMESPACE));
            }
            table.appendChild(tr);
        }
    }

    /**
     * Sets the content of a table cell with the given row and column indices to the given text.
     *
     * @param row    the row index
     * @param column the column index
     * @param value  the cell text
     */
    public void setText(int row, int column, String value) {
        table.getChildElements("tr", ReportProcess.NAMESPACE).get(row).getChildElements(row == 0 ? "th" : "td",
                ReportProcess.NAMESPACE).get(column).appendChild(value);
    }

    /**
     * Sets the content of a table cell with the given row and column indices to a link with the given address and
     * text.
     *
     * @param row    the row index
     * @param column the column index
     * @param href   the link address
     * @param text   the link text
     */
    public void setLink(int row, int column, String href, String text) {
        Element a = new Element("a", ReportProcess.NAMESPACE);
        a.addAttribute(new Attribute("href", href));
        a.appendChild(text);
        table.getChildElements("tr", ReportProcess.NAMESPACE).get(row).getChildElements(row == 0 ? "th" : "td",
                ReportProcess.NAMESPACE).get(column).appendChild(a);
    }

    /**
     * Sets the attribute of a table cell with the given row and column indices to the given name and value.
     *
     * @param row    the row index
     * @param column the column index
     * @param name   the attribute name
     * @param value  the attribute value
     */
    public void setAttribute(int row, int column, String name, String value) {
        table.getChildElements("tr", ReportProcess.NAMESPACE).get(row).getChildElements(row == 0 ? "th" : "td",
                ReportProcess.NAMESPACE).get(column).addAttribute(new Attribute(name, value));
    }

    /**
     * Returns the XOM table element.
     *
     * @return the table element
     */
    public Element getTable() {
        return table;
    }

}
