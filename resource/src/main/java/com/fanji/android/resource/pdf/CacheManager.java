package com.fanji.android.resource.pdf;

import static com.fanji.android.resource.pdf.util.Constants.Cache.CACHE_SIZE;
import static com.fanji.android.resource.pdf.util.Constants.Cache.THUMBNAILS_CACHE_SIZE;

import android.graphics.RectF;

import com.fanji.android.resource.pdf.model.PagePart;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * @author: jiangshide
 * @date: 2023/8/20
 * @email: 18311271399@163.com
 * @description:
 */
class CacheManager {

    private PriorityQueue<PagePart> passiveCache;

    private PriorityQueue<PagePart> activeCache;

    private Vector<PagePart> thumbnails;

    public CacheManager() {
        activeCache = new PriorityQueue<PagePart>(CACHE_SIZE, new PagePartComparator());
        passiveCache = new PriorityQueue<PagePart>(CACHE_SIZE, new PagePartComparator());
        thumbnails = new Vector<PagePart>();
    }

    public void cachePart(PagePart part) {

        // If cache too big, remove and recycle
        makeAFreeSpace();

        // Then add part
        activeCache.offer(part);

    }

    public void makeANewSet() {
        passiveCache.addAll(activeCache);
        activeCache.clear();
    }

    private void makeAFreeSpace() {

        while ((activeCache.size() + passiveCache.size()) >= CACHE_SIZE &&
                !passiveCache.isEmpty()) {
            passiveCache.poll().getRenderedBitmap().recycle();
        }

        while ((activeCache.size() + passiveCache.size()) >= CACHE_SIZE &&
                !activeCache.isEmpty()) {
            activeCache.poll().getRenderedBitmap().recycle();
        }
    }

    public void cacheThumbnail(PagePart part) {

        // If cache too big, remove and recycle
        if (thumbnails.size() >= THUMBNAILS_CACHE_SIZE) {
            thumbnails.remove(0).getRenderedBitmap().recycle();
        }

        // Then add thumbnail
        thumbnails.add(part);

    }

    public boolean upPartIfContained(int userPage, int page, float width, float height, RectF pageRelativeBounds, int toOrder) {
        PagePart fakePart = new PagePart(userPage, page, null, width, height, pageRelativeBounds, false, 0);

        PagePart found;
        if ((found = find(passiveCache, fakePart)) != null) {
            passiveCache.remove(found);
            found.setCacheOrder(toOrder);
            activeCache.offer(found);
            return true;
        }

        return find(activeCache, fakePart) != null;
    }

    /** Return true if already contains the described PagePart */
    public boolean containsThumbnail(int userPage, int page, float width, float height, RectF pageRelativeBounds) {
        PagePart fakePart = new PagePart(userPage, page, null, width, height, pageRelativeBounds, true, 0);
        for (PagePart part : thumbnails) {
            if (part.equals(fakePart)) {
                return true;
            }
        }
        return false;
    }

    private PagePart find(PriorityQueue<PagePart> vector, PagePart fakePart) {
        for (PagePart part : vector) {
            if (part.equals(fakePart)) {
                return part;
            }
        }
        return null;
    }

    public Vector<PagePart> getPageParts() {
        Vector<PagePart> parts = new Vector<PagePart>(passiveCache);
        parts.addAll(activeCache);
        return parts;
    }

    public Vector<PagePart> getThumbnails() {
        return thumbnails;
    }

    public void recycle() {
        for (PagePart part : activeCache) {
            part.getRenderedBitmap().recycle();
        }
        for (PagePart part : activeCache) {
            part.getRenderedBitmap().recycle();
        }
        for (PagePart part : thumbnails) {
            part.getRenderedBitmap().recycle();
        }
        passiveCache.clear();
        activeCache.clear();
        thumbnails.clear();
    }

    class PagePartComparator implements Comparator<PagePart> {
        @Override
        public int compare(PagePart part1, PagePart part2) {
            if (part1.getCacheOrder() == part2.getCacheOrder()) {
                return 0;
            }
            return part1.getCacheOrder() > part2.getCacheOrder() ? 1 : -1;
        }
    }

}
