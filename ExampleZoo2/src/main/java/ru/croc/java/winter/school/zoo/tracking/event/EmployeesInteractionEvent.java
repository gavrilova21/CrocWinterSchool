package ru.croc.java.winter.school.zoo.tracking.event;

import ru.croc.java.winter.school.zoo.tracking.interaction.Interaction;

public class EmployeesInteractionEvent extends TrackingEvent {
    private final Interaction interaction;

    public EmployeesInteractionEvent(Interaction interaction) {
        super(interaction.getStartTime());
        this.interaction = interaction;
    }

    public Interaction getInteraction() {
        return interaction;
    }
}
