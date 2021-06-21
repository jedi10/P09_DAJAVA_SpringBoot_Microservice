package com.mediscreen.ui.tool;

import java.util.Random;

public class Snippets {

    public static int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.ints(min, (max + 1)).findFirst().getAsInt();
        //https://mkyong.com/java/java-generate-random-integers-in-a-range/
    }
}
