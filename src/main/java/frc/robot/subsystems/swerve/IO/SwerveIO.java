package frc.robot.subsystems.swerve.IO;

import com.ctre.phoenix6.hardware.Pigeon2;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import java.util.function.DoubleSupplier;

import org.littletonrobotics.junction.AutoLog;

public interface SwerveIO {
  @AutoLog
  public static class SwerveInputs{
    public Pose2d currentPose = new Pose2d();
    public Rotation2d heading = new Rotation2d();
    public ChassisSpeeds currentSpeed = new ChassisSpeeds();
  }

  public Pose2d getPose();

  public Rotation2d getHeading();

  public ChassisSpeeds getRobotRelativeSpeeds();

  public void resetOdometry(Pose2d pose);

  public Pose2d getPoseSim();

  public void resetOdometrySim(Pose2d pose);

  public void driveFieldOriented(ChassisSpeeds speed);

  public Pigeon2 getPigeon();

  public Command getAutonomousCommand(String path, boolean altern);

  public Command driveCommand(
      DoubleSupplier x, DoubleSupplier y, DoubleSupplier omega, boolean fieldOriented);

  public void updateInputs(SwerveInputs inputs);
}
