import java.io.*;
import java.util.*;

public class Main {
    // ...
    private static int POPULATION_SIZE = 0;
    private static int GENERATIONS = 0;

    private static List<City> cities;

    public static void mutate(List<City> cities) {
        List<City> newTest = new ArrayList<>(cities);
        int index1 = new Random().nextInt(cities.size());
        int index2 = new Random().nextInt(cities.size());
        Collections.swap(newTest, index1, index2);

        if (index1 > index2) {
            int temp = index1;
            index1 = index2;
            index2 = temp;
        }

        while (index1 < index2) {
            Collections.swap(newTest, index1, index2);
            index1++;
            index2--;
        }

        double newTestTotalDistance = calculateTotalDistance(newTest);
        double citiesTotalDistance = calculateTotalDistance(cities);

        if (newTestTotalDistance < citiesTotalDistance) {
            cities.clear();
            cities.addAll(newTest);
            System.out.println(cities.size());
            System.out.println(
                    "New best route found with distance: " + newTestTotalDistance + " GENERATION: " + GENERATIONS);
        }
    }

    public static double calculateTotalDistance(List<City> route) {
        double totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += route.get(i).distanceTo(route.get(i + 1));
        }
        return totalDistance;
    }

    public static void solveTSP() {

        while (true) {

            mutate(cities);
            GENERATIONS++;
        }
    }

    public static List<City> loadCities(String filename) {
        List<City> cities = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            POPULATION_SIZE = Integer.parseInt(reader.readLine()) - 1;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                double latitude = Double.parseDouble(parts[0]);
                double longitude = Double.parseDouble(parts[1]);
                String name = parts[2];
                cities.add(new City(name, latitude, longitude));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cities;
    }

    public static void main(String[] args) {
        List<City> loadedCities = loadCities("./data.txt");

        cities = loadedCities;

        solveTSP();
    }
}