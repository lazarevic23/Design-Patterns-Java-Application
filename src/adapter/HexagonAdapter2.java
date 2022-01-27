package adapter;
import java.awt.Graphics;

import shapes.Shape;
import shapes.SurfaceShape;

public class HexagonAdapter2 extends SurfaceShape {

	private HexagonAdapter2 hexagon2;
	

	public HexagonAdapter2(HexagonAdapter2 hexagon2) {
		this.hexagon2 = hexagon2;
	}

	@Override
	public void moveBy(int x, int y) {
		
		
	}

	@Override
	public int compareTo(Shape o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void fillUpShape(Graphics shapeForFillUp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void selected(Graphics g) {
		// TODO Auto-generated method stub
		
	}
}
