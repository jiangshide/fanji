package com.fanji.android.cache.file;

import java.io.File;
import java.io.IOException;

/**
 * Declares how {@link FileCache} will use disc space.
 *
 * @author Alexey Danilov (jiangshide8@gmail.com).
 */
public interface DiskUsage {

    void touch(File file) throws IOException;

}
