# Design Patterns Java Application
Java aplication for drawing shapes with the implementation of design patterns. The application is a project from the course Design Patterns.

### Design patterns that were used:
1. MVC
2. Adapter - used for Hexagon shape
3. Command - used for Undo/Redo functionality
4. Prototype - used for Undo/Redo functionality
5. Strategy - used for saving the command log and saving the complete drawing
6. Observer - used for Edit/Delete buttons to move the shape along the z axis 

### Shapes that you can draw:
* Point
* Line
* Circle
* Rectangle
* Donut
* Hexagon

You can also select multiple object, modify specific object and delete them.
Each command is recorded in the log. It is possible to save a log or an entire drawing.
There is a possibility to load command by command from the saved log.
