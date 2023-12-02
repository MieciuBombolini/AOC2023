package dev.bebomny.challenges;

import dev.bebomny.utils.AoCUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day02 extends AoCUtils {

    public Day02() {
        super("2");
    }

    //possible with 12 red cubes, 13 green cubes, and 14 blue cubes
    //line schema
    // GAME {ID}: {amount} {color}, {amount} {color}, {amount} {color}; {amount} {color}, {amount} {color}, {amount} {color}; ...
    private static final Pattern GAME_ID_PATTERN = Pattern.compile("Game\\s(\\d+):");
    private static final Pattern SPACE_NUMBER_PATTERN = Pattern.compile(" (\\d+)");
    private static final Pattern GROUP_PATTERN = Pattern.compile("( [a-z, (\\d+)a-z]+)+");
    private static final Pattern CUBES_PATTERN = Pattern.compile("(\\d+) (red|green|blue)");
    private static final String RED_KEY = "red";
    private static final String GREEN_KEY = "green";
    private static final String BLUE_KEY = "blue";
    private static final int RED_MAX = 12;
    private static final int GREEN_MAX = 13;
    private static final int BLUE_MAX = 14;

    @Override
    public void solve(List<String> input) {
        int sumOfIDs = 0;
        long sumOfPowers = 0;

        for (String line : input) {
            int gameID = extractID(line);
            boolean possible = true;

            Map<String, Integer> cubeMaxMap = new HashMap<>();
            cubeMaxMap.put(RED_KEY, 1);
            cubeMaxMap.put(GREEN_KEY, 1);
            cubeMaxMap.put(BLUE_KEY, 1);

            Matcher groupMatcher = GROUP_PATTERN.matcher(line);
            while (groupMatcher.find()) {
                if (groupMatcher.group().matches(SPACE_NUMBER_PATTERN.toString()))
                    continue;

                Map<String, Integer> cubeMap = extractCubes(groupMatcher.group());

                cubeMap.forEach((color, amount) -> {
                    if (amount > cubeMaxMap.get(color))
                        cubeMaxMap.put(color, amount);
                });

                if ((cubeMap.get(RED_KEY) > RED_MAX) || (cubeMap.get(GREEN_KEY) > GREEN_MAX) || (cubeMap.get(BLUE_KEY) > BLUE_MAX))
                    possible = false;
            }

            if (possible)
                sumOfIDs += gameID;

            sumOfPowers += (long) cubeMaxMap.get(RED_KEY) * cubeMaxMap.get(GREEN_KEY) * cubeMaxMap.get(BLUE_KEY);
        }

        lap(sumOfIDs);
        lap(sumOfPowers);

    }

    private static int extractID(String input) {
        Matcher idMatcher = GAME_ID_PATTERN.matcher(input);
        return idMatcher.find() ? Integer.parseInt(idMatcher.group(1)) : 0;
    }

    private static Map<String, Integer> extractCubes(String input) {
        Map<String, Integer> cubeMap = new HashMap<>();
        cubeMap.put(RED_KEY, 0);
        cubeMap.put(GREEN_KEY, 0);
        cubeMap.put(BLUE_KEY, 0);

        Matcher cubeMatcher = CUBES_PATTERN.matcher(input);
        while (cubeMatcher.find())
            cubeMap.replace(cubeMatcher.group(2), cubeMap.get(cubeMatcher.group(2)) + Integer.parseInt(cubeMatcher.group(1)));

        return cubeMap;
    }
}