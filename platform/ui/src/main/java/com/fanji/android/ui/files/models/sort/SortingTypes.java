package com.fanji.android.ui.files.models.sort;

import java.util.Comparator;

import com.fanji.android.ui.files.models.Document;

/**
 * Created by jiangshide on 10/2/17.
 */
public enum SortingTypes {
    name(new NameComparator()), none(null);

    final private Comparator<Document> comparator;

    SortingTypes(Comparator<Document> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Document> getComparator() {
        return comparator;
    }
}
