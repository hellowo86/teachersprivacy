package com.day2life.timeblocks.dialog

import android.app.Dialog
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.hellowo.teachersprivacy.R
import com.hellowo.teachersprivacy.ui.dialog.BottomSheetDialog

class TwoButtonBottomDialog : BottomSheetDialog() {
    lateinit var dialogInterface: DialogInterface
    private var yesStringId: Int = 0
    private var noStringId: Int = 0

    fun init(dialogInterface: DialogInterface, yesStringId: Int, noStringId: Int){
        this.dialogInterface = dialogInterface
        this.yesStringId = yesStringId
        this.noStringId = noStringId
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.dialog_two_button_bottom, null)
        dialog.setContentView(contentView)

        val layoutParams = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        sheetBehavior = layoutParams.behavior as BottomSheetBehavior<*>?
        if (sheetBehavior != null) {
            sheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)

            val yesBtn = contentView.findViewById<TextView>(R.id.yesBtn)
            val noBtn = contentView.findViewById<TextView>(R.id.noBtn)
            yesBtn.setText(yesStringId)
            noBtn.setText(noStringId)

            yesBtn.setOnClickListener { dialogInterface.onYes() }
            noBtn.setOnClickListener { dialogInterface.onNo() }
        }
    }

    interface DialogInterface {
        fun onYes()
        fun onNo()
    }
}
