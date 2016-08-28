/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.monitor.protocol;

import model.monitor.message.ReceiveMessage;
import model.monitor.message.ReceiveMessage.Command;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import support.StreamSimulator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("HardCodedStringLiteral")
public class ProtocolTest {

    public static final byte[] BYTES = new byte[0];
    private StreamSimulator streamSimulator;

    @BeforeMethod
    public void setUp() throws IOException {
        streamSimulator = new StreamSimulator();
        streamSimulator.init();
    }

    @AfterMethod
    public void tearDown() throws IOException {
        streamSimulator.exit();
    }

    @Test
    public void testAyt1() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            streamSimulator.writeForInputStream("ROK 1 9");
            ReceiveMessage receiveMessage = protocol.ayt();
            Assert.assertEquals(receiveMessage.getCommand(), Command.ROK);
            Assert.assertEquals(receiveMessage.getBody(), String.valueOf(9).getBytes(StandardCharsets.UTF_8));
            Assert.assertEquals(streamSimulator.readLastFromOutputStream(), "AYT 0 ".getBytes(StandardCharsets.UTF_8));
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }

    @Test
    public void testAyt2() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            streamSimulator.writeForInputStream("ROK 1 9");
            protocol.ayt();
            streamSimulator.writeForInputStream("ROK 1 9");
            protocol.ayt();
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }
    }

    @Test
    public void testGco1() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            streamSimulator.writeForInputStream("ROK 1 9");
            protocol.ayt();
            streamSimulator.writeForInputStream("ROK 11 test1,test2");
            ReceiveMessage receiveMessage = protocol.gco();
            Assert.assertEquals(receiveMessage.getCommand(), Command.ROK);
            Assert.assertEquals(receiveMessage.getBody(), "test1,test2".getBytes(StandardCharsets.UTF_8));
            Assert.assertEquals(streamSimulator.readLastFromOutputStream(), "GCO 0 ".getBytes(StandardCharsets.UTF_8));
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }

    @Test
    public void testGco2() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            protocol.gco();
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }
    }

    @Test
    public void testSco1() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            streamSimulator.writeForInputStream("ROK 1 9");
            protocol.ayt();
            streamSimulator.writeForInputStream("ROK 0 ");
            ReceiveMessage receiveMessage = protocol.sco("test");
            Assert.assertEquals(receiveMessage.getCommand(), Command.ROK);
            Assert.assertEquals(receiveMessage.getBody(), BYTES);
            Assert.assertEquals(streamSimulator.readLastFromOutputStream(),
                    "SCO 4 test".getBytes(StandardCharsets.UTF_8));
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }

    @Test
    public void testSco2() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            protocol.sco("");
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }
    }

    @Test
    public void testScp1() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            streamSimulator.writeForInputStream("ROK 1 9");
            protocol.ayt();
            streamSimulator.writeForInputStream("ROK 0 ");
            protocol.sco("test");
            streamSimulator.writeForInputStream("ROK 0 ");
            Map<String, String> parameter = new HashMap<>(1);
            parameter.put("testkey1", "testvalue1");
            parameter.put("testkey2", "testvalue2");
            ReceiveMessage receiveMessage = protocol.scp(parameter);
            Assert.assertEquals(receiveMessage.getCommand(), Command.ROK);
            Assert.assertEquals(receiveMessage.getBody(), BYTES);
            Assert.assertEquals(streamSimulator.readLastFromOutputStream(),
                    ("SCP 39 testkey2=testvalue2," + "testkey1=testvalue1").getBytes(StandardCharsets.UTF_8));
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }

    @Test
    public void testScp2() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            protocol.scp(new HashMap<>(0));
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }
    }

    @Test
    public void testCtt1() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            streamSimulator.writeForInputStream("ROK 1 9");
            protocol.ayt();
            streamSimulator.writeForInputStream("ROK 0 ");
            protocol.sco("test");
            streamSimulator.writeForInputStream("ROK 13 crashed=false");
            ReceiveMessage receiveMessage = protocol.ctt();
            Assert.assertEquals(receiveMessage.getCommand(), Command.ROK);
            Assert.assertEquals(receiveMessage.getBody(), "crashed=false".getBytes(StandardCharsets.UTF_8));
            Assert.assertEquals(streamSimulator.readLastFromOutputStream(), "CTT 0 ".getBytes(StandardCharsets.UTF_8));
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }

    @Test
    public void testCtt2() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            protocol.ctt();
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }
    }

    @Test
    public void testGwa1() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            streamSimulator.writeForInputStream("ROK 1 9");
            protocol.ayt();
            streamSimulator.writeForInputStream("ROK 0 ");
            protocol.sco("test");
            streamSimulator.writeForInputStream("ROK 13 crashed=false");
            protocol.ctt();
            streamSimulator.writeForInputStream("ROK 21 testvalue1,testvalue2");
            ReceiveMessage receiveMessage = protocol.gwa("test");
            Assert.assertEquals(receiveMessage.getCommand(), Command.ROK);
            Assert.assertEquals(receiveMessage.getBody(), "testvalue1,testvalue2".getBytes(StandardCharsets.UTF_8));
            Assert.assertEquals(streamSimulator.readLastFromOutputStream(),
                    "GWA 4 test".getBytes(StandardCharsets.UTF_8));
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }

    @Test
    public void testGwa2() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            protocol.gwa("test");
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }
    }

    @Test
    public void testSwa1() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            streamSimulator.writeForInputStream("ROK 1 9");
            protocol.ayt();
            streamSimulator.writeForInputStream("ROK 0 ");
            protocol.sco("test");
            streamSimulator.writeForInputStream("ROK 13 crashed=false");
            protocol.ctt();
            streamSimulator.writeForInputStream("ROK 0 ");
            ReceiveMessage receiveMessage = protocol.swa("test");
            Assert.assertEquals(receiveMessage.getCommand(), Command.ROK);
            Assert.assertEquals(receiveMessage.getBody(), BYTES);
            Assert.assertEquals(streamSimulator.readLastFromOutputStream(),
                    "SWA 4 test".getBytes(StandardCharsets.UTF_8));
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }

    @Test
    public void testSwa2() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            protocol.swa("test");
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }
    }

    @Test
    public void testCtf1() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            streamSimulator.writeForInputStream("ROK 1 9");
            protocol.ayt();
            streamSimulator.writeForInputStream("ROK 0 ");
            protocol.sco("test");
            streamSimulator.writeForInputStream("ROK 13 crashed=false");
            protocol.ctt();
            streamSimulator.writeForInputStream("ROK 0 ");
            protocol.swa("test");
            streamSimulator.writeForInputStream("ROK 13 crashed=false");
            ReceiveMessage receiveMessage = protocol.ctf("abc123".getBytes(StandardCharsets.UTF_8));
            Assert.assertEquals(receiveMessage.getCommand(), Command.ROK);
            Assert.assertEquals(receiveMessage.getBody(), "crashed=false".getBytes(StandardCharsets.UTF_8));
            Assert.assertEquals(streamSimulator.readLastFromOutputStream(),
                    "CTF 6 abc123".getBytes(StandardCharsets.UTF_8));
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }

    @Test
    public void testCtf2() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            protocol.ctf();
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }
    }

    @Test
    public void testRst1() throws IOException {
        try (DataInputStream dataInputStream = streamSimulator.getDataInputStream();
                DataOutputStream dataOutputStream = streamSimulator.getDataOutputStream()) {
            Protocol protocol = new Protocol(dataInputStream, dataOutputStream);
            streamSimulator.writeForInputStream("ROK 1 9");
            protocol.ayt();
            streamSimulator.writeForInputStream("ROK 0 ");
            ReceiveMessage receiveMessage = protocol.rst();
            Assert.assertEquals(receiveMessage.getCommand(), Command.ROK);
            Assert.assertEquals(receiveMessage.getBody(), BYTES);
            Assert.assertEquals(streamSimulator.readLastFromOutputStream(), "RST 0 ".getBytes(StandardCharsets.UTF_8));
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }


}
