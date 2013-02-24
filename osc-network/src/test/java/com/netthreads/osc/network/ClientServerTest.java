package com.netthreads.osc.network;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;

import com.netthreads.network.osc.client.OSCClient;
import com.netthreads.network.osc.client.OSCClientImpl;
import com.netthreads.osc.common.domain.OSCBundle;
import com.netthreads.osc.note.service.AppNotes;
import com.netthreads.osc.note.service.MIDINoteDefinition;
import com.netthreads.osc.note.service.NoteDefinition;
import com.netthreads.osc.note.service.NoteModel;
import com.netthreads.osc.note.service.NoteModelImpl;
import com.netthreads.osc.test.task.OSCClientTask;
import com.netthreads.osc.test.task.OSCServerTask;

/**
 * Simple test of sending and receiving OSC messages between client and server
 * objects.
 * 
 */
public class ClientServerTest
{
	private static final int TASK_COUNT = 2;
	private static final int MSG_COUNT = 2;
	private static final int NOTE_COUNT = 5;
	
	public static final String DEFAULT_HOST = "localhost";
	public static final int DEFAULT_PORT = 9000;
	
	private NoteModel noteModel;
	private OSCClient oscClient;
	
	private ExecutorService executor;
	
	private OSCServerTask serverTask;
	private OSCClientTask clientTask;
	
	/**
	 * Set up components.
	 * 
	 */
	@Before
	public void before()
	{
		noteModel = AppInjector.getInjector().getInstance(NoteModel.class);
		
		initialiseNotes(noteModel);
		
		oscClient = AppInjector.getInjector().getInstance(OSCClientImpl.class);
		
		executor = Executors.newFixedThreadPool(TASK_COUNT);
	}
	
	/**
	 * Test sending messages from client to server.
	 * 
	 * TODO: Is there a neater way to do this?
	 */
	@Test
	public void testClientServer()
	{
		// ---------------------------------------------------------------
		// Initialise test notes
		// ---------------------------------------------------------------
		initNotes();
		
		// ---------------------------------------------------------------
		// Server
		// ---------------------------------------------------------------
		
		serverTask = new OSCServerTask(DEFAULT_PORT, MSG_COUNT);
		
		executor.submit(serverTask);
		
		// ---------------------------------------------------------------
		// Client
		// ---------------------------------------------------------------
		boolean connected = oscClient.connect(DEFAULT_HOST, DEFAULT_PORT);
		if (connected)
		{
			clientTask = new OSCClientTask(oscClient, noteModel);
			executor.submit(clientTask);
			
			// Pause
			try
			{
				Thread.sleep(1000);
				
				List<OSCBundle> bundles = serverTask.getBundles();
				
				int size = bundles.size();
				
				assertTrue(size == MSG_COUNT);
				
				for (OSCBundle oscBundle : bundles)
				{
					assertTrue(oscBundle.getMessages().size() == NOTE_COUNT);
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		executor.shutdown();
	}
	
	/**
	 * Initialise notes.
	 * 
	 * @param data
	 *            The note active array.
	 */
	private void initialiseNotes(NoteModel noteModel)
	{
		NoteDefinition data[][] = noteModel.getNotes();
		
		for (int i = 0; i < NoteModelImpl.COUNT_PANELS; i++)
		{
			for (int j = 0; j < NoteModelImpl.COUNT_NOTES; j++)
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
	 * Activate example notes in model.
	 * 
	 */
	private void initNotes()
	{
		for (int i = 0; i < NOTE_COUNT; i++)
		{
			noteModel.setState(0, 0, i, true);
		}
	}
	
}
