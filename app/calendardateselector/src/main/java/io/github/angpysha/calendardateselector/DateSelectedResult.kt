package io.github.angpysha.calendardateselector

sealed class DateSelectedResult {
    abstract var SelectionType: ESelectionType
}

data class SingleDateSelectedResult(val date: CalendarMonthItem) : DateSelectedResult() {

    override var SelectionType: ESelectionType = ESelectionType.Single
}

data class RangeDateSelectedResult(val dateRange: List<CalendarMonthItem>) : DateSelectedResult() {
    override var SelectionType: ESelectionType = ESelectionType.Range
}