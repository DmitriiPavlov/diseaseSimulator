package com.mygdx.disease;

import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Human extends Image {
    public long phaseStart;
    public Phase phase;

    public Bezier<Vector2> curve;
    public float t;

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
        Vector2 x3 = new Vector2(MathUtils.random(0,Parameters.worldWidth), MathUtils.random(0,Parameters.worldHeight));
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
        if (MathUtils.random() <= Parameters.infectiousnessPerFrame) {
            if (phase == Phase.Carrier | phase == Phase.Sick) {
                DiseaseSimulator.w.humanList.iter();
                Human h = DiseaseSimulator.w.humanList.next();
                while (h != null){
                    if (!h.equals(this) && h.phase == Phase.Healthy && h.distanceSquared(this) <= Parameters.infectiousRadius*Parameters.infectiousRadius) {
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
        updateColor();
    }

    public void advancePhases() {
        if (phase == Phase.Healthy){
            return;
        }
        //multiply by
        else if (phase == Phase.Carrier && TimeUtils.timeSinceMillis(phaseStart) > Parameters.incubationTime * Parameters.dayLength){
            setPhase(Phase.Sick);
        }
        else if (phase == Phase.Sick){
            if (MathUtils.random() <= Parameters.deadlinessPerFrame){
                killSelf();
            }

            else if (TimeUtils.timeSinceMillis(phaseStart) > Parameters.sicknessTime * Parameters.dayLength){
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
