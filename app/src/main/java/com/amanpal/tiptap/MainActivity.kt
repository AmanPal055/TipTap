package com.amanpal.tiptap

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.sql.Types.NULL

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {

    private lateinit var etBaseAmount: EditText
    private lateinit var etSpitNumber: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipPercent: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var tvAmountForEach: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipPercent = findViewById(R.id.tvTipPercent)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        etSpitNumber = findViewById(R.id.etSplitNumber)
        tvAmountForEach = findViewById(R.id.tvAmountForEach)

        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        changeDescriptionText(INITIAL_TIP_PERCENT)

        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG, "onProgressChanged $p1")
                tvTipPercent.text = "$p1%"
                computeTipAndTotal()
                changeDescriptionText(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
        etBaseAmount.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG,"afterTextChanged $p0")
                computeTipAndTotal()
            }

        })

        etSpitNumber.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) {
                if (computeTipAndTotal().equals(false)){
                    return
                }
                computeSplitBill()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun computeSplitBill() {
        if (etSpitNumber.text.isEmpty() || etSpitNumber.text.toString().toDouble() == 0.0){
            tvAmountForEach.text = ""
            return
        }
//        get number of person

        var noOfPerson = etSpitNumber.text.toString().toDouble()
        val totalAmount = tvTotalAmount.text.toString().toDouble()

//        calculate split amount
        val splitAmount = totalAmount / noOfPerson

//        Update UI
        tvAmountForEach.text = "%.2f".format(splitAmount)
    }

    private fun changeDescriptionText(tipPercent: Int) {
        val tipDescription = when (tipPercent){
            in 0..9 -> "Poor\uD83D\uDE44"
            in 10..14 -> "Acceptable\uD83D\uDE42"
            in 15..24 -> "Good☺️"
            else -> {"Amazing\uD83D\uDE0D"}
        }
        tvTipDescription.text = tipDescription

        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this,R.color.color_worst_tip),
            ContextCompat.getColor(this,R.color.color_best_tip)
        )as  Int
        tvTipDescription.setTextColor(color)
    }

    @SuppressLint("SetTextI18n")
    private fun computeTipAndTotal(): Boolean {
        if (etBaseAmount.text.isEmpty()){
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return false
        }
        // 1. Get value of tip percent and edit text
        val tipPercent = seekBarTip.progress
        val baseAmount = etBaseAmount.text.toString().toDouble()

        // 2. Compute tip and total amount
        val tipAmount = tipPercent * baseAmount / 100
        val totalAmount = tipAmount + baseAmount

        // 3. update the UI
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
        return true
    }
}