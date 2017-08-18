/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.workflow.factory;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.AlgorithmResolver;
import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.constants.KeyExchangeAlgorithm;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.exceptions.ConfigurationException;
import de.rub.nds.tlsattacker.core.protocol.message.ApplicationMessage;
import de.rub.nds.tlsattacker.core.protocol.message.CertificateMessage;
import de.rub.nds.tlsattacker.core.protocol.message.CertificateRequestMessage;
import de.rub.nds.tlsattacker.core.protocol.message.CertificateVerifyMessage;
import de.rub.nds.tlsattacker.core.protocol.message.ChangeCipherSpecMessage;
import de.rub.nds.tlsattacker.core.protocol.message.ClientHelloMessage;
import de.rub.nds.tlsattacker.core.protocol.message.DHClientKeyExchangeMessage;
import de.rub.nds.tlsattacker.core.protocol.message.DHEServerKeyExchangeMessage;
import de.rub.nds.tlsattacker.core.protocol.message.ECDHClientKeyExchangeMessage;
import de.rub.nds.tlsattacker.core.protocol.message.ECDHEServerKeyExchangeMessage;
import de.rub.nds.tlsattacker.core.protocol.message.EncryptedExtensionsMessage;
import de.rub.nds.tlsattacker.core.protocol.message.FinishedMessage;
import de.rub.nds.tlsattacker.core.protocol.message.HeartbeatMessage;
import de.rub.nds.tlsattacker.core.protocol.message.HelloRequestMessage;
import de.rub.nds.tlsattacker.core.protocol.message.HelloVerifyRequestMessage;
import de.rub.nds.tlsattacker.core.protocol.message.ProtocolMessage;
import de.rub.nds.tlsattacker.core.protocol.message.RSAClientKeyExchangeMessage;
import de.rub.nds.tlsattacker.core.protocol.message.SSL2ClientHelloMessage;
import de.rub.nds.tlsattacker.core.protocol.message.SSL2ServerHelloMessage;
import de.rub.nds.tlsattacker.core.protocol.message.ServerHelloDoneMessage;
import de.rub.nds.tlsattacker.core.protocol.message.ServerHelloMessage;
import de.rub.nds.tlsattacker.core.record.BlobRecord;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTrace;
import de.rub.nds.tlsattacker.core.workflow.action.MessageAction;
import de.rub.nds.tlsattacker.core.workflow.action.MessageActionFactory;
import de.rub.nds.tlsattacker.core.workflow.action.RenegotiationAction;
import de.rub.nds.tlsattacker.core.workflow.action.TLSAction;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Juraj Somorovsky <juraj.somorovsky@rub.de>
 * @author Philip Riese <philip.riese@rub.de>
 * @author Nurullah Erinola <nurullah.erinola@rub.de>
 */
public class WorkflowConfigurationFactory {

    static final Logger LOGGER = LogManager.getLogger(WorkflowConfigurationFactory.class);

    protected final Config config;

    public WorkflowConfigurationFactory(Config config) {
        this.config = config;
    }

    public WorkflowTrace createWorkflowTrace(WorkflowTraceType type) {
        switch (type) {
            case HELLO:
                return createHelloWorkflow();
            case FULL:
                return createFullWorkflow();
            case HANDSHAKE:
                return createHandshakeWorkflow();
            case SHORT_HELLO:
                return createShortHelloWorkflow();
            case SSL2_HELLO:
                return createSsl2HelloWorkflow();
            case CLIENT_RENEGOTIATION:
                return createClientRenegotiationWorkflow();
            case SERVER_RENEGOTIATION:
                return createServerRenegotiationWorkflow();
            case RESUMPTION:
                return new WorkflowTrace(); // TODO add real workflow
        }
        throw new ConfigurationException("Unknown WorkflowTraceType " + type.name());
    }

