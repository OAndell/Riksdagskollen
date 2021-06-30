package oscar.riksdagskollen.Util.Helper

import java.text.SimpleDateFormat
import java.util.*

class DateFormatter {
    companion object {

        fun formatShort(inDate: String): String {
            return try {
                println(inDate)
                val formatter = SimpleDateFormat("MMM y", Locale("sv"))
                val date = formatter.parse(inDate);
                val newFormat = SimpleDateFormat("MM/yy", Locale("sv"))
                newFormat.format(date);
            } catch (e: Exception) {
                "";
            }

        }


    }
}