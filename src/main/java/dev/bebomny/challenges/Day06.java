package dev.bebomny.challenges;

import dev.bebomny.utils.AoCUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day06 extends AoCUtils {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");

    // solve: -hold^2 + time*hold - distance = 0
    //        hold = (time +- sqrt(time^2 - 4 * distance)) / 2
    //             = (time +- d) / 2
    // solution: (time - d) / 2 < hold < (time - d) / 2

    public Day06() {
        super("6");
    }

    @Override
    public void solve(List<String> input) {
        List<Race> races = prepareRaces(input);

        //Part 1
        long margin = races.stream()
                .map(this::solveQuadratic)
                .reduce((integer, integer2) -> integer * integer2)
                .get();
        lap(margin);

        //Part 2
        Race bigRace = prepareOneLargeRace(input);
        long bigRaceMargin = solveQuadratic(bigRace);
        lap(bigRaceMargin);
    }

    private long solveQuadratic(Race race) {
        double delta = Math.sqrt(race.holdTime() * race.holdTime() - 4 * race.recordLength());
        return (long) (((race.holdTime() + delta) / 2) - Math.ceil(((race.holdTime() - delta) / 2)) + 1 - 2 * ((delta % 1) == 0 ? 1 : 0));
    }

    private Race prepareOneLargeRace(List<String> input) {
        Matcher timeMatcher = NUMBER_PATTERN.matcher(input.get(0));
        Matcher recordLengthMatcher = NUMBER_PATTERN.matcher(input.get(1));

        String time = "";
        String record = "";
        while (timeMatcher.find() && recordLengthMatcher.find()) {
            time = time.concat(timeMatcher.group());
            record = record.concat(recordLengthMatcher.group());
        }

        return new Race(Long.parseLong(time), Long.parseLong(record));
    }

    private List<Race> prepareRaces(List<String> input) {
        List<Race> races = new ArrayList<>();
        Matcher timeMatcher = NUMBER_PATTERN.matcher(input.get(0));
        Matcher recordLengthMatcher = NUMBER_PATTERN.matcher(input.get(1));

        while (timeMatcher.find() && recordLengthMatcher.find())
            races.add(new Race(Long.parseLong(timeMatcher.group()), Long.parseLong(recordLengthMatcher.group())));

        return races;
    }

    private record Race(long holdTime, long recordLength) {
    }
}
