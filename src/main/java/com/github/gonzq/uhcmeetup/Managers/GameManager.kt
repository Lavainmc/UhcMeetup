package com.github.gonzq.uhcmeetup.Managers

import com.github.gonzq.uhcmeetup.Enums.GameState
import com.github.gonzq.uhcmeetup.Players.GamePlayer
import com.github.gonzq.uhcmeetup.Tasks.CountdownTask
import com.github.gonzq.uhcmeetup.UhcMeetup
import com.github.gonzq.uhcmeetup.Utils.Utils

/**
 * State of one match. Owned by the plugin instead of a static singleton, so a
 * second match can exist later without sharing counters or state.
 */
open class GameManager(private val pl: UhcMeetup) {

    private var voteScenarios: VoteManager? = null

    fun getVoteScenarios(): VoteManager {
        if (voteScenarios == null) voteScenarios = VoteManager(pl, pl.scenarios)
        return voteScenarios!!
    }

    private var countdown: Int = pl.config.getConfig().getInt("countdown-time")
    fun getCountdown(): Int {
        return countdown
    }
    fun restCountdown() {
        countdown--
    }

    private var state: GameState = GameState.WAITING
    fun setState(state: GameState) {
        this.state = state
    }
    fun getState(): GameState {
        return state
    }
    private var elapsed: Int = 0
    fun setTimeElapsed(i: Int) {
        elapsed = i
    }
    fun getTimeElapsed(): Int {
        return elapsed
    }

    fun start() {
        pl.pm.getPlayerList().stream().filter(GamePlayer::isOnline).forEach{p ->
            Utils.delay(5) {
                pl.scatter.scatter(p)
                p.getPlayer()!!.isInvulnerable = true
            }
        }

        setState(GameState.STARTING)
        CountdownTask().runTaskTimer(pl,0,20)
        if (pl.config.getConfig().getBoolean("vote-system.scenario-system")) {
            getVoteScenarios().setCanVote(true)
        }
    }
}
