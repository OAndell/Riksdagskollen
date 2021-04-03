package oscar.riksdagskollen.Fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.volley.VolleyError
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.subjects.PublishSubject
import oscar.riksdagskollen.R
import oscar.riksdagskollen.RiksdagskollenApp
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
        val chart = view.findViewById(R.id.polling_chart) as LineChart
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


                    val dataSets: MutableList<ILineDataSet> = ArrayList()

                    for (partyPolling in pollData) {
                        val partyInfo = partyData.find { it.abbreviation == partyPolling.party }
                        if (partyInfo != null) {
                            dataSets.add(createPartyDataSet(partyPolling, partyInfo))
                        }
                    }

                    val lineData = LineData(dataSets)
                    chart.data = lineData;
                    chart.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    chart.xAxis.setValueFormatter { value, axis -> pollData[0].data.reversed()[value.toInt()].period }
                    chart.xAxis.setLabelCount(4)
                    chart.invalidate()
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
}