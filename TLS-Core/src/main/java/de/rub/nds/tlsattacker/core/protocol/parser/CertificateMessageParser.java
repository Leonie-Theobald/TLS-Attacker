/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.protocol.parser;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.HandshakeByteLength;
import de.rub.nds.tlsattacker.core.constants.HandshakeMessageType;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.protocol.message.CertificateMessage;
import de.rub.nds.tlsattacker.core.protocol.message.cert.CertificateEntry;
import de.rub.nds.tlsattacker.core.protocol.message.cert.CertificatePair;
import de.rub.nds.tlsattacker.core.protocol.message.extension.ExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.cert.CertificatePairParser;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.ExtensionListParser;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CertificateMessageParser extends HandshakeMessageParser<CertificateMessage> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final ConnectionEndType talkingConnectionEndType;

    /**
     * Constructor for the Parser class
     *
     * @param stream
     * @param version Version of the Protocol
     * @param config A Config used in the current context
     * @param talkingConnectionEndType
     */
    public CertificateMessageParser(InputStream stream, ProtocolVersion version, Config config, ConnectionEndType talkingConnectionEndType) {
        super(stream, HandshakeMessageType.CERTIFICATE, version, config);
        this.talkingConnectionEndType = talkingConnectionEndType;
    }

    @Override
    protected void parseHandshakeMessageContent(CertificateMessage msg) {
        LOGGER.debug("Parsing CertificateMessage");
        if (getVersion().isTLS13()) {
            parseRequestContextLength(msg);
            parseRequestContextBytes(msg);
        }
        parseCertificatesListLength(msg);
        parseCertificateListBytes(msg);
        if (getVersion().isTLS13()) {
            parseCertificateList(msg);
        }
    }

    @Override
    protected CertificateMessage createHandshakeMessage() {
        return new CertificateMessage();
    }

    /**
     * Reads the next bytes as the RequestContextLength and writes them in the
     * message
     *
     * @param msg Message to write in
     */
    private void parseRequestContextLength(CertificateMessage msg) {
        msg.setRequestContextLength(parseIntField(HandshakeByteLength.CERTIFICATE_REQUEST_CONTEXT_LENGTH));
        LOGGER.debug("RequestContextLength: " + msg.getRequestContextLength());
    }

    /**
     * Reads the next bytes as the requestContextBytes and writes them in the
     * message
     *
     * @param msg Message to write in
     */
    private void parseRequestContextBytes(CertificateMessage msg) {
        msg.setRequestContext(parseByteArrayField(msg.getRequestContextLength().getValue()));
        LOGGER.debug("RequestContextBytes: " + ArrayConverter.bytesToHexString(msg.getRequestContext()));
    }

    /**
     * Reads the next bytes as the CertificateLength and writes them in the
     * message
     *
     * @param msg Message to write in
     */
    private void parseCertificatesListLength(CertificateMessage msg) {
        msg.setCertificatesListLength(parseIntField(HandshakeByteLength.CERTIFICATES_LENGTH));
        LOGGER.debug("CertificatesListLength: " + msg.getCertificatesListLength());
    }

    /**
     * Reads the next bytes as the CertificateBytes and writes them in the
     * message
     *
     * @param msg Message to write in
     */
    private void parseCertificateListBytes(CertificateMessage msg) {
        msg.setCertificatesListBytes(parseByteArrayField(msg.getCertificatesListLength().getValue()));
        LOGGER.debug("CertificatesListBytes: " + ArrayConverter.bytesToHexString(msg.getCertificatesListBytes()));
    }

    /**
     * Reads the bytes from the CertificateListBytes and writes them in the
     * CertificateList
     *
     * @param msg Message to write in
     */
    private void parseCertificateList(CertificateMessage msg) {
        List<CertificatePair> pairList = new LinkedList<>();
        ByteArrayInputStream innerStream = new ByteArrayInputStream(msg.getCertificatesListBytes().getValue());
        while (innerStream.available() > 0) {
            CertificatePairParser parser = new CertificatePairParser(innerStream);
            pairList.add(parser.parse());
        }
        msg.setCertificatesList(pairList);

        List<CertificateEntry> entryList = new LinkedList<>();
        for (CertificatePair pair : msg.getCertificatesList()) {
            ExtensionListParser parser = new ExtensionListParser(new ByteArrayInputStream(pair.getExtensions().getValue()), config, talkingConnectionEndType, getVersion(), false);
            List<ExtensionMessage> extensionMessages = parser.parse();
            entryList.add(new CertificateEntry(pair.getCertificate().getValue(), extensionMessages));
        }
        msg.setCertificatesListAsEntry(entryList);
    }
}
