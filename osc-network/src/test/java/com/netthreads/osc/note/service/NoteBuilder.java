package com.netthreads.osc.note.service;

import com.google.inject.ImplementedBy;

@ImplementedBy(NoteBuilderImpl.class)  
public interface NoteBuilder
{
	/**
	 * Begin building note.
	 * 
	 */
	public void begin();
	
	/**
	 * Add note on message.
	 * 
	 * @param noteDefinition
	 */
	public void note(NoteDefinition noteDefinition);
	
	/**
	 * End build note.
	 * 
	 */
	public void end();
}
