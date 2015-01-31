package model.protocol;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class InjectedProtocolBlockTest {

    @Test
    public void testSetLibrary() throws Exception {
        Byte[] bytes = {null};
        InjectedProtocolBlock injectedProtocolBlock = new InjectedProtocolBlock(ProtocolBlock.Type.VAR, bytes);
        Path path = Paths.get(getClass().getResource("/library.txt").toURI());
        injectedProtocolBlock.setLibrary(path);
        Assert.assertEquals(injectedProtocolBlock.getLibrary(), path);
    }

    @Test
    public void testSetLibraryInjection() throws Exception {
        Byte[] bytes = {null};
        InjectedProtocolBlock injectedProtocolBlock = new InjectedProtocolBlock(ProtocolBlock.Type.VAR, bytes);
        injectedProtocolBlock.setLibraryInjection();
        Assert.assertEquals(injectedProtocolBlock.getDataInjectionMethod(), InjectedProtocolBlock.DataInjectionMethod
                .LIBRARY);
    }

    @Test
    public void testSetRandomInjection() throws Exception {
        Byte[] bytes = {null};
        InjectedProtocolBlock injectedProtocolBlock = new InjectedProtocolBlock(ProtocolBlock.Type.VAR, bytes);
        injectedProtocolBlock.setRandomInjection();
        Assert.assertEquals(injectedProtocolBlock.getDataInjectionMethod(), InjectedProtocolBlock.DataInjectionMethod
                .RANDOM);
    }

    @Test
    public void testGetNumOfLibraryLines() throws Exception {
        Byte[] bytes = {null};
        InjectedProtocolBlock injectedProtocolBlock = new InjectedProtocolBlock(ProtocolBlock.Type.VAR, bytes);
        Path path = Paths.get(getClass().getResource("/library.txt").toURI());
        injectedProtocolBlock.setLibraryInjection();
        injectedProtocolBlock.setLibrary(path);
        Assert.assertEquals(injectedProtocolBlock.getNumOfLibraryLines(), 5);
    }

    @Test
    public void testGetLibraryLine() throws Exception {
        Byte[] bytes = {null};
        InjectedProtocolBlock injectedProtocolBlock = new InjectedProtocolBlock(ProtocolBlock.Type.VAR, bytes);
        Path path = Paths.get(getClass().getResource("/library.txt").toURI());
        injectedProtocolBlock.setLibrary(path);
        for (int i = 0; i < injectedProtocolBlock.getNumOfLibraryLines(); i++) {
            Assert.assertEquals(injectedProtocolBlock.getLibraryLine(i), new byte[]{(byte) (65 + i)});
        }
    }

    @Test
    public void testGetRandomLibraryLine() throws Exception {
        Byte[] bytes = {null};
        InjectedProtocolBlock injectedProtocolBlock = new InjectedProtocolBlock(ProtocolBlock.Type.VAR, bytes);
        Path path = Paths.get(getClass().getResource("/library.txt").toURI());
        injectedProtocolBlock.setLibraryInjection();
        injectedProtocolBlock.setLibrary(path);
        int[] counts = new int[injectedProtocolBlock.getNumOfLibraryLines()];
        for (int i = 0; i < 1000; i++) {
            byte[] b = injectedProtocolBlock.getRandomLibraryLine();
            counts[b[0] % 65]++;
        }
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int each : counts) {
            min = Math.min(each, min);
            max = Math.max(each, max);
        }
        Assert.assertTrue(max - min < 50);
    }
}
