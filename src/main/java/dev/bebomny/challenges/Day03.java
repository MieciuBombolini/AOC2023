package dev.bebomny.challenges;

import dev.bebomny.utils.AoCUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day03 extends AoCUtils {

    public Day03() {
        super("3");
    }

    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");
    private static final Pattern SYMBOL_SEARCH_PATTERN = Pattern.compile("[^\\d.]+");
    private static final Pattern GEAR_PATTERN = Pattern.compile("(\\*)");
    private static final int MAX_NUMBER_LENGTH = 4; //It's 3, but just to be safe I'll make it 4

    @Override
    public void solve(List<String> input) {
        long sumOfPartNumbers = 0;

        int lineIndex = 0;
        for (String line : input) {
            Matcher numberMatcher = NUMBER_PATTERN.matcher(line); //start = start() | end = end()-1

            while (numberMatcher.find()) {
                String foundNumber = numberMatcher.group();
                if (isEnginePart(input, lineIndex, line, numberMatcher)) {
                    sumOfPartNumbers += Integer.parseInt(foundNumber);
                }

            }

            lineIndex++;
        }

        lap(sumOfPartNumbers);

        //Part 2
        long sumOfGearRatios = 0;

        int gearLineIndex = 0;
        for (String line : input) {
            Matcher gearMatcher = GEAR_PATTERN.matcher(line); //start = start() | end = end()-1

            while (gearMatcher.find()) {
                gearMatcher.group(); //ignored
                Optional<Long> gearRatio = getGearRatio(input, gearLineIndex, gearMatcher);

                if (gearRatio.isPresent())
                    sumOfGearRatios += gearRatio.get();
            }

            gearLineIndex++;
        }

        lap(sumOfGearRatios);
    }

    private Optional<Long> getGearRatio(List<String> input, int gearLineIndex, Matcher gearMatcher) {
        List<Long> numbersFound = new ArrayList<>();

        for (int i = (gearLineIndex > 0 ? -1 : 0); i < (gearLineIndex < input.size() ? 2 : 0); i++) {
            Optional<List<Integer>> lineNumbersFound = getAllNearGearNumbers(input.get(gearLineIndex + i), gearMatcher.start());

            lineNumbersFound.ifPresent(
                    gearNumbers -> numbersFound.addAll(
                            gearNumbers.stream()
                                    .map(Integer::longValue)
                                    .toList()));

            if (numbersFound.size() >= 2)
                return Optional.of(numbersFound.get(0) * numbersFound.get(1));
        }

        return Optional.empty();
    }

    private Optional<List<Integer>> getAllNearGearNumbers(String line, int gearPos) {
        List<Integer> validNumbers = new ArrayList<>();
        Matcher numberMatcher = NUMBER_PATTERN.matcher(line);

        while (numberMatcher.find()) {
            String number = numberMatcher.group();

            if ((numberMatcher.start() >= gearPos - 1 && numberMatcher.start() <= gearPos + 1) //Start between gearPos - 1 and gearPos + 1
                    || ((numberMatcher.end() - 1) >= gearPos - 1 && (numberMatcher.end() - 1) <= gearPos + 1)) { //End between gearPos - 1 and gearPos + 1
                validNumbers.add(Integer.parseInt(number));
            }

            if (numberMatcher.start() > gearPos + MAX_NUMBER_LENGTH)
                break; //Skip if start() is beyond the valid search are - shaves of 1ms - Optimization at It's finest hah
        }

        if (validNumbers.isEmpty())
            return Optional.empty();

        return Optional.of(validNumbers);
    }

    private boolean isEnginePart(List<String> input, int currentLineIndex, String currentLine, Matcher currentNumberMatcher) {
        int searchStart = Math.max(currentNumberMatcher.start() - 1, 0);
        int searchEnd = Math.min(currentNumberMatcher.end(), currentLine.length() - 1);

        for (int i = (currentLineIndex > 0 ? -1 : 0); i < (currentLineIndex < input.size() ? 2 : 0); i++) {
            Matcher symbolMatcher = SYMBOL_SEARCH_PATTERN.matcher(input.get(currentLineIndex + i));
            while (symbolMatcher.find()) {
                symbolMatcher.group(); // ignored
                if (symbolMatcher.start() >= searchStart && symbolMatcher.start() <= searchEnd)
                    return true;
            }
        }

        return false;
    }
}