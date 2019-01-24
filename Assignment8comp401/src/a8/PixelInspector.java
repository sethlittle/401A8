package a8;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PixelInspector {

	// this class creates the JPanel that holds the picture. it uses the
	// pixelinspectorwidget to show several descriptive values for each pixel
	// (red, green, blue, brightness, x, and y)

	public static void main(String[] args) throws IOException {
		Picture p = A8Helper.readFromURL("http://www.cs.unc.edu/~kmp/kmp-in-namibia.jpg");
		PixelInspectorWidget piw = new PixelInspectorWidget(p);

		JFrame main_frame = new JFrame();
		main_frame.setTitle("Assignment 8 Simple Picture View");
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel top_panel = new JPanel();
		top_panel.setLayout(new BorderLayout());
		top_panel.add(piw, BorderLayout.CENTER);
		main_frame.setContentPane(top_panel);

		main_frame.pack();
		main_frame.setVisible(true);

	}

}
