package dev.bebomny.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AoCUtils {

    public static final String RESOURCE_PATH = "src/main/resources/";
    //use reFetch() if you need to refresh the data
    //use dontFetch() if you don't want to fetch new data

    private int part = 1;

    //The start time in nanoseconds
    private long timerStart;

    public AoCUtils(String day) {
        Path path = Path.of( RESOURCE_PATH + "day" + day + ".txt");
        InputDataFetcher.fetchAndSave(day);

        //If the file exists run with all data
        if (Files.exists(path)) {
            try {
                List<String> input = Files.readAllLines(path);
                timerStart = System.nanoTime(); // start measuring time
                solve(input);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            timerStart = System.nanoTime(); // start measuring time
            solve(new ArrayList<>()); // runs the algorithm with empty List as input
        }
    }

    //In this method each class that extends this one should have the solving algorithm
    public abstract void solve(List<String> input);

    public void lap(int answer) {
        lap(String.valueOf(answer));
    }

    public void lap(long answer) {
        lap(String.valueOf(answer));
    }

    //Records the time it took to run the algorithm and displays the answer with time it took to solve
    public void lap(String answer) {
        long timeSpent = (System.nanoTime() - timerStart) / 1000;
        System.out.println("Part " + part + ": " + answer + ", Duration: " + timeToString(timeSpent));
        timerStart = System.nanoTime();
        part++;
    }

    //Converting nanosecond time into a more readable form
    public String timeToString(long timeSpent) {
        if (timeSpent < 1000)
            return timeSpent + "μs";
        if (timeSpent < 1000000)
            return (timeSpent / 1000.0) + "ms";
        return (timeSpent / 1000000.0) + "s";
    }

    //Converts an ArrayList of Strings into an ArrayList of Integers
    public List<Integer> convertToInts(List<String> input) {
        List<Integer> ints = new ArrayList<>();
        for (String s : input)
            ints.add(Integer.parseInt(s));
        return ints;
    }

    //Converts an ArrayList of Strings into an ArrayList of Longs
    public List<Long> convertToLongs(List<String> input) {
        List<Long> longs = new ArrayList<>();
        for (String s : input)
            longs.add(Long.parseLong(s));
        return longs;
    }

    public static void refetch() {
        InputDataFetcher.refetch = true;
    }

    public static void dontFetch() {
        InputDataFetcher.dontFetch = true;
    }
}
