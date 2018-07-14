package com.virjar.vscrawler.core.util;

/**
 * An interface that can receive Update operations that are applied to a list.
 * <p>
 * This class can be used together with DiffUtil to detect changes between two lists.
 */
public interface ListUpdateCallback {
    /**
     * Called when {@code count} number of items are inserted at the given position.
     *
     * @param position The position of the new item.
     * @param count    The number of items that have been added.
     */
    void onInserted(int position, int count);

    /**
     * Called when {@code count} number of items are removed from the given position.
     *
     * @param position The position of the item which has been removed.
     * @param count    The number of items which have been removed.
     */
    void onRemoved(int position, int count);

    /**
     * Called when an item changes its position in the list.
     *
     * @param fromPosition The previous position of the item before the move.
     * @param toPosition   The new position of the item.
     */
    void onMoved(int fromPosition, int toPosition);

    /**
     * Called when {@code count} number of items are updated at the given position.
     *
     * @param position The position of the item which has been updated.
     * @param count    The number of items which has changed.
     */
    void onChanged(int position, int count, Object payload);
}
