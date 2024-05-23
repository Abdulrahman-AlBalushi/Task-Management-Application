package com.example.task;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private List<Task> taskList;
    private LinearLayout layoutTasks;
    private Map<String, Integer> colorMap;
    private Calendar dueDateCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutTasks = findViewById(R.id.layoutTasks);
        Button buttonAddTask = findViewById(R.id.buttonAddTask);
        Button buttonDeleteTasks = findViewById(R.id.buttonDeleteTasks);
        Button buttonDueDate = findViewById(R.id.buttonDueDate);
        Spinner spinnerPriority = findViewById(R.id.spinnerPriority);
        Spinner spinnerColor = findViewById(R.id.spinnerColor);

        taskList = new ArrayList<>();
        dueDateCalendar = Calendar.getInstance();
        initializeColorMap();

        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        buttonDeleteTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllTasks();
            }
        });

        buttonDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dueDateCalendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        buttonDueDate.setText(sdf.format(dueDateCalendar.getTime()));
                    }
                }, dueDateCalendar.get(Calendar.YEAR), dueDateCalendar.get(Calendar.MONTH),
                        dueDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this,
                R.array.color_array, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(colorAdapter);
    }

    private void initializeColorMap() {
        colorMap = new HashMap<>();
        colorMap.put("Red", Color.RED);
        colorMap.put("Green", Color.GREEN);
        colorMap.put("Blue", Color.BLUE);
        colorMap.put("Yellow", Color.YELLOW);
        colorMap.put("Orange", Color.rgb(255, 165, 0)); // Orange color
        colorMap.put("Purple", Color.rgb(128, 0, 128)); // Purple color
    }

    private void deleteAllTasks() {
        taskList.clear();
        layoutTasks.removeAllViews();
    }

    public void deleteTask(View view) {
        Log.d("MainActivity", "deleteTask() method called");

        View parentView = (View) view.getParent();
        int index = layoutTasks.indexOfChild(parentView);

        if (index != -1) {
            taskList.remove(index);
            layoutTasks.removeView(parentView);
        } else {
            Log.e("MainActivity", "Unable to find task to delete");
        }
    }

    public void editTask(View view) {
        View parentView = (View) view.getParent();
        int index = layoutTasks.indexOfChild(parentView);

        if (index != -1) {
            Task task = taskList.get(index);

            EditText editTextTask = findViewById(R.id.editTextTask);
            Spinner spinnerPriority = findViewById(R.id.spinnerPriority);
            Spinner spinnerColor = findViewById(R.id.spinnerColor);
            Button buttonDueDate = findViewById(R.id.buttonDueDate);

            editTextTask.setText(task.getName());
            spinnerPriority.setSelection(((ArrayAdapter<String>) spinnerPriority.getAdapter()).getPosition(task.getPriority()));
            spinnerColor.setSelection(((ArrayAdapter<String>) spinnerColor.getAdapter()).getPosition(task.getColor()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            buttonDueDate.setText(sdf.format(task.getDueDate()));

            taskList.remove(index);
            layoutTasks.removeView(parentView);
        } else {
            Log.e("MainActivity", "Unable to find task to edit");
        }
    }

    private void addTask() {
        EditText editTextTask = findViewById(R.id.editTextTask);
        Spinner spinnerPriority = findViewById(R.id.spinnerPriority);
        Spinner spinnerColor = findViewById(R.id.spinnerColor);

        String taskName = editTextTask.getText().toString().trim();
        String taskPriority = spinnerPriority.getSelectedItem().toString();
        String taskColor = spinnerColor.getSelectedItem().toString();
        Date taskDueDate = dueDateCalendar.getTime();

        if (!taskName.isEmpty()) {
            Task task = new Task();
            task.setName(taskName);
            task.setPriority(taskPriority);
            task.setColor(taskColor);
            task.setDueDate(taskDueDate);

            taskList.add(task);
            displayTask(task, taskList.size());

            editTextTask.setText("");
            spinnerPriority.setSelection(0);
            spinnerColor.setSelection(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            ((Button) findViewById(R.id.buttonDueDate)).setText(sdf.format(new Date()));
        }
    }

    private void displayTask(Task task, int taskNumber) {
        View taskView = getLayoutInflater().inflate(R.layout.task_item, null);
        TextView textViewTask = taskView.findViewById(R.id.textViewTask);
        TextView textViewPriority = taskView.findViewById(R.id.textViewPriority);
        TextView textViewDueDate = taskView.findViewById(R.id.textViewDueDate);

        String taskText = taskNumber + ". " + task.getName();
        textViewTask.setText(taskText);
        textViewPriority.setText(task.getPriority());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        textViewDueDate.setText(sdf.format(task.getDueDate()));

        Integer color = colorMap.get(task.getColor());
        if (color != null) {
            taskView.setBackgroundColor(color);
        }

        layoutTasks.addView(taskView);
    }
}
