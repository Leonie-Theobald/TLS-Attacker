/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.config.delegate;

import com.beust.jcommander.JCommander;
import de.rub.nds.tlsattacker.core.config.Config;
import org.apache.commons.lang3.builder.EqualsBuilder;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Robert Merget - robert.merget@rub.de
 */
public class TimeoutDelegateTest {

    private TimeoutDelegate delegate;
    private JCommander jcommander;
    private String[] args;

    @Before
    public void setUp() {
        this.delegate = new TimeoutDelegate();
        this.jcommander = new JCommander(delegate);
    }

    /**
     * Test of getTimeout method, of class TimeoutDelegate.
     */
    @Test
    public void testGetTimeout() {
        args = new String[2];
        args[0] = "-timeout";
        args[1] = "123";
        assertTrue(delegate.getTimeout() == null);
        jcommander.parse(args);
        assertTrue(delegate.getTimeout() == 123);
    }

    /**
     * Test of setTimeout method, of class TimeoutDelegate.
     */
    @Test
    public void testSetTimeout() {
        assertTrue(delegate.getTimeout() == null);
        delegate.setTimeout(123);
        assertTrue(delegate.getTimeout() == 123);
    }

    /**
     * Test of applyDelegate method, of class TimeoutDelegate.
     */
    @Test
    public void testApplyDelegate() {
        Config config = Config.createConfig();
        args = new String[2];
        args[0] = "-timeout";
        args[1] = "123";
        jcommander.parse(args);
        delegate.applyDelegate(config);
        assertTrue(config.getTimeout() == 123);
    }

    @Test
    public void testNothingSetNothingChanges() {
        Config config = Config.createConfig();
        Config config2 = Config.createConfig();
        delegate.applyDelegate(config);
        assertTrue(EqualsBuilder.reflectionEquals(config, config2, "keyStore", "ourCertificate"));// little
        // ugly
    }
}