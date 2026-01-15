package frc.robot.subsystems.intake;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeSubsystem extends SubsystemBase {
    private static IntakeSubsystem m_instance;
    
    public static IntakeSubsystem getInstance() {
        if (m_instance == null) m_instance = new IntakeSubsystem();
        return m_instance;
    }
}
