package dev.bebomny.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AoCUtils {

    private int part = 1;
    private long timerStart;

    public AoCUtils(String day) {
        Path path = Path.of("src/main/resources/day" + day + ".txt");
        List<String> inputLines = new ArrayList<>();
        if (Files.exists(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    inputLines.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        timerStart = System.nanoTime();
        solve(inputLines);
    }

    public abstract void solve(List<String> input);

    public void lap(int answer) {
        lap(String.valueOf(answer));
    }

    public void lap(long answer) {
        lap(String.valueOf(answer));
    }

    public void lap(String answer) {
        long timeSpent = (System.nanoTime() - timerStart) / 1000;
        System.out.println("Part " + part + ": " + answer + ", Duration: " + timeToString(timeSpent));
        timerStart = System.nanoTime();
        part++;
    }

    public String timeToString(long timeSpent) {
        if (timeSpent < 1000)
            return timeSpent + "μs";
        if (timeSpent < 1000000)
            return (timeSpent / 1000.0) + "ms";
        return (timeSpent / 1000000.0) + "s";
    }

    public List<Integer> convertToInts(List<String> input) {
        List<Integer> ints = new ArrayList<>();
        for (String s : input)
            ints.add(Integer.parseInt(s));
        return ints;
    }

    public List<Long> convertToLongs(List<String> input) {
        List<Long> longs = new ArrayList<>();
        for (String s : input)
            longs.add(Long.parseLong(s));
        return longs;
    }
}
