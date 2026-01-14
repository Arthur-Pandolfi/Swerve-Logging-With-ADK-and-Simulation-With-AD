package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.RobotBase;
import frc.frc_java9485.utils.AllianceFlip;

public class Constants {
  public static final class RobotConsts {
    public static final RobotModes SIM_MODE = RobotModes.SIM;
    public static final RobotModes CURRENT_ROBOT_MODE =
        RobotBase.isReal() ? RobotModes.REAL : RobotModes.SIM;

    public static final String LOGS_PATH = "/home/lvuser/logs";
    public static final String SPARK_ODOMETRY_THREAD_PATH = "Spark Odometry Thread";

    public static enum RobotModes {
      SIM,
      REAL
    }
  }

  public static final class DriveConsts {
    public static final String SWERVE_STATES_LOG_ENTRY = "Swerve/States";

    public static final String CANCODER_MODULE_LOG_ENTRY = "Swerve/CanCoders/Module";

    public static final String ODOMETRY_GYRO_LOG_ENTRY = "RealOutputs/Swerve/Odometry/Gyro";
    public static final String ODOMETRY_POSE2D_LOG_ENTRY = "Swerve/Odometry/Pose2d";

    public static final String ACTIVE_TRACJECTORY_LOG_ENTRY = "Swerve/Auto/Active Trajectory";
    public static final String TRAJECTORY_SETPOINT_LOG_ENTRY = "Swerve/Auto/Trajectory Setpoint";

    public static final int CANCODER_MODULE1_ID = 10;
    public static final int CANCODER_MODULE2_ID = 11;
    public static final int CANCODER_MODULE3_ID = 12;
    public static final int CANCODER_MODULE4_ID = 13;

    public static final double AUTO_TRANSLATION_kP = 0.01;
    public static final double AUTO_TRANSLATION_kI = 0;
    public static final double AUTO_TRANSLATION_kD = 0.001;

    public static final double AUTO_ROTATION_kP = 0.08;
    public static final double AUTO_ROTATION_kI = 0;
    public static final double AUTO_ROTATION_kD = 0.0;

    public static final int ODOMETRY_FREQUENCY = 100;

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

  public static final class FieldConsts {
    public static final double FIELD_WIDTH_METERS = 8.21; // X
    public static final double FIELD_LENGTH_METERS = 16.54; // Y

    public static final double FUEL_DIAMETER = 0.15; // CM -> M
    public static final double FUEL_RADIUS = FUEL_DIAMETER / 2; // CM -> M
    public static final double FUEL_SPACING = 0.15; // CM -> M

    public static final Pose2d BLUE_LEFT_START_POSE =
        new Pose2d(3.52, 7.44, Rotation2d.fromDegrees(0));

    public static final Pose2d BLUE_CENTER_START_POSE =
        new Pose2d(3.14, 3.96, Rotation2d.fromDegrees(0));

    public static final Pose2d BLUE_RIGHT_START_POSE =
        new Pose2d(3.54, 0.66, Rotation2d.fromDegrees(0));

    public static final Pose2d RED_LEFT_START_POSE =
        AllianceFlip.flipPose2dToRed(BLUE_LEFT_START_POSE);

    public static final Pose2d RED_CENTER_START_POSE =
        AllianceFlip.flipPose2dToRed(BLUE_CENTER_START_POSE);

    public static final Pose2d RED_RIGHT_START_POSE =
        AllianceFlip.flipPose2dToRed(BLUE_RIGHT_START_POSE);

    public static final Pose2d FIELD_CENTER_POSE =
        new Pose2d(FIELD_LENGTH_METERS / 2, FIELD_WIDTH_METERS / 2, Rotation2d.fromDegrees(0));
  }

  public static final class Joysticks {
    public static final int DRIVER_PORT = 0;
    public static final double DRIVER_DEADBAND = 0.1;

    public static final int MECHANISM_PORT = 1;
  }

  public static final class Components {
    public static final int PIGEON2 = 9;
    public static final String LIMELIGHT = "limelight";
  }
}
