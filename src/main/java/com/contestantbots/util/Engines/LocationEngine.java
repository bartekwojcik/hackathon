package com.contestantbots.util.Engines;

import com.scottlogic.hackathon.game.Direction;
import com.scottlogic.hackathon.game.GameState;
import com.scottlogic.hackathon.game.Position;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LocationEngine {

    public Stream<Position> getSurroundingPositions(final GameState gameState, final Position position, final int distance) {
        Stream<Position> positions = Arrays.stream(Direction.values())
        .flatMap(direction -> IntStream.rangeClosed(1, distance)
        .mapToObj(currentDistance -> gameState.getMap().getRelativePosition(position, direction, currentDistance)));

        positions = Stream.concat(Stream.of(position), positions);

        return positions;
    }
}
