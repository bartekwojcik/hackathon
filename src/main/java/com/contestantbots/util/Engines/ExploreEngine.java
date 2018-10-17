package com.contestantbots.util.Engines;

import com.contestantbots.util.Route;
import com.scottlogic.hackathon.game.GameState;
import com.scottlogic.hackathon.game.Move;
import com.scottlogic.hackathon.game.Player;
import com.scottlogic.hackathon.game.Position;

import java.util.*;
import java.util.stream.Collectors;

public class ExploreEngine {
    private MoveEngine moveEngine;
    private PlayerEngine playerEngine;
    private RouteEngine routeEngine;
    private Set<Position> unseenPositions;

    public ExploreEngine(MoveEngine moveEngine, PlayerEngine playerEngine, RouteEngine routeEngine, Set<Position> unseenPositions ) {
        this.moveEngine = moveEngine;
        this.playerEngine = playerEngine;
        this.routeEngine = routeEngine;
        this.unseenPositions = unseenPositions;
    }

    public List<Move> doExplore(final GameState gameState, final List<Position> nextPositions) {

        List<Move> exploreMoves = new ArrayList<>();

        exploreMoves.addAll(gameState.getPlayers().stream()
        .map(player -> moveEngine.doMove(gameState, nextPositions, player))
        .collect(Collectors.toList()));

        return exploreMoves;
    }

    public List<Move> doExploreUnseen(final GameState gameState, final Map<Player, Position> assignedPlayerDestinations, final List<Position> nextPositions) {
        List<Move> exploreMoves = new ArrayList<>();

        Set<Player> players = gameState.getPlayers().stream()
        .filter(player -> playerEngine.isMyPlayer(player))
        .filter(player -> !assignedPlayerDestinations.containsKey(player))
        .collect(Collectors.toSet());

        List<Route> unseenRoutes = routeEngine.generateRoutes(gameState, players, unseenPositions);

        Collections.sort(unseenRoutes);
        exploreMoves.addAll(routeEngine.assignRoutes(gameState, assignedPlayerDestinations, nextPositions, unseenRoutes));

        System.out.println(exploreMoves.size() + " players exploring unseen");
        return exploreMoves;
    }

}
