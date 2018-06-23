package oscar.riksdagskollen.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.JSONModels.StringRequestCallback;
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

        TextView title = findViewById(R.id.vote_title);
        title.setText(document.getTitel());

        final RikdagskollenApp app = oscar.riksdagskollen.RikdagskollenApp.getInstance();
        app.getRequestManager().downloadHtmlPage("http:" + document.getDokument_url_html(), new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                VoteResults results = new VoteResults(response);
                setupMainGraph(results);
                ArrayList<HorizontalBarChart> partyCharts = new ArrayList<>();
                partyCharts.add((HorizontalBarChart) findViewById(R.id.chartS));
                partyCharts.add((HorizontalBarChart) findViewById(R.id.chartM));
                partyCharts.add((HorizontalBarChart) findViewById(R.id.chartSD));
                partyCharts.add((HorizontalBarChart) findViewById(R.id.chartMP));
                partyCharts.add((HorizontalBarChart) findViewById(R.id.chartC));
                String[] parties = {"S","M","SD","MP","C","V","L","KD"};
                setupPartyGraph(results, partyCharts, parties);
            }

            @Override
            public void onFail(VolleyError error) {

            }


        });




    }

    private void setupMainGraph(VoteResults voteResults){
        BarData data = new BarData(getDataSet(voteResults.getTotal()));
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);

        HorizontalBarChart chart =  findViewById(R.id.chart);
        chart.setData(data);
        chart.setDescription(null);

        //X-axis settings
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.setAxisLineWidth(2);
        xAxis.setAxisLineColor(Color.BLACK);


        //TODO borde gå och lösa, fast kanske är bättre att göra som vi gör nu
        /*xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return getXAxisValues().get((int)value);
            }
        });*/

        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawAxisLine(false);

        chart.getLegend().setEnabled(false);
        chart.setDrawValueAboveBar(true);
        chart.setFitBars(true);
        //chart.setDrawBorders(true); //Looks nice to have this on, but values get outside the border
        chart.invalidate();
    }


    private BarDataSet getDataSet(int[] totalVotes){
        ArrayList<Integer> colors = new ArrayList<>();
        //TODO colors
        colors.add(Color.GRAY);
        colors.add(Color.BLACK);
        colors.add(Color.RED);
        colors.add(Color.GREEN);

        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(1, totalVotes[3]));
        entries.add(new BarEntry(2, totalVotes[2]));
        entries.add(new BarEntry(3, totalVotes[1]));
        entries.add(new BarEntry(4, totalVotes[0]));;

        BarDataSet dataset = new BarDataSet(entries,"");
        dataset.setColors(colors);
        dataset.setDrawValues(true);
        return dataset;

    }

    //TODO not used mabye remove
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Frånvarande");
        xAxis.add("Avstående");
        xAxis.add("Nej");
        xAxis.add("Ja");
        xAxis.add("");

        return xAxis;
    }


    private void setupPartyGraph(VoteResults voteResults, ArrayList<HorizontalBarChart> charts, String[] parties){
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GREEN);
        colors.add(Color.RED);
        colors.add(Color.BLACK);
        colors.add(Color.GRAY);

        for (int i = 0; i < charts.size(); i++) {
            HorizontalBarChart chart =  charts.get(i);

            ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

            int[] partyResults = voteResults.getPartyVotes(parties[i]);
            yVals1.add(new BarEntry(0, new float[]{partyResults[0], partyResults[1], partyResults[2], partyResults[3]}));

            BarDataSet set1;

            if (charts.get(i).getData() != null &&
                    chart.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
                set1.setValues(yVals1);
                chart.getData().notifyDataChanged();
                chart.notifyDataSetChanged();
            } else {
                set1 = new BarDataSet(yVals1, "");
                set1.setDrawIcons(false);
                set1.setColors(colors);
                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);

                BarData data = new BarData(dataSets);
                data.setValueTextColor(Color.BLACK);

                chart.setData(data);
            }

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
            chart.invalidate();

        }


    }

    class VoteResults{

        private HashMap<String,int[]> voteResults = new HashMap<>();

        public VoteResults(String response) {
            Document doc = Jsoup.parse(response);

            //This is probably a really bad way of doing this.
            String allVotesString = doc.getElementsByClass("vottabell").text().split("Frånvarande")[1];
            String allVotesArr[] = allVotesString.split(" ");
            for (int i = 1; i < allVotesArr.length; i=i+5) {
                int[] data = {Integer.valueOf(allVotesArr[i+1]),Integer.valueOf(allVotesArr[i+2]),
                        Integer.valueOf(allVotesArr[i+3]),Integer.valueOf(allVotesArr[i+4])};
                voteResults.put(allVotesArr[i],data);
            }

        }

        public int[] getPartyVotes(String party){
            return voteResults.get(party);
        }

        public int[] getTotal(){
            return voteResults.get("Totalt");
        }

    }
}
