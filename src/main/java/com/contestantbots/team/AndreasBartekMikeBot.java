package com.contestantbots.team;

import com.contestantbots.util.Engines.*;
import com.contestantbots.util.MoveImpl;
import com.contestantbots.util.GameStateLogger;
import com.contestantbots.util.Route;
import com.scottlogic.hackathon.client.Client;
import com.scottlogic.hackathon.game.Bot;
import com.scottlogic.hackathon.game.Direction;
import com.scottlogic.hackathon.game.GameState;
import com.scottlogic.hackathon.game.Move;
import com.scottlogic.hackathon.game.Player;
import com.scottlogic.hackathon.game.Position;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AndreasBartekMikeBot extends Bot {
    private final GameStateLogger gameStateLogger;
    private Set<Position> unseenPositions = new HashSet<>();
    private Set<Position> enemySpawnPointPositions = new HashSet<>();
    private MoveEngine moveEngine;
    private PlayerEngine playerEngine;
    private RouteEngine routeEngine;
    private ExploreEngine exploreEngine;
    private CollectEngine collectEngine;
    private AttackEngine attackEngine;
    private LocationEngine locationEngine;

    public AndreasBartekMikeBot() {
        super("we are here for pizza");
        gameStateLogger = new GameStateLogger(getId());
    }

    @Override
    public void initialise(GameState initialGameState) {
        // add all positions to the unseen set
        for (int x = 0; x < initialGameState.getMap().getWidth(); x++) {
            for (int y = 0; y < initialGameState.getMap().getHeight(); y++) {
                unseenPositions.add(new Position(x, y));
            }
        }


        MoveEngine moveEngine = new MoveEngine();
        PlayerEngine playerEngine = new PlayerEngine(this);
        RouteEngine routeEngine = new RouteEngine(moveEngine);
        ExploreEngine exploreEngine = new ExploreEngine(moveEngine,playerEngine,routeEngine,unseenPositions);
        CollectEngine collectEngine = new CollectEngine(playerEngine,routeEngine,moveEngine);
        AttackEngine attackEngine = new AttackEngine(playerEngine,routeEngine, enemySpawnPointPositions);
        LocationEngine locationEngine = new LocationEngine();
        this.moveEngine = moveEngine;
        this.playerEngine = playerEngine;
        this.routeEngine = routeEngine;
        this.exploreEngine = exploreEngine;
        this.collectEngine = collectEngine;
        this.attackEngine = attackEngine;
        this.locationEngine = locationEngine;
    }

    @Override
    public List<Move> makeMoves(final GameState gameState){
        gameStateLogger.process(gameState);
        List<Move> moves = new ArrayList<>();
        List<Position> nextPositions = new ArrayList<>();
        Map<Player, Position> assignedPlayerDestinations = new HashMap<>();

        updateEnemySpawnPointLocations(gameState);
        updateUnseenLocations(gameState);

        moves.addAll(collectEngine.doCollect(gameState, assignedPlayerDestinations, nextPositions));
        moves.addAll(exploreEngine.doExplore(gameState,nextPositions));
        moves.addAll(exploreEngine.doExploreUnseen(gameState, assignedPlayerDestinations, nextPositions));
        moves.addAll(attackEngine.doAttack(gameState, assignedPlayerDestinations, nextPositions));

        return moves;
    }


    private void updateUnseenLocations(final GameState gameState) {
        // assume players can 'see' a distance of 5 squares
        int visibleDistance = 5;
        final Set<Position> visiblePositions = gameState.getPlayers()
        .stream()
        .filter(player -> playerEngine.isMyPlayer(player))
        .map(player -> player.getPosition())
        .flatMap(playerPosition -> locationEngine.getSurroundingPositions(gameState, playerPosition, visibleDistance))
        .distinct()
        .collect(Collectors.toSet());

        // remove any positions that can be seen
        unseenPositions.removeIf(position -> visiblePositions.contains(position));
    }


    private void updateEnemySpawnPointLocations(final GameState gameState) {
        enemySpawnPointPositions.addAll(gameState.getSpawnPoints().stream()
        .filter(spawnPoint -> !spawnPoint.getOwner().equals(getId()))
        .map(spawnPoint -> spawnPoint.getPosition())
        .collect(Collectors.toList()));

        enemySpawnPointPositions.removeAll(gameState.getRemovedSpawnPoints().stream()
        .filter(spawnPoint -> !spawnPoint.getOwner().equals(getId()))
        .map(spawnPoint -> spawnPoint.getPosition())
        .collect(Collectors.toList()));
    }



    /*
     * Run this main as a java application to test and debug your code within your IDE.
     * After each turn, the current state of the game will be printed as an ASCII-art representation in the console.
     * You can study the map before hitting 'Enter' to play the next phase.
     */
    public static void main(String ignored[]) throws Exception {

        final String[] args = new String[]{
                /*
                Pick the map to play on
                -----------------------
                Each successive map is larger, and has more out-of-bounds positions that must be avoided.
                Make sure you only have ONE line uncommented below.
                 */
                "--map",
//                    "VeryEasy",
                    "Easy",
//                    "Medium",
//                    "LargeMedium",
//                    "Hard",

                /*
                Pick your opponent bots to test against
                ---------------------------------------
                Every game needs at least one opponent, and you can pick up to 3 at a time.
                Uncomment the bots you want to face, or specify the same opponent multiple times to face multiple
                instances of the same bot.
                 */
                "--bot",
//                    "Default", // Players move in random directions
                    "Milestone1", // Players just try to stay out of trouble
//                    "Milestone2", // Some players gather collectables, some attack enemy players, and some attack enemy spawn points
//                    "Milestone3", // Strategy dynamically updates based on the current state of the game
//                    "FastExpansion", // Advanced dynamic strategy where players work together

                /*
                Enable debug mode
                -----------------
                This causes all Bots' 'makeMoves()' methods to be invoked from the main thread,
                and prevents them from being disqualified if they take longer than the usual time limit.
                This allows you to run in your IDE debugger and pause on break points without timing out.

                Comment this line out if you want to check that your bot is running fast enough.
                 */
                "--debug",

                // Use this class as the 'main' Bot
                "--className", AndreasBartekMikeBot.class.getName()
        };

        Client.main(args);
    }

}
