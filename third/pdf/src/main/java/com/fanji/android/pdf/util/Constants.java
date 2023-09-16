package com.fanji.android.pdf.util;

/**
 * @author: jiangshide
 * @date: 2023/8/20
 * @email: 18311271399@163.com
 * @description:
 */
public interface Constants {

    static final boolean DEBUG_MODE = false;

    /** Size of the minimum, in percent of the component size */
    static final float MINIMAP_MAX_SIZE = 200f;

    /** Number of pages loaded (default 3) */
    static final int LOADED_SIZE = 3;

    /** Between 0 and 1, the thumbnails quality (default 0.2) */
    static final float THUMBNAIL_RATIO = 0.2f;

    /**
     * The size of the rendered parts (default 256)
     * Tinier : a little bit slower to have the whole page rendered but more reactive.
     * Bigger : user will have to wait longer to have the first visual results
     */
    static final float PART_SIZE = 256;

    /** Transparency of masks around the main page (between 0 and 255, default 50) */
    static final int MASK_ALPHA = 20;

    /** The size of the grid of loaded images around the current point */
    static final int GRID_SIZE = 7;

    public interface Cache {

        /** The size of the cache (number of bitmaps kept) */
        static final int CACHE_SIZE = (int) Math.pow(GRID_SIZE, 2d);

        static final int THUMBNAILS_CACHE_SIZE = 4;
    }

    public interface Pinch {

        static final float MAXIMUM_ZOOM = 10;

        static final float MINIMUM_ZOOM = 1;

        /**
         * A move must be quicker than this duration and longer than
         * this distance to be considered as a quick move
         */
        static final int QUICK_MOVE_THRESHOLD_TIME = 250, //

        QUICK_MOVE_THRESHOLD_DISTANCE = 50;

    }

}
