package com.rightapps.camprompter.utils.views

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.viewbinding.ViewBinding

abstract class BoundActivity<ViewBindingType : ViewBinding> : AppCompatActivity(),
    LifecycleObserver {

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


//    /*
//     * Safe call method, just in case, if anything is messed up and lifecycle Event does not gets
//     * called.
//     */
//    // Clears the binding and removes the observer when the activity is destroyed.
//    override fun onDestroy() {
//        lifecycle.removeObserver(this)
//        _binding = null
//        super.onDestroy()
//    }
}