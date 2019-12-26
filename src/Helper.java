//DHRUV class
import java.io.Serializable;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import com.vividsolutions.jts.geom.Geometry;


import java.util.*;
public class Helper<String> implements Serializable {

	private String shape; 
	public String getShape(){
   		return this.shape;
   	}
   	
	/** Coordinates. */
   	@QuerySqlField(index = true) 
   	
   	private Geometry coords;
   	
     Helper(Geometry coords, String shape) {
         this.coords = coords;
         this.shape = shape;
     }

     public Geometry getGeom(){
      	return coords;
     }
}
