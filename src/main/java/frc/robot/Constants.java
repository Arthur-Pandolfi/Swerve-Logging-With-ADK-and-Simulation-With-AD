package frc.robot;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.RobotBase;

public class Constants{
  public static final class Logging {
    public static final RobotModes SIM_MODE = RobotModes.SIM;
    public static final RobotModes CURRENT_ROBOT_MODE = RobotBase.isReal() ? RobotModes.REAL : RobotModes.SIM;

    public static final String LOGS_PATH = "/home/lvuser/logs";

    public static enum RobotModes {
      SIM,
      REAL,
      REPLAY
    }
  }

  public static final class SwerveConsts{
    public static final double AUTO_TRANSLATION_kP = 0.01;
    public static final double AUTO_TRANSLATION_kI = 0;
    public static final double AUTO_TRANSLATION_kD = 0.001;

    public static final double AUTO_ROTATION_kP = 0.08;
    public static final double AUTO_ROTATION_kI = 0;
    public static final double AUTO_ROTATION_kD = 0.0;

    public static final Translation2d[] MODULES_TRANSLATION = 
      new Translation2d[] {
        new Translation2d(0.356, 0.356),
        new Translation2d(0.356, -0.356),
        new Translation2d(-0.356, 0.356),
        new Translation2d(-0.356, -0.356)
      };

    public static final double MAX_SPEED = 7.0;
    public static final boolean FIELD_ORIENTED = true;
  }

  public static final class Joysticks {
    public static final int DRIVER_PORT = 0;
    public static final int MECHANISM_PORT = 1;

    public static final double DEADBAND = 0.1;
  }

  public static final class Components{
    public static final int PIGEON2 = 9;
    public static final String LIMELIGHT = "limelight";
  }
}