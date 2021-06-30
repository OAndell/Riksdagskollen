package oscar.riksdagskollen.Util.JSONModel.RiksdagskollenAPI.PartyDataModels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PartyData(val name: String,
                     val abbreviation: String,
                     val twitter: String,
                     val website: String,
                     val color: String,
                     val electionResult: String,
                     val seats: Int,
                     val description: PartyDescription) : Parcelable
