import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ArrayDeque;


public class ford-fulkerson-algorithm {
	// Initialize constants.
	private static final int SOURCE = 0; // Source is mapped to value 0.
	private static final int NO_PARENT = -1;
	private static final int INT_MAX = Integer.MAX_VALUE;

	// The main method to implement Ford Fulkerson's algorithm for maximum flow.
	int maxFlowFinder(int graph[][], int src, int sink, int vertices) {
		
		/**
		 * Initialize residual graph as graph 
		 * which will be passed to another method.		 
		 */
        int residualGraph[][] = new int[vertices][vertices];
        for (int i = 0; i < vertices; i++)
            for (int j = 0; j < vertices; j++)
                residualGraph[i][j] = graph[i][j];
        
        // Initialize maximum flow as 0.
        int totalMaxFlow = 0;
 
        // Initialize parent array.
        int parents[] = new int[vertices];

        /**
         * Execute augmentingPathFinder in conditioning
         * and return true if there is augmenting path.
         * The code contains modifications for parent
         * array. Use modified parent array inside of 
         * loop to modify residual graph and maximum flow.
         */
        while (augmentingPathFinder(residualGraph, parents, src, sink, vertices)) {
        	
        	/**
        	 * Declare parent node and initialize current 
        	 * node as sink, bottleneck flow as infinity.
             */
        	int parentNode, currentNode = sink, bottleneckFlow = INT_MAX;
            
            // Loop to find bottleneck flow in found augmenting path.
            while (currentNode != src) {
            	parentNode = parents[currentNode];
				bottleneckFlow = (bottleneckFlow < residualGraph[parentNode][currentNode]) ? 
								  bottleneckFlow : residualGraph[parentNode][currentNode];
            	currentNode = parents[currentNode];
            }
            
            /*
             *  Loop from sink to source in the path
             *  to subtract bottleneck flow from edges, 
             *  create reverse edges and add bottleneck
             *  flow to reverse edges.
             */
            currentNode = sink;
            while (currentNode != src) {
            	parentNode = parents[currentNode];
                residualGraph[parentNode][currentNode] -= bottleneckFlow;
                residualGraph[currentNode][parentNode] += bottleneckFlow;
            	currentNode = parents[currentNode];
            }
 
            // Add this augmenting path's bottleneck flow to overall flow.
            totalMaxFlow += bottleneckFlow;
        }
        
		// Return the overall flow
		return totalMaxFlow;
	}
	
	// Check if there is augmenting path for continuation of modification of maximum flow.
	boolean augmentingPathFinder(int residualGraph[][], int parents[], int src, int sink, int vertices) {
		
		// Source doesn't have a parent.
		parents[src] = NO_PARENT;

		// Index will return true if index is visited.
		boolean[] visited = new boolean[vertices];
		
		// Initialize all vertices as unvisited, except the source.
		for (int i = 0; i < vertices; i++)
			visited[i] = false;
		visited[src] = true;
		
		// ArrayDeque is used for performance considerations.
		ArrayDeque<Integer> arrayDeque = new ArrayDeque<Integer>();
		// Add source to queue.
		arrayDeque.add(src);
				
		/** 
		 * Find augmenting path, from source to sink
		 * and obtain related parents array.
		 */
		while (arrayDeque.size() != 0) {
			
			// Obtain current node to loop through adjacency nodes.
			int currentNode = arrayDeque.poll();

			for (int adjNode = 0; adjNode < vertices; adjNode++) {
				
				/**
				 * Find new augmenting path around not visited nodes
				 * and edges whose remaining capacity is greater than
				 * zero. Fill parents and visited array, enqueue 
				 * adjacency nodes if sink is not reached yet.  
				 */
				if (!(visited[adjNode]) && residualGraph[currentNode][adjNode] > 0) {

					parents[adjNode] = currentNode;
					
					// If sink node is reached, then building augmenting path is finished.
					if (adjNode == sink) 
						// New path is found.
						return true;
					
					/**
					 * If adjacency node is not sink, then continue looping while 
					 * checking adjacency nodes of current adjacency nodes and so on.
					 */					
					else {
						// Mark this adjacency node as visited, since we want to move to the sink.
						visited[adjNode] = true;
						// Add this node to queue, since we want to loop through its neighbors.
						arrayDeque.add(adjNode);
					}
				}
			}
		}
		
		// There is no remaining augmenting path.
		return false;
	}
		
