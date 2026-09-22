import java.util.*;

/**
 * Vacuum World Search Strategy Solver
 * Implementation of BFS, DFS, and A* Search.
 */
public class vacuum {
   
     // STEP 1: Defining the State of the Room
public static class rState {
    String room;    // "Room A" or "Room B"
    boolean isroomAclean; // true means the room is Clean, false is considered Dirty
    boolean isroomBclean; // true means the room is Clean, false is considered Dirty

    public rState(String room, boolean isroomAclean, boolean isroomBclean) {
        this.room = room;
        this.isroomAclean = isroomAclean;
        this.isroomBclean = isroomBclean;
    }

    public boolean isGoal() {
        return isroomAclean && isroomBclean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof rState)) return false;
        rState state = (rState) o;
         return isroomAclean == state.isroomAclean &&
             isroomBclean == state.isroomBclean &&
               Objects.equals(room, state.room);
    }

    @Override
    public int hashCode() {
        return Objects.hash(room, isroomAclean, isroomBclean);
    }

    @Override
    public String toString() {
        return String.format("[The Vacuum in Rooms %s | Room A: %s | Room B: %s]",
                room, isroomAclean ? "Clean" : "Dirty", isroomBclean ? "Clean" : "Dirty");
    }
}

// STEP 2: Defining the Search Node and Action Generator
    public static class Node {
        rState state;   // Current state
        Node parent;    // Parent node to trace back the path
        String action;  // Action taken ("Suck", "Move Left", "Move Right", or "Start")
        int cost;       // Total step cost from the start

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
        rState curr = node.state;

        // Action 1: Suck The Dust Off (only valid if the current room is dirty)
        if ((curr.room.equalsIgnoreCase("Room A") && !curr.isroomAclean) || 
            (curr.room.equalsIgnoreCase("Room B") && !curr.isroomBclean)) {
            
            boolean nextA = curr.room.equalsIgnoreCase("Room A") ? true : curr.isroomAclean;
            boolean nextB = curr.room.equalsIgnoreCase("Room B") ? true : curr.isroomBclean;
            
            rState nextState = new rState(curr.room, nextA, nextB);
            successors.add(new Node(nextState, node, "Suck the Dust", node.cost + 1));
        }

        // Action 2: Move to the Left (only valid if currently in Room B)
        if (curr.room.equalsIgnoreCase("Room B")) {
            rState nextState = new rState("Room A", curr.isroomAclean, curr.isroomBclean);
            successors.add(new Node(nextState, node, "Move to the Left", node.cost + 1));
        }

        // Action 3: Move to the Right (only valid if currently in Room A)
        if (curr.room.equalsIgnoreCase("Room A")) {
            rState nextState = new rState("Room B", curr.isroomAclean, curr.isroomBclean);
            successors.add(new Node(nextState, node, "Move to the Right", node.cost + 1));
        }

        return successors;
    }

    // -------------------------------------------------------------
    // STEP 3: BREADTH-FIRST SEARCH (BFS)
    // -------------------------------------------------------------
    public static void solveBFS(rState initialState) {
        System.out.println("\n=== Executing Breadth-First Search (BFS) ===");
        
        Queue<Node> frontier = new LinkedList<>();
        Set<rState> visited = new HashSet<>();

        Node root = new Node(initialState, null, "Start", 0);
        frontier.add(root);
        visited.add(initialState);

        int nodesExpanded = 0;

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
        System.out.println("No solution found.");
    }

    // -------------------------------------------------------------
    // STEP 3: Solving the Problem Using Depth-First Search (DFS)
    // -------------------------------------------------------------
    public static void solveDFS(rState initialState) {
        System.out.println("\n=== Executing Depth-First Search (DFS) ===");
        
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
    // HEURISTIC FUNCTION FOR A* SEARCH (Estimates remaining steps)
    // -------------------------------------------------------------
    private static int calculateHeuristic(rState state) {
        int dirtyCount = 0;
        if (!state.isroomAclean) dirtyCount++;
        if (!state.isroomBclean) dirtyCount++;

        int distance = 0;
        // If 1 room is dirty and vacuum is in the clean room, add 1 step to travel
        if (dirtyCount == 1) {
            if (!state.isroomAclean && state.room.equalsIgnoreCase("Room B")) distance = 1;
            if (!state.isroomBclean && state.room.equalsIgnoreCase("Room A")) distance = 1;
        }

        return dirtyCount + distance;
    }

    // -------------------------------------------------------------
    // STEP 3: A* SEARCH (Informed Strategy - Bonus Merit)
    // -------------------------------------------------------------
    public static void solveAStar(rState initialState) {
        System.out.println("\n=== Executing A* Search (Informed Search) ===");

        // Priority Queue sorts nodes by total estimated cost f(n) = g(n) + h(n)
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
    // STEP 4: Printing the Narrative (Trace Path & Output Narrative)
    // -------------------------------------------------------------
    private static void printNarrative(Node goalNode, int nodesExpanded) {
        List<Node> path = new ArrayList<>();
        Node curr = goalNode;

        // Traces backward from goal node to start node using 'parent'
        while (curr != null) {
            path.add(curr);
            curr = curr.parent;
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
    // INTERACTIVE USER INTERFACE
    // -------------------------------------------------------------
    public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
        System.out.println("==========================================");
        System.out.println("   VACUUM WORLD SEARCH STRATEGY SOLVER   ");
        System.out.println("==========================================");

        System.out.print("Enter initial location of Vacuum (A/B): ");
        String roomChoice = scanner.next().trim().toUpperCase();
        while (!roomChoice.equals("A") && !roomChoice.equals("B")) {
            System.out.print("Invalid location. Enter A or B: ");
            roomChoice = scanner.next().trim().toUpperCase();
        }
        String room = roomChoice.equals("A") ? "Room A" : "Room B";

        System.out.print("Is Room A clean? (true/false): ");
        boolean isroomAclean = scanner.nextBoolean();

        System.out.print("Is Room B clean? (true/false): ");
        boolean isroomBclean = scanner.nextBoolean();

        rState initialState = new rState(room, isroomAclean, isroomBclean);

        System.out.println("\nSelect Search Strategy:");
        System.out.println("1. Breadth-First Search (BFS)");
        System.out.println("2. Depth-First Search (DFS)");
        System.out.println("3. A* Search (Informed - Bonus Merit)");
        System.out.println("4. Run All Strategies");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1 -> solveBFS(initialState);
            case 2 -> solveDFS(initialState);
            case 3 -> solveAStar(initialState);
            case 4 -> {
                solveBFS(initialState);
                solveDFS(initialState);
                solveAStar(initialState);
            }
            default -> {
                System.out.println("Invalid choice. Running BFS by default.");
                solveBFS(initialState);
            }
        }   }
    }
 

}