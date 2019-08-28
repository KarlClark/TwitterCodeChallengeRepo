package com.twitter.challenge;

import org.assertj.core.data.Offset;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.within;

public class StandardDeviationCalculatorTests {

    @Test
    public void testStandardDeviationCalculator() {
        ArrayList<Float> points;
        Float[] a1 = {4f, 4f, 4f, 4f, 4f};
        Float[] a2 ={0.15f, -0.09f, 0.10f, 0.06f};
        Float[] a3 ={5f, 7f, 3f, 7f};
        Float[] a4 ={9f, 2f, 5f, 4f, 12f, 7f, 8f, 11f, 9f, 3f, 7f, 4f, 12f, 5f, 4f, 10f, 9f, 6f, 9f, 4f};
        final Offset<Float> precision = within(0.001f);

        points = new ArrayList<Float>(Arrays.asList(a1));
        assertThat(StandardDeviationCalculator.standardDeviation(points)).isEqualTo(0, precision);

        points = new ArrayList<Float>(Arrays.asList(a2));
        assertThat(StandardDeviationCalculator.standardDeviation(points)).isEqualTo(0.1034f, precision);

        points = new ArrayList<Float>(Arrays.asList(a3));
        assertThat(StandardDeviationCalculator.standardDeviation(points)).isEqualTo(1.915f, precision);

        points = new ArrayList<Float>(Arrays.asList(a4));
        assertThat(StandardDeviationCalculator.standardDeviation(points)).isEqualTo(3.061f, precision);
    }


}


