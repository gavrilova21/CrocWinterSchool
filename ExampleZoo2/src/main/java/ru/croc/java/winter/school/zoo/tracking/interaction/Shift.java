package ru.croc.java.winter.school.zoo.tracking.interaction;

import ru.croc.java.winter.school.zoo.employee.Employee;
import ru.croc.java.winter.school.zoo.tracking.Tracked;

import java.time.Duration;
import java.time.LocalDateTime;

public class Shift {
    private final Employee employee;
    private final LocalDateTime startTime;
    private LocalDateTime finishTime;

    /**
     * Фиксируется начало смены работника
     * @param employee работник
     * @param startTime время начала
     */
    public Shift(Employee employee,  LocalDateTime startTime) {
        this.employee = employee;
        this.startTime = startTime;
        finishTime = null;
    }

    /**
     * Время окончания смены.
     *
     * @return время, null если объекты еще взаимодействуют
     */
    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    /**
     * Устанавливаем время окончания смены.
     *
     * @param finishTime время окончания
     */
    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public Tracked getEmployee() {
        return employee;
    }


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public long getDuration() {
        Duration duration;
        if (finishTime!=null) {
            duration = Duration.between(finishTime, startTime);
        } else {
            duration = Duration.between(LocalDateTime.now(), startTime);
        }
        return Math.abs(duration.toMillis());
    }
}
