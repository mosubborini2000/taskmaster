package com.taskmaster.myapplication.activity.adapter;

import static com.taskmaster.myapplication.activity.MainActivity.TASK_BODY_TAG;
import static com.taskmaster.myapplication.activity.MainActivity.TASK_STATE_TAG;
import static com.taskmaster.myapplication.activity.MainActivity.TASK_Title_TAG;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Task;
import com.taskmaster.myapplication.R;
import com.taskmaster.myapplication.activity.TaskDetailsActivity;


import java.util.List;

public class TaskListRecyclerVIewAdapter extends RecyclerView.Adapter<TaskListRecyclerVIewAdapter.TaskListViewHolder>{
    List<Task> tasks;
    Context callingActivity;

    public TaskListRecyclerVIewAdapter(List<Task> tasks, Context callingActivity) {
        this.tasks = tasks;
        this.callingActivity = callingActivity;
    }

    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View taskFragment = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task_list, parent,false);
        return new TaskListViewHolder(taskFragment);
    }



    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {
        TextView taskFragmentTextView = (TextView) holder.itemView.findViewById(R.id.taskFragmentTextViewHome);
        taskFragmentTextView.setText(tasks.get(position).getTitle());
        String taskTitle = tasks.get(position).getTitle();
        String taskBody = tasks.get(position).getBody();
        String taskState = tasks.get(position).getState().toString();


        View taskViewHolder = holder.itemView;
        taskViewHolder.setOnClickListener(view -> {
            Intent goToViewTaskFormIntent = new Intent(callingActivity, TaskDetailsActivity.class);
            goToViewTaskFormIntent.putExtra(TASK_BODY_TAG, taskBody);
            goToViewTaskFormIntent.putExtra(TASK_Title_TAG, taskTitle);
            goToViewTaskFormIntent.putExtra(TASK_STATE_TAG, taskState);
            callingActivity.startActivity(goToViewTaskFormIntent);
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskListViewHolder extends RecyclerView.ViewHolder {
        public TaskListViewHolder(View fragmentItemView) {
            super(fragmentItemView);
        }

    }
}
