package com.netthreads.osc.note.service;

/**
 * Note definition holds note value and OSC channel.
 *
 */
public class NoteDefinition
{
	private int note;
	private int channel;
	
	public NoteDefinition(int note, int channel)
	{
		this.note = note;
		this.channel = channel;
	}
	
	public int getNote()
	{
		return note;
	}
	
	public int getChannel()
	{
		return channel;
	}
	
}
