import java.io.*;
import java.util.*;

public class Main {
    // ...
    private static int POPULATION_SIZE = 0;
    private final static int GENERATIONS = 1000;
    private final static double MUTATION_RATE = 0.05;
    private final static double CROSSOVER_RATE = 0.9;

    private static List<City> cities;

    public static List<List<City>> initializePopulation(List<City> cities) {
        List<List<City>> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            List<City> newIndividual = new ArrayList<>(cities);
            Collections.shuffle(newIndividual);
            population.add(newIndividual);
        }
        return population;
    }

    public static double[] evaluateFitness(List<List<City>> population) {
        double[] fitness = new double[population.size()];
        for (int i = 0; i < population.size(); i++) {
            fitness[i] = calculateTotalDistance(population.get(i));
        }
        return fitness;
    }

    public static List<List<City>> select(List<List<City>> population, double[] fitness) {
        List<List<City>> selected = new ArrayList<>();
        for (int i = 0; i < population.size(); i++) {
            int randomIndex = new Random().nextInt(population.size());
            selected.add(new ArrayList<>(population.get(randomIndex)));
        }
        return selected;
    }

    public static List<List<City>> crossover(List<List<City>> selected) {
        List<List<City>> offspring = new ArrayList<>();
        for (int i = 0; i < selected.size(); i += 2) {
            List<City> parent1 = selected.get(i);
            List<City> parent2 = selected.get(i + 1);
            List<City> child1 = inversionSequenceCrossover(parent1, parent2);
            List<City> child2 = inversionSequenceCrossover(parent2, parent1);
            offspring.add(child1);
            offspring.add(child2);
        }
        return offspring;
    }

    public static List<City> inversionSequenceCrossover(List<City> parent1, List<City> parent2) {
        List<Integer> parent1Inversion = toInversionSequence(parent1);
        List<Integer> parent2Inversion = toInversionSequence(parent2);

        int crossoverPoint = new Random().nextInt(parent1Inversion.size());
        List<Integer> childInversion = new ArrayList<>(parent1Inversion.subList(0, crossoverPoint));
        childInversion.addAll(parent2Inversion.subList(crossoverPoint, parent2Inversion.size()));

        return fromInversionSequence(childInversion);
    }

    public static void mutate(List<List<City>> offspring) {
        for (List<City> individual : offspring) {
            if (Math.random() < MUTATION_RATE) {
                List<Integer> inversion = toInversionSequence(individual);

                int index1 = new Random().nextInt(inversion.size());
                int index2 = new Random().nextInt(inversion.size());
                Collections.swap(inversion, index1, index2);

                individual = fromInversionSequence(inversion);
            }
        }
    }

    public static List<List<City>> replace(List<List<City>> population, List<List<City>> offspring, double[] fitness,
            double[] offspringFitness) {
        return offspring;
    }

    public static int getIndexOfBestSolution(double[] fitness) {
        int bestIndex = 0;
        for (int i = 1; i < fitness.length; i++) {
            if (fitness[i] < fitness[bestIndex]) {
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    public static double calculateTotalDistance(List<City> route) {
        double totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += route.get(i).distanceTo(route.get(i + 1));
        }
        return totalDistance;
    }

    public static List<City> solveTSP() {
        List<List<City>> population = initializePopulation(cities);

        double[] fitness = evaluateFitness(population);

        double bestFitness = Double.MAX_VALUE;
        List<City> bestRoute = null;

        while (true) {
            List<List<City>> selected = select(population, fitness);

            List<List<City>> offspring = crossover(selected);

            mutate(offspring);

            double[] offspringFitness = evaluateFitness(offspring);

            population = replace(population, offspring, fitness, offspringFitness);

            int bestIndex = getIndexOfBestSolution(offspringFitness);
            if (offspringFitness[bestIndex] < bestFitness) {
                bestFitness = offspringFitness[bestIndex];
                bestRoute = new ArrayList<>(population.get(bestIndex));
                System.out.println("New best route found with distance: " + bestFitness);
            }
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

        List<City> bestRoute = solveTSP();
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