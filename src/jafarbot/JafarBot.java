package jafarbot;
import robocode.AdvancedRobot;
import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.SkippedTurnEvent;
import robocode.WinEvent;

import java.awt.*;

public class JafarBot extends AdvancedRobot {

    public void run() {
        try {
            initComponents();
            initColors();

            setAdjustGunForRobotTurn(true);
            setAdjustRadarForGunTurn(true);

            while (true) {
                //Add your execute methods here
                execute();
            }
        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }

    //Initialization process
    //If you have data structures or preprocessing before the match
    private void initComponents() {
    }

    //Fancy colours for your bot
    private void initColors() {
        Color thgColor = new Color(142, 255, 242);
        setColors(Color.black, Color.red, thgColor);
    }


    //When you scan an opponent do something
    public void onScannedRobot(ScannedRobotEvent e) {
        try {


        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }


    //Somebody died, maybe record some information about it?
    public void onRobotDeath(RobotDeathEvent e) {
        try {

        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }


    //You got hit by a bullet.. FIGHT OR FLIGHT
    public void onHitByBullet(HitByBulletEvent e) {
        try {

        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }


    //You hit someone with your gun, make sure to use that to your advantage
    public void onBulletHit(BulletHitEvent e) {
        try {

        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }


    //Bullets can disable other bullets, there is also an event for this
    public void onBulletHitBullet(BulletHitBulletEvent e) {
        try {

        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }

    //Trivial
    public void onHitWall(HitWallEvent e) {
        System.out.println("WALL HIT: (" + getTime() + ").");
    }


    //Congrats
    public void onWin(WinEvent e) {
        try {

        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }


    //GG, code some more to kill them all!
    public void onDeath(DeathEvent e) {
        try {

        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }


    //If your robot gets too big it might not have enough time to complete a turn
    //There is an event for skipped turns, use this to debug
    public void onSkippedTurn(SkippedTurnEvent e) {
        System.out.println("TURN SKIP : " + e.getTime());
    }
    


}
