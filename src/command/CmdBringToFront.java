package command;

import mvc.DrawingModel;
import shapes.Shape;


public class CmdBringToFront implements Command{
	
	private DrawingModel drawModel;
	private Shape selectedShape;
	private int indexBefore;
	
	public CmdBringToFront(DrawingModel drawModel,Shape selectedShape, int i) {	
		this.drawModel=drawModel;
	    this.selectedShape=selectedShape;	
	}

	public CmdBringToFront(DrawingModel drawModel,Shape selectedShape) {	
		this.drawModel=drawModel;
	    this.selectedShape=selectedShape;	
	}

	@Override
	public void execute() {
		indexBefore=drawModel.getIndexOfShape(selectedShape);
		drawModel.getShapes().remove(selectedShape);
		drawModel.getShapes().add(selectedShape);
	}

	@Override
	public void unexecute() {
	   drawModel.getShapes().remove(selectedShape);
	   drawModel.getShapes().add(indexBefore, selectedShape);
	}

	@Override
	public String toLogText() {
		return "Bringed to front->"+selectedShape.toString();
	}

}
