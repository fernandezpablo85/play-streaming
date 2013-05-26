package model

case class StatusUpdate(user: String, status: String, time: Long = System.currentTimeMillis)