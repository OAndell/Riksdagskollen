package oscar.riksdagskollen.Util.JSONModel.RiksdagskollenAPI.PartyDataModels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class PartyDescription(val text: String, val ideology: Array<String>, val source: String) : Parcelable;
