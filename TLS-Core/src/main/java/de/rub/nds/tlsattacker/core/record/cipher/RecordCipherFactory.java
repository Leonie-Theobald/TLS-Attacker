/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.record.cipher;

import de.rub.nds.tlsattacker.core.constants.AlgorithmResolver;
import de.rub.nds.tlsattacker.core.constants.CipherType;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Robert Merget <robert.merget@rub.de>
 */
public class RecordCipherFactory {

    private static final Logger LOGGER = LogManager.getLogger(RecordCipherFactory.class);

    public static RecordCipher getRecordCipher(TlsContext context) {
        if (context.getSelectedCipherSuite() == null) {
            return new RecordNullCipher();
        } else {
            CipherType type = AlgorithmResolver.getCipherType(context.getChooser().getSelectedCipherSuite());
            switch (type) {
                case AEAD:
                    return new RecordAEADCipher(context);
                case BLOCK:
                    return new RecordBlockCipher(context);
                case STREAM:
                    return new RecordStreamCipher(context);
            }
            LOGGER.warn("UnknownCipherType:" + type.name());
            return new RecordNullCipher();
        }
    }

    private RecordCipherFactory() {
    }
}