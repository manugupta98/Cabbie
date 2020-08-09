package com.server;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

class Edge{
    final private Node destination;
//    final private double dist;
    final private double time;

    /**
     * Creates a new edge
     * @param destination
     * @param dist
     */
    Edge(Node destination, double dist){
        this.destination = destination;
//        this.dist = dist;
        time = dist/10;
    }

    /**
     * @return time
     */
    double getTime(){
        return time;
    }

    /**
     * @return destination
     */
    Node getDestination(){
        return destination;
    }
}

class Node{
    final private int sector;
    final private int x;
    final private int y;
    final private char block;
    final ArrayList<Edge> edges = new ArrayList<>();

    /**
     * Creates a new node
     * @param sector
     * @param block
     * @param x
     * @param y
     */
    Node(int sector, char block, int x, int y){
        this.sector = sector;
        this.block = block;
        this.x = x;
        this.y = y;
        System.out.println("Node Created: " + sector + " " + block + " at (" + x + ", " + y + ")");
    }

    /**
     * @return sector
     */
    int getSector(){
        return sector;
    }

    /**
     * @return x
     */
    int getX(){
        return x;
    }

    /**
     * @return y
     */
    int getY(){
        return y;
    }

    /**
     * @return block
     */
    char getBlock(){
        return block;
    }

    /**
     * checks if two nodes are same
     * @param node
     * @return true if this.sector == node.sector && this.block == node.block, otherwise false
     * @overrides
     */
    public boolean equals(Node node) {
        return (this.sector == node.sector && this.block == node.block);
    }

}

public class Map{
    private static Map map = null;
    final ArrayList<Node> nodes = new ArrayList<Node>();

    private Map() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/uber?useSSL=false&allowPublicKeyRetrieval=true", "root", "sql@connect");
            Statement st = conn.createStatement();

            ResultSet nodesSet = st.executeQuery("SELECT * FROM nodes;");
            while (nodesSet.next()) {
                int sector = nodesSet.getInt("sector");
                char block = (nodesSet.getString("block")).charAt(0);
                int x = nodesSet.getInt("x");
                int y = nodesSet.getInt("y");
                nodes.add(new Node(sector, block, x, y));
            }

            ResultSet edgesSet = st.executeQuery("SELECT * FROM edges;");
            while (edgesSet.next()) {
                Node n1, n2;
                n1 = getNode(edgesSet.getInt("sector_1"), edgesSet.getString("block_1").charAt(0));
                n2 = getNode(edgesSet.getInt("sector_2"), edgesSet.getString("block_2").charAt(0));
                double dist = Math.sqrt(Math.pow(n1.getX() - n2.getX(), 2) + Math.pow(n1.getY() - n2.getY(), 2));
                n1.edges.add(new Edge(n2, dist));
                n2.edges.add(new Edge(n1, dist));
                System.out.println("Edge Created: " + n1.getSector() + " " + n1.getBlock() + " to " + n2.getSector() + " " + n2.getBlock() + " with dist: " + dist);
            }
            st.close();
            conn.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    static Map getMap(){
        if (map == null){
            map = new Map();
        }
        return map;
    }

//    Node getNode(int id){
//        for (Node node: nodes){
//            if (node.id == id){
//                return node;
//            }
//        }
//        return null;
//    }

    Node getNode(int sector, char block){
        for (Node node: nodes){
            if (node.getSector() == sector && node.getBlock() == block){
                return node;
            }
        }
        return null;
    }

    Node getClosestNode(int x, int y){
        int minDist = -1;
        Node closestNode = null;
        for (Node node: nodes){
            int deltaX = Math.abs(node.getX() - x);
            int deltaY = Math.abs(node.getY() - y);
            if (minDist == -1 || deltaX + deltaY < minDist){
                closestNode = node;
                minDist = deltaX + deltaY;
            }
        }
        return closestNode;
    }

