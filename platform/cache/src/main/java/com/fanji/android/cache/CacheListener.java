package com.fanji.android.cache;

import java.io.File;

/**
 * Listener for cache availability.
 *
 * @author Alexey Danilov (jiangshide8@gmail.com).
 */
public interface CacheListener {

    void onCacheAvailable(File cacheFile, String url, int percentsAvailable);
}
