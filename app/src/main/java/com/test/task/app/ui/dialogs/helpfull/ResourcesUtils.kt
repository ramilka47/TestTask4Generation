package com.test.task.app.ui.dialogs.helpfull

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.appcompat.content.res.AppCompatResources


internal object ResourcesUtils {
    private val tmpValueLock = Any()

    private var tmpValue: TypedValue? = TypedValue()

    private fun obtainTempTypedValue(): TypedValue {
        var tmpValue: TypedValue? = null
        synchronized(tmpValueLock) {
            if (ResourcesUtils.tmpValue != null) {
                tmpValue = ResourcesUtils.tmpValue
                ResourcesUtils.tmpValue = null
            }
        }
        return if (tmpValue == null) {
            TypedValue()
        } else tmpValue!!
    }

    private fun releaseTempTypedValue(value: TypedValue) {
        synchronized(tmpValueLock) {
            if (tmpValue == null) {
                tmpValue = value
            }
        }
    }

    @Throws(NotFoundException::class)
    private fun resolveAttribute(
        context: Context, attrId: Int, value: TypedValue, resolveRefs: Boolean
    ) {
        if (!context.theme.resolveAttribute(attrId, value, resolveRefs)) {
            throw NotFoundException(
                "Can't resolve attribute ID #0x" + Integer.toHexString(attrId)
            )
        }
    }

    @Throws(NotFoundException::class)
    fun getAttrFloat(context: Context, id: Int): Float {
        val value = obtainTempTypedValue()
        try {
            resolveAttribute(context, id, value, true)
            if (value.type == TypedValue.TYPE_FLOAT) {
                return value.float
            }
            throw NotFoundException(
                "Resource ID #0x" + Integer.toHexString(id)
                        + " type #0x" + Integer.toHexString(value.type) + " is not valid"
            )
        } finally {
            releaseTempTypedValue(value)
        }
    }

    @Throws(NotFoundException::class)
    fun getAttrDimension(context: Context, id: Int): Float {
        val value = obtainTempTypedValue()
        try {
            resolveAttribute(context, id, value, true)
            if (value.type == TypedValue.TYPE_DIMENSION) {
                return TypedValue.complexToDimension(
                    value.data, context.resources.displayMetrics
                )
            }
            throw NotFoundException(
                "Resource ID #0x" + Integer.toHexString(id)
                        + " type #0x" + Integer.toHexString(value.type) + " is not valid"
            )
        } finally {
            releaseTempTypedValue(value)
        }
    }

    @Throws(NotFoundException::class)
    fun getAttrDrawable(context: Context, id: Int): Drawable? {
        val value = obtainTempTypedValue()
        try {
            resolveAttribute(context, id, value, false)
            if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && value.type <= TypedValue.TYPE_LAST_COLOR_INT
            ) {
                return ColorDrawable(value.data)
            } else if (value.type == TypedValue.TYPE_REFERENCE) {
                return AppCompatResources.getDrawable(context, value.data)
            }
            throw NotFoundException(
                "Resource ID #0x" + Integer.toHexString(id)
                        + " type #0x" + Integer.toHexString(value.type) + " is not valid"
            )
        } finally {
            releaseTempTypedValue(value)
        }
    }
}
