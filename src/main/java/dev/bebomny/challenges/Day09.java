package dev.bebomny.challenges;

import dev.bebomny.utils.AoCUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Day09 extends AoCUtils {

    private static final Pattern EXTENDED_NUMBER_PATTERN = Pattern.compile("(-?\\d+)"); // includes negative numbers

    public Day09() {
        super("9");
    }

    //My first Idea was to use Lagrange Interpolation formula
    // - for every finite sequence of numbers there is at least 1 polynomial such
    //   f(i) = a_i for all i between 1 and n. - description yeeted from stackExchange
    // but I couldn't get It to work properly :/
    // so I went with the algorithm shown as an example
    // It's probably the Neville's algorithm
    // (https://mathworld.wolfram.com/NevillesAlgorithm.html)
    @Override
    public void solve(List<String> input) {
        List<List<Integer>> sequences = new ArrayList<>(extractSequences(input));

        List<Integer> nextInSequence = sequences.stream()
                .map(this::getNextInSequence)
                .toList();

        //nextInSequence.forEach(System.out::println);
        int sum = nextInSequence.stream().reduce(Integer::sum).orElse(0);
        lap(sum);


        //part2 - Get Uno reversed I guess
        List<List<Integer>> reversedSequences = sequences.stream()
                .map(List::reversed)
                .toList();

        List<Integer> reversedNextInSequence = reversedSequences.stream()
                .map(this::getNextInSequence)
                .toList();

        //reversedNextInSequence.forEach(System.out::println);
        int reversedSum = reversedNextInSequence.stream().reduce(Integer::sum).orElse(0);
        lap(reversedSum);
    }

    private int getNextInSequence(List<Integer> sequence) {
        List<List<Integer>> subSequences = new ArrayList<>();
        subSequences.add(sequence);

        while (!subSequences.getLast().stream().allMatch(integer -> integer == 0)) {
            List<Integer> lastSequence = subSequences.getLast();
            List<Integer> nextSequence = new ArrayList<>();
            for (int i = 1; i < lastSequence.size(); i++) {
                nextSequence.add(lastSequence.get(i) - lastSequence.get(i - 1));
            }
            subSequences.add(nextSequence);
        }

        for (int i = subSequences.size() - 1; i >= 0; i--) {
            if (i == subSequences.size() - 1) {
                subSequences.get(i).add(0);
            } else {
                List<Integer> currentSequence = new ArrayList<>(subSequences.get(i));
                List<Integer> nextSequence = new ArrayList<>(subSequences.get(i + 1));
                int currentValue = currentSequence.getLast();
                int nextValue = nextSequence.getLast();
                currentSequence.add(currentValue + nextValue);
                subSequences.set(i, new ArrayList<>(currentSequence)); //I had some problems with Immutability here hah
            }
        }
        return subSequences.get(0).getLast();
    }

    //Works on example data, but on the main sample there's an edge case somewhere
    // where this produces a wrong number :/ sadge
    private int calcNextUsingLagrangePoly(List<Integer> sequence) {
        double result = 0;
        int n = sequence.size();

        for (int i = 0; i < n; i++) {
            double term = sequence.get(i);
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    term = term * (n - j) / (i - j);
                }
            }
            result += term;
        }

        return (int) result;
    }

    private List<List<Integer>> extractSequences(List<String> input) {
        List<List<Integer>> sequences = new ArrayList<>();
        for (String line : input) {
            List<Integer> initialSequence = EXTENDED_NUMBER_PATTERN.matcher(line).results()
                    .map(MatchResult::group)
                    .map(Integer::parseInt)
                    .toList();
            sequences.add(initialSequence);
        }
        return new ArrayList<>(sequences);
    }
}