	/**
	 * A method to check if given bag can travel by 
	 * different 4 types of vehicles. Returns true
	 * for types specific indexes for each vehicle type.
	 */
	private static boolean[] getBool(String bagType) {
		
		// To check if bag is one of the following strings, if so then create an edge.
		String[] trainToGreenStr = {"a", "b", "d", "ab", "ad", "bd", "abd"};
		ArrayList<String> TTGId = new ArrayList<String>();
		for (int i = 0; i < trainToGreenStr.length; i++)
			TTGId.add(trainToGreenStr[i]);

		// To check if bag is one of the following strings, if so then create an edge.
		String[] trainToRedStr = {"a", "c", "d", "ac", "ad", "cd", "acd"};
		ArrayList<String> TTRId = new ArrayList<String>();
		for (int i = 0; i < trainToRedStr.length; i++)
			TTRId.add(trainToRedStr[i]);

		// To check if bag is one of the following strings, if so then create an edge.
		String[] rdeerToGreenStr = {"a", "b", "e", "ab", "ae", "be", "abe"};
		ArrayList<String> RTGId = new ArrayList<String>();
		for (int i = 0; i < rdeerToGreenStr.length; i++)
			RTGId.add(rdeerToGreenStr[i]);

		// To check if bag is one of the following strings, if so then create an edge.
		String[] rdeerToRedStr = {"a", "c", "e", "ac", "ae", "ce", "ace"};
		ArrayList<String> RTRId = new ArrayList<String>();
		for (int i = 0; i < rdeerToRedStr.length; i++)
			RTRId.add(rdeerToRedStr[i]);
		
		/**
		 * Initialize contains boolean array, 
		 * to check whether bag can be 
		 * traveled with vehicles or not 
		 * (4 different type of vehicles).
		 */
		boolean[] contains = new boolean[4];
		
		/**
		 * String may include more than one of type of vehicles, e.g. "a" is included
		 * in all types of vehicles. If bag type is "a", then we have to create an edge
		 * between that bag to all amount of vehicles where type doesn't matter. In how
		 * many of edges gift will pass is determined by maximum flow algorithm. So,
		 * obtain which type of vehicles the current bag will have an edge to.
		 */
		contains[0] = (TTGId.contains(bagType)) ? true : false;
		contains[1] = (TTRId.contains(bagType)) ? true : false;
		contains[2] = (RTGId.contains(bagType)) ? true : false;
		contains[3] = (RTRId.contains(bagType)) ? true : false;
		
		return contains;
	}

	

	public static void main (String[] args) throws IOException {
		
		long start1 = System.nanoTime();
	    
		// Read all lines in input.
		ArrayList<String> allLines = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			String line = reader.readLine();
			while (line != null) {
				allLines.add(line);
				line = reader.readLine();
			}				
			reader.close();
		} catch (FileNotFoundException err) {
			System.out.println("An error occured while reading the file.");
			err.printStackTrace();
		}
		
		/**
		 * The idea is that for each vehicle (could be same type) there is a node,
		 * and all these vehicles have directed edges to the sink. Implementation
		 * of graph is started with these vehicles, to do that bags are counted
		 * first and starting index of vehicles are found. Each bag (could be same
		 * type) is also mapped to different node. While looping the last two lines,
		 * directed edges from source to bags and bags to vehicles are created. To
		 * connect bags with vehicles is made by testing whether type of bag can travel.
		 * After implementation, graph is passed to max flow finder method and where
		 * residual graph is created and modifications are made.
		 * 
		 */
		
		// Initialize total vertex number as 2 which is source and sink.
		int totalVertex = 2;
		
		// Loop through 0th, 2nd, 4th, 6th, 8th lines to find total vertex number.
		for (int i = 0; i < allLines.size(); i += 2)
			totalVertex += Integer.parseInt(allLines.get(i).split(" ")[0]);
		int sink = totalVertex - 1; // Sink's index.
		
