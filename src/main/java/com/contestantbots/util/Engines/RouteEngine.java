package com.contestantbots.util.Engines;

import com.contestantbots.util.MoveImpl;
import com.contestantbots.util.Route;
import com.scottlogic.hackathon.game.*;

import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

public class RouteEngine {
    private MoveEngine moveEngine;

    public RouteEngine(MoveEngine moveEngine) {
        this.moveEngine = moveEngine;
    }

    public List<Route> generateRoutes(final GameState gameState, Set<Player> players, Set<Position> destinations) {
        List<Route> routes = new ArrayList<>();
        for (Position destination : destinations) {
            for (Player player : players) {
                int distance = gameState.getMap().distance(player.getPosition(), destination);
                Route route = new Route(player, destination, distance);
                routes.add(route);
            }
        }
        return routes;
    }

    public List<Move> assignRoutes(final GameState gameState, final Map<Player, Position> assignedPlayerDestinations, final List<Position> nextPositions, List<Route> routes) {
        return routes.stream()
        .filter(route -> !assignedPlayerDestinations.containsKey(route.getPlayer())&& !assignedPlayerDestinations.containsValue(route.getDestination()))
        .map(route -> {
            Optional<Direction> direction = gameState.getMap().directionsTowards(route.getPlayer().getPosition(), route.getDestination()).findFirst();
            if (direction.isPresent() && moveEngine.canMove(gameState, nextPositions, route.getPlayer(), direction.get())) {
                assignedPlayerDestinations.put(route.getPlayer(), route.getDestination());
                return new MoveImpl(route.getPlayer().getId(), direction.get());
            }
            return null;
        })
        .filter(move -> move != null)
        .collect(Collectors.toList());
    }
}
