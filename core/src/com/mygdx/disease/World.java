package com.mygdx.disease;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.PooledLinkedList;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class World extends Stage {
    PooledLinkedList<Human> humanList;

    public World() {
        super(new FitViewport(84, 60));

        humanList = new PooledLinkedList<>(1000);

        Image bg = new Image(TextureManager.get("background.png"));
        bg.setBounds(-1000,-1000,30000,30000);
        this.addActor(bg);
        this.addActor(new Human(84,40));
        for (int i = 0 ; i < 300; i++){
            this.addActor(new Human());
        }
        Human h = new Human();
        h.phase = Phase.Sick;
        this.addActor(h);

        Parameters.computeInfectiousness();
    }

    public void resize(int width, int height){
        this.getViewport().update(width, height,true);
        this.getCamera().update();
    }


    public void addActor(Human h){
        super.addActor(h);
        humanList.add(h);
    }
}
