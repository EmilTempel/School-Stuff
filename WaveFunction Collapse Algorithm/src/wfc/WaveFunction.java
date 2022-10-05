package wfc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class WaveFunction {

	static int[][] directions2 = new int[][] { { 0, -1 }, { -1, 0 }, { 1, 0 }, { 0, 1 } };

	BufferedImage[] tiles;
	HashMap<BufferedImage, Double> probability;

	SuperPosition[] W;
	double[] H;

	int width, height, N;
	int[] directions;

	BufferedImage[][][] connections;

	Random seed;

	public WaveFunction(BufferedImage input, int width, int height, int N, boolean rotate, Random seed) {
		probability = new HashMap<>();
		tiles = tile(input, N, rotate, probability);

		connections = computeConnections(tiles);

		this.width = width;
		this.height = height;
		this.N = N;

		directions = new int[] { -width, -1, 1, width };

		W = new SuperPosition[width * height];
		H = new double[width * height];

		for (int i = 0; i < W.length; i++) {
			W[i] = new SuperPosition(tiles, probability);
			H[i] = W[i].entropy();
		}

		this.seed = seed;
	}

	public void collapse() {
		while (!isCollapsed()) {
			int index = smallestEntropy();
			
			W[index].collapse(seed);

			boolean[] traveled = new boolean[width * height];

			propagate(index, traveled);

//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		System.out.println("finished");
	}

	public int smallestEntropy() {
		ArrayList<Integer> minList = new ArrayList<>();
		double min = Double.POSITIVE_INFINITY;

		for (int i = 0; i < W.length; i++) {
			double entropy = H[i];
			if (entropy == min) {
				minList.add(i);
			} else if (entropy < min && entropy != 0) {
				minList = new ArrayList<>();
				minList.add(i);
				min = entropy;
			}

		}
		

		return minList.get(seed.nextInt(minList.size()));
	}

	public void propagate(int i, boolean[] traveled) {
		for (int j = 0; j < 4; j++) {
			int k = k(i, j);
			update(k);
			if (!traveled[k]) {

				traveled[k] = true;
				propagate(k, traveled);

			}
		}
	}

	public void update(int i) {
		if (W[i].isCollapsed()) {
			return;
		} else {
			ArrayList<BufferedImage> positions = new ArrayList<>();
			int c = 0;
			for (BufferedImage tile1 : W[i].positions) {
				c++;

				boolean possible = true;

				for (int j = 0; j < 4; j++) {
					int k = k(i, j);

					if (W[k].isCollapsed()) {
						BufferedImage tile2 = W[k].getValue();
						if (!check(tile1, tile2, j)) {
							possible = false;
						}
					} else if (W[k].positions.size() == 0) {
						continue;
					} else {
						boolean flag = false;

						for (BufferedImage tile2 : W[k].positions) {
							if (check(tile1, tile2, j)) {
								flag = true;

							}
						}
						if (!flag) {
							possible = false;

						}
					}
				}
				if (possible) {
					positions.add(tile1);
				}
			}

//		System.out.println("positions.size() = " + positions.size() );

			if (positions.size() == 0) {
				W[i].setValue(null);
			} else if (positions.size() == 1) {
				W[i].setValue(positions.get(0));
			} else {
				W[i].positions = positions;
			}
			
			H[i] = W[i].entropy();
		}
	}

	public boolean check(BufferedImage tile1, BufferedImage tile2, int j) {
		int index = indexOf(tile1);

		assert index != -1;

		for (int c = 0; c < connections[index][j].length; c++) {
			if (connections[index][j][c].equals(tile2)) {
				return true;
			}
		}
		return false;
	}

	public boolean isCollapsed() {
		for (int i = 0; i < W.length; i++) {
			if (W[i].entropy() != 0) {
				return false;
			}
		}
		return true;
	}

	public int indexOf(BufferedImage tile) {
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i] == tile) {
				return i;
			}
		}
		return -1;
	}

	public int k(int i, int j) {
		int k;
		if (j == 1 && i % width == 0) {
			k = i + width - 1;
		} else if (j == 2 && i % width == width - 1) {
			k = i - (width - 1);
		} else if (j == 0 && i / width == 0) {
			k = i + width * (height - 1);
		} else if (j == 3 && i / width == width - 1) {
			k = i - width * (height - 1);
		} else {
			k = i + directions[j];
		}
		return k;
	}

	public BufferedImage toImage() {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = img.createGraphics();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (W[i * width + j].isCollapsed()) {
					g.setColor(new Color(W[i * width + j].getValue().getRGB(0, 0)));

//					g.drawImage(W[i * width + j].getValue(), i, j, N, N, null);
//					System.out.println("yes");
				} else {
					g.setColor(Color.cyan);

				}
				g.fillRect(i, j, 1, 1);
			}
		}
		return img;
	}

	public static BufferedImage[] tile(BufferedImage input, int N, boolean rotate,
			HashMap<BufferedImage, Double> probability) {
		BufferedImage wrapped = new BufferedImage(input.getWidth() * 2, input.getHeight() * 2,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = wrapped.createGraphics();
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				g.drawImage(input, i * input.getWidth(), j * input.getHeight(), null);
			}
		}
		g.dispose();

		ArrayList<BufferedImage> tiles = new ArrayList<>();

		for (int i = 0; i < input.getWidth(); i++) {
			for (int j = 0; j < input.getHeight(); j++) {
				BufferedImage tile = wrapped.getSubimage(i, j, N, N);
				for (int k = 0; k < (rotate ? 4 : 1); k++) {
					tile = rotateImage(tile, k);
					boolean newTile = true;
					for (BufferedImage img : tiles) {
						if (equals(img, tile)) {
							probability.put(img, probability.get(img) + 1);
							newTile = false;
							break;
						}
					}
					if (newTile) {
						tiles.add(tile);
						probability.put(tile, 1.0);
					}
				}
			}
		}

		BufferedImage[] t = new BufferedImage[tiles.size()];
		for (int i = 0; i < tiles.size(); i++) {
			t[i] = tiles.get(i);
		}
		return t;
	}

	public static BufferedImage rotateImage(BufferedImage src, int k) {
		int width = src.getWidth();
		int height = src.getHeight();

		BufferedImage dest = new BufferedImage(height, width, src.getType());

		Graphics2D graphics2D = dest.createGraphics();
		graphics2D.translate((height - width) / 2, (height - width) / 2);
		graphics2D.rotate(k * Math.PI / 2, height / 2, width / 2);
		graphics2D.drawRenderedImage(src, null);

		return dest;

	}

	public static BufferedImage[][][] computeConnections(BufferedImage[] tiles) {
		BufferedImage[][][] connections = new BufferedImage[tiles.length][4][];

		for (int i = 0; i < tiles.length; i++) {

			for (int j = 0; j < 4; j++) {
				ArrayList<BufferedImage> c = new ArrayList<>();
				for (int k = 0; k < tiles.length; k++) {
					if (overlaps(tiles[i], tiles[k], j)) {
						c.add(tiles[k]);
					}
				}
				connections[i][j] = new BufferedImage[c.size()];
				for (int k = 0; k < c.size(); k++) {
					connections[i][j][k] = c.get(k);
				}
			}
		}

		return connections;
	}

	public static boolean equals(BufferedImage img1, BufferedImage img2) {
		if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight())
			return false;

		for (int i = 0; i < img1.getWidth(); i++) {
			for (int j = 0; j < img1.getHeight(); j++) {
				if (img1.getRGB(i, j) != img2.getRGB(i, j)) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean overlaps(BufferedImage img1, BufferedImage img2, int i) {
		for (int x = 0; x < img1.getWidth(); x++) {
			for (int y = 0; y < img1.getHeight(); y++) {
				try {
					int RGB1 = img1.getRGB(x, y);
					int RGB2 = img2.getRGB(x - directions2[i][0], y - directions2[i][1]);
					if (RGB1 != RGB2) {
						return false;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					continue;
				}
			}
		}
		return true;
	}

	public interface ImageExecutable {
		public abstract void run(BufferedImage img1, BufferedImage img2, int j);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(500, 500);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		BufferedImage input = null;
		try {
			input = ImageIO.read(new File("test.png"));
		} catch (IOException e) {
		}

		WaveFunction w = new WaveFunction(input, 40, 40, 3, false, new Random());

		System.out.println(Arrays.deepToString(w.W));
		
		JLabel label = new JLabel() {
			protected void paintComponent(Graphics g) {
//				g.setColor(Color.WHITE);
//				for (int i = 0; i < w.tiles.length; i++) {
//					g.drawImage(w.tiles[i], i * 30, 0, 30, 30, null);
//					g.drawRect(i * 30, 0, 30, 30);
//					for (int j = 0; j < w.connections[i][3].length; j++) {
//						g.drawImage(w.connections[i][3][j], i * 30, (j + 1) * 30, 30, 30, null);
//						g.drawRect(i * 30, (j + 1) * 30, 30, 30);
//					}
//				}

				g.drawImage(w.toImage(), 0, 0, 500, 500, null);
				repaint();
			}
		};

		label.setVisible(true);
		label.setSize(500, 500);

		frame.add(label);
		frame.setVisible(true);

		w.collapse();
	}
}
