import java.util.*;

/**
 * Vacuum World Search Strategy Solver
 * Implementation of BFS, DFS, and A* Search.
 */
public class vacuum {

    // Standard AI State Representation
    static class State {
        String agentLocation; // "A" or "B"
        boolean isAClean;     // true = Clean, false = Dirty
        boolean isBClean;     // true = Clean, false = Dirty

        public State(String agentLocation, boolean isAClean, boolean isBClean) {
            this.agentLocation = agentLocation;
            this.isAClean = isAClean;
            this.isBClean = isBClean;
        }

        public boolean isGoal() {
            return isAClean && isBClean;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof State)) return false;
            State state = (State) o;
            return isAClean == state.isAClean &&
                   isBClean == state.isBClean &&
                   Objects.equals(agentLocation, state.agentLocation);
        }

        @Override
        public int hashCode() {
            return Objects.hash(agentLocation, isAClean, isBClean);
        }

        @Override
        public String toString() {
            return String.format("[Agent: %s | Room A: %s | Room B: %s]",
                    agentLocation, isAClean ? "Clean" : "Dirty", isBClean ? "Clean" : "Dirty");
        }
    }

    // Node in Search Tree
    static class Node implements Comparable<Node> {
        State state;
        Node parent;
        String action;
        int gCost;
        int hCost;

        public Node(State state, Node parent, String action, int gCost, int hCost) {
            this.state = state;
            this.parent = parent;
            this.action = action;
            this.gCost = gCost;
            this.hCost = hCost;
        }

        public int getFCost() {
            return gCost + hCost;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.getFCost(), other.getFCost());
        }
    }

    // Successor Generation
    private static List<Node> getSuccessors(Node node, boolean isAStar) {
        List<Node> successors = new ArrayList<>();
        State curr = node.state;

        // Action 1: Suck
        if ((curr.agentLocation.equals("A") && !curr.isAClean) || 
            (curr.agentLocation.equals("B") && !curr.isBClean)) {
            
            boolean nextA = curr.agentLocation.equals("A") ? true : curr.isAClean;
            boolean nextB = curr.agentLocation.equals("B") ? true : curr.isBClean;
            State nextState = new State(curr.agentLocation, nextA, nextB);
            int h = isAStar ? calculateHeuristic(nextState) : 0;
            successors.add(new Node(nextState, node, "Suck", node.gCost + 1, h));
        }

        // Action 2: Move Left
        if (curr.agentLocation.equals("B")) {
            State nextState = new State("A", curr.isAClean, curr.isBClean);
            int h = isAStar ? calculateHeuristic(nextState) : 0;
            successors.add(new Node(nextState, node, "Move Left", node.gCost + 1, h));
        }

        // Action 3: Move Right
        if (curr.agentLocation.equals("A")) {
            State nextState = new State("B", curr.isAClean, curr.isBClean);
            int h = isAStar ? calculateHeuristic(nextState) : 0;
            successors.add(new Node(nextState, node, "Move Right", node.gCost + 1, h));
        }

        return successors;
    }

    private static int calculateHeuristic(State state) {
        int dirtyCount = 0;
        if (!state.isAClean) dirtyCount++;
        if (!state.isBClean) dirtyCount++;

        int distance = 0;
        if (dirtyCount == 1) {
            if (!state.isAClean && state.agentLocation.equals("B")) distance = 1;
            if (!state.isBClean && state.agentLocation.equals("A")) distance = 1;
        }

        return dirtyCount + distance;
    }

    // Breadth-First Search (BFS)
    public static void solveBFS(State initialState) {
        System.out.println("\n==========================================");
        System.out.println("   BREADTH-FIRST SEARCH (BFS) NARRATIVE   ");
        System.out.println("==========================================");
        
        Queue<Node> frontier = new LinkedList<>();
        Set<State> visited = new HashSet<>();

        Node root = new Node(initialState, null, "Start", 0, 0);
        frontier.add(root);
        visited.add(initialState);

        int nodesExpanded = 0;

        while (!frontier.isEmpty()) {
            Node current = frontier.poll();
            nodesExpanded++;

            if (current.state.isGoal()) {
                printNarrative(current, nodesExpanded);
                return;
            }

            for (Node child : getSuccessors(current, false)) {
                if (!visited.contains(child.state)) {
                    visited.add(child.state);
                    frontier.add(child);
                }
            }
        }
        System.out.println("No solution found.");
    }

    // Depth-First Search (DFS)
    public static void solveDFS(State initialState) {
        System.out.println("\n==========================================");
        System.out.println("    DEPTH-FIRST SEARCH (DFS) NARRATIVE   ");
        System.out.println("==========================================");
        
        Stack<Node> frontier = new Stack<>();
        Set<State> visited = new HashSet<>();

        Node root = new Node(initialState, null, "Start", 0, 0);
        frontier.push(root);

        int nodesExpanded = 0;

        while (!frontier.isEmpty()) {
            Node current = frontier.pop();

            if (!visited.contains(current.state)) {
                visited.add(current.state);
                nodesExpanded++;

                if (current.state.isGoal()) {
                    printNarrative(current, nodesExpanded);
                    return;
                }

                for (Node child : getSuccessors(current, false)) {
                    if (!visited.contains(child.state)) {
                        frontier.push(child);
                    }
                }
            }
        }
        System.out.println("No solution found.");
    }

    // A* Search (Informed Strategy)
    public static void solveAStar(State initialState) {
        System.out.println("\n==========================================");
        System.out.println("     A* SEARCH (INFORMED) NARRATIVE      ");
        System.out.println("==========================================");
        
        PriorityQueue<Node> frontier = new PriorityQueue<>();
        Set<State> visited = new HashSet<>();

        Node root = new Node(initialState, null, "Start", 0, calculateHeuristic(initialState));
        frontier.add(root);

        int nodesExpanded = 0;

        while (!frontier.isEmpty()) {
            Node current = frontier.poll();

            if (visited.contains(current.state)) continue;
            visited.add(current.state);
            nodesExpanded++;

            if (current.state.isGoal()) {
                printNarrative(current, nodesExpanded);
                return;
            }

            for (Node child : getSuccessors(current, true)) {
                if (!visited.contains(child.state)) {
                    frontier.add(child);
                }
            }
        }
        System.out.println("No solution found.");
    }

    // Print Narrative Trace
    private static void printNarrative(Node goalNode, int nodesExpanded) {
        List<Node> path = new ArrayList<>();
        Node curr = goalNode;
        while (curr != null) {
            path.add(curr);
            curr = curr.parent;
        }
        Collections.reverse(path);

        System.out.println("\n--- Solution Trace Found ---");
        for (int i = 0; i < path.size(); i++) {
            Node node = path.get(i);
            if (i == 0) {
                System.out.printf("Step %d: Initial State -> %s\n", i, node.state);
            } else {
                System.out.printf("Step %d: Action [%s] -> %s\n", i, node.action, node.state);
            }
        }
        System.out.println("------------------------------------------");
        System.out.printf("Total Path Cost (Actions Taken): %d\n", goalNode.gCost);
        System.out.printf("Total States Expanded: %d\n", nodesExpanded);
    }

    // Interactive Console
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==========================================");
        System.out.println("   VACUUM WORLD SEARCH STRATEGY SOLVER   ");
        System.out.println("==========================================");

        System.out.print("Enter initial location of Vacuum Cleaner (A/B): ");
        String location = scanner.next().trim().toUpperCase();
        while (!location.equals("A") && !location.equals("B")) {
            System.out.print("Invalid location. Enter A or B: ");
            location = scanner.next().trim().toUpperCase();
        }

        System.out.print("Is Room A clean? (true/false): ");
        boolean isAClean = scanner.nextBoolean();

        System.out.print("Is Room B clean? (true/false): ");
        boolean isBClean = scanner.nextBoolean();

        State initialState = new State(location, isAClean, isBClean);

        System.out.println("\nSelect Search Strategy:");
        System.out.println("1. Breadth-First Search (BFS)");
        System.out.println("2. Depth-First Search (DFS)");
        System.out.println("3. A* Search (Informed - Merit)");
        System.out.println("4. Run All Strategies (Comparative View)");
        System.out.print("Choice (1-4): ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                solveBFS(initialState);
                break;
            case 2:
                solveDFS(initialState);
                break;
            case 3:
                solveAStar(initialState);
                break;
            case 4:
                solveBFS(initialState);
                solveDFS(initialState);
                solveAStar(initialState);
                break;
            default:
                System.out.println("Invalid selection. Executing all strategies.");
                solveBFS(initialState);
                solveDFS(initialState);
                solveAStar(initialState);
                break;
        }

        scanner.close();
    }
}