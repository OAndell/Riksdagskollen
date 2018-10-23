package oscar.riksdagskollen.Util.View;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private TextView resultDetailsText;


    public PartyVoteView(Context context, int imgRes, int[] partyResults) {
        super(context);
        inflate(context, R.layout.party_vote, this);
        logo = findViewById(R.id.party_vote_partylogo);
        chart = findViewById(R.id.party_vote_chart);
        resultDetailsText = findViewById(R.id.party_vote_text);
        logo.setImageResource(imgRes);
        setUpChart(partyResults);
        setResultDetailsText(partyResults);
        setOnClick();

    }

    private void setResultDetailsText(int[] partyResults) {
        String resultText = "";
        if (partyResults[0] > 0) {
            resultText += "Ja: " + partyResults[0];
        }
        if (partyResults[1] > 0) {
            resultText += "  Nej: " + partyResults[1];
        }
        if (partyResults[2] > 0) {
            resultText += "  Avståendende: " + partyResults[2];
        }
        if (partyResults[3] > 0) {
            resultText += "  Frånvarande: " + partyResults[3];
        }
        resultDetailsText.setText(resultText);
    }

    private void setOnClick() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resultDetailsText.getVisibility() == VISIBLE) {
                    resultDetailsText.setVisibility(GONE);
                } else {
                    resultDetailsText.setVisibility(VISIBLE);
                }
            }
        });
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
