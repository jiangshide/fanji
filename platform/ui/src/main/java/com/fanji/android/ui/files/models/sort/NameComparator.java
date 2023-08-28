package com.fanji.android.ui.files.models.sort;

import java.util.Comparator;

import com.fanji.android.ui.files.models.Document;

/**
 * Created by jiangshide on 10/2/17.
 */
public class NameComparator implements Comparator<Document> {

    protected NameComparator() { }

    @Override
    public int compare(Document o1, Document o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
    }
}