    public WorkflowTrace createHelloWorkflow() {
        WorkflowTrace workflowTrace = new WorkflowTrace();
        List<ProtocolMessage> messages = new LinkedList<>();
        ClientHelloMessage clientHello = null;
        if (config.getHighestProtocolVersion() == ProtocolVersion.DTLS10
                || config.getHighestProtocolVersion() == ProtocolVersion.DTLS12) {
            clientHello = new ClientHelloMessage(config);
            clientHello.setIncludeInDigest(false);
        } else {
            clientHello = new ClientHelloMessage(config);
        }
        messages.add(clientHello);

        workflowTrace.addTlsAction(MessageActionFactory.createAction(config.getConnectionEndType(),
                ConnectionEndType.CLIENT, messages));
        if (config.getHighestProtocolVersion() == ProtocolVersion.DTLS10
                || config.getHighestProtocolVersion() == ProtocolVersion.DTLS12) {

            HelloVerifyRequestMessage helloVerifyRequestMessage = new HelloVerifyRequestMessage(config);
            helloVerifyRequestMessage.setIncludeInDigest(false);
            messages = new LinkedList<>();

            messages.add(helloVerifyRequestMessage);
            workflowTrace.addTlsAction(MessageActionFactory.createAction(config.getConnectionEndType(),
                    ConnectionEndType.SERVER, messages));
            clientHello = new ClientHelloMessage(config);
            messages = new LinkedList<>();
            messages.add(clientHello);
            workflowTrace.addTlsAction(MessageActionFactory.createAction(config.getConnectionEndType(),
                    ConnectionEndType.CLIENT, messages));
        }
        messages = new LinkedList<>();
        messages.add(new ServerHelloMessage(config));

        if (config.getHighestProtocolVersion().isTLS13()) {
            messages.add(new EncryptedExtensionsMessage(config));
            if (config.isClientAuthentication()) {
                CertificateRequestMessage certRequest = new CertificateRequestMessage(config);
                messages.add(certRequest);
            }
            if (config.getConnectionEndType() == ConnectionEndType.CLIENT) {
                messages.add(new CertificateMessage());
            } else {
                messages.add(new CertificateMessage(config));
            }
            messages.add(new CertificateVerifyMessage(config));
            messages.add(new FinishedMessage(config));
        } else {
            if (config.getConnectionEndType() == ConnectionEndType.CLIENT) {
                messages.add(new CertificateMessage());
            } else {
                messages.add(new CertificateMessage(config));
            }
            if (config.getDefaultSelectedCipherSuite().isEphemeral()) {
                addServerKeyExchangeMessage(messages);
            }
            if (config.isClientAuthentication()) {
                CertificateRequestMessage certRequest = new CertificateRequestMessage(config);
                messages.add(certRequest);
            }
            messages.add(new ServerHelloDoneMessage(config));
        }
        workflowTrace.addTlsAction(MessageActionFactory.createAction(config.getConnectionEndType(),
                ConnectionEndType.SERVER, messages));
        return workflowTrace;
    }

    public WorkflowTrace createHandshakeWorkflow() {
        WorkflowTrace workflowTrace = this.createHelloWorkflow();
        List<ProtocolMessage> messages = new LinkedList<>();
        if (config.getHighestProtocolVersion().isTLS13()) {
            if (config.isClientAuthentication()) {
                messages.add(new CertificateMessage(config));
                messages.add(new CertificateVerifyMessage(config));
            }
        } else {
            if (config.isClientAuthentication()) {
                messages.add(new CertificateMessage(config));
                addClientKeyExchangeMessage(messages);
                messages.add(new CertificateVerifyMessage(config));
            } else {
                addClientKeyExchangeMessage(messages);
            }
            messages.add(new ChangeCipherSpecMessage(config));
        }
        messages.add(new FinishedMessage(config));
        workflowTrace.addTlsAction(MessageActionFactory.createAction(config.getConnectionEndType(),
                ConnectionEndType.CLIENT, messages));
        if (!config.getHighestProtocolVersion().isTLS13()) {
            messages = new LinkedList<>();
            messages.add(new ChangeCipherSpecMessage(config));
            messages.add(new FinishedMessage(config));
            workflowTrace.addTlsAction(MessageActionFactory.createAction(config.getConnectionEndType(),
                    ConnectionEndType.SERVER, messages));
        }
        return workflowTrace;
    }

    private void addClientKeyExchangeMessage(List<ProtocolMessage> messages) {
        CipherSuite cs = config.getDefaultClientSupportedCiphersuites().get(0);
        KeyExchangeAlgorithm algorithm = AlgorithmResolver.getKeyExchangeAlgorithm(cs);
        if (algorithm != null) {

            switch (algorithm) {
                case RSA:
                    messages.add(new RSAClientKeyExchangeMessage(config));
                    break;
                case ECDHE_ECDSA:
                case ECDH_ECDSA:
                case ECDH_RSA:
                case ECDHE_RSA:
                    messages.add(new ECDHClientKeyExchangeMessage(config));
                    break;
                case DHE_DSS:
                case DHE_RSA:
                case DH_ANON:
                case DH_DSS:
                case DH_RSA:
                    messages.add(new DHClientKeyExchangeMessage(config));
                    break;
                default:
                    LOGGER.warn("Unsupported key exchange algorithm: " + algorithm
                            + ", not adding ClientKeyExchange Message");
                    break;
            }
        } else {
            LOGGER.warn("Unsupported key exchange algorithm: " + algorithm + ", not adding ClientKeyExchange Message");
        }
    }

