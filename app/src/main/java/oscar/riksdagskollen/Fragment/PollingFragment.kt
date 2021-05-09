package oscar.riksdagskollen.Fragment

import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
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
import oscar.riksdagskollen.Util.Helper.CustomTabs
import oscar.riksdagskollen.Util.Helper.DateFormatter
import oscar.riksdagskollen.Util.JSONModel.Party
import oscar.riksdagskollen.Util.JSONModel.RiksdagskollenAPI.PartyDataModels.PartyData
import oscar.riksdagskollen.Util.JSONModel.RiksdagskollenAPI.PollingDataModels.PollingData
import oscar.riksdagskollen.Util.RiksdagenCallback.RKAPICallbacks.PartyDataListCallback
import oscar.riksdagskollen.Util.RiksdagenCallback.RKAPICallbacks.PollingDataListCallback


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
        (activity as AppCompatActivity?)!!.supportActionBar!!.setTitle("Opinionsmätning")


        return inflater.inflate(R.layout.fragment_polling, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lineChart = view.findViewById(R.id.polling_chart) as LineChart
        val barChart = view.findViewById(R.id.block_chart) as BarChart
        val sourceTextView = view.findViewById(R.id.polling_source) as TextView
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
            }

            override fun onFail(error: VolleyError?) {}

        });


        Observable.combineLatest(
                pollingDataObservable,
                partyDataObservable,
                BiFunction<Array<PollingData>, Array<PartyData>, Pair<Array<PollingData>, Array<PartyData>>> { t1, t2 -> Pair(t1, t2) })
                .subscribe {
                    val pollData = it.first
                    val partyData = it.second

                    initSourceTextView(sourceTextView, pollData[0].source);

                    //Line Chart
                    val dataSets: MutableList<ILineDataSet> = ArrayList()
                    for (partyPolling in pollData) {
                        val partyInfo = partyData.find { it.abbreviation == partyPolling.party }
                        if (partyInfo != null) {
                            dataSets.add(createPartyDataSet(partyPolling, partyInfo))
                        }
                    }

                    val lineData = LineData(dataSets)
                    lineData.setValueTextSize(12f)
                    lineChart.data = lineData;
                    lineChart.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    lineChart.axisLeft.isEnabled = false
                    lineChart.xAxis.setValueFormatter { value, axis -> DateFormatter.formatShort(pollData[0].data.reversed()[value.toInt()].period) }
                    lineChart.xAxis.setLabelCount(4)
                    lineChart.axisLeft.setDrawLabels(false)
                    lineChart.axisRight.setValueFormatter { value, axis -> value.toString() + "%" }
                    lineChart.axisRight.textSize = 12f
                    lineChart.xAxis.textSize = 12f
                    lineChart.extraTopOffset = 3f //Prevents top text to be cut off
                    lineChart.setPadding(0, 100, 0, 0)
                    lineChart.description.text = ""
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
                    data.setBarWidth(0.7f);

                    barChart.data = data
                    barChart.axisLeft.addLimitLine(createFiftyPercentLimitLine())

                    val rightBarDefaults = arrayOf("S", "V", "MP", "L", "C")
                    val leftBarDefaults = arrayOf("M", "SD", "KD")

                    populateButtonBar(barChart, buttonBarRight, buttonBarLeft, pollData, 0, column1, rightBarDefaults)
                    populateButtonBar(barChart, buttonBarLeft, buttonBarRight, pollData, 1, column2, leftBarDefaults)
                    barChart.axisLeft.setAxisMinimum(0f)
                    barChart.axisLeft.setAxisMaximum(100f)

                    barChart.axisLeft.setDrawGridLines(false)
                    barChart.getXAxis().setDrawAxisLine(false);
                    barChart.getXAxis().setDrawGridLines(false);
                    barChart.getXAxis().setDrawLabels(false);

                    barChart.axisLeft.setDrawLabels(false);
                    barChart.axisLeft.setDrawGridLines(false);
                    barChart.axisLeft.setDrawAxisLine(false);
                    barChart.getAxisRight().setDrawLabels(false);
                    barChart.getAxisRight().setDrawGridLines(false);
                    barChart.getAxisRight().setDrawAxisLine(false);

                    barChart.getLegend().setEnabled(false);

                    barChart.setTouchEnabled(false)

                    barChart.description.isEnabled = false

                    barChart.invalidate()

                }
    }

    private fun populateButtonBar(barChart: BarChart, layout: LinearLayout, neighbourLayout: LinearLayout, pollData: Array<PollingData>, barChartIndex: Int, partyMap: Map<String, Int>, defaultSelected: Array<String>) {
        for (d in pollData) {
            val partyPercent = resultStringToFloat(d.data[0].percent)
            val valueIndex: Int = partyMap.get(d.party)!!;
            val logo = LogoButton(app.baseContext, CurrentParties.getParty(d.party), partyPercent, barChart, barChartIndex, valueIndex, defaultSelected.contains(d.party))
            logo.clickListener = {
                if (!logo.active) {
                    logo.add()
                    neighbourLayout.children
                            .find { (it as? LogoButton)?.id == logo.id }
                            .also { (it as? LogoButton)?.remove() }
                } else {
                    logo.remove()
                    neighbourLayout.children
                            .find { (it as? LogoButton)?.id == logo.id }
                            .also { (it as? LogoButton)?.add() }
                }
            }
            layout.addView(logo);
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
        return line
    }

    private fun createFiftyPercentLimitLine(): LimitLine {
        val line = LimitLine(50f, "50%");
        line.enableDashedLine(10f, 5f, 0f)
        line.lineWidth = 1f
        line.textSize = 22f
        line.textColor = Color.WHITE
        line.lineColor = Color.WHITE
        line.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        return line;
    }

    private fun resultStringToFloat(percent: String): Float {
        return percent.replace("%", "").replace(",", ".").toFloat()
    }

    private fun initSourceTextView(txtView: TextView, url: String) {
        txtView.setOnClickListener { CustomTabs.openTab(context, url) }
        val spannableString = SpannableString(txtView.text)
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
        txtView.text = spannableString
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                PollingFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    class LogoButton(context: Context, party: Party, val pollingPercent: Float, val chart: BarChart, val barChartIndex: Int, val valueIndex: Int, val isDefault: Boolean) : androidx.appcompat.widget.AppCompatImageView(context) {
        var active: Boolean = false
        var id = party.id

        var clickListener: (() -> (Unit))? = null

        init {
            setImageDrawable(context.resources.getDrawable(party.drawableLogo));
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 200)
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
            setOnClickListener {
                clickListener?.invoke()
            }

            if (isDefault) {
                add();
            }
        }

        fun add() {
            val column = chart.barData.getDataSetByIndex(0).getEntryForIndex(barChartIndex)
            column.yVals.set(valueIndex, pollingPercent);
            chart.invalidate()
            active = true;
            setColorFilter(null)
            scaleX = 1.1f
            scaleY = 1.1f
        }

        fun remove() {
            val column = chart.barData.getDataSetByIndex(0).getEntryForIndex(barChartIndex)
            column.yVals.set(valueIndex, 0f)
            chart.invalidate()
            active = false;
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
            scaleX = 1f
            scaleY = 1f
        }
    }
}

