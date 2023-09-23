package com.fanji.android.files.models.sort;

import java.util.Comparator;

import com.fanji.android.files.models.Document;
import com.fanji.android.util.data.FileData;

/**
 * Created by gabriel on 10/2/17.
 */

public class NameComparator implements Comparator<FileData> {

    protected NameComparator() { }

    @Override
    public int compare(FileData o1, FileData o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
    }
}
