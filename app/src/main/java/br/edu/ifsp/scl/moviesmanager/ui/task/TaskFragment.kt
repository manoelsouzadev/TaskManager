package br.edu.ifsp.scl.moviesmanager.ui.task

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
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

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.task_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
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