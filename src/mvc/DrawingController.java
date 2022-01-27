package mvc;

//import java.awt.Color;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import adapter.HexagonAdapter;
import command.CmdAddCircle;
import command.CmdAddDonut;
import command.CmdAddHexagon;
import command.CmdAddLine;
import command.CmdAddPoint;
import command.CmdAddRectangle;
import command.CmdBringToBack;
import command.CmdBringToFront;
import command.CmdRemoveCircle;
import command.CmdRemoveDonut;
import command.CmdRemoveHexagon;
import command.CmdRemoveLine;
import command.CmdRemovePoint;
import command.CmdRemoveRectangle;
import command.CmdShapeDeselect;
import command.CmdShapeSelect;
import command.CmdToBack;
import command.CmdToFront;
import command.CmdUpdateCircle;
import command.CmdUpdateDonut;
import command.CmdUpdateHexagon;
import command.CmdUpdateLine;
import command.CmdUpdatePoint;
import command.CmdUpdateRectangle;
import command.Command;
import command.UndoRedo; 
import dialogs.DlgCircle;
import dialogs.DlgDonut;
import dialogs.DlgHexagon;
import dialogs.DlgLine;
import dialogs.DlgPoint;
import dialogs.DlgRectangle;
import hexagon.Hexagon;
import observer.Observable;
import observer.Observer;
import dialogs.DlgDelete;
import shapes.Circle;
import shapes.Donut;
import shapes.Line;
import shapes.Point;
import shapes.Rectangle;
import shapes.Shape;
import strategy.LogFile;
import strategy.ManagerFile;
import strategy.SaveDraw;
import strategy.SerializableFile;


public class DrawingController {
	
	private DrawingModel model;
	private DrawingFrame frame;
	
	private Shape selected; //pomocna
	
	private Observable observable = new Observable();
	private Observer observer;
	
	private List<Shape> selectedShapes = new ArrayList<Shape>(); 
	
	private Stack<Command> stackUndo = new Stack<Command>(); 
	private Stack<Command> stackRedo = new Stack<Command>(); 
	
	private PropertyChangeSupport propertyChangeSupport; 
	private DefaultListModel<String> log; 
	private ManagerFile manager;
	private int counterOfSelectedShapes = 0; 
	
	public DrawingController(DrawingModel model, DrawingFrame frame) {
		this.model = model;
		this.frame = frame;
		
		observer = new Observer(frame);
		observable.addPropertyChangeListener(observer);
		
		propertyChangeSupport = new PropertyChangeSupport(this); 
		log = frame.getDlmList();
	}
	
