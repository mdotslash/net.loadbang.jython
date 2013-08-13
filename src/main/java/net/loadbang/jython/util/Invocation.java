//	$Id$
//	$Source$

package net.loadbang.jython.util;


/**	Synchronous thread spawning for Jython.

	@author Nick Rothwell, nick@cassiel.com / nick@loadbang.net
 */

abstract public class Invocation implements Runnable {
	/** Optional flag to *not* create a sub-thread. Probably faster, but not safe
	 	if a thread can pass through more than one mxj instance. */
	private boolean itsMonoThreaded;

	/**	Abstract method: make a call into a Jython interpreter
	 	in an interlocked thread. */
	
	public Invocation(boolean monoThreaded) {
		itsMonoThreaded = monoThreaded;
	}

	abstract public void invoke();
	
	/**	Run the body of this invocation in its own thread, then notify
	  	the caller.
	  	
	 	@see java.lang.Runnable#run()
	 */

	public synchronized void run() {
		try {
			invoke();
		} finally {
			notify();
		}
	}
	
	/**	Invoke a body of code using an interpreter, in a new thread.
		The call is synchronous: we block until the called code exits.
	 */
	
	public synchronized void protect() {
		if (itsMonoThreaded) {
			invoke();
		} else {
			Thread t = new Thread(this);
			t.start();

			try {
				wait();
			} catch (InterruptedException _) { }
		}
	}
}
