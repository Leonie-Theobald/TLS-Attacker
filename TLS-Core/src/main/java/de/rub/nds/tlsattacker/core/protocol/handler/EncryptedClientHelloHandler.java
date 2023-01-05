/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.protocol.handler;

import de.rub.nds.tlsattacker.core.layer.context.TlsContext;
import de.rub.nds.tlsattacker.core.protocol.message.EncryptedClientHelloMessage;

public class EncryptedClientHelloHandler
        extends CoreClientHelloHandler<EncryptedClientHelloMessage> {

    public EncryptedClientHelloHandler(TlsContext tlsContext) {
        super(tlsContext);
    }
}