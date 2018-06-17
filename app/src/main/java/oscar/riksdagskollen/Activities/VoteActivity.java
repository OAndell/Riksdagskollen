package oscar.riksdagskollen.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Utilities.JSONModels.Vote;

/**
 * Created by oscar on 2018-06-16.
 */

public class VoteActivity extends AppCompatActivity{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        Vote document = getIntent().getParcelableExtra("document");
        HorizontalBarChart chart =  findViewById(R.id.chart);

        ArrayList<BarEntry> yesEntries = new ArrayList();
        yesEntries.add(new BarEntry(1f, 10));

        ArrayList<BarEntry> noEntries = new ArrayList();
        yesEntries.add(new BarEntry(2f, 5));

        BarDataSet yes = new BarDataSet(yesEntries, "Ja");
        BarDataSet no = new BarDataSet(noEntries, "Nej");



        BarData data = new BarData(yes, no);

        chart.getXAxis().setDrawGridLines(false);
        chart.setData(data);
        chart.invalidate();


    }


    private BarDataSet getDataSet(){
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.RED);

        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(1f, 1));
        entries.add(new BarEntry(2f, 1));
        entries.add(new BarEntry(3f, 2));
        entries.add(new BarEntry(4f, 3));;

        BarDataSet dataset = new BarDataSet(entries,"");

        dataset.setColors(colors);


        return dataset;

    }
}
