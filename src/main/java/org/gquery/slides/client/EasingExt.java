/*
 * Copyright 2012 Matthew Tai.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.gquery.slides.client;

import com.google.gwt.query.client.plugins.effects.PropertiesAnimation.Easing;

public class EasingExt {
  public static Easing LINEAR = Easing.LINEAR;

  public static Easing SWING = Easing.SWING;

  public static Easing EASE_IN_QUAD = new Easing() {
    public double interpolate(double progress) {
      return progress * progress;
    }
  };

  public static Easing EASE_OUT_QUAD = new Easing() {
    public double interpolate(double progress) {
      return 2 * progress - progress * progress;
    }
  };

  public static Easing EASE_IN_OUT_QUAD = new Easing() {
    public double interpolate(double progress) {
      if (progress < 0.5) {
        return progress * progress / 0.5;
      }

      return (2 * progress - progress * progress) / 0.5 - 1;
    };
  };

  public static Easing EASE_IN_CUBIC = new Easing() {
    public double interpolate(double progress) {
      return progress * progress * progress;
    };
  };

  public static Easing EASE_OUT_CUBIC = new Easing() {
    public double interpolate(double progress) {
      return (progress - 1) * (progress - 1) * (progress - 1) + 1;
    };
  };

  public static Easing EASE_IN_OUT_CUBIC = new Easing() {
    public double interpolate(double progress) {
      if (progress < 0.5) {
        return progress * progress * progress / 0.25;
      }

      return ((progress - 1) * (progress - 1) * (progress - 1) + 1) / 0.25 - 3;
    };
  };

  public static Easing EASE_IN_QUART = new Easing() {
    public double interpolate(double progress) {
      return progress * progress * progress * progress;
    };
  };

  public static Easing EASE_OUT_QUART = new Easing() {
    public double interpolate(double progress) {
      return -((progress - 1) * (progress - 1) * (progress - 1) * (progress - 1) - 1);
    };
  };

  public static Easing EASE_IN_OUT_QUART = new Easing() {
    public double interpolate(double progress) {
      if (progress < 0.5) {
        return (progress * progress * progress * progress) / 0.125;
      }

      return (-((progress - 1) * (progress - 1) * (progress - 1) * (progress - 1) - 1)) / 0.125 - 7;
    };
  };

  public static Easing EASE_IN_QUINT = new Easing() {
    public double interpolate(double progress) {
      return progress * progress * progress * progress * progress;
    };
  };

  public static Easing EASE_OUT_QUINT = new Easing() {
    public double interpolate(double progress) {
      return ((progress - 1) * (progress - 1) * (progress - 1) * (progress - 1) * (progress - 1) + 1);
    };
  };

  public static Easing EASE_IN_OUT_QUINT = new Easing() {
    public double interpolate(double progress) {
      if (progress < 0.5) {
        return (progress * progress * progress * progress * progress) / 0.0625;
      }

      return ((progress - 1) * (progress - 1) * (progress - 1) * (progress - 1) * (progress - 1) + 1) / 0.0625 - 15;
    };
  };

  public static Easing EASE_IN_SINE = new Easing() {
    public double interpolate(double progress) {
      return -1 * Math.cos(progress * (Math.PI / 2)) + 1;
    };
  };

  public static Easing EASE_OUT_SINE = new Easing() {
    public double interpolate(double progress) {
      return Math.sin(progress * (Math.PI / 2));
    }
  };

  public static Easing EASE_IN_OUT_SINE = new Easing() {
    public double interpolate(double progress) {
      return -0.5 * (Math.cos(Math.PI * progress / 1) - 1) + 0;
    }
  };

  public static Easing EASE_IN_EXPO = new Easing() {
    public double interpolate(double progress) {
      return Math.pow(2, 10 * (progress - 1));
    }
  };

  public static Easing EASE_OUT_EXPO = new Easing() {
    public double interpolate(double progress) {
      return -Math.pow(2, -10 * progress) + 1;
    }
  };

  public static Easing EASE_IN_OUT_EXPO = new Easing() {
    public double interpolate(double progress) {
      progress *= 2;

      if (progress < 1) {
        return 0.5 * Math.pow(2, 10 * (progress - 1));
      }

      return 0.5 * (-Math.pow(2, -10 * (progress - 1)) + 2);
    }
  };

  public static Easing EASE_IN_CIRC = new Easing() {
    public double interpolate(double progress) {
      return -(Math.sqrt(1 - progress * progress) - 1);
    }
  };

  public static Easing EASE_OUT_CIRC = new Easing() {
    public double interpolate(double progress) {
      return Math.sqrt(1 - (progress - 1) * (progress - 1));
    }
  };

  public static Easing EASE_IN_OUT_CIRC = new Easing() {
    public double interpolate(double progress) {
      if (progress < 0.5) {
        return EASE_OUT_CIRC.interpolate(2 * progress) / 2;
      }

      return EASE_IN_CIRC.interpolate(2 * progress - 1) / 2 + 0.5;
    }
  };

  public static Easing EASE_IN_ELASTIC = new Easing() {
    public double interpolate(double progress) {
      double s = 0.3 / (2 * Math.PI) * Math.asin(1);

      return -(Math.pow(2, 10 * (progress - 1)) * Math
          .sin((progress - 1 - s) * (2 * Math.PI) / 0.3));
    }
  };

  public static Easing EASE_OUT_ELASTIC = new Easing() {
    public double interpolate(double progress) {
      double s = 0.3 / (2 * Math.PI) * Math.asin(1);

      return Math.pow(2, -10 * progress) * Math.sin((progress - s) * (2 * Math.PI) / 0.3) + 1;

    }
  };

  public static Easing EASE_IN_OUT_ELASTIC = new Easing() {
    public double interpolate(double progress) {
      double amplitude = 1;
      double period = 0.4;

      if (progress == 0)
        return 0;

      progress *= 2;

      if (progress == 2)
        return 1;

      double s;

      if (amplitude < 1) {
        amplitude = 1;
        s = period / 4;
      } else {
        s = period / (2 * Math.PI) * Math.asin(1 / amplitude);
      }

      if (progress < 1) {
        return -0.5
            * (amplitude * Math.pow(2, 10 * (progress - 1)) * Math.sin((progress - 1 - s)
                * (2 * Math.PI) / period));
      } else {
        return amplitude * Math.pow(2, -10 * (progress - 1))
            * Math.sin((progress - 1 - s) * (2 * Math.PI) / period) * 0.5 + 1;
      }
    }
  };

  public static Easing EASE_IN_BACK = new Easing() {
    public double interpolate(double progress) {

      return progress * progress * (2.70158 * progress - 1.70158);
    }
  };

  public static Easing EASE_OUT_BACK = new Easing() {
    public double interpolate(double progress) {
      return (progress - 1) * (progress - 1) * (2.70158 * (progress - 1) + 1.70158) + 1;
    }
  };

  public static Easing EASE_IN_OUT_BACK = new Easing() {
    public double interpolate(double progress) {
      double s = 1.70158f;
      progress *= 2;

      if (progress < 1) {
        s *= 1.525f;
        return 0.5 * (progress * progress * ((s + 1) * progress - s));
      } else {
        progress -= 2;
        s *= 1.525;
        return 0.5 * (progress * progress * ((s + 1) * progress + s) + 2);
      }
    }
  };

  public static Easing EASE_IN_BOUNCE = new Easing() {
    public double interpolate(double progress) {
      return 1 - EASE_OUT_BOUNCE.interpolate(1 - progress);
    };
  };

  public static Easing EASE_OUT_BOUNCE = new Easing() {
    public double interpolate(double progress) {
      if (progress < (1 / 2.75)) {
        return 7.5625 * progress * progress;
      } else if (progress < (2 / 2.75)) {
        return (7.5625 * (progress -= (1.5 / 2.75)) * progress + .75);
      } else if (progress < (2.5 / 2.75)) {
        return (7.5625 * (progress -= (2.25 / 2.75)) * progress + .9375);
      } else {
        return (7.5625 * (progress -= (2.625 / 2.75)) * progress + .984375);
      }
    }
  };

  public static Easing EASE_IN_OUT_BOUNCE = new Easing() {
    public double interpolate(double progress) {
      if (progress < 0.5) {
        return EASE_IN_BOUNCE.interpolate(progress * 2) * .5;
      }

      return EASE_OUT_BOUNCE.interpolate(progress * 2 - 1) * .5 + 1 * .5;
    }
  };
}