package com.netthreads.osc.common.domain;

import com.netthreads.osc.common.collections.Poolable;
import com.netthreads.osc.common.collections.Resettable;


/**
 * The unit of transmission of OSC is an OSC Packet. Any application that sends
 * OSC Packets is an OSC Client; any application that receives OSC Packets is an
 * OSC Server.
 * 
 */
public interface OSCPacket extends Resettable, Poolable
{
}
