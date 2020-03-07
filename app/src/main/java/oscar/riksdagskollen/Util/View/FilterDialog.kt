package oscar.riksdagskollen.Util.View

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.filter_dialog.*
import kotlinx.android.synthetic.main.filter_dialog.view.*
import oscar.riksdagskollen.R
import oscar.riksdagskollen.Util.JSONModel.Party


class FilterDialog(val testTitle: String, val displayNames: Array<CharSequence>, var checked: BooleanArray) : DialogFragment() {

  lateinit var listView: ListView
  var positiveButtonListener: View.OnClickListener? = null
  var negativeButtonListener: View.OnClickListener? = null
  var itemSelectedListener: OnItemSelectedListener? = null
  var onDismissListener: DialogInterface.OnDismissListener? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(DialogFragment.STYLE_NORMAL, R.style.AlertDialogCustom)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.filter_dialog, container)
    view.alertTitle.text = testTitle

    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    listView = view.listview
    val displayNamesStringArray = displayNames.map { it.toString() }.toTypedArray()

    android.R.layout.select_dialog_multichoice
    listView.adapter = ArrayAdapter<String>(context!!,
            R.layout.filter_row, displayNamesStringArray)
    checked.forEachIndexed { index, isChecked -> listView.setItemChecked(index, isChecked) }

    refreshSelectAllCheckbox()

    positive_button.text = "OK"
    negative_button.text = "Avbryt"

    positive_button.setOnClickListener { v ->
      positiveButtonListener?.onClick(v)
      dismiss()
    }

    negative_button.setOnClickListener { v ->
      negativeButtonListener?.onClick(v)
      dismiss()
    }

    listView.onItemClickListener = object : AdapterView.OnItemClickListener {
      override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        itemSelectedListener?.onItemSelected(position, listView.isItemChecked(position))
        refreshSelectAllCheckbox()
      }
    }
    contentPanel.setOnClickListener { v ->
      select_all.performClick()
    }

    select_all.setOnStateChangedListener { checkBox, state ->
      when (state) {
        true -> {
          displayNames.forEachIndexed { index, _ ->
            listView.setItemChecked(index, true)
            itemSelectedListener?.onItemSelected(index, true)
          }
        }
        false -> {
          displayNames.forEachIndexed { index, _ ->
            listView.setItemChecked(index, false)
            itemSelectedListener?.onItemSelected(index, false)
          }
        }
      }
    }


  }

  private fun refreshSelectAllCheckbox() {
    if (listView.checkedItemCount == displayNames.size) {
      select_all.setState(true)
    } else if (listView.checkedItemCount == 0) {
      select_all.setState(false)
    } else {
      select_all.setState(null)
    }
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    onDismissListener?.onDismiss(dialog)
  }

  fun showFilterDialog(parties: ArrayList<Party>, checked: BooleanArray, displayNames: Array<CharSequence>) {

  }

  interface OnItemSelectedListener {
    fun onItemSelected(which: Int, checked: Boolean)
  }


}