package ru.croc.java.winter.school.zoo.tracking.event;

import ru.croc.java.winter.school.zoo.tracking.interaction.Shift;

public class EmployeeShiftStartEvent extends TrackingEvent {
    private final Shift shift;

    public EmployeeShiftStartEvent(Shift shift) {
        super(shift.getStartTime());
        this.shift = shift;
    }

    public Shift getShift() {
        return shift;
    }
}
