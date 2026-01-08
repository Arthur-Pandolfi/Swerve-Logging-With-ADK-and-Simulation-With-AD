package frc.frc_java9485.joystick.mechanism;

import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import frc.robot.Constants.Joysticks;

public class MechanismJoystick implements MechanismJoystickIO {
  private final CommandJoystick joystick;
  private static MechanismJoystick mInstnace;

  public static MechanismJoystick getInstance() {
    if (mInstnace == null) {
      mInstnace = new MechanismJoystick();
    }
    return mInstnace;
  }

  private MechanismJoystick() {
    joystick = new CommandJoystick(Joysticks.MECHANISM_PORT);
  }
}
