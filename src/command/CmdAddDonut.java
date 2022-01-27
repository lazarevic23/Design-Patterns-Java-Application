package command;

import mvc.DrawingModel;
import shapes.Donut;

public class CmdAddDonut implements Command {

	private Donut donut;
	private DrawingModel model;
	
	public CmdAddDonut(Donut donut, DrawingModel model) {
		this.donut=donut;
		this.model=model;
	}
	
	@Override
	public void execute() {
		model.add(donut);	
	}

	@Override
	public void unexecute() {
		model.remove(donut);
	}

	@Override
	public String toLogText() {
		return "Added->" + donut.toString();
	}
}