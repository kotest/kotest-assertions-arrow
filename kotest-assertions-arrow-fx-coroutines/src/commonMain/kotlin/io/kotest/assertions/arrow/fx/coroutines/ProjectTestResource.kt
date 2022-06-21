package io.kotest.assertions.arrow.fx.coroutines

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.ResourceScope
import arrow.fx.coroutines.continuations.resource
import io.kotest.core.listeners.ProjectListener

/**
 * Useful to register [Resource] on a Project wide configuration.
 * For example:
 *
 * ```kotlin
 * object ProjectConfig : AbstractProjectConfig() {
 *   val hikari: TestResource<HikariDataSource> =
 *     ProjectTestResource(Resource.fromCloseable { HikariDataSource(...) })
 *
 *   override fun extensions(): List<Extension> = listOf(hikari)
 * }
 *
 * class MySpec : StringSpec({
 *   "my test" {
 *     val dataSource: HikariDataSource = ProjectConfig.hikari()
 *   }
 * })
 * ```
 */
public class ProjectTestResource<A> internal constructor(
  public override val resource: Resource<A>,
  private val default: DefaultTestResource<A> = DefaultTestResource(resource)
) : ProjectListener, TestResource<A> by default {

  public constructor(block: suspend ResourceScope.() -> Resource<A>) : this(resource { block().bind() })

  override suspend fun beforeProject() {
    default.init()
  }

  override suspend fun afterProject() {
    default.release()
  }
}
