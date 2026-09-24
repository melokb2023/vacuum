import java.util.*;

/**
 * Vacuum World Search Strategy Solver
 * Implementation of BFS, DFS, and A* Search.
 */
public class vacuum {
   
     // STEP 1: Defining the State of the Room whether if the room is clean or dirty and the location of the vacuum
public static class rState {
    String room;    // "Room A" or "Room B"
    boolean isroomAclean; // true means the room is Clean, false is considered Dirty
    boolean isroomBclean; // true means the room is Clean, false is considered Dirty

    //Constructor to initialize room state and vacuum location
    public rState(String room, boolean isroomAclean, boolean isroomBclean) {
        this.room = room;
        this.isroomAclean = isroomAclean;
        this.isroomBclean = isroomBclean;
    }

    //Checks if both of the rooms are clean, which is considered to be the goal state of the problem
    public boolean isGoal() {
        return isroomAclean && isroomBclean;
    }

    // Overrides the equals and hashCode to ensure proper comparison and storage in collections
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Check if the same object
        if (!(o instanceof rState)) return false; //Checks if the object is an instance of rState
        rState state = (rState) o; //Compares the current state with the provided state
        //Returns the value true if the current state and the provided state are equal, otherwise returns false
         return isroomAclean == state.isroomAclean &&
             isroomBclean == state.isroomBclean &&
               Objects.equals(room, state.room);
    }

    //Hashing code in order to make sure that Room and Room b are properly stored in collections and can be retrieved efficiently
    @Override
    public int hashCode() {
        return Objects.hash(room, isroomAclean, isroomBclean);
    }

    //Provides a String format determining the current state of the vacuum and the cleanliness of the rooms
    @Override
    public String toString() {
        return String.format("[The Vacuum in Rooms %s | Room A: %s | Room B: %s]",
                room, isroomAclean ? "Clean" : "Dirty", isroomBclean ? "Clean" : "Dirty");
    }
}

