package ru.croc.java.winter.school.zoo.tracking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.croc.java.winter.school.zoo.Zoo;
import ru.croc.java.winter.school.zoo.animal.Animal;
import ru.croc.java.winter.school.zoo.employee.Employee;
import ru.croc.java.winter.school.zoo.tracking.event.EmployeeAndAnimalInteractionEvent;
import ru.croc.java.winter.school.zoo.tracking.event.EmployeeShiftStartEvent;
import ru.croc.java.winter.school.zoo.tracking.event.EmployeesInteractionEvent;
import ru.croc.java.winter.school.zoo.tracking.interaction.Interaction;
import ru.croc.java.winter.school.zoo.tracking.interaction.Shift;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Проверка сервиса отслеживания объектов в зоопарке.
 */
public class TrackingServiceTest {
    private Zoo zoo;
    private Employee bob, alise;
    private Animal elephant;

    @BeforeEach
    public void init() {
        // Сотрудники
        bob = new Employee("Боб", LocalDate.of(1980, 3, 1));
        alise = new Employee("Алиса", LocalDate.of(1987, 7, 1));
        // Животные
        elephant = new Animal("Слон", LocalDate.now());
        final Animal monkey = new Animal("Обезьяна", LocalDate.now());

        final Zoo zoo = new Zoo("Африка рядом");
        zoo.add(bob, alise);
        zoo.add(elephant, bob);
        zoo.add(monkey, alise);

        this.zoo = zoo;
    }

    @DisplayName("Проверка журнала отслеживания животных")
    @Test
    public void testJournalOfAnimalTracking() throws InterruptedException {
        final TrackingService trackingService = zoo.getTrackingService();

        final Animal lion = new Animal("Лев", LocalDate.of(1990, 3, 8));
        final Set<Tracked> animalsAndEmployees = new HashSet<>();
        animalsAndEmployees.addAll(zoo.getAnimals());
        animalsAndEmployees.addAll(zoo.getEmployees());
        Assertions.assertEquals(animalsAndEmployees, trackingService.getTrackable());

        Assertions.assertFalse(trackingService.getTrackable().contains(lion));
        zoo.add(lion, bob);
        Assertions.assertTrue(trackingService.getTrackable().contains(lion));

        final LocalDateTime beforeTime = LocalDateTime.now();
        Thread.sleep(1);
        trackingService.update(lion.getId(), 0, 0);
        Thread.sleep(1);
        final LocalDateTime betweenTime = LocalDateTime.now();
        Thread.sleep(1);
        trackingService.update(lion.getId(), 10, 10);
        Thread.sleep(1);
        final LocalDateTime afterTime = LocalDateTime.now();

        Assertions.assertTrue(lion.getLocations().get(0).time.isAfter(beforeTime));
        Assertions.assertTrue(lion.getLocations().get(0).time.isBefore(betweenTime));

        Assertions.assertTrue(lion.getLocations().get(1).time.isAfter(betweenTime));
        Assertions.assertTrue(lion.getLocations().get(1).time.isBefore(afterTime));

    }


    @DisplayName("Проверка отслеживания событий взаимодействия животных")
    @Test
    public void testInteractionEmployeeAndAnimal() {
        final TrackingService trackingService = zoo.getTrackingService();

        // начальные позиции
        trackingService.update(bob.getId(), 0, 0);
        trackingService.update(elephant.getId(), 10, 10);
        Assertions.assertEquals(1, trackingService.getEvents().size());

        // Боб подошел к слону
        trackingService.update(bob.getId(), 10, 10);
        Assertions.assertEquals(2, trackingService.getEvents().size());
        final Interaction interaction = ((EmployeeAndAnimalInteractionEvent) trackingService.getEvents().get(1))
                .getInteraction();
        Assertions.assertEquals(interaction.getA(), bob);
        Assertions.assertEquals(interaction.getB(), elephant);
        Assertions.assertNull(interaction.getFinishTime());

        // Боб продолжает стоять рядом со слоном
        trackingService.update(bob.getId(), 10.01, 9.99);
        trackingService.update(elephant.getId(), 9.98, 10.001);
        Assertions.assertEquals(2, trackingService.getEvents().size());
        Assertions.assertNull(interaction.getFinishTime());

        // Слон убежал от Боба
        trackingService.update(elephant.getId(), 5.01, 5.99);
        Assertions.assertEquals(2, trackingService.getEvents().size());
        Assertions.assertNotNull(interaction.getFinishTime());

        // Боб догнал слона
        trackingService.update(bob.getId(), 4.98, 6.02);
        Assertions.assertEquals(3, trackingService.getEvents().size());
    }

