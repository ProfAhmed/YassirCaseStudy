package com.osama.pro.app.extension

import android.content.Intent
import androidx.fragment.app.Fragment
import com.osama.pro.app.detail.DetailActivity
import com.osama.pro.app.detail.DetailViewModel.Companion.EXTRA_ID
import com.osama.pro.app.detail.DetailViewModel.Companion.EXTRA_TYPE
import com.osama.pro.core.domain.model.DomainModel

fun Fragment.intentToDetailsActivity(item: DomainModel) =
    Intent(requireActivity(), DetailActivity::class.java).run {
        putExtra(EXTRA_ID, item.id)
        putExtra(EXTRA_TYPE, type)
        startActivity(this)
    }