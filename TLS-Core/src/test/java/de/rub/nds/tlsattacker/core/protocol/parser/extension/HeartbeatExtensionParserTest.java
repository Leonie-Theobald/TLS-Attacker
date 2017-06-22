/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.protocol.parser.extension;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.constants.ExtensionType;
import de.rub.nds.tlsattacker.core.protocol.message.extension.HeartbeatExtensionMessage;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Robert Merget - robert.merget@rub.de
 */
@RunWith(Parameterized.class)
public class HeartbeatExtensionParserTest {

    @Parameterized.Parameters
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][] { { ArrayConverter.hexStringToByteArray("000f000101"),
                ExtensionType.HEARTBEAT, 1, new byte[] { 1 } } }); // is the
                                                                   // same for
                                                                   // TLS10 and
                                                                   // TLS11
    }

    private byte[] extension;
    private int start;
    private byte[] completeExtension;
    private ExtensionType type;
    private int extensionLength;
    private byte[] heartbeatMode;

    public HeartbeatExtensionParserTest(byte[] extension, ExtensionType type, int extensionLength, byte[] heartbeatMode) {
        this.extension = extension;
        this.start = 0;
        this.completeExtension = extension;
        this.type = type;
        this.extensionLength = extensionLength;
        this.heartbeatMode = heartbeatMode;
    }

    /**
     * Test of parseExtensionMessageContent method, of class
     * HeartbeatExtensionParser.
     */
    @Test
    public void testParseExtensionMessageContent() {
        HeartbeatExtensionParser parser = new HeartbeatExtensionParser(start, extension);
        HeartbeatExtensionMessage msg = parser.parse();
        assertArrayEquals(msg.getExtensionBytes().getValue(), completeExtension);
        assertArrayEquals(type.getValue(), msg.getExtensionType().getValue());
        assertTrue(extensionLength == msg.getExtensionLength().getValue());
        assertArrayEquals(heartbeatMode, msg.getHeartbeatMode().getValue());
    }
}
