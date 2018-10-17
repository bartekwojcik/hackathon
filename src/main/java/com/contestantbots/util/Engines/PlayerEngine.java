package com.contestantbots.util.Engines;

import com.scottlogic.hackathon.game.Bot;
import com.scottlogic.hackathon.game.Player;

public class PlayerEngine {
    private Bot bot;

    public PlayerEngine(Bot bot){

        this.bot = bot;
    }

    public boolean isMyPlayer(final Player player) {
        return player.getOwner().equals(bot.getId());
    }
}
