package com.rightapps.camprompter.utils.views

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

abstract class BoundActivity<ViewBindingType : ViewBinding> : AppCompatActivity(),
    LifecycleObserver, DefaultLifecycleObserver {

    companion object {
        private const val TAG = "BoundActivity"
    }

    // Variables
    private var _binding: ViewBindingType? = null

    // Binding variable to be used for accessing views.
    protected val binding
        get() = requireNotNull(_binding)

    /*
     * Calls the abstract function to return the ViewBinding and set up LifeCycle Observer to get
     * rid of binding once done.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        _binding = setupViewBinding(layoutInflater)
        setContentView(requireNotNull(_binding).root)
        lifecycle.addObserver(this)
        init()
    }

    abstract fun init()

    abstract fun setupViewBinding(inflater: LayoutInflater): ViewBindingType

    // Clears the binding and removes the observer when the activity is destroyed.
    override fun onDestroy(owner: LifecycleOwner) {
        _binding = null
        lifecycle.removeObserver(this)
        super<AppCompatActivity>.onDestroy()
    }

    /*
     * Safe call method, just in case, if anything is messed up and lifecycle Event does not gets
     * called.
     */
    override fun onDestroy() {
        _binding = null
        super<AppCompatActivity>.onDestroy()
    }
}