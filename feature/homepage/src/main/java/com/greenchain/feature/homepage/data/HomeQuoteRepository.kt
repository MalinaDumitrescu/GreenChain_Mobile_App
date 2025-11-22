package com.greenchain.feature.homepage.data

import com.greenchain.feature.homepage.model.Quote
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeQuoteRepository @Inject constructor() {

    private val quotes = listOf(
        Quote("The greatest threat to our planet is the belief that someone else will save it."),
        Quote("We do not inherit the Earth from our ancestors; we borrow it from our children."),
        Quote("Small acts, when multiplied by millions of people, can transform the world."),
        Quote("There is no such thing as ‘away’. When we throw something away, it must go somewhere."),
        Quote("The Earth is a fine place and worth fighting for."),
        Quote("Every bottle recycled is one less in nature."),
        Quote("Live simply so others may simply live."),
        Quote("Green choices today create a better tomorrow."),
        Quote("Nature does not hurry, yet everything is accomplished."),
        Quote("The future will be green, or not at all."),
        Quote("Take care of the Earth, and she will take care of you."),
        Quote("Reduce what you can, reuse what you have, recycle what you can no longer use."),
        Quote("Sustainability is not a goal; it is a way of living."),
        Quote("When the last tree is cut, the last fish caught, the last river poisoned, you will realize you cannot eat money."),
        Quote("Be the change you want to see in the world."),
        Quote("The Earth is what we all have in common."),
        Quote("Your future self will thank you for every green choice you make today."),
        Quote("What we save today is what our children will live with tomorrow."),
        Quote("Less pollution is the best solution."),
        Quote("Choose planet over convenience."),
        Quote("Sustainability is about caring for what cares for us."),
        Quote("Recycling turns things into other things. Which is like magic."),
        Quote("If you can’t reuse it, refuse it."),
        Quote("The world changes by your example, not your opinion."),
        Quote("There is beauty in living lightly on this Earth.")
    )

    fun getRandomQuote(): Quote = quotes.random()
}
