package org.application.objects.animals.herbivorous;

import org.application.annotations.Config;
import org.application.console.Console;
import org.application.objects.animals.Herbivorous;
import org.application.config.database.Record;

@Config(filePath = "animals/sheep.yaml")
public class Sheep extends Herbivorous {

    private final Record record;

    public Sheep(Record record) {
        super(record);
        this.record = record;
    }

    @Override
    public Sheep multiply() {
        Console.logBornOrganism(this.getClass());
        return new Sheep(record);
    }
}