    @DisplayName("Проверка отслеживания событий начала и окончания смен работников")
    @Test
    public void testEmployeeShiftStartEvents() {
        final TrackingService trackingService = zoo.getTrackingService();
        //Боб гуляет не выходя на смену
        trackingService.update(bob.getId(), -1, -10);
        Assertions.assertTrue(trackingService.getEvents().isEmpty());

        //Боб зашел в зоопарк
        trackingService.update(bob.getId(), 0, 0);
        Assertions.assertEquals(1, trackingService.getEvents().size());
        final Shift shift = ((EmployeeShiftStartEvent) trackingService.getEvents().get(0))
                .getShift();
        Assertions.assertEquals(shift.getEmployee(), bob);
        Assertions.assertNull(shift.getFinishTime());

        //Боб ходит по зоопарку
        trackingService.update(bob.getId(), 20, 20);
        Assertions.assertEquals(1, trackingService.getEvents().size());

        //Боб покинул зоопарк
        trackingService.update(bob.getId(), -10, -1);
        Assertions.assertEquals(1, trackingService.getEvents().size());
        Assertions.assertNotNull(shift.getFinishTime());

        //Вернулся ночью, чтобы украсть слона
        trackingService.update(bob.getId(), 10, 1);
        Assertions.assertEquals(2, trackingService.getEvents().size());
    }

    @DisplayName("Проверка отслеживания событий взаимодействия сотрудников")
    @Test
    public void testInteractionEmployees() {
        final TrackingService trackingService = zoo.getTrackingService();

        // начальные позиции
        trackingService.update(bob.getId(), 0, 0);
        trackingService.update(alise.getId(), 10, 10);
        Assertions.assertEquals(2, trackingService.getEvents().size());// 2 так как оба запустили смену

        // Боб подошел к Алисе
        trackingService.update(bob.getId(), 10, 10);
        Assertions.assertEquals(3, trackingService.getEvents().size());
        final Interaction interaction = ((EmployeesInteractionEvent) trackingService.getEvents().get(2))
                .getInteraction();
        Assertions.assertEquals(interaction.getA(), bob);
        Assertions.assertEquals(interaction.getB(), alise);
        Assertions.assertNull(interaction.getFinishTime());

        // Боб продолжает стоять рядом со Алисой
        trackingService.update(bob.getId(), 10.01, 9.99);
        trackingService.update(alise.getId(), 9.98, 10.001);
        Assertions.assertEquals(3, trackingService.getEvents().size());
        Assertions.assertNull(interaction.getFinishTime());

        // Алиса убежала от Боба
        trackingService.update(alise.getId(), 5.01, 5.99);
        Assertions.assertEquals(3, trackingService.getEvents().size());
        Assertions.assertNotNull(interaction.getFinishTime());

        // Боб догнал Алису
        trackingService.update(bob.getId(), 4.98, 6.02);
        Assertions.assertEquals(4, trackingService.getEvents().size());
    }

    @DisplayName("Проверка отслеживания времени проведенного сотрудником с подопечным")
    @Test
    public void testTimeSpendWithAnimal() {
        final TrackingService trackingService = zoo.getTrackingService();

        // начальные позиции
        trackingService.update(bob.getId(), 0, 0);
        trackingService.update(elephant.getId(), 10, 10);

        // Боб подошел к слону
        trackingService.update(bob.getId(), 10, 10);
        final Interaction interaction1 = ((EmployeeAndAnimalInteractionEvent) trackingService.getEvents().get(1))
                .getInteraction();

        // Боб продолжает стоять рядом со слоном
        trackingService.update(bob.getId(), 10.01, 9.99);
        trackingService.update(elephant.getId(), 9.98, 10.001);

        // Слон убежал от Боба
        trackingService.update(elephant.getId(), 5.01, 5.99);

        // Боб догнал слона
        trackingService.update(bob.getId(), 4.98, 6.02);
        // Слон cнова убежал от Боба
        final Interaction interaction2 = ((EmployeeAndAnimalInteractionEvent) trackingService.getEvents().get(2))
                .getInteraction();
        trackingService.update(elephant.getId(), 15.01, 15.99);
        Assertions.assertEquals(interaction1.getDuration()+interaction2.getDuration(),
                trackingService.timeSpendWithCaredAnimal(bob.getId()));
    }

    @DisplayName("Проверка отслеживания числа украденных животных")
    @Test
    public void testCountTimesStealAnimals() {
        final TrackingService trackingService = zoo.getTrackingService();

        // начальные позиции
        trackingService.update(bob.getId(), 0, 0);
        trackingService.update(elephant.getId(), 10, 10);

        // Боб подошел к слону
        trackingService.update(bob.getId(), 10, 10);
        final Interaction interaction1 = ((EmployeeAndAnimalInteractionEvent) trackingService.getEvents().get(1))
                .getInteraction();

        // Боб продолжает стоять рядом со слоном
        trackingService.update(bob.getId(), 10.01, 9.99);
        trackingService.update(elephant.getId(), 9.98, 10.001);

        // Боб ведет Слона ко входу пока никто не видит
        trackingService.update(bob.getId(), 0.01, 0.99);
        trackingService.update(elephant.getId(), 0.02, 0.9);
        // Боб выводит Слона за пределы зоопарка
        trackingService.update(bob.getId(), -5.01, -5.99);
        trackingService.update(elephant.getId(), -5.08, -5.991);
        Assertions.assertEquals(1,trackingService.countTimesEmployeeStealAnimals(bob.getId()));
    }
}
