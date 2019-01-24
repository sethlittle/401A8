package a8;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.font.*;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * this class has labels for the x, y, red, green, blue, and brightness values for each pixel in the picture.
 *  When a click on the picture occurs, the values are printed on the left hand side
 */

public class PixelInspectorWidget extends JPanel implements MouseListener {

	private PictureView _picture_view;
	private JLabel _xLabel;
	private JLabel _yLabel;
	private JLabel _redLabel;
	private JLabel _greenLabel;
	private JLabel _blueLabel;
	private JLabel _brightnessLabel;

	public PixelInspectorWidget(Picture p) {
		if (p == null) {
			throw new IllegalArgumentException("Picture p cannot be null");
		}
		setLayout(new BorderLayout());

		_picture_view = new PictureView(p.createObservable());
		// the constructor adds a mouse listener with this passed as the
		// parameter.
		_picture_view.addMouseListener(this);
		add(_picture_view, BorderLayout.CENTER);

		JPanel j = new JPanel();
		j.setLayout(new GridLayout(6, 1));

		_xLabel = new JLabel("X: 0");
		_xLabel.setFont(new Font("Serif", Font.PLAIN, 14));
		_yLabel = new JLabel("Y: 0");
		_yLabel.setFont(new Font("Serif", Font.PLAIN, 14));
		_redLabel = new JLabel("Red: 0");
		_redLabel.setFont(new Font("Serif", Font.PLAIN, 14));
		_greenLabel = new JLabel("Green: 0");
		_greenLabel.setFont(new Font("Serif", Font.PLAIN, 14));
		_blueLabel = new JLabel("Blue: 0");
		_blueLabel.setFont(new Font("Serif", Font.PLAIN, 14));
		_brightnessLabel = new JLabel("Brightness: 0");
		_brightnessLabel.setFont(new Font("Serif", Font.PLAIN, 14));

		j.add(_xLabel);
		j.add(_yLabel);
		j.add(_redLabel);
		j.add(_greenLabel);
		j.add(_blueLabel);
		j.add(_brightnessLabel);

		add(j, BorderLayout.WEST);

	}

	// this method is called every time there is a click on the picture
	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		Coordinate c = new Coordinate(x, y);
		Pixel pix = _picture_view.getPicture().getPixel(c);
		double red = pix.getRed();
		double blue = pix.getBlue();
		double green = pix.getGreen();
		double brightness = round((red * 0.3) + (blue * 0.59) + (green * 0.11), 2);

		_xLabel.setText("X: " + x);
		_yLabel.setText("Y: " + y);
		_redLabel.setText("Red: " + round(red, 2));
		_greenLabel.setText("Green: " + round(green, 2));
		_blueLabel.setText("Blue: " + round(blue, 2));
		_brightnessLabel.setText("Brightness: " + round(brightness, 2));
		// these methods update the JLabels to a new text

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	// this private helper method turns a double value given in the parameter to
	// another double value rounded to p decimal places
	private static double round(double d, int p) {
		if (p < 0) {
			throw new IllegalArgumentException("Must have at least 0 decimal points");
		}
		return (Math.round(d * Math.pow(10, p))) / Math.pow(10, p);
	}

}
