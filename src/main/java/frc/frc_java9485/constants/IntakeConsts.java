package frc.frc_java9485.constants;

import frc.frc_java9485.utils.TunableControls.ControlConstants;
import frc.frc_java9485.utils.TunableControls.TunableControlConstants;

public class IntakeConsts {
    public static final int CATCH_BALL_ID = 9;
    public static final int THROW_INTAKE_ID = 10;

    public static final int A_ENCODER_CHANNEL = 8;
    public static final int B_ENCODER_CHANNEL = 9;
    public static final boolean ENCODER_INVERTED = true;

    public static final double SETPOINT_UP = 113.00;
    public static final double SETPOINT_DOWN = 10;

    public static final ControlConstants CATCH_BALLS_CONSTANTS = new ControlConstants()
    .withPID(0.07, 0, 0.007)
    .withTolerance(3)
    .withProfile(150, 100);

    public static final TunableControlConstants TUNABLE_CATCH_BALL_CONST =
        new TunableControlConstants("tunable catch balls",  CATCH_BALLS_CONSTANTS);
}
