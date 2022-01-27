package command;

import mvc.DrawingModel;
import shapes.Rectangle;

public class CmdRemoveRectangle implements Command {

	private Rectangle rectangle;
	private DrawingModel model;
	
	public CmdRemoveRectangle(Rectangle rectangle,DrawingModel model) {
		this.rectangle=rectangle;
		this.model=model;
	}

	@Override
	public void execute() {
		model.remove(rectangle);
	}

	@Override
	public void unexecute() {
		model.add(rectangle);
	}
	
	@Override
	public String toLogText() {
		return "Deleted->"+rectangle.toString();	 
	}
}