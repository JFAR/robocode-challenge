package jafarbot;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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
import robocode.util.Utils;

public class JafarBot extends AdvancedRobot {

    private static int count = 0;
    private double turn = 2;
    private final double direction = 1;
    private double oldEnergy = 100;
    private int currentEnemyVelocity;
    private long oldTime;
    private boolean fired;
    private int aimingEnemyVelocity;
    static double enemyVelocities[][] = new double[400][4];
    private int averageCount;
    private int velocityToAimAt;
    private double oldEnemyHeading;

    @Override
    public void run() {
        try {
            initComponents();
            initColors();

            setAdjustGunForRobotTurn(true);
            setAdjustRadarForGunTurn(true);

            while (true) {
                //Add your execute methods here
                turnRadarRightRadians(Double.POSITIVE_INFINITY);
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
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        try {

            double absBearing = e.getBearingRadians() + getHeadingRadians();
            Graphics2D g = getGraphics();

            // increase our turn speed amount each tick,to a maximum of 8 and a minimum of 4
            turn += 0.2 * Math.random();
            if (turn > 8) {
                turn = 2;
            }

            double goalDirection = absBearing - Math.PI / 2 * direction;
            Rectangle2D fieldRect = new Rectangle2D.Double(18, 18, getBattleFieldWidth() - 36,
                    getBattleFieldHeight() - 36);
            while (!fieldRect.contains(getX() + Math.sin(goalDirection) * 120, getY() +
                    Math.cos(goalDirection) * 120)) {
                goalDirection += direction * .1; // turn a little toward enemy and try again
            }

            double turn = robocode.util.Utils.normalRelativeAngle(goalDirection - getHeadingRadians());
            if (Math.abs(turn) > Math.PI / 2) {
                turn = robocode.util.Utils.normalRelativeAngle(turn + Math.PI);
                setBack(100);
            } else
                setAhead(100);
            setTurnRightRadians(turn);

            oldEnergy = e.getEnergy();

            // find our which velocity segment our enemy is at right now
            if (e.getVelocity() < -2) {
                currentEnemyVelocity = 0;
            } else if (e.getVelocity() > 2) {
                currentEnemyVelocity = 1;
            } else if (e.getVelocity() <= 2 && e.getVelocity() >= -2) {
                if (currentEnemyVelocity == 0) {
                    currentEnemyVelocity = 2;
                } else if (currentEnemyVelocity == 1) {
                    currentEnemyVelocity = 3;
                }
            }

            // update the one we are using to determine where to store our velocities if we have fired and there has been enough time for a bullet to
            // reach an enemy
            // (only a rough approximation of bullet travel time);
            if (getTime() - oldTime > e.getDistance() / 12.8 && fired == true) {
                aimingEnemyVelocity = currentEnemyVelocity;
            } else {
                fired = false;
            }

            // record a new enemy velocity and raise the count
            enemyVelocities[count][aimingEnemyVelocity] = e.getVelocity();
            count++;
            if (count == 400) {
                count = 0;
            }

            // calculate our average velocity for our current segment
            averageCount = 0;
            velocityToAimAt = 0;
            while (averageCount < 400) {
                velocityToAimAt += enemyVelocities[averageCount][currentEnemyVelocity];
                averageCount++;
            }
            velocityToAimAt /= 400;

            double bulletPower = Math.min(2.4, Math.min(e.getEnergy() / 4, getEnergy() / 10));
            double myX = getX();
            double myY = getY();
            double enemyX = getX() + e.getDistance() * Math.sin(absBearing);
            double enemyY = getY() + e.getDistance() * Math.cos(absBearing);
            double enemyHeading = e.getHeadingRadians();
            double enemyHeadingChange = enemyHeading - oldEnemyHeading;
            oldEnemyHeading = enemyHeading;
            double deltaTime = 0;
            double battleFieldHeight = getBattleFieldHeight(),
                    battleFieldWidth = getBattleFieldWidth();
            double predictedX = enemyX, predictedY = enemyY;
            while ((++deltaTime) * (20.0 - 3.0 * bulletPower) < Point2D.Double.distance(myX, myY, predictedX, predictedY)) {
                predictedX += Math.sin(enemyHeading) * velocityToAimAt;
                predictedY += Math.cos(enemyHeading) * velocityToAimAt;
                enemyHeading += enemyHeadingChange;
                g.setColor(Color.red);
                g.fillOval((int) predictedX - 2, (int) predictedY - 2, 4, 4);
                if (predictedX < 18.0
                        || predictedY < 18.0
                        || predictedX > battleFieldWidth - 18.0
                        || predictedY > battleFieldHeight - 18.0) {

                    predictedX = Math.min(Math.max(18.0, predictedX),
                            battleFieldWidth - 18.0);
                    predictedY = Math.min(Math.max(18.0, predictedY),
                            battleFieldHeight - 18.0);
                    break;
                }
            }
            double theta = Utils.normalAbsoluteAngle(Math.atan2(
                    predictedX - getX(), predictedY - getY()));

            setTurnRadarRightRadians(Utils.normalRelativeAngle(
                    absBearing - getRadarHeadingRadians()) * 2);
            setTurnGunRightRadians(Utils.normalRelativeAngle(
                    theta - getGunHeadingRadians()));
            if (getGunHeat() == 0) {
                fire(bulletPower);
                fired = true;
            }

        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }


    //Somebody died, maybe record some information about it?
    @Override
    public void onRobotDeath(RobotDeathEvent e) {
        try {

        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }


    //You got hit by a bullet.. FIGHT OR FLIGHT
    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        try {

        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }


    //You hit someone with your gun, make sure to use that to your advantage
    @Override
    public void onBulletHit(BulletHitEvent e) {
        try {

        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }


    //Bullets can disable other bullets, there is also an event for this
    @Override
    public void onBulletHitBullet(BulletHitBulletEvent e) {
        try {

        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }

    //Trivial
    @Override
    public void onHitWall(HitWallEvent e) {
        System.out.println("WALL HIT: (" + getTime() + ").");
    }


    //Congrats
    @Override
    public void onWin(WinEvent e) {
        try {

        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }


    //GG, code some more to kill them all!
    @Override
    public void onDeath(DeathEvent e) {
        try {

        } catch (RuntimeException re) {
            System.out.println(re);
        }
    }


    //If your robot gets too big it might not have enough time to complete a turn
    //There is an event for skipped turns, use this to debug
    @Override
    public void onSkippedTurn(SkippedTurnEvent e) {
        System.out.println("TURN SKIP : " + e.getTime());
    }



}
