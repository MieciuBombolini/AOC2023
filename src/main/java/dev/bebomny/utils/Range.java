package dev.bebomny.utils;

import java.util.regex.Pattern;

public record Range(long start, long end) {

    //Already corrects with -1
    public static Range of(long start, long length) {
        return new Range(start, start + length - 1);
    }

    public boolean inRange(long location) {
        return location >= start && location <= end;
    }

    //this should not be used when not in range
    public long getOffset(long location) {
        if (inRange(location))
            return location - start;
        throw new QuackYouException("You need to be in range to calculate the offset correctly");
    }

    @Override
    public int hashCode() {
        //*Borrowed* from somewhere IDK what this changes really
        //I know that this Overrides the hash method used in hashmaps
        //but what this changes in the solution that I know not, but It works?
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (start ^ (start >>> 32));
        result = prime * result + (int) (end ^ (end >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Range{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
