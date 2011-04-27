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

package net.java.slee.resource.diameter.gx.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.base.events.avp.IPFilterRule;
/**
 * Defines an interface representing the TFT-Packet-Filter-Information grouped AVP type.<br>
 * <br>
 * Charging rule provisioning over Gx interface(3GPP TS 29.210 V6.7.0) specification:
 * <pre>
 *  5.2.19 TFT-Packet-Filter-Information AVP
    The TFT-Packet-Filter-Information AVP (AVP code 1013) is of type Grouped, and it contains
 *  the information from a single TFT packet filter including the evaluation precedence,
 *  the filter and the Type-of-Service/Traffic Class sent from the TPF to the CRF.
 *  The TPF shall include one TFT-Packet-Filter-Information AVP for each TFT packet filters
 *  applicable at a PDP context in separate TFT-Packet-Filter-Information AVPs within each
 *  charging rule request. corresponding to that PDP context. TFT-Packet-Filter-Information AVPs
 *  are derived from the Traffic Flow Template (TFT) defined in 3GPP TS 24.008 [14].
 *  When SBLP is used the packet filters shall be omitted.
 *  AVP Format:
 *  TFT-Packet-Filter-Information ::= < AVP Header: 1013>
 *                                   [ Precedence ]
 *                                   [ TFT-Filter ]
 *                                   [ ToS-Traffic-Class ]
 * </pre>
 *
 * @author <a href="mailto:karthikeyan_s@spanservices.com"> Karthikeyan Shanmugam (EmblaCom)</a>
 */
public interface TFTPacketFilterInformation extends GroupedAvp {

    /**
     * Returns the value of the Precedence AVP, of type Unsigned32.
     * @return long
     */
    abstract long getPrecedence();

    /**
     * Returns the value of the TFT-Filter AVP, of type Octet IPFilterRule.
     * @return IPFilterRule
     */
    abstract IPFilterRule getTFTFilter();

    /**
     * Returns the value of the ToS-Traffic-Class AVP, of type Octet String.
     * @return String
     */
    abstract String getTosTrafficClass();

     /**
     * Returns true if the Precedence AVP is present in the message.
     * @return boolean
     */
    abstract boolean hasPrecedence();

     /**
     * Returns true if the TFT-Filter AVP is present in the message.
     * @return boolean
     */
    abstract boolean hasTFTFilter();

     /**
     * Returns true if the ToS-Traffic-Class AVP is present in the message.
     * @return boolean
     */
    abstract boolean hasTosTrafficClass();

    /**
     * Sets the value of the Precedence AVP, of type Unsigned32.
     * @param flowDescription
     */
    abstract void setPrecedence(long precedence);

    /**
     * Sets the value of the TFT-Filter-Base-Name AVP, of type IPFilterRule.
     * @param flowDescription
     */
    abstract void setTFTFilter(IPFilterRule tftFilter);

    /**
     * Sets the value of the ToS-Traffic-Class AVP, of type Octet String.
     * @param flowDescription
     */
    abstract void setTosTrafficClass(String tosTrafficClass);

}
