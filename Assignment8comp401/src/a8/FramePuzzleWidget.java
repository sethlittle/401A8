package a8;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class FramePuzzleWidget extends JPanel implements KeyListener, MouseListener {

	/*
	 * This class stores a double array of PictureView elements and adds them to
	 * a 5 by 5 GridLayout that will be the main part of the frame puzzle
	 */

	private JPanel _panel;
	private int _solidrow;
	private int _solidcolumn;
	private PictureView[][] _pvs;
	private TileIterator _ti;

	public FramePuzzleWidget(Picture p) {
		if (p == null) {
			throw new IllegalArgumentException("Picture p cannot be null");
		}

		setLayout(new BorderLayout());

		_pvs = new PictureView[5][5];
		// because it is a 5 by 5 grid, each element will have a width and a
		// height 1/5 of the total picture
		_ti = new TileIterator(p, p.getWidth() / 5, p.getHeight() / 5);

		_panel = new JPanel();
		_panel.setLayout(new GridLayout(5, 5));

		Picture[][] subpics = new Picture[5][5];

		// The tileOperator creates subPictures, which we will then convert into
		// the PictureView in the next block of code
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (_ti.hasNext()) {
					subpics[i][j] = _ti.next();
				}
			}
		}

		// For the bottom right corner, the solid, the row and column will be
		// recorded as the private fields _solidrow and _solidcolumn. Every
		// PictureView element that is added needs a MouseListener and a
		// KeyListener and to Focus on the Keys in order to achieve
		// functionality. Then each will be added to the _panel
		for (int m = 0; m < 5; m++) {
			for (int n = 0; n < 5; n++) {
				if (m == 4 && n == 4) {
					_pvs[m][n] = new PictureView(turnWhite(subpics[m][n]).createObservable());
					_solidrow = 4;
					_solidcolumn = 4;
				} else {
					_pvs[m][n] = new PictureView(subpics[m][n].createObservable());
				}
				_pvs[m][n].addMouseListener(this);
				_pvs[m][n].addKeyListener(this);
				_pvs[m][n].setFocusable(true);
				_pvs[m][n].requestFocus();
				_panel.add(_pvs[m][n]);
			}
		}

		// We then add the _panel to the Widget to complete
		add(_panel, BorderLayout.CENTER);

	}

	/*
	 * This method will be called whenever there is a Click on the WidgetPanel.
	 * The source will be checked to determine which PictureView is being
	 * clicked, then the row and column will be stored. Comparing these values
	 * with the _solidrow and _solidcolumn values, there will be four different
	 * cases.
	 * 
	 * That the solid needs to move to the left, right, up or down
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		PictureView pv = (PictureView) e.getSource();

		// these are initialized to -1 to ensure they are initialized, but also
		// will throw a NoSuchElementException if they are not changed
		int row = -1;
		int column = -1;

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (pv == _pvs[i][j]) {
					row = i;
					column = j;
				}
			}
		}

		if (row == _solidrow && column == _solidcolumn) {
			// nothing should happen if the solid is clicked
		} else if (row == _solidrow) {
			// this indicates that the tile will need to move horizontally
			if (_solidcolumn > column) {
				// this indicates the solid will move Right
				List<ObservablePicture> toMoveRight = new ArrayList<ObservablePicture>();
				toMoveRight.add(turnWhite((_pvs[_solidrow][_solidcolumn].getPicture())));
				for (int p = column; p < _solidcolumn; p++) {
					toMoveRight.add(_pvs[row][p].getPicture());
				}

				// the list now has the white solid and the following
				// PictureViews to be moved

				// Now the Pictures will be changed by iterating through the
				// list and setting the Picture of the PictureView double array
				// object. Then finally the _solidcolumn value will be updated
				int k = 0;
				for (int b = column; b < 5; b++) {
					if (k < toMoveRight.size()) {
						_pvs[row][b].setPicture(toMoveRight.get(k));
						k++;
					}
				}
				_solidcolumn = column;
			} else if (_solidcolumn < column) {
				// The solid will move left

				// The same process will occur just for moving the pieces to the
				// Left, the solid will be added to the end of the array this
				// time instead of the beginning
				List<ObservablePicture> toMoveLeft = new ArrayList<ObservablePicture>();
				ObservablePicture whitePic = turnWhite((_pvs[_solidrow][_solidcolumn].getPicture()));
				for (int p = _solidcolumn + 1; p <= column; p++) {
					toMoveLeft.add(_pvs[row][p].getPicture());
				}
				toMoveLeft.add(whitePic);
				int k = 0;
				for (int b = _solidcolumn; b <= column; b++) {
					if (k < toMoveLeft.size()) {
						_pvs[row][b].setPicture(toMoveLeft.get(k));
						k++;
					}
				}
				_solidcolumn = column;
			}
		} else if (column == _solidcolumn) {
			// The solid will move vertically
			if (_solidrow > row) {
				// the solid will be moved up
				// in much the same way that the solid was moved left, it is
				// moved right, just switching the x and y changes
				List<ObservablePicture> toMoveUp = new ArrayList<ObservablePicture>();
				toMoveUp.add(turnWhite((_pvs[_solidrow][_solidcolumn].getPicture())));
				for (int p = row; p < _solidrow; p++) {
					toMoveUp.add(_pvs[p][column].getPicture());
				}
				int k = 0;
				for (int b = row; b < 5; b++) {
					if (k < toMoveUp.size()) {
						_pvs[b][column].setPicture(toMoveUp.get(k));
						k++;
					}
				}
				_solidrow = row;
			} else if (_solidrow < row) {
				// the solid will be moved down
				// the same as moving the solid down except it iterates
				// backwards and adds the whitePicture last instead of first
				List<ObservablePicture> toMoveDown = new ArrayList<ObservablePicture>();
				ObservablePicture whitePic = turnWhite((_pvs[_solidrow][_solidcolumn].getPicture()));
				for (int p = _solidrow + 1; p <= row; p++) {
					toMoveDown.add(_pvs[p][column].getPicture());
				}
				toMoveDown.add(whitePic);
				int k = 0;
				for (int b = _solidrow; b <= row; b++) {
					if (k < toMoveDown.size()) {
						_pvs[b][column].setPicture(toMoveDown.get(k));
						k++;
					}
				}
				_solidrow = row;
			}
		}

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

	@Override
	public void keyTyped(KeyEvent e) {

	}

	/*
	 * This method will handle all the KeyStrokes.
	 * 
	 * Each direction (4 directions, so 4 cases) will determine if a move can be
	 * made, for instance if the solid is at the top then pressing the up key
	 * will do nothing
	 * 
	 * After determining which key was pressed, it will create a temporary
	 * observable picture object which gets the solid's adjacent PictureView,
	 * then change the PictureView will change to a solid, and the place where
	 * the solid was will be assigned the temp picture. Then the _solidrow and
	 * _solidcolumn values will be updated if needed
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			if (_solidrow != 0) {
				ObservablePicture temp = _pvs[_solidrow - 1][_solidcolumn].getPicture();
				_pvs[_solidrow - 1][_solidcolumn].setPicture(turnWhite(_pvs[_solidrow][_solidcolumn].getPicture()));
				_pvs[_solidrow][_solidcolumn].setPicture(temp);
				_solidrow--;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			if (_solidcolumn != 0) {
				ObservablePicture temp = _pvs[_solidrow][_solidcolumn - 1].getPicture();
				_pvs[_solidrow][_solidcolumn - 1].setPicture(turnWhite(_pvs[_solidrow][_solidcolumn].getPicture()));
				_pvs[_solidrow][_solidcolumn].setPicture(temp);
				_solidcolumn--;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (_solidcolumn != 4) {
				ObservablePicture temp = _pvs[_solidrow][_solidcolumn + 1].getPicture();
				_pvs[_solidrow][_solidcolumn + 1].setPicture(turnWhite(_pvs[_solidrow][_solidcolumn].getPicture()));
				_pvs[_solidrow][_solidcolumn].setPicture(temp);
				_solidcolumn++;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			if (_solidrow != 4) {
				ObservablePicture temp = _pvs[_solidrow + 1][_solidcolumn].getPicture();
				_pvs[_solidrow + 1][_solidcolumn].setPicture(turnWhite(_pvs[_solidrow][_solidcolumn].getPicture()));
				_pvs[_solidrow][_solidcolumn].setPicture(temp);
				_solidrow++;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	/*
	 * The following two helper methods just turn a Picture or Observable (*
	 * Picture into a white picture by taking the length and the width and
	 * filling in a white color pixel into every sport for a pixel. This method
	 * will be useful when having to change a PictureView to the solid object
	 * and ensuring a copy of another PictureView is never placed
	 */

	private ObservablePicture turnWhite(Picture p) {
		Picture output = new PictureImpl(p.getWidth(), p.getHeight());
		for (int i = 0; i < p.getWidth(); i++) {
			for (int j = 0; j < p.getHeight(); j++) {
				output.setPixel(i, j, new ColorPixel(1, 1, 1));
			}
		}
		return output.createObservable();
	}

	private ObservablePicture turnWhite(ObservablePicture p) {
		Picture pic = p.getPicture();
		Picture output = new PictureImpl(pic.getWidth(), pic.getHeight());
		for (int i = 0; i < pic.getWidth(); i++) {
			for (int j = 0; j < pic.getHeight(); j++) {
				output.setPixel(i, j, new ColorPixel(1, 1, 1));
			}
		}
		return output.createObservable();
	}

}
