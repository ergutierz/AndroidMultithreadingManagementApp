package com.example.ammf_core.lifecyclemanagement

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Interface for lifecycle-aware operations within the AMMF (Android Multithreading Management Framework).
 *
 * This interface extends DefaultLifecycleObserver and provides methods to bind and unbind
 * AMMF's functionalities to a LifecycleOwner, such as an Activity or Fragment. Implementing this interface
 * allows the AMMF to be aware of and respond appropriately to Android lifecycle events, ensuring efficient
 * resource management and operation handling in line with the lifecycle state of the host component.
 *
 * Implementers of this interface can override lifecycle event methods to perform specific actions
 * when a lifecycle event occurs.
 */
interface LifecycleAwareManager : DefaultLifecycleObserver {

    /**
     * Binds the AMMF framework operations to a specific LifecycleOwner.
     *
     * This method should be called to associate the AMMF's lifecycle-aware components
     * with a LifecycleOwner, typically in the onCreate() method of an Activity or Fragment.
     *
     * @param owner The LifecycleOwner (such as an Activity or Fragment) to bind to.
     */
    fun bindToLifecycle(owner: LifecycleOwner)

    /**
     * Unbinds the AMMF framework operations from the currently bound LifecycleOwner.
     *
     * This method should be called to dissociate the AMMF's lifecycle-aware components
     * from a LifecycleOwner, typically in the onDestroy() method of an Activity or Fragment.
     *
     * @param owner The LifecycleOwner to unbind from.
     */
    fun unbindFromLifecycle(owner: LifecycleOwner)
}
