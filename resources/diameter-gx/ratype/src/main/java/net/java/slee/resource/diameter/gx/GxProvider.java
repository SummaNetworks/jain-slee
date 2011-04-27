/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package net.java.slee.resource.diameter.gx;

import java.io.IOException;

import net.java.slee.resource.diameter.Validator;
import net.java.slee.resource.diameter.base.CreateActivityException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.gx.events.GxCreditControlAnswer;
import net.java.slee.resource.diameter.gx.events.GxCreditControlRequest;

/**
 * The SBB interface for the Diameter Gx Resource Adaptor.
 *
 * This API can be used in either an asynchronous or synchronous manner.
 *
 * To send messages asynchronously, create a GxClientSessionActivity using one of the createGxClientSessionActivity() methods.
 *
 * To send messages synchronously, use the following methods:
 * <ul>eventCreditControlRequest(CreditControlRequest)</ul>
 * <ul>initialCreditControlRequest(CreditControlRequest)</ul>
 * <ul>updateCreditControlRequest(CreditControlRequest)</ul>
 * <ul>terminationCreditControlRequest(CreditControlRequest)</ul>
 *
 * The Credit-Control-Request messages must be created using the GxMessageFactory returned from getGxMessageFactory().
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:carl-magnus.bjorkell@emblacom.com"> Carl-Magnus Björkell </a>
 */
public interface GxProvider {

    /**
     * Return a message factory to be used to create concrete implementations of credit control messages.
     *
     * @return an instance of a GxMessageFactory.
     */
    GxMessageFactory getGxMessageFactory();

    /**
     * Return an AVP factory to be used to create concrete implementations of credit control AVPs.
     *
     * @return an AVP factory instance.
     */
    GxAvpFactory getGxAvpFactory();

    /**
     * Create a new activity to send and receive Diameter messages.
     *
     * @return a DiameterActivity
     * @throws CreateActivityException if the RA could not create the activity for any reason
     */
    GxClientSessionActivity createGxClientSessionActivity() throws CreateActivityException;

    /**
     * Create a new activity to send and receive Diameter messages.
     *
     * @param destinationHost a destination host to automatically put in all messages
     * @param destinationRealm a destination realm to automatically put in all messages
     * @return a DiameterActivity
     * @throws CreateActivityException if the RA could not create the activity for any reason
     */
    GxClientSessionActivity createGxClientSessionActivity(DiameterIdentity destinationHost, DiameterIdentity destinationRealm) throws CreateActivityException;

    /**
     * Send a Credit-Control-Request message to the appropriate peers, and block until the response is received then return it.
     *
     * @param ccr the CreditControlRequest to send
     * @return the answer received
     * @throws IOException if an error occurred sending the request to the peer
     */
    GxCreditControlAnswer sendGxCreditControlRequest(GxCreditControlRequest ccr) throws IOException;

    /**
     * Return the number of peers this Diameter resource adaptor is connected to.
     *
     * @return connected peer count
     */
    int getPeerCount();

    /**
     * Returns array containing identities of connected peers.
     *
     * @return
     */
    DiameterIdentity[] getConnectedPeers();

    /**
     * Returns the Diameter Validator
     * 
     * @return
     */
    Validator getValidator();

}
