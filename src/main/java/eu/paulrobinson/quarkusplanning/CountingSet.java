package eu.paulrobinson.quarkusplanning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CountingSet<E> extends HashSet<E> {

    private Map<Object, Integer> occurancesMap = new HashMap<>();

    @Override
    public boolean add(E o) {

        Integer occurances = occurancesMap.get(o);
        if (occurances == null)
        {
            occurances = 0;
        }
        occurances++;
        occurancesMap.put(o, occurances);

        return super.add(o);
    }

    public int getOccurances(E item) {
        return occurancesMap.get(item);
    }
}
