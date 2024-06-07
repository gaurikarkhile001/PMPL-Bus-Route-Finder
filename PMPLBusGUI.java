import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class PMPLBusGUI extends JFrame implements ActionListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private JComboBox<String> sourceComboBox;
    private JComboBox<String> destinationComboBox;
    private JButton findRouteButton;
    private JButton displayStationsButton;
    private JButton displayMapButton;
    private JTextArea routeTextArea;

    private Map<String, Map<String, Set<Integer>>> graph; // Updated to store route numbers



    public PMPLBusGUI() {
        setTitle("PMPL Bus Route Finder");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize GUI components
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        sourceComboBox = new JComboBox<>();
        destinationComboBox = new JComboBox<>();
        findRouteButton = new JButton("Find Route");
        displayStationsButton = new JButton("Display Stations");
        displayMapButton = new JButton("Map"); // New button for displaying the map
        routeTextArea = new JTextArea(20, 40);
        routeTextArea.setEditable(false);

        // Add GUI components to the top panel
        topPanel.add(new JLabel("Source:"));
        topPanel.add(sourceComboBox);
        topPanel.add(new JLabel("Destination:"));
        topPanel.add(destinationComboBox);
        topPanel.add(findRouteButton);
        topPanel.add(displayStationsButton);
        topPanel.add(displayMapButton); // Add the map button

        // Add ActionListeners
        findRouteButton.addActionListener(this);
        displayStationsButton.addActionListener(this);
        displayMapButton.addActionListener(this); // Add ActionListener for the map button

        // Add components to the frame
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(routeTextArea), BorderLayout.CENTER);

        // Initialize graph
        initializeGraph();

        // Populate source and destination comboboxes
        populateComboBoxes();

        setVisible(true);
    }





    private void initializeGraph() {
        graph = new HashMap<>();

        // Adding places in Pune with route numbers
        addVertexWithRoutes("Katraj", "1,2,3,4,5,6,15");
        addVertexWithRoutes("Swargate", "1,2,4,6,7,8,9,10,11,13");
        addVertexWithRoutes("Shivajinager", "2,4,9,10,13");
        addVertexWithRoutes("Warje", "3,5");
        addVertexWithRoutes("Kothrud", "3,5,6,8");
        addVertexWithRoutes("Bhumkar Chowk", "3,5");
        addVertexWithRoutes("Dange Chowk", "3,5,13");
        addVertexWithRoutes("Chinchwad", "3,5,13");
        addVertexWithRoutes("SPPU", "4,14");
        addVertexWithRoutes("Khadki", "4,14");
        addVertexWithRoutes("Aundh", "4,14");
        addVertexWithRoutes("Nashik Phata", "4,14");
        addVertexWithRoutes("Bhosari", "4,14");
        addVertexWithRoutes("Nigdi", "5,13");
        addVertexWithRoutes("Pune Station", "7,12,14,15");
        addVertexWithRoutes("Upper Depo", "10,11,12,13");

        // Adding distances between places (undirected edges)
        addUndirectedEdge("Katraj", "Swargate", 6);
        addUndirectedEdge("Swargate", "Shivajinager", 5);
        addUndirectedEdge("Katraj", "Warje", 8);
        addUndirectedEdge("Warje", "Kothrud", 4);
        addUndirectedEdge("Kothrud", "Bhumkar Chowk", 17);
        addUndirectedEdge("Bhumkar Chowk", "Dange Chowk", 2);
        addUndirectedEdge("Dange Chowk", "Chinchwad", 7);
        addUndirectedEdge("Chinchwad", "Nigdi", 6);
        addUndirectedEdge("SPPU", "Aundh", 4);
        addUndirectedEdge("Aundh", "Khadki", 5);
        addUndirectedEdge("Khadki", "Nashik Phata", 6);
        addUndirectedEdge("Nashik Phata", "Bhosari", 5);
        addUndirectedEdge("Kothrud", "Swargate", 7);
        addUndirectedEdge("Pune Station", "Swargate", 6);
        addUndirectedEdge("Upper Depo", "Pune Station", 9);
        addUndirectedEdge("Upper Depo", "Swargate", 6);
        addUndirectedEdge("Shivajinager", "SPPU", 4);
        addUndirectedEdge("Pune Station", "Shivajinager", 5);
    }

    private void addVertexWithRoutes(String vertex, String routesStr) {
        graph.put(vertex, new HashMap<>());
        String[] routesArr = routesStr.split(",");
        Set<Integer> routes = new HashSet<>();
        for (String route : routesArr) {
            routes.add(Integer.parseInt(route));
        }
        graph.get(vertex).put(vertex, routes);
    }

    private void addUndirectedEdge(String stop1, String stop2, int weight) {
        if (!graph.containsKey(stop1)) {
            graph.put(stop1, new HashMap<>());
        }
        if (!graph.containsKey(stop2)) {
            graph.put(stop2, new HashMap<>());
        }
        graph.get(stop1).put(stop2, new HashSet<>(Collections.singletonList(weight)));
        graph.get(stop2).put(stop1, new HashSet<>(Collections.singletonList(weight)));
    }

    private void populateComboBoxes() {
        for (String node : graph.keySet()) {
            sourceComboBox.addItem(node);
            destinationComboBox.addItem(node);
        }
    }

    private Map<String, Integer> dijkstra(String source, Map<String, String> previous) {
        Map<String, Integer> distances = new HashMap<>();
        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }
        distances.put(source, 0);

        Set<String> visited = new HashSet<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        queue.add(source);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            visited.add(current);

            for (String neighbor : graph.get(current).keySet()) {
                if (!visited.contains(neighbor)) {
                    int distance = distances.get(current) + graph.get(current).get(neighbor).iterator().next(); // Add distance
                    if (distance < distances.get(neighbor)) {
                        distances.put(neighbor, distance);
                        previous.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
        }
        return distances;
    }

    private String findInterchangeRoute(String source, String destination, Map<String, String> previous) {
        int routeChanges = 0;
        String current = destination;
        String interchangeRoute = "";
        Set<String> interchangedStops = new HashSet<>();
        while (current != null && !current.equals(source)) {
            String prev = previous.get(current);
            if (prev != null && !interchangedStops.contains(prev) && !graph.get(prev).keySet().equals(graph.get(current).keySet())) {
                routeChanges++;
                interchangedStops.add(prev);
                if (!interchangeRoute.isEmpty()) {
                    interchangeRoute = findShortestPath(previous, current) + " -> " + interchangeRoute;
                } else {
                    interchangeRoute = findShortestPath(previous, current);
                }
            }
            current = prev;
        }
        return routeChanges > 0 ? String.valueOf(routeChanges) : null;
    }

    private int calculateFare(int distance) {
        if (distance <= 10) {
            return 10;
        } else if (distance <= 15) {
            return 15;
        } else if (distance <= 20) {
            return 20;
        } else if (distance <= 25) {
            return 25;
        } else if (distance <= 30) {
            return 30;
        } else {
            return 35;
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == findRouteButton) {
            // Existing code for finding routes
        } else if (e.getSource() == displayStationsButton) {
            // Existing code for displaying stations
        } else if (e.getSource() == displayMapButton) {
            // Load and display the map
            ImageIcon mapIcon = new ImageIcon("C:\\Users\\Gauri\\Downloads\\Shortestpath.drawio (1).png");

            // Adjust the width and height of the image
            int width = 1000; // Desired width
            int height = 600; // Desired height
            Image scaledImage = mapIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon scaledMapIcon = new ImageIcon(scaledImage);

            JLabel mapLabel = new JLabel(scaledMapIcon);
            JOptionPane.showMessageDialog(this, mapLabel, "Map", JOptionPane.PLAIN_MESSAGE);
        }
        if (e.getSource() == findRouteButton) {
            String source = (String) sourceComboBox.getSelectedItem();
            String destination = (String) destinationComboBox.getSelectedItem();

            if (source != null && destination != null) {
                Map<String, String> previous = new HashMap<>();
                Map<String, Integer> distances = dijkstra(source, previous);

                int distance = distances.get(destination);
                String shortestPath = "Shortest route from " + source + " to " + destination + ":\n";
                if (distance == Integer.MAX_VALUE) {
                    String interchange = findInterchangeRoute(source, destination, previous);
                    if (interchange != null) {
                        // Adjust total interchanges by subtracting 1
                        shortestPath += "Total Interchanges: " + (Integer.parseInt(interchange) - 1) + "\n";
                    } else {
                        shortestPath += "Not reachable";
                    }
                } else {
                    shortestPath += "Distance: " + distance + " KM\n";
                    // Calculate fare based on distance
                    int fare = calculateFare(distance);
                    shortestPath += "Fare: Rs " + fare + "\n";
                    String interchange = findInterchangeRoute(source, destination, previous);
                    if (interchange != null) {
                        // Adjust total interchanges by subtracting 1
                        shortestPath += "Total Interchanges: " + (Integer.parseInt(interchange) - 1) + "\n";
                    }
                    shortestPath += "Path: " + findShortestPath(previous, destination);
                }
                routeTextArea.setText(shortestPath);
            }
        } else if (e.getSource() == displayStationsButton) {
            StringBuilder stationsList = new StringBuilder("List of Bus Stops:\n");
            for (String station : graph.keySet()) {
                stationsList.append(station).append("\n");
            }
            JOptionPane.showMessageDialog(this, stationsList.toString(), "Bus Stops", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String findShortestPath(Map<String, String> previous, String destination) {
        StringBuilder pathBuilder = new StringBuilder();
        String current = destination;
        while (current != null) {
            pathBuilder.insert(0, current);
            current = previous.get(current);
            if (current != null) {
                pathBuilder.insert(0, " -> ");
            }
        }
        return pathBuilder.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PMPLBusGUI::new);
    }
}