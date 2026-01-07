package frc.robot.subsystems.swerve;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.math.kinematics.ChassisSpeeds;

public interface SwerveIO {
    public Pose2d getPose();
    public Rotation2d getHeading();
    public ChassisSpeeds getRobotRelativeSpeeds();
    
    public void resetOdometry(Pose2d pose);
    public void driveFieldOriented(ChassisSpeeds speed);

    public void resetSwerve();
    public Command getAutonomousCommand(String path, boolean altern);
    public Command driveCommand(DoubleSupplier x, DoubleSupplier y, DoubleSupplier omega, boolean fieldOriented);
}