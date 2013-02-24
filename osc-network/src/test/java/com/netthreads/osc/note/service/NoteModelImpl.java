package com.netthreads.osc.note.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netthreads.osc.network.ClientServerTest;

/**
 * Note panel and bar matrix model.
 * 
 */
@Singleton
public class NoteModelImpl implements NoteModel
{
	private static final Logger logger = LoggerFactory.getLogger(ClientServerTest.class);

	public static final int COUNT_BARS = 16;
	public static final int COUNT_BARS_RESET = COUNT_BARS - 1;
	public static final int COUNT_NOTES = 8;
	public static final int COUNT_PANELS = 4;

	private int bar;

	private boolean[][][] matrix;
	private NoteDefinition[][] notes;

	private boolean[][] last;

	private NoteBuilder noteBuilder;

	/**
	 * Create model.
	 * 
	 */
	@Inject
	public NoteModelImpl(NoteBuilder noteBuilder)
	{
		this.noteBuilder = noteBuilder;

		// Matrix of all notes. On or off.
		matrix = new boolean[COUNT_PANELS][COUNT_BARS][COUNT_NOTES];

		initialiseMatrix(matrix);

		// The last notes played in each bar so we can switch them off when the
		// next bar is played.
		last = new boolean[COUNT_PANELS][COUNT_NOTES];

		initialiseLast(last);

		// The note definitions contain MIDI note id and MIDI channel id.
		notes = new NoteDefinition[COUNT_PANELS][COUNT_NOTES];

		initialiseNotes(notes);

		bar = COUNT_BARS_RESET;
	}

	/**
	 * Initialise matrix.
	 * 
	 * @param data
	 *            The note active array.
	 */
	private void initialiseMatrix(boolean data[][][])
	{
		for (int i = 0; i < COUNT_PANELS; i++)
		{
			for (int j = 0; j < COUNT_BARS; j++)
			{
				for (int k = 0; k < COUNT_NOTES; k++)
				{
					data[i][j][k] = false;
				}
			}
		}
	}

	/**
	 * Initialise last notes played. We need this to ensure we send notes off.
	 * 
	 * @param data
	 *            The note active array.
	 */
	private void initialiseLast(boolean data[][])
	{
		for (int i = 0; i < COUNT_PANELS; i++)
		{
			for (int j = 0; j < COUNT_NOTES; j++)
			{
				data[i][j] = false;
			}
		}
	}

	/**
	 * Initialise notes.
	 * 
	 * @param data
	 *            The note active array.
	 */
	private void initialiseNotes(NoteDefinition data[][])
	{
		for (int i = 0; i < COUNT_PANELS; i++)
		{
			for (int j = 0; j < COUNT_NOTES; j++)
			{
				Integer note = MIDINoteDefinition.NOTES.get(AppNotes.APP_NOTES[j]);

				/**
				 * Channels go 1..16 not 0..15
				 */
				data[i][j] = new NoteDefinition(note, i + 1);
			}
		}
	}

	/**
	 * Set note state.
	 * 
	 * @param bar
	 *            The bar (across the matrix).
	 * @param note
	 *            The note (up and down the matrix)
	 * @param state
	 *            The state.
	 */
	public void setState(int panel, int bar, int note, boolean state)
	{
		matrix[panel][bar][note] = state;
	}

	/**
	 * Get note state.
	 * 
	 * @param bar
	 *            The bar (across the matrix).
	 * @param note
	 *            The note (up and down the matrix)
	 * 
	 * @return The current state.
	 */
	public boolean getState(int panel, int bar, int note)
	{
		return matrix[panel][bar][note];
	}

	/**
	 * Play bar.
	 * 
	 * Fairly straightforward - silence last bar, play the next and so on...
	 * 
	 * @param panel
	 * 
	 * @return The bar played.
	 */
	@Override
	public int play()
	{
		// Mute last
		silenceBar(bar);

		// Cycle around the bars.
		bar = (bar + 1) % COUNT_BARS;

		playBar(bar);

		return bar;
	}

	/**
	 * Get last bar played.
	 * 
	 * @return The last bar played.
	 */
	@Override
	public int getPlayed()
	{
		return bar;
	}

	/**
	 * Reset bar.
	 * 
	 * We silence any last notes being played and reset bar positions.
	 * 
	 */
	@Override
	public void reset()
	{
		// Silence last notes.
		silenceBar(bar);

		initialiseLast(last);

		bar = COUNT_BARS_RESET;
	}

	/**
	 * Silence a bar of notes from the model.
	 * 
	 * @param bar
	 *            The bar to silence.
	 */
	public void silenceBar(int bar)
	{
		noteBuilder.begin();

		for (int panel = 0; panel < COUNT_PANELS; panel++)
		{
			for (int note = 0; note < COUNT_NOTES; note++)
			{
				if (last[panel][note])
				{
					NoteDefinition noteDefinition = notes[panel][note];

					if (noteDefinition != null)
					{
						noteBuilder.note(noteDefinition);

						// Gdx.app.log("", "Note off " + panel + "," + note);
					}
					else
					{
						logger.debug("", "No Note for panel:" + panel + "," + bar + ",note:" + note);
					}

					// Reset
					last[panel][note] = false;
				}
			}
		}

		// End note
		noteBuilder.end();
	}

	/**
	 * Play a bar of notes from the model.
	 * 
	 * @param bar
	 *            The bar to play.
	 */
	public void playBar(int bar)
	{
		noteBuilder.begin();

		for (int panel = 0; panel < COUNT_PANELS; panel++)
		{
			for (int note = 0; note < COUNT_NOTES; note++)
			{
				if (matrix[panel][bar][note])
				{
					NoteDefinition noteDefinition = notes[panel][note];

					if (noteDefinition != null)
					{
						noteBuilder.note(noteDefinition);

						// Gdx.app.log("", "Note on " + panel + "," + note);
					}
					else
					{
						logger.debug("", "No note definition:" + panel + "," + bar + ",note:" + note);
					}

					// Make a note of note on panel.
					last[panel][note] = true;
				}
			}
		}

		// End note
		noteBuilder.end();
	}

	/**
	 * Return notes.
	 * 
	 * @return The model notes.
	 */
	public NoteDefinition[][] getNotes()
    {
    	return notes;
    }
	
}
