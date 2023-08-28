package com.fanji.android.cache.sourcestorage;

import com.fanji.android.cache.SourceInfo;

/**
 * Storage for {@link SourceInfo}.
 *
 * @author Alexey Danilov (jiangshide8@gmail.com).
 */
public interface SourceInfoStorage {

    SourceInfo get(String url);

    void put(String url, SourceInfo sourceInfo);

    void release();
}
