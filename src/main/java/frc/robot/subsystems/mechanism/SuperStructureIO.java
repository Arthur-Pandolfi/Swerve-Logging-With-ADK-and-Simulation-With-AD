package frc.robot.subsystems.mechanism;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.mechanism.SuperStructure.Actions;

import org.littletonrobotics.junction.AutoLog;

public interface SuperStructureIO {

  @AutoLog
  public static class SuperStructureInputs {
    public double shooter_speed = 0;
    public double turret_angle = 0;
    public double hood_angle = 0;
    public double intake_speed = 0;
    public boolean intake_is_collecting = false;
    public boolean turrent_is_shooting = false;
    public Actions current_action = Actions.NORMAL;
    public boolean inCorrectAngle = false;
  }

  public Command setAction(Actions action);

  public Actions getAction();

  public boolean isShooting();

  public boolean isColecting();

  public boolean isClimbing();

  public boolean turretIsInCorrectAngle();

  public void updateInputs(SuperStructureInputs inputs);
}
