package de.bybackfish.telosaddons.utils

import java.util.*

val a = arrayListOf<Quadret<Int, String, Long, Int>>()
val skills = Collections.synchronizedList(a)


fun recalculateSlots(currentSlot: Int): MutableList<Quadret<Int, String, Long, Int>> {
    val newSkills = Collections.synchronizedList(arrayListOf<Quadret<Int, String, Long, Int>>())
    var slot = currentSlot + 1
    for (skill in skills) {
        if (skill.a >= slot) {
            skill.a++
        }

        newSkills.add(skill)
    }
    return newSkills
}

fun main() {
    skills.add(Quadret(1, "First Skill", 0L, 0))
    skills.add(Quadret(2, "Second Skill", 0L, 0))
    skills.add(Quadret(3, "Third Skill", 0L, 0))
    skills.add(Quadret(4, "Fourth Skill", 0L, 0))
    skills.add(Quadret(5, "Fifth Skill", 0L, 0))


    println(recalculateSlots(1).map { it.a to it.b })

}