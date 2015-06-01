package model.xml;

import nu.xom.Attribute.Type;
import nu.xom.NodeFactory;
import nu.xom.Nodes;

public class WhiteSpaceEliminator extends NodeFactory {

    /**
     * Trims the given string of white space, line breaks, and tabs.
     *
     * @param data the input string
     * @return the trimmed string
     */
    private static String normalizeSpace(String data) {
        //noinspection HardcodedLineSeparator
        String string = data.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ').trim();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (i == 0 || string.charAt(i - 1) != ' ' || string.charAt(i) != ' ') {
                result.append(string.charAt(i));
            }
        }
        return result.toString();
    }

    @SuppressWarnings("ElementOnlyUsedFromTestCode")
    @Override
    public Nodes makeText(String data) {
        String string = normalizeSpace(data);
        if (string.isEmpty()) {
            return new Nodes();
        }
        return super.makeText(string);
    }

    @SuppressWarnings("ElementOnlyUsedFromTestCode")
    @Override
    public Nodes makeAttribute(String name, String URI, String value, Type type) {
        String string = normalizeSpace(value);
        return super.makeAttribute(name, URI, string, type);
    }
}
