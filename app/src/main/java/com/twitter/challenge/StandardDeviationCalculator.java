package com.twitter.challenge;

import java.util.List;

public class StandardDeviationCalculator {

    public static float standardDeviation(List<Float> dataPoints) {

        float total = 0;
        for (float point : dataPoints){
            total += point;
        }

        float average = total/dataPoints.size();

        double summation = 0;
        for (float point : dataPoints){
            summation += Math.pow((point - average), 2);
        }

        return (float)Math.sqrt(summation/(dataPoints.size() - 1));
    }
}
