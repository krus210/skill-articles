package ru.skillbranch.skillarticles.ui.custom.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.Spannable
import android.text.Spanned
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.core.graphics.withTranslation

@SuppressLint("ViewConstructor", "AppCompatCustomView")
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
class MarkdownTextView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attr, defStyleAttr) {

//    constructor(context: Context, fontSize: Float) : this(context, fontSize, null)

//    override var fontSize: Float
//
//    override val spannableContent: Spannable
//
//    private val color  //colorOnBackground
//    private val focusRect = Rect()

    private val searchBgHelper = SearchBgHelper(context) { top, bottom ->
        //TODO implement me
    }


    override fun onDraw(canvas: Canvas) {
        if (text is Spanned && layout != null) {
            canvas.withTranslation(totalPaddingLeft.toFloat(), totalPaddingRight.toFloat()) {
                searchBgHelper.draw(canvas, text as Spanned, layout)
            }
        }
        super.onDraw(canvas)
    }
}