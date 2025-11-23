package org.denic_t.web;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/*
 * Output Data Transfer Object (Out DTO) для хранения результата бизнес-операции
 * Метаданные о выполнении
 */
public class CheckResult {
  private final double x;
  
  private final double y;
  
  private final double r;
  
  private final boolean hit;
  
  private final Instant timestamp;
  
  private final long executionTimeNanos;
    
  public CheckResult(double x, double y, double r, boolean hit, long executionTimeNanos) {
    this.x = x;
    this.y = y;
    this.r = r;
    this.hit = hit;
    this.timestamp = Instant.now();
    this.executionTimeNanos = executionTimeNanos;
  }

  /**
   * Конструктор для загрузки из БД с явным timestamp
   */
  public CheckResult(double x, double y, double r, boolean hit, long executionTimeNanos, LocalDateTime timestamp) {
    this.x = x;
    this.y = y;
    this.r = r;
    this.hit = hit;
    this.timestamp = timestamp.atZone(ZoneId.systemDefault()).toInstant();
    this.executionTimeNanos = executionTimeNanos;
  }
  
  // Геттеры для доступа к полям
  public double getX() {
    return x;
  }
  
  public double getY() {
    return y;
  }
  
  public double getR() {
    return r;
  }
  
  public boolean isHit() {
    return hit;
  }
  
  public String getCurrentTime() {
    return timestamp.toString();
  }
  
  public long getExecutionTimeNanos() {
    return executionTimeNanos;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  /**
   * Возвращает timestamp как LocalDateTime для совместимости с JSF
   */
  public LocalDateTime getTimestampAsLocalDateTime() {
    return LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault());
  }
}