    ArrayList<Node> getShortestPath(Node currentNode, Node destinationNode){
        ArrayList<Node> shortestPath;
        ArrayList<Node> visited = new ArrayList<Node>();
        visited.add(currentNode);
        HashMap<ArrayList<Node>,Double> bfsQueue = new HashMap<ArrayList<Node>, Double>();
        bfsQueue.put((ArrayList<Node>) visited.clone(), 0.0);
        boolean found = false;
        System.out.println(" ");
        if (currentNode.equals(destinationNode)){
            found = true;
            return (ArrayList<Node>) visited.clone();
        }

        while (!found){
            Entry<ArrayList<Node>, Double> min = null;
            for (Entry<ArrayList<Node>, Double> entry : bfsQueue.entrySet()) {
                if (min == null || min.getValue() > entry.getValue()) {
                    min = entry;
                }
            }
            shortestPath = min.getKey();
            bfsQueue.remove(shortestPath);
            Node lastNode = shortestPath.get(shortestPath.size()-1);
            for (Edge edge: lastNode.edges){
                if (!visited.contains(edge.getDestination())){
                    visited.add(edge.getDestination());
                    shortestPath.add(edge.getDestination());
//                    for (Node node:shortestPath){
//                        System.out.print(node.sector + ":" + node.block + " ");
//                    }
//                    System.out.println(" ");
                    if (edge.getDestination().equals(destinationNode)){
                        found = true;
                        return shortestPath;
                    }
                    bfsQueue.put((ArrayList<Node>) shortestPath.clone(), min.getValue() + map.getTimeToAdjacentNode(lastNode, edge.getDestination()));
                    shortestPath.remove(edge.getDestination());
                }
            }
        }

        return null;
    }

    double getTimeToAdjacentNode(Node currentNode, Node destinationNode){
        for (Edge edge: currentNode.edges){
            if (destinationNode == edge.getDestination()){
                return edge.getTime();
            }
        }
        return -1;
    }

    double getTimeOfPath(ArrayList<Node> path){
        int size = path.size() - 1;
        double time = 0;
        for (int i = 0; i < size; i++){
            time += getTimeToAdjacentNode(path.get(i), path.get(i + 1));
        }
        return time;
//        return getTimeToAdjacentNode(path.get(0), path.get(1)) + getTimeOfPath((ArrayList<Node>) path.subList(1,path.size()-1));
    }

}

/*

CREATE TABLE driver();

CREATE TABLE accounts(uid varchar(100) Primary Key, password varchar(100) , user_name varchar(100), phone_number varchar(10), email_id varchar(100));

CREATE TABLE wallets(uid varchar(100) Primary Key, balance int);

*/


