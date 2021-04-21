package oscar.riksdagskollen.Fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.android.volley.VolleyError
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.subjects.PublishSubject
import oscar.riksdagskollen.R
import oscar.riksdagskollen.RiksdagskollenApp
import oscar.riksdagskollen.Util.Enum.CurrentParties
import oscar.riksdagskollen.Util.JSONModel.RiksdagskollenAPI.PartyDataModels.PartyData
import oscar.riksdagskollen.Util.JSONModel.RiksdagskollenAPI.PollingDataModels.PollingData
import oscar.riksdagskollen.Util.RiksdagenCallback.RKAPICallbacks.PartyDataListCallback
import oscar.riksdagskollen.Util.RiksdagenCallback.RKAPICallbacks.PollingDataListCallback
import java.util.*
import kotlin.collections.ArrayList



const val SECTION_NAME = "polling"

/**
 * A simple [Fragment] subclass.
 * Use the [PollingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PollingFragment : Fragment() {

    val app: RiksdagskollenApp = RiksdagskollenApp.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_polling, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lineChart = view.findViewById(R.id.polling_chart) as LineChart
        val barChart = view.findViewById(R.id.block_chart) as BarChart
        val buttonBarRight = view.findViewById(R.id.block_chart_right_bar) as LinearLayout
        val buttonBarLeft = view.findViewById(R.id.block_chart_left_bar) as LinearLayout
        val pollingDataObservable = PublishSubject.create<Array<PollingData>>()
        val partyDataObservable = PublishSubject.create<Array<PartyData>>()

        app.riksdagskollenAPIManager.getPollingData(object : PollingDataListCallback {
            override fun onFetched(data: Array<PollingData>) {
                pollingDataObservable.onNext(data);
            }

            override fun onFail(error: VolleyError) {}
        })


        app.riksdagskollenAPIManager.getPartyData(object : PartyDataListCallback {
            override fun onFetched(data: Array<PartyData>) {
                partyDataObservable.onNext(data);
                println(data.get(0).color);
            }

            override fun onFail(error: VolleyError?) {
            }

        });


        Observable.combineLatest(
                pollingDataObservable,
                partyDataObservable,
                BiFunction<Array<PollingData>, Array<PartyData>, Pair<Array<PollingData>, Array<PartyData>>> { t1, t2 -> Pair(t1, t2) })
                .subscribe {
                    val pollData = it.first
                    val partyData = it.second

                    //Line Chart
                    val dataSets: MutableList<ILineDataSet> = ArrayList()
                    for (partyPolling in pollData) {
                        val partyInfo = partyData.find { it.abbreviation == partyPolling.party }
                        if (partyInfo != null) {
                            dataSets.add(createPartyDataSet(partyPolling, partyInfo))
                        }
                    }

                    val lineData = LineData(dataSets)
                    lineChart.data = lineData;
                    lineChart.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    lineChart.xAxis.setValueFormatter { value, axis -> pollData[0].data.reversed()[value.toInt()].period }
                    lineChart.xAxis.setLabelCount(4)
                    lineChart.axisLeft.addLimitLine(createFourPercentLimitLine())
                    lineChart.axisLeft.setValueFormatter { value, axis -> value.toString() + "%" }
                    lineChart.axisRight.setValueFormatter { value, axis -> value.toString() + "%" }
                    lineChart.description.text = "";
                    lineChart.invalidate()

                    //Bar chart
                    val columns: MutableList<BarEntry> = ArrayList()

                    val column1 = hashMapOf<String, Int>();
                    val column2 = hashMapOf<String, Int>();
                    val colors: MutableList<Int> = ArrayList();

                    for (i in partyData.indices) {
                        column1.put(partyData[i].abbreviation, i)
                        column2.put(partyData[i].abbreviation, i)
                        colors.add(Color.parseColor(partyData[i].color))
                    }

                    columns.add(BarEntry(0f, floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)))
                    columns.add(BarEntry(1f, floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)))

                    val barDataSet = BarDataSet(columns, "")
                    barDataSet.setDrawValues(false)
                    barDataSet.setColors(colors)

                    val data = BarData(barDataSet)
                    barChart.data = data
                    barChart.axisLeft.addLimitLine(createFiftyPercentLimitLine())
                    barChart.description.isEnabled = false

                    val rightBarDefaults = arrayOf("S", "V", "MP", "L", "C")
                    val leftBarDefaults = arrayOf("M", "SD", "KD")

                    populateButtonBar(barChart, buttonBarRight, pollData, 0, column1, rightBarDefaults)
                    populateButtonBar(barChart, buttonBarLeft, pollData, 1, column2, leftBarDefaults)
                    barChart.axisLeft.setAxisMinimum(0f)
                    barChart.axisLeft.setAxisMaximum(100f)

                    barChart.axisLeft.setDrawGridLines(false)
                    barChart.getXAxis().setDrawAxisLine(false);
                    barChart.getXAxis().setDrawGridLines(false);
                    barChart.getXAxis().setDrawLabels(false);

                    barChart.getAxisLeft().setDrawLabels(false);
                    barChart.getAxisLeft().setDrawGridLines(false);
                    barChart.getAxisLeft().setDrawAxisLine(false);
                    barChart.getAxisRight().setDrawLabels(false);
                    barChart.getAxisRight().setDrawGridLines(false);
                    barChart.getAxisRight().setDrawAxisLine(false);

                    barChart.getLegend().setEnabled(false);
                    barChart.setDescription(null);
                    barChart.setTouchEnabled(false);


                    //barChart.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    /*
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
                     */
                    barChart.invalidate()

                }
    }

    private fun populateButtonBar(barChart: BarChart, layout: LinearLayout, pollData: Array<PollingData>, barChartIndex: Int, partyMap: Map<String, Int>, defaultSelected: Array<String>) {
        for (d in pollData) {
            val partyPercent = resultStringToFloat(d.data[0].percent)
            val valueIndex: Int = partyMap.get(d.party)!!;
            val logo = LogoButton(app.baseContext, CurrentParties.getParty(d.party).drawableLogo, partyPercent, barChart, barChartIndex, valueIndex, defaultSelected.contains(d.party))
            layout.addView(logo.imageView);
        }
    }

    private fun createPartyDataSet(pollData: PollingData, partyData: PartyData): ILineDataSet {
        val partyAbr = pollData.party
        val data = pollData.data.reversed()
        val entries: MutableList<Entry> = java.util.ArrayList()
        for (i in data.indices) {
            val value = data[i].percent.replace("%", "").replace(",", ".").toFloat()
            entries.add(Entry(i.toFloat(), value));
        }
        val dataSet = LineDataSet(entries, partyAbr)
        dataSet.setCircleColor(Color.parseColor(partyData.color))
        dataSet.setCircleColorHole(Color.parseColor(partyData.color))
        dataSet.setColor(Color.parseColor(partyData.color));
        dataSet.lineWidth = 3f
        return dataSet;
    }

    private fun createFourPercentLimitLine(): LimitLine {
        val line = LimitLine(4f, "");
        line.enableDashedLine(10f, 5f, 0f)
        line.lineWidth = 2f
        line.textSize = 18f
        line.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        return line;
    }

    private fun createFiftyPercentLimitLine(): LimitLine {
        val line = LimitLine(50f, "50%");
        line.enableDashedLine(10f, 5f, 0f)
        line.lineWidth = 2f
        line.textSize = 18f
        line.textColor = Color.BLACK
        line.lineColor = Color.BLACK
        line.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
        return line;
    }

    private fun resultStringToFloat(percent: String): Float {
        return percent.replace("%", "").replace(",", ".").toFloat()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PollingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                PollingFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    class LogoButton(context: Context, logo: Int, val pollingPercent: Float, val chart: BarChart, val barChartIndex: Int, val valueIndex: Int, val isDefault: Boolean) {
        var active: Boolean = false;
        val imageView: ImageView = ImageView(context);

        init {
            imageView.setImageDrawable(context.resources.getDrawable(logo));
            imageView.layoutParams = ViewGroup.LayoutParams(80, 200)
            imageView.setColorFilter(R.color.black)
            imageView.setOnClickListener {
                if (!active) {
                    add()
                } else {
                    remove()
                }
            }

            if (isDefault) {
                add();
            }
        }

        private fun add() {
            val column = chart.barData.getDataSetByIndex(0).getEntryForIndex(barChartIndex)
            column.yVals.set(valueIndex, pollingPercent);
            chart.invalidate()
            active = true;
            imageView.setColorFilter(null)
        }

        private fun remove() {
            val column = chart.barData.getDataSetByIndex(0).getEntryForIndex(barChartIndex)
            column.yVals.set(valueIndex, 0f)
            chart.invalidate()
            active = false;
            imageView.setColorFilter(R.color.black);
        }
    }
}

