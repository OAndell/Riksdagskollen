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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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


        val pollingDataObservable = PublishSubject.create<Array<PollingData>>();
        val partyDataObservable = PublishSubject.create<Array<PartyData>>();

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
                    val valueList: MutableList<Float> = ArrayList()
                    val entries: MutableList<BarEntry> = ArrayList()
                    val title = "Title"
                    //input data
                    for (i in 0..1) {
                        valueList.add((0).toFloat())
                    }
                    for (i in 0 until valueList.size) {
                        val barEntry = BarEntry(i.toFloat(), valueList[i])
                        entries.add(barEntry)
                    }
                    val barDataSet = BarDataSet(entries, title)
                    barDataSet.setColors(intArrayOf(Color.RED, Color.BLUE).asList())

                    val data = BarData(barDataSet)
                    barChart.data = data
                    barChart.axisLeft.addLimitLine(createFiftyPercentLimitLine())
                    populateButtonBar(barChart, buttonBarRight, pollData, 0)
                    populateButtonBar(barChart, buttonBarLeft, pollData, 1)
                    barChart.axisLeft.setAxisMinimum(0f);
                    barChart.axisLeft.setAxisMaximum(100f);
                    barChart.setBackgroundColor(Color.parseColor("#FFFFFF"))



                    barChart.invalidate()

                }
    }

    private fun populateButtonBar(barChart: BarChart, layout: LinearLayout, pollData: Array<PollingData>, barChartIndex: Int) {
        for (d in pollData) {
            val partyPercent = resultStringToFloat(d.data[0].percent)
            val logo = LogoButton(app.baseContext, CurrentParties.getParty(d.party).drawableLogo, partyPercent, barChart, barChartIndex)
            layout.addView(logo.imageView);
        }
    }

    companion object {
        fun createPartyDataSet(pollData: PollingData, partyData: PartyData): ILineDataSet {
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
            dataSet.lineWidth = 3.toFloat();
            return dataSet;
        }

        fun createFourPercentLimitLine(): LimitLine {
            val line = LimitLine(4.toFloat(), "");
            line.enableDashedLine(10.toFloat(), 5.toFloat(), 0.toFloat())
            line.lineWidth = 2.toFloat()
            line.textSize = 18.toFloat()
            line.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
            return line;
        }

        fun createFiftyPercentLimitLine(): LimitLine {
            val line = LimitLine(50.toFloat(), "50%");
            line.enableDashedLine(10.toFloat(), 5.toFloat(), 0.toFloat())
            line.lineWidth = 2.toFloat()
            line.textSize = 18.toFloat()
            line.labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
            return line;
        }

        fun resultStringToFloat(percent: String): Float {
            return percent.replace("%", "").replace(",", ".").toFloat()
        }


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

    class LogoButton(context: Context, logo: Int, pollingPercent: Float, chart: BarChart, barChartIndex: Int) {
        var active: Boolean = false;
        val imageView: ImageView = ImageView(context);

        init {
            imageView.setImageDrawable(context.resources.getDrawable(logo));
            imageView.layoutParams = ViewGroup.LayoutParams(80, 200)
            imageView.setColorFilter(R.color.black);
            imageView.setOnClickListener {
                if (!active) {

                    val currentData = chart.barData.getDataSetByIndex(0).getEntryForIndex(barChartIndex).y
                    chart.barData.getDataSetByIndex(0).getEntryForIndex(barChartIndex).y = currentData + pollingPercent;
                    chart.invalidate();

                    active = true;
                    imageView.setColorFilter(null)
                } else {
                    val currentData = chart.barData.getDataSetByIndex(0).getEntryForIndex(barChartIndex).y
                    chart.barData.getDataSetByIndex(0).getEntryForIndex(barChartIndex).y = currentData - pollingPercent;
                    chart.invalidate();
                    active = false;
                    imageView.setColorFilter(R.color.black);
                }
            }
        }


    }
}

