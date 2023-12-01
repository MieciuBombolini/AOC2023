package dev.bebomny.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AoCUtils {

    private int part = 1;

    //The start time in nanoseconds
    private long timerStart;

    public AoCUtils(String day) {

        //Checks if it has a new file available in the "resources" folder for each day
        //That file should contain that days input data
        File dayInputFile = new File("src/main/resources/day" + day + ".txt");

        //If that file doesn't exist it runs it with an empty ArrayList
        if(!dayInputFile.exists()) {
            System.out.println("File does not exist!");
            timerStart = System.nanoTime(); // start measuring time
            solve(new ArrayList<>()); // runs the algorithm implemented in the solve method with an empty ArrayList
            return;
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(dayInputFile));
        } catch (FileNotFoundException e) {
            System.err.println("File Not Found!");
            timerStart = System.nanoTime(); // start measuring time
            solve(new ArrayList<>()); // runs the algorithm implemented in the solve method with an empty ArrayList
            return;
        }

        //If the file exists it put each line as String into an ArrayList
        List<String> inputLines = new ArrayList<>();
        try {
            String line;
            while((line = reader.readLine()) != null)
                inputLines.add(line);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        timerStart = System.nanoTime(); // start measuring time
        solve(inputLines); // runs the algorithm with the ArrayList as input
    }

    //In this method each class that extends this one should have the solving algorithm
    public abstract void solve(List<String> input);

    public void lap(int answer){
        lap(String.valueOf(answer));
    }

    public void lap(long answer){
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
    public String timeToString(long timeSpent){
        if(timeSpent < 1000)
            return timeSpent + "μs";
        if(timeSpent < 1000000)
            return (timeSpent / 1000.0) + "ms";
        return (timeSpent / 1000000.0) + "s";
    }

    //Converts and ArrayList of Strings into an ArrayList of Integers
    public List<Integer> convertToInts(List<String> input){
        List<Integer> ints = new ArrayList<>();
        for(String s : input)
            ints.add(Integer.parseInt(s));
        return ints;
    }

    //Converts and ArrayList of Strings into an ArrayList of Longs
    public List<Long> convertToLongs(List<String> input){
        List<Long> longs = new ArrayList<>();
        for(String s : input)
            longs.add(Long.parseLong(s));
        return longs;
    }
}
