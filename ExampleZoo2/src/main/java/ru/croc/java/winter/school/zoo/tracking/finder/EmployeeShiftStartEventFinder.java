package ru.croc.java.winter.school.zoo.tracking.finder;

import ru.croc.java.winter.school.zoo.employee.Employee;
import ru.croc.java.winter.school.zoo.tracking.Tracked;
import ru.croc.java.winter.school.zoo.tracking.event.EmployeeAndAnimalInteractionEvent;
import ru.croc.java.winter.school.zoo.tracking.event.EmployeeShiftStartEvent;
import ru.croc.java.winter.school.zoo.tracking.event.TrackingEvent;
import ru.croc.java.winter.school.zoo.tracking.interaction.Interaction;
import ru.croc.java.winter.school.zoo.tracking.interaction.Shift;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Анализатор события {@link EmployeeShiftStartEvent}.
 */

public class EmployeeShiftStartEventFinder implements EventFinder{
    /** Список незавершенных смен работников. */
    private final Map<String, Shift> shifts = new HashMap<>();
    private double zooRadius;

    public EmployeeShiftStartEventFinder(double zooRadius) {
        this.zooRadius = zooRadius;
    }

    @Override
    public List<EmployeeShiftStartEvent> findNext(Tracked updatedTracked, Map<String, Tracked> trackable) {
        final List<EmployeeShiftStartEvent> newEvents = new ArrayList<>();
        if(!(updatedTracked instanceof Employee)){
            return newEvents;
        }
        Employee employee = (Employee) updatedTracked;
        if(onShift(employee)){
            if(!insideZoo(employee)) {
                shifts.get(employee.getId()).setFinishTime(LocalDateTime.now());
                shifts.remove(employee.getId());
            }

        } else {
            if(insideZoo(employee)){
                final Shift shift = new Shift(employee, LocalDateTime.now());
                shifts.put(employee.getId(),shift);
                newEvents.add(new EmployeeShiftStartEvent(shift));

            }
        }
        return newEvents;
    }
    public boolean onShift(Employee employee){
        return shifts.get(employee.getId())!=null;
    }

    public boolean insideZoo(Employee employee){
        return  Math.sqrt(Math.pow(employee.getCurrentLocation().position.x, 2) +
                Math.pow(employee.getCurrentLocation().position.y - zooRadius, 2)) <= zooRadius;
    }

}
