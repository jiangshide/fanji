package com.fanji.android.cache.sourcestorage;

import android.content.Context;

/**
 * Simple factory for {@link SourceInfoStorage}.
 *
 * @author Alexey Danilov (jiangshide8@gmail.com).
 */
public class SourceInfoStorageFactory {

    public static SourceInfoStorage newSourceInfoStorage(Context context) {
        return new DatabaseSourceInfoStorage(context);
    }

    public static SourceInfoStorage newEmptySourceInfoStorage() {
        return new NoSourceInfoStorage();
    }
}
