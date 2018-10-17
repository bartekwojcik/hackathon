package com.contestantbots.util.Engines;

import com.contestantbots.util.Route;
import com.scottlogic.hackathon.game.GameState;
import com.scottlogic.hackathon.game.Move;
import com.scottlogic.hackathon.game.Player;
import com.scottlogic.hackathon.game.Position;

import java.util.*;
import java.util.stream.Collectors;

public class AttackEngine {

    private PlayerEngine playerEngine;
    private RouteEngine routeEngine;
    private Set<Position> enemySpawnPointPositions;

    public AttackEngine(PlayerEngine playerEngine, RouteEngine routeEngine, Set<Position> enemySpawnPointPositions) {
        this.playerEngine = playerEngine;
        this.routeEngine = routeEngine;
        this.enemySpawnPointPositions = enemySpawnPointPositions;
    }

    public List<Move> doAttack(final GameState gameState, final Map<Player, Position> assignedPlayerDestinations,
                                final List<Position> nextPositions) {
        List<Move> attackMoves = new ArrayList<>();

        Set<Player> players = gameState.getPlayers().stream()
        .filter(player -> playerEngine.isMyPlayer(player))
        .filter(player -> !assignedPlayerDestinations.containsKey(player.getId()))
        .collect(Collectors.toSet());
        System.out.println(players.size() + " players available to attack");

        List<Route> attackRoutes = routeEngine.generateRoutes(gameState, players, enemySpawnPointPositions);

        Collections.sort(attackRoutes);
        attackMoves.addAll(routeEngine.assignRoutes(gameState, assignedPlayerDestinations, nextPositions, attackRoutes));

        System.out.println(attackMoves.size() + " players attacking");
        return attackMoves;
    }
}
