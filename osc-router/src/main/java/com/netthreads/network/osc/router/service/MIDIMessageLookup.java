package com.netthreads.network.osc.router.service;

import com.google.inject.ImplementedBy;

/**
 * We define interface in case we choose to inject it.
 *
 */
@ImplementedBy(MIDIMessageLookupImpl.class)
public interface MIDIMessageLookup
{
	int getMessage(String name);
}