    private void addServerKeyExchangeMessage(List<ProtocolMessage> messages) {
        CipherSuite cs = config.getDefaultSelectedCipherSuite();
        if (cs.isEphemeral()) {
            switch (AlgorithmResolver.getKeyExchangeAlgorithm(cs)) {
                case ECDHE_ECDSA:
                case ECDHE_RSA:
                    messages.add(new ECDHEServerKeyExchangeMessage(config));
                    break;
                case DHE_DSS:
                case DHE_RSA:
                    messages.add(new DHEServerKeyExchangeMessage(config));
                    break;
                default:
                    LOGGER.warn("Unsupported key exchange algorithm: " + AlgorithmResolver.getKeyExchangeAlgorithm(cs)
                            + ", not adding ServerKeyExchange Message");
                    break;
            }
        } else {
            LOGGER.debug("Not adding ServerKeyExchange message - " + cs.name() + " is not an Ephermaral Ciphersuite");
        }
    }

    /**
     * Creates an extended TLS workflow including an application data and
     * heartbeat messages
     *
     * @return
     */
    public WorkflowTrace createFullWorkflow() {
        WorkflowTrace workflowTrace = this.createHandshakeWorkflow();
        List<ProtocolMessage> messages = new LinkedList<>();
        if (config.isServerSendsApplicationData()) {
            messages.add(new ApplicationMessage(config));
            workflowTrace.addTlsAction(MessageActionFactory.createAction(config.getConnectionEndType(),
                    ConnectionEndType.SERVER, messages));
            messages = new LinkedList<>();
        }
        messages.add(new ApplicationMessage(config));

        if (config.isAddHeartbeatExtension()) {
            messages.add(new HeartbeatMessage(config));
            workflowTrace.addTlsAction(MessageActionFactory.createAction(config.getConnectionEndType(),
                    ConnectionEndType.CLIENT, messages));
            messages = new LinkedList<>();
            messages.add(new HeartbeatMessage(config));
            workflowTrace.addTlsAction(MessageActionFactory.createAction(config.getConnectionEndType(),
                    ConnectionEndType.SERVER, messages));
        } else {
            workflowTrace.addTlsAction(MessageActionFactory.createAction(config.getConnectionEndType(),
                    ConnectionEndType.CLIENT, messages));
        }
        return workflowTrace;
    }

    private WorkflowTrace createShortHelloWorkflow() {
        WorkflowTrace trace = new WorkflowTrace();
        trace.addTlsAction(MessageActionFactory.createAction(config.getConnectionEndType(), ConnectionEndType.CLIENT,
                new ClientHelloMessage(config)));
        trace.addTlsAction(MessageActionFactory.createAction(config.getConnectionEndType(), ConnectionEndType.SERVER,
                new ServerHelloMessage(config)));
        return trace;
    }

    private WorkflowTrace createSsl2HelloWorkflow() {
        WorkflowTrace trace = new WorkflowTrace();
        MessageAction action = MessageActionFactory.createAction(config.getConnectionEndType(),
                ConnectionEndType.CLIENT, new SSL2ClientHelloMessage(config));
        action.setRecords(new BlobRecord());
        trace.addTlsAction(action);
        action = MessageActionFactory.createAction(config.getConnectionEndType(), ConnectionEndType.SERVER,
                new SSL2ServerHelloMessage(config));
        action.setRecords(new BlobRecord());
        trace.addTlsAction(action);
        return trace;
    }

    private WorkflowTrace createClientRenegotiationWorkflow() {
        WorkflowTrace trace = this.createHandshakeWorkflow();
        trace.addTlsAction(new RenegotiationAction());
        WorkflowTrace renegotiationTrace = this.createHandshakeWorkflow();
        for (TLSAction reneAction : renegotiationTrace.getTlsActions()) {
            trace.addTlsAction(reneAction);
        }
        return trace;
    }

    private WorkflowTrace createServerRenegotiationWorkflow() {
        WorkflowTrace trace = this.createHandshakeWorkflow();
        WorkflowTrace renegotiationTrace = this.createHandshakeWorkflow();
        trace.addTlsAction(new RenegotiationAction());
        MessageAction action = MessageActionFactory.createAction(config.getConnectionEndType(),
                ConnectionEndType.SERVER, new HelloRequestMessage(config));
        trace.addTlsAction(action);
        for (TLSAction reneAction : renegotiationTrace.getTlsActions()) {
            trace.addTlsAction(reneAction);
        }
        return trace;
    }
}