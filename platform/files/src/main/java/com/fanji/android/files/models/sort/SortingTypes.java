package com.fanji.android.files.models.sort;

import java.util.Comparator;

import com.fanji.android.files.models.Document;
import com.fanji.android.util.data.FileData;

/**
 * Created by gabriel on 10/2/17.
 */

public enum SortingTypes {
    name(new NameComparator()), none(null);

    final private Comparator<FileData> comparator;

    SortingTypes(Comparator<FileData> comparator) {
        this.comparator = comparator;
    }

    public Comparator<FileData> getComparator() {
        return comparator;
    }
}
