package command;

import mvc.DrawingModel;
import shapes.Shape;

public class CmdBringToBack implements Command{
	
	private DrawingModel drawModel;
	private Shape selectedShape;
	private int index;

	public CmdBringToBack(DrawingModel drawModel,Shape selectedShape) {
		this.selectedShape=selectedShape;
		this.drawModel=drawModel;
	}
	
	@Override
	public void execute() {
		index=drawModel.getIndexOfShape(selectedShape);
		drawModel.getShapes().remove(selectedShape);
		drawModel.getShapes().add(0, selectedShape);
	}

	@Override
	public void unexecute() {
		drawModel.getShapes().remove(0);
		drawModel.getShapes().add(index, selectedShape);	
	}

	@Override
	public String toLogText() {
		return "Bringed to back->"+selectedShape.toString();
	}
}
