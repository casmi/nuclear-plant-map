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

package plantsmap.view;

import java.nio.IntBuffer;
import java.util.List;

import javax.media.opengl.GL;

import plantsmap.data.Plant;
import plantsmap.model.TrackBall;
import casmi.Applet;
import casmi.AppletRunner;
import casmi.graphics.Graphics;
import casmi.graphics.Graphics.MatrixMode;
import casmi.graphics.color.Color;
import casmi.graphics.color.ColorSet;
import casmi.graphics.element.Rect;
import casmi.graphics.element.Sphere;
import casmi.graphics.element.Text;
import casmi.graphics.element.TextAlign;
import casmi.graphics.element.Texture;
import casmi.graphics.font.Font;

import com.sun.opengl.util.BufferUtil;

/**
 * Nuclear Plants Map main applet
 * @author Y. Ban
 *
 */
public class NuclearPlantsMap extends Applet {
	
	private static final String TITLE = "NuclearPlantsMap";
    
    private static final int SELECTION_BUFSIZE = 512;
	
    private Sphere earth = null;
    private Texture earthTexture = null;

    private Rect board = null;
    
    private static final int NUM_CAPACITY_SEGMENT = 10;
    private Rect capacityRect[] = new Rect[NUM_CAPACITY_SEGMENT];
    
    private Text labelCapacity, labelPowerMax, labelPowerMin, labelName, labelTitle;

    private TrackBall trackBall = null;

    private int selectedIndex = 0;
    
    private static final double MIN_ZOOM = 0.65;
    private static final double MAX_ZOOM = 1.2;
    private double zoom = 1.0;

    private List<Plant> plants = null;
    
    @Override
	public void setup(){
		
    	setSize(1024, 768);
    	
		earth = new Sphere(1);
		earth.setStroke(false);
        earthTexture = new Texture( getClass().getResource("/plantsmap/earthDiffuse.png") );
        
        plants = PowerPlantLoader.load( getClass().getResource("/plantsmap/nuclear.csv") );
        
        board = new Rect(300,150);
        board.setStroke(false);
        board.setFillColor(new Color(255,255,255,100));
        
        for(int i = 0; i<NUM_CAPACITY_SEGMENT; i++){
        	capacityRect[i] = new Rect((board.getWidth()-30)/(double)NUM_CAPACITY_SEGMENT, 10);
        	capacityRect[i].setStrokeWidth(5);
        	capacityRect[i].setFillColor(Color.lerpColor(Plant.MIN_COLOR, Plant.MAX_COLOR, i/(float)(NUM_CAPACITY_SEGMENT-1)));
        	capacityRect[i].setStroke(false);
        }
        
        Font font = new Font("San-Serif");
        font.setSize(12);
        
        Font fontS = new Font("San-Serif");
        fontS.setSize(10);
        
        labelName = new Text("Name:",font);
        labelName.setStrokeColor(Color.color((ColorSet.WHEAT)));
        labelName.setStrokeColor(Color.color((ColorSet.WHITESMOKE)));
        
        labelCapacity = new Text("CapacityLevel", fontS);
        labelCapacity.setStrokeColor(Color.color(ColorSet.WHITE));
        
        labelPowerMin = new Text(Double.toString(Plant.MIN_POWER), fontS);
        labelPowerMin.setStrokeColor(Color.color(ColorSet.WHITE));
        labelPowerMin.setAlign(TextAlign.LEFT);
        
        labelPowerMax = new Text(Double.toString(Plant.MAX_POWER), fontS);
        labelPowerMax.setStrokeColor(Color.color(ColorSet.WHITE));
        labelPowerMax.setAlign(TextAlign.RIGHT);
        
        Font fontTitle = new Font("San-Serif");
        fontTitle.setSize(18);
        
        labelTitle = new Text(TITLE, fontTitle);
        labelTitle.setStrokeColor(Color.color(ColorSet.ORANGE));
        
        trackBall = new TrackBall(getWidth(), getHeight());
	}
		
