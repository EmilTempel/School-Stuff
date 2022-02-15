package gui;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class LoadingFrame extends JFrame {

	CodingArea ca;

	public LoadingFrame(CodingArea ca) {
		this.ca = ca;

		setTitle("Saving...");
		setSize(200, 100);
		setVisible(true);
		setLocationRelativeTo(null);

		JTextField path = new JTextField();

		JButton load = new JButton("load");
		load.addActionListener(a -> {
			try {
				load(path.getText());
			} catch (IOException e) {
				System.out.println("ERROR! This filepath is incorrect.");
			}
		});

		getContentPane().add(path, BorderLayout.CENTER);
		getContentPane().add(load, BorderLayout.SOUTH);
	}

	public void load(String path) throws IOException {
		File f = new File(path);
		if (!f.exists()) {
			throw new IOException();
		} else {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String str = "";
			String line;
			while ((line = reader.readLine()) != null) {
				str += line + "\n";
				System.out.println(1);
			}
			reader.close();
			
			ca.fromString(str);
		}
	}
}
