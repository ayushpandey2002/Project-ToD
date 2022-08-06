package com.bawp.todo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import com.bawp.todo.model.Priority;
import com.bawp.todo.model.SharedViewModel;
import com.bawp.todo.model.Task;
import com.bawp.todo.model.TaskViewModel;
import com.bawp.todo.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    public BottomSheetFragment(){
    }
    private EditText enterTodo;
    private ImageButton calenderButton;
    private ImageButton priorityButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedRadioButton;
    private int selectButtonId;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calenderGroup;
    private Date dueDate;
    Calendar calendar = Calendar.getInstance();
    private SharedViewModel sharedViewModel;
    private boolean isEdit;
    private Priority priority;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view=inflater.inflate(R.layout.bottom_sheet, container, false);
        calenderGroup=view.findViewById(R.id.calendar_group);
        calendarView=view.findViewById(R.id.calendar_view);
        calenderButton =view.findViewById(R.id.today_calendar_button);
        enterTodo = view.findViewById(R.id.enter_todo_et);
        saveButton =view.findViewById(R.id.save_todo_button);
        priorityButton=view.findViewById(R.id.priority_todo_button);

        priorityRadioGroup=view.findViewById(R.id.radioGroup_priority);

        Chip todayChip =view.findViewById(R.id.today_chip);
        todayChip.setOnClickListener(this);
        Chip tomorrowChip =view.findViewById(R.id.tomorrow_chip);
        tomorrowChip.setOnClickListener(this);
        Chip nextWeekChip =view.findViewById(R.id.next_week_chip);
        nextWeekChip.setOnClickListener(this);

        return view;
    }
@Override
public void onResume() {
    super.onResume();
    if (sharedViewModel.getSelectedItem().getValue() !=null) {
        isEdit = sharedViewModel.getIsEdit();
        Task task = sharedViewModel.getSelectedItem().getValue();
        enterTodo.setText(task.getTask());
        priority=task.getPriority();
        dueDate=task.getDueDate();

    }
    else {
        enterTodo.setText("");
        isEdit = sharedViewModel.getIsEdit();


    }
}


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel=new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);



        calenderButton.setOnClickListener(view12 -> {
         calenderGroup.setVisibility(
                 calenderGroup.getVisibility()==View.GONE ? View.VISIBLE:View.GONE
         );
            Utils.hideSoftKeyboard(view12);
        });
        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMoth) -> {
            calendar.clear();
            calendar.set(year,month,dayOfMoth);
            dueDate=calendar.getTime();
        });
        priorityButton.setOnClickListener(view13->{
            Utils.hideSoftKeyboard(view13);
            priorityRadioGroup.setVisibility(
                    priorityRadioGroup.getVisibility() == View.GONE ? View.VISIBLE: View.GONE
            );
            priorityRadioGroup.setOnCheckedChangeListener((radioGroup,checkedId)->{
              if (priorityRadioGroup.getVisibility() == View.VISIBLE){
                  selectButtonId=checkedId;
                  selectedRadioButton=view.findViewById(selectButtonId);
                  if (selectedRadioButton.getId()==R.id.radioButton_high){
                      priority =Priority.HIGH;
                  }
                 else if (selectedRadioButton.getId()==R.id.radioButton_med){
                      priority =Priority.MEDIUM;
                  }
                  else if (selectedRadioButton.getId()==R.id.radioButton_low){
                      priority =Priority.LOW;
                  }
                  else {
                      priority=Priority.LOW;
                  }
              }
              else {
                  priority =Priority.LOW;
              }
            });
        });


        saveButton.setOnClickListener(view1 ->{

            String task=enterTodo.getText().toString().trim();
            if (!TextUtils.isEmpty(task) && dueDate!=null && priority!=null) {
                Task mytask =new Task(task, priority,
                dueDate,Calendar.getInstance().getTime(),false);
                Task updateTask= sharedViewModel.getSelectedItem().getValue();
                if (isEdit){

                    assert updateTask != null;
                    updateTask.setTask(task);
                    updateTask.setDateCreated(Calendar.getInstance().getTime());
                    updateTask.setPriority(priority);
                    updateTask.setDueDate(dueDate);
                    TaskViewModel.update(updateTask);



                }else {
                    Task mynewtask =new Task(task, priority,
                        dueDate,Calendar.getInstance().getTime(),false);
                    TaskViewModel.insert(mynewtask);
                    enterTodo.setText("");

                }

              }

                if (this.isVisible()){
                    this.dismiss();
                }


            else {

                Snackbar.make(saveButton,R.string.empty_field,Snackbar.LENGTH_LONG)
                        .show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if (id==R.id.today_chip){

            calendar=(Calendar.getInstance());

            dueDate=calendar.getTime();

        }
        else if (id==R.id.tomorrow_chip){
            calendar=(Calendar.getInstance());
            calendar.add(Calendar.DAY_OF_YEAR,1);

            dueDate=calendar.getTime();

        }
        else  if (id==R.id.next_week_chip){
            calendar=(Calendar.getInstance());
            calendar.add(Calendar.DAY_OF_YEAR,7);

            dueDate=calendar.getTime();

        }

    }
}