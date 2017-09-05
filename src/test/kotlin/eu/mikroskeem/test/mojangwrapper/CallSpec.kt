/*
 * Copyright 2017 Mark Vainomaa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package eu.mikroskeem.test.mojangwrapper

import eu.mikroskeem.mojangwrapper.MojangWrapper
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * @author Mark Vainomaa
 */
@RunWith(JUnitPlatform::class)
class CallSpec: Spek({
    val mojangWrapper = MojangWrapper()

    given("Player mikroskeem") {
        on("UUID fetch") {
            val resultingUUID = mojangWrapper.resolveUUID("mikroskeem")

            it("resulting UUID should equal to static UUID") {
                assertEquals(UUID.fromString("4d03444c-2e0b-4b8e-a445-a2965c907676"), resultingUUID)
            }
        }
    }

    given("Mojang block head skins") {
        on("Bulk UUID fetch") {
            val namesArray = arrayOf(
                    // MHF heads from https://minecraft.gamepedia.com/Mob_head#Mojang_skins
                    "MHF_Alex",
                    "MHF_Blaze",
                    "MHF_CaveSpider",
                    "MHF_Chicken",
                    "MHF_Cow",
                    "MHF_Creeper",
                    "MHF_Enderman",
                    "MHF_Ghast",
                    "MHF_Golem",
                    "MHF_Herobrine",
                    "MHF_LavaSlime",
                    "MHF_MushroomCow",
                    "MHF_Ocelot",
                    "MHF_Pig",
                    "MHF_PigZombie",
                    "MHF_Sheep",
                    "MHF_Skeleton",
                    "MHF_Slime",
                    "MHF_Spider",
                    "MHF_Squid",
                    "MHF_Steve",
                    "MHF_Villager",
                    "MHF_WSkeleton",
                    "MHF_Zombie",
                    "MHF_Cactus",
                    "MHF_Cake",
                    "MHF_Chest",
                    "MHF_CoconutB",
                    "MHF_CoconutG",
                    "MHF_Melon",
                    "MHF_OakLog",
                    "MHF_Present1",
                    "MHF_Present2",
                    "MHF_Pumpkin",
                    "MHF_TNT",
                    "MHF_TNT2",
                    "MHF_ArrowUp",
                    "MHF_ArrowDown",
                    "MHF_ArrowLeft",
                    "MHF_ArrowRight",
                    "MHF_Exclamation",
                    "MHF_Question",
                    // https://www.planetminecraft.com/blog/minecraft-playerheads-2579899/
                    "Ernie77",
                    "popcorn_lvr",
                    "ZachWarnerHD",
                    "Chipsandip",
                    "FlabbenBaggen",
                    "QuadratCookie",
                    "Chazwell777",
                    "DutchGuard",
                    "AmericanOreo",
                    "KylexDavis",
                    "PatrickAVG",
                    "Pandasaurus_R",
                    "Thanauser",
                    "Spinken5840",
                    "Metroidling",
                    "GameNilo",
                    "Laserpanda",
                    "nonesuchplace",
                    "Addelburgh",
                    "Alistor",
                    "CoderPuppy",
                    "Hack",
                    "ImportPython",
                    "Is200PingGood",
                    "EladYat",
                    "Ferocious_Ben",
                    "Axle39",
                    "uioz",
                    "trainrider",
                    "BurningFurnace",
                    "Redstone",
                    "3i5g00d",
                    "zEL3M3nTz",
                    "Olaf_C",
                    "Vectrix",
                    "johnsquawk",
                    "gocodygo",
                    "Hannah4848",
                    "CruXXx",
                    "thresh3",
                    "SeerPotion",
                    "Erixia",
                    "CS001",
                    "raxo55",
                    "Asiankid2004",
                    "ThePearlyBum",
                    "Pencil",
                    "bman1661",
                    "Freshmuffin",
                    "Seska_Rotan",
                    "Sting",
                    "AzBandit2000",
                    "Richard1230"
            )

            val resultingUUIDs = mojangWrapper.resolveUUIDs(*namesArray)

            it("should resolve") {
                resultingUUIDs.forEachIndexed { index, it ->
                    assertNotNull(it, "${namesArray[index]} --> $it == null!")
                }
            }
        }
    }
})
