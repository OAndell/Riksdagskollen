package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RiksdagskollenApp;
import oscar.riksdagskollen.Util.Helper.VoteResults;

public class VoteResultsView extends LinearLayout {

    private HorizontalBarChart chart;

    public VoteResultsView(Context context, VoteResults voteResults) {
        super(context);
        inflate(context, R.layout.vote_result_chart, this);
        chart = findViewById(R.id.vote_result_chart_chart);
        setupMainGraph(voteResults);
    }


    private void setupMainGraph(VoteResults voteResults) {

        BarData data = new BarData(createDataSet(voteResults.getTotal()));
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);

        chart.setData(data);
        chart.setDescription(null);

        //X-axis settings
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.setAxisLineWidth(2);
        xAxis.setAxisLineColor(RiksdagskollenApp.getColorFromAttribute(R.attr.mainBodyTextColor, getContext()));

        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawAxisLine(false);

        chart.getLegend().setEnabled(false);
        chart.setDrawValueAboveBar(true);
        chart.setFitBars(true);
        chart.setTouchEnabled(false); //Remove ability to zoom n stuff
        //chart.setDrawBorders(true); //Looks nice to have this on, but values get outside the border
        chart.invalidate();
    }

    private BarDataSet createDataSet(int[] totalVotes) {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getContext(), R.color.absentVoteColor));
        colors.add(ContextCompat.getColor(getContext(), R.color.refrainVoteColor));
        colors.add(ContextCompat.getColor(getContext(), R.color.noVoteColor));
        colors.add(ContextCompat.getColor(getContext(), R.color.yesVoteColor));

        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(1, totalVotes[3]));
        entries.add(new BarEntry(2, totalVotes[2]));
        entries.add(new BarEntry(3, totalVotes[1]));
        entries.add(new BarEntry(4, totalVotes[0]));
        BarDataSet dataset = new BarDataSet(entries, "");
        dataset.setColors(colors);
        dataset.setDrawValues(true);
        return dataset;

    }
}
