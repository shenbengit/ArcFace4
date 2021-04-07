package com.shencoder.arcface.util;

import androidx.annotation.NonNull;

import java.util.HashMap;

/**
 * @author ShenBen
 * @date 2021/03/25 14:45
 * @email 714081644@qq.com
 */
public class AspectRatio implements Comparable<AspectRatio> {

    private final static HashMap<String, AspectRatio> sCache = new HashMap<>(16);

    private final int mX;
    private final int mY;

    private AspectRatio(int x, int y) {
        mX = x;
        mY = y;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    @NonNull
    public static AspectRatio of(int x, int y) {
        int gcd = gcd(x, y);
        if (gcd > 0) {
            x /= gcd;
        }
        if (gcd > 0) {
            y /= gcd;
        }
        String key = x + ":" + y;
        AspectRatio cached = sCache.get(key);
        if (cached == null) {
            cached = new AspectRatio(x, y);
            sCache.put(key, cached);
        }
        return cached;
    }


    @NonNull
    public static AspectRatio parse(@NonNull String string) {
        String[] parts = string.split(":");
        if (parts.length != 2) {
            throw new NumberFormatException("Illegal AspectRatio string. Must be x:y");
        }
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        return of(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof AspectRatio) {
            return toFloat() == ((AspectRatio) o).toFloat();
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return mX + ":" + mY;
    }

    public float toFloat() {
        return (float) mX / mY;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(toFloat());
    }

    @Override
    public int compareTo(@NonNull AspectRatio another) {
        return Float.compare(toFloat(), another.toFloat());
    }

    private static int gcd(int a, int b) {
        while (b != 0) {
            int c = b;
            b = a % b;
            a = c;
        }
        return a;
    }
}
