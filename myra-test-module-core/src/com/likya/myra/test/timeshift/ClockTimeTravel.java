package com.likya.myra.test.timeshift;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;

/** 
 Set the starting date-time and time-zone, but then 
 let the time vary normally.
 
 Example output:
  2018-12-25T05:00:00Z
  Sleep for 5 seconds...
  2018-12-25T05:00:05.005Z
  Done.

 @since Java 8.
*/

// http://www.javapractices.com/topic/TopicAction.do?Id=234

public final class ClockTimeTravel extends Clock {
  
  /** Simple demo of the behaviour of this class. */
  public static void main(String[] args) throws InterruptedException {
    ClockTimeTravel timeTravel = new ClockTimeTravel(
      LocalDateTime.parse("2018-12-25T00:00:00"), ZoneOffset.of("-05:00")
    );
    log(timeTravel.instant());
    log("Sleep for 5 seconds...");
    Thread.currentThread().sleep(5000);
    log(timeTravel.instant());
    log("Done.");
  }
  
  private static void log(Object msg){
    System.out.println(Objects.toString(msg));
  }
  
  public ClockTimeTravel(LocalDateTime t0, ZoneOffset zoneOffset){
    this.zoneOffset = zoneOffset;
    this.t0LocalDateTime = t0;
    this.t0Instant = t0.toInstant(zoneOffset);
    this.whenObjectCreatedInstant = Instant.now();
  }
  
  @Override public ZoneId getZone() {
    return zoneOffset;
  }
  
  /** The caller needs to actually pass a ZoneOffset object here. */
  @Override public Clock withZone(ZoneId zone) {
    return new ClockTimeTravel(t0LocalDateTime, (ZoneOffset)zone);
  }
  
  @Override public Instant instant() {
    return nextInstant();
  }
  
  //PRIVATE
  
  /** t0 is the moment this clock object was created in Java-land. */
  private final Instant t0Instant;
  private final LocalDateTime t0LocalDateTime;
  
  private final ZoneOffset zoneOffset;
  private final Instant whenObjectCreatedInstant;
  
  /** 
   Figure out how much time has elapsed between the moment this 
   object was created, and the moment when this method is being called.
   Then, apply that diff to t0. 
  */
  private Instant nextInstant() {
    Instant now = Instant.now();
    long diff = now.toEpochMilli() - whenObjectCreatedInstant.toEpochMilli();
    return t0Instant.plusMillis(diff);
  }
}