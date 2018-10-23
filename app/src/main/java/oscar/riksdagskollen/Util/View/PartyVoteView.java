package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

import oscar.riksdagskollen.R;

public class PartyVoteView extends LinearLayout {

    private ImageView logo;
    private HorizontalBarChart chart;


    public PartyVoteView(Context context, int imgRes, int[] partyResults) {
        super(context);
        inflate(context, R.layout.party_vote, this);
        logo = findViewById(R.id.party_vote_partylogo);
        chart = findViewById(R.id.party_vote_chart);
        logo.setImageResource(imgRes);
        setUpChart(partyResults);
    }


    private void setUpChart(int[] partyResults) {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getContext(), R.color.yesVoteColor));
        colors.add(ContextCompat.getColor(getContext(), R.color.noVoteColor));
        colors.add(ContextCompat.getColor(getContext(), R.color.refrainVoteColor));
        colors.add(ContextCompat.getColor(getContext(), R.color.absentVoteColor));

        ArrayList<BarEntry> yValues = new ArrayList<>();
        yValues.add(new BarEntry(0, new float[]{partyResults[0], partyResults[1], partyResults[2], partyResults[3]}));
        BarDataSet set1 = new BarDataSet(yValues, "");
        set1.setDrawIcons(false);
        set1.setColors(colors);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        //data.setValueTextColor(Color.BLACK);
        //TODO find a nice way of showing values, something with onclick seems best
        data.setDrawValues(false);

        chart.setData(data);

        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawLabels(false);

        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawAxisLine(false);

        chart.getLegend().setEnabled(false);
        chart.setDrawValueAboveBar(false);
        chart.setFitBars(true);
        chart.setDescription(null);
        chart.setTouchEnabled(false); //Remove interactivity.
        chart.invalidate();

    }


}
