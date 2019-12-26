import org.apache.ignite.*;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
//Dhruv
import java.util.*;
import java.io.File;
import java.lang.reflect.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.util.Collection;
import java.lang.*;

public class IgniteStart_New 
{	
	static String AREA_CACHE = "Area-Cache";
	static String POINT_CACHE = "Point-Cache";
	static String EDGE_CACHE = "Edge-Cache";
	static String WATER_AREA_CACHE = "WaterArea-Cache";

	//Dhruv
	static IgniteCache<String, Helper<String>> dhruvAreaCache;
	static IgniteCache<String, Helper<String>> dhruvEdgeCache;

	static IgniteCache tableCache;


	static IgniteCache<String, Area> areaCache;
	static IgniteCache<String, WaterArea> waterAreaCache;
	static IgniteCache<String, Point> pointCache;
	static IgniteCache<String, Edge> edgeCache;


	public static void main( String[] args ) 
	{
		//String[] datasets = {"arealm", "areawater", "pointlm", "edges"};
		//Dhruv: String[] datasets = {"arealm_merge_ca", "areawater_merge_ca", "pointlm_merge_ca", "edges_merge_ca"};
		String[] datasets = {"arealm_merge_ca", "areawater_merge_ca", "pointlm_merge_ca", "edges_merge_ca_split_dhruv"};
		String datasetPath = ""; // /home/bigdata/dataset/ignite
		int QP;  //Dhruv: QP is query parallelism

		if(args.length < 2) {
			System.out.println("Parameter Missing! dataset path, QP");
			return;
		}
		else {
			datasetPath = args[0]; //dataset path
			QP = Integer.parseInt(args[1]); //query parallelism
		}
		System.out.println("Dataset Path: " + datasetPath);

		RendezvousAffinityFunction affFunc = new RendezvousAffinityFunction();     
		affFunc.setExcludeNeighbors(true);		        
		affFunc.setPartitions(2048);

		CacheConfiguration<String, Area> areaCacheCfg = new CacheConfiguration<>(AREA_CACHE);
		areaCacheCfg.setIndexedTypes(String.class, Area.class);
		areaCacheCfg.setSqlFunctionClasses(GeometryFunctions.class);
		areaCacheCfg.setCacheMode(CacheMode.PARTITIONED);					
		//areaCacheCfg.setDataRegionName("AREALM");
		areaCacheCfg.setAffinity(affFunc);
		areaCacheCfg.setBackups(0);
		//areaCacheCfg.setOnheapCacheEnabled(true);		
		areaCacheCfg.setQueryParallelism(QP);		

		//System.out.println("Query Parallelism: " + areaCacheCfg.getQueryParallelism());

		CacheConfiguration<String, WaterArea> waterAreaCacheCfg = new CacheConfiguration<>(WATER_AREA_CACHE);
		waterAreaCacheCfg.setIndexedTypes(String.class, WaterArea.class);
		waterAreaCacheCfg.setSqlFunctionClasses(GeometryFunctions.class);
		waterAreaCacheCfg.setCacheMode(CacheMode.PARTITIONED);		
		//waterAreaCacheCfg.setDataRegionName("AREAWATER");
		waterAreaCacheCfg.setAffinity(affFunc);
		waterAreaCacheCfg.setBackups(0);
		//waterAreaCacheCfg.setOnheapCacheEnabled(true);
		waterAreaCacheCfg.setQueryParallelism(QP);

		CacheConfiguration<String, Point> pointCacheCfg = new CacheConfiguration<>(POINT_CACHE);
		pointCacheCfg.setIndexedTypes(String.class, Point.class);
		pointCacheCfg.setSqlFunctionClasses(GeometryFunctions.class);
		pointCacheCfg.setCacheMode(CacheMode.PARTITIONED);
		//pointCacheCfg.setDataRegionName("POINTLM");
		pointCacheCfg.setAffinity(affFunc);
		pointCacheCfg.setBackups(0);
		//pointCacheCfg.setOnheapCacheEnabled(true);
		pointCacheCfg.setQueryParallelism(QP);

		CacheConfiguration<String, Edge> edgeCacheCfg = new CacheConfiguration<>(EDGE_CACHE);
		edgeCacheCfg.setIndexedTypes(String.class, Edge.class);
		edgeCacheCfg.setSqlFunctionClasses(GeometryFunctions.class);
		//edgeCacheCfg.setSwapEnabled(true);
		edgeCacheCfg.setCacheMode(CacheMode.PARTITIONED);    
		//edgeCacheCfg.setDataRegionName("EDGES");
		edgeCacheCfg.setAffinity(affFunc);
		edgeCacheCfg.setBackups(0);
		//edgeCacheCfg.setOnheapCacheEnabled(true);
		edgeCacheCfg.setQueryParallelism(QP);

		//Dhruv
		HashMap<String, Element> contentsForEveryTable = null;
		try (Ignite ignite = Ignition.start()) {

			ignite.cluster().active(true); //to activate cluster with persistence			

			// GET OR CREATE CACHES
			////////////////////////////////////////////////////////////////
			//Dhruv
			/*CacheConfiguration<String, Helper<String>> dhruvareaCacheCfg = new CacheConfiguration<>(AREA_CACHE);
			areaCacheCfg.setIndexedTypes(String.class, Area.class);
			areaCacheCfg.setSqlFunctionClasses(GeometryFunctions.class);
			areaCacheCfg.setCacheMode(CacheMode.PARTITIONED);						
			//areaCacheCfg.setDataRegionName("AREALM");
			areaCacheCfg.setAffinity(affFunc);
			areaCacheCfg.setBackups(0);
			//areaCacheCfg.setOnheapCacheEnabled(true);		
			areaCacheCfg.setQueryParallelism(QP);
			dhruvAreaCache = ignite.getOrCreateCache(dhruvareaCacheCfg);
			System.out.println("DHRUV AREA CACHE CREATED");
			 */

			////////////////////////////////////////////////////////////////////////////////////

			//Dhruv 
			//filling generic cache
			tableCache = ignite.cache("tableCache");
			ArrayList<String> tabKeys = new ArrayList<String>();
			tabKeys.add("arealm");
			tabKeys.add("edges");
			
			//parsing file
			try{
				File tableconfigFile = new File("tableconfig.xml");
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(tableconfigFile);
				doc.getDocumentElement().normalize();
				Node root = doc.getDocumentElement();
				//System.out.println("Root element :" + root.getNodeName());
				NodeList tables = root.getChildNodes();  //stores all the tables
				//make lists of elements for every table
				// table1 -> name, schema, datafile
				contentsForEveryTable = new HashMap<String, Element>();
				for (int temp = 0; temp < tables.getLength(); temp++) {
					Node thisTable = tables.item(temp);
					//System.out.println("\nCurrent Element :" + thisTable.getNodeName());
					if (thisTable.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) thisTable;
						if(eElement != null){
							contentsForEveryTable.put(thisTable.getNodeName(), eElement);
						}
						//					System.out.println("name : " 
						//							+ eElement.getElementsByTagName("name").item(0).getTextContent());
						//					System.out.println("datafile : " 
						//							+ eElement
						//							.getElementsByTagName("datafile").item(0).getTextContent());
					}
				}
			} catch (Exception e) {   //exception handling for parsing xml
				e.printStackTrace();
			}
			for(int i = 0 ; i < tableCache.size() ; i++){
				//tableCache.put(tabKeys.get(i), Class.forName(contentsForEveryTable.get(tabKeys.get(i))));
				tableCache.put(tabKeys.get(i), Class.forName(tabKeys.get(i)));
			}
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%5");
			System.out.println(contentsForEveryTable.get(tabKeys.get(0)));
			//convert class to IgniteCache object
			// Class.forName should return a class such as area class
			//Dhruv
			//areaCache = (IgniteCache<String, Area>)tableCache.get(tabKeys.get(0));
			areaCache = ignite.getOrCreateCache(areaCacheCfg);
			pointCache = ignite.getOrCreateCache(pointCacheCfg);
			edgeCache = ignite.getOrCreateCache(edgeCacheCfg);
			waterAreaCache = ignite.getOrCreateCache(waterAreaCacheCfg);

			System.out.println("Query Parallelism: " + areaCacheCfg.getQueryParallelism());

			try {
				LoadData.loadData(datasetPath, datasets);
			}
			catch(Exception e) {				
				e.printStackTrace();
				return;
			}				

			try {
				for(int i = 1; i < 4; i++) {
					System.out.println("\nRUN QUERY PHASE-" + i + ">>>\n");
					TopologicalRelations.runQueries();
					SpatialAnalysis.runQueries();				
				}
			}
			catch(Exception e) {
				ignite.destroyCache(AREA_CACHE);
				ignite.destroyCache(POINT_CACHE);
				ignite.destroyCache(EDGE_CACHE);
				ignite.destroyCache(WATER_AREA_CACHE);
				e.printStackTrace();
				return;
			}


			//TopologicalRelations.runQueries();
			//SpatialAnalysis.runQueries();

			System.out.println("\n!!!SUCCESSFUL!!!\n");

			ignite.destroyCache(AREA_CACHE);
			ignite.destroyCache(POINT_CACHE);
			ignite.destroyCache(EDGE_CACHE);
			ignite.destroyCache(WATER_AREA_CACHE);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void answerQuery(String query, String number){
		long startTime = System.currentTimeMillis();

		SqlFieldsQuery qry = new SqlFieldsQuery(query);
		qry.setCollocated(true);
		qry.setLazy(true);
		//qry.setDistributedJoins(true);

		Collection<java.util.List<?>> entries = areaCache.query( qry ).getAll();

		int counter = entries.size();

		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println(number + " ELAPSED TIME = " + (1.0 * elapsedTime/1000.0) + "s, Result Size: " + counter);
		//System.out.println(number + " ELAPSED TIME = " + (1.0 * elapsedTime/1000.0) + "s, \nResult Size: " + entries.toString());
	}

	public static void answerQuery2(String query, String number){
		long startTime = System.currentTimeMillis();

		SqlFieldsQuery qry = new SqlFieldsQuery(query);
		qry.setCollocated(true);
		qry.setLazy(true);

		Collection<java.util.List<?>> entries = pointCache.query( qry ).getAll();

		int counter = entries.size();

		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println(number + " ELAPSED TIME = " + (1.0 * elapsedTime/1000.0) + "s, Result Size: " + counter);
	}

	public static void answerQuery3(String query, String number){
		long startTime = System.currentTimeMillis();

		SqlFieldsQuery qry = new SqlFieldsQuery(query);
		qry.setCollocated(true);
		qry.setLazy(true);

		Collection<java.util.List<?>> entries = edgeCache.query( qry ).getAll();

		int counter = entries.size();

		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println(number + " ELAPSED TIME = " + (1.0 * elapsedTime/1000.0) + "s, Result Size: " + counter);
	}

	public static void answerQuery4(String query, String number){
		long startTime = System.currentTimeMillis();

		SqlFieldsQuery qry = new SqlFieldsQuery(query);
		qry.setCollocated(true);
		qry.setLazy(true);

		Collection<java.util.List<?>> entries = waterAreaCache.query( qry ).getAll();

		int counter = entries.size();

		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println(number + " ELAPSED TIME = " + (1.0 * elapsedTime/1000.0) + "s, Result Size: " + counter);
	}
}
