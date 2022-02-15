package gui;

public class Player{

	RegisterArea ra;
	boolean running;
	long millis;
	
	Thread t;

	public Player(RegisterArea ra) {
		this.ra = ra;
		t = new Thread() {
			public void run() {
				while (true) {
					setWaiting();
					count();
				}
			}
		};
		t.start();
	}
	
	public synchronized void setWaiting() {
		try {
			System.out.println("waiting...");
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void wakeUp() {
		notifyAll();
	}

	public void setMillis(long millis) {
		this.millis = millis;
		System.out.println(millis);
	}

	public void stopTheCount() {
		running = false;
	}

	public void count() {
		running = true;

		for (int i = ra.index; i < ra.steps.size(); i++) {
			ra.setIndex(i);

			if (running)
				return;

			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	
}
