package com.netthreads.osc.note.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netthreads.network.osc.client.OSCClient;
import com.netthreads.osc.common.domain.OSCBundle;
import com.netthreads.osc.common.domain.OSCMessage;

@Singleton
public class NoteBuilderImpl implements NoteBuilder
{
	private static final String MESSAGE_ADDRESS_NOTEON = "/midi/note";

	private OSCBundle oscBundle;

	private OSCClient oscClient;

	@Inject
	public NoteBuilderImpl(OSCClient oscClient)
	{
		this.oscClient = oscClient;
	}

	@Override
	public void begin()
	{
		// Get OSC bundle
		oscBundle = OSCBundle.$();
	}

	@Override
	public void note(NoteDefinition noteDefinition)
	{
		OSCMessage oscMessage = OSCMessage.$(MESSAGE_ADDRESS_NOTEON);

		oscMessage.addArgument(noteDefinition.getNote()); // note
		oscMessage.addArgument(127); // velocity
		oscMessage.addArgument(noteDefinition.getChannel()); // channel

		oscBundle.addMessage(oscMessage);
	}

	@Override
	public void end()
	{
		// If there are messages to send then send them.
		if (oscBundle.getMessages().size() > 0)
		{
			oscClient.send(oscBundle);
			
			//Gdx.app.log("oscBundle", " "+System.identityHashCode(oscBundle));
			
			oscBundle.free();
		}

	}

}
