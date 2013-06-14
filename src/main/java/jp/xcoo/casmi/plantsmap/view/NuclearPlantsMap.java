/*
 *   Nuclear Plants Map
 *   http://casmi.github.com/
 *   Copyright (C) 2011, Xcoo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.xcoo.casmi.plantsmap.view;

import java.util.List;

import jp.xcoo.casmi.plantsmap.data.Plant;
import jp.xcoo.casmi.plantsmap.data.PowerPlantLoader;
import casmi.Applet;
import casmi.AppletRunner;
import casmi.KeyEvent;
import casmi.MouseButton;
import casmi.MouseEvent;
import casmi.Trackball;
import casmi.graphics.color.RGBColor;
import casmi.graphics.element.Element;
import casmi.graphics.element.MouseClickCallback;
import casmi.graphics.element.Rect;
import casmi.graphics.element.Sphere;
import casmi.graphics.element.Text;
import casmi.graphics.element.Texture;
import casmi.graphics.group.Group;
import casmi.matrix.Matrix3D;
import casmi.matrix.Vertex;

/**
 * Nuclear Plants Map main applet
 *
 * @author Y. Ban
 * @author Takashi AOKI <federkasten@me.com>
 */
public class NuclearPlantsMap extends Applet {

	private static final String TITLE = "Nuclear Plants Map";

    private Sphere earth = null;
    private Texture earthTexture = null;

    private Rect boardBackground = null;

    private Text labelCapacity, labelPowerMax, labelPowerMin, labelName, labelTitle;

    private Trackball trackBall = null;

    private static final double MIN_ZOOM = 0.65;
    private static final double MAX_ZOOM = 1.2;
    private double zoom = 1.0;

    private List<Plant> plants = null;

    private Group map;

    @Override
	public void setup(){

    	setSize(1024, 768);

    	map = new Group();

		earth = new Sphere(1);
		earth.setStroke(false);
        earthTexture = new Texture( getClass().getResource("/plantsmap/earthDiffuse.png") );
        earth.setTexture(earthTexture);
        earth.setRotationZ(180.0);

        map.add(earth);

        plants = PowerPlantLoader.load( getClass().getResource("/plantsmap/nuclear.csv") );

        for (Plant p : plants) {
            double lat = p.getLongitude();
            double lng = p.getLatitude();

            p.setRotationX(-lat);
            p.setRotationZ(-lng);

            Vertex v = new Vertex(0, 1, 0);
            Matrix3D matX = Matrix3D.getRotateXMatrix(-lat);
            Matrix3D matZ = Matrix3D.getRotateZMatrix(-lng);

            v = matZ.mult(matX.mult(v));

            p.setPosition(v);

            p.addMouseEventCallback( new MouseClickCallback() {
                public void run(MouseClickTypes eventtype, Element element) {
                    Plant e = (Plant) element;

                    switch(eventtype) {
                    case CLICKED:
                        e.setSelected(true);
                        break;
                    case RELEASED:
                        e.setSelected(false);
                        break;
                    default:
                        break;
                    }
                }
            });

            map.add(p);
        }

        addObject(map);

        // board
        Group board = new Group();

        boardBackground = new Rect(300, 150);
        boardBackground.setStroke(false);
        boardBackground.setFillColor(new RGBColor(1.0, 1.0, 1.0, 0.3));

        board.add(boardBackground);

//        for(int i = 0; i<NUM_CAPACITY_SEGMENT; i++){
//        	capacityRect[i] = new Rect((board.getWidth()-30)/NUM_CAPACITY_SEGMENT, 10);
//        	capacityRect[i].setStrokeWidth(5);
//        	capacityRect[i].setFillColor(RGBColor.lerpColor(Plant.MIN_COLOR, Plant.MAX_COLOR, i/(float)(NUM_CAPACITY_SEGMENT-1)));
//        	capacityRect[i].setStroke(false);
//        }

//        Font font = new Font("San-Serif");
//        font.setSize(12);
//
//        Font fontS = new Font("San-Serif");
//        fontS.setSize(10);
//
//        labelName = new Text("Name:",font);
//        labelName.setStrokeColor(ColorSet.WHEAT);
//        labelName.setStrokeColor(ColorSet.WHITE_SMOKE);
//        board.add(labelName);
//
//        labelCapacity = new Text("CapacityLevel", fontS);
//        labelCapacity.setStrokeColor(ColorSet.WHITE);
//        board.add(labelCapacity);
//
//        labelPowerMin = new Text(Double.toString(Plant.MIN_POWER), fontS);
//        labelPowerMin.setStrokeColor(ColorSet.WHITE);
//        labelPowerMin.setAlign(TextAlign.LEFT);
//        board.add(labelPowerMin);
//
//        labelPowerMax = new Text(Double.toString(Plant.MAX_POWER), fontS);
//        labelPowerMax.setStrokeColor(ColorSet.WHITE);
//        labelPowerMax.setAlign(TextAlign.RIGHT);
//        board.add(labelPowerMax);
//
//        Font fontTitle = new Font("San-Serif");
//        fontTitle.setSize(18);
//
//        labelTitle = new Text(TITLE, fontTitle);
//        labelTitle.setStrokeColor(ColorSet.ORANGE);
//        board.add(labelTitle);

//        addObject(board);  // TODO: fix

        trackBall = new Trackball(getWidth(), getHeight());

        setPerspective(30, (double)getWidth()/(double)getHeight(), 1.0, 100);
        setCamera(2.4*zoom, 3.2*zoom, 4.0*zoom, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
	}

    @Override
    public void update() {
        map.applyMatrix(trackBall.getRotationMatrix());
    }

    @Override
    public void exit() {
    }

    @Override
    public void mouseEvent(MouseEvent e, MouseButton b) {
        if (e == MouseEvent.DRAGGED) {
            trackBall.update(getMouseX(), getMouseY(), getPrevMouseX(), getPrevMouseY());
        }
    }

    @Override
    public void keyEvent(KeyEvent e) {
        if (e == KeyEvent.PRESSED) {
            if (getKeyCode() == 27) {
                System.exit(0);
            }
        }

        if(getKeyCode() == 87){
            zoom += 0.01;
        }
        if(getKeyCode() == 83){
            zoom -= 0.01;
        }

        if( zoom < MIN_ZOOM ) {
            zoom = MIN_ZOOM;
        }

        if( zoom > MAX_ZOOM ) {
            zoom = MAX_ZOOM;
        }

        map.setScale(zoom);
    }

    public static void main(String args[]) {
        AppletRunner.run( "jp.xcoo.casmi.plantsmap.view.NuclearPlantsMap", TITLE);
    }
}
