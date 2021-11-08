package io.github.angpysha.calendardateselector

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import io.github.angpysha.calendardateselector.Enums.ECalendarMode
import io.github.angpysha.calendardateselector.Enums.EStartWeekType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class CalendarDateSelector @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttribute: Int = -1
) : LinearLayout(context, attrs, defStyleAttribute) {
    @RequiresApi(Build.VERSION_CODES.O)
    private var currentMonth: Int = LocalDate.now().month.value
    private var currentYear: Int = LocalDate.now().year


    private lateinit var monthStr: TextView

    private var currentSelectedDate: CalendarMonthItem? = null
    private var currentSelectedDateRange: List<CalendarMonthItem>? = null

    private var onItemClicked: ((cal: DateSelectedResult) -> Unit)? = null
    private var dates: List<CalendarMonthItem>? = null

    private var calendarMode: ECalendarMode = ECalendarMode.Month

    private var _startWeekType: EStartWeekType = EStartWeekType.Monday
    var startWeekType: EStartWeekType
        get() = _startWeekType
        set(value) {
            _startWeekType = value

        }
    private var _selectionMode: ESelectionType = ESelectionType.Single
    var selectionMode: ESelectionType
        get() = _selectionMode
        set(value) {
            _selectionMode = value
            OnSelectionModeChanged()
        }

    private fun OnSelectionModeChanged() {
        clearPrevSingleSelction()
        currentSelectedDate = null


        updateTextViewsForRange(Color.TRANSPARENT)
        currentSelectedDateRange = null
    }

    init {
        context?.let {
            inflate(context, R.layout.calendar_main, this)
            monthStr = findViewById(R.id.calendar_month_string)
//            setupWeekBar()
            //          insertDatesIntoLayout()
            var prevButton = findViewById<Button>(R.id.calendar_prev_but)
            prevButton.setOnClickListener {
                if (currentMonth == 1) {
                    currentMonth = 12
                    currentYear -= 1
                } else {
                    currentMonth -= 1
                }
                clearOldLayout()
                insertDatesIntoLayout()
                updateSelectionVisual()
            }

            var nextButton = findViewById<Button>(R.id.calendar_next_but)
            nextButton.setOnClickListener {
                if (currentMonth == 12) {
                    currentMonth = 1;
                    currentYear += 1;
                } else {
                    currentMonth += 1
                }
                clearOldLayout()
                insertDatesIntoLayout()
                updateSelectionVisual()
            }

            monthStr.setOnClickListener {
                if (calendarMode == ECalendarMode.Month) {
                    clearOldLayout()
                    var weekBar = findViewById<LinearLayout>(R.id.calendar_view_week_bar)
                    weekBar.visibility = View.GONE
                    monthStr.text = currentYear.toString()
                    addMonthesLayout()
                }
            }
        }
    }

    private fun addMonthesLayout() {
        var v = findViewById<LinearLayout>(R.id.calendar_container_main)
        var linearLayout: LinearLayout = LinearLayout(context)
        linearLayout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        linearLayout.orientation = LinearLayout.VERTICAL
        var layouts = emptyList<LinearLayout>().toMutableList()
        var monthValue = 1
        for (i in 0..3) {

                var layout = LinearLayout(context)
                layout.orientation = LinearLayout.HORIZONTAL
                layout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                layouts.add(layout)
            var lastLayout = layouts.last()
            for (j in 0..2) {
                var height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,30f,context.resources.displayMetrics).toInt()
                var textView: TextView = TextView(context)
                val lay_params = LinearLayout.LayoutParams(0, height)
                lay_params.weight = 1f
                textView.layoutParams = lay_params
                textView.text = Month.of(monthValue).getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                var monthTmp = monthValue
                textView.setOnClickListener {
                    currentMonth = monthTmp
                    clearOldLayout()
                    insertDatesIntoLayout()
                    var weekBar = findViewById<LinearLayout>(R.id.calendar_view_week_bar)
                    weekBar.visibility = View.VISIBLE
                }
                lastLayout.addView(textView)
                monthValue+=1
            }
        }

        for (l in layouts) {
            linearLayout.addView(l)
        }

        v.addView(linearLayout)
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        //    setupWeekBar()
//        insertDatesIntoLayout()
    }

    private var _isLoaded: Boolean = false
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (!_isLoaded) {
            setupWeekBar()
            insertDatesIntoLayout()
            _isLoaded = true
        }
    }

    private fun setupWeekBar() {
        val txtView1 = findViewById<TextView>(R.id.calendar_view_week_bar1)
        val txtView2 = findViewById<TextView>(R.id.calendar_view_week_bar2)
        val txtView3 = findViewById<TextView>(R.id.calendar_view_week_bar3)
        val txtView4 = findViewById<TextView>(R.id.calendar_view_week_bar4)
        val txtView5 = findViewById<TextView>(R.id.calendar_view_week_bar5)
        val txtView6 = findViewById<TextView>(R.id.calendar_view_week_bar6)
        val txtView7 = findViewById<TextView>(R.id.calendar_view_week_bar7)

        if (startWeekType == EStartWeekType.Monday) {
            txtView1.text = toLocalizedDayWeek(DayOfWeek.MONDAY)
            txtView1.setTextColor(Color.BLACK)

            txtView2.text = toLocalizedDayWeek(DayOfWeek.TUESDAY)
            txtView2.setTextColor(Color.BLACK)

            txtView3.text = toLocalizedDayWeek(DayOfWeek.WEDNESDAY)
            txtView3.setTextColor(Color.BLACK)


            txtView4.text = toLocalizedDayWeek(DayOfWeek.THURSDAY)
            txtView4.setTextColor(Color.BLACK)

            txtView5.text = toLocalizedDayWeek(DayOfWeek.FRIDAY)
            txtView5.setTextColor(Color.BLACK)


            txtView6.text = toLocalizedDayWeek(DayOfWeek.SATURDAY)
            txtView6.setTextColor(Color.RED)


            txtView7.text = toLocalizedDayWeek(DayOfWeek.SUNDAY)
            txtView7.setTextColor(Color.RED)
        } else {
            txtView2.text = toLocalizedDayWeek(DayOfWeek.MONDAY)
            txtView2.setTextColor(Color.BLACK)

            txtView3.text = toLocalizedDayWeek(DayOfWeek.TUESDAY)
            txtView3.setTextColor(Color.BLACK)

            txtView4.text = toLocalizedDayWeek(DayOfWeek.WEDNESDAY)
            txtView4.setTextColor(Color.BLACK)


            txtView5.text = toLocalizedDayWeek(DayOfWeek.THURSDAY)
            txtView5.setTextColor(Color.BLACK)

            txtView6.text = toLocalizedDayWeek(DayOfWeek.FRIDAY)
            txtView6.setTextColor(Color.BLACK)


            txtView7.text = toLocalizedDayWeek(DayOfWeek.SATURDAY)
            txtView7.setTextColor(Color.RED)


            txtView1.text = toLocalizedDayWeek(DayOfWeek.SUNDAY)
            txtView1.setTextColor(Color.RED)
        }
    }

    private fun toLocalizedDayWeek(dayOfWeek: DayOfWeek): String {
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
    }

    private fun updateSelectionVisual() {
        if (selectionMode == ESelectionType.Single) {

        } else {
            updateTextViewsForRange(Color.CYAN)
        }
    }

    private fun insertDatesIntoLayout() {
        val linearLayout = LinearLayout(context)
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        linearLayout.layoutParams = lp
        linearLayout.orientation = LinearLayout.VERTICAL

        var v = findViewById<LinearLayout>(R.id.calendar_container_main)
        dates = getDaysForCurrentMonth()
        var layouts = emptyList<LinearLayout>().toMutableList()
        if (dates == null)
            return
        dates!!.forEachIndexed { index, calendarMonthItem ->
            if (index % 7 == 0) {
                var ll = LinearLayout(context)
                var lpp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                ll.layoutParams = lpp
                ll.orientation = LinearLayout.HORIZONTAL
                layouts.add(ll)
            }

            var px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                30f,
                context.resources.displayMetrics
            )

            var l = layouts.last()
            var item_lp = LayoutParams(0, px.toInt())
            item_lp.weight = 1f
            var txt = TextView(context)
            txt.layoutParams = item_lp
            txt.text = calendarMonthItem.day.toString()
            txt.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            txt.setTextColor(Color.BLACK)

            txt.setOnClickListener {
                if (calendarMonthItem.isSelectable) {
                    if (selectionMode == ESelectionType.Single) {
                        if (currentSelectedDate != null && currentSelectedDate!!.date.monthValue == calendarMonthItem.date.monthValue)
                            clearPrevSingleSelction()

                        currentSelectedDate = calendarMonthItem
//                        oldTxt?.let {
//                            oldTxt?.setBackgroundColor(Color.TRANSPARENT)
//                        }


                        txt?.setBackgroundColor(Color.CYAN)
                        //  oldTxt = txt
                        onItemClicked?.invoke(SingleDateSelectedResult(calendarMonthItem))
                    } else {
                        val firstClick = currentSelectedDateRange == null
                        if (firstClick) {
                            currentSelectedDateRange = listOf(calendarMonthItem)
                            txt?.setBackgroundColor(Color.CYAN)
                        } else if (currentSelectedDateRange != null && currentSelectedDateRange!!.count() == 1) {
                            var date = currentSelectedDateRange?.first()

                            date?.let {
                                var inx1 = dates!!.indexOf(date)
                                var inx2 = dates!!.indexOf(calendarMonthItem)
                                if (date.date.monthValue == calendarMonthItem.date.monthValue) {
                                    if (inx1 == inx2) {
                                        return@setOnClickListener
                                    } else if (inx1 < inx2) {
                                        currentSelectedDateRange = dates!!.slice(inx1..inx2)
                                        calendarBorders = toLayoutPositionRange(inx1, inx2)
                                    } else {
                                        currentSelectedDateRange = dates!!.slice(inx2..inx1)
                                        calendarBorders = toLayoutPositionRange(inx2, inx1)
                                    }
                                    updateTextViews()
                                    onItemClicked?.invoke(
                                        RangeDateSelectedResult(
                                            currentSelectedDateRange!!
                                        )
                                    )
                                } else {
                                    if (date.date < calendarMonthItem.date) {


                                        currentSelectedDateRange =
                                            getDateRangeForPeriod(date.date, calendarMonthItem.date)
                                    } else {
                                        currentSelectedDateRange =
                                            getDateRangeForPeriod(calendarMonthItem.date, date.date)
                                    }
                                    updateTextViewsForRange(Color.CYAN)
                                    onItemClicked?.invoke(
                                        RangeDateSelectedResult(
                                            currentSelectedDateRange!!
                                        )
                                    )
                                }
                            }
                        } else {
                            updateTextViewsForRange(Color.TRANSPARENT)
                            currentSelectedDateRange = null
                            onItemClicked?.invoke(RangeDateSelectedResult(emptyList()))
                        }
                    }
                }

            }

            if ((index % 7 == 5 || index % 7 == 6)) {
                txt.setTextColor(Color.RED)
            }
            if ((index < 7 && calendarMonthItem.day > 20) || (index > 28 && calendarMonthItem.day < 10)) {
                txt.setTextColor(Color.LTGRAY)
            }
            var now = LocalDate.now()
            if (calendarMonthItem.date.dayOfMonth == now.dayOfMonth && calendarMonthItem.date.year == now.year && calendarMonthItem.date.monthValue == now.monthValue) {
                txt.setTextColor(Color.MAGENTA)
            }


            l.addView(txt)
        }
        layouts.forEach { linearLayout.addView(it) }
        v.addView(linearLayout)
    }

    private fun clearPrevSingleSelction() {
        if (currentSelectedDate != null) {
            var prevSelectedIndex = dates!!.indexOfFirst { it.date == currentSelectedDate!!.date }
            var row = prevSelectedIndex / 7
            var col = prevSelectedIndex - row * 7
            updateTextViewBackgoround(Color.TRANSPARENT, row, col)
        }
    }

    private fun updateTextViewsForRange(color: Int) {

        var v = findViewById<LinearLayout>(R.id.calendar_container_main)
        if (v.childCount > 0) {
            var chld = v.children.first() as LinearLayout
            chld.children.forEachIndexed { index, view ->
                val vg = view as LinearLayout
                vg.children.forEachIndexed { index2, view2 ->
                    var txtView = view2 as TextView
                    var idx = rowAndColToIndex(index, index2)
                    currentSelectedDateRange?.let {
                        val selItem = dates!![idx]
                        if (currentSelectedDateRange!!.any { it.date == selItem.date } && selItem.isSelectable) {
                            txtView.setBackgroundColor(color)
                        }
                    }
                    // var pos = toLayoutPoistiion()

                }
            }
        }
    }

    private fun updateTextViewBackgoround(color: Int, row: Int, col: Int) {
        var v = findViewById<LinearLayout>(R.id.calendar_container_main)
        var chld = v.children.first() as LinearLayout
        val txtView =
            (chld.children.elementAt(row) as ViewGroup).children.elementAt(col) as TextView
        txtView.setBackgroundColor(color)
    }

    private var calendarBorders: CalendarItemRange? = null


    private fun rowAndColToIndex(row: Int, col: Int): Int {
        return col + row * 7
    }

    private fun getDateRangeForPeriod(start: LocalDate, end: LocalDate): List<CalendarMonthItem> {
        val daysCount = start.until(end, ChronoUnit.DAYS)

        var datesList = emptyList<CalendarMonthItem>().toMutableList()

        for (i in 0..daysCount) {
            val dateTmp = start.plusDays(i)
            datesList.add(CalendarMonthItem(dateTmp.dayOfMonth, dateTmp, false))
        }
        return datesList
    }

    private fun clearOldLayout() {
        var v = findViewById<LinearLayout>(R.id.calendar_container_main)
        v.removeAllViews()
    }

    fun setOnItemClickedListener(onclick: (cal: DateSelectedResult) -> Unit) {
        onItemClicked = onclick
    }

    @SuppressLint("NewApi")
    private fun getDaysForCurrentMonth(): List<CalendarMonthItem> {
        val firstDayOfmonth = LocalDate.of(currentYear, currentMonth, 1)
        var dayOfweek = firstDayOfmonth.dayOfWeek.value

        var daysInMonth: Int = firstDayOfmonth.month.maxLength()
        if (firstDayOfmonth.isLeapYear && firstDayOfmonth.monthValue == 2) {
            daysInMonth = 29
        } else if (!firstDayOfmonth.isLeapYear && firstDayOfmonth.monthValue == 2) {
            daysInMonth = 28
        }
        var days = (1..daysInMonth).toList().map { it ->
            CalendarMonthItem(
                it,
                LocalDate.of(firstDayOfmonth.year, firstDayOfmonth.month.value, it),
                true
            )
        }
        var monthPrev = firstDayOfmonth.minusMonths(1)
        var daysInPrevMonth: Int = monthPrev.month.maxLength()

        if (monthPrev.isLeapYear && monthPrev.monthValue == 2) {
            daysInPrevMonth = 29
        } else if (!monthPrev.isLeapYear && monthPrev.monthValue == 2) {
            daysInPrevMonth = 28
        }
        if (startWeekType == EStartWeekType.Sunday) {
            dayOfweek = convertDayOfWeekToImperial(dayOfweek)
        }
        if (dayOfweek != 1) {
            val prevMonth = firstDayOfmonth.minusDays((dayOfweek - 1).toLong())
            var beginDay = firstDayOfmonth.minusDays((dayOfweek - 1).toLong()).dayOfMonth
            var ofsset = (beginDay..daysInPrevMonth).toList()
                .map {
                    CalendarMonthItem(
                        it,
                        LocalDate.of(prevMonth.year, prevMonth.month, it),
                        false
                    )
                }
            days = concatenate(ofsset, days)
        }


        if (days.count() % 7 != 0) {
            val toAdd = 7 - days.count() % 7
            var nextMonth = firstDayOfmonth.plusMonths(1)
            var toAddCol = (1..toAdd).toList().map {
                CalendarMonthItem(
                    it,
                    LocalDate.of(nextMonth.year, nextMonth.month, it),
                    false
                )
            }
            days = concatenate(days, toAddCol)

        }
        monthStr.text = firstDayOfmonth.month.getDisplayName(
            TextStyle.FULL,
            Locale.ENGLISH
        ) + ", " + currentYear
        return days

    }

    private fun convertDayOfWeekToImperial(day: Int): Int {
        if (day == 7) {
            return 1
        } else {
            return day + 1
        }
    }

    private fun toLayoutPositionRange(idx1: Int, idx2: Int): CalendarItemRange {
        var startRow = idx1 / 7;
        var startCol = idx1 - startRow * 7;
        var endRow = idx2 / 7;
        var endCol = idx2 - endRow * 7;


        return CalendarItemRange(
            CalendarItemPosition(startRow, startCol),
            CalendarItemPosition(endRow, endCol)
        )
    }

    private fun isInRange(row: Int, col: Int): Boolean {
        calendarBorders?.let {
            if (row < calendarBorders!!.start.row)
                return false
            if (row > calendarBorders!!.end.row)
                return false
            var isInRangeVar = row >= calendarBorders!!.start.row
            if (row == calendarBorders!!.start.row) {
                isInRangeVar = isInRangeVar && col >= calendarBorders!!.start.col
            }
            isInRangeVar == isInRangeVar && row <= calendarBorders!!.end.row
            if (row == calendarBorders!!.end.row) {
                isInRangeVar = isInRangeVar && col <= calendarBorders!!.end.col
            }
            return isInRangeVar
        }
        return false
    }

    private fun updateTextViews() {
        var v = findViewById<LinearLayout>(R.id.calendar_container_main)
        var chld = v.children.first() as LinearLayout
        chld.children.forEachIndexed { index, view ->
            val vg = view as LinearLayout
            vg.children.forEachIndexed { index2, view2 ->
                var txtView = view2 as TextView
                // var pos = toLayoutPoistiion()
                if (isInRange(index, index2)) {
                    txtView?.setBackgroundColor(Color.CYAN)
                }
            }
        }
    }

    fun <T> concatenate(vararg lists: List<T>): List<T> {
        return listOf(*lists).flatten()
    }
}