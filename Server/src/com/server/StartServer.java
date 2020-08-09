package com.server;

class StartServer{
    public static void main(String[] args){
        Map map = Map.getMap();

        new ClientsAdderThread(8001);
        TripControl tripControl = TripControl.getTripControl();
//        ArrayList<Node> path = map.getShortestPath(map.getNode(2,'H'), map.getNode(4, 'V'));
//        for (Node node:path){
//            System.out.print(node.sector + ":" + node.block + " ");
//        }
//        System.out.println(" ");
//        try {
//            Thread.sleep(10000);
//        }
//        catch (Exception e){
//            ;
//        }
//        Driver driver =  tripControl.getNearestDriver(map.getNode(2, 'H'));
//        System.out.println(driver.name + " : " + driver.getX() + " " + driver.getY());



        System.out.println("Serer Initialized");

//        com.main.LoginPage.main(new String[0]);
    }
}