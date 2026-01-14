package frc.frc_java9485.utils;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.Constants.FieldConsts;

import java.util.ArrayList;
import java.util.List;

import org.littletonrobotics.junction.Logger;

import com.pathplanner.lib.util.FlippingUtil;

import swervelib.simulation.ironmaple.simulation.SimulatedArena;
import swervelib.simulation.ironmaple.simulation.seasonspecific.rebuilt2026.Arena2026Rebuilt;
import swervelib.simulation.ironmaple.simulation.seasonspecific.rebuilt2026.RebuiltFuelOnField;
import swervelib.simulation.ironmaple.simulation.seasonspecific.rebuilt2026.RebuiltHub;
import swervelib.simulation.ironmaple.simulation.seasonspecific.rebuilt2026.RebuiltOutpost;

@SuppressWarnings("unused")
public class Simulation {
  private static Simulation m_instance;

  private final Translation2d[] blueDepositFuelStartPoses =
      new Translation2d[] {
        new Translation2d(0.1, 5.6), new Translation2d(0.1, 5.75), new Translation2d(0.1, 5.9),
        new Translation2d(0.1, 6.05), new Translation2d(0.1, 6.20), new Translation2d(0.1, 6.35),
        new Translation2d(0.254, 5.6), new Translation2d(0.254, 5.75), new Translation2d(0.254, 5.9),
        new Translation2d(0.254, 6.05), new Translation2d(0.254, 6.20), new Translation2d(0.254, 6.35),
        new Translation2d(0.408, 5.6), new Translation2d(0.408, 5.75), new Translation2d(0.408, 5.9),
        new Translation2d(0.408, 6.05), new Translation2d(0.408, 6.20), new Translation2d(0.408, 6.35),
        new Translation2d(0.512, 5.6), new Translation2d(0.512, 5.75), new Translation2d(0.512, 5.9),
        new Translation2d(0.512, 6.05), new Translation2d(0.512, 6.20), new Translation2d(0.512, 6.35),
      };

  private final RebuiltHub redHub;
  private final RebuiltHub blueHub;
  private final SimulatedArena arena;
  private final RebuiltOutpost blueOutpost;
  private final RebuiltOutpost redOutpost;
  private final Arena2026Rebuilt rebuiltArena;

  public static Simulation getInstance() {
    if (m_instance == null) {
      m_instance = new Simulation();
    }

    return m_instance;
  }

  private Simulation() {
    rebuiltArena = new Arena2026Rebuilt(true);

    redHub = new RebuiltHub(rebuiltArena, false);
    blueHub = new RebuiltHub(rebuiltArena, true);

    redOutpost = new RebuiltOutpost(rebuiltArena, false);
    blueOutpost = new RebuiltOutpost(rebuiltArena, true);

    SimulatedArena.overrideInstance(rebuiltArena);

    arena = SimulatedArena.getInstance();

    // Blue Deposit Fuel
    for (Translation2d fuelPose : blueDepositFuelStartPoses) {
      arena.addGamePiece(new RebuiltFuelOnField(fuelPose));
    }

    // Red Deposit Fuel
    for (Translation2d fuelPose : blueDepositFuelStartPoses) {
      arena.addGamePiece(new RebuiltFuelOnField(flipFuelToRed(fuelPose)));
    }

    generateMiddleFuels();
  }

  public void updateArena() {
    Pose3d[] fuelPoses = arena.getGamePiecesArrayByType("Fuel");
    Logger.recordOutput("Field Simulation/Fuel poses", fuelPoses);

    arena.simulationPeriodic();
  }

  private Translation2d flipFuelToRed(Translation2d translation) {
    translation = AllianceFlip.flipTranslation2dToRed(translation);
    return new Translation2d(
      translation.getX(),
      (FieldConsts.FIELD_WIDTH_METERS - 0.122) - translation.getY()
    );
  }

  private void generateMiddleFuels() {
    int fuelXNumber = 20;
    int fuelYNumber = 18;

    double startXUp = 9.166;
    double startYUp = 1.789;

    double startXBottom = 7.415;
    double startYBottom = 1.812;
    
    Translation2d startTopTranslation = new Translation2d(startXUp, startYUp);
    Translation2d startBottomTranslation = new Translation2d(startXBottom, startYBottom);

    Translation2d verticalY = startTopTranslation.minus(startBottomTranslation);
    verticalY = verticalY.div(verticalY.getNorm()).times(FieldConsts.FUEL_SPACING);

    Translation2d verticalX = new Translation2d(-verticalY.getY(), verticalY.getX());
    verticalX = verticalX.div(verticalX.getNorm()).times(FieldConsts.FUEL_SPACING);

    for (int iX = 0; iX < fuelXNumber; iX++) {
      for (int iY = 0; iY < fuelYNumber; iY++) {

        Translation2d pos = startBottomTranslation
            .plus(verticalX.times(iX))
            .plus(verticalY.times(iY));

        arena.addGamePiece(new RebuiltFuelOnField(pos));
      }
    }
  }
}
