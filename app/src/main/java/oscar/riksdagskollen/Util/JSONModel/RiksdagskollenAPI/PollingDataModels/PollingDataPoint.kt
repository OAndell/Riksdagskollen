package oscar.riksdagskollen.Util.JSONModel.RiksdagskollenAPI.PollingDataModels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class PollingDataPoint(val period: String, val percent: String) : Parcelable
