package com.greenchain.feature.homepage.data

import com.greenchain.feature.homepage.model.Quest
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class QuestRepository @Inject constructor() {

    private val quests = listOf(
        Quest(1, "Recycle a Bottle", "Recycle at least one plastic bottle today."),
        Quest(2, "Water Your Plants", "Take a moment to water your indoor or outdoor plants."),
        Quest(3, "Pick Up Litter", "Pick up 3 pieces of litter you see on your walk."),
        Quest(4, "Use a Reusable Bag", "Use a reusable bag for your shopping today."),
        Quest(5, "No Single-Use Plastic", "Avoid using single-use plastic cutlery or straws today."),
        Quest(6, "Walk or Bike", "Choose to walk or bike instead of driving for a short trip."),
        Quest(7, "Conserve Energy", "Turn off lights in rooms you are not using."),
        Quest(8, "Shorten Your Shower", "Try to take a shower that is 2 minutes shorter than usual."),
        Quest(9, "Eat a Plant-Based Meal", "Have at least one meal today that is entirely plant-based."),
        Quest(10, "Unplug Electronics", "Unplug chargers and electronics when not in use."),
        Quest(11, "Plant a Seed", "Plant a seed or a small plant today."),
        Quest(12, "Compost Scraps", "Compost your fruit and vegetable scraps instead of throwing them away."),
        Quest(13, "Use a Reusable Bottle", "Drink water from a reusable bottle all day."),
        Quest(14, "Share a Green Tip", "Share an eco-friendly tip with a friend or family member."),
        Quest(15, "Declutter & Donate", "Find one item you no longer need and set it aside for donation.")
    )

    fun getDailyQuest(): Quest {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)

        // Folosim ziua și anul ca seed pentru a avea același quest toată ziua
        val seed = year * 1000 + dayOfYear
        val random = Random(seed)

        return quests[random.nextInt(quests.size)]
    }
}
