//DHRUV class
import java.util.HashMap;
public class Shape {

	HashMap<String, String> shapes = new HashMap<String, String>();
	
	public void addNewShape(String shape){
		this.shapes.put(shape, shape);
	}
	
	public HashMap getShapes(){
		return shapes;
	}
}
