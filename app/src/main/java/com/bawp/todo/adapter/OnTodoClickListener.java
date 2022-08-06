package com.bawp.todo.adapter;

import com.bawp.todo.model.Task;

public interface OnTodoClickListener {
    void onTodoClick(Task task);
    void onTodoRadioButtonClick(Task task);
}
