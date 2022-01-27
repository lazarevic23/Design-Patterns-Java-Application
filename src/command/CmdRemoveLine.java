package command;

import mvc.DrawingModel;
import shapes.Line;

public class CmdRemoveLine implements Command{

	private Line line;
	private DrawingModel model;
	
	public CmdRemoveLine(Line line, DrawingModel model){
		this.line=line;
		this.model=model;
	}
	
	@Override
	public void execute() {
		model.remove(line);
	}

	@Override
	public void unexecute() {
		model.add(line);
	}
	
	@Override
	public String toLogText() {
		return "Deleted->"+line.toString();	 
	}

}