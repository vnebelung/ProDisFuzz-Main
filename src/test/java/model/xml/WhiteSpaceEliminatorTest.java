package model.xml;

import nu.xom.Attribute;
import nu.xom.Attribute.Type;
import nu.xom.Text;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

@SuppressWarnings({"HardCodedStringLiteral", "HardcodedLineSeparator"})
public class WhiteSpaceEliminatorTest {

    private WhiteSpaceEliminator whiteSpaceEliminator;

    @BeforeClass
    public void setUp() throws IOException {
        whiteSpaceEliminator = new WhiteSpaceEliminator();
    }

    @Test
    public void testMakeText() throws Exception {
        Assert.assertEquals(whiteSpaceEliminator.makeText("a b").size(), 1);
        Assert.assertTrue(whiteSpaceEliminator.makeText("a b").get(0) instanceof Text);
        Assert.assertEquals(whiteSpaceEliminator.makeText("a b").get(0).toString(), "[nu.xom.Text: a b]");

        Assert.assertEquals(whiteSpaceEliminator.makeText("\ta \t \r \n b ").size(), 1);
        Assert.assertTrue(whiteSpaceEliminator.makeText("\ta \t \r \n b ").get(0) instanceof Text);
        Assert.assertEquals(whiteSpaceEliminator.makeText("\ta \t \r \n b ").get(0).getValue(), "a b");

        Assert.assertEquals(whiteSpaceEliminator.makeText(" \t \r \n ").size(), 0);
    }

    @Test
    public void testMakeAttribute() throws Exception {
        Assert.assertEquals(whiteSpaceEliminator.makeAttribute("test", "", "a b", Type.ID).size(), 1);
        Assert.assertTrue(whiteSpaceEliminator.makeAttribute("test", "", "a b", Type.ID).get(0) instanceof Attribute);
        Assert.assertEquals(whiteSpaceEliminator.makeAttribute("test", "", "a b", Type.ID).get(0).toString(), "[nu" +
                ".xom.Attribute: test=\"a b\"]");

        Assert.assertEquals(whiteSpaceEliminator.makeAttribute("test", "", "\ta \t \r \n b ", Type.ID).size(), 1);
        Assert.assertTrue(whiteSpaceEliminator.makeAttribute("test", "", "\ta \t \r \n b ", Type.ID).get(0)
                instanceof Attribute);
        Assert.assertEquals(whiteSpaceEliminator.makeAttribute("test", "", "\ta \t \r \n b ", Type.ID).get(0)
                .getValue(), "a b");

        Assert.assertEquals(whiteSpaceEliminator.makeAttribute("test", "", " \t \r \n ", Type.ID).size(), 1);
        Assert.assertTrue(whiteSpaceEliminator.makeAttribute("test", "", " \t \r \n ", Type.ID).get(0) instanceof
                Attribute);
        Assert.assertEquals(whiteSpaceEliminator.makeAttribute("test", "", "\t \r \n ", Type.ID).get(0).getValue(), "");
    }
}
