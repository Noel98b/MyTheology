package com.example.mytheology

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class AdapterClass(context: Context, List: MutableList<MainModel>): BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var itemList = List
    private var updateAndDelete:UpdateAndDelete = context as UpdateAndDelete


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val UID:String = itemList.get(position).UID as String
        val itemTextData: String? = itemList.get(position).sectionTitle as String?
        val done: Boolean? = itemList.get(position).done as? Boolean
        val entries: ArrayList<Entry>? = itemList.get(position).entries as ArrayList<Entry>?

        val view: View
        val viewHolder : ListViewHolder

        if(convertView==null){
            view=inflater.inflate(R.layout.row_itemslayout, parent, false)
            viewHolder = ListViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder=view.tag as ListViewHolder
        }

        viewHolder.textLabel.text = itemTextData


        viewHolder.isDeleted.setOnClickListener{
            updateAndDelete.onItemDelete(UID)

        }

        viewHolder.relLayout.setOnClickListener {
            if (itemTextData != null) {
                updateAndDelete.onSectionClick(UID, itemTextData)
            }
        }

        return view

    }

    private class ListViewHolder(row: View){
        val textLabel:TextView=row!!.findViewById(R.id.ItemText) as TextView
        val isDeleted:ImageButton = row!!.findViewById(R.id.close) as ImageButton
        val relLayout:RelativeLayout=row!!.findViewById<RelativeLayout>(R.id.RelLayout)
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