/*

CREATE TABLE nodes(sector int, block varchar(1), x int, y int);


INSERT INTO nodes VALUES(1, 'A', 245, 1018);
INSERT INTO nodes VALUES(1, 'B', 321, 1017);
INSERT INTO nodes VALUES(1, 'C', 321, 1049);
INSERT INTO nodes VALUES(1, 'D', 295, 1049);
INSERT INTO nodes VALUES(1, 'E', 277, 1049);
INSERT INTO nodes VALUES(1, 'F', 245, 1049);
INSERT INTO nodes VALUES(1, 'G', 245, 1143);
INSERT INTO nodes VALUES(1, 'H', 268, 1143);
INSERT INTO nodes VALUES(1, 'I', 268, 1116);
INSERT INTO nodes VALUES(1, 'J', 302, 1130);
INSERT INTO nodes VALUES(1, 'K', 304, 1143);
INSERT INTO nodes VALUES(1, 'L', 321, 1143);
INSERT INTO nodes VALUES(1, 'M', 321, 1119);
INSERT INTO nodes VALUES(1, 'N', 383, 1119);
INSERT INTO nodes VALUES(1, 'O', 321, 1095);
INSERT INTO nodes VALUES(1, 'P', 295, 1095);
INSERT INTO nodes VALUES(1, 'Q', 277, 1095);
INSERT INTO nodes VALUES(1, 'R', 321, 1071);
INSERT INTO nodes VALUES(1, 'S', 349, 1071);
INSERT INTO nodes VALUES(1, 'T', 349, 1048);
INSERT INTO nodes VALUES(1, 'U', 393, 1048);
INSERT INTO nodes VALUES(1, 'V', 393, 1071);
INSERT INTO nodes VALUES(1, 'W', 350, 1144);
INSERT INTO nodes VALUES(1, 'X', 370, 1144);


INSERT INTO nodes VALUES(2, 'A', 612, 1020);
INSERT INTO nodes VALUES(2, 'B', 613, 1054);
INSERT INTO nodes VALUES(2, 'C', 582, 1054);
INSERT INTO nodes VALUES(2, 'D', 562, 1054);
INSERT INTO nodes VALUES(2, 'E', 582, 1070);
INSERT INTO nodes VALUES(2, 'F', 564, 1070);
INSERT INTO nodes VALUES(2, 'G', 613, 1098);
INSERT INTO nodes VALUES(2, 'H', 519, 1097);
INSERT INTO nodes VALUES(2, 'I', 520, 1071);
INSERT INTO nodes VALUES(2, 'J', 496, 1071);
INSERT INTO nodes VALUES(2, 'K', 442, 1071);
INSERT INTO nodes VALUES(2, 'L', 442, 1049);
INSERT INTO nodes VALUES(2, 'M', 496, 1050);
INSERT INTO nodes VALUES(2, 'N', 521, 1060);
INSERT INTO nodes VALUES(2, 'O', 546, 1059);
INSERT INTO nodes VALUES(2, 'P', 545, 1022);
INSERT INTO nodes VALUES(2, 'Q', 522, 1022);
INSERT INTO nodes VALUES(2, 'R', 417, 1022);
INSERT INTO nodes VALUES(2, 'S', 417, 1049);
INSERT INTO nodes VALUES(2, 'T', 417, 1095);
INSERT INTO nodes VALUES(2, 'U', 466, 1096);
INSERT INTO nodes VALUES(2, 'V', 466, 1146);
INSERT INTO nodes VALUES(2, 'W', 429, 1145);
INSERT INTO nodes VALUES(2, 'X', 585, 1147);
INSERT INTO nodes VALUES(2, 'Y', 612, 1147);


INSERT INTO nodes VALUES(3, 'A', 612, 1198);
INSERT INTO nodes VALUES(3, 'B', 612, 1243);
INSERT INTO nodes VALUES(3, 'C', 581, 1242);
INSERT INTO nodes VALUES(3, 'D', 581, 1229);
INSERT INTO nodes VALUES(3, 'E', 581, 1198);
INSERT INTO nodes VALUES(3, 'F', 580, 1185);
INSERT INTO nodes VALUES(3, 'G', 522, 1185);
INSERT INTO nodes VALUES(3, 'H', 521, 1198);
INSERT INTO nodes VALUES(3, 'I', 509, 1198);
INSERT INTO nodes VALUES(3, 'J', 498, 1197);
INSERT INTO nodes VALUES(3, 'K', 497, 1185);
INSERT INTO nodes VALUES(3, 'L', 428, 1183);
INSERT INTO nodes VALUES(3, 'M', 427, 1198);
INSERT INTO nodes VALUES(3, 'N', 426, 1233);
INSERT INTO nodes VALUES(3, 'O', 509, 1233);
INSERT INTO nodes VALUES(3, 'P', 509, 1214);
INSERT INTO nodes VALUES(3, 'Q', 543, 1214);
INSERT INTO nodes VALUES(3, 'R', 509, 1243);
INSERT INTO nodes VALUES(3, 'S', 427, 1242);
INSERT INTO nodes VALUES(3, 'T', 370, 1185);
INSERT INTO nodes VALUES(3, 'U', 349, 1185);
INSERT INTO nodes VALUES(3, 'V', 369, 1200);
INSERT INTO nodes VALUES(3, 'W', 344, 1201);
INSERT INTO nodes VALUES(3, 'X', 344, 1243);
INSERT INTO nodes VALUES(3, 'Y', 370, 1242);
INSERT INTO nodes VALUES(3, 'Z', 585, 1175);


INSERT INTO nodes VALUES(4, 'A', 245, 1170);
INSERT INTO nodes VALUES(4, 'B', 245, 1183);
INSERT INTO nodes VALUES(4, 'C', 245, 1198);
INSERT INTO nodes VALUES(4, 'D', 304, 1170);
INSERT INTO nodes VALUES(4, 'E', 304, 1198);
INSERT INTO nodes VALUES(4, 'F', 321, 1200);
INSERT INTO nodes VALUES(4, 'G', 321, 1185);
INSERT INTO nodes VALUES(4, 'H', 321, 1213);
INSERT INTO nodes VALUES(4, 'I', 292, 1213);
INSERT INTO nodes VALUES(4, 'J', 279, 1213);
INSERT INTO nodes VALUES(4, 'K', 278, 1198);
INSERT INTO nodes VALUES(4, 'L', 277, 1183);
INSERT INTO nodes VALUES(4, 'M', 245, 1213);
INSERT INTO nodes VALUES(4, 'N', 245, 1243);
INSERT INTO nodes VALUES(4, 'O', 276, 1244);
INSERT INTO nodes VALUES(4, 'P', 292, 1244);
INSERT INTO nodes VALUES(4, 'Q', 292, 1269);
INSERT INTO nodes VALUES(4, 'R', 277, 1268);
INSERT INTO nodes VALUES(4, 'S', 278, 1295);
INSERT INTO nodes VALUES(4, 'T', 245, 1295);
INSERT INTO nodes VALUES(4, 'U', 320, 1295);
INSERT INTO nodes VALUES(4, 'V', 321, 1269);
INSERT INTO nodes VALUES(4, 'W', 321, 1242);

 */


