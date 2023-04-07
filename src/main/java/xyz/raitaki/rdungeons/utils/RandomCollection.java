package xyz.raitaki.rdungeons.utils;

import lombok.Getter;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {
    @Getter private final NavigableMap<Double, E> map = new TreeMap<>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public E next() {
        double value = random.nextDouble() * total;
        if(map.higherEntry(value) == null)
            return map.get(map.lastKey());
        return map.higherEntry(value).getValue();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }
}