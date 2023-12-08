package dev.bebomny.challenges;

import dev.bebomny.utils.AoCUtils;
import dev.bebomny.utils.QuackYouException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day07 extends AoCUtils {

    private static final Pattern HAND_PATTERN = Pattern.compile("(\\w+) (\\d+)");

    public Day07() {
        super("7");
    }

    @Override
    public void solve(List<String> input) {
        long totalWinnings = 0;
        List<Hand> hands = new ArrayList<>(input.stream()
                .map(Hand::of)
                .toList());

        hands.sort(Comparator.comparingInt((Hand hand) -> hand.handType().worth())
                //.reversed()
                .thenComparing(Hand::compareCards));

        totalWinnings = getTotalWinnings(hands);
        lap(totalWinnings);

        //Part 2
        List<Hand> handsPart2 = new ArrayList<>(input.stream()
                .map(Hand::ofPart2)
                .toList());

        handsPart2.sort(Comparator.comparingInt((Hand hand) -> hand.handType().worth())
                .thenComparing(Hand::compareCards));

        long newTotalWinnings = getTotalWinnings(handsPart2);
        lap(newTotalWinnings);
    }

    private long getTotalWinnings(List<Hand> hands) {
        long totalWinnings = 0;
        for (int i = 0; i < hands.size(); i++) {
            totalWinnings += (long) hands.get(i).bid() * (i + 1);
        }
        return totalWinnings;
    }

    private record Hand(String hand, List<Card> cards, HandType handType, int bid) {

        public static Hand of(String line) {
            Matcher handMatcher = HAND_PATTERN.matcher(line);
            handMatcher.find();

            String stringHand = handMatcher.group(1);
            List<Card> cards = Arrays.stream(stringHand.split(""))
                    .map(s -> s.charAt(0))
                    .map(Card::getCardFromChar)
                    .toList();

            HandType type = HandType.getHandType(cards, false);
            int bid = Integer.parseInt(handMatcher.group(2));

            return new Hand(stringHand, cards, type, bid);
        }

        public static Hand ofPart2(String line) {
            Matcher handMatcher = HAND_PATTERN.matcher(line);
            handMatcher.find();

            String stringHand = handMatcher.group(1);
            List<Card> cards = Arrays.stream(stringHand.split(""))
                    .map(s -> s.charAt(0))
                    .map(Card::getCardFromCharPart2)
                    .toList();

            HandType type = HandType.getHandType(cards, true);
            int bid = Integer.parseInt(handMatcher.group(2));

            return new Hand(stringHand, cards, type, bid);
        }

        public static int compareCards(Hand hand1, Hand hand2) {
            for (int i = 0; i < hand1.cards().size(); i++) {
                if(hand1.cards().get(i) != hand2.cards().get(i))
                    return hand1.cards().get(i).value - hand2.cards().get(i).value;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "Hand{" +
                    "hand='" + hand + '\'' +
                    ", cards=" + cards +
                    ", handType=" + handType +
                    ", bid=" + bid +
                    '}';
        }

        private enum HandType {
            FIVE_OF_KIND(7), //five the same
            FOUR_OF_KIND(6), //four the same
            FULL_HOUSE(5), //three of one and two of other kind
            THREE_OF_KIND(4), //three the same
            TWO_PAIR(3), //two pairs of different
            ONE_PAIR(2), //one pair
            HIGH_CARD(1); //all different

            private final int worth;

            HandType(int value) {
                this.worth = value;
            }

            public static HandType getHandType(List<Card> cards, boolean part2) {
                Map<Card, Integer> handCount = cards.stream()
                        .collect(Collectors.groupingBy(
                                card -> card,
                                Collectors.summingInt(e -> 1)
                        ));

                if(part2) {
                    int jokerCount = handCount.getOrDefault(Card.JOKER, 0);
                    Card cardWithHighestCount = handCount.entrySet()
                            .stream()
                            .filter(cardIntegerEntry -> cardIntegerEntry.getKey() != Card.JOKER)
                            .max(Map.Entry.<Card, Integer>comparingByValue()
                                    .thenComparing(Map.Entry::getKey))
                            .map(Map.Entry::getKey)
                            .orElse(Card.JOKER);

                    handCount.replace(
                            cardWithHighestCount,
                            handCount.getOrDefault(cardWithHighestCount, 0) + jokerCount);

                    if(jokerCount < 5)
                        handCount.remove(Card.JOKER);
                }

                switch (handCount.size()) {
                    case 1 -> { //only one type
                        return FIVE_OF_KIND;
                    }

                    case 2 -> { //two different //check for four_kind, full house,
                        if(handCount.containsValue(4)) //four kind
                            return FOUR_OF_KIND;
                        if(handCount.containsValue(3)) //full house
                            return FULL_HOUSE;
                    }

                    case 3 -> { //check for three of kind, two pair
                        if(handCount.containsValue(3)) //Three kind
                            return THREE_OF_KIND;
                        if(handCount.containsValue(2)) //Two Pair
                            return TWO_PAIR;
                    }

                    case 4 -> {//check for one pair
                        return ONE_PAIR;
                    }

                    default -> { //Five different
                        return HIGH_CARD;
                    }
                }

                throw new QuackYouException("Card identification failed!");
            }

            public int worth() {
                return worth;
            }
        }

        private enum Card {
            ACE('A', 14),
            KING('K', 13),
            QUEEN('Q', 12),
            JACK('J', 11),
            TEN('T', 10),
            NINE('9', 9),
            EIGHT('8', 8),
            SEVEN('7', 7),
            SIX('6', 6),
            FIVE('5', 5),
            FOUR('4', 4),
            THREE('3', 3),
            TWO('2', 2),
            JOKER('J', 1);

            final char character;
            final int value;

            Card(char character, int value) {
                this.character = character;
                this.value = value;
            }

            public static Card getCardFromChar(char character) {
                for(Card card : values()) {
                    if(card.character == character)
                        return card;
                }
                throw new QuackYouException("Trying to parse an unknown card");
            }

            public static Card getCardFromCharPart2(char character) {
                if(character == JOKER.character)
                    return JOKER;

                for(Card card : values()) {
                    if(card.character == character)
                        return card;
                }
                throw new QuackYouException("Trying to parse an unknown card");
            }
        }
    }
}
