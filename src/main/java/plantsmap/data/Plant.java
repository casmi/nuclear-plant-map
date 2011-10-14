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

package plantsmap.data;

import casmi.graphics.color.Color;
import casmi.graphics.color.ColorSet;
import casmi.graphics.element.Cone;

/**
 * Power plant object
 * 
 * @author Y. Ban
 *
 */
public class Plant {
	
    private String name;
    private String country;
    private double capacity;
    private double longitude;
    private double latitude;
    
    private Cone cone;

    public static final double MAX_POWER = 6000.0;
    public static final double MIN_POWER = 1000.0;
    
    public static final Color MAX_COLOR = new Color(240,0,0);
    public static final Color MIN_COLOR = new Color(180,180,0);
    
    public Plant(String name, double capacity, String country, double longitude, double latitude) {
        this.setName(name);
        this.setCountry(country);
        this.setCapacity(capacity);
        this.setLongitude(longitude);
        this.setLatitude(latitude);
        this.cone = new Cone(0.015, this.getCapacity()/15000.0);
        this.cone.setStroke(false);
        this.cone.setStrokeColor(Color.color(ColorSet.WHITE));
        this.cone.setFillColor(Color.lerpColor(MIN_COLOR, MAX_COLOR, (float)((this.getCapacity()-MIN_POWER)/(MAX_POWER-MIN_POWER))));
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public Cone getCone() {
		return cone;
	}

	public void setCone(Cone cone) {
		this.cone = cone;
	}
}
