package org;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        final int intervalSize = 25;

        List<Thread> threads = new ArrayList<>();
        List<Integer> maxIntervalValues = new ArrayList<>();

        String[] texts = new String[intervalSize];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time

        for (String text : texts) {
            FutureTask<Integer> valuesIntervalTask = new FutureTask<>(() -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);

                return maxSize;
            });
            Thread valuesIntervalThread = new Thread(valuesIntervalTask);

            valuesIntervalThread.start();
            maxIntervalValues.add(valuesIntervalTask.get());
            threads.add(valuesIntervalThread);
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
        System.out.println("Maximum interval value: " + Collections.max(maxIntervalValues));
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}