package com.netthreads.network.osc.router.service;

import com.google.inject.ImplementedBy;

@ImplementedBy(MessageLookupImpl.class)
public interface MessageLookup
{
	int getMessage(String name);
}