// Step 2: Defining the Search Node and Action Generator
    public static class Node {
        rState state;   // Current state of the room and vacuum location
        Node parent;    // A parent node to trace back the path to the initial state
        String action;  // The actions taken ("Suck", "Move to the Left", "Move to the Right")
        int cost;       // Total step cost from the start node to the current node

        //This is used to initialize the node with the current state, parent node, action taken, and cost incurred to reach this node making sure that the rooms are in fact clean
        public Node(rState state, Node parent, String action, int cost) {
            this.state = state;
            this.parent = parent;
            this.action = action;
            this.cost = cost;
        }
    }

    // Generates all possible next states from a given node
    public static List<Node> getSuccessors(Node node) {
        List<Node> successors = new ArrayList<>();
        rState present = node.state;

        //The present is used to determine the current state of the vacuum and the cleanliness of the rooms, which is used to generate these action and the possible next states based on the current state of the vacuum and the cleanliness of the rooms
        // First Action : Suck The Dust Off (It is only valid if the current room is dirty)
        if ((present.room.equalsIgnoreCase("Room A") && !present.isroomAclean) || 
            (present.room.equalsIgnoreCase("Room B") && !present.isroomBclean)) {
            
            boolean nextA = present.room.equalsIgnoreCase("Room A") ? true : present.isroomAclean;
            boolean nextB = present.room.equalsIgnoreCase("Room B") ? true : present.isroomBclean;
            
            rState nextState = new rState(present.room, nextA, nextB);
            successors.add(new Node(nextState, node, "Suck the Dust", node.cost + 1));
        }

        // Second Action: Move Left to Get to Room A(only valid if currently in Room B)
        if (present.room.equalsIgnoreCase("Room B")) {
            rState nextState = new rState("Room A", present.isroomAclean, present.isroomBclean);
            successors.add(new Node(nextState, node, "Move to the Left", node.cost + 1));
        }

        // Third Action: Move Right to Get to Room B(only valid if currently in Room A)
        if (present.room.equalsIgnoreCase("Room A")) {
            rState nextState = new rState("Room B", present.isroomAclean, present.isroomBclean);
            successors.add(new Node(nextState, node, "Move to the Right", node.cost + 1));
        }

        return successors;
    }

    // -------------------------------------------------------------
    // Step 3: Solving the Problem Using Breadth-First Search (BFS)
    // -------------------------------------------------------------
    public static void solveBFS(rState initialState) {
        System.out.println("\n----Breadth-First Search (BFS) ----");
        
        Queue<Node> frontier = new LinkedList<>();
        Set<rState> visited = new HashSet<>();

        Node root = new Node(initialState, null, "Start", 0);
        frontier.add(root);
        visited.add(initialState);

        int nodesExpanded = 0;

        //When the word frontier is not empty, the process of loop will cotinue until it reaches towards the goal state, which is when both rooms are clean. The loop will continue to expand nodes and generate successors until a solution is found or all possibilities are exhausted.
        while (!frontier.isEmpty()) {
            Node current = frontier.poll(); // Take the oldest node from the queue
            nodesExpanded++;

            // Goal Test: Check if both rooms are clean
            if (current.state.isGoal()) {
                printNarrative(current, nodesExpanded);
                return;
            }

            // Generate next possible moves
            for (Node child : getSuccessors(current)) {
                if (!visited.contains(child.state)) {
                    visited.add(child.state);
                    frontier.add(child);
                }
            }
        }
        System.out.println("There is no solution found.");
    }

    // -------------------------------------------------------------
    // Step 3: Solving the Problem Using Depth-First Search (DFS)
    // -------------------------------------------------------------
    public static void solveDFS(rState initialState) {
        System.out.println("\n=== Depth-First Search (DFS) ===");
        
        Stack<Node> frontier = new Stack<>();
        Set<rState> visited = new HashSet<>();

        Node root = new Node(initialState, null, "Start", 0);
        frontier.push(root);

        int nodesExpanded = 0;

        while (!frontier.isEmpty()) {
            Node current = frontier.pop(); // Take the newest node from the stack

            if (!visited.contains(current.state)) {
                visited.add(current.state);
                nodesExpanded++;

                // Goal Test: Check if both rooms are clean
                if (current.state.isGoal()) {
                    printNarrative(current, nodesExpanded);
                    return;
                }

                // Generate next possible moves
                for (Node child : getSuccessors(current)) {
                    if (!visited.contains(child.state)) {
                        frontier.push(child);
                    }
                }
            }
        }
        System.out.println("No solution found.");
    }

    // -------------------------------------------------------------
    // Heuristic Function Value for A* Search (Informed Strategy)
    // -------------------------------------------------------------
    private static int calculateHeuristic(rState state) {
        int dirtyroomQuantCount = 0;
        if (!state.isroomAclean) dirtyroomQuantCount++;
        if (!state.isroomBclean) dirtyroomQuantCount++;

        int distance = 0;
        // If 1 room is dirty and vacuum is in the clean room, add 1 step to travel
        if (dirtyroomQuantCount == 1) {
            if (!state.isroomAclean && state.room.equalsIgnoreCase("Room B")) distance = 1;
            if (!state.isroomBclean && state.room.equalsIgnoreCase("Room A")) distance = 1;
        }

        return dirtyroomQuantCount + distance;
    }

    // -------------------------------------------------------------
    // Step 3: Solving the Problem Using A* Search (Informed Strategy )
    // -------------------------------------------------------------
    public static void solveAStar(rState initialState) {
        System.out.println("\n=== Executing A* Search (Informed Search) ===");

        // The Priority Queue sorts nodes by total estimated cost f(n) = g(n) + h(n)
        //g(n) = Cost=so-far, h(n) = Estimated cost to goal (heuristic)
        PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingInt(n -> n.cost + calculateHeuristic(n.state)));
        Set<rState> visited = new HashSet<>();

        Node root = new Node(initialState, null, "Start", 0);
        frontier.add(root);

        int nodesExpanded = 0;

        while (!frontier.isEmpty()) {
            Node current = frontier.poll(); // Pulls the node with lowest f(n) cost

            if (visited.contains(current.state)) continue;
            visited.add(current.state);
            nodesExpanded++;

            if (current.state.isGoal()) {
                printNarrative(current, nodesExpanded);
                return;
            }

            for (Node child : getSuccessors(current)) {
                if (!visited.contains(child.state)) {
                    frontier.add(child);
                }
            }
        }
        System.out.println("No solution found.");
    }

    // -------------------------------------------------------------
    // Step 4: Printing the Narrative (Trace Path & Output Narrative)
    // -------------------------------------------------------------
    private static void printNarrative(Node goalNode, int nodesExpanded) {
        List<Node> path = new ArrayList<>();
        Node present = goalNode;

        // Traces backward from goal node to start node using 'parent'
        while (present != null) {
            path.add(present);
            present = present.parent;
        }

        // Reverse list so steps print from start (Step 0) to goal
        Collections.reverse(path);

        System.out.println("\n--- Solution Narrative Trace ---");
        for (int i = 0; i < path.size(); i++) {
            Node node = path.get(i);
            if (i == 0) {
                System.out.printf("Step %d: Initial State -> %s\n", i, node.state);
            } else {
                // Displays action taken to reach state
                System.out.printf("Step %d: Action [%s] -> %s\n", i, node.action, node.state);
            }
        }
        System.out.printf("\nTotal Actions Taken: %d\n", goalNode.cost);
        System.out.printf("Total States Expanded: %d\n", nodesExpanded);
    }

    // -------------------------------------------------------------
    // Main Method: User Input and Strategy Selection
    // -------------------------------------------------------------
    public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
        System.out.println("-----------------------------------------");
        System.out.println("   VACUUM WORLD SEARCH STRATEGY SYSTEM  ");
        System.out.println("----------------------------------------");

        System.out.print("Enter initial location of Vacuum (A/B): ");
        String roomChoice = scanner.next().trim().toUpperCase();
        while (!roomChoice.equals("A") && !roomChoice.equals("B")) {
            System.out.print("WRONG LOCATION. Enter A or B only: ");
            roomChoice = scanner.next().trim().toUpperCase();
        }
        String room = roomChoice.equals("A") ? "Room A" : "Room B";

        System.out.print("Is Room A clean? Say it with a True or False: ");
        boolean isroomAclean = scanner.nextBoolean();

        System.out.print("Is Room B clean? Say it with a True or False: ");
        boolean isroomBclean = scanner.nextBoolean();

        rState initialState = new rState(room, isroomAclean, isroomBclean);

        System.out.println("\nWhich strategy do you like to solve:");
        System.out.println("1. Breadth-First Search (BFS)");
        System.out.println("2. Depth-First Search (DFS)");
        System.out.println("3. A* Search (Informed - Bonus Merit)");
        System.out.println("4. Run All Strategies");
        System.out.println("Pick One of the Options Above (1-4):");
        System.out.print(" Your Choice: ");
        int numchoice = scanner.nextInt();


        switch (numchoice) {
            case 1 -> solveBFS(initialState);
            case 2 -> solveDFS(initialState);
            case 3 -> solveAStar(initialState);
            case 4 -> {
                solveBFS(initialState);
                solveDFS(initialState);
                solveAStar(initialState);
            }
            default -> {
                System.out.println("Choice chosen outside of the given options. BFS will be solved by default!");
                solveBFS(initialState);
            }
        }   }
    }
 

}