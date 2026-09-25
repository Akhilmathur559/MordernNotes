package com.mordernnotes.app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(16));
        root.setBackgroundColor(Color.rgb(248, 247, 252));

        // Header
        TextView title = new TextView(this);
        title.setText("Mordern Notes");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(25, 25, 30));
        title.setTypeface(null, 1);

        root.addView(title, new LinearLayout.LayoutParams(
                -1, dp(48)
        ));

        TextView subtitle = new TextView(this);
        subtitle.setText("Capture your thoughts, your way.");
        subtitle.setTextSize(15);
        subtitle.setTextColor(Color.rgb(100, 100, 110));

        root.addView(subtitle, new LinearLayout.LayoutParams(
                -1, dp(35)
        ));

        // Search
        TextInputLayout searchLayout = new TextInputLayout(
                this,
                null,
                com.google.android.material.R.attr.textInputOutlinedStyle
        );
        searchLayout.setHint("Search notes");

        TextInputEditText search = new TextInputEditText(this);
        search.setSingleLine(true);
        searchLayout.addView(search);

        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(-1, dp(62));
        searchParams.setMargins(0, dp(12), 0, dp(18));

        root.addView(searchLayout, searchParams);

        // Empty-state card
        MaterialCardView card = new MaterialCardView(this);
        card.setRadius(dp(24));
        card.setCardElevation(dp(2));
        card.setStrokeWidth(dp(1));
        card.setStrokeColor(Color.rgb(225, 224, 232));

        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setGravity(Gravity.CENTER);
        cardContent.setPadding(dp(24), dp(32), dp(24), dp(32));

        TextView icon = new TextView(this);
        icon.setText("✦");
        icon.setTextSize(42);
        icon.setGravity(Gravity.CENTER);
        icon.setTextColor(Color.rgb(95, 80, 190));

        cardContent.addView(icon, new LinearLayout.LayoutParams(
                -1, dp(65)
        ));

        TextView emptyTitle = new TextView(this);
        emptyTitle.setText("Your notes live here");
        emptyTitle.setTextSize(21);
        emptyTitle.setTypeface(null, 1);
        emptyTitle.setGravity(Gravity.CENTER);
        emptyTitle.setTextColor(Color.rgb(30, 30, 35));

        cardContent.addView(emptyTitle);

        TextView emptyText = new TextView(this);
        emptyText.setText("Create your first note and keep your ideas organized.");
        emptyText.setTextSize(14);
        emptyText.setGravity(Gravity.CENTER);
        emptyText.setTextColor(Color.rgb(105, 105, 115));
        emptyText.setPadding(0, dp(8), 0, dp(18));

        cardContent.addView(emptyText);

        MaterialButton createButton = new MaterialButton(this);
        createButton.setText("＋  Create Note");
        createButton.setCornerRadius(dp(18));

        cardContent.addView(createButton, new LinearLayout.LayoutParams(
                dp(170), dp(52)
        ));

        card.addView(cardContent);

        root.addView(card, new LinearLayout.LayoutParams(
                -1, dp(300)
        ));

        // Spacer
        View spacer = new View(this);
        root.addView(spacer, new LinearLayout.LayoutParams(
                1, 0, 1
        ));

        // Floating Action Button
        FloatingActionButton fab = new FloatingActionButton(this);
        fab.setContentDescription("Create note");
        fab.setImageResource(android.R.drawable.ic_input_add);

        LinearLayout bottom = new LinearLayout(this);
        bottom.setGravity(Gravity.END | Gravity.BOTTOM);

        bottom.addView(fab, new LinearLayout.LayoutParams(
                dp(60), dp(60)
        ));

        root.addView(bottom, new LinearLayout.LayoutParams(
                -1, dp(70)
        ));

        setContentView(root);
    }
}
