package org.b0102.kafka.producer;

import org.springframework.stereotype.Component;

@Component
public class KafkaSelector
{
  public static enum Target
  {
    PRIMARY,
    SECONDARY
  }
  private Target target = Target.PRIMARY;

  public synchronized void switchOver()
  {
    target = target == Target.PRIMARY ? Target.SECONDARY : Target.PRIMARY;
  }

  public synchronized Target getTarget() {
    return target;
  }

}