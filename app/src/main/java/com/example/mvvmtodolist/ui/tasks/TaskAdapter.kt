package com.example.mvvmtodolist.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmtodolist.data.Task
import com.example.mvvmtodolist.databinding.ItemTaskBinding


class TaskAdapter(private val listener:OnItemClickListener) : androidx.recyclerview.widget.ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback()) {



  inner class TaskViewHolder(private val binding : ItemTaskBinding) : RecyclerView.ViewHolder(binding.root){
        //inner class is equivalent to static class in java
        init {
            binding.apply {
                root.setOnClickListener{
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        val task = getItem(position)
                        listener.onItemClick(task)
                    }
                }
                checkBoxCompleted.setOnClickListener{
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        val task = getItem(position)
                        listener.onCheckBoxClick(task,checkBoxCompleted.isChecked)
                    }
                }

            }
        }

        fun bind(task : Task) {
            binding.apply {
                checkBoxCompleted.isChecked = task.completed
                textViewName.text = task.name
                textViewName.paint.isStrikeThruText = task.completed
                labelPriority.isVisible = task.important

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    interface OnItemClickListener{
        fun onItemClick(task: Task)
        fun onCheckBoxClick(task: Task,isChecked :Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
           return  oldItem == newItem
        }
    }
}