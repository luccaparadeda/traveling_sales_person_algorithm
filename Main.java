import java.io.*;
import java.util.*;

public class Main {
    // ...
    private static int POPULATION_SIZE = 0;
    private static int GENERATIONS = 0;
    private final static double MUTATION_RATE = 0.05;
    private final static double CROSSOVER_RATE = 0.9;

    private static List<City> cities;

    // public static List<List<City>> initializePopulation(List<City> cities) {
    // List<List<City>> population = new ArrayList<>();
    // for (int i = 0; i < POPULATION_SIZE; i++) {
    // List<City> newIndividual = new ArrayList<>(cities);
    // Collections.shuffle(newIndividual);
    // population.add(newIndividual);
    // }
    // return population;
    // }

    // public static double[] evaluateFitness(List<List<City>> population) {
    // double[] fitness = new double[population.size()];
    // for (int i = 0; i < population.size(); i++) {
    // fitness[i] = calculateTotalDistance(population.get(i));
    // }
    // return fitness;
    // }

    // public static List<List<City>> select(List<List<City>> population, double[]
    // fitness) {
    // // Implement a selection method, such as tournament selection, roulette wheel
    // // selection, etc.
    // // For simplicity, let's implement a random selection
    // List<List<City>> selected = new ArrayList<>();
    // for (int i = 0; i < population.size(); i++) {
    // int randomIndex = new Random().nextInt(population.size());
    // selected.add(new ArrayList<>(population.get(randomIndex)));
    // }
    // return selected;
    // }

    // public static List<List<City>> crossover(List<List<City>> selected) {
    // // Implement a crossover method, such as single-point crossover, multi-point
    // // crossover, etc.
    // // For simplicity, let's implement a single-point crossover
    // List<List<City>> offspring = new ArrayList<>();
    // for (int i = 0; i < selected.size(); i += 2) {
    // List<City> parent1 = selected.get(i);
    // List<City> parent2 = selected.get(i + 1);
    // int crossoverPoint = new Random().nextInt(parent1.size());
    // List<City> child1 = new ArrayList<>(parent1.subList(0, crossoverPoint));
    // child1.addAll(parent2.subList(crossoverPoint, parent2.size()));
    // List<City> child2 = new ArrayList<>(parent2.subList(0, crossoverPoint));
    // child2.addAll(parent1.subList(crossoverPoint, parent1.size()));
    // offspring.add(child1);
    // offspring.add(child2);
    // }
    // return offspring;
    // }

    public static void mutate(List<City> cities) {
        // Implement a mutation method, such as swap mutation, inversion mutation, etc.
        // For simplicity, let's implement a swap mutation
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

    public static List<List<City>> replace(List<List<City>> population, List<List<City>> offspring, double[] fitness,
            double[] offspringFitness) {
        return offspring;
    }

    // public static int getIndexOfBestSolution(double[] fitness) {
    // int bestIndex = 0;
    // for (int i = 1; i < fitness.length; i++) {
    // if (fitness[i] < fitness[bestIndex]) {
    // bestIndex = i;
    // }
    // }
    // return bestIndex;
    // }

    public static double calculateTotalDistance(List<City> route) {
        double totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += route.get(i).distanceTo(route.get(i + 1));
        }
        return totalDistance;
    }

    public static void solveTSP() {
        // Initialize population
        // List<List<City>> population = initializePopulation(cities);

        // Evaluate fitness
        // double[] fitness = evaluateFitness(population);

        double bestFitness = Double.MAX_VALUE;
        List<City> bestRoute = null;

        while (true) { // Infinite loop

            // Mutation
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

    public static List<Integer> toInversionSequence(List<City> permutation) {
        List<Integer> inversion = new ArrayList<>();
        for (int i = 0; i < permutation.size(); i++) {
            int inversions = 0;
            for (int j = 0; j < i; j++) {
                if (permutation.get(j).name.compareTo(permutation.get(i).name) > 0) {
                    inversions++;
                }
            }
            inversion.add(inversions);
        }
        return inversion;
    }

    public static List<City> fromInversionSequence(List<Integer> inversion) {
        List<City> permutation = new ArrayList<>(cities);
        for (int i = inversion.size() - 1; i >= 0; i--) {
            int inversions = inversion.get(i);
            City city = permutation.remove(i);
            int newIndex = Math.min(i + inversions, permutation.size());
            permutation.add(newIndex, city);
        }
        return permutation;
    }
}