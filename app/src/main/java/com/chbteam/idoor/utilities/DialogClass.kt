package com.chbteam.idoor.utilities

import android.app.AlertDialog
import android.content.Context
import com.google.android.material.textfield.TextInputLayout
import com.kaopiz.kprogresshud.KProgressHUD

fun showProgress(context: Context) : KProgressHUD{
    return KProgressHUD.create(context)
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setLabel("Please wait")
        .setDetailsLabel("Downloading data")
        .setCancellable(true)
        .setAnimationSpeed(2)
        .setDimAmount(0.5f)
}
