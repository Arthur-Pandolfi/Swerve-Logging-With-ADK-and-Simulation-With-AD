package frc.robot.subsystems.swerve.IO;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import java.util.function.DoubleSupplier;

public interface SwerveIO {
  public Pose2d getPose();

  public Rotation2d getHeading();

  public ChassisSpeeds getRobotRelativeSpeeds();

  public void resetOdometry(Pose2d pose);

  public Pose2d getPoseSim();

  public void resetOdometrySim(Pose2d pose);

  public void driveFieldOriented(ChassisSpeeds speed);

  public void resetSwerve();

  public Command getAutonomousCommand(String path, boolean altern);

  public Command driveCommand(
      DoubleSupplier x, DoubleSupplier y, DoubleSupplier omega, boolean fieldOriented);
}
