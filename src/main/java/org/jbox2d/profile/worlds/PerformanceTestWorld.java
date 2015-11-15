package org.jbox2d.profile.worlds;

import org.jbox2d.dynamics.World;

public interface PerformanceTestWorld {
  void setupWorld(World world);
  void step();
}