	@Override
	public void draw(Graphics g) {

		g.perspective(30, (double)getWidth()/(double)getHeight(), 1.0, 100);

    	g.camera(2.4*zoom, 3.2*zoom, 4.0*zoom, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

    	if(isKeyPressed()){
        	if(getKeycode()==40){
        		zoom += 0.01;
        	}
        	if(getKeycode()==38){
        		zoom -= 0.01;
        	}
        	
        	if( zoom < MIN_ZOOM ) {
        		zoom = MIN_ZOOM;
        	}
        	
        	if( zoom > MAX_ZOOM ) {
        		zoom = MAX_ZOOM;
        	}
    	}
    	
    	g.matrixMode(MatrixMode.MODELVIEW);
    	g.pushMatrix();
    	{
    		trackBall.update( getMouseX(), getMouseY(), getPreMouseX(), getPreMouseY(), 
    					  	  isMouseDragged(), isMousePressed(), isMouseReleased() );
    		
    		g.applyMatrix(trackBall.getMatrix());
    		
    		g.pushMatrix();
    		{
    			// render earth
    			earthTexture.enableTexture();
    			
    			g.pushMatrix();
    			{
    				g.rotateX(90);
    				g.render(earth);
    			}
    			g.popMatrix();
    			
    			earthTexture.disableTexture();
    			
    			// render plants
    			g.pushMatrix();
    			{
    				g.rotateY(180);
    				g.rotateX(90);
    				
    				drawCone(g,g.getGL(),GL.GL_RENDER);
    			}
    			g.popMatrix();
    		}
    		g.popMatrix();
    	}
    	g.matrixMode(MatrixMode.MODELVIEW);
    	g.popMatrix();
    	
    	// picking
    	if(isMousePressed()) {
			pickCone(g,g.getGL());
    	}
    	
    	g.ortho();
		drawBoard(g);
	}
	
	private void drawBoard(Graphics g){
		g.pushMatrix();
		{
			g.translate(getWidth() - board.getWidth() / 2 - 10, 105);
			
			g.render(board);
			
			g.translate(capacityRect[0].getWidth() / 2, - 50);
			
			g.pushMatrix();
			{
				g.translate(-capacityRect[0].getWidth() - (board.getWidth() / 2 - 15), -15);
				g.render(labelPowerMin);
			}
			g.popMatrix();
			
			g.pushMatrix();
			{
				g.translate((board.getWidth() / 2 - 15), -15);
				g.render(labelPowerMax);
			}
			g.popMatrix();
			
			g.pushMatrix();
			{
				g.translate(-board.getWidth() / 2 - 5, 10);
				g.render(labelCapacity);
				g.translate(-5, 95);
				g.render(labelTitle);
				g.translate(5, -18);
				labelName.setText("Name:     " + plants.get(selectedIndex).getName());
				g.render(labelName);
				g.translate(0,-18);
				labelName.setText("Capacity: " + plants.get(selectedIndex).getCapacity());
				g.render(labelName);
				g.translate(0, -18);
				labelName.setText("Country:  " + plants.get(selectedIndex).getCountry());
				g.render(labelName);
				g.translate(0, -18);
				labelName.setText("Location:  (" + plants.get(selectedIndex).getLongitude() + "," + plants.get(selectedIndex).getLatitude() + ")");
				g.render(labelName);
			}
			g.popMatrix();
			
			for(int i=0;i<NUM_CAPACITY_SEGMENT;i++){
				g.pushMatrix();
				{
					g.translate((i - NUM_CAPACITY_SEGMENT / 2) * capacityRect[i].getWidth(), 0);
					g.render(capacityRect[i]);
				}
				g.popMatrix();
			}
		}
		g.popMatrix();
	}
	
	private void drawCone(Graphics g, GL gl, int mode){
		if(mode == GL.GL_SELECT) {
			gl.glLoadName(plants.size());
		}
				
		int index = 0;
		for(Plant p : plants ) {
			
			g.pushMatrix();
			{
				g.rotateZ(-p.getLatitude());
				g.rotateX(-p.getLongitude());
				g.translate(0,1,0);
				
				if(mode == GL.GL_SELECT) {
					gl.glLoadName(index);
				}
				
				if(index == selectedIndex){
					Color originalColor = p.getCone().getFillColor();
					p.getCone().setFillColor( Color.color(ColorSet.WHITE) );
					
					g.render(p.getCone());
					
					p.getCone().setFillColor(originalColor);				
				}
				else{
					g.render(p.getCone());
				}
			}
			g.popMatrix();
			
			index ++;
		}
	}
	
	private void pickCone(Graphics g,GL gl){
		int selectBuff[] = new int[SELECTION_BUFSIZE];
		IntBuffer selectBuffer = BufferUtil.newIntBuffer(SELECTION_BUFSIZE);
		int hits;
		int viewport[] = new int[4];
		
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glSelectBuffer(SELECTION_BUFSIZE, selectBuffer);
		gl.glRenderMode(GL.GL_SELECT);
		
		gl.glInitNames();
		gl.glPushName(-1);
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		g.pushMatrix();
		{
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			
			g.getGLU().gluPickMatrix(getMouseX(), getMouseY(), 5.0, 5.0, viewport, 0);
			
			g.getGLU().gluPerspective(30,(double)getWidth()/(double)getHeight(),1.0,100);
			
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
			
			gl.glMatrixMode(GL.GL_MODELVIEW);
			g.pushMatrix();
			{
				g.camera(2.4*zoom, 3.2*zoom, 4.0*zoom, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
				g.applyMatrix(trackBall.getMatrix());
			
				g.rotateY(180);
				g.rotateX(90);
			
				drawCone(g,gl,GL.GL_SELECT);
			}
			gl.glMatrixMode(GL.GL_MODELVIEW);
			g.popMatrix();
		}
		gl.glMatrixMode(GL.GL_PROJECTION);
		g.popMatrix();
		
		gl.glFlush();
		
		hits = gl.glRenderMode(GL.GL_RENDER);
		selectBuffer.get(selectBuff);
		processHits(hits,selectBuff);
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}
	
	private void processHits(int hits, int buffer[]){
		if( hits > 0 ) {
			if(buffer[4*hits-1]!=153) {
				selectedIndex = buffer[4*hits-1];
			}
		}
	}
	
	public static void main(String args[]) {
		AppletRunner.run( "plantsmap.view.NuclearPlantsMap", "Nuclear Plants Map");
	}

}
