package multithreading;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Threading<In, Out> {
	Thread[] threads;
	int processors, counter;
	Object lock;

	int[] beginning, end;
	In[] in;
	Out[] out;
	Parallelizable<In, Out> action;

	public Threading() {
		processors = Runtime.getRuntime().availableProcessors();
		counter = processors;
		lock = new Object();
		threads = new Thread[processors];
		beginning = new int[processors];
		end = new int[processors];
		Threading<In, Out> threading = this;
		for (int i = 0; i < processors; i++) {
			int I = i;
			threads[i] = new Thread("" + i) {
				public void run() {
					threading.run(I);
				}
			};
			threads[i].start();
		}
	}

	public synchronized void run(int ID) {
		while (true) {
//			System.out.println("Thread " + ID + " is currently waiting...");
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println("Thread " + ID + " is currently waking up");
			for (int i = beginning[ID]; i < end[ID]; i++) {
				action.execute(in, out, i);
			}
			if (--counter == 0) {
				synchronized (lock) {
					lock.notify();
				}
			}
		}
	}

	public Out[] parallelize(In[] in, Out[] out, Parallelizable<In, Out> action) {
		counter = processors;
		int len = in.length / processors;
		int remainder = in.length % processors;
		this.in = in;
		this.out = out;
		this.action = action;
		for (int i = 0; i < processors; i++) {
			beginning[i] = i * len;
			end[i] = i * len + len + ((i == processors - 1) ? remainder : 0);
		}
		synchronized (this) {
			this.notifyAll();
		}
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return out;
	}

	public interface Parallelizable<In, Out> {
		public abstract void execute(In[] in, Out[] out, int i);
	}

	public static void main(String[] args) {
		Threading<BufferedImage, Graphics> t = new Threading<BufferedImage, Graphics>();
		int N = 1000, l = 16;
		
		BufferedImage[] imgs = new BufferedImage[N];
		for(int i = 0; i < N; i++) {
			imgs[i] = new BufferedImage(l,l,BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = imgs[i].createGraphics();
			g.setColor(new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256)));
			g.fillRect(0, 0, imgs[i].getWidth(), imgs[i].getHeight());
		}
		
		JFrame frame = new JFrame();
		frame.setVisible(true);
		frame.setSize(500,500);
		
		JLabel lbl = new JLabel() {
			protected void paintComponent(Graphics g) {
				Instant then = Instant.now();
//				for(int i = 0; i < N; i++) {
//					g.drawImage(imgs[i], 0,0, i*(frame.getWidth() * N/l), i* (frame.getHeight() * N/l),null);
//				}
				t.parallelize(imgs, new Graphics[] {g}, (in, out, n) -> out[0].drawImage(in[n], 0, 0, frame.getWidth(), frame.getHeight(), null));
				System.out.println(Duration.between(then,Instant.now()).getNano()*10E-9);
				repaint();
			}
		};
		lbl.setSize(500,500);
		lbl.setVisible(true);
		
		frame.add(lbl);
	}
}
