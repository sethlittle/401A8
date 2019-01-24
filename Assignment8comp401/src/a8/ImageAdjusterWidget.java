package a8;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImageAdjusterWidget extends JPanel implements ChangeListener {

	/*
	 * This class is the JPanel for the ImageAdjuster, it will have the picture,
	 * and 3 sliders, Blur (1-5), saturate(-100, 100), and brightness (-100,
	 * 100) that will change the Picture accordingly, and all the changes are
	 * undoable
	 */

	private Picture _original_pic;
	private PictureView _picture_view;
	private JSlider _blur_slider;
	private JSlider _brightness_slider;
	private JSlider _saturation_slider;
	private JLabel _blur_label;
	private JLabel _brightness_label;
	private JLabel _saturation_label;

	// statechanged handles the methods

	public ImageAdjusterWidget(Picture p) {
		if (p == null) {
			throw new IllegalArgumentException("Picture p cannot be null");
		}

		setLayout(new BorderLayout());

		// store the original pic and add it to the picture view
		_original_pic = p;
		_picture_view = new PictureView(_original_pic.createObservable());
		add(_picture_view, BorderLayout.CENTER);

		JPanel j = new JPanel();
		j.setLayout(new GridLayout(3, 1));

		// the blur label will snap to ticks because integer values are needed
		// to find the number of pixels around the specified one
		JPanel a = new JPanel();
		a.setLayout(new BorderLayout());
		_blur_label = new JLabel("Blur: ");
		_blur_label.setFont(new Font("Serif", Font.PLAIN, 14));
		a.add(_blur_label, BorderLayout.WEST);
		_blur_slider = new JSlider(0, 5);
		_blur_slider.setPaintTicks(true);
		_blur_slider.setPaintLabels(true);
		_blur_slider.setSnapToTicks(true);
		_blur_slider.setMajorTickSpacing(1);
		_blur_slider.setValue(0);
		_blur_slider.addChangeListener(this);
		// addChangeListener allows for the slider changes to call the method
		// stateChanged, each slider has the change listener
		a.add(_blur_slider, BorderLayout.CENTER);

		// The brightness label will span from -100 to 100, with -100 being a
		// black solid picture and 100 being a white solid picture
		JPanel b = new JPanel();
		b.setLayout(new BorderLayout());
		_brightness_label = new JLabel("Brightness: ");
		_brightness_label.setFont(new Font("Serif", Font.PLAIN, 14));
		b.add(_brightness_label, BorderLayout.WEST);
		_brightness_slider = new JSlider(-100, 100);
		_brightness_slider.setPaintTicks(true);
		_brightness_slider.setPaintLabels(true);
		_brightness_slider.setMajorTickSpacing(25);
		_brightness_slider.setValue(0);
		_brightness_slider.addChangeListener(this);
		b.add(_brightness_slider, BorderLayout.CENTER);

		// the saturate slider will increase the intensity of each pixel based
		// on the major component (green, red, blue) of it
		JPanel c = new JPanel();
		c.setLayout(new BorderLayout());
		_saturation_label = new JLabel("Saturation: ");
		_saturation_label.setFont(new Font("Serif", Font.PLAIN, 14));
		c.add(_saturation_label, BorderLayout.WEST);
		_saturation_slider = new JSlider(-100, 100);
		_saturation_slider.setPaintTicks(true);
		_saturation_slider.setPaintLabels(true);
		_saturation_slider.setMajorTickSpacing(25);
		_saturation_slider.setValue(0);
		_saturation_slider.addChangeListener(this);
		c.add(_saturation_slider, BorderLayout.CENTER);

		j.add(a);
		j.add(c);
		j.add(b);
		add(j, BorderLayout.SOUTH);

	}

	/*
	 * The stateChanged method is called when one of the sliders changes, this
	 * method gets the values of all the sliders and passes a picture to blur,
	 * then the blurred pic is passed on to the saturate method with the
	 * saturate slider value. then that picture is passed on to the brighten
	 * method which is changed to the new picture view
	 */

	@Override
	public void stateChanged(ChangeEvent e) {
		Picture unfiltered = copy(_original_pic);

		int blurFactor = _blur_slider.getValue();
		double brightenFactor = _brightness_slider.getValue();
		double saturateFactor = _saturation_slider.getValue();

		Picture blurredOutput = blur(unfiltered, blurFactor);
		Picture saturatedOutput = saturate(blurredOutput, saturateFactor);
		Picture brightenedOutput = brighten(saturatedOutput, brightenFactor);
		ObservablePicture output = brightenedOutput.createObservable();
		_picture_view.setPicture(output);
	}

	// methods for blur saturate and brightness with value passed return a new
	// Picture and chain pictures

	// blur cycles through the pixels in blurredFactor distance from each pixel
	// and averages the rgb values.
	private Picture blur(Picture p, int blurredFactor) {
		Picture output = new PictureImpl(p.getWidth(), p.getHeight());
		double totalRed = 0.0;
		double totalGreen = 0.0;
		double totalBlue = 0.0;
		int total = 0;
		for (int i = 0; i < p.getWidth(); i++) {
			for (int j = 0; j < p.getHeight(); j++) {
				for (int m = i - blurredFactor; m < i - blurredFactor + (2 * blurredFactor + 1); m++) {
					for (int n = j - blurredFactor; n < j - blurredFactor + (2 * blurredFactor + 1); n++) {
						if ((m - blurredFactor >= 0 && n - blurredFactor >= 0)
								&& (m < p.getWidth() && n < p.getHeight())) {
							totalRed = totalRed + p.getPixel(m, n).getRed();
							totalGreen = totalGreen + p.getPixel(m, n).getGreen();
							totalBlue = totalBlue + p.getPixel(m, n).getBlue();
							total++;
						}
					}
				}
				double avgRed = totalRed / total;
				double avgGreen = totalGreen / total;
				double avgBlue = totalBlue / total;
				output.setPixel(i, j, new ColorPixel(avgRed, avgGreen, avgBlue));
				totalRed = 0.0;
				totalGreen = 0.0;
				totalBlue = 0.0;
				total = 0;
			}

		}

		return output;
	}

	// brighten uses the darken(-brightenFactor) and lighten methods(when
	// brightenFactor > 0). if the value is 0, the new Picture just has the
	// value from the old picture
	private Picture brighten(Picture p, double brightenFactor) {
		Picture output = copy(p);
		double bfactor = brightenFactor / 100.0;

		for (int i = 0; i < p.getWidth(); i++) {
			for (int j = 0; j < p.getHeight(); j++) {
				if (bfactor < 0) {
					output.setPixel(i, j, p.getPixel(i, j).darken(-bfactor));
				} else if (bfactor > 0) {
					output.setPixel(i, j, p.getPixel(i, j).lighten(bfactor));
				} else {
					output.setPixel(i, j, p.getPixel(i, j));
				}
			}
		}
		return output;
	}

	// saturate uses the formulas from the Assignment Description and copies the
	// picture and then creates a new pixel with rgb values.
	private Picture saturate(Picture p, double saturateFactor) {
		Picture output = copy(p);
		double f = saturateFactor / 100.0;

		for (int i = 0; i < p.getWidth(); i++) {
			for (int j = 0; j < p.getHeight(); j++) {
				Coordinate c = new Coordinate(i, j);
				double b = p.getPixel(c).getIntensity();
				if (f < 0) {
					double oldRed = p.getPixel(c).getRed();
					double oldGreen = p.getPixel(c).getGreen();
					double oldBlue = p.getPixel(c).getBlue();
					double newRed = oldRed * (1.0 + (f)) - (b * f);
					double newGreen = oldGreen * (1.0 + (f)) - (b * f);
					double newBlue = oldBlue * (1.0 + (f)) - (b * f);
					double[] outs = limits(newRed, newGreen, newBlue);
					output.setPixel(c, new ColorPixel(outs[0], outs[1], outs[2]));
				} else if (f > 0) {
					double oldRed = p.getPixel(c).getRed();
					double oldGreen = p.getPixel(c).getGreen();
					double oldBlue = p.getPixel(c).getBlue();
					double a = oldRed;
					if (oldGreen > oldRed && oldGreen > oldBlue) {
						a = oldGreen;
					} else if (oldBlue > oldRed && oldBlue > oldGreen) {
						a = oldBlue;
					}
					try {
						double newRed = oldRed * ((a + ((1.0 - a) * (f))) / a);
						double newGreen = oldGreen * ((a + ((1.0 - a) * (f))) / a);
						double newBlue = oldBlue * ((a + ((1.0 - a) * (f))) / a);
						double[] outs = limits(newRed, newGreen, newBlue);
						output.setPixel(c, new ColorPixel(outs[0], outs[1], outs[2]));
					} catch (RuntimeException e) {
						output.setPixel(c, new ColorPixel(0, 0, 0));
					}
				} else {
					output.setPixel(c, p.getPixel(c));
				}
			}
		}

		return output;
	}

	// copy cycles through the pixels in a picture and creates a new Picture
	// that has the same pixels in the same places, a replica
	private Picture copy(Picture p) {
		if (p == null) {
			throw new IllegalArgumentException("Cannot create a copy of a null Picture");
		}
		Picture replica = new PictureImpl(p.getWidth(), p.getHeight());

		for (int i = 0; i < p.getWidth(); i++) {
			for (int j = 0; j < p.getHeight(); j++) {
				replica.setPixel(i, j, p.getPixel(i, j));
			}
		}
		return replica;
	}

	// limits is a helper method used to limit the value of the rgb to between 0
	// and 1, if greater than 1, it turns it to 1. if the value is less than 0
	// it turns the value to 0
	private double[] limits(double a, double b, double c) {
		if (a > 1.0 || a < 0.0) {
			if (a > 1.0) {
				a = 1.0;
			} else {
				a = 0.0;
			}
		}
		if (b > 1.0 || b < 0.0) {
			if (b > 1.0) {
				b = 1.0;
			} else {
				b = 0.0;
			}
		}
		if (c > 1.0 || c < 0.0) {
			if (c > 1.0) {
				c = 1.0;
			} else {
				c = 0.0;
			}
		}

		return new double[] { a, b, c };
	}
}
