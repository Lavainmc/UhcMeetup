package com.github.gonzq.uhcmeetup.Managers

import com.github.gonzq.uhcmeetup.Scenarios.Scenario
import com.github.gonzq.uhcmeetup.Scenarios.Types.*
import com.github.gonzq.uhcmeetup.UhcMeetup

class ScenarioManager(private val pl: UhcMeetup) {
    private val scenarios = mutableSetOf<Scenario>()

    fun setup() {
        scenarios.add(GoldenHeadDorp())
        scenarios.add(ValueDorp())
        scenarios.add(OldSmithing())
        scenarios.add(NoClean(pl, pl.config.getConfig().getInt("noclean-time")))
        pl.game.getVoteScenarios()
    }

    fun getScenario(name: String): Scenario? {
        for (scen in scenarios) {
            if (scen.getName() == name) return scen
        }
        return null
    }

    fun getScenarios(): MutableList<Scenario> {
        return scenarios.toMutableList()
    }

    fun getEnabledScenarios(): MutableList<Scenario> {
        return scenarios.stream().filter(Scenario::isEnabled).toList().toMutableList()
    }
    fun getDisabledScenarios(): MutableList<Scenario> {
        return scenarios.stream().filter {scen -> !scen.isEnabled()}.toList().toMutableList()
    }
}