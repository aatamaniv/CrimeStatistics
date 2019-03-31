package com.atamaniv

import akka.actor.{Actor, ActorLogging, Props}

object IotSupervisor {
  def props(): Props = Props(new CrimeSupervisor)
}

class CrimeSupervisor extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("Crime supervisor started")
  override def postStop(): Unit = log.info("Crime supervisor stopped")

  override def receive = Actor.emptyBehavior

}