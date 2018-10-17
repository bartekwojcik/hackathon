package com.contestantbots.util.Engines;

import com.contestantbots.util.MoveImpl;
import com.scottlogic.hackathon.game.*;

import java.util.List;
import java.util.Set;

public class MoveEngine {


    public boolean canMove(final GameState gameState,
                            final List<Position> nextPositions,
                            final Player player,
                            final Direction direction) {
        Set<Position> outOfBounds = gameState.getOutOfBoundsPositions();
        Position newPosition = gameState.getMap().getNeighbour(player.getPosition(), direction);
        if (!nextPositions.contains(newPosition)
        && !outOfBounds.contains(newPosition)) {
            nextPositions.add(newPosition);
            return true;
        } else {
            return false;
        }

    }

    public Move doMove(final GameState gameState, final List<Position> nextPositions, final Player player) {
        Direction direction;
        do {
            direction = Direction.random();
        } while (!canMove(gameState, nextPositions, player, direction));
        return new MoveImpl(player.getId(), direction);
    }

}
