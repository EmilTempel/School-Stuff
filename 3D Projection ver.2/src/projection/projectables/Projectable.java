package projection.projectables;

import geometry.Vector;
import projection.PointOfView;

public abstract class Projectable {

	PointOfView PoV;
	
	public Projectable(PointOfView PoV) {
		this.PoV = PoV;
	}
	
	public abstract void update(Vector r);
	
	public abstract Vector project (Vector p3D);
}
