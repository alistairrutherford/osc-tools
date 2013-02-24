package com.netthreads.osc.note.service;

import com.google.inject.ImplementedBy;

@ImplementedBy(NoteModelImpl.class)
public interface NoteModel
{
	/**
	 * Play current bar and advance the bar index.
	 * 
	 * @return The bar played.
	 */
	public int play();

	/**
	 * Reset the model.
	 * 
	 */
	public void reset();

	/**
	 * Get index of bar played.
	 * 
	 * @return The index of bar played.
	 */
	public int getPlayed();

	/**
	 * Get state of note in model.
	 * 
	 * @param panel
	 * @param bar
	 * @param note
	 * 
	 * @return True if set.
	 */
	public boolean getState(int panel, int bar, int note);

	/**
	 * Set state of note in model.
	 * 
	 * @param panel
	 * @param bar
	 * @param note
	 * @param state
	 */
	public void setState(int panel, int bar, int note, boolean state);
	
	/**
	 * Return internal notes.
	 * 
	 * @return The notes.
	 */
	public NoteDefinition[][] getNotes();

}
