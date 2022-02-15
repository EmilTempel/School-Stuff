package gui;

import java.awt.BorderLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class SavingFrame extends JFrame{

	CodingArea ca;
	
	public SavingFrame(CodingArea ca) {
		this.ca = ca;
		
		setTitle("Saving...");
		setSize(200,100);
		setVisible(true);
		setLocationRelativeTo(null);
		
		JTextField path = new JTextField();
		
		JButton save = new JButton("save");
		save.addActionListener(a -> {
			try {
				save(path.getText());
			} catch (IOException e) {
				System.out.println("ERROR! This filepath is incorrect.");
			}
		});
		
		getContentPane().add(path, BorderLayout.CENTER);
		getContentPane().add(save, BorderLayout.SOUTH);
	}
	
	public void save(String path) throws IOException {
		File f = new File(path);
		
		if(!f.exists()) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(ca.toString());
			writer.close();
		}else {
			System.out.println("ERROR! File already exists!");
		}
	}
}
