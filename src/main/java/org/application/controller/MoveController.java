package org.application.controller;

import org.application.enums.Direction;
import org.application.interfaces.Movable;
import org.application.island.Island;
import org.application.island.Location;
import org.application.objects.Organism;
import org.application.objects.animals.Animal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MoveController extends Controller {


    public MoveController(Island island) {
        super(island);
    }

    @Override
    public void start() {
        Arrays.stream(locations)
                .flatMap(Arrays::stream)
                .peek(this::doAction)
                .flatMap(location -> location.getOrganism().stream())
                .filter(organism -> organism instanceof Animal animal && !animal.isCanMove())
                .forEach(organism -> ((Animal) organism).setCanMove(true));
    }

    @Override
    protected void doAction(Location location) {
        location.getOrganism()
                .stream()
                .filter(organism -> organism instanceof Animal animal && animal.isCanMove())
                .map(organism -> (Movable) organism)
                .forEach(movable -> moveAnimal(movable, location));
    }

    private void moveAnimal(Movable movable, Location currentLocation) {
        Direction[] steps = movable.move();

        Location newLocation = currentLocation;

        for (Direction direction : steps) {
            int x = checkX(newLocation, direction);
            int y = checkY(newLocation, direction);

            newLocation = locations[y][x];
        }

        if (newLocation == currentLocation) return;

        Animal animal = (Animal) movable;
        if (newLocation.checkMaxCountOrganismOnLocation(animal)) return;

        Map<Class<? extends Organism>, Set<Organism>> organismOnLocation = newLocation.getObjects();
        Set<Organism> newLocationOrganismSet = organismOnLocation.get(animal.getClass());
        newLocationOrganismSet = newLocationOrganismSet == null ? new HashSet<>() : newLocationOrganismSet;

        organismOnLocation.merge(animal.getClass(), newLocationOrganismSet, (set1, set2) -> {
            set1.addAll(set2);
            return set1;
        });

        currentLocation.removeOrganism(animal);
        newLocationOrganismSet.add(animal);
        animal.setCanMove(false);
    }

    private int checkX(Location location, Direction direction) {
        int x = location.getX();
        switch (direction) {
            case RIGHT -> ++x;
            case LEFT -> --x;
        }
        if (x < 0) x++;
        if (x >= locations[0].length) x--;
        return x;
    }

    private int checkY(Location location, Direction direction) {
        int y = location.getY();
        switch (direction) {
            case DOWN -> ++y;
            case UP -> --y;
        }
        if (y < 0) y++;
        if (y >= locations.length) y--;
        return y;
    }
}