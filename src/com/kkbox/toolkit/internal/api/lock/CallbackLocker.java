package com.kkbox.toolkit.internal.api.lock;

/**
 * Created by williamwang on 2016/3/30.
 */
public interface CallbackLocker {

	void registerUnlockCallback(UnlockListener unlockListener);

	void triggerToUnlock();

}