	public void mouseClicked(MouseEvent e) {
		
		if(frame.getState() == 1) {
			Point point = new Point(e.getX(), e.getY(), frame.getBtnEdge().getBackground());
		
			//model.getShapes().add(point);
			CmdAddPoint cmdAddPoint = new CmdAddPoint(model, point);
			cmdAddPoint.execute();
			
			addCommandInStack(cmdAddPoint); 
			log.addElement(cmdAddPoint.toLogText());	
			stackRedo.removeAllElements();
			frame.getBtnRedo().setEnabled(false);	
		}
		
		if(frame.getState() == 2) {
			if(model.getStartPoint() == null) {
				model.setStartPoint(new Point(e.getX(), e.getY()));
			}
			else {
				 Line line = new Line(model.getStartPoint(), new Point(e.getX(), e.getY()), frame.getBtnEdge().getBackground());
				 
				 model.setStartPoint(null);
				//model.getShapes().add(line);
				 CmdAddLine cmdAddLine = new CmdAddLine(model, line);
				 cmdAddLine.execute();
				 
				 addCommandInStack(cmdAddLine); 
				 log.addElement(cmdAddLine.toLogText()); 
				 stackRedo.removeAllElements();
				 frame.getBtnRedo().setEnabled(false); 
			}
		}
		
		if(frame.getState() == 3) {
			DlgCircle dlgCircle = new DlgCircle();
			dlgCircle.getTxtXcoordinateOfCenter().setText(Integer.toString(e.getX())); 
			dlgCircle.getTxtXcoordinateOfCenter().disable();
			dlgCircle.getTxtYcoordinateOfCenter().setText(Integer.toString(e.getY()));
			dlgCircle.getTxtYcoordinateOfCenter().disable();
			dlgCircle.getBtnEdgeColor().setVisible(false);
			dlgCircle.getBtnInteriorColor().setVisible(false);
			dlgCircle.setVisible(true);
			
			if(dlgCircle.isConfirmed()) {
				try {
					Circle circle = new Circle(new Point(e.getX(), e.getY()), Integer.parseInt(dlgCircle.getTxtRadiusLength().getText()), frame.getBtnEdge().getBackground(), frame.getBtnInterior().getBackground());
					
					//model.getShapes().add(circle);
					CmdAddCircle cmdAddCircle = new CmdAddCircle(model, circle);
					cmdAddCircle.execute();
					
					addCommandInStack(cmdAddCircle); 
					log.addElement(cmdAddCircle.toLogText());	
					stackRedo.removeAllElements();
					frame.getBtnRedo().setEnabled(false);
					
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(frame,
							"Radius must be greater than 0!",
							"Illegal radius error",
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		}
		
		if(frame.getState() == 4) {
			DlgRectangle dlgRectangle = new DlgRectangle();
			dlgRectangle.getTxtXcoordinate().setText(Integer.toString(e.getX())); 
			dlgRectangle.getTxtXcoordinate().disable();
			dlgRectangle.getTxtYcoordinate().setText(Integer.toString(e.getY()));
			dlgRectangle.getTxtYcoordinate().disable();	
			dlgRectangle.getBtnEdgeColor().setVisible(false);
			dlgRectangle.getBtnInteriorColor().setVisible(false);
			dlgRectangle.setVisible(true);
			
			if(dlgRectangle.isConfirmed()) {
				try {
					Rectangle rectangle = new Rectangle(new Point(e.getX(), e.getY()),Integer.parseInt(dlgRectangle.getTxtWidth().getText()),Integer.parseInt(dlgRectangle.getTxtHeight().getText()), frame.getBtnEdge().getBackground(), frame.getBtnInterior().getBackground());
					
					//model.getShapes().add(rectangle);
					CmdAddRectangle cmdAddRectangle = new CmdAddRectangle(rectangle, model);
					cmdAddRectangle.execute();
					
					addCommandInStack(cmdAddRectangle); 
					log.addElement(cmdAddRectangle.toLogText());
					stackRedo.removeAllElements();
					frame.getBtnRedo().setEnabled(false);
					
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(frame,
							"Width and height must be greater than 0!", null,
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		}
		
		if(frame.getState() == 5) {
			DlgDonut dlgDonut = new DlgDonut();
			dlgDonut.getTxtXcoordinateOfCenter().setText(Integer.toString(e.getX())); 
			dlgDonut.getTxtXcoordinateOfCenter().disable();
			dlgDonut.getTxtYcoordinateOfCenter().setText(Integer.toString(e.getY()));
			dlgDonut.getTxtYcoordinateOfCenter().disable();
			dlgDonut.getBtnEdgeColor().setVisible(false);
			dlgDonut.getBtnInteriorColor().setVisible(false);
			dlgDonut.setVisible(true);
			
			if(dlgDonut.isConfirmed()) {
				try {
					Donut donut = new Donut(new Point(e.getX(), e.getY()), Integer.parseInt(dlgDonut.getTxtRadiusLength().getText()), Integer.parseInt(dlgDonut.getTxtInnerRadiusLength().getText()), frame.getBtnEdge().getBackground(), frame.getBtnInterior().getBackground());
					
					//model.getShapes().add(donut);
					CmdAddDonut cmdAddDonut = new CmdAddDonut(donut, model);
					cmdAddDonut.execute();
					
					addCommandInStack(cmdAddDonut);
					log.addElement(cmdAddDonut.toLogText());
					stackRedo.removeAllElements();
					frame.getBtnRedo().setEnabled(false);
					
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(frame,
							"Radius must be greater than 0!",
							"Illegal radius error",
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
			
		}
		
		if(frame.getState() == 6) {
			DlgHexagon dlgHexagon = new DlgHexagon();
			dlgHexagon.getTxtXcoordinate().setText(Integer.toString(e.getX())); 
			dlgHexagon.getTxtXcoordinate().disable();
			dlgHexagon.getTxtYcoordinate().setText(Integer.toString(e.getY()));
			dlgHexagon.getTxtYcoordinate().disable();
			dlgHexagon.getBtnEdgeColor().setVisible(false);
			dlgHexagon.getBtnInteriorColor().setVisible(false);
			dlgHexagon.setVisible(true);
			
			if(dlgHexagon.isConfirmed()) {
				try {
					HexagonAdapter hexagon = new HexagonAdapter(new Point(e.getX(),e.getY()),Integer.parseInt(dlgHexagon.getTxtRadiusLength().getText()), frame.getBtnEdge().getBackground(), frame.getBtnInterior().getBackground());
					
					//model.getShapes().add(hexagon);
					CmdAddHexagon cmdAddHexagon = new CmdAddHexagon(hexagon, model);
					cmdAddHexagon.execute();
					
					addCommandInStack(cmdAddHexagon); 
					log.addElement(cmdAddHexagon.toLogText());
					stackRedo.removeAllElements();
					frame.getBtnRedo().setEnabled(false);
					
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(frame,
							"Width and height must be greater than 0!", null,
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		}
		
		if(frame.getState() == 7){
			for (int i = 0; i<model.getShapes().size(); i++) {
				selected = model.getShapes().get(i);
				
				if(model.getShapes().get(i).contains(e.getX(), e.getY())) {
					if(selected.isSelected()) {
						CmdShapeDeselect cmdShapeDeselect = new CmdShapeDeselect(this,selected);
						cmdShapeDeselect.execute();
						
						addCommandInStack(cmdShapeDeselect);
						log.addElement(cmdShapeDeselect.toLogText());
						
						frame.getBtnUndo().setEnabled(true);
						frame.getBtnRedo().setEnabled(false);
						
						checkButtons();
						
					} 
					else {
						CmdShapeSelect cmdShapeSelect = new CmdShapeSelect(this,selected);
						cmdShapeSelect.execute();
						
						addCommandInStack(cmdShapeSelect); 
						log.addElement(cmdShapeSelect.toLogText());
						
						frame.getBtnUndo().setEnabled(true);
						frame.getBtnRedo().setEnabled(false);

						checkButtons();
					}
					
					//model.getShapes().get(i).setSelected(true);
					//selectedShapes.add(model.getShapes().get(i));
				}
				
			}
			
			checkShapes(e);
			if(checkShapes(e) == false) {
				unselectAll();
				checkButtons();
			}
		}
		
		frame.getView().repaint();
	}
	
	
	private boolean checkShapes(MouseEvent e) {
		for(int i = 0; i<model.getShapes().size(); i++) {
			if(model.getShapes().get(i).contains(e.getX(), e.getY())) {
				return true;
			}
		}
		return false;
	}
	
	public void unselectAll() {
		Iterator<Shape> it = selectedShapes.iterator();
		while(it.hasNext()) {
			it.next().setSelected(false);
			it.remove();
		}
	}
	
	public void delete() {
		DlgDelete dlgChoose = new DlgDelete();
		dlgChoose.setVisible(true);
		
		if(dlgChoose.confirmation) {
		
		for(int i=selectedShapes.size()-1; i>=0; i--) {
			if(selectedShapes.get(i) instanceof Point) {
				CmdRemovePoint cmdRemovePoint = new CmdRemovePoint((Point)selectedShapes.get(i),model);
				cmdRemovePoint.execute();
				
				addCommandInStack(cmdRemovePoint); 
				log.addElement(cmdRemovePoint.toLogText());
				
				frame.getView().repaint();
				selectedShapes.remove(i);
			}
			 else if(selectedShapes.get(i) instanceof Line) {
				CmdRemoveLine cmdRemoveLine = new CmdRemoveLine((Line)selectedShapes.get(i),model);
				cmdRemoveLine.execute();
				
				addCommandInStack(cmdRemoveLine); 
				log.addElement(cmdRemoveLine.toLogText());
			
				frame.getView().repaint();
				selectedShapes.remove(i);
			} 
			else if(selectedShapes.get(i) instanceof Rectangle) {
				CmdRemoveRectangle cmdRemoveRectangle = new CmdRemoveRectangle((Rectangle)selectedShapes.get(i),model);
				cmdRemoveRectangle.execute();
				
				addCommandInStack(cmdRemoveRectangle); 
				log.addElement(cmdRemoveRectangle.toLogText());
				
				frame.getView().repaint();
				selectedShapes.remove(i);
			} 
			else if(selectedShapes.get(i) instanceof Circle) {
				CmdRemoveCircle cmdRemoveCircle = new CmdRemoveCircle((Circle)selectedShapes.get(i),model);
				cmdRemoveCircle.execute();
				
				addCommandInStack(cmdRemoveCircle); 
				log.addElement(cmdRemoveCircle.toLogText());
				
				frame.getView().repaint();
				selectedShapes.remove(i);
			} 
			else if(selectedShapes.get(i) instanceof Donut) {
				CmdRemoveDonut cmdRemoveDonut  = new CmdRemoveDonut((Donut)selectedShapes.get(i),model);
				cmdRemoveDonut.execute();
				
				addCommandInStack(cmdRemoveDonut); 
				log.addElement(cmdRemoveDonut.toLogText());
				
				frame.getView().repaint();
				selectedShapes.remove(i);
			} 
			else if(selectedShapes.get(i) instanceof HexagonAdapter) {
				CmdRemoveHexagon cmdRemoveHexagon = new CmdRemoveHexagon((HexagonAdapter)selectedShapes.get(i),model);
				cmdRemoveHexagon.execute();
				
				addCommandInStack(cmdRemoveHexagon); 
				log.addElement(cmdRemoveHexagon.toLogText());
				
				frame.getView().repaint();
				selectedShapes.remove(i);
			}
		}
		}
		
		checkButtons();
	}

	public void updateShapeClicked() {
		Shape shape = getSelectedShape();
		if (shape instanceof Point) btnUpdatePointClicked((Point) shape); 
		else if (shape instanceof Line) btnUpdateLineClicked((Line) shape);
		else if (shape instanceof Rectangle) btnUpdateRectangleClicked((Rectangle) shape);
		else if (shape instanceof Donut) btnUpdateDonutClicked((Donut) shape);
		else if (shape instanceof Circle) btnUpdateCircleClicked((Circle) shape);	
		else if (shape instanceof HexagonAdapter) btnUpdateHexagonClicked((HexagonAdapter) shape);
		frame.getView().repaint();
	}

	private void btnUpdateHexagonClicked(HexagonAdapter oldHexagon) {
		DlgHexagon dlgHexagon = new DlgHexagon();
		dlgHexagon.getTxtXcoordinate().setText(Integer.toString(oldHexagon.getX()));
		dlgHexagon.getTxtYcoordinate().setText(Integer.toString(oldHexagon.getY()));
		dlgHexagon.getTxtRadiusLength().setText(Integer.toString(oldHexagon.getR()));
		dlgHexagon.getBtnEdgeColor().setBackground(oldHexagon.getColor());
		dlgHexagon.getBtnInteriorColor().setBackground(oldHexagon.getInteriorColor());
		
		dlgHexagon.setVisible(true);
		if(dlgHexagon.isConfirmed()) {
			Hexagon hex = new Hexagon(dlgHexagon.getXcoordinate(), dlgHexagon.getYcoordinate(), dlgHexagon.getRadiusLength());
			hex.setAreaColor(dlgHexagon.getBtnInteriorColor().getBackground());
			hex.setBorderColor(dlgHexagon.getBtnEdgeColor().getBackground());
			
			HexagonAdapter newHexagon = new HexagonAdapter(hex);
			
			CmdUpdateHexagon cmdUpdateHexagon = new CmdUpdateHexagon(oldHexagon, newHexagon);
			cmdUpdateHexagon.execute();
			
			addCommandInStack(cmdUpdateHexagon); 
			log.addElement(cmdUpdateHexagon.toLogText());
			stackRedo.removeAllElements();
			frame.getBtnRedo().setEnabled(false);
			
			//oldHexagon.setX(newHexagon.getX());
			//oldHexagon.setY(newHexagon.getY());
			//oldHexagon.setColor(newHexagon.getColor());
			//oldHexagon.setInteriorColor(newHexagon.getInteriorColor());
			//oldHexagon.setR(newHexagon.getR());	
		}
		
	}

	private void btnUpdateDonutClicked(Donut oldDonut) {
		DlgDonut dlgDonut = new DlgDonut();
		dlgDonut.getTxtXcoordinateOfCenter().setText(Integer.toString(oldDonut.getCenter().getX())); 
		dlgDonut.getTxtYcoordinateOfCenter().setText(Integer.toString(oldDonut.getCenter().getY()));
		dlgDonut.getTxtRadiusLength().setText(Integer.toString(oldDonut.getRadius())); 
		dlgDonut.getTxtInnerRadiusLength().setText(Integer.toString(oldDonut.getInnerRadius()));
		dlgDonut.getBtnEdgeColor().setBackground(oldDonut.getColor());
		dlgDonut.getBtnInteriorColor().setBackground(oldDonut.getInteriorColor());
		
		dlgDonut.setVisible(true);
		if(dlgDonut.isConfirmed()) {
			Donut newDonut = new Donut(new Point(dlgDonut.getXcoordinateOfCenter(), dlgDonut.getYcoordinateOfCenter()), dlgDonut.getRadiusLength(), dlgDonut.getInnerRadiusLength(), dlgDonut.getBtnEdgeColor().getBackground(), dlgDonut.getBtnInteriorColor().getBackground());
			
			CmdUpdateDonut cmdUpdateDonut = new CmdUpdateDonut(oldDonut, newDonut);
			cmdUpdateDonut.execute();
			
			addCommandInStack(cmdUpdateDonut); 
			log.addElement(cmdUpdateDonut.toLogText());
			stackRedo.removeAllElements();
			frame.getBtnRedo().setEnabled(false);
			
			//oldDonut.setCenter(newDonut.getCenter());
			//oldDonut.setRadius(newDonut.getRadius());
			//oldDonut.setInnerRadius(newDonut.getInnerRadius());
			//oldDonut.setInteriorColor(newDonut.getInteriorColor());
			//oldDonut.setColor(newDonut.getColor());
		}
				
	}

	private void btnUpdateRectangleClicked(Rectangle oldRectangle) {
		DlgRectangle dlgRectangle = new DlgRectangle();
		dlgRectangle.getTxtXcoordinate().setText(Integer.toString(oldRectangle.getUpLeft().getX()));
		dlgRectangle.getTxtYcoordinate().setText(Integer.toString(oldRectangle.getUpLeft().getY()));
		dlgRectangle.getTxtWidth().setText(Integer.toString(oldRectangle.getWidth()));
		dlgRectangle.getTxtHeight().setText(Integer.toString(oldRectangle.getHeight()));
		dlgRectangle.getBtnEdgeColor().setBackground(oldRectangle.getColor());
		dlgRectangle.getBtnInteriorColor().setBackground(oldRectangle.getInteriorColor());
		
		dlgRectangle.setVisible(true);
		if(dlgRectangle.isConfirmed()) {
			Rectangle newRectangle = new Rectangle(new Point(dlgRectangle.getXcoordinate(), dlgRectangle.getYcoordinate()), dlgRectangle.getRectangleWidth(), dlgRectangle.getRectangleHeight(), dlgRectangle.getBtnEdgeColor().getBackground(), dlgRectangle.getBtnInteriorColor().getBackground());
			
			CmdUpdateRectangle cmdUpdateRectangle = new CmdUpdateRectangle(oldRectangle, newRectangle);
			cmdUpdateRectangle.execute();
			
			addCommandInStack(cmdUpdateRectangle); 
			log.addElement(cmdUpdateRectangle.toLogText());
			stackRedo.removeAllElements();
			frame.getBtnRedo().setEnabled(false);
			
			//oldRectangle.setUpLeft(newRectangle.getUpLeft());
			//oldRectangle.setWidth(newRectangle.getWidth());
			//oldRectangle.setHeight(newRectangle.getHeight());
			//oldRectangle.setColor(newRectangle.getColor());
			//oldRectangle.setInteriorColor(newRectangle.getInteriorColor());
		}
		
	}

	private void btnUpdateLineClicked(Line oldLine) {
		DlgLine dlgLine = new DlgLine();
		dlgLine.getTxtxCoordinateStartPoint().setText(Integer.toString(oldLine.getStartPoint().getX()));
		dlgLine.getTxtyCoordinateStartPoint().setText(Integer.toString(oldLine.getStartPoint().getY()));
		dlgLine.getTxtxCoordinateEndPoint().setText(Integer.toString(oldLine.getEndPoint().getX()));
		dlgLine.getTxtyCoordinateEndPoint().setText(Integer.toString(oldLine.getEndPoint().getY()));
		dlgLine.getBtnColor().setBackground(oldLine.getColor());
				
		dlgLine.setVisible(true);
		if(dlgLine.isConfirmed()) {
			Line newLine =  new Line(new Point(dlgLine.getxCoordinateStartPoint(), dlgLine.getyCoordinateStartPoint()), new Point(dlgLine.getxCoordinateEndPoint(), dlgLine.getyCoordinateEndPoint()), dlgLine.getBtnColor().getBackground());
			
			CmdUpdateLine cmdUpdateLine = new CmdUpdateLine(oldLine, newLine);
			cmdUpdateLine.execute();
			
			addCommandInStack(cmdUpdateLine); 
			log.addElement(cmdUpdateLine.toLogText());
			stackRedo.removeAllElements();
			frame.getBtnRedo().setEnabled(false);
			
			//oldLine.setStartPoint(newLine.getStartPoint());
			//oldLine.setEndPoint(newLine.getEndPoint());
			//oldLine.setColor(newLine.getColor());
		}
		
	}

	private void btnUpdatePointClicked(Point oldPoint) {
		DlgPoint dlgPoint = new DlgPoint();
		dlgPoint.getTxtXcoordinate().setText(Integer.toString(oldPoint.getX())); 
		dlgPoint.getTxtYcoordinate().setText(Integer.toString(oldPoint.getY()));
		dlgPoint.getBtnColor().setBackground(oldPoint.getColor());	
		
		dlgPoint.setVisible(true);
		if(dlgPoint.isConfirmed()) {
			Point newPoint = new Point(dlgPoint.getXcoordinate(), dlgPoint.getYcoordinate(), dlgPoint.getBtnColor().getBackground());
			
			CmdUpdatePoint cmdUpdatePoint = new CmdUpdatePoint(oldPoint, newPoint);
			cmdUpdatePoint.execute();
			
			addCommandInStack(cmdUpdatePoint); 
			log.addElement(cmdUpdatePoint.toLogText());
			stackRedo.removeAllElements();
			frame.getBtnRedo().setEnabled(false);
			
			//oldPoint.setX(newPoint.getX());
			//oldPoint.setY(newPoint.getY());
			//oldPoint.setColor(newPoint.getColor());	
		}
	}

	private void btnUpdateCircleClicked(Circle oldCircle) {
		DlgCircle dlgCircle = new DlgCircle();
		dlgCircle.getTxtXcoordinateOfCenter().setText(Integer.toString(oldCircle.getCenter().getX())); 
		dlgCircle.getTxtYcoordinateOfCenter().setText(Integer.toString(oldCircle.getCenter().getY()));
		dlgCircle.getTxtRadiusLength().setText(Integer.toString(oldCircle.getRadius())); 
		dlgCircle.getBtnEdgeColor().setBackground(oldCircle.getColor());
		dlgCircle.getBtnInteriorColor().setBackground(oldCircle.getInteriorColor());
		
		dlgCircle.setVisible(true);
		if(dlgCircle.isConfirmed()) {
			Circle newCircle = new Circle(new Point(dlgCircle.getXcoordinateOfCenter(), dlgCircle.getYcoordinateOfCenter()), dlgCircle.getRadiusLength(), dlgCircle.getBtnEdgeColor().getBackground(), dlgCircle.getBtnInteriorColor().getBackground());			
			
			CmdUpdateCircle cmdUpdateCircle = new CmdUpdateCircle(oldCircle, newCircle);
			cmdUpdateCircle.execute();
			
			addCommandInStack(cmdUpdateCircle); 
			log.addElement(cmdUpdateCircle.toLogText());
			stackRedo.removeAllElements();
			frame.getBtnRedo().setEnabled(false);
			
			//oldCircle.setCenter(newCircle.getCenter());
			//oldCircle.setRadius(newCircle.getRadius());
			//oldCircle.setInteriorColor(newCircle.getInteriorColor());
			//oldCircle.setColor(newCircle.getColor());
		}	
	}

	private Shape getSelectedShape() {
		Iterator<Shape> iterator = model.getAll().iterator();
		while(iterator.hasNext()) {
			Shape shapeForModification = iterator.next();
			if(shapeForModification.isSelected())
				return shapeForModification;
		}
		return null;
	}
	
	public void checkButtons() {
		if(selectedShapes.size() !=0) {
			if(selectedShapes.size()==1) {
				observable.setBtnUpdateActivated(true);
				btnUpdate();
			} 
			else {
				observable.setBtnUpdateActivated(false);
				observable.setBtnBringToBackActivated(false);
				observable.setBtnBringToFrontActivated(false);
				observable.setBtnToBackActivated(false);
				observable.setBtnToFrontActivated(false);
			}
			observable.setBtnDeleteActivated(true);
		} 
		else {
			observable.setBtnUpdateActivated(false);
			observable.setBtnDeleteActivated(false);
			observable.setBtnBringToBackActivated(false);
			observable.setBtnBringToFrontActivated(false);
			observable.setBtnToBackActivated(false);
			observable.setBtnToFrontActivated(false);
		}
	}
	
	public void btnUpdate() {
		ListIterator<Shape> it = model.getShapes().listIterator();
		while(it.hasNext())
		{
			selected = it.next();
			if(selected.isSelected()) {
				if(model.getShapes().size() !=1) {
					if(selected.equals(model.get(model.getShapes().size()-1))) { 
						observable.setBtnBringToBackActivated(true);
						observable.setBtnBringToFrontActivated(false);
						observable.setBtnToBackActivated(true);
						observable.setBtnToFrontActivated(false);
					} 
					else if (selected.equals(model.get(0))) { 
						observable.setBtnBringToBackActivated(false);
						observable.setBtnBringToFrontActivated(true);
						observable.setBtnToBackActivated(false);
						observable.setBtnToFrontActivated(true);
					} 
					else { 
						observable.setBtnBringToBackActivated(true);
						observable.setBtnBringToFrontActivated(true);
						observable.setBtnToBackActivated(true);
						observable.setBtnToFrontActivated(true);
					}
				}
			}
		}
	}
	
	public void bringToFront() {
		if (selectedShapes.size() != 1)
			return;
		Shape s= getSelectedShape();
		
		CmdBringToFront cmdBToF=new CmdBringToFront(model,s);
		cmdBToF.execute();
		
		addCommandInStack(cmdBToF); 
		log.addElement(cmdBToF.toLogText());

		frame.getView().repaint();
		checkButtons();
	}

	public void bringToBack() {
		if (selectedShapes.size() != 1)
			return;
		Shape s=getSelectedShape();
		
		CmdBringToBack cmdBToB=new CmdBringToBack(model,s);
		cmdBToB.execute();
		
		addCommandInStack(cmdBToB); 
		log.addElement(cmdBToB.toLogText());
	
		frame.getView().repaint();
		checkButtons();
	}

	public void toBack() {
		if (selectedShapes.size() != 1)
			return;
		Shape s=getSelectedShape();
		
		CmdToBack cmdToB=new CmdToBack(model,s);
		cmdToB.execute();
		
		addCommandInStack(cmdToB); 
		log.addElement(cmdToB.toLogText());
		
		frame.getView().repaint();	
		checkButtons();
	}

	public void toFront() {
		if (selectedShapes.size() != 1)
			return;
		Shape s=getSelectedShape();
		
		CmdToFront cmdToF=new CmdToFront(model,s);
		cmdToF.execute();
		
		addCommandInStack(cmdToF);
		log.addElement(cmdToF.toLogText());
		
		frame.getView().repaint();	
		checkButtons();
	}
	
	public void undo() {
		if(frame.getBtnUndo().isEnabled()) {
			//System.out.println(stackUndo.size());
			//System.out.println(stackUndo.peek().toLogText());
			log.addElement("Undo->"+stackUndo.peek().toLogText());
			
			UndoRedo.undo(stackUndo,stackRedo);
		
			if(stackUndo.isEmpty() && !stackRedo.isEmpty()) {
				frame.getBtnUndo().setEnabled(false);
				frame.getBtnRedo().setEnabled(true);
				} 
			else if(!stackUndo.isEmpty() && !stackRedo.isEmpty()) {
					frame.getBtnUndo().setEnabled(true);
					frame.getBtnRedo().setEnabled(true);
				} 
			frame.getView().repaint();
			checkButtons();
		}	
	}
	
	public void redo() {
		if(frame.getBtnRedo().isEnabled()) {
			
			log.addElement("Redo->"+stackRedo.peek().toLogText());
			
			UndoRedo.redo(stackUndo,stackRedo);
			
			if(stackRedo.isEmpty() && !stackUndo.isEmpty()) {
				frame.getBtnUndo().setEnabled(true);
				frame.getBtnRedo().setEnabled(false);
				
				} 
			else if(!stackRedo.isEmpty() && !stackUndo.isEmpty()) {
					frame.getBtnUndo().setEnabled(true);
					frame.getBtnRedo().setEnabled(true);
				}
			frame.getView().repaint();
			checkButtons();
		}	
	}
	
	public void addCommandInStack(Command command) { 
		 stackUndo.push(command); 
		
		if(!stackUndo.isEmpty()) {
			frame.getBtnUndo().setEnabled(true);
		}
	}

	public void save() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
	    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); 
		chooser.enableInputMethods(false);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileHidingEnabled(false);
		chooser.setEnabled(true);
		chooser.setDialogTitle("Save");
		chooser.setAcceptAllFileFilterUsed(false);
		if (!model.getAll().isEmpty()) {
			chooser.setFileFilter(new FileNameExtensionFilter("Serialized draw", "ser"));
		}
		if (!stackUndo.isEmpty()) chooser.setFileFilter(new FileNameExtensionFilter("Commands log", "log"));
		if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			if (chooser.getFileFilter().getDescription().equals("Serialized draw")) manager = new ManagerFile(new SerializableFile(model));
			else if (chooser.getFileFilter().getDescription().equals("Commands log")) manager = new ManagerFile(new LogFile(frame, model, this));
			else manager = new ManagerFile(new SaveDraw(frame));
			manager.save(chooser.getSelectedFile());
		}
		chooser.setVisible(false);
	}
	
	public void open() {
		JFileChooser chooser = new JFileChooser();
		chooser.enableInputMethods(true);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileHidingEnabled(false);
		chooser.setEnabled(true);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
		chooser.setFileFilter(new FileNameExtensionFilter("Serialized draw", "ser"));
		chooser.setFileFilter(new FileNameExtensionFilter("Commands log", "log"));
		if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			model.removeAll();
			log.removeAllElements();
			stackRedo.clear();
			stackUndo.clear();
			frame.getView().repaint();
			if (chooser.getFileFilter().getDescription().equals("Serialized draw")) {
				manager = new ManagerFile(new SerializableFile(model));
				propertyChangeSupport.firePropertyChange("serialized draw opened", false, true);
			}
			else if (chooser.getFileFilter().getDescription().equals("Commands log")) manager = new ManagerFile(new LogFile(frame, model, this));
			manager.open(chooser.getSelectedFile());
		}	
		chooser.setVisible(false);
	}
	
	public void newDraw() {
		if(JOptionPane.showConfirmDialog(null, "Are you sure that you want to start new draw?", "Warning", JOptionPane.YES_NO_OPTION) == 0) {	
			model.removeAll();
			log.removeAllElements();
			stackRedo.clear();
			stackUndo.clear();
			propertyChangeSupport.firePropertyChange("draw is empty", false, true);
			frame.getView().repaint();
		}
	}
	
	public void handleSelect(String s, String command) {
		if (command.equals("redo")) {
			if (s.equals("Selected")) ++counterOfSelectedShapes;
			else --counterOfSelectedShapes;
			handleSelectButtons();
		} else if (command.equals("undo")) {
			if (s.equals("Selected")) --counterOfSelectedShapes;
			else ++counterOfSelectedShapes;
			handleSelectButtons();
		} else if (command.equals("parser")) {
			if (s.equals("Selected")) ++counterOfSelectedShapes;
			else --counterOfSelectedShapes;
		}
	}
	
	public void handleSelectButtons() {
		if (counterOfSelectedShapes == 0) propertyChangeSupport.firePropertyChange("unselected", false, true);
		else if (counterOfSelectedShapes == 1)  {
			propertyChangeSupport.firePropertyChange("update turn on", false, true);
			propertyChangeSupport.firePropertyChange("selected", false, true);
		}  
		else if (counterOfSelectedShapes > 1) propertyChangeSupport.firePropertyChange("update turn off", false, true);
	}
	
	public void executeCommand(Command command) {
		command.execute();
		stackUndo.push(command);
		
		if (!stackRedo.isEmpty()) {
			stackRedo.removeAllElements();
			propertyChangeSupport.firePropertyChange("redo turn off", false, true);
		}
		
		if (model.getAll().isEmpty()) propertyChangeSupport.firePropertyChange("don't exist", false, true);
		else if (model.getAll().size() == 1) propertyChangeSupport.firePropertyChange("exist", false, true);
		
		if (stackUndo.isEmpty()) propertyChangeSupport.firePropertyChange("draw is empty", false, true);
		else if (stackUndo.size() == 1) propertyChangeSupport.firePropertyChange("draw is not empty", false, true);
		frame.getView().repaint();
	}

	public List<Shape> getSelectedShapes() {
		return selectedShapes;
	}
}
