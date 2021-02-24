package com.example.mytheology


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlin.coroutines.coroutineContext

class EntryAdapter (context: Context, List:MutableList<Entry>): BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var itemList = List
    private var updateAndDeleteEntry:UpdateAndDeleteEntry = context as UpdateAndDeleteEntry


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val entry: Entry = itemList.get(position) as Entry
        val entryID = entry.entryID as String?
        val view: View
        val viewHolder : ListViewHolder

        if(convertView==null){
            view=inflater.inflate(R.layout.row_entries_itemslayout, parent, false)
            viewHolder = ListViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder=view.tag as ListViewHolder
        }

        viewHolder.titleLabel.text = entry.title
        viewHolder.textLabel.text = entry.entry

        viewHolder.isDeleted.setOnClickListener{
            if (entryID != null) {
                updateAndDeleteEntry.onItemDelete(entryID)
            }
        }

        viewHolder.relLayout.setOnClickListener {
            if (entry != null) {
                if (entryID != null) {
                    updateAndDeleteEntry.onSectionClick(entryID)
                }
            }
        }
        return view
    }

    private class ListViewHolder(row: View){
        val titleLabel:TextView=row!!.findViewById(R.id.EntryTitle) as TextView
        val textLabel:TextView=row!!.findViewById(R.id.EntryText) as TextView
        val relLayout:RelativeLayout=row!!.findViewById<RelativeLayout>(R.id.RelLayoutEntry)
        val isDeleted:ImageButton = row!!.findViewById(R.id.entryclose) as ImageButton
    }

    override fun getItem(position: Int): Any {
        return itemList.get(position)
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getCount(): Int {
        return itemList.size
    }

}