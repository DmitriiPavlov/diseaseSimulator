package com.mygdx.disease;

import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Human extends Image {
    public long phaseStart;
    public Phase phase;

    public Bezier<Vector2> curve;
    public float t;

    //makes death a lot more likely
    boolean vulnerable = false;
    //makes infecting others twice less likely, and makes the disease last half the time
    boolean vaccinated = false;
    boolean mask = false;
    boolean quarantine = true;

    public Human(float x, float y){
        super(TextureManager.get("circle.png"));
        this.setBounds(x,y,1,1);
        generatePath();
        phase = Phase.Healthy;
        updateColor();
    }
    public Human(){
        this(MathUtils.random(0, Parameters.worldWidth), MathUtils.random(0, Parameters.worldHeight));
    }

    @Override
    public void act(float deltaTime){
        actMotion(deltaTime);
        infectOthers();
        advancePhases();
    }

    public void generatePath(){
        Vector2 x0 = new Vector2(this.getX(),this.getY());

        if (Phase.Sick == phase && quarantine && (Parameters.worldWidth- Parameters.quarantineZoneSize <= x0.x && x0.x <= Parameters.worldWidth) && (Parameters.worldHeight - Parameters.quarantineZoneSize <= x0.y && x0.y <= Parameters.worldHeight)){
            Vector2 x1, x2, x3;
            x1 = new Vector2(MathUtils.random(Parameters.worldWidth- Parameters.quarantineZoneSize, Parameters.worldWidth), MathUtils.random(Parameters.worldHeight- Parameters.quarantineZoneSize, Parameters.worldHeight));
            x2 = new Vector2(MathUtils.random(Parameters.worldWidth- Parameters.quarantineZoneSize, Parameters.worldWidth), MathUtils.random(Parameters.worldHeight- Parameters.quarantineZoneSize, Parameters.worldHeight));
            x3 = new Vector2(MathUtils.random(Parameters.worldWidth- Parameters.quarantineZoneSize, Parameters.worldWidth), MathUtils.random(Parameters.worldHeight- Parameters.quarantineZoneSize, Parameters.worldHeight));
            curve = new Bezier<Vector2>(x0,x1,x2,x3);
            return;
        }

        Vector2 temp = new Vector2();
        Vector2 x1;
        if (curve != null){
            x1 = curve.derivativeAt(temp,t).nor().scl(20.0f);
            x1.add(x0);
        }
        else {
            x1 = new Vector2(MathUtils.random(0, Parameters.worldWidth), MathUtils.random(0, Parameters.worldHeight));
        }
        Vector2 x2 = new Vector2(MathUtils.random(0,Parameters.worldWidth), MathUtils.random(0,Parameters.worldHeight));
        Vector2 x3;
        if (quarantine && phase == Phase.Sick) {
            //that means we path to the bottom right of the map
            x3 = new Vector2(MathUtils.random(Parameters.worldWidth- Parameters.quarantineZoneSize, Parameters.worldWidth), MathUtils.random(Parameters.worldHeight- Parameters.quarantineZoneSize, Parameters.worldHeight));
        }
        else {
            if (quarantine) {
                while (true) {
                    x3 = new Vector2(MathUtils.random(0, Parameters.worldWidth), MathUtils.random(0, Parameters.worldHeight));
                    if ((Parameters.worldWidth- Parameters.quarantineZoneSize <= x3.x && x3.x <= Parameters.worldWidth) && (Parameters.worldHeight - Parameters.quarantineZoneSize <= x3.y && x3.y <= Parameters.worldHeight)){
                        continue;
                    }
                    else{
                        break;
                    }
                }
            }
            else{
                x3 = new Vector2(MathUtils.random(0, Parameters.worldWidth), MathUtils.random(0, Parameters.worldHeight));
            }

        }

        curve = new Bezier<Vector2>(x0,x1,x2,x3);

    }

    public void actMotion(float deltaTime){
        Vector2 pos = new Vector2();
        curve.derivativeAt(pos,t);

        //we want to speed this up based on the day length, with the default being the day at 5 seconds
        t += Parameters.moveSpeed*5*deltaTime / pos.len() * ((5*1000)/(float) Parameters.dayLength );

        curve.valueAt(pos,t);
        this.moveBy(pos.x - getX(), pos.y - getY());

        if (t>=1){
            generatePath();
            t = 0;
        }
    }

    public float distanceSquared(Human other){
        return (other.getX() - this.getX())*(other.getX() - this.getX()) + (other.getY() - this.getY())*(other.getY() - this.getY());
    }

    public void infectOthers(){
        if ((!vaccinated && MathUtils.random() <= Parameters.infectiousnessPerFrame) || (vaccinated && MathUtils.random() <= Parameters.vaccinatedInfectiousnessPerFrame)) {
            if (phase == Phase.Carrier | phase == Phase.Sick) {
                DiseaseSimulator.w.humanList.iter();
                Human h = DiseaseSimulator.w.humanList.next();
                //because it needs to scale quadratically it is 0.25 rather than 0.5f
                float maskedMultiplier = mask ? 0.25f : 1.0f;
                while (h != null){
                    if (!vaccinated && !h.equals(this) && h.phase == Phase.Healthy && h.distanceSquared(this) <= Parameters.infectiousRadius*Parameters.infectiousRadius * maskedMultiplier) {
                        h.setPhase(Phase.Carrier);
                        break;
                    }

                    else if (vaccinated && !h.equals(this) && h.phase == Phase.Healthy && h.distanceSquared(this) <= Parameters.vaccinatedRadius*Parameters.vaccinatedRadius * maskedMultiplier) {
                        h.setPhase(Phase.Carrier);
                        break;
                    }

                    h = DiseaseSimulator.w.humanList.next();
                }

            }
        }
    }

    public void updateColor(){
        if (phase == Phase.Healthy){
            this.setColor(0.0f,0.5f,0.5f,1.0f);
        }

        if (phase == Phase.Carrier){
            this.setColor(1.0f,0.5f,0.2f,1.0f);
        }

        if (phase == Phase.Sick){
            this.setColor(1.0f,0.0f,0.0f,1.0f);
        }

        if (phase == Phase.Cured){
            this.setColor(0.7f,0.0f,0.8f,1.0f);
        }
    }

    public void setPhase(Phase p){
        this.phase = p;
        phaseStart = TimeUtils.millis();
        if (phase == Phase.Sick){
            generatePath();
            t = 0;
        }
        updateColor();
    }

    public void advancePhases() {
        if (phase == Phase.Healthy){
            return;
        }

        else if (phase == Phase.Carrier){
            if (TimeUtils.timeSinceMillis(phaseStart) > Parameters.incubationTime * Parameters.dayLength) {
                setPhase(Phase.Sick);
            }
            else if (vaccinated && TimeUtils.timeSinceMillis(phaseStart) > Parameters.incubationTime * Parameters.dayLength * 0.5f){
                setPhase(Phase.Sick);
            }
        }
        else if (phase == Phase.Sick){

            if (vulnerable && MathUtils.random() <= Parameters.vulnerableDeadlinessPerFrame){
                killSelf();
                return;
            }

            if (!vulnerable && MathUtils.random() <= Parameters.deadlinessPerFrame){
                killSelf();
                return;
            }

            if (TimeUtils.timeSinceMillis(phaseStart) > Parameters.sicknessTime * Parameters.dayLength){
                setPhase(Phase.Cured);
            }

            else if (vaccinated && TimeUtils.timeSinceMillis(phaseStart) > Parameters.sicknessTime * Parameters.dayLength * 0.5f){
                setPhase(Phase.Cured);
            }

        }

        else if (phase == Phase.Cured && TimeUtils.timeSinceMillis(phaseStart) > Parameters.curedTime * Parameters.dayLength){
            setPhase(Phase.Healthy);
        }
    }

    public void killSelf(){
        DiseaseSimulator.w.humanList.iter();
        Human h = DiseaseSimulator.w.humanList.next();
        while (h!=this){
            h = DiseaseSimulator.w.humanList.next();
        }

        DiseaseSimulator.w.humanList.remove();
        this.getParent().removeActor(this);
    }
}
