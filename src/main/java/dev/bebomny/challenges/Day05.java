package dev.bebomny.challenges;

import dev.bebomny.utils.AoCUtils;
import dev.bebomny.utils.Range;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day05 extends AoCUtils {

    public Day05() {
        super("5");
    }

    //Day 5 brute-force approach
    //Maybe one day I'll try it using range overlaps

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    private static final Pattern SEED_RANGE_PATTERN = Pattern.compile("(\\d+) (\\d+)");
    private static final Pattern STARTING_SEEDS_PATTERN = Pattern.compile("seeds: (\\d+(?: \\d+)*)");
    private static final Pattern MAP_CAPTURING_PATTERN = Pattern.compile("(\\w+ map:\\n(?>\\d+(?> |\\n|))+)");

    @Override
    public void solve(List<String> input) {
        String compiledInput = input.stream()
                .map(s -> s.concat("\n"))
                .reduce(String::concat)
                .orElse("");

        List<Long> seeds = extractSeeds(compiledInput); //works //part 1
        List<String> maps = extractMaps(compiledInput);
        List<Map<Range, Range>> mappingList = prepareMappings(maps);

        for(Map<Range, Range> mapping : mappingList) {
            seeds = applyMappings(seeds, mapping);
        }

        lap(seeds.stream().mapToLong(value -> value).min().getAsLong());

        //part 2
        List<Range> seedRanges = extractSeedRanges(compiledInput);

        for(long i = 0;; i++) {
            if(locationMapsToSeed(i, seedRanges, mappingList)) {
                lap(i);
                break;
            }
        }

    }

    private boolean locationMapsToSeed(long destination, List<Range> seedRanges, List<Map<Range, Range>> mappingList) {
        List<Long> destinations = List.of(destination);
        for(int i = mappingList.size() - 1; i >= 0; i--) {
            Map<Range, Range> mapping = mappingList.get(i);
            destinations = applyReverseMappings(destinations, mapping);
        }

        for (Range seedRange : seedRanges) {
            for(long location : destinations) {
                if(seedRange.inRange(location))
                    return true;
            }
        }

        return false;
    }

    private List<Long> applyReverseMappings(List<Long> destinationLocations, Map<Range, Range> mappings) { //mappings are source -> destination - so we need to check them backwards
        List<Long> seeds = new ArrayList<>();
        for(Long location : destinationLocations) {
            Optional<Range> destinationRange = mappings.values().stream()
                    .filter(range -> range.inRange(location))
                    .findFirst();
            long seed = location;
            if(destinationRange.isPresent()) {
                Range destination = destinationRange.get();
                long offset = destination.getOffset(location);
                seed = mappings.entrySet()
                        .stream()
                        .filter(entry -> destination.equals(entry.getValue()))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .get()
                        .start()
                        + offset;
            }
            seeds.add(seed);
        }
        return seeds;
    }

    private List<Map<Range, Range>> prepareMappings(List<String> maps) {
        return maps.stream()
                .map(this::getMappings)
                .toList();
    }

    private List<Long> applyMappings(List<Long> initialSeeds, Map<Range, Range> mappings) {
        List<Long> locations = new ArrayList<>();
        for(Long seed : initialSeeds) {
            Optional<Range> optionalRange = mappings.keySet().stream()
                    .filter(range -> range.inRange(seed))
                    .findFirst();
            long destination = seed;
            if(optionalRange.isPresent()) {
                Range source = optionalRange.get();
                long offset = source.getOffset(seed);
                destination = mappings.get(source).start() + offset;
            }
            locations.add(destination);
        }
        return locations;
    }

    private List<String> extractMaps(String input) {
        List<String> maps = new ArrayList<>();
        Matcher mapMatcher = MAP_CAPTURING_PATTERN.matcher(input);
        while(mapMatcher.find()) {
            String map = mapMatcher.group();
            maps.add(map);
        }
        return maps;
    }

    private Map<Range, Range> getMappings(String map) {
        Map<Range, Range> mappings = new HashMap<>();

        List<String> ranges = Arrays.stream(map.split("\n")).filter(s -> !s.contains("map:")).toList();
        for(String line : ranges) {
            Matcher numberMatcher = NUMBER_PATTERN.matcher(line);
            numberMatcher.find();
            long destinationStart = Long.parseLong(numberMatcher.group());
            numberMatcher.find();
            long sourceStart = Long.parseLong(numberMatcher.group());
            numberMatcher.find();
            long rangeLength = Long.parseLong(numberMatcher.group());
            Range sourceRange = Range.of(sourceStart, rangeLength);
            Range destinationRange = Range.of(destinationStart, rangeLength);
            mappings.put(sourceRange, destinationRange);
        }

        return mappings;
    }

    private List<Long> extractSeeds(String input) {
        List<Long> seeds = new ArrayList<>();
        Matcher seedMatcher = STARTING_SEEDS_PATTERN.matcher(input);

        while (seedMatcher.find()) {
            String seedLine = seedMatcher.group();
            Matcher numberMatcher = NUMBER_PATTERN.matcher(seedLine);

            while(numberMatcher.find())
                seeds.add(Long.parseLong(numberMatcher.group()));

        }
        return seeds;
    }

    private List<Range> extractSeedRanges(String input) {
        List<Range> seedRanges = new ArrayList<>();
        Matcher seedMatcher = STARTING_SEEDS_PATTERN.matcher(input);

        while (seedMatcher.find()) {
            String seedLine = seedMatcher.group();
            Matcher seedRangeMatcher = SEED_RANGE_PATTERN.matcher(seedLine);

            while(seedRangeMatcher.find()) {
                long start = Long.parseLong(seedRangeMatcher.group(1));
                long length = Long.parseLong(seedRangeMatcher.group(2));
                Range seedRange = Range.of(start, length);
                seedRanges.add(seedRange);
            }
        }

        return seedRanges;
    }
}

