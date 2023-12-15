package com.rightapps.camprompter.utils.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.viewbinding.ViewBinding

abstract class BoundFragment<ViewBindingType : ViewBinding> : Fragment(), LifecycleObserver,
    DefaultLifecycleObserver {

    companion object {
        private const val TAG = "BoundFragment"
    }

    // Variables
    private var _binding: ViewBindingType? = null

    // Binding variable to be used for accessing views.
    protected val binding
        get() = requireNotNull(_binding)

    // Calls the abstract function to return the ViewBinding.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = setupViewBinding(inflater, container)
        return requireNotNull(_binding).root
    }

    // Set up the LifeCycle observer to get rid of binding once done.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(this)
        onViewCreate()
    }

    abstract fun setupViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
    ): ViewBindingType

    abstract fun onViewCreate()

    // Clears the binding and removes the observer when the Fragment's views get destroyed.
    override fun onDestroy() {
        _binding = null
        viewLifecycleOwner.lifecycle.removeObserver(this)
        super<Fragment>.onDestroy()
    }

    /*
     * Safe call method, just in case, if anything is messed up and lifecycle Event does not gets
     * called.
     */
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}