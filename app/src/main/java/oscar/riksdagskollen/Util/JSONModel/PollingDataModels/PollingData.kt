package oscar.riksdagskollen.Util.JSONModel.PollingDataModels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PollingData(val party: String,
                       val source: String,
                       val data: ArrayList<PollingDataPoint>) : Parcelable
