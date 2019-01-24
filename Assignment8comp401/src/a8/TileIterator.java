package a8;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TileIterator implements Iterator<SubPicture> {

	/*
	 * The TileIterator is an implementation of the Iterator interface
	 * 
	 * This class iterates through a Picture and returns the SubPicture in a
	 * tile and then moves to the tile directly beside or under depending on the
	 * width of the Picture
	 */

	private Picture _source;
	private int _x;
	private int _y;
	private int _width;
	private int _height;

	public TileIterator(Picture p, int tile_width, int tile_height) {
		if (p == null) {
			throw new IllegalArgumentException("Picture p cannot be null");
		}
		if (tile_width < 0 && tile_width <= p.getWidth()) {
			throw new IllegalArgumentException(
					"The tile width must be positive and less than or equal to the Picture width");
		}
		if (tile_height < 0 && tile_height <= p.getHeight()) {
			throw new IllegalArgumentException(
					"The tile height must be positive and less than or equal to the Picture height");
		}

		_source = p;
		_x = 0;
		_y = 0;
		_width = tile_width;
		_height = tile_height;
	}

	/*
	 * This method tests whether or not the Picture has anymore Pixels to cycle
	 * through by checking the value of _x added with the _width and _y added to
	 * the _height and making sure they are less than or equal to the dimensions
	 * of the Picture
	 */

	@Override
	public boolean hasNext() {
		return (_x + _width <= _source.getWidth() && _y + _height <= _source.getHeight());
	}

	/*
	 * This method returns the next tile of SubPictures. Then it iterates
	 * through the Picture by increasing either the _x or _y values by the tile
	 * width (for x) or the tile height (for y)
	 * 
	 * throws an exception if hasNext is false
	 */

	@Override
	public SubPicture next() {
		if (hasNext()) {
			SubPicture sub = _source.extract(_x, _y, _width, _height);
			if (_x + _width < _source.getWidth()) {
				_x = _x + _width;
			} else {
				_x = 0;
				_y = _y + _height;
			}
			return sub;
		} else {
			throw new NoSuchElementException("The next method cannot be called if hasNext is false");
		}
	}

}
