package com.example.jobsapp.ui.jobs

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jobsapp.databinding.ItemJobBinding
import com.example.jobsapp.model.Job

class JobsAdapter(private var jobs: List<Job>) :
    RecyclerView.Adapter<JobsAdapter.JobViewHolder>() {

    fun submitList(newJobs: List<Job>) {
        jobs = newJobs
        notifyDataSetChanged()
    }

    inner class JobViewHolder(val binding: ItemJobBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobs[position]
        holder.binding.tvPosition.text = job.position ?: "Untitled role"
        holder.binding.tvCompany.text = job.company ?: "Unknown company"
        holder.binding.tvLocation.text = job.location?.takeIf { it.isNotBlank() } ?: "Remote"
        holder.binding.tvTags.text = job.tags?.joinToString(" · ") ?: ""

        holder.binding.root.setOnClickListener {
            val url = job.url
            if (!url.isNullOrBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                holder.binding.root.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = jobs.size
}
