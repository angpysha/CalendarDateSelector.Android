package io.github.angpysha.calendardateselector

data class CalendarItemPosition(val row: Int, val col: Int)

data class CalendarItemRange(val start: CalendarItemPosition, val end: CalendarItemPosition)