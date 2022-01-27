package command;

import mvc.DrawingModel;

import adapter.HexagonAdapter;

public class CmdAddHexagon implements Command{

	private HexagonAdapter hexagon;
	private DrawingModel model;
	
	public CmdAddHexagon(HexagonAdapter hexagonAdapter, DrawingModel model) {
		this.hexagon=hexagonAdapter;
		this.model=model;
	}
	
	@Override
	public void execute() {
		model.add(hexagon);
	}

	@Override
	public void unexecute() {
		model.remove(hexagon);
	}

	@Override
	public String toLogText() {
		return "Added->" + hexagon.toString();
	}
}