/*

CREATE TABLE edges(sector_1 int, block_1 varchar(1), sector_2 int, block_2 varchar(1));


1-1

INSERT INTO edges VALUES(1, 'A', 1, 'B');
INSERT INTO edges VALUES(1, 'A', 1, 'F');
INSERT INTO edges VALUES(1, 'B', 1, 'C');
INSERT INTO edges VALUES(1, 'C', 1, 'D');
INSERT INTO edges VALUES(1, 'C', 1, 'R');
INSERT INTO edges VALUES(1, 'D', 1, 'E');
INSERT INTO edges VALUES(1, 'D', 1, 'P');
INSERT INTO edges VALUES(1, 'E', 1, 'F');
INSERT INTO edges VALUES(1, 'E', 1, 'Q');
INSERT INTO edges VALUES(1, 'F', 1, 'G');
INSERT INTO edges VALUES(1, 'G', 1, 'H');
INSERT INTO edges VALUES(1, 'H', 1, 'I');
INSERT INTO edges VALUES(1, 'H', 1, 'K');
INSERT INTO edges VALUES(1, 'J', 1, 'K');
INSERT INTO edges VALUES(1, 'K', 1, 'L');
INSERT INTO edges VALUES(1, 'L', 1, 'M');
INSERT INTO edges VALUES(1, 'L', 1, 'W');
INSERT INTO edges VALUES(1, 'M', 1, 'O');
INSERT INTO edges VALUES(1, 'M', 1, 'N');
INSERT INTO edges VALUES(1, 'O', 1, 'P');
INSERT INTO edges VALUES(1, 'O', 1, 'R');
INSERT INTO edges VALUES(1, 'P', 1, 'Q');
INSERT INTO edges VALUES(1, 'R', 1, 'S');
INSERT INTO edges VALUES(1, 'S', 1, 'T');
INSERT INTO edges VALUES(1, 'S', 1, 'V');
INSERT INTO edges VALUES(1, 'T', 1, 'T');
INSERT INTO edges VALUES(1, 'U', 1, 'V');
INSERT INTO edges VALUES(1, 'W', 1, 'X');


2-2


INSERT INTO edges VALUES(2, 'A', 2, 'B');
INSERT INTO edges VALUES(2, 'A', 2, 'P');
INSERT INTO edges VALUES(2, 'B', 2, 'C');
INSERT INTO edges VALUES(2, 'B', 2, 'G');
INSERT INTO edges VALUES(2, 'C', 2, 'D');
INSERT INTO edges VALUES(2, 'C', 2, 'E');
INSERT INTO edges VALUES(2, 'E', 2, 'F');
INSERT INTO edges VALUES(2, 'G', 2, 'H');
INSERT INTO edges VALUES(2, 'G', 2, 'Y');
INSERT INTO edges VALUES(2, 'H', 2, 'I');
INSERT INTO edges VALUES(2, 'H', 2, 'U');
INSERT INTO edges VALUES(2, 'I', 2, 'J');
INSERT INTO edges VALUES(2, 'I', 2, 'N');
INSERT INTO edges VALUES(2, 'J', 2, 'K');
INSERT INTO edges VALUES(2, 'J', 2, 'M');
INSERT INTO edges VALUES(2, 'K', 2, 'L');
INSERT INTO edges VALUES(2, 'L', 2, 'M');
INSERT INTO edges VALUES(2, 'L', 2, 'S');
INSERT INTO edges VALUES(2, 'N', 2, 'O');
INSERT INTO edges VALUES(2, 'N', 2, 'Q');
INSERT INTO edges VALUES(2, 'O', 2, 'P');
INSERT INTO edges VALUES(2, 'P', 2, 'Q');
INSERT INTO edges VALUES(2, 'Q', 2, 'R');
INSERT INTO edges VALUES(2, 'R', 2, 'S');
INSERT INTO edges VALUES(2, 'S', 2, 'T');
INSERT INTO edges VALUES(2, 'T', 2, 'U');
INSERT INTO edges VALUES(2, 'U', 2, 'V');
INSERT INTO edges VALUES(2, 'V', 2, 'W');
INSERT INTO edges VALUES(2, 'V', 2, 'X');
INSERT INTO edges VALUES(2, 'X', 2, 'Y');

1 - 2

INSERT INTO edges VALUES(1, 'B', 2, 'R');
INSERT INTO edges VALUES(1, 'U', 2, 'S');
INSERT INTO edges VALUES(1, 'O', 2, 'T');
INSERT INTO edges VALUES(1, 'X', 2, 'W');

4 - 4

INSERT INTO edges VALUES(4, 'A', 4, 'B');
INSERT INTO edges VALUES(4, 'A', 4, 'D');
INSERT INTO edges VALUES(4, 'B', 4, 'C');
INSERT INTO edges VALUES(4, 'B', 4, 'L');
INSERT INTO edges VALUES(4, 'C', 4, 'K');
INSERT INTO edges VALUES(4, 'C', 4, 'M');
INSERT INTO edges VALUES(4, 'D', 4, 'E');
INSERT INTO edges VALUES(4, 'E', 4, 'F');
INSERT INTO edges VALUES(4, 'E', 4, 'K');
INSERT INTO edges VALUES(4, 'F', 4, 'G');
INSERT INTO edges VALUES(4, 'F', 4, 'H');
INSERT INTO edges VALUES(4, 'H', 4, 'I');
INSERT INTO edges VALUES(4, 'H', 4, 'W');
INSERT INTO edges VALUES(4, 'I', 4, 'M');
INSERT INTO edges VALUES(4, 'I', 4, 'P');
INSERT INTO edges VALUES(4, 'J', 4, 'K');
INSERT INTO edges VALUES(4, 'J', 4, 'M');
INSERT INTO edges VALUES(4, 'K', 4, 'L');
INSERT INTO edges VALUES(4, 'M', 4, 'N');
INSERT INTO edges VALUES(4, 'N', 4, 'O');
INSERT INTO edges VALUES(4, 'N', 4, 'T');
INSERT INTO edges VALUES(4, 'O', 4, 'P');
INSERT INTO edges VALUES(4, 'O', 4, 'R');
INSERT INTO edges VALUES(4, 'P', 4, 'Q');
INSERT INTO edges VALUES(4, 'Q', 4, 'R');
INSERT INTO edges VALUES(4, 'Q', 4, 'V');
INSERT INTO edges VALUES(4, 'R', 4, 'S');
INSERT INTO edges VALUES(4, 'S', 4, 'T');
INSERT INTO edges VALUES(4, 'S', 4, 'U');
INSERT INTO edges VALUES(4, 'U', 4, 'V');
INSERT INTO edges VALUES(4, 'V', 4, 'W');

3 - 3


INSERT INTO edges VALUES(3, 'A', 3, 'B');
INSERT INTO edges VALUES(3, 'A', 3, 'E');
INSERT INTO edges VALUES(3, 'B', 3, 'C');
INSERT INTO edges VALUES(3, 'C', 3, 'D');
INSERT INTO edges VALUES(3, 'C', 3, 'R');
INSERT INTO edges VALUES(3, 'D', 3, 'E');
INSERT INTO edges VALUES(3, 'D', 3, 'O');
INSERT INTO edges VALUES(3, 'E', 3, 'F');
INSERT INTO edges VALUES(3, 'E', 3, 'H');
INSERT INTO edges VALUES(3, 'F', 3, 'G');
INSERT INTO edges VALUES(3, 'G', 3, 'H');
INSERT INTO edges VALUES(3, 'H', 3, 'I');
INSERT INTO edges VALUES(3, 'I', 3, 'J');
INSERT INTO edges VALUES(3, 'I', 3, 'P');
INSERT INTO edges VALUES(3, 'J', 3, 'K');
INSERT INTO edges VALUES(3, 'J', 3, 'M');
INSERT INTO edges VALUES(3, 'K', 3, 'L');
INSERT INTO edges VALUES(3, 'L', 3, 'M');
INSERT INTO edges VALUES(3, 'M', 3, 'N');
INSERT INTO edges VALUES(3, 'N', 3, 'O');
INSERT INTO edges VALUES(3, 'N', 3, 'S');
INSERT INTO edges VALUES(3, 'O', 3, 'P');
INSERT INTO edges VALUES(3, 'O', 3, 'R');
INSERT INTO edges VALUES(3, 'P', 3, 'Q');
INSERT INTO edges VALUES(3, 'R', 3, 'S');
INSERT INTO edges VALUES(3, 'S', 3, 'Y');
INSERT INTO edges VALUES(3, 'T', 3, 'U');
INSERT INTO edges VALUES(3, 'T', 3, 'V');
INSERT INTO edges VALUES(3, 'V', 3, 'W');
INSERT INTO edges VALUES(3, 'V', 3, 'Y');
INSERT INTO edges VALUES(3, 'W', 3, 'X');
INSERT INTO edges VALUES(3, 'X', 3, 'Y');


2 - 3


INSERT INTO edges VALUES(2, 'W', 3, 'L');
INSERT INTO edges VALUES(2, 'X', 3, 'Z');
INSERT INTO edges VALUES(2, 'Y', 3, 'A');

3 - 4


INSERT INTO edges VALUES(3, 'U', 4, 'G');
INSERT INTO edges VALUES(3, 'W', 4, 'F');
INSERT INTO edges VALUES(3, 'X', 4, 'W');

1 - 3


INSERT INTO edges VALUES(1, 'W', 3, 'U');
INSERT INTO edges VALUES(1, 'X', 3, 'T');


1 - 4


INSERT INTO edges VALUES(1, 'G', 4, 'A');
INSERT INTO edges VALUES(1, 'K', 4, 'D');
INSERT INTO edges VALUES(1, 'L', 4, 'G');


 */