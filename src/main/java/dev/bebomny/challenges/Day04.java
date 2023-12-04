package dev.bebomny.challenges;

import dev.bebomny.utils.AoCUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day04 extends AoCUtils {

    public Day04() {
        super("4");
    }

    private static final String LINE_SPLIT = "\\|";
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");


    @Override
    public void solve(List<String> input) {
        long totalWinnings = 0;

        for(String line : input) {
            String card = line.split(":")[0];
            String numbers = line.split(":")[1];
            List<Integer> winningNumbers = extractNumbers(numbers.split(LINE_SPLIT)[0]);
            List<Integer> presentNumbers = extractNumbers(numbers.split(LINE_SPLIT)[1]);

            totalWinnings += presentNumbers.stream()
                    .filter(winningNumbers::contains)
                    .map(integer -> 1)
                    .reduce(this::getCardValue)
                    .orElse(0);
        }

        lap(totalWinnings);

        //part 2 - no need to make it recursive I think?>
        int totalScratchcards = 0;
        Map<Integer, Integer> scratchcards = new HashMap<>();
        for(int i = 1; i <= input.size(); i++) {
            scratchcards.put(i, 1);
        }

        for(String line : input) {
            int card = extractNumbers(line.split(":")[0]).getFirst();
            String numbers = line.split(":")[1];
            List<Integer> winningNumbers = extractNumbers(numbers.split(LINE_SPLIT)[0]);
            List<Integer> presentNumbers = extractNumbers(numbers.split(LINE_SPLIT)[1]);

            int cardsWon = presentNumbers.stream()
                    .filter(winningNumbers::contains)
                    .map(integer -> 1)
                    .reduce(Integer::sum)
                    .orElse(0);

            for (int i = 1; i <= cardsWon; i++)
                scratchcards.put((card) + i, scratchcards.get((card) + i) + scratchcards.get(card));
        }
        totalScratchcards = scratchcards.values().stream()
                .reduce(Integer::sum)
                .orElse(0);

        lap(totalScratchcards);
    }

    private Integer getCardValue(int a, int b) {
        return a*2;
    }

    private List<Integer> extractNumbers(String linePart) {
        List<Integer> numbers = new ArrayList<>();

        Matcher numbreMatcher = NUMBER_PATTERN.matcher(linePart);
        while(numbreMatcher.find())
            numbers.add(Integer.parseInt(numbreMatcher.group()));

        return numbers;
    }
}
