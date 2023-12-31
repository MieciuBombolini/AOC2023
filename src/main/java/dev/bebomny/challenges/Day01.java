package dev.bebomny.challenges;

import dev.bebomny.utils.AoCUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day01 extends AoCUtils {

    public Day01() {
        super("1");
    }

    //part1
    //extract first and last number of each string
    //form a 2-digit number with them
    //  If there is only one digit use it 2 times
    //  If there are more digits only use the first and last one
    //sum them
    private static final Pattern numberOnlyPattern = Pattern.compile("\\d");

    //part2
    //Extract number asin part1 but also include worded number like: "three"
    private static final HashMap<String, Integer> wordToNumberMap = new HashMap<>();
    private static final HashMap<String, Integer> reversedWordToNumberMap = new HashMap<>();
    private static final String combinedPattern;
    private static final String reversedCombinedPattern;
    private static final Pattern extendedNumberPattern;
    private static final Pattern reversedExtendedNumberPattern;

    static {
        wordToNumberMap.put("one", 1);
        wordToNumberMap.put("two", 2);
        wordToNumberMap.put("three", 3);
        wordToNumberMap.put("four", 4);
        wordToNumberMap.put("five", 5);
        wordToNumberMap.put("six", 6);
        wordToNumberMap.put("seven", 7);
        wordToNumberMap.put("eight", 8);
        wordToNumberMap.put("nine", 9);

        reversedWordToNumberMap.put("eno", 1);
        reversedWordToNumberMap.put("owt", 2);
        reversedWordToNumberMap.put("eerht", 3);
        reversedWordToNumberMap.put("ruof", 4);
        reversedWordToNumberMap.put("evif", 5);
        reversedWordToNumberMap.put("xis", 6);
        reversedWordToNumberMap.put("neves", 7);
        reversedWordToNumberMap.put("thgie", 8);
        reversedWordToNumberMap.put("enin", 9);

        combinedPattern = "\\d|(" + String.join("|", wordToNumberMap.keySet()) + ")";
        extendedNumberPattern = Pattern.compile(combinedPattern, Pattern.CASE_INSENSITIVE);

        reversedCombinedPattern = "\\d|(" + String.join("|", reversedWordToNumberMap.keySet()) + ")";
        reversedExtendedNumberPattern = Pattern.compile(reversedCombinedPattern, Pattern.CASE_INSENSITIVE);
    }


    @Override
    public void solve(List<String> input) {
        //Part 1
        long calibrationSum = 0;

        for (String line : input) {
            List<Integer> extractedNumbers = extractNumberNumbers(line);
            if(extractedNumbers.isEmpty()) continue;
            String number = extractedNumbers.getFirst() + String.valueOf(extractedNumbers.getLast());
            //System.out.println("Calibration number: " + number + " | Number1: " + extractedNumbers.getFirst() + " Number2: " + extractedNumbers.getLast());
            calibrationSum += Integer.parseInt(number);
        }

        lap(calibrationSum);

        //Part 2
        long fixedCalibrationSum = 0;

        for (String line : input) {
            List<Integer> extractedNumbers = extractAllNumbers(line);
            if(extractedNumbers.isEmpty()) continue;
            String number = extractedNumbers.getFirst() + String.valueOf(extractedNumbers.getLast());
            //System.out.println("Fixed calibration number: " + number + " | String: " + line + "| Number1: " + extractedNumbers.getFirst() + " Number2: " + extractedNumbers.getLast());
            fixedCalibrationSum += Integer.parseInt(number);
        }

        lap(fixedCalibrationSum);
    }

    private static List<Integer> extractNumberNumbers(String input) {
        List<Integer> numbers = new ArrayList<>();

        Matcher numberMatcher = numberOnlyPattern.matcher(input);

        while(numberMatcher.find()) {
            numbers.add(Integer.parseInt(numberMatcher.group()));
        }

        return numbers;
    }

    private static List<Integer> extractAllNumbers(String input) {
        List<Integer> numbers = new ArrayList<>();

        Matcher numberMatcher = extendedNumberPattern.matcher(input);

        if (numberMatcher.find()) {
            String match = numberMatcher.group();
            if (match.matches("\\d")) {
                // If it's a numeric digit, convert and add to the list
                numbers.add(Integer.parseInt(match));
            } else {
                // If it's a word representation, look up and add to the list
                numbers.add(wordToNumberMap.get(match.toLowerCase()));
            }
        }

        //Reverse everything
        String reversedString = new StringBuilder()
                .append(input)
                .reverse()
                .toString();
        Matcher reverseMatcher = reversedExtendedNumberPattern.matcher(reversedString);

        if (reverseMatcher.find()) {
            String match = reverseMatcher.group();
            if (match.matches("\\d")) {
                // If it's a numeric digit, convert and add to the list
                numbers.add(Integer.parseInt(match));
            } else {
                // If it's a word representation, look up and add to the list
                numbers.add(reversedWordToNumberMap.get(match.toLowerCase()));
            }
        }

        return numbers;
    }

}
