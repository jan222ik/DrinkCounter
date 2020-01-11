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

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.jan222ik.eisteecounter.data.entity.Drink
import com.github.jan222ik.eisteecounter.ui.activities.addDrink.AddDrinkActivity
import com.github.jan222ik.eisteecounter.ui.vm.DateOutOfBoundsException
import com.github.jan222ik.eisteecounter.ui.vm.DrinkVM
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineExceptionHandler
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity(), DrinkListAdapter.AddDrinkAmount {

    private val newWordActivityRequestCode = 1
    private lateinit var drinkVM: DrinkVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = DrinkListAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        drinkVM = ViewModelProvider(this).get(DrinkVM::class.java)

        drinkVM.allDrinks.observe(this, Observer { drinks ->
            // Update the cached copy of the drinks in the adapter.
            drinks?.let { adapter.setDrinks(it) }
        })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, AddDrinkActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }

        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            if (throwable::class == DateOutOfBoundsException::class) {
                Toast.makeText(this, "You cannot see this date!", Toast.LENGTH_SHORT).show()
            } else {
                throwable.printStackTrace()
                Toast.makeText(this, "Exception occurred", Toast.LENGTH_SHORT).show()
            }
        }

        nextDate.setOnClickListener {
            drinkVM.nextDate(this, coroutineExceptionHandler)
        }

        prevDate.setOnClickListener {
            drinkVM.prevDate(this, coroutineExceptionHandler)
        }


        drinkVM.allConsumption.observe(this, Observer {
            Log.d("TAG", "onCreate: Update")
            it.date.observe(this, Observer { date ->
                Log.d("TAG", "onCreate: Update Date")
                currentDateTextView.text = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            })
            it.consumptions.observe(this, Observer { consumptions ->
                Log.d("TAG", "onCreate: Update ${consumptions.size} entries")
                Toast.makeText(
                    applicationContext,
                    consumptions.joinToString { consumption -> "${consumption.drinkId}:${consumption.amount}" },
                    Toast.LENGTH_LONG
                ).show()
                if (consumptions.isNotEmpty()) {
                    Log.i("TAG", "onCreate: Create Chart")
                    chart as PieChart
                    chart.setUsePercentValues(true)
                    val sumBy = consumptions.sumBy { it.amount }
                    val entries = consumptions.mapIndexed { index, consumption ->
                        PieEntry(
                            consumption.amount.toFloat() / sumBy,
                            drinkVM.allDrinks.value!!.find { it.drinkId == consumption.drinkId }?.drinkName
                        )
                    }.toMutableList()
                    val set = PieDataSet(entries, "")
                    set.colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
                    val data = PieData(set)
                    data.setValueFormatter(PercentFormatter())
                    chart.rotationAngle = 180F
                    chart.maxAngle = 180F
                    chart.data = data
                    chart.legend.isEnabled = false
                    chart.isRotationEnabled = false
                    chart.description.isEnabled = true
                    chart.setHoleColor(Color.parseColor("#80000000"))

                    val display: Display = windowManager.defaultDisplay
                    val point = Point()
                    display.getSize(point)
                    val height = point.y
                    val offset = (height * 0.9).toInt()

                    val rlParams: RelativeLayout.LayoutParams =
                        chart.layoutParams as RelativeLayout.LayoutParams
                    rlParams.setMargins(0, 0, 0, -offset)
                    chart.layoutParams = rlParams

                    chart.setUsePercentValues(true)
                    chart.invalidate()
                }
            })
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.getStringExtra(AddDrinkActivity.EXTRA_REPLY)?.let {
                val drink = Drink(it)
                drinkVM.insert(drink)
                Unit
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun addDrinkAmount(drink: Drink, amount: Double) {
        val find =
            drinkVM.allConsumption.value?.consumptions?.value?.find { it.drinkId == drink.drinkId }
        if (find == null) {
            drinkVM.createConsumption(drink, amount)
        } else {
            drinkVM.updateAmount(find, amount)
        }
    }
}
