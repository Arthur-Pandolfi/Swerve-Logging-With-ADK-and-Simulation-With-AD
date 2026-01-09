package frc.frc_java9485.utils;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.Constants.FieldConsts;

public class AllianceFlipUtil {
  private AllianceFlipUtil() {}

  public static Pose2d flipToRed(Pose2d bluePose) {
    return new Pose2d(
        FieldConsts.FIELD_LENGTH_METERS - bluePose.getX(),
        bluePose.getY(),
        bluePose.getRotation().plus(Rotation2d.k180deg));
  }

  public static Pose2d flipToRedAndNormalize(Pose2d bluePose) {
    return new Pose2d(
        FieldConsts.FIELD_LENGTH_METERS - bluePose.getX(),
        bluePose.getY(),
        bluePose.getRotation().plus(Rotation2d.k180deg).minus(Rotation2d.fromDegrees(360)));
  }
}
