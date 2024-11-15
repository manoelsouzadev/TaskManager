package br.edu.ifsp.scl.moviesmanager.ui.task

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.scl.moviesmanager.R
import br.edu.ifsp.scl.moviesmanager.databinding.FragmentTaskBinding
import br.edu.ifsp.scl.moviesmanager.viewmodel.TaskViewModel
import com.google.android.material.snackbar.Snackbar


class TaskFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTaskBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        adapter = TaskAdapter(TaskClickListener { taskEntry ->
            findNavController().navigate(TaskFragmentDirections.actionTaskFragmentToUpdateFragment(taskEntry))
        })

        viewModel.getAllTasks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.apply {

            binding.recyclerView.adapter = adapter

            floatingActionButton.setOnClickListener() {
                findNavController().navigate(R.id.action_addFragment_to_taskFragment)
            }
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback ( 0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val taskEntry = adapter.getTaskEntryAt(position)
                if (taskEntry != null) {
                    viewModel.delete(taskEntry)
                }
                Snackbar.make(binding.root, "Deleted!", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        if (taskEntry != null) {
                            viewModel.insert(taskEntry)
                        }
                    }

                    show()
                }

            }

        }).attachToRecyclerView(binding.recyclerView)

        setHasOptionsMenu(true)
        hideKeyboard(requireActivity())
        return binding.root
    }

    private fun hideKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = activity.currentFocus
        currentFocusedView.let {
            inputMethodManager.hideSoftInputFromWindow(
                currentFocusedView?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.task_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    runQuery(newText)
                }
                return true
            }
        })
    }

    fun runQuery(query: String) {
        val searchQuery = "%$query%"
        viewModel.searchDatabase (searchQuery). observe (viewLifecycleOwner, Observer { tasks ->
            adapter.submitList (tasks)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_priority -> viewModel.getAllPriotityTasks.observe (viewLifecycleOwner, Observer { tasks ->
            adapter.submitList (tasks)
        })
            R.id.action_delete_all -> deleteAllItem()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllItem() {
        AlertDialog.Builder(requireContext()).setTitle("Delete All").setTitle("Are you sure?").setPositiveButton("yes") { dialog, _ ->
            viewModel.deleteAll()
            dialog.dismiss()
        }.setNegativeButton("No"){ dialog, _ ->
            dialog.dismiss()
        }.create().show()
    }
}