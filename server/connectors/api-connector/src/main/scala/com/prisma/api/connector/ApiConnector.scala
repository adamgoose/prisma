package com.prisma.api.connector

import com.prisma.shared.models.ApiConnectorCapability.ScalarListsCapability
import com.prisma.shared.models.{ConnectorCapability, Project, ProjectIdEncoder}
import play.api.libs.json.JsValue

import scala.concurrent.Future

trait ApiConnector {
  def databaseMutactionExecutor: DatabaseMutactionExecutor
  def dataResolver(project: Project): DataResolver
  def masterDataResolver(project: Project): DataResolver
  def projectIdEncoder: ProjectIdEncoder
  def capabilities: Set[ConnectorCapability]

  def hasCapability(capability: ConnectorCapability): Boolean = {
    capability match {
      case ScalarListsCapability => capabilities.exists(_.isInstanceOf[ScalarListsCapability])
      case c                     => capabilities.contains(c)
    }
  }

  def initialize(): Future[Unit]
  def shutdown(): Future[Unit]
}

case class MutactionResults(results: Vector[DatabaseMutactionResult])

trait DatabaseMutactionExecutor {
  def executeTransactionally(mutaction: TopLevelDatabaseMutaction): Future[MutactionResults]
  def executeNonTransactionally(mutaction: TopLevelDatabaseMutaction): Future[MutactionResults]
  def executeRaw(query: String): Future[JsValue]
}
