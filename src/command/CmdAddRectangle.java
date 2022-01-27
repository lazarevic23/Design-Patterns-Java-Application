package command;

import mvc.DrawingModel;
import shapes.Rectangle;

public class CmdAddRectangle implements Command {
	
	private Rectangle rectangle;
	private DrawingModel model;
	
	public CmdAddRectangle(Rectangle rectangle,DrawingModel model) {
		this.rectangle=rectangle;
		this.model=model;
	}

	@Override
	public void execute() {
		model.add(rectangle);	
	}

	@Override
	public void unexecute() {
		model.remove(rectangle);
	}

	@Override
	public String toLogText() {
		return "Added->" + rectangle.toString();
	}
}