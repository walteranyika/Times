package sacco.times.attendance.models;

import java.util.Comparator;

/**
 * Created by walter on 1/4/19.
 */

public class CustomComparator implements Comparator<Item> {
    @Override
    public int compare(Item o1, Item o2) {
        return o1.getLoginDate().compareTo(o2.getLoginDate());
    }
}
