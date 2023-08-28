package com.fanji.android.cache.sourcestorage;

import com.fanji.android.cache.SourceInfo;

/**
 * {@link SourceInfoStorage} that does nothing.
 *
 * @author Alexey Danilov (jiangshide8@gmail.com).
 */
public class NoSourceInfoStorage implements SourceInfoStorage {

    @Override
    public SourceInfo get(String url) {
        return null;
    }

    @Override
    public void put(String url, SourceInfo sourceInfo) {
    }

    @Override
    public void release() {
    }
}
