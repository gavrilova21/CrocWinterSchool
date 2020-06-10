package ru.croc.java.winter.school.zoo.tracking;

import ru.croc.java.winter.school.zoo.animal.Animal;
import ru.croc.java.winter.school.zoo.employee.Employee;
import ru.croc.java.winter.school.zoo.tracking.event.EmployeeAndAnimalInteractionEvent;
import ru.croc.java.winter.school.zoo.tracking.event.EmployeeShiftStartEvent;
import ru.croc.java.winter.school.zoo.tracking.event.TrackingEvent;
import ru.croc.java.winter.school.zoo.tracking.finder.TrackedInteractionEventFinder;
import ru.croc.java.winter.school.zoo.tracking.finder.EmployeeShiftStartEventFinder;
import ru.croc.java.winter.school.zoo.tracking.finder.EventFinder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Сервис отслеживания {@link Tracked}.
 */
public class TrackingService {
    /** Отслеживаемые объекты. ид -> объект. */
    private final Map<String, Tracked> trackable;
    /** Журнал событий. */
    private final List<TrackingEvent> events;
    /** Анализаторы событий. */
    private final List<EventFinder> eventFinders;

    public TrackingService() {
        trackable = new HashMap<>();
        events = new ArrayList<>();
        eventFinders = new ArrayList<>();
        eventFinders.add(new EmployeeShiftStartEventFinder(100));
        eventFinders.add(new TrackedInteractionEventFinder(1));
    }

    /**
     * Добавляем новый объект для отслеживания.
     *
     * @param tracked новый объект
     */
    public void add(Tracked tracked) {
        trackable.put(tracked.getId(), tracked);
    }

    /**
     * Пришли данные с GPS-датчика(обработанные).
     *
     * @param id ид отсл. объекта
     * @param x Х
     * @param y Y
     */
    public void update(String id, double x, double y) {
        if (!trackable.containsKey(id)) {
            return;
        }

        trackable.get(id).updatePosition(x, y);
        for (EventFinder eventFinder : eventFinders) {
            events.addAll(eventFinder.findNext(trackable.get(id), trackable));
        }
    }

    /**
     *
     * @param id ид сотрудника
     * @return сколько времени сотрудник провел с подопечными животными
     */
    public long timeSpendWithCaredAnimal(String id){
        Employee employee = (Employee) trackable.get(id);
        Animal animal;
        long timeSpend = 0;
        for (TrackingEvent event : events){
            if(event instanceof EmployeeAndAnimalInteractionEvent){
                if(((EmployeeAndAnimalInteractionEvent) event).getInteraction().getA() == employee){
                    animal = (Animal) ((EmployeeAndAnimalInteractionEvent) event).getInteraction().getB();
                }else {
                    animal = (Animal) ((EmployeeAndAnimalInteractionEvent) event).getInteraction().getA();
                }
                if(employee.isCare(animal)){
                    timeSpend += ((EmployeeAndAnimalInteractionEvent) event).getInteraction().getDuration();
                }
            }
        }
        return timeSpend;
    }

    /**
     *
     * @param id ид сотрудника
     * @return сколько раз выводил животных из зоопарка
     */
    public long countTimesEmployeeStealAnimals(String id){
        Employee employee = (Employee) trackable.get(id);
        Animal animal;
        HashSet<Animal> stolenAnimals = new HashSet<>();
        LocalDateTime shiftStart = null, shiftEnd = null, interactionStart, interactionEnd;
        for (TrackingEvent event : events){
            if(event instanceof EmployeeShiftStartEvent){
                if(((EmployeeShiftStartEvent) event).getShift().getEmployee()==employee){
                    shiftStart = ((EmployeeShiftStartEvent) event).getShift().getStartTime();
                    shiftEnd = ((EmployeeShiftStartEvent) event).getShift().getFinishTime();
                }
            }
            if(event instanceof EmployeeAndAnimalInteractionEvent){
                if(((EmployeeAndAnimalInteractionEvent) event).getInteraction().getA() == employee){
                    animal = (Animal) ((EmployeeAndAnimalInteractionEvent) event).getInteraction().getB();
                }else {
                    animal = (Animal) ((EmployeeAndAnimalInteractionEvent) event).getInteraction().getA();
                }
                interactionStart = ((EmployeeAndAnimalInteractionEvent) event).getInteraction().getStartTime();
                interactionEnd = ((EmployeeAndAnimalInteractionEvent) event).getInteraction().getFinishTime();
                if((shiftStart==null)||(interactionStart.isBefore(shiftStart))|| // животное прыгнуло через забор
                        ((interactionEnd!=null)&&(shiftEnd!=null)&&(interactionEnd.isAfter(shiftEnd)))|| // вывели животное и спрятали
                        (interactionEnd==null)&&(shiftEnd!=null)) { // смена закончилась, но сотрудник еще рядом с животным
                    stolenAnimals.add(animal);
                }
            }
        }
        return stolenAnimals.size();
    }
    /**
     * Снимаем слежение с объекта.
     *
     * @param tracked объект
     */
    public void remove(Tracked tracked) {
        trackable.remove(tracked);
    }

    public Set<Tracked> getTrackable() {
        return new HashSet<>(trackable.values());
    }

    public List<TrackingEvent> getEvents() {
        return events;
    }
}
