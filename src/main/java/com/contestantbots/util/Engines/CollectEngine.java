package com.contestantbots.util.Engines;

import com.contestantbots.util.MoveImpl;
import com.contestantbots.util.Route;
import com.scottlogic.hackathon.game.*;

import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectEngine {
    private PlayerEngine playerEngine;
    private RouteEngine routeEngine;
    private MoveEngine moveEngine;

    public CollectEngine(PlayerEngine playerEngine, RouteEngine routeEngine, MoveEngine moveEngine) {
        this.playerEngine = playerEngine;
        this.routeEngine = routeEngine;
        this.moveEngine = moveEngine;
    }

    public List<Move> doCollect(final GameState gameState,
                                 final Map<Player, Position> assignedPlayerDestinations,
                                 final List<Position> nextPositions) {

        List<Move> collectMoves = new ArrayList<>();

        Set<Position> collectablePositions = gameState.getCollectables().stream()
        .map(collectable -> collectable.getPosition())
        .collect(Collectors.toSet());
        Set<Player> players = gameState.getPlayers().stream()
        .filter(player -> playerEngine.isMyPlayer(player))
        .collect(Collectors.toSet());

        List<Route> collectableRoutes = routeEngine.generateRoutes(gameState, players, collectablePositions);
        collectMoves.addAll(routeEngine.assignRoutes(gameState, assignedPlayerDestinations, nextPositions, collectableRoutes));

        for (Position collectablePosition : collectablePositions) {
            for (Player player : players) {
                int distance = gameState.getMap().distance(player.getPosition(), collectablePosition);
                Route route = new Route(player, collectablePosition, distance);
                collectableRoutes.add(route);
            }
        }

        Collections.sort(collectableRoutes);

        for (Route route : collectableRoutes) {
            if (!assignedPlayerDestinations.containsKey(route.getPlayer())
            && !assignedPlayerDestinations.containsValue(route.getDestination())) {
                Optional<Direction> direction = gameState.getMap().directionsTowards(route.getPlayer().getPosition(), route.getDestination()).findFirst();
                if (direction.isPresent() && moveEngine.canMove(gameState, nextPositions, route.getPlayer(), direction.get())) {
                    collectMoves.add(new MoveImpl(route.getPlayer().getId(), direction.get()));
                    assignedPlayerDestinations.put(route.getPlayer(), route.getDestination());
                }
            }
        }

        System.out.println(collectMoves.size() + " players collecting");
        return collectMoves;
    }
}
