package com.mordernnotes.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private ArrayList<Note> notes = new ArrayList<>();

    private LinearLayout root;
    private LinearLayout notesContainer;
    private EditText searchBox;

    private boolean darkMode = false;
    private int editingIndex = -1;

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("MordernNotes", MODE_PRIVATE);
        darkMode = prefs.getBoolean("dark_mode", false);

        loadNotes();
        showHome();
    }

    // =========================
    // HOME SCREEN
    // =========================

    private void showHome() {

        editingIndex = -1;

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(16));

        applyBackground(root);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText("Mordern Notes");
        title.setTextSize(28);
        title.setTypeface(null, 1);
        title.setTextColor(textColor());

        titleBox.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Capture your thoughts, your way.");
        subtitle.setTextSize(15);
        subtitle.setTextColor(secondaryColor());

        titleBox.addView(subtitle);

        header.addView(titleBox,
                new LinearLayout.LayoutParams(0, dp(70), 1));

        // Theme button
        MaterialButton themeButton = new MaterialButton(this);
        themeButton.setText(darkMode ? "☀" : "☾");
        themeButton.setTextSize(20);
        themeButton.setMinWidth(0);
        themeButton.setPadding(dp(10), 0, dp(10), 0);
        themeButton.setOnClickListener(v -> {
            darkMode = !darkMode;
            prefs.edit().putBoolean("dark_mode", darkMode).apply();
            showHome();
        });

        header.addView(themeButton,
                new LinearLayout.LayoutParams(dp(55), dp(55)));

        root.addView(header);

        // Search
        searchBox = new EditText(this);
        searchBox.setHint("Search notes");
        searchBox.setSingleLine(true);
        searchBox.setTextSize(16);
        searchBox.setPadding(dp(16), 0, dp(16), 0);
        searchBox.setTextColor(textColor());
        searchBox.setHintTextColor(secondaryColor());

        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(56)
                );

        searchParams.setMargins(0, dp(15), 0, dp(15));

        root.addView(searchBox, searchParams);

        // Scroll area
        ScrollView scrollView = new ScrollView(this);

        notesContainer = new LinearLayout(this);
        notesContainer.setOrientation(LinearLayout.VERTICAL);

        scrollView.addView(notesContainer);

        root.addView(
                scrollView,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        // FAB
        LinearLayout bottom = new LinearLayout(this);
        bottom.setGravity(Gravity.END | Gravity.BOTTOM);

        FloatingActionButton fab =
                new FloatingActionButton(this);

        fab.setContentDescription("Create note");
        fab.setImageResource(android.R.drawable.ic_input_add);

        fab.setOnClickListener(v -> showEditor(-1));

        bottom.addView(
                fab,
                new LinearLayout.LayoutParams(
                        dp(60),
                        dp(60)
                )
        );

        root.addView(
                bottom,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(70)
                )
        );

        setContentView(root);

        searchBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {

                displayNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        displayNotes("");
    }

    // =========================
    // DISPLAY NOTES
    // =========================

    private void displayNotes(String query) {

        notesContainer.removeAllViews();

        String q = query.trim().toLowerCase();

        int found = 0;

        for (int i = 0; i < notes.size(); i++) {

            Note note = notes.get(i);

            if (!q.isEmpty()
                    && !note.title.toLowerCase().contains(q)
                    && !note.content.toLowerCase().contains(q)) {
                continue;
            }

            addNoteCard(note, i);
            found++;
        }

        if (found == 0) {

            LinearLayout empty =
                    new LinearLayout(this);

            empty.setOrientation(LinearLayout.VERTICAL);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(
                    dp(20),
                    dp(80),
                    dp(20),
                    dp(50)
            );

            TextView icon = new TextView(this);
            icon.setText(q.isEmpty() ? "✦" : "⌕");
            icon.setTextSize(42);
            icon.setGravity(Gravity.CENTER);
            icon.setTextColor(Color.rgb(100, 75, 200));

            empty.addView(icon);

            TextView message = new TextView(this);

            if (q.isEmpty()) {
                message.setText(
                        "Create your first note\nand keep your ideas organized."
                );
            } else {
                message.setText("No notes found.");
            }

            message.setGravity(Gravity.CENTER);
            message.setTextSize(16);
            message.setTextColor(secondaryColor());

            empty.addView(
                    message,
                    new LinearLayout.LayoutParams(
                            -1,
                            dp(80)
                    )
            );

            if (q.isEmpty()) {

                MaterialButton button =
                        new MaterialButton(this);

                button.setText("＋  Create Note");
                button.setCornerRadius(dp(18));

                button.setOnClickListener(
                        v -> showEditor(-1)
                );

                empty.addView(
                        button,
                        new LinearLayout.LayoutParams(
                                dp(190),
                                dp(55)
                        )
                );
            }

            notesContainer.addView(empty);
        }
    }

    // =========================
    // NOTE CARD
    // =========================

    private void addNoteCard(Note note, int index) {

        MaterialCardView card =
                new MaterialCardView(this);

        card.setRadius(dp(20));
        card.setCardElevation(dp(3));
        card.setStrokeWidth(dp(1));
        card.setStrokeColor(
                darkMode
                        ? Color.rgb(70, 70, 80)
                        : Color.rgb(225, 224, 232)
        );

        LinearLayout content =
                new LinearLayout(this);

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setPadding(
                dp(18),
                dp(18),
                dp(18),
                dp(18)
        );

        TextView title =
                new TextView(this);

        title.setText(
                note.title.isEmpty()
                        ? "Untitled Note"
                        : note.title
        );

        title.setTextSize(19);
        title.setTypeface(null, 1);
        title.setTextColor(textColor());

        content.addView(title);

        TextView preview =
                new TextView(this);

        String previewText = note.content;

        if (previewText.length() > 130) {
            previewText =
                    previewText.substring(0, 130)
                            + "...";
        }

        preview.setText(previewText);
        preview.setTextSize(14);
        preview.setTextColor(secondaryColor());

        LinearLayout.LayoutParams previewParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        previewParams.setMargins(
                0,
                dp(7),
                0,
                dp(7)
        );

        content.addView(
                preview,
                previewParams
        );

        TextView date =
                new TextView(this);

        date.setText(note.date);
        date.setTextSize(12);
        date.setTextColor(secondaryColor());

        content.addView(date);

        card.addView(content);

        card.setOnClickListener(
                v -> showEditor(index)
        );

        card.setOnLongClickListener(v -> {

            showDeleteDialog(index);
            return true;
        });

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.setMargins(
                0,
                0,
                0,
                dp(12)
        );

        notesContainer.addView(card, params);
    }

    // =========================
    // EDITOR
    // =========================

    private void showEditor(int index) {

        editingIndex = index;

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(20)
        );

        applyBackground(root);

        // Top bar
        LinearLayout top =
                new LinearLayout(this);

        top.setGravity(
                Gravity.CENTER_VERTICAL
        );

        MaterialButton back =
                new MaterialButton(this);

        back.setText("←  Back");
        back.setCornerRadius(dp(16));

        back.setOnClickListener(
                v -> showHome()
        );

        top.addView(
                back,
                new LinearLayout.LayoutParams(
                        dp(100),
                        dp(50)
                )
        );

        TextView heading =
                new TextView(this);

        heading.setText(
                index == -1
                        ? "New Note"
                        : "Edit Note"
        );

        heading.setTextSize(22);
        heading.setTypeface(null, 1);
        heading.setTextColor(textColor());
        heading.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams headingParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(50),
                        1
                );

        top.addView(heading, headingParams);

        root.addView(top);

        // Title
        EditText title =
                new EditText(this);

        title.setHint("Note title");
        title.setTextSize(21);
        title.setSingleLine(true);
        title.setTextColor(textColor());
        title.setHintTextColor(secondaryColor());

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(65)
                );

        titleParams.setMargins(
                0,
                dp(20),
                0,
                dp(10)
        );

        root.addView(title, titleParams);

        // Content
        EditText content =
                new EditText(this);

        content.setHint("Write your note here...");
        content.setGravity(
                Gravity.TOP | Gravity.START
        );

        content.setTextSize(16);
        content.setTextColor(textColor());
        content.setHintTextColor(secondaryColor());
        content.setPadding(
                dp(14),
                dp(14),
                dp(14),
                dp(14)
        );

        content.setSingleLine(false);

        root.addView(
                content,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        // Save
        MaterialButton save =
                new MaterialButton(this);

        save.setText("Save Note");
        save.setTextSize(16);
        save.setCornerRadius(dp(18));

        LinearLayout.LayoutParams saveParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(58)
                );

        saveParams.setMargins(
                0,
                dp(15),
                0,
                0
        );

        root.addView(save, saveParams);

        if (index >= 0) {

            title.setText(notes.get(index).title);
            content.setText(notes.get(index).content);
        }

        save.setOnClickListener(v -> {

            String noteTitle =
                    title.getText()
                            .toString()
                            .trim();

            String noteContent =
                    content.getText()
                            .toString()
                            .trim();

            if (noteTitle.isEmpty()
                    && noteContent.isEmpty()) {

                Toast.makeText(
                        this,
                        "Write something first",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            String date =
                    new SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a",
                            Locale.getDefault()
                    ).format(new Date());

            if (editingIndex == -1) {

                notes.add(
                        new Note(
                                noteTitle,
                                noteContent,
                                date
                        )
                );

            } else {

                notes.get(editingIndex).title =
                        noteTitle;

                notes.get(editingIndex).content =
                        noteContent;

                notes.get(editingIndex).date =
                        date;
            }

            saveNotes();

            Toast.makeText(
                    this,
                    "Note saved",
                    Toast.LENGTH_SHORT
            ).show();

            showHome();
        });

        setContentView(root);
    }

    // =========================
    // DELETE
    // =========================

    private void showDeleteDialog(int index) {

        new AlertDialog.Builder(this)
                .setTitle("Delete note?")
                .setMessage(
                        "This note will be permanently deleted."
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            notes.remove(index);
                            saveNotes();
                            displayNotes(
                                    searchBox == null
                                            ? ""
                                            : searchBox
                                            .getText()
                                            .toString()
                            );

                            Toast.makeText(
                                    this,
                                    "Note deleted",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .show();
    }

    // =========================
    // SAVE NOTES
    // =========================

    private void saveNotes() {

        JSONArray array = new JSONArray();

        try {

            for (Note note : notes) {

                JSONObject object =
                        new JSONObject();

                object.put(
                        "title",
                        note.title
                );

                object.put(
                        "content",
                        note.content
                );

                object.put(
                        "date",
                        note.date
                );

                array.put(object);
            }

            prefs.edit()
                    .putString(
                            "notes",
                            array.toString()
                    )
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // LOAD NOTES
    // =========================

    private void loadNotes() {

        notes.clear();

        String data =
                prefs.getString(
                        "notes",
                        ""
                );

        if (data.isEmpty()) {
            return;
        }

        try {

            JSONArray array =
                    new JSONArray(data);

            for (int i = 0;
                 i < array.length();
                 i++) {

                JSONObject object =
                        array.getJSONObject(i);

                notes.add(
                        new Note(
                                object.optString("title"),
                                object.optString("content"),
                                object.optString("date")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // COLORS
    // =========================

    private void applyBackground(View view) {

        view.setBackgroundColor(
                darkMode
                        ? Color.rgb(25, 23, 30)
                        : Color.rgb(248, 247, 252)
        );
    }

    private int textColor() {

        return darkMode
                ? Color.rgb(245, 243, 250)
                : Color.rgb(30, 30, 35);
    }

    private int secondaryColor() {

        return darkMode
                ? Color.rgb(175, 172, 185)
                : Color.rgb(105, 105, 115);
    }

    // =========================
    // NOTE MODEL
    // =========================

    private static class Note {

        String title;
        String content;
        String date;

        Note(
                String title,
                String content,
                String date
        ) {

            this.title = title;
            this.content = content;
            this.date = date;
        }
    }
}