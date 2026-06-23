package com.example.jobsapp.ui.jobs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobsapp.databinding.FragmentJobsBinding

class JobsFragment : Fragment() {

    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: JobsViewModel by viewModels()
    private val adapter = JobsAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerJobs.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerJobs.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadJobs() }

        viewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            adapter.submitList(jobs)
            binding.tvEmpty.visibility = if (jobs.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.swipeRefresh.isRefreshing = loading
            if (loading) binding.tvEmpty.text = "Loading jobs…"
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.tvEmpty.text = error
                binding.tvEmpty.visibility = View.VISIBLE
            }
        }

        viewModel.loadJobs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
