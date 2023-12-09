package dev.bebomny.challenges;

import dev.bebomny.utils.AoCUtils;
import dev.bebomny.utils.QuackYouException;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day08 extends AoCUtils {

    private static final Pattern NODE_PATTERN = Pattern.compile("(\\w+) = \\((\\w+), (\\w+)\\)");
    private static final String START_NODE = "AAA";
    private static final String END_NODE = "ZZZ";

    public Day08() {
        super("8");
    }

    @Override
    public void solve(List<String> input) {
        List<Instruction> instructionSet = parseInstructionSet(input.getFirst());
        System.out.println("instructionSet = " + instructionSet);

        Map<String, Node> nodes = parseNodes(input);
        int steps = traverseNodes(START_NODE, nodes, instructionSet);

        lap(steps);

        //part 2
        List<String> startingNodes = getPart2StartingNodes(nodes);
        System.out.println("startingNodes = " + startingNodes);
        //Bruteforce solution - don't use hah ~126years of runtime
        //long stepsPart2 = traversePart2Nodes(startingNodes, nodes, instructionSet);

        //THE plan:
        // 1. Compute all nodes one by one
        // 2. and find the lowest common multiple
        // 2.1 using the LCM by GCF(greatest common factor) method
        // 2.1.1 LCM(a,b) = (a*b)/GCF(a,b) - Found somewhere, *borrowed*, ty someone!
        List<Long> stepsList = new ArrayList<>();
        for (String startingNode : startingNodes)
            stepsList.add(traverseSinglePart2Node(startingNode, nodes, instructionSet));

        System.out.println("stepsList = " + stepsList);

        long stepsPart2 = stepsList.stream()
                .reduce((this::lowestCommonMultiple))
                .orElse(0L);

        lap(stepsPart2);
    }

    //LCM(a,b) = (a*b)/GCF(a,b)
    private long lowestCommonMultiple(long a, long b) {
        return (a * b) / greatestCommonFactor(a, b);
    }

    //Find the GCF using the Euclidean algorithm(IDK what that is probably this thingy)
    private long greatestCommonFactor(long a, long b) {
        return b == 0 ? a : greatestCommonFactor(b, a % b);
    }

    private long traverseSinglePart2Node(String startingNode, Map<String, Node> nodes, List<Instruction> instructionSet) {
        long steps = 0L;
        String currentNode = startingNode;

        while (!endWithEndChar(currentNode, 'Z')) {
            for (Instruction instruction : instructionSet) {
                currentNode = nodes.get(currentNode).getNextNode(instruction);
                steps++;
            }
        }
        return steps;
    }

    //Brute-force solution according to the internet it would take ~126 years to compute hah
    private long traversePart2Nodes(List<String> startingNodes, Map<String, Node> nodes, List<Instruction> instructionSet) {
        AtomicLong steps = new AtomicLong();
        List<String> currentNodes = startingNodes;

        while (!allEndWithEndChar(currentNodes, 'Z')) {
            //System.out.println("currentNodes = " + currentNodes);
            for (Instruction instruction : instructionSet) {
                currentNodes = currentNodes.stream().map(node -> {
                    steps.getAndIncrement();
                    return nodes.get(node).getNextNode(instruction);
                }).toList();
            }
        }
        System.out.println("Final nodes = " + currentNodes);
        return steps.get();
    }

    private boolean endWithEndChar(String node, char endChar) {
        return node.charAt(2) == endChar;
    }

    private boolean allEndWithEndChar(List<String> nodes, char endChar) {
        for (String node : nodes) {
            if (node.charAt(2) != endChar)
                return false;
        }
        return true;
    }

    private List<String> getPart2StartingNodes(Map<String, Node> nodes) {
        return nodes.keySet().stream().filter(s -> s.charAt(2) == 'A').toList();
    }

    private int traverseNodes(String startingNode, Map<String, Node> nodes, List<Instruction> instructionSet) {
        int steps = 0;
        String currentNode = startingNode;
        while (!Objects.equals(currentNode, END_NODE)) {
            for (Instruction instruction : instructionSet) {
                currentNode = nodes.get(currentNode).getNextNode(instruction);
                steps++;
            }
        }

        return steps;
    }

    private Map<String, Node> parseNodes(List<String> nodeInput) {
        Map<String, Node> nodes = new HashMap<>();
        for (String line : nodeInput) {
            Matcher nodeMatcher = NODE_PATTERN.matcher(line);
            if (!nodeMatcher.find())
                continue;
            Node node = new Node(nodeMatcher.group(1), nodeMatcher.group(2), nodeMatcher.group(3));
            nodes.put(node.name(), node);
        }
        return nodes;
    }

    private List<Instruction> parseInstructionSet(String stringInstructions) {
        return Arrays.stream(stringInstructions.split(""))
                .map(s -> s.charAt(0))
                .map(Instruction::getInstructionFromChar)
                .toList();
    }

    private enum Instruction {
        LEFT('L'),
        RIGHT('R');

        private final char character;

        Instruction(char character) {
            this.character = character;
        }

        public static Instruction getInstructionFromChar(char character) {
            for (Instruction instruction : values()) {
                if (instruction.character() == character)
                    return instruction;
            }
            throw new QuackYouException("Invalid instruction character!");
        }

        public char character() {
            return character;
        }
    }

    private record Node(String name, String leftNodeName, String rightNodeName) {
        public String getNextNode(Instruction instruction) {
            return switch (instruction) {
                case LEFT -> leftNodeName();
                case RIGHT -> rightNodeName();
            };
        }
    }
}
