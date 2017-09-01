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

    given("Player saYcrest and DirolGaming") {
        on("Bulk UUID fetch") {
            val resultingUUIDs = mojangWrapper.resolveUUIDs("saYcrest", "DirolGaming")

            it("should both resolve") {
                assertNotNull(resultingUUIDs[0])
                assertNotNull(resultingUUIDs[1])
            }
        }

        on("failing") {
            it("shouldn't fail") {
                assertNotNull(null)
            }
        }
    }
})