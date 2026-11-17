package com.menacycle.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    private Calendar lastPeriod = Calendar.getInstance();
    private Calendar visibleMonth = Calendar.getInstance();
    private EditText cycleInput;
    private TextView dateText, resultText, monthText;
    private GridLayout calendarGrid;
    private int cycleLength = 28;
    private boolean calculated = false;

    private final int pink = Color.rgb(225, 75, 145);
    private final int dark = Color.rgb(55, 35, 55);
    private final int palePink = Color.rgb(255, 235, 245);
    private final int green = Color.rgb(210, 241, 218);
    private final int yellow = Color.rgb(255, 239, 180);
    private final int red = Color.rgb(255, 205, 210);
    private final int white = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastPeriod.set(Calendar.HOUR_OF_DAY, 0);
        lastPeriod.set(Calendar.MINUTE, 0);
        lastPeriod.set(Calendar.SECOND, 0);
        lastPeriod.set(Calendar.MILLISECOND, 0);
        visibleMonth = (Calendar) lastPeriod.clone();
        buildScreen();
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(24));
        root.setBackgroundColor(Color.rgb(255, 248, 251));
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("🌸 MenaCycle");
        title.setTextSize(28);
        title.setTextColor(pink);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title, matchWrap());

        TextView subtitle = new TextView(this);
        subtitle.setText("Kajy tsotra momba ny tsingerin’ny fadimbolana");
        subtitle.setTextSize(14);
        subtitle.setTextColor(dark);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(5), 0, dp(18));
        root.addView(subtitle, matchWrap());

        TextView dateLabel = label("Daty voalohany tamin’ny fadimbolana farany:");
        root.addView(dateLabel, matchWrap());

        dateText = new TextView(this);
        dateText.setText(formatDate(lastPeriod));
        dateText.setTextSize(17);
        dateText.setTextColor(dark);
        dateText.setPadding(dp(12), dp(12), dp(12), dp(12));
        dateText.setBackground(rounded(white, 12, Color.LTGRAY));
        root.addView(dateText, matchWrap());

        Button chooseDate = button("Safidio ny daty");
        chooseDate.setOnClickListener(v -> showDatePicker());
        root.addView(chooseDate, matchWrap());

        TextView cycleLabel = label("Faharetan’ny cycle (andro):");
        cycleLabel.setPadding(0, dp(14), 0, dp(5));
        root.addView(cycleLabel, matchWrap());

        cycleInput = new EditText(this);
        cycleInput.setSingleLine(true);
        cycleInput.setInputType(2);
        cycleInput.setText("28");
        cycleInput.setTextSize(17);
        cycleInput.setTextColor(dark);
        cycleInput.setPadding(dp(12), 0, dp(12), 0);
        cycleInput.setBackground(rounded(white, 12, Color.LTGRAY));
        root.addView(cycleInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(48)));

        Button calculate = button("KAJIO NY DATY");
        calculate.setOnClickListener(v -> calculateDates());
        LinearLayout.LayoutParams calcParams = matchWrap();
        calcParams.topMargin = dp(12);
        root.addView(calculate, calcParams);

        resultText = new TextView(this);
        resultText.setText("Ampidiro ny daty sy ny faharetan’ny cycle, dia tsindrio « KAJIO NY DATY ».");
        resultText.setTextColor(dark);
        resultText.setTextSize(15);
        resultText.setLineSpacing(dp(4), 1f);
        resultText.setPadding(dp(14), dp(14), dp(14), dp(14));
        resultText.setBackground(rounded(white, 12, Color.rgb(245, 205, 225)));
        LinearLayout.LayoutParams resultParams = matchWrap();
        resultParams.topMargin = dp(14);
        root.addView(resultText, resultParams);

        TextView calTitle = label("KALANDRIE");
        calTitle.setTextSize(19);
        calTitle.setTypeface(null, Typeface.BOLD);
        calTitle.setPadding(0, dp(22), 0, dp(8));
        root.addView(calTitle, matchWrap());

        LinearLayout monthNav = new LinearLayout(this);
        monthNav.setGravity(Gravity.CENTER);
        Button prev = button("‹");
        Button next = button("›");
        monthText = new TextView(this);
        monthText.setTextSize(17);
        monthText.setTextColor(dark);
        monthText.setTypeface(null, Typeface.BOLD);
        monthText.setGravity(Gravity.CENTER);
        monthNav.addView(prev, new LinearLayout.LayoutParams(dp(52), dp(44)));
        monthNav.addView(monthText, new LinearLayout.LayoutParams(0, dp(44), 1));
        monthNav.addView(next, new LinearLayout.LayoutParams(dp(52), dp(44)));
        root.addView(monthNav, matchWrap());
        prev.setOnClickListener(v -> {
            visibleMonth.add(Calendar.MONTH, -1);
            drawCalendar();
        });
        next.setOnClickListener(v -> {
            visibleMonth.add(Calendar.MONTH, 1);
            drawCalendar();
        });

        calendarGrid = new GridLayout(this);
        calendarGrid.setColumnCount(7);
        calendarGrid.setUseDefaultMargins(false);
        LinearLayout.LayoutParams gridParams = matchWrap();
        gridParams.topMargin = dp(8);
        root.addView(calendarGrid, gridParams);

        TextView legend = new TextView(this);
        legend.setText("🟢 Tombanana ho ambany kokoa  •  🟡 Fotoana fertile tombanana  •  🔴 Ovulation tombanana");
        legend.setTextColor(dark);
        legend.setTextSize(12);
        legend.setPadding(0, dp(12), 0, dp(8));
        root.addView(legend, matchWrap());

        TextView warning = new TextView(this);
        warning.setText("FAMPITANDREMANA: Tsy misy andro azo antoka 100% hanaovana firaisana tsy misy fiarovana. Tombana fotsiny ireo daty ireo ary mety hiova ny ovulation. Raha tsy te ho bevohoka ianao dia ampiasao préservatif na fomba fanabeazana aizana azo antoka isaky ny manao firaisana. Tsy manolo torohevitra ara-pahasalamana ity application ity.");
        warning.setTextColor(Color.rgb(130, 35, 35));
        warning.setTextSize(13);
        warning.setPadding(dp(12), dp(12), dp(12), dp(12));
        warning.setBackground(rounded(Color.rgb(255, 235, 235), 12, Color.rgb(240, 170, 170)));
        LinearLayout.LayoutParams warnParams = matchWrap();
        warnParams.topMargin = dp(12);
        root.addView(warning, warnParams);

        setContentView(scroll);
        drawCalendar();
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) -> {
            lastPeriod.set(year, month, day, 0, 0, 0);
            lastPeriod.set(Calendar.MILLISECOND, 0);
            dateText.setText(formatDate(lastPeriod));
            visibleMonth = (Calendar) lastPeriod.clone();
            calculated = false;
            resultText.setText("Niova ny daty. Tsindrio indray ny « KAJIO NY DATY ».");
            drawCalendar();
        }, lastPeriod.get(Calendar.YEAR), lastPeriod.get(Calendar.MONTH),
                lastPeriod.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void calculateDates() {
        String raw = cycleInput.getText().toString().trim();
        try {
            int n = Integer.parseInt(raw);
            if (n < 21 || n > 35) {
                Toast.makeText(this, "Ampidiro cycle eo anelanelan’ny 21 sy 35 andro.", Toast.LENGTH_LONG).show();
                return;
            }
            cycleLength = n;
            calculated = true;

            Calendar nextPeriod = (Calendar) lastPeriod.clone();
            nextPeriod.add(Calendar.DAY_OF_YEAR, cycleLength);

            Calendar ovulation = (Calendar) nextPeriod.clone();
            ovulation.add(Calendar.DAY_OF_YEAR, -14);

            Calendar fertileStart = (Calendar) ovulation.clone();
            fertileStart.add(Calendar.DAY_OF_YEAR, -5);

            Calendar fertileEnd = (Calendar) ovulation.clone();
            fertileEnd.add(Calendar.DAY_OF_YEAR, 1);

            resultText.setText(
                    "FADIMBOLANA MANARAKA (tombana): " + formatDate(nextPeriod) +
                    "\n\nOVULATION (tombana): " + formatDate(ovulation) +
                    "\n\nFOTOANA FERTILE (tombana): " + formatDate(fertileStart) +
                    " hatramin’ny " + formatDate(fertileEnd) +
                    "\n\nMarihina: tombana amin’ny cycle " + cycleLength +
                    " andro ireo daty ireo. Tsy antoka hisorohana vohoka izy ireo."
            );
            drawCalendar();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ampidiro isa marina ny faharetan’ny cycle.", Toast.LENGTH_LONG).show();
        }
    }

    private void drawCalendar() {
        if (calendarGrid == null || monthText == null) return;
        calendarGrid.removeAllViews();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", new Locale("fr", "FR"));
        monthText.setText(monthFormat.format(visibleMonth.getTime()));

        String[] days = {"Al", "Tal", "Alr", "Alk", "Zom", "Sab", "Ala"};
        for (String day : days) {
            TextView head = new TextView(this);
            head.setText(day);
            head.setTextColor(pink);
            head.setTextSize(12);
            head.setTypeface(null, Typeface.BOLD);
            head.setGravity(Gravity.CENTER);
            calendarGrid.addView(head, cellParams());
        }

        Calendar first = (Calendar) visibleMonth.clone();
        first.set(Calendar.DAY_OF_MONTH, 1);
        int offset = (first.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        int max = first.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < offset; i++) {
            TextView blank = new TextView(this);
            calendarGrid.addView(blank, cellParams());
        }

        for (int day = 1; day <= max; day++) {
            Calendar date = (Calendar) first.clone();
            date.set(Calendar.DAY_OF_MONTH, day);
            TextView cell = new TextView(this);
            cell.setText(String.valueOf(day));
            cell.setTextColor(dark);
            cell.setTextSize(14);
            cell.setGravity(Gravity.CENTER);
            cell.setBackground(rounded(white, 8, Color.rgb(240, 225, 235)));

            if (calculated) {
                int cycleDay = getCycleDay(date);
                int ovDay = cycleLength - 13; // ovulation is approximately day cycleLength - 14 + 1
                int fertileStartDay = Math.max(1, ovDay - 5);
                int fertileEndDay = ovDay + 1;
                if (cycleDay == ovDay) {
                    cell.setBackground(rounded(red, 8, Color.rgb(225, 120, 130)));
                } else if (cycleDay >= fertileStartDay && cycleDay <= fertileEndDay) {
                    cell.setBackground(rounded(yellow, 8, Color.rgb(225, 190, 100)));
                } else {
                    cell.setBackground(rounded(green, 8, Color.rgb(135, 200, 150)));
                }
            }
            calendarGrid.addView(cell, cellParams());
        }
    }

    private int getCycleDay(Calendar date) {
        long diff = date.getTimeInMillis() - lastPeriod.getTimeInMillis();
        long days = TimeUnit.MILLISECONDS.toDays(diff);
        int mod = (int) (days % cycleLength);
        if (mod < 0) mod += cycleLength;
        return mod + 1;
    }

    private String formatDate(Calendar c) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(c.getTime());
    }

    private TextView label(String text) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(15);
        t.setTextColor(dark);
        t.setTypeface(null, Typeface.BOLD);
        t.setPadding(0, dp(5), 0, dp(5));
        return t;
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(white);
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setBackground(rounded(pink, 10, pink));
        return b;
    }

    private GradientDrawable rounded(int color, int radius, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        d.setStroke(dp(1), stroke);
        return d;
    }

    private GridLayout.LayoutParams cellParams() {
        GridLayout.LayoutParams p = new GridLayout.LayoutParams();
        p.width = 0;
        p.height = dp(42);
        p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        p.setMargins(dp(2), dp(2), dp(2), dp(2));
        return p;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
