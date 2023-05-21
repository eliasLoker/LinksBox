package com.example.linksbox.utils.stringprovider

interface StringProvider {

    fun getStringByStringRes(stringRes: StringRes, vararg args: String?) : String
}