		// Find vehicles' starting index (+1 is due to the source).
		int vehicleIndex = Integer.parseInt(allLines.get(8).split(" ")[0]) + 1;
		
		// Declare number of vehicles for each type and starting vertices.
		ArrayList<Integer> vehicleNum = new ArrayList<Integer>();
		ArrayList<Integer> startingVertices = new ArrayList<Integer>();
		
		// Initialize graph.
		int graph[][] = new int[totalVertex][totalVertex];
		
		// Vehicles to sink part of graph.
		int ind;
		for (ind = 0; ind < 8; ind++) {
			// Get how many vehicles there are.
			String[] wordsArr = allLines.get(ind++).split(" ");
			
			// Store number of vehicles and starting indexes of them for future use.
			vehicleNum.add(Integer.parseInt(wordsArr[0]));
			startingVertices.add(vehicleIndex);
			
			// Get capacities of vehicles.
			wordsArr = allLines.get(ind).split(" ");
			
			// Loop through capacities.
			for (int k = 0; k < vehicleNum.get(ind/2); k++)
				graph[vehicleIndex++][sink] = Integer.parseInt(wordsArr[k]);
		}
		
		/**
		 * Directed edges from source to bags 
		 * and bags to vehicles parts of graph.
		 */
		
		// Obtain number of bags to loop.
		int bagNumber = Integer.parseInt(allLines.get(ind++).split(" ")[0]);
		
		// Line about bag types and numbers of gifts.
		String[] bagsStrArray = allLines.get(ind).split(" ");
		
		// Bag indexes start with 1, 0 is source.
		int bagIndex = 1;
		
		/**
		 * Initialize total capacities of bags 
		 * to find undistributed bag number.
		 */
		int totalCapacity = 0;
		
		for (int i = 0; i < 2 * bagNumber; i++) {
			
			// Initialize contains boolean array and call getBool method.
			boolean[] canTravel = new boolean[4];			
			canTravel = getBool(bagsStrArray[i]);
			
			// Check if bag type contains string "a".
			boolean containsCharA = bagsStrArray[i].contains("a");
			
			// Initialize capacity of current bag.
			int capacity = Integer.parseInt(bagsStrArray[++i]);
			
			// Increase total capacity.
			totalCapacity += capacity;
			
			// Add edge from source to index of the bag with capacity.
			graph[SOURCE][bagIndex] = capacity;
			
			
			/**
			 * If bag type contains string "a", then assigned capacities 
			 * towards different kind of vehicles are 1, else normal.
			 */
			if (containsCharA) {
				// Loop through four types of vehicles.
				for (int j = 0; j < 4; j++) {
					/**
					 * If bag can be traveled with j'th type 
					 * of vehicle, then create edge from bag 
					 * node to all j'th type of vehicles.
					 */
					if (canTravel[j])
						for (int k = 0; k < vehicleNum.get(j); k++)
							graph[bagIndex][startingVertices.get(j) + k] = 1;
				}
			} else {
				for (int j = 0; j < 4; j++) {
				if (canTravel[j])
					for (int k = 0; k < vehicleNum.get(j); k++)
						graph[bagIndex][startingVertices.get(j) + k] = capacity;
				}
			}
			
			// Increase index of bag by one to create next node for bags.
			bagIndex++;
		}
	
		// Create an instance of main class.
		ford-fulkerson-algorithm instance = new ford-fulkerson-algorithm();
		
		// Call maxFlowFinder method to obtain maximum flow.
		int maxFlow = instance.maxFlowFinder(graph, SOURCE, sink, totalVertex);
		
		// Find result.
		String result = String.valueOf(totalCapacity - maxFlow);
		
		// Create output file and check if that file name exists.
		String outputPath = args[1];
		File output = new File(outputPath);
		try {					
			if (output.createNewFile())
				System.out.println("File is created.");
			else {
				System.out.println("File already exists.");
				return;
			}
			FileWriter writer = new FileWriter(outputPath);
			writer.write(result);
			writer.close();
		} catch (Exception err) {
			System.out.println("An error occured while writing to the file.");
			err.getStackTrace();
		}
		System.out.println("Milliseconds passed: " + (System.nanoTime() - start1)/1000000);	
	}
}
