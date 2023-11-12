package com.jfennelly.brain.network.utils;

import java.util.List;
import java.util.Random;

public class RandomUtils {
    public static int randInt(int min, int max) {
        if (min > max) {
            int tmp = min;
            min = max;
            max = tmp;
        }

        return new Random().nextInt((max - min) + 1) + min;
    }

    public static <T> T getRandomItem(List<T> list) {
        return list.get(RandomUtils.randInt(0, list.size() - 1));
    }

    public static <T> T getRandomItemNonRepeat(List<T> list, T exclude) {
        int randInt = randInt(0, list.size() - 1);
        if(list.get(randInt).equals(exclude)) {
            return list.get((randInt + 1) % list.size());
        }
        return list.get(randInt);
    }
}
