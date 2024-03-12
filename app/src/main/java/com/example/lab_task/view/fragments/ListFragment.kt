package com.example.lab_task.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab_task.adapters.TagAdapter
import com.example.lab_task.databinding.FragmentListBinding
import com.example.lab_task.viewmodel.ListViewModel

class ListFragment : Fragment(), TagAdapter.OnAdapterActionListener {
    private lateinit var binding: FragmentListBinding
    private lateinit var viewModel: ListViewModel
    private lateinit var adapter: TagAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TagAdapter(ArrayList(), this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.tagsForDisplay.observe(viewLifecycleOwner){
            adapter.data = it
            adapter.notifyDataSetChanged()
        }

        viewModel.currUsername.observe(viewLifecycleOwner){
            adapter.currUsername = it
            adapter.notifyDataSetChanged()
        }
    }

    override fun onLikeClick(tagId: String, isLiked: Boolean) {
        viewModel.changeLike(tagId, isLiked)
    }

    override fun onDeleteClick(tadId: String) {
        viewModel.deleteTag(tadId)
    }

    override fun onSubscribeClick(tagId: String) {

    }


}