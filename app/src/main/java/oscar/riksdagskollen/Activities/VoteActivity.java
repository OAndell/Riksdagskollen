package oscar.riksdagskollen.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
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

        final RikdagskollenApp app = oscar.riksdagskollen.RikdagskollenApp.getInstance();
        app.getRequestManager().downloadHtmlPage("http:" + document.getDokument_url_html(), new StringRequestCallback() {
            @Override
            public void onResponse(String response) {
                setUpGraph(new VoteResults(response));
            }

            @Override
            public void onFail(VolleyError error) {

            }


        });




    }

    private void setUpGraph(VoteResults voteResults){
        BarData data = new BarData(getDataSet(voteResults.getTotal()));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        HorizontalBarChart chart =  findViewById(R.id.chart);
        chart.setData(data);
        chart.setDescription(null);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawLabels(false);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawValueAboveBar(false);
        chart.setFitBars(true);
        chart.invalidate();
    }


    private BarDataSet getDataSet(int[] totalVotes){
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.RED);

        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(1f, totalVotes[0]));
        entries.add(new BarEntry(2f, totalVotes[1]));
        entries.add(new BarEntry(3f, totalVotes[2]));
        entries.add(new BarEntry(4f, totalVotes[3]));;

        BarDataSet dataset = new BarDataSet(entries,"Röster");

        dataset.setColors(colors);
        dataset.setDrawValues(true);

        return dataset;

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
