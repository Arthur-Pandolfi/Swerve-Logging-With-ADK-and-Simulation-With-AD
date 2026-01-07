package frc.robot.subsystems.swerve;

import swervelib.SwerveDrive;
import swervelib.SwerveModule;
import swervelib.parser.SwerveParser;
import swervelib.telemetry.SwerveDriveTelemetry;
import swervelib.telemetry.SwerveDriveTelemetry.TelemetryVerbosity;

import java.io.File;
import java.util.function.DoubleSupplier;

import org.littletonrobotics.junction.Logger;

import frc.frc_java9485.utils.MathUtils;
import frc.robot.Constants.Components;
import frc.robot.Constants.SwerveConsts;
import frc.robot.subsystems.vision.LimelightHelpers;

import com.ctre.phoenix6.hardware.Pigeon2;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;


public class Swerve extends SubsystemBase implements SwerveIO{
    private final Pigeon2 pigeon;
    private final SwerveDrive swerveDrive;
    private final SwerveDriveKinematics kinematics;
    private final SwerveDrivePoseEstimator poseEstimator;

    private SwerveModule[] modules;
    private SwerveModuleState state[];

    public static Swerve mInstance = null;

    public static Swerve getInstance(){
        if(mInstance == null){
            mInstance = new Swerve(new File(Filesystem.getDeployDirectory(), "swerve"));
        }
        return mInstance;
    }

    private Swerve(File directory){
        try {
            swerveDrive = new SwerveParser(directory).createSwerveDrive(SwerveConsts.MAX_SPEED);

            pigeon = new Pigeon2(Components.PIGEON2);
            kinematics = new SwerveDriveKinematics(SwerveConsts.MODULES_TRANSLATION);

            poseEstimator = new SwerveDrivePoseEstimator(
                kinematics,
                getHeading(), 
                swerveDrive.getModulePositions(),
                new Pose2d()
            );

            setupPathPlanner();

            SwerveDriveTelemetry.verbosity =  TelemetryVerbosity.INFO;

        } catch (Exception e){
            throw new RuntimeException("Erro criando swerve drive!\nStack Trace: " + e.getStackTrace() 
            + "\n Causa: " + e.getCause()
            + "\n Mensagem: " + e.getMessage());
        }
    }
    
    private void setupPathPlanner() {
        RobotConfig config;
        try {
            config = RobotConfig.fromGUISettings();
            AutoBuilder.configure(
                this::getPose,
                this::resetOdometry,
                this::getRobotRelativeSpeeds,
                (speeds, feedforwards) -> driveFieldOriented(speeds),
                new PPHolonomicDriveController( 
                    new PIDConstants(SwerveConsts.AUTO_TRANSLATION_kP, SwerveConsts.AUTO_TRANSLATION_kI, SwerveConsts.AUTO_TRANSLATION_kD), // Translation
                    new PIDConstants(SwerveConsts.AUTO_ROTATION_kP, SwerveConsts.AUTO_ROTATION_kI, SwerveConsts.AUTO_ROTATION_kD) // Rotation
                ),
                config,
                () -> {
                    var alliance = DriverStation.getAlliance();
                    if (alliance.isPresent()) {
                        return alliance.get() == DriverStation.Alliance.Red;
                    }
                    return false;
                },
                this 
            );
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void periodic() {
        if (poseEstimator != null) {
            Pose2d poseEstimated = LimelightHelpers.getBotPose2d("");
            poseEstimator.update(pigeon.getRotation2d(), swerveDrive.getModulePositions());
            poseEstimator.addVisionMeasurement(poseEstimated, Timer.getFPGATimestamp());

            Logger.recordOutput(getName() + "Pose", getPose());
        }        
        
        SwerveDriveTelemetry.updateData();
    }

    @Override
    public Pose2d getPose() {
        return poseEstimator.getEstimatedPosition();
    }

    @Override
    public ChassisSpeeds getRobotRelativeSpeeds() {
        return swerveDrive.getRobotVelocity();
    }

    @Override
    public void driveFieldOriented(ChassisSpeeds speed){
      swerveDrive.driveFieldOriented(speed);
    }

    @Override
    public void resetOdometry(Pose2d pose) {
        poseEstimator.resetPose(pose);
    }

    @Override
    public void resetSwerve(){
        pigeon.reset();
        swerveDrive.zeroGyro();
    }

    @Override
    public Rotation2d getHeading(){
        return Rotation2d.fromDegrees(MathUtils.scope0To360(pigeon.getYaw().getValueAsDouble()));
    }

    @Override
    public Command getAutonomousCommand(String path, boolean altern){
    if(altern){
      return AutoBuilder.buildAuto(path);
    }
      return new PathPlannerAuto(path);
    }

    @Override
    public Command driveCommand(DoubleSupplier X, DoubleSupplier Y, DoubleSupplier omega, boolean fieldOriented){
        return run(() ->{
            double Xcontroller = Math.pow(X.getAsDouble(), 3);
            double Ycontroller = Math.pow(Y.getAsDouble(), 3);
            double rotation = omega.getAsDouble();
            double td = 0.02;

            ChassisSpeeds speeds = fieldOriented ? ChassisSpeeds.fromFieldRelativeSpeeds(Xcontroller * swerveDrive.getMaximumChassisVelocity(), 
                                                                                        Ycontroller * swerveDrive.getMaximumChassisVelocity(),
                                                                                        rotation * swerveDrive.getMaximumChassisAngularVelocity(),
                                                                                        pigeon.getRotation2d()
                                                                                        )
                                                                                        :
                                                                                        new ChassisSpeeds(Xcontroller * swerveDrive.getMaximumChassisVelocity(),
                                                                                                          Ycontroller * swerveDrive.getMaximumChassisVelocity(),
                                                                                                          rotation * swerveDrive.getMaximumChassisAngularVelocity()
                                                                                                          );

            ChassisSpeeds discretize = ChassisSpeeds.discretize(speeds, td);
            state = swerveDrive.kinematics.toSwerveModuleStates(discretize);
            SwerveDriveKinematics.desaturateWheelSpeeds(state, SwerveConsts.MAX_SPEED);
            modules = swerveDrive.getModules();

            for(int i = 0; i < state.length; i++){
                modules[i].setDesiredState(state[i], true, true);
            }
        });
    }
}