package com.holland.kit.base;

public class Shuffle {

    public static void main(String[] args) {
        String s = "com.holland.kit.base";
        String shuffle = shuffle(s.toCharArray(), new int[]{0, 7, 2, 8, 4, 5});
        System.out.println(shuffle);
    }

    public static String shuffle(char[] chars, int[] indexes) {
        for (int i = 0; i < indexes.length; i += 2) {
            chars[indexes[i]] += chars[indexes[i + 1]];
            chars[indexes[i + 1]] = (char) (chars[indexes[i]] - chars[indexes[i + 1]]);
            chars[indexes[i]] = (char) (chars[indexes[i]] - chars[indexes[i + 1]]);
        }
        return String.valueOf(chars);
    }
}
