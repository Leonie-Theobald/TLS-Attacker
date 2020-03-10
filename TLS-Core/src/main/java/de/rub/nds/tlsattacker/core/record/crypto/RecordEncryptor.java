/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.record.crypto;

import de.rub.nds.tlsattacker.core.exceptions.CryptoException;
import de.rub.nds.tlsattacker.core.record.BlobRecord;
import de.rub.nds.tlsattacker.core.record.Record;
import de.rub.nds.tlsattacker.core.record.cipher.RecordCipher;
import de.rub.nds.tlsattacker.core.record.cipher.RecordNullCipher;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecordEncryptor extends Encryptor {

    private static final Logger LOGGER = LogManager.getLogger();

    private final TlsContext context;

    private final RecordNullCipher nullCipher;

    public RecordEncryptor(RecordCipher recordCipher, TlsContext context) {
        super(recordCipher);
        this.context = context;
        nullCipher = new RecordNullCipher(context);
    }

    @Override
    public void encrypt(BlobRecord record) {
        LOGGER.debug("Encrypting BlobRecord");

        try {
            recordCipher.encrypt(record);
        } catch (CryptoException ex) {
            LOGGER.warn("Could not encrypt BlobRecord. Using NullCipher");
            try {
                nullCipher.encrypt(record);
            } catch (CryptoException ex1) {
                LOGGER.error("Could not encrypt with NullCipher", ex1);
            }
        }
        context.increaseWriteSequenceNumber();
    }

    @Override
    public void encrypt(Record record) {

        LOGGER.debug("Encrypting Record:");

        try {
            recordCipher.encrypt(record);
        } catch (CryptoException ex) {
            LOGGER.warn("Could not encrypt BlobRecord. Using NullCipher");
            try {
                nullCipher.encrypt(record);
            } catch (CryptoException ex1) {
                LOGGER.error("Could not encrypt with NullCipher", ex1);
            }
        }
        context.increaseWriteSequenceNumber();
    }
}
