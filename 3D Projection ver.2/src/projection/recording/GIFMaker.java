package projection.recording;

import static geometry.Vector.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import geometry.Mesh;
import geometry.Vector;
import projection.PointOfView;

public class GIFMaker {

	public static void main(String[] args) {
		Mesh m = Mesh.MarchingCubes(new Vector(5, 5, 5), -10, 20, v -> (Math.cos(v.x()) * Math.cos(v.y()) - v.z()));

		CircularPath c = new CircularPath(new Vector(0, 0, 0), 20, 5, 400);

		try {
			renderGIF(m, (PoV, frame, frames) -> {
				PoV.setS(c.positions[frame]);
				PoV.SetR(norm(PoV.getS()));
				PoV.setLight(
						norm(add(PoV.getLight(), sub(c.positions[frame], c.positions[frame - 1 < 0 ? frames - 1 : frame]))));

			}, 400, new Dimension(1920, 1080), "rotation400cosdarkmode");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void renderGIF(Mesh m, Transformation t, int frames, Dimension d, String name) throws IOException {
		ImageOutputStream output = new FileImageOutputStream(new File(name + ".gif"));

		GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_4BYTE_ABGR_PRE, 0, true);

		PointOfView PoV = new PointOfView(new Vector(0, 0, 0), 90, d);

		for (int i = 0; i < frames; i++) {
			t.transform(PoV, i, frames);

			BufferedImage img = new BufferedImage(d.width, d.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = img.createGraphics();
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, 0, d.width, d.height);
			PoV.paint(m, g);
			g.dispose();
			writer.writeToSequence(img);
			System.out.println("currently at frame: " + i);
		}
		writer.close();
		output.close();
	}

	public interface Transformation {
		void transform(PointOfView PoV, int frame, int frames);
	}
}
