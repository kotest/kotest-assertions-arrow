package io.kotest.assertions.arrow.fx.coroutines

import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.Resource
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred

class ResourceSpec : StringSpec({
  "resolves resource" {
    checkAll(Arb.int()) { n ->
      val r: Int by resource(Resource({ n }, { _, _ -> Unit }))

      (r + 1) shouldBe n + 1
    }
  }

  "value resource is released with Completed" {
    checkAll(Arb.int()) { n: Int ->
      val p = CompletableDeferred<ExitCase>()

      val nn: Int by resource(Resource({ n }, { _, ex -> p.complete(ex) }))

      nn shouldBe n
      afterSpec {
        p.await().shouldBeCompleted()
      }
    }
  }
})