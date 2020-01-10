/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jan222ik.eisteecounter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.jan222ik.eisteecounter.data.entity.Drink


class DrinkListAdapter internal constructor(
        context: Context,
        private val addDrinkAmountImpl: AddDrinkAmount
) : RecyclerView.Adapter<DrinkListAdapter.DrinkViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var drinks = emptyList<Drink>() // Cached copy of words

    inner class DrinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordItemView: TextView = itemView.findViewById(R.id.textView)
        val add05LBtn: Button = itemView.findViewById(R.id.add_button_half_L)
        val add1LBtn: Button = itemView.findViewById(R.id.add_button_one_L)
        val add1And05LBtn: Button = itemView.findViewById(R.id.add_button_one_and_half_L)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinkViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return DrinkViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DrinkViewHolder, position: Int) {
        val current = drinks[position]
        holder.wordItemView.text = "${current.drinkName} (${current.drinkId})"
        holder.add05LBtn.setOnClickListener {
            addDrinkAmount(current, 0.5)
        }
        holder.add1LBtn.setOnClickListener {
            addDrinkAmount(current, 1.0)
        }
        holder.add1And05LBtn.setOnClickListener {
            addDrinkAmount(current, 1.5)
        }
    }

    fun addDrinkAmount(drink: Drink, amount: Double) {
        addDrinkAmountImpl.addDrinkAmount(drink, amount)
    }

    interface AddDrinkAmount {
        fun addDrinkAmount(drink: Drink, amount: Double)
    }

    internal fun setDrinks(words: List<Drink>) {
        this.drinks = words
        notifyDataSetChanged()
    }

    override fun getItemCount() = drinks